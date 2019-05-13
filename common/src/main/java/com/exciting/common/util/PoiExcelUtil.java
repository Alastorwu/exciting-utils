package com.exciting.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by wujiaxing on 2017/5/2.
 */
@Slf4j
public class PoiExcelUtil{


    /**
     * @param file file
     * @param sheetName sheet名
     * @param title Map<Excel对应标题,Object对应字段>
     * @param outClass 输出类型
     * @return List<T>
     * @throws IOException IOException
     * @throws IllegalAccessException IllegalAccessException
     * @throws InstantiationException InstantiationException
     * @throws InvocationTargetException InvocationTargetException
     *
     */
    public static <T> List<T> readExcelToObjectList(File file
            , String sheetName
            , Map<String,String> title
            , Class<T> outClass)
            throws IOException, IllegalAccessException, InstantiationException, InvocationTargetException {
        String fileName = file.getName();
        log.info("PoiExcelUtil读取的文件名为：" + fileName);
        FileInputStream fileInputStream = new FileInputStream(file);
        return readExcelToObjectList(fileInputStream,fileName,sheetName,title,outClass);
    }

    /**
     * @param inputStream Excel文件FileInputStream
     * @param fileName Excel文件名
     * @param sheetName sheet名
     * @param title Map<Excel对应标题,Object对应字段>
     * @param outClass 输出类型
     * @return List<T>
     * @throws IOException IOException
     * @throws IllegalAccessException IllegalAccessException
     * @throws InstantiationException InstantiationException
     * @throws InvocationTargetException InvocationTargetException
     *
     */
    public static <T> List<T> readExcelToObjectList(InputStream inputStream
                                            , String fileName
                                            , String sheetName
                                            , Map<String,String> title
                                            , Class<T> outClass)
            throws IOException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Workbook workbook = null;
        //根据文件后缀读取
        if(fileName.toLowerCase().endsWith("xlsx")){
            workbook = new XSSFWorkbook(inputStream);
        }else if(fileName.toLowerCase().endsWith("xls")){
            workbook = new HSSFWorkbook(inputStream);
        }
        if(workbook==null){return null;}
        Method[] allMethods = outClass.getMethods();
        if(allMethods==null){return null;}
        Sheet sheet;
        if(StringUtils.isBlank(sheetName)){
            sheet = workbook.getSheetAt(0);
        }else{
            sheet = workbook.getSheet(sheetName);
        }
        if(sheet == null){return null;}
        List<Method> methods = new ArrayList<>();
        List<T> list = new ArrayList<>();
        for (Row row : sheet) {
            //读取标题
            if(row.getRowNum()==0){
                for (Cell cell : row) {
                    String key = title.get(cell.getStringCellValue());
                    if(StringUtils.isBlank(key)){
                        continue;
                    }
                    for (Method method : allMethods) {
                        if(method.getName().equals("set" + ExcitingStringUtils.captureName(key))){
                            methods.add(method);break;
                        }
                    }
                }
            }else{
                //读取一行字段转换为Object
                T outInfo = outClass.newInstance();
                for (int j = 0; j < methods.size(); j++) {
                    Cell cell = row.getCell(j);
                    Method method = methods.get(j);
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if(parameterTypes.length == 0 || cell == null){
                        continue;
                    }else if(parameterTypes[0]==String.class){
                        method.invoke(outInfo, cell.getStringCellValue());
                    }else if(parameterTypes[0]==int.class || parameterTypes[0]==Integer.class){
                        int cellType = cell.getCellType();
                        if(Cell.CELL_TYPE_FORMULA!=cellType && Cell.CELL_TYPE_NUMERIC!=cellType){
                            continue;
                        }
                        method.invoke(outInfo, (int)cell.getNumericCellValue());
                    }else if(parameterTypes[0]==double.class || parameterTypes[0]==Double.class){
                        int cellType = cell.getCellType();
                        if(Cell.CELL_TYPE_FORMULA!=cellType && Cell.CELL_TYPE_NUMERIC!=cellType){
                            continue;
                        }
                        method.invoke(outInfo, cell.getNumericCellValue());
                    }else if(parameterTypes[0]==BigDecimal.class){
                        int cellType = cell.getCellType();
                        if(Cell.CELL_TYPE_FORMULA!=cellType && Cell.CELL_TYPE_NUMERIC!=cellType){
                            continue;
                        }
                        method.invoke(outInfo, new BigDecimal(cell.getNumericCellValue()));
                    }else if(parameterTypes[0]==Date.class ){
                        int cellType = cell.getCellType();
                        if(Cell.CELL_TYPE_FORMULA!=cellType && Cell.CELL_TYPE_NUMERIC!=cellType){
                            continue;
                        }
                        if(!DateUtil.isCellDateFormatted(cell)){continue;}
                        method.invoke(outInfo, cell.getDateCellValue());
                    }

                }
                list.add(outInfo);
            }
        }
        return list;
    }


}
