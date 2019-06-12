package com.exciting.common.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

/**
 *
 * @author wujiaxing
 * @date 2017/5/2
 */
@Slf4j
public class PoiExcelUtil{


    private static final String XLSX = ".xlsx";

    private static final String XLS = ".xls";


    /**
     * 读取Excel返回实体类
     *
     * @param file file
     * @param sheetName sheetName
     * @param title title
     * @param outClass outClass
     * @param <T> <T>
     * @return List<T>
     * @throws IOException IOException
     */
    public static <T> List<T> readExcelToList(File file
            , String sheetName
            , Map<String,String> title
            , Class<T> outClass)
            throws IOException {
        String fileName = file.getName();
        log.info("PoiExcelUtil读取的文件名为：" + fileName);
        FileInputStream fileInputStream = new FileInputStream(file);
        return readExcelToList(fileInputStream,fileName,sheetName,title,outClass);
    }

    /**
     * 读取Excel返回实体类
     *
     * @param inputStream inputStream
     * @param fileName fileName
     * @param sheetName sheet名
     * @param title Map<Excel对应标题,Object对应字段>
     * @param outClass 输出类型
     * @return List<T>
     * @throws IOException IOException
     */
    public static <T> List<T> readExcelToList(InputStream inputStream
            , String fileName
            , String sheetName
            , Map<String,String> title
            , Class<T> outClass)
            throws IOException{
        List<Map<String, Object>> maps = readExcelToMap(inputStream, fileName, sheetName, title);
        if(maps==null){
            return null;
        }
        List<T> returnList = new ArrayList<>();
        maps.forEach((m)->
                        returnList.add(TypeUtils.castToJavaBean(m,outClass))
        );
        return returnList;
    }


    /**
     * 读取Excel返回实体类
     *
     * @param file file
     * @param sheetName sheet名
     * @param title Map<Excel对应标题,Object对应字段>
     * @param outClass 输出类型
     * @return List<T>
     * @throws IOException IOException
     * @throws IllegalAccessException IllegalAccessException
     * @throws InstantiationException InstantiationException
     * @throws InvocationTargetException InvocationTargetException
     */
    @Deprecated
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
     * 读取Excel返回实体类
     *
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
     */
    @Deprecated
    public static <T> List<T> readExcelToObjectList(InputStream inputStream
                                                    , String fileName
                                                    , String sheetName
                                                    , Map<String,String> title
                                                    , Class<T> outClass)
            throws IOException, IllegalAccessException, InstantiationException, InvocationTargetException {
        if(outClass==null){return null;}
        Method[] allMethods = outClass.getMethods();
        Sheet sheet = getSheet(inputStream,fileName,sheetName);
        if(sheet == null){return null;}
        Map<Cell,Method> titleMethod = new HashMap<>(16);
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
     * 读取Excel返回Map
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
     * 读取Excel返回Map
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
        Map<String,Cell> titleCellMap = new HashMap<>(16);
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
                Map<String, Object> map = new HashMap<>(16);
                //读取一行字段转换为Object
                for (Map.Entry<String, Cell> cellMethodEntry : titleCellMap.entrySet()) {
                    String titleKey = cellMethodEntry.getKey();
                    Cell titleCell = cellMethodEntry.getValue();

                    Cell cell = row.getCell(titleCell.getColumnIndex());
                    if(cell == null){
                        continue;
                    }
                    int cellType = cell.getCellType();
                    if(titleisTime(titleKey) && Cell.CELL_TYPE_NUMERIC==cellType){
                        map.put(titleKey,cell.getDateCellValue());
                    }else if( Cell.CELL_TYPE_FORMULA==cellType
                        || Cell.CELL_TYPE_NUMERIC==cellType
                        || Cell.CELL_TYPE_ERROR==cellType
                        || Cell.CELL_TYPE_BLANK==cellType){
                        map.put(titleKey,new BigDecimal(cell.getNumericCellValue()+""));
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

    private static boolean titleisTime(String titleKey) {
        return     titleKey.contains("时间")
                || titleKey.contains("日期")
                || titleKey.contains("date")
                || titleKey.contains("time");
    }


    /**
     * 读取Excel返回Map
     *
     * @param file file
     * @param sheetName sheetName
     * @param title title
     * @return JSONArray
     * @throws IOException IOException
     */
    public static JSONArray readExcelToJSON(File file
            , String sheetName
            , Map<String, String> title)throws IOException {
        String fileName = file.getName();
        log.info("PoiExcelUtil读取的文件名为：" + fileName);
        FileInputStream fileInputStream = new FileInputStream(file);
        return readExcelToJSON(fileInputStream,fileName,sheetName,title);

    }


    /**
     * 读取Excel返回Map
     *
     * @param inputStream inputStream
     * @param fileName fileName
     * @param sheetName sheetName
     * @param title title
     * @return List<Map<String, Object>>
     * @throws IOException IOException
     */
    public static JSONArray readExcelToJSON(InputStream inputStream
                                                           , String fileName
                                                           , String sheetName
                                                           , Map<String, String> title) throws IOException {
        Sheet sheet = getSheet(inputStream,fileName,sheetName);
        if(sheet == null){return null;}
        JSONArray jsonArray = new JSONArray();
        Map<String,Cell> titleCellMap = new HashMap<>(16);
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
                JSONObject jsonObject = new JSONObject();
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
                        jsonObject.put(titleKey,cell.getNumericCellValue());
                    }else if(Cell.CELL_TYPE_BOOLEAN==cellType){
                        jsonObject.put(titleKey,cell.getBooleanCellValue());
                    }else{
                        jsonObject.put(titleKey,cell.getStringCellValue());
                    }
                }
                jsonArray.add(jsonObject);
            }
        }
        return jsonArray;
    }


    /**
     * workbook转流
     *
     * @param workbook workbook
     * @return InputStream
     * @throws IOException IOException
     */
    public static InputStream workbookToInputStream(Workbook workbook) throws IOException {
        if (workbook==null){
            return null;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        byte[] barry = bos.toByteArray();
        return new ByteArrayInputStream(barry);
    }




    private static Sheet getSheet(InputStream inputStream, String fileName, String sheetName) throws IOException {
        Workbook workbook = null;
        //根据文件后缀读取
        if(fileName.toLowerCase().endsWith(XLSX)){
            workbook = new XSSFWorkbook(inputStream);
        }else if(fileName.toLowerCase().endsWith(XLS)){
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
