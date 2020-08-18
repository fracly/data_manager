package com.bcht.data_manager.utils;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.jasper.tagplugins.jstl.core.Out;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.bcht.data_manager.consts.Constants.*;
import static com.bcht.data_manager.utils.PropertyUtils.getString;

/**
 * file utils
 */
public class FileUtils {
    public static final Logger logger = LoggerFactory.getLogger(FileUtils.class);


    public static void copyFile(MultipartFile file, String destFilename) throws IOException {
        File destFile = new File(destFilename);
        File destParentDir = new File(destFile.getParent());

        if (!destParentDir.exists()) {
            org.apache.commons.io.FileUtils.forceMkdir(destParentDir);
        }

        Files.copy(file.getInputStream(), Paths.get(destFilename));
    }

    /**
     * file to resource
     *
     * @param filename
     * @return
     */
    public static Resource file2Resource(String filename) throws MalformedURLException {
        Path file = Paths.get(filename);

        Resource resource = new UrlResource(file.toUri());
        if (resource.exists() || resource.isReadable()) {
            return resource;
        } else {
            logger.error("file can not read : {}", filename);

        }
        return null;
    }

    /**
     * get file suffix
     *
     * @param filename
     * @return file suffix
     */
    public static String suffix(String filename) {

        String fileSuffix = "";
        if (StringUtils.isNotEmpty(filename)) {
            int lastIndex = filename.lastIndexOf(".");
            if (lastIndex > 0) {
                fileSuffix = filename.substring(lastIndex + 1);
            }
        }
        return fileSuffix;
    }

    public static String getFileName(String filename) {

        String fileSuffix = "";
        if (StringUtils.isNotEmpty(filename)) {
            int lastIndex = filename.lastIndexOf("/");
            if (lastIndex > 0) {
                fileSuffix = filename.substring(lastIndex + 1);
            }
        }
        return fileSuffix;
    }


    /**
     * get download file absolute path and name
     *
     * @param filename
     * @return download file name
     */
    public static String getDownloadFilename(String filename) {
        return String.format("%s/%s/%s", getString(DATA_DOWNLOAD_BASEDIR_PATH), DateUtils.getCurrentTime(YYYYMMDDHHMMSS), filename);
    }

    public static String getUploadFilename(String filename) {
        return String.format("%s/%s/%s", getString(DATA_UPLOAD_BASEDIR_PATH), DateUtils.getCurrentTime(YYYYMMDDHHMMSS), filename);
    }

    /**
     * get upload file absolute path and name
     *
     * @param tenantCode tenant code
     * @param filename file name
     * @return local file path
     */
    public static String getUploadFilename(String tenantCode, String filename) {
        return String.format("%s/%s/resources/%s",getString(DATA_BASEDIR_PATH), tenantCode, filename);
    }


    /**
     * write content to file ,if parent path not exists, it will do one's utmost to mkdir
     *
     * @param content       content
     * @param filePath      target file path
     * @return
     */
    public static boolean writeContent2File(String content, String filePath) {
        boolean flag = true;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        try {
            File distFile = new File(filePath);
            if (!distFile.getParentFile().exists()) {
                distFile.getParentFile().mkdirs();
            }
            bufferedReader = new BufferedReader(new StringReader(content));
            bufferedWriter = new BufferedWriter(new FileWriter(distFile));
            char buf[] = new char[1024];
            int len;
            while ((len = bufferedReader.read(buf)) != -1) {
                bufferedWriter.write(buf, 0, len);
            }
            bufferedWriter.flush();
            bufferedReader.close();
            bufferedWriter.close();
        } catch (IOException e) {
            FileUtils.logger.error(e.getMessage(), e);
            flag = false;
            return flag;
        } finally {
            IOUtils.closeQuietly(bufferedWriter);
            IOUtils.closeQuietly(bufferedReader);
        }
        return flag;
    }

