/**
 * e-fuli.com Inc.
 * Copyright (c) 2015-2018 All Rights Reserved.
 */
package com.exciting.common.util.file;

import java.io.*;


/**
 * 
 * <pre>
 *
 * </pre>
 *
 * @author wujiaxing
 * @version $Id: FileUtil.java, v 0.1 2019年4月26日 下午5:17:03 wujiaxing Exp $
 */
public final class FileUtil {

    public static final int     DEFAULT_BUFFER_SIZE = 2048;

    private FileUtil() {

    }

    /**
     * 通过绝对路径读取文本文件内容
     * @param path
     * @return
     */
    public static String readText(String path) {
        try {
            return readString(new FileInputStream(path));
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    /**
     * 通过classpath路径读取文本文件内容
     * @param classpath
     * @return
     */
    public static String readClasspathText(String classpath) {
        if (!classpath.startsWith("/")) {
            classpath = "/" + classpath;
        }
        return readString(FileUtil.class.getResourceAsStream(classpath));
    }

    /**
     * <pre>
     * 读取文本文件内容,
     *  filePath如果为classpath:�?头则读取classpath下的文件，否则为当前用户目录
     * </pre>
     * @param filePath
     * @param charset
     * @return
     */
    public static String readTextFromPath(String filePath, String charset) {
        InputStream bin = null;
        ByteArrayOutputStream bos = null;
        boolean isClasspath = false;
        if (filePath.startsWith("classpath")) {
            isClasspath = true;
            filePath = filePath.replace("classpath:", "");
            if (!filePath.startsWith("/")) {
                filePath = "/" + filePath;
            }
        } else {
            filePath = makeFilePath(System.getProperty("user.dir"), filePath);
        }
        try {
            if (isClasspath) {
                bin = FileUtil.class.getResourceAsStream(filePath);
            } else {
                bin = new FileInputStream(filePath);
            }
            bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int len = 0;
            while ((len = bin.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            return bos.toString(charset);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("读取文件失败: [" + filePath + "]");
        } finally {
            try {
                if (bin != null) {
                    bin.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                throw new RuntimeException("关闭文件流失�?: [" + filePath + "]");
            }
        }
    }

    /**
     * 将输入流中信息读取成字符�?, 缓冲默认使用2kb
     * @param in
     * @return
     */
    public static byte[] readByteArray(InputStream in) {
        return readByteArray(in, DEFAULT_BUFFER_SIZE);

    }

    /**
     * 将输入流中信息读取成字节数组
     * @param in
     * @param bufferSize
     *            读取流时的字节缓冲大�?
     * @return
     */
    public static byte[] readByteArray(InputStream in, int bufferSize) {
        BufferedInputStream bis = new BufferedInputStream(in);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[bufferSize];
        int i = 0;
        try {
            while ((i = bis.read(buf)) != -1) {
                bos.write(buf, 0, i);
            }
            return bos.toByteArray();
        } catch (IOException e) {
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                }
            }
        }
        return null;

    }

    /**
     * 将输入流中信息读取成字符�?
     * @param in
     * @param bufferSize
     *            读取流时的字节缓冲大�?
     * @return
     */
    public static String readString(InputStream in, int bufferSize) {
        BufferedInputStream bis = new BufferedInputStream(in);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[bufferSize];
        int i = 0;
        try {
            while ((i = bis.read(buf)) != -1) {
                bos.write(buf, 0, i);
            }
            return bos.toString("UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                }
            }
        }
        return null;

    }

    /**
     * 将输入流中信息读取成字符�?, 缓冲默认使用2kb
     * @param in
     * @return
     */
    public static String readString(InputStream in) {
        return readString(in, DEFAULT_BUFFER_SIZE);

    }

    /**
     * <pre>
     * 获取classpath下文件流对象
     * </pre>
     * @param classpath
     * @return
     */
    public static InputStream getClasspathFileInputStream(String classpath) {
        if (!classpath.startsWith("/")) {
            classpath = "/" + classpath;
        }
        return FileUtil.class.getResourceAsStream(classpath);
    }

    /**
     * 将字符串写入流中
     * @param out
     * @param data
     */
    public static void writeString(OutputStream out, String data) {
        BufferedOutputStream bout = new BufferedOutputStream(out);
        try {
            bout.write(data.getBytes("UTF-8"));
        } catch (IOException e) {
        } finally {
            try {
                if (bout != null) {
                    bout.flush();
                    bout.close();
                }
            } catch (IOException e) {
            }

        }
    }

    /**
     * <pre>
     * 文件路径组装
     * </pre>
     * @param folderPath
     * @param fileName
     * @return
     */
    public static String makeFilePath(String folderPath, String fileName) {
        return folderPath.endsWith("\\") || folderPath.endsWith("/") ? folderPath + fileName : folderPath + File.separatorChar + fileName;
    }

}
