package com.company.db;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDBConnection {
    private static MongoClient client;
    private static final String URI = "mongodb://localhost:27017";
    private static final String DB_NAME = "attendance_db";

    public static MongoDatabase getDatabase() {
        if (client == null) {
            client = MongoClients.create(URI);
        }
        return client.getDatabase(DB_NAME);
    }

    public static void close() {
        if (client != null) {
            client.close();
        }
    }
}