    /**
     * Writes a String to a file creating the file if it does not exist.
     *
     * NOTE: As from v1.3, the parent directories of the file will be created
     * if they do not exist.
     *
     * @param file  the file to write
     * @param data  the content to write to the file
     * @param encoding  the encoding to use, {@code null} means platform default
     * @throws IOException in case of an I/O error
     * @throws UnsupportedEncodingException if the encoding is not supported by the VM
     * @since 2.4
     */
    public static void writeStringToFile(File file, String data, Charset encoding) throws IOException {
        writeStringToFile(file, data, encoding, false);
    }

    /**
     * Writes a String to a file creating the file if it does not exist.
     *
     * NOTE: As from v1.3, the parent directories of the file will be created
     * if they do not exist.
     *
     * @param file  the file to write
     * @param data  the content to write to the file
     * @param encoding  the encoding to use, {@code null} means platform default
     * @throws IOException in case of an I/O error
     * @throws UnsupportedEncodingException if the encoding is not supported by the VM
     */
    public static void writeStringToFile(File file, String data, String encoding) throws IOException {
        writeStringToFile(file, data, encoding, false);
    }

    /**
     * Writes a String to a file creating the file if it does not exist.
     *
     * @param file  the file to write
     * @param data  the content to write to the file
     * @param encoding  the encoding to use, {@code null} means platform default
     * @param append if {@code true}, then the String will be added to the
     * end of the file rather than overwriting
     * @throws IOException in case of an I/O error
     * @since 2.3
     */
    public static void writeStringToFile(File file, String data, Charset encoding, boolean append) throws IOException {
        OutputStream out = null;
        try {
            out = openOutputStream(file, append);
            IOUtils.write(data, out, encoding);
            out.close(); // don't swallow close Exception if copy completes normally
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    /**
     * Writes a String to a file creating the file if it does not exist.
     *
     * @param file  the file to write
     * @param data  the content to write to the file
     * @param encoding  the encoding to use, {@code null} means platform default
     * @param append if {@code true}, then the String will be added to the
     * end of the file rather than overwriting
     * @throws IOException in case of an I/O error
     * @throws UnsupportedCharsetException
     *             thrown instead of {@link UnsupportedEncodingException} in version 2.2 if the encoding is not
     *             supported by the VM
     * @since 2.1
     */
    public static void writeStringToFile(File file, String data, String encoding, boolean append) throws IOException {
        writeStringToFile(file, data, Charsets.toCharset(encoding), append);
    }

    /**
     * Writes a String to a file creating the file if it does not exist using the default encoding for the VM.
     *
     * @param file  the file to write
     * @param data  the content to write to the file
     * @throws IOException in case of an I/O error
     */
    public static void writeStringToFile(File file, String data) throws IOException {
        writeStringToFile(file, data, Charset.defaultCharset(), false);
    }

    /**
     * Writes a String to a file creating the file if it does not exist using the default encoding for the VM.
     *
     * @param file  the file to write
     * @param data  the content to write to the file
     * @param append if {@code true}, then the String will be added to the
     * end of the file rather than overwriting
     * @throws IOException in case of an I/O error
     * @since 2.1
     */
    public static void writeStringToFile(File file, String data, boolean append) throws IOException {
        writeStringToFile(file, data, Charset.defaultCharset(), append);
    }

    /**
     * Opens a {@link FileOutputStream} for the specified file, checking and
     * creating the parent directory if it does not exist.
     * <p>
     * At the end of the method either the stream will be successfully opened,
     * or an exception will have been thrown.
     * <p>
     * The parent directory will be created if it does not exist.
     * The file will be created if it does not exist.
     * An exception is thrown if the file object exists but is a directory.
     * An exception is thrown if the file exists but cannot be written to.
     * An exception is thrown if the parent directory cannot be created.
     *
     * @param file  the file to open for output, must not be {@code null}
     * @return a new {@link FileOutputStream} for the specified file
     * @throws IOException if the file object is a directory
     * @throws IOException if the file cannot be written to
     * @throws IOException if a parent directory needs creating but that fails
     * @since 1.3
     */
    public static FileOutputStream openOutputStream(File file) throws IOException {
        return openOutputStream(file, false);
    }

    /**
     * Opens a {@link FileOutputStream} for the specified file, checking and
     * creating the parent directory if it does not exist.
     * <p>
     * At the end of the method either the stream will be successfully opened,
     * or an exception will have been thrown.
     * <p>
     * The parent directory will be created if it does not exist.
     * The file will be created if it does not exist.
     * An exception is thrown if the file object exists but is a directory.
     * An exception is thrown if the file exists but cannot be written to.
     * An exception is thrown if the parent directory cannot be created.
     *
     * @param file  the file to open for output, must not be {@code null}
     * @param append if {@code true}, then bytes will be added to the
     * end of the file rather than overwriting
     * @return a new {@link FileOutputStream} for the specified file
     * @throws IOException if the file object is a directory
     * @throws IOException if the file cannot be written to
     * @throws IOException if a parent directory needs creating but that fails
     * @since 2.1
     */
    public static FileOutputStream openOutputStream(File file, boolean append) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (file.canWrite() == false) {
                throw new IOException("File '" + file + "' cannot be written to");
            }
        } else {
            File parent = file.getParentFile();
            if (parent != null) {
                if (!parent.mkdirs() && !parent.isDirectory()) {
                    throw new IOException("Directory '" + parent + "' could not be created");
                }
            }
        }
        return new FileOutputStream(file, append);
    }


