package com.company.service;

import com.company.model.User;
import com.company.db.MongoDBConnection;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.util.ArrayList;
import java.util.List;
import static com.mongodb.client.model.Filters.*;

public class UserService {
    public void createUser(User user) {
        Document doc = new Document()
                .append("name", user.getName())
                .append("email", user.getEmail())
                .append("password", user.getPassword())
                .append("role", user.getRole())
                .append("managerId", user.getManagerId());

        MongoDBConnection.getDatabase().getCollection("users").insertOne(doc);
    }

    public void deleteUser(ObjectId userId) {
        MongoDBConnection.getDatabase().getCollection("users")
                .deleteOne(eq("_id", userId));
    }

    public User authenticate(String email, String password) {
        Document doc = MongoDBConnection.getDatabase().getCollection("users")
                .find(and(eq("email", email), eq("password", password))).first();
        return doc != null ? documentToUser(doc) : null;
    }

    public List<User> getAllManagers() {
        List<User> managers = new ArrayList<>();
        for(Document doc : MongoDBConnection.getDatabase().getCollection("users")
                .find(eq("role", "MANAGER"))) {
            managers.add(documentToUser(doc));
        }
        return managers;
    }

    public List<User> getEmployeesByManager(ObjectId managerId) {
        List<User> employees = new ArrayList<>();
        for(Document doc : MongoDBConnection.getDatabase().getCollection("users")
                .find(eq("managerId", managerId))) {
            employees.add(documentToUser(doc));
        }
        return employees;
    }

    private User documentToUser(Document doc) {
        User user = new User();
        user.setId(doc.getObjectId("_id"));
        user.setName(doc.getString("name"));
        user.setEmail(doc.getString("email"));
        user.setPassword(doc.getString("password"));
        user.setRole(doc.getString("role"));
        user.setManagerId(doc.getObjectId("managerId"));
        return user;
    }
}