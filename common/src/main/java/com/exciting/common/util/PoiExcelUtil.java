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
import java.util.*;

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
        Method[] allMethods = outClass.getMethods();
        if(allMethods==null){return null;}
        Sheet sheet = getSheet(inputStream,fileName,sheetName);
        if(sheet == null){return null;}
        Map<Cell,Method> titleMethod = new HashMap<>();
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
                            titleMethod.put(cell,method);
                            break;
                        }
                    }
                }
            }else{
                //读取一行字段转换为Object
                T outInfo = outClass.newInstance();
                for (Map.Entry<Cell, Method> cellMethodEntry : titleMethod.entrySet()) {
                    Cell titleCell = cellMethodEntry.getKey();
                    Method method = cellMethodEntry.getValue();

                    Cell cell = row.getCell(titleCell.getColumnIndex());
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if(parameterTypes.length == 0 || cell == null){
                        continue;
                    }
                    Class<?> parameterType = parameterTypes[0];
                    if(parameterType == String.class){
                        method.invoke(outInfo, cell.getStringCellValue());
                    }else if(parameterType ==int.class || parameterType ==Integer.class){
                        int cellType = cell.getCellType();
                        if(Cell.CELL_TYPE_FORMULA!=cellType && Cell.CELL_TYPE_NUMERIC!=cellType){
                            continue;
                        }
                        method.invoke(outInfo, (int)cell.getNumericCellValue());
                    }else if(parameterType == double.class || parameterType == Double.class){
                        int cellType = cell.getCellType();
                        if(Cell.CELL_TYPE_FORMULA!=cellType && Cell.CELL_TYPE_NUMERIC!=cellType){
                            continue;
                        }
                        method.invoke(outInfo, cell.getNumericCellValue());
                    }else if(parameterType == BigDecimal.class){
                        int cellType = cell.getCellType();
                        if(Cell.CELL_TYPE_FORMULA!=cellType && Cell.CELL_TYPE_NUMERIC!=cellType){
                            continue;
                        }
                        method.invoke(outInfo, new BigDecimal(cell.getNumericCellValue()));
                    }else if(parameterType == Date.class ){
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


    /**
     *
     * @param file file
     * @param sheetName sheetName
     * @param title title
     * @return List<Map<String, Object>>
     * @throws IOException IOException
     */
    public static List<Map<String, Object>> readExcelToMap(File file
                                                           , String sheetName
                                                           , Map<String, String> title)throws IOException {
        String fileName = file.getName();
        log.info("PoiExcelUtil读取的文件名为：" + fileName);
        FileInputStream fileInputStream = new FileInputStream(file);
        return readExcelToMap(fileInputStream,fileName,sheetName,title);

    }


    /**
     *
     * @param inputStream inputStream
     * @param fileName fileName
     * @param sheetName sheetName
     * @param title title
     * @return List<Map<String, Object>>
     * @throws IOException IOException
     */
    public static List<Map<String, Object>> readExcelToMap(InputStream inputStream
                                                           , String fileName
                                                           , String sheetName
                                                           , Map<String, String> title) throws IOException {
        Sheet sheet = getSheet(inputStream,fileName,sheetName);
        if(sheet == null){return null;}
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String,Cell> titleCellMap = new HashMap<>();
        for (Row row : sheet) {
            //读取标题
            if(row.getRowNum()==0){
                for (Cell cell : row) {
                    String key = title.get(cell.getStringCellValue());
                    if(StringUtils.isBlank(key)){
                        continue;
                    }
                    titleCellMap.put(key,cell);

                }
            }else{
                Map<String, Object> map = new HashMap<>();
                //读取一行字段转换为Object
                for (Map.Entry<String, Cell> cellMethodEntry : titleCellMap.entrySet()) {
                    String titleKey = cellMethodEntry.getKey();
                    Cell titleCell = cellMethodEntry.getValue();

                    Cell cell = row.getCell(titleCell.getColumnIndex());
                    if(cell == null){
                        continue;
                    }
                    int cellType = cell.getCellType();
                    if( Cell.CELL_TYPE_FORMULA==cellType
                        || Cell.CELL_TYPE_NUMERIC==cellType
                        || Cell.CELL_TYPE_ERROR==cellType
                        || Cell.CELL_TYPE_BLANK==cellType){
                        map.put(titleKey,cell.getNumericCellValue());
                    }else if(Cell.CELL_TYPE_BOOLEAN==cellType){
                        map.put(titleKey,cell.getBooleanCellValue());
                    }else{
                        map.put(titleKey,cell.getStringCellValue());
                    }
                }
                list.add(map);
            }
        }
        return list;
    }

    private static Sheet getSheet(InputStream inputStream, String fileName, String sheetName) throws IOException {
        Workbook workbook = null;
        //根据文件后缀读取
        if(fileName.toLowerCase().endsWith("xlsx")){
            workbook = new XSSFWorkbook(inputStream);
        }else if(fileName.toLowerCase().endsWith("xls")){
            workbook = new HSSFWorkbook(inputStream);
        }
        if (workbook==null){
            return null;
        }
        Sheet sheet;
        if(StringUtils.isBlank(sheetName)){
            sheet = workbook.getSheetAt(0);
        }else{
            sheet = workbook.getSheet(sheetName);
        }
        return sheet;
    }


}
