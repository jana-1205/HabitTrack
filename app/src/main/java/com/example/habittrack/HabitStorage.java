package com.example.habittrack;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HabitStorage {

    private static final String PREFS_NAME = "HabitTrackPreferences";
    private static final String HABITS_KEY = "habits";

    private final SharedPreferences preferences;

    public HabitStorage(Context context) {
        preferences = context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
        );
    }

    public void saveHabits(List<Habit> habits) {

        JSONArray habitsArray = new JSONArray();

        try {
            for (Habit habit : habits) {

                JSONObject habitObject = new JSONObject();

                habitObject.put("id", habit.getId());
                habitObject.put("name", habit.getName());

                JSONArray datesArray = new JSONArray();

                for (String date : habit.getCompletedDates()) {
                    datesArray.put(date);
                }

                habitObject.put("completedDates", datesArray);

                habitsArray.put(habitObject);
            }

            preferences.edit()
                    .putString(HABITS_KEY, habitsArray.toString())
                    .apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Habit> loadHabits() {

        List<Habit> habits = new ArrayList<>();

        String savedData = preferences.getString(HABITS_KEY, null);

        if (savedData == null) {
            return habits;
        }

        try {

            JSONArray habitsArray = new JSONArray(savedData);

            for (int i = 0; i < habitsArray.length(); i++) {

                JSONObject habitObject = habitsArray.getJSONObject(i);

                String id = habitObject.getString("id");
                String name = habitObject.getString("name");

                List<String> completedDates = new ArrayList<>();

                JSONArray datesArray =
                        habitObject.optJSONArray("completedDates");

                if (datesArray != null) {

                    for (int j = 0; j < datesArray.length(); j++) {
                        completedDates.add(
                                datesArray.getString(j)
                        );
                    }
                }

                habits.add(
                        new Habit(
                                id,
                                name,
                                completedDates
                        )
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return habits;
    }
}