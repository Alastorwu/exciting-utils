package com.exciting.common.util.file;

import lombok.extern.slf4j.Slf4j;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by ray.liu on 2017/4/5.
 */
@Slf4j
public class FileUtils {

    public static File getOutFile(String filePath, String fileName) throws IOException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        return Paths.get(filePath,fileName).toFile();
    }

    //图片转化成base64字符串
    public static String GetImageStr(String url){//将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        String imgFile = url;//待处理的图片
        InputStream in = null;
        byte[] data = null;
        //读取图片字节数组
        try
        {
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        //对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);//返回Base64编码过的字节数组字符串
    }

    /**
     * 文件写入本地
     * @param inputStream 文件流
     * @param path 路径
     * @param fileName 文件名
     * @return 文件路径
     */
    public static String saveFile(InputStream inputStream,String path,String fileName){
        File filePath = new File(path);
        File file = new File(path,fileName);
        try {
            if (!filePath.exists()){
                filePath.mkdirs();
            }
            if (!file.exists()){
                file.createNewFile();
            }
            byte[] bytes = new byte[1024];
            int len = 0;
            FileOutputStream outputStream = new FileOutputStream(file);
            while ((len = inputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        }
        return path + fileName;
    }

    public static byte[] getBytesFromFile(File file) {
        if (file == null) {
            // log.error("helper:the file is null!");
            return null;
        }
        /*byte[] ret = null;
        try {
            if (file == null) {
                // log.error("helper:the file is null!");
                return null;
            }
            FileInputStream       in  = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
            byte[]                b   = new byte[4096];
            int                   n;
            while ((n = in.read(b)) != -1) {
                out.write(b, 0, n);
            }
            in.close();
            out.close();
            ret = out.toByteArray();
        } catch (IOException e) {
            // log.error("helper:get bytes from file process error!");
            e.printStackTrace();
        }
        return ret;*/
        try {
            return FileUtils.getBytesFromInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(),e);
        }
        return null;
    }


    public static byte[] getBytesFromInputStream(InputStream in) {
        byte[] ret = null;
        try {

            ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
            byte[]                b   = new byte[4096];
            int                   n;
            while ((n = in.read(b)) != -1) {
                out.write(b, 0, n);
            }
            in.close();
            out.close();
            ret = out.toByteArray();
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        }
        return ret;
    }


    /**
     * 返回Base64编码过的字节数组字符串
     * @param bty
     * @return
     */
    public static String getBase64String(byte[] bty) {
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(bty);//返回Base64编码过的字节数组字符串
    }


}