    /**
     * deletes a directory recursively
     * @param dir
     */

    public static void deleteDir(String dir) throws IOException {
        org.apache.commons.io.FileUtils.deleteDirectory(new File(dir));
    }

    /**
     * Deletes a file. If file is a directory, delete it and all sub-directories.
     * <p>
     * The difference between File.delete() and this method are:
     * <ul>
     * <li>A directory to be deleted does not have to be empty.</li>
     * <li>You get exceptions when a file or directory cannot be deleted.
     *      (java.io.File methods returns a boolean)</li>
     * </ul>
     *
     * @param filename
     * @throws IOException in case deletion is unsuccessful
     */
    public static void deleteFile(String filename) throws IOException {
        org.apache.commons.io.FileUtils.forceDelete(new File(filename));
    }

    /**
     * Gets all the parent subdirectories of the parentDir directory
     * @param parentDir
     * @return
     */
    public static File[] getAllDir(String parentDir){
        if(parentDir == null || "".equals(parentDir)) {
            throw new RuntimeException("parentDir can not be empty");
        }

        File file = new File(parentDir);
        if(!file.exists() || !file.isDirectory()) {
            throw new RuntimeException("parentDir not exist, or is not a directory:"+parentDir);
        }

        File[] schemaDirs = file.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                if (pathname.isDirectory()) {
                    return true;
                }
                else {
                    return false;
                }
            }
        });

        return schemaDirs;
    }

    /**
     * Get Content
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static String readFile2Str(InputStream inputStream) throws IOException{
        String all_content=null;
        try {
            all_content = new String();
            InputStream ins = inputStream;
            ByteArrayOutputStream outputstream = new ByteArrayOutputStream();
            byte[] str_b = new byte[1024];
            int i = -1;
            while ((i=ins.read(str_b)) > 0) {
                outputstream.write(str_b,0,i);
            }
            all_content = outputstream.toString();
            return all_content;
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Encrypt Data
     */
    public static boolean encryptAndDecryptFile(String src, String target) throws IOException {
        int dataOfFile = 0; // 字节内容
        int numOfEncAndDec = 0x99; //加解密密钥

        File srcFile = new File(src);
        File targetFile = new File(target);
        if(!srcFile.exists()) {
            logger.error("需要加密/解密的源文件不存在");
            return false;
        }

        if(!targetFile.exists()) {
            if(!targetFile.getParentFile().exists()) {
                targetFile.getParentFile().mkdirs();
            }
            logger.info("创建加密文件");
            targetFile.createNewFile();
        }

        InputStream fis = new FileInputStream(srcFile);
        OutputStream fos = new FileOutputStream(targetFile);

        while((dataOfFile = fis.read()) > -1) {
            fos.write(dataOfFile^numOfEncAndDec);
        }
        fis.close();
        fos.flush();
        fos.close();
        return true;
    }
}
