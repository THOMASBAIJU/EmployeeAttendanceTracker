package com.company.model;

import java.time.LocalDate;
import org.bson.types.ObjectId;

public class Attendance {
    private ObjectId userId;
    private LocalDate date;
    private boolean present;

    public Attendance() {}

    public Attendance(ObjectId userId, LocalDate date, boolean present) {
        this.userId = userId;
        this.date = date;
        this.present = present;
    }

    // Getters and Setters
    public ObjectId getUserId() { return userId; }
    public void setUserId(ObjectId userId) { this.userId = userId; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public boolean isPresent() { return present; }
    public void setPresent(boolean present) { this.present = present; }
}