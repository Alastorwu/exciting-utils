package com.exciting.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class PoiExcelUtilTest {


    @Test
    public void export() throws IOException {
//        exportExcel("201809");
//        exportExcel("201810");
//        exportExcel("201811");
//        exportExcel("201812");
        exportExcel("201901");
        exportExcel("201902");
    }

    private void exportExcel(String fileDate) throws IOException {
        long start = System.currentTimeMillis();
        System.out.println("读取"+fileDate+"开始:"+start);

        File file = new File("D:\\oneDrive\\work\\兜礼积分明细1\\兜礼积分明细\\交行对账单1rar\\"+fileDate+".xlsx");
        Map<String, String> title = new HashMap<>();
        title.put("下单时间","下单时间");
        title.put("订单号","订单号");
        title.put("三方订单号","三方订单号");
        title.put("商品BN","商品BN");
        title.put("商品名称","商品名称");
        title.put("分类","分类");
        title.put("金额","金额");
        title.put("数量","数量");
        title.put("运费","运费");
        title.put("积分值（开票）","积分值（开票）");
        title.put("积分","积分");
        title.put("现金值","现金值");
        title.put("交易流水号","交易流水号");
        title.put("支付方式","支付方式");
        title.put("收货人","收货人");
        title.put("收货人手机号","收货人手机号");
        title.put("收货地址","收货地址");
        List<Map<String, Object>> maps= PoiExcelUtil.readExcelToMap(file, null, title);
        //System.out.println(JSON.toJSONString(maps.get(0)));


        File file1 = new File("D:\\oneDrive\\work\\兜礼积分明细1\\兜礼积分明细\\积分流水记录明细-"+fileDate+".xls");
        Map<String, String> title1 = new HashMap<>();
        title1.put("交易流水号","交易流水号");
        title1.put("交易积分","交易积分");
        title1.put("交易类型","交易类型");
        title1.put("交易状态","交易状态");
        title1.put("第三方业务流水号","第三方业务流水号");
        title1.put("第三方原业务流水号","第三方原业务流水号");
        title1.put("第三方名称","第三方名称");
        title1.put("交易时间","交易时间");
        List<Map<String, Object>> maps1 = PoiExcelUtil.readExcelToMap(file1, null, title1);
        //System.out.println(JSON.toJSONString(maps1.get(0)));


        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("sheet1");
        int titleCellIndex = 0;
        Row titleRow = sheet.createRow(0);
        for (Map.Entry<String, String> entry:title1.entrySet()) {
            Cell cell = titleRow.createCell(titleCellIndex);
            cell.setCellValue(entry.getValue());
            titleCellIndex++;
        }
        for (Map.Entry<String, String> entry:title.entrySet()) {
            Cell cell = titleRow.createCell(titleCellIndex);
            cell.setCellValue(entry.getValue());
            titleCellIndex++;
        }

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        DateTimeFormatter timeFormatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        int rownum = 1;
        for (Map m:maps1) {
            String 交易时间 = "" + m.get("交易时间");
            List<Map<String, Object>> filters = maps.stream().filter(s -> {
                Object o = s.get("下单时间");
                if(o==null){
                    return false;
                }
                if(o instanceof Date){
                    Date 下单时间 = (Date) o;
                    LocalDateTime localDateTime = DateUtils.dateToLocalDateTime(下单时间);
                    String format = timeFormatter.format(localDateTime);
                    if (StringUtils.equals(交易时间, format)) {
                        return true;
                    }
                }else{
                    LocalDateTime parse1 = LocalDateTime.parse(o + "",timeFormatter1);
                    String format = timeFormatter.format(parse1);
                    if (StringUtils.equals(交易时间, format)) {
                        return true;
                    }
                }

                return false;
            }).collect(Collectors.toList());
            maps.removeAll(filters);
            Row row = sheet.createRow(rownum);
            rownum++;
            for (Cell tc:titleRow) {
                String key = tc.getStringCellValue();
                if(StringUtils.isNotBlank(key)){
                    Cell cell = row.createCell(tc.getColumnIndex());
                    Object o = m.get(key);
                    if(o!=null){
                        cell.setCellValue(o+"");
                    }else if(!CollectionUtils.isEmpty(filters)){
                        String s = filters.get(0).get(key) + "";
                        // 金额	数量	运费	 积分值（开票） 	积分	现金值
                        if(
                                StringUtils.equals(key,"金额")||
                                        StringUtils.equals(key,"数量")||
                                        StringUtils.equals(key,"运费")||
                                        StringUtils.equals(key,"积分值（开票）")||
                                        StringUtils.equals(key,"积分")||
                                        StringUtils.equals(key,"现金值")
                        ){
                            BigDecimal bigDecimal = new BigDecimal(0);
                            //bigDecimal = bigDecimal.setScale(4,BigDecimal.ROUND_DOWN);
                            for (Map f:filters) {
                                Object o1 = f.get(key);
                                if (o1==null){continue;}
                                BigDecimal bigDecimal2 = (BigDecimal) o1;
                                bigDecimal = bigDecimal.add(bigDecimal2);
                            }
                            cell.setCellValue(bigDecimal.doubleValue());
                        }else{
                            StringBuffer stringBuffer = new StringBuffer();
                            for (Map f:filters) {
                                stringBuffer.append(f.get(key)+";");
                            }
                            cell.setCellValue(stringBuffer.toString());
                        }

                    }
                }
            }


        }
        for (Map m:maps) {
            Row row = sheet.createRow(rownum);
            for (Cell tc:titleRow) {
                String key = tc.getStringCellValue();
                Object o = m.get(key);
                if(o==null){
                    continue;
                }
                Cell cell = row.createCell(tc.getColumnIndex());
                cell.setCellValue(o+"");
            }
        }

        workbook.write(new FileOutputStream("D:\\oneDrive\\work\\export\\"+fileDate+"-export.xlsx"));

        long end = System.currentTimeMillis();
        System.out.println("读取"+fileDate+"结束，耗时:"+(end-start));

        /*Map<String, Map<String, Object>> collect = maps.stream().collect(
                Collectors.toMap((m) -> ""+m.get("三方订单号"), (m) -> m)
        );*/

        //System.out.println(JSON.toJSONString(collect));

    }

    private boolean isNum(String str) {
        return str.matches("-?[0-9]+.*[0-9]*");

    }
}