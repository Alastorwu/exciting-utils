package com.exciting.common.util;

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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class PoiExcelUtilTest {


    @Test
    public void export() throws IOException {
        exportExcel("201808");
        exportExcel("201809");
//        exportExcel("201810");
//        exportExcel("201811");
//        exportExcel("201812");
//        exportExcel("201901");
//        exportExcel("201902");
//        exportExcel("2018121");
    }

    private void exportExcel(String fileDate) throws IOException {
        long start = System.currentTimeMillis();
        System.out.println("读取"+fileDate+"开始:"+start);

        File file = new File("D:\\oneDrive\\work\\兜礼积分明细1\\兜礼积分明细\\交行对账单1rar\\"+fileDate+".xlsx");
//        File file = new File("D:\\oneDrive\\work\\兜礼积分明细1\\交行最新订单信息\\积分流水记录明细-"+fileDate+".xls");
        Map<String, String> sonTitle = new HashMap<>();
        sonTitle.put("下单时间","下单时间");
        sonTitle.put("订单号","订单号");
        sonTitle.put("三方订单号","三方订单号");
        sonTitle.put("商品BN","商品BN");
        sonTitle.put("商品名称","商品名称");
        sonTitle.put("分类","分类");
        sonTitle.put("金额","金额");
        sonTitle.put("数量","数量");
        sonTitle.put("运费","运费");
        sonTitle.put("积分值（开票）","积分值（开票）");
        sonTitle.put("积分","积分");
        sonTitle.put("现金值","现金值");
        sonTitle.put("交易流水号","交易流水号");
        sonTitle.put("支付方式","支付方式");
        sonTitle.put("收货人","收货人");
        sonTitle.put("收货人手机号","收货人手机号");
        sonTitle.put("收货地址","收货地址");

        /*sonTitle.put("下单时间","下单时间");
        sonTitle.put("订单号","订单号");
        sonTitle.put("商品编码","商品编码");
        sonTitle.put("所属供应商","所属供应商");
        sonTitle.put("商品名称","商品名称");
        sonTitle.put("数量","数量");
        sonTitle.put("分类","分类");
        sonTitle.put("积分现金值","积分现金值");
        sonTitle.put("积分值","积分值");
        sonTitle.put("在线支付现金值","在线支付现金值");
        sonTitle.put("在线支付交易流水","在线支付交易流水");
        sonTitle.put("在线支付方式","在线支付方式");
        sonTitle.put("收货人","收货人");
        sonTitle.put("收货手机号","收货手机号");
        sonTitle.put("收货人地址","收货人地址");*/

        List<Map<String, Object>> sonMaps= PoiExcelUtil.readExcelToMap(file, null, sonTitle);
        //System.out.println(JSON.toJSONString(sonMaps.get(0)));


        //File file1 = new File("D:\\oneDrive\\work\\兜礼积分明细1\\兜礼积分明细\\积分流水记录明细-"+fileDate+".xls");
        File file1 = new File("D:\\oneDrive\\work\\兜礼积分明细1\\交行最新订单信息\\积分流水记录明细-"+fileDate+".xls");
        Map<String, String> mainTitle = new HashMap<>();
        mainTitle.put("交易流水号","交易流水号");
        mainTitle.put("交易积分","交易积分");
        mainTitle.put("交易类型","交易类型");
        mainTitle.put("交易状态","交易状态");
        mainTitle.put("第三方业务流水号","第三方业务流水号");
        mainTitle.put("第三方原业务流水号","第三方原业务流水号");
        mainTitle.put("第三方名称","第三方名称");
        mainTitle.put("交易时间","交易时间");
        List<Map<String, Object>> mainMap = PoiExcelUtil.readExcelToMap(file1, null, mainTitle);
        //System.out.println(JSON.toJSONString(mainMap.get(0)));


        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("sheet1");
        int titleCellIndex = 0;
        Row titleRow = sheet.createRow(0);
        for (Map.Entry<String, String> entry:mainTitle.entrySet()) {
            Cell cell = titleRow.createCell(titleCellIndex);
            cell.setCellValue(entry.getValue());
            titleCellIndex++;
        }
        for (Map.Entry<String, String> entry:sonTitle.entrySet()) {
            Cell cell = titleRow.createCell(titleCellIndex);
            cell.setCellValue(entry.getValue());
            titleCellIndex++;
        }

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        DateTimeFormatter timeFormatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        int rownum = 1;
        for (Map m:mainMap) {
            String mainValue = "" + m.get("第三方业务流水号");
            if(StringUtils.isBlank(mainValue)){
                continue;
            }
            List<Map<String, Object>> filters = sonMaps.stream().filter(s -> {
                Object o = s.get("订单号");
                if(o==null){
                    return false;
                }
                String orderNo = "" + o;
                if(StringUtils.isNotBlank(orderNo) && orderNo.length()>2){
                    orderNo = orderNo.substring(2);
                }

                if( mainValue.contains(orderNo) ){
                    return true;
                }
                o = s.get("下单时间");
                if(o==null){
                    return false;
                }
                if(o instanceof Date){
                    Date orderTime = (Date) o;
                    LocalDateTime localDateTime = DateUtils.dateToLocalDateTime(orderTime);
                    if(localDateTime==null){
                        return false;
                    }
                    String format = timeFormatter.format(localDateTime);
                    format = format.substring(2);
                    return mainValue.contains(format);
                }else{
                    LocalDateTime parse1 = LocalDateTime.parse(o + "",timeFormatter1);
                    String format = timeFormatter.format(parse1);
                    format = format.substring(2);
                    return mainValue.contains(format);
                }

            }).collect(Collectors.toList());
            sonMaps.removeAll(filters);
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
                            String sb = "";
                            for (Map f:filters) {
                                sb = f.get(key)+";";
                            }
                            if(sb.length()>1){
                                sb = sb.substring(0,sb.length()-1);
                            }
                            cell.setCellValue(sb);
                        }

                    }
                }
            }


        }
        for (Map m:sonMaps) {
            Row row = sheet.createRow(rownum);
            rownum++;
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

        workbook.write(new FileOutputStream("D:\\oneDrive\\work\\export3\\"+fileDate+"-export.xlsx"));

        long end = System.currentTimeMillis();
        System.out.println("读取"+fileDate+"结束，耗时:"+(end-start));

        /*Map<String, Map<String, Object>> collect = sonMaps.stream().collect(
                Collectors.toMap((m) -> ""+m.get("三方订单号"), (m) -> m)
        );*/

        //System.out.println(JSON.toJSONString(collect));

    }

    private boolean isNum(String str) {
        return str.matches("-?[0-9]+.*[0-9]*");

    }
}