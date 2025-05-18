package com.company.service;

import com.company.model.Attendance;
import com.company.db.MongoDBConnection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import static com.mongodb.client.model.Filters.*;

public class AttendanceService {
    public void markAttendance(Attendance attendance) {
        Document doc = new Document()
                .append("userId", attendance.getUserId())
                .append("date", attendance.getDate())
                .append("present", attendance.isPresent());

        getDatabase().getCollection("attendance").insertOne(doc);
    }

    public double calculateAttendancePercentage(ObjectId userId) {
        long totalDays = getDatabase().getCollection("attendance")
                .countDocuments(eq("userId", userId));
        long presentDays = getDatabase().getCollection("attendance")
                .countDocuments(and(eq("userId", userId), eq("present", true)));
        return (totalDays > 0) ? (presentDays * 100.0) / totalDays : 0.0;
    }

    public boolean isAttendanceMarked(ObjectId userId, LocalDate date) {
        return getDatabase().getCollection("attendance")
                .countDocuments(and(eq("userId", userId), eq("date", date))) > 0;
    }

    public List<Attendance> getTeamAttendance(ObjectId managerId) {
        List<ObjectId> employeeIds = getEmployeesByManager(managerId);
        List<Attendance> teamAttendance = new ArrayList<>();
        for(ObjectId employeeId : employeeIds) {
            teamAttendance.addAll(getAttendanceForUser(employeeId));
        }
        return teamAttendance;
    }

    public List<Attendance> getAttendanceForUser(ObjectId userId) {
        List<Attendance> records = new ArrayList<>();
        for(Document doc : getDatabase().getCollection("attendance")
                .find(eq("userId", userId))) {
            records.add(new Attendance(
                    doc.getObjectId("userId"),
                    doc.getDate("date").toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                    doc.getBoolean("present")
            ));
        }
        return records;
    }

    public String getUserName(ObjectId userId) {
        Document doc = getDatabase().getCollection("users")
                .find(eq("_id", userId)).first();
        return doc != null ? doc.getString("name") : "Unknown";
    }

    private List<ObjectId> getEmployeesByManager(ObjectId managerId) {
        List<ObjectId> employeeIds = new ArrayList<>();
        for(Document doc : getDatabase().getCollection("users")
                .find(eq("managerId", managerId))) {
            employeeIds.add(doc.getObjectId("_id"));
        }
        return employeeIds;
    }

    private MongoDatabase getDatabase() {
        return MongoDBConnection.getDatabase();
    }
}