package com.exciting.common.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class ITextPdfUtil {

    private static String generateBarCode() {
        String msg = "123456789";
//        String path = "barcode.png";
        String path = "D:\\OneDrive\\work\\barcode.png";
        //generateFile(msg, path);
        System.out.println("条形码生成=="+BarcodeUtil.generateFile(msg, path));
        return path;
    }

    public static void main(String[] args) throws IOException, DocumentException {

        // 1.新建document对象
        Document document = new Document();

        // 2.建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中。
        // 创建 PdfWriter 对象 第一个参数是对文档对象的引用，第二个参数是文件的实际名称，在该名称中还会给出其输出路径。
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("D:/OneDrive/work/test.pdf"));

        // 3.打开文档
        document.open();
        //中文字体,解决中文不能显示问题
        BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H",BaseFont.NOT_EMBEDDED);
        Font font = new Font(bfChinese);
        Font font2 = new Font(bfChinese);
        font.setColor(BaseColor.BLACK);
        font2.setColor(BaseColor.BLACK);
        font2.setSize(12f);

        // 5列的表.
        PdfPTable table = new PdfPTable(5);
        //table.setWidthPercentage(100); // 宽度100%填充
//        table.setSpacingBefore(10f); // 前间距
//        table.setSpacingAfter(10f); // 后间距

        List<PdfPRow> listRow = table.getRows();
        //设置列宽
        float[] columnWidths = { 3.88f, 3.88f, 1f, 2.88f, 3.88f };
        table.setWidths(columnWidths);

        PdfPCell[] cells1 = initPdfCell(5);
        PdfPRow row1 = new PdfPRow(cells1);
        //单元格
        cells1[0].setPhrase(new Paragraph("级别A",font));
        cells1[3].setPhrase(new Paragraph("单肩包",font));
        cells1[3].setColspan(2);

        //行2
        PdfPCell[] cells2= initPdfCell(5);
        cells2[0].setPhrase(new Paragraph("Bvlgari(宝格丽)",font2));
        cells2[0].setColspan(5);
        PdfPRow row2 = new PdfPRow(cells2);
        //图片1
        Image image1 = Image.getInstance(generateBarCode());
        //设置图片位置的x轴和y周
        //设置图片的宽度和高度
        image1.scaleAbsolute(240, 70);
        //将图片1添加到pdf文件中
        PdfPCell[] cells3 = initPdfCell(5);
        cells3[0].setImage(image1);
        cells3[0].setColspan(5);
        PdfPRow row3 = new PdfPRow(cells3);
        //把第一行添加到集合
        listRow.add(row1);
        listRow.add(row2);
        listRow.add(row3);
        //把表格添加到文件中
        document.add(table);
        // 5.关闭文档
        document.close();
        System.out.println("pdf生成完成;");
    }

    private static PdfPCell[] initPdfCell(int i) {
        List<PdfPCell> cells = new ArrayList<>();
        for (int j = 0; j < i; j++) {
            PdfPCell cell = new PdfPCell();
            cell.setBorder(0);
            cells.add(cell);
        }
        return cells.toArray(new PdfPCell[i]);
    }

}
