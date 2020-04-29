package com.bcht.data_manager.utils;

import com.bcht.data_manager.entity.DataSource;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class HDFSUtils {
    private static Logger logger = LoggerFactory.getLogger(HDFSUtils.class);

    private static FileSystem getDefaultFileSystem() {
        Configuration configuration = new Configuration();
        configuration.set("fs.defaultFS", PropertyUtils.getString("fs.defaultFS"));
        FileSystem fs = null;
        try{
            FileSystem.get(configuration);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return fs;
    }

    private static boolean closeFileSystem(FileSystem fs) {
        try{
            fs.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  false;
    }

    public static boolean checkConnection(String ip, int port) {
        Configuration conf = new Configuration();
        String hdfsUrl = "hdfs://" + ip + ":" + port;
        conf.set("fs.defaultFS", hdfsUrl);
        FileSystem fs = null;
        try {
            fs = FileSystem.get(conf);
            boolean exists = fs.exists(new Path("/"));
            return exists;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try{
                fs.close();
            } catch (Exception e){}
        }
    }

    /**
     * 创建HDFS目录，只有在目录存在，且目录不为空的情况下，返回false
     *  目录存在为空
     *  目录不存在，创建成功 都返回true
     */
    public static boolean mkdir(DataSource dataSource, String path) {
        Configuration conf = new Configuration();
        String hdfsUrl = "hdfs://" + dataSource.getIp() + ":" + dataSource.getPort();
        conf.set("fs.defaultFS", hdfsUrl);
        Path p = new Path(path);
        FileSystem fs = null;
        try {
            fs = FileSystem.get(conf);
            boolean exists = fs.exists(p);
            if(exists) {
                boolean isDirecotory = fs.isDirectory(p);
                if(isDirecotory) {
                    FileStatus[] fileStatuses = fs.listStatus(p);
                    if(fileStatuses.length != 0){
                        return false;
                    } else {
                        return true;
                    }
                }else {
                    return false;
                }
            } else {
                return fs.mkdirs(p);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try{
                fs.close();
            } catch (Exception e){}
        }
    }

    public static boolean copyHdfsToLocal(String srcHdfsFilePath, String dstFile, boolean deleteSource, boolean overwrite) {
        Path srcPath = new Path(srcHdfsFilePath);
        File dstPath = new File(dstFile);

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
        boolean result = false;
        try{
            return FileUtil.copy(fs, srcPath, dstPath, deleteSource, fs.getConf());
        }catch (Exception e){
            e.printStackTrace();
        }
        closeFileSystem(fs);
        return result;
    }


}
