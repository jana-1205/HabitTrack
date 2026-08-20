package com.example.habittrack;

import java.util.ArrayList;
import java.util.List;

public class Habit {

    private String id;
    private String name;
    private List<String> completedDates;

    public Habit(String id, String name) {
        this.id = id;
        this.name = name;
        this.completedDates = new ArrayList<>();
    }

    public Habit(String id, String name, List<String> completedDates) {
        this.id = id;
        this.name = name;
        this.completedDates = completedDates;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getCompletedDates() {
        return completedDates;
    }

    public void addCompletedDate(String date) {
        if (!completedDates.contains(date)) {
            completedDates.add(date);
        }
    }

    public void removeCompletedDate(String date) {
        completedDates.remove(date);
    }

    public boolean isCompletedOn(String date) {
        return completedDates.contains(date);
    }
}