package com.bcht.data_manager.utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;

public class HDFSUtils {
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


}
