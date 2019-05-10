package com.exciting.util.banner;



import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;

/**
 * @author 东哥 2016年10月27日
 *
 */
public class AsciiPic {

    private final static String BASE = "@#&$%*o!;.";// 字符串由复杂到简单

    public static void createAsciiPic(InputStream inputStream) {
        try {
            final BufferedImage image = ImageIO.read(inputStream);
            for (int y = 0; y < image.getHeight(); y += 2) {
                for (int x = 0; x < image.getWidth(); x++) {
                    final int pixel = image.getRGB(x, y);
                    final int r = (pixel & 0xff0000) >> 16, g = (pixel & 0xff00) >> 8, b = pixel & 0xff;
                    final float gray = 0.299f * r + 0.578f * g + 0.114f * b;
                    final int index = Math.round(gray * (BASE.length() + 1) / 255);
                    System.out.print(index >= BASE.length() ? " " : String.valueOf(BASE.charAt(index)));
                }
                System.out.println();
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * test
     *
     * @param args
     */
    public static void main(final String[] args) throws FileNotFoundException {
        File file = new File("D:\\oneDrive\\图片\\表情\\其他\\2d63aef98c99aa39.jpg");
        FileInputStream fileInputStream = new FileInputStream(file);
        AsciiPic.createAsciiPic(fileInputStream);

    }


    private static void method2(String fileName, String content) {
        FileWriter writer = null;
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            writer = new FileWriter(fileName, true);
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(writer != null){
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}