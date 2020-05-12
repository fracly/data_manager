package com.bcht.data_manager.utils;

import com.bcht.data_manager.consts.Constants;
import com.bcht.data_manager.entity.DataSource;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HDFSUtils {
    private static Logger logger = LoggerFactory.getLogger(HDFSUtils.class);

    private static FileSystem getDefaultFileSystem() throws IOException {
        Configuration configuration = new Configuration();
        configuration.set("fs.defaultFS", PropertyUtils.getString("fs.defaultFS"));
        FileSystem fs = FileSystem.get(configuration);
        return fs;
    }

    public static boolean checkConnection(String ip, int port) throws IOException{
        Configuration conf = new Configuration();
        String hdfsUrl = "hdfs://" + ip + ":" + port;
        conf.set("fs.defaultFS", hdfsUrl);
        FileSystem fs = FileSystem.get(conf);
        boolean exists = fs.exists(new Path("/"));
        close(fs);
        return exists;

    }

    /**
     * 创建HDFS目录，只有在目录存在，且目录不为空的情况下，返回false
     *  目录存在为空
     *  目录不存在，创建成功 都返回true
     */
    public static boolean mkdir(String path) throws IOException{
        boolean result = false;
        FileSystem fs = getDefaultFileSystem();
        boolean exists = fs.exists(new Path(path));
        if(exists) {
            boolean isDirecotory = fs.isDirectory(new Path(path));
            if(isDirecotory) {
                FileStatus[] fileStatuses = fs.listStatus(new Path(path));
                if(fileStatuses.length == 0){
                    result = true;
                }
            }
        } else {
            result =  fs.mkdirs(new Path(path));
        }
        close(fs);
        return result;
    }

    public static boolean rmdir(String path) throws IOException{
        boolean result = false;
        FileSystem fs = getDefaultFileSystem();
        Path p = new Path(path);
        if (fs.exists(p)) {
            result = fs.delete(p, false);
        }
        return result;
    }

    public static boolean copyHdfsToLocal(String srcHdfsFilePath, String dstFile, boolean deleteSource, boolean overwrite) throws IOException{
        Path srcPath = new Path(srcHdfsFilePath);
        File dstPath = new File(dstFile);

        boolean result;

        if (dstPath.exists()) {
            if (dstPath.isFile()) {
                if (overwrite) {
                    dstPath.delete();
                }
            } else {
                logger.error("destination file must be a file");
            }
        }
        if(!dstPath.getParentFile().exists()){
            dstPath.getParentFile().mkdirs();
        }
        FileSystem fs = getDefaultFileSystem();
        result =  FileUtil.copy(fs, srcPath, dstPath, deleteSource, fs.getConf());
        close(fs);
        return result;
    }

    public static boolean copyLocalToHdfs(String srcFile, String dstHdfsPath, boolean deleteSource, boolean overwrite) throws IOException {
        boolean result = false;
        Path srcPath = new Path(srcFile);
        Path dstPath= new Path(dstHdfsPath);
        FileSystem fs = getDefaultFileSystem();
        fs.copyFromLocalFile(deleteSource, overwrite, srcPath, dstPath);
        close(fs);
        return true;
    }

    public static boolean deleteHDFSFile(DataSource dataSource, String fileName) throws IOException {
        boolean result = false;
        FileSystem fs = getDefaultFileSystem();
        String absPath = dataSource.getCategory1() + "/" + fileName;
        if(fs.exists(new Path(absPath))) {
            result = fs.delete(new Path(absPath), true);
        }
        close(fs);
        return result;
    }

    public static ContentSummary getFileInfo(DataSource dataSource, String fileName) throws IOException {
        FileSystem fs = getDefaultFileSystem();
        ContentSummary contentSummary = fs.getContentSummary(new Path(dataSource.getCategory1() + "/" + fileName));
        close(fs);
        return contentSummary;
    }

    public static List<String> previewFile(String fileName) throws IOException {
        List<String> result = new ArrayList<>();
        FileSystem fs = getDefaultFileSystem();
        FSDataInputStream fsDataInputStream;
        Path path = new Path(fileName);
        fsDataInputStream = fs.open(path);
        String line;
        int total = 1;
        while((line = fsDataInputStream.readLine()) != null && total <= Constants.maxPreviewRecord) {
            result.add(line);
            total ++;
        }
        fsDataInputStream.close();
        close(fs);
        return result;
    }

    // 关闭资源
    private static void close(FileSystem fs) {
        if(fs != null) {
            try {
                fs.close();
            } catch (IOException e) {
                logger.error("关闭HDFS文件目录失败！" + e.getMessage());
            }
        }
    }
}
