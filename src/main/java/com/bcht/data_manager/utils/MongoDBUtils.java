package com.bcht.data_manager.utils;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MongoDBUtils {
    private static Logger logger = LoggerFactory.getLogger(MongoDBUtils.class);

    // 获取链接
    public static MongoDatabase getMongoDBDatabase(String ip, Integer port, String databaseName, String username, String password) {
        MongoClient client;
        if(StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(password)) {
            List<ServerAddress> adds = new ArrayList<>();

            ServerAddress serverAddress = new ServerAddress(ip, port);
            adds.add(serverAddress);
            List<MongoCredential> credentials = new ArrayList<>();
            MongoCredential mongoCredential = MongoCredential.createScramSha1Credential(username, databaseName, password.toCharArray());
            credentials.add(mongoCredential);

            client  = new MongoClient(adds, credentials);
        } else {
            client = new MongoClient(ip, port);
        }
        MongoDatabase database = client.getDatabase(databaseName);
        return database;
    }

    // 获取某个表的文档集合
    public static MongoCollection getTableCollection(MongoDatabase database, String tableName) {
        return database.getCollection(tableName);
    }

}
