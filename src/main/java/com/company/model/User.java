package com.company.model;

import org.bson.types.ObjectId;

public class User {
    private ObjectId id;
    private String name;
    private String email;
    private String password;
    private String role;
    private ObjectId managerId;

    public User() {}

    public User(String name, String email, String password, String role, ObjectId managerId) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.managerId = managerId;
    }

    // Getters and Setters
    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public ObjectId getManagerId() { return managerId; }
    public void setManagerId(ObjectId managerId) { this.managerId = managerId; }
}