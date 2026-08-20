package com.example.habittrack;

import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.snackbar.Snackbar;

import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    // =========================================================
    // STORAGE
    // =========================================================

    private HabitStorage habitStorage;
    private List<Habit> habits;

    // =========================================================
    // UI
    // =========================================================

    private LinearLayout habitContainer;
    private LinearLayout tvEmpty;

    private TextView tvHabitCount;
    private TextView tvProgress;
    private TextView tvProgressDescription;
    private TextView tvBestStreak;
    private TextView tvGreeting;
    private TextView tvDate;
    private TextView tvThemeToggle;

    private ProgressBar progressBar;

    // =========================================================
    // THEME STORAGE
    // =========================================================

    private static final String THEME_PREFS =
            "ThemePreferences";

    private static final String DARK_MODE_KEY =
            "darkMode";


    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /*
         * Apply the saved theme BEFORE creating the Activity UI.
         */
        applySavedTheme();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        /*
         * Keep the normal Android status bar visible.
         * We intentionally do NOT use fullscreen flags.
         */
        configureSystemBars();

        // -----------------------------------------------------
        // Find views
        // -----------------------------------------------------

        habitContainer =
                findViewById(R.id.habitContainer);

        tvEmpty =
                findViewById(R.id.tvEmpty);

        tvHabitCount =
                findViewById(R.id.tvHabitCount);

        tvProgress =
                findViewById(R.id.tvProgress);

        tvProgressDescription =
                findViewById(R.id.tvProgressDescription);

        tvBestStreak =
                findViewById(R.id.tvBestStreak);

        tvGreeting =
                findViewById(R.id.tvGreeting);

        tvDate =
                findViewById(R.id.tvDate);

        progressBar =
                findViewById(R.id.progressBar);

        tvThemeToggle =
                findViewById(R.id.tvThemeToggle);

        EditText etHabitName =
                findViewById(R.id.etHabitName);

        Button btnAddHabit =
                findViewById(R.id.btnAddHabit);


        // -----------------------------------------------------
        // Storage
        // -----------------------------------------------------

        habitStorage =
                new HabitStorage(this);

        habits =
                habitStorage.loadHabits();


        // -----------------------------------------------------
        // Header
        // -----------------------------------------------------

        updateGreeting();

        updateDate();

        updateThemeButton();


        // -----------------------------------------------------
        // DARK MODE BUTTON
        // -----------------------------------------------------

        tvThemeToggle.setOnClickListener(
                v -> toggleTheme()
        );


        // -----------------------------------------------------
        // ADD HABIT
        // -----------------------------------------------------

        btnAddHabit.setOnClickListener(v -> {

            String name = etHabitName
                    .getText()
                    .toString()
                    .trim();

            // Don't allow empty names
            if (name.isEmpty()) {

                Toast.makeText(
                        MainActivity.this,
                        "Enter a habit name first.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            // Normalize the name so:
            // "Workout", " workout ", "WORKOUT"
            // are all treated as the same habit.
            String normalizedNewName =
                    name
                            .replaceAll("\\s+", " ")
                            .trim()
                            .toLowerCase(Locale.ROOT);

            // Check every existing habit
            for (Habit existingHabit : habits) {

                String existingName =
                        existingHabit
                                .getName()
                                .replaceAll("\\s+", " ")
                                .trim()
                                .toLowerCase(Locale.ROOT);

                if (existingName.equals(normalizedNewName)) {

                    Toast.makeText(
                            MainActivity.this,
                            "You already have this habit.",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
                }
            }


            addHabit(name);

            etHabitName.setText("");

            etHabitName.clearFocus();
        });


        // -----------------------------------------------------
        // INITIAL SCREEN
        // -----------------------------------------------------

        refreshScreen();
    }


    // =========================================================
    // THEME
    // =========================================================

    private void applySavedTheme() {

        boolean darkMode =
                getSharedPreferences(
                        THEME_PREFS,
                        MODE_PRIVATE
                ).getBoolean(
                        DARK_MODE_KEY,
                        false
                );

        if (darkMode) {

            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES
            );

        } else {

            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
            );
        }
    }


    private void toggleTheme() {

        boolean currentlyDark =
                getSharedPreferences(
                        THEME_PREFS,
                        MODE_PRIVATE
                ).getBoolean(
                        DARK_MODE_KEY,
                        false
                );

        boolean newDarkMode =
                !currentlyDark;


        // Save user's choice.

        getSharedPreferences(
                THEME_PREFS,
                MODE_PRIVATE
        )
                .edit()
                .putBoolean(
                        DARK_MODE_KEY,
                        newDarkMode
                )
                .apply();


        /*
         * AppCompat handles the actual Day/Night
         * configuration change.
         */
        if (newDarkMode) {

            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES
            );

        } else {

            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
            );
        }
    }


    private void updateThemeButton() {

        boolean darkMode =
                getSharedPreferences(
                        THEME_PREFS,
                        MODE_PRIVATE
                ).getBoolean(
                        DARK_MODE_KEY,
                        false
                );

        if (darkMode) {

            tvThemeToggle.setText("☀");

        } else {

            tvThemeToggle.setText("☾");
        }
    }


    // =========================================================
    // SYSTEM BARS
    // =========================================================

    private void configureSystemBars() {

        Window window =
                getWindow();


        /*
         * IMPORTANT:
         *
         * We are NOT hiding the status bar.
         * We are NOT using fullscreen.
         * We are NOT using FLAG_FULLSCREEN.
         */

        boolean darkMode =
                getSharedPreferences(
                        THEME_PREFS,
                        MODE_PRIVATE
                ).getBoolean(
                        DARK_MODE_KEY,
                        false
                );


        /*
         * Status bar uses the same background as
         * the application.
         */
        window.setStatusBarColor(
                getColor(R.color.background)
        );


        /*
         * Navigation bar also follows the app.
         */
        window.setNavigationBarColor(
                getColor(R.color.background)
        );


        /*
         * Android 6.0+:
         *
         * LIGHT_STATUS_BAR means the status bar
         * icons are dark.
         */
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.M) {

            int flags =
                    window
                            .getDecorView()
                            .getSystemUiVisibility();


            if (!darkMode) {

                flags |=
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;

            } else {

                flags &=
                        ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }


            window
                    .getDecorView()
                    .setSystemUiVisibility(flags);
        }


        /*
         * Android 8.0+:
         *
         * LIGHT_NAVIGATION_BAR means navigation
         * icons are dark.
         */
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {

            int flags =
                    window
                            .getDecorView()
                            .getSystemUiVisibility();


            if (!darkMode) {

                flags |=
                        View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;

            } else {

                flags &=
                        ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            }


            window
                    .getDecorView()
                    .setSystemUiVisibility(flags);
        }
    }


    // =========================================================
    // ADD HABIT
    // =========================================================

    private void addHabit(String name) {

        String cleanedName = name.trim();

        // ---------------------------------------------------------
        // Prevent duplicate habit names
        // ---------------------------------------------------------

        for (Habit existingHabit : habits) {

            if (existingHabit.getName()
                    .trim()
                    .equalsIgnoreCase(cleanedName)) {

                Toast.makeText(
                        MainActivity.this,
                        "You already have this habit.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }
        }


        // ---------------------------------------------------------
        // Create new habit
        // ---------------------------------------------------------

        Habit habit =
                new Habit(
                        UUID.randomUUID().toString(),
                        cleanedName
                );

        habits.add(habit);

        habitStorage.saveHabits(habits);

        refreshScreen();

        showSnackbar(
                "Habit added ✓",
                null
        );
    }


    // =========================================================
    // REFRESH SCREEN
    // =========================================================

    private void refreshScreen() {

        habitContainer.removeAllViews();

        updateSummary();

        if (habits.isEmpty()) {

            tvEmpty.setVisibility(
                    View.VISIBLE
            );

        } else {

            tvEmpty.setVisibility(
                    View.GONE
            );

            for (Habit habit : habits) {

                createHabitCard(habit);
            }
        }

        updateThemeButton();
    }


    // =========================================================
    // CREATE HABIT CARD
    // =========================================================

    private void createHabitCard(Habit habit) {

        LayoutInflater inflater =
                LayoutInflater.from(this);

        View card =
                inflater.inflate(
                        R.layout.item_habit,
                        habitContainer,
                        false
                );


        TextView tvHabitName =
                card.findViewById(
                        R.id.tvHabitName
                );

        TextView tvStreak =
                card.findViewById(
                        R.id.tvStreak
                );

        LinearLayout weekContainer =
                card.findViewById(
                        R.id.weekContainer
                );

        Button btnComplete =
                card.findViewById(
                        R.id.btnComplete
                );

        ImageButton btnDelete =
                card.findViewById(
                        R.id.btnDelete
                );


        // -----------------------------------------------------
        // Habit information
        // -----------------------------------------------------

        tvHabitName.setText(
                habit.getName()
        );


        int streak =
                calculateCurrentStreak(habit);


        tvStreak.setText(
                "🔥 " +
                        streak +
                        (streak == 1
                                ? " day streak"
                                : " days streak")
        );


        // -----------------------------------------------------
        // Weekly progress
        // -----------------------------------------------------

        createWeekProgress(
                weekContainer,
                habit
        );


        // -----------------------------------------------------
        // Complete button
        // -----------------------------------------------------

        updateCompleteButton(
                btnComplete,
                habit
        );


        // =====================================================
        // COMPLETE / UNDO COMPLETION
        // =====================================================

        btnComplete.setOnClickListener(v -> {

            String today =
                    getTodayDate();


            boolean wasCompleted =
                    habit.isCompletedOn(today);


            if (wasCompleted) {

                /*
                 * Undo completion.
                 */
                habit.removeCompletedDate(today);

                habitStorage.saveHabits(
                        habits
                );

                refreshScreen();

                showSnackbar(
                        "Completion undone",
                        null
                );


            } else {

                /*
                 * Complete today.
                 */
                habit.addCompletedDate(today);

                habitStorage.saveHabits(
                        habits
                );

                refreshScreen();


                /*
                 * Give the user a real UNDO action.
                 */
                showSnackbar(
                        "Habit completed ✓",
                        () -> {

                            habit.removeCompletedDate(
                                    today
                            );

                            habitStorage.saveHabits(
                                    habits
                            );

                            refreshScreen();
                        }
                );
            }
        });


        // =====================================================
        // DELETE / UNDO DELETE
        // =====================================================

        btnDelete.setOnClickListener(v -> {

            int position =
                    habits.indexOf(habit);


            if (position == -1) {
                return;
            }


            /*
             * Remove habit.
             */
            habits.remove(position);

            habitStorage.saveHabits(
                    habits
            );

            refreshScreen();


            /*
             * Allow user to restore it.
             */
            showSnackbar(
                    "Habit deleted",
                    () -> {

                        habits.add(
                                position,
                                habit
                        );

                        habitStorage.saveHabits(
                                habits
                        );

                        refreshScreen();
                    }
            );
        });


        habitContainer.addView(card);
    }


    // =========================================================
    // SNACKBAR
    // =========================================================

    private void showSnackbar(
            String message,
            Runnable undoAction
    ) {

        Snackbar snackbar =
                Snackbar.make(
                        habitContainer,
                        message,
                        Snackbar.LENGTH_LONG
                );


        if (undoAction != null) {

            snackbar.setAction(
                    "UNDO",
                    v -> undoAction.run()
            );
        }


        snackbar.show();
    }


    // =========================================================
    // WEEKLY PROGRESS
    // =========================================================

    private void createWeekProgress(
            LinearLayout container,
            Habit habit
    ) {

        container.removeAllViews();


        Calendar calendar =
                Calendar.getInstance();


        /*
         * Start six days ago.
         * That gives us seven days including today.
         */
        calendar.add(
                Calendar.DAY_OF_YEAR,
                -6
        );


        for (int i = 0; i < 7; i++) {

            Date date =
                    calendar.getTime();


            String dateKey =
                    formatDate(date);


            boolean completed =
                    habit.isCompletedOn(
                            dateKey
                    );


            boolean today =
                    dateKey.equals(
                            getTodayDate()
                    );


            LinearLayout dayLayout =
                    new LinearLayout(this);


            dayLayout.setOrientation(
                    LinearLayout.VERTICAL
            );


            dayLayout.setGravity(
                    android.view.Gravity.CENTER
            );


            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1
                    );


            dayLayout.setLayoutParams(
                    params
            );


            // -------------------------------------------------
            // Day letter
            // -------------------------------------------------

            TextView dayName =
                    new TextView(this);


            SimpleDateFormat dayFormat =
                    new SimpleDateFormat(
                            "EEEEE",
                            Locale.getDefault()
                    );


            dayName.setText(
                    dayFormat.format(date)
            );


            dayName.setTextSize(11);


            dayName.setGravity(
                    android.view.Gravity.CENTER
            );


            dayName.setTextColor(
                    getColor(
                            R.color.text_muted
                    )
            );


            // -------------------------------------------------
            // Day circle
            // -------------------------------------------------

            TextView circle =
                    new TextView(this);


            LinearLayout.LayoutParams circleParams =
                    new LinearLayout.LayoutParams(
                            30,
                            30
                    );


            circleParams.topMargin = 6;


            circle.setLayoutParams(
                    circleParams
            );


            circle.setGravity(
                    android.view.Gravity.CENTER
            );


            if (completed) {

                circle.setBackgroundResource(
                        R.drawable.bg_week_completed
                );

                circle.setText("✓");

                circle.setTextColor(
                        getColor(R.color.white)
                );

            } else if (today) {

                circle.setBackgroundResource(
                        R.drawable.bg_week_today
                );

                circle.setText("");

            } else {

                circle.setBackgroundResource(
                        R.drawable.bg_week_empty
                );

                circle.setText("");
            }


            dayLayout.addView(
                    dayName
            );


            dayLayout.addView(
                    circle
            );


            container.addView(
                    dayLayout
            );


            calendar.add(
                    Calendar.DAY_OF_YEAR,
                    1
            );
        }
    }


    // =========================================================
    // COMPLETE BUTTON
    // =========================================================

    private void updateCompleteButton(
            Button button,
            Habit habit
    ) {

        boolean completed =
                habit.isCompletedOn(
                        getTodayDate()
                );


        if (completed) {

            button.setText(
                    "✓  Completed today"
            );

            button.setTextColor(
                    getColor(R.color.white)
            );

            button.setBackgroundResource(
                    R.drawable.bg_complete_button_done
            );

        } else {

            button.setText(
                    "✓  Complete today"
            );

            button.setTextColor(
                    getColor(R.color.green_dark)
            );

            button.setBackgroundResource(
                    R.drawable.bg_complete_button
            );
        }
    }


    // =========================================================
    // SUMMARY
    // =========================================================

    private void updateSummary() {

        int total =
                habits.size();


        int completed =
                0;


        int bestStreak =
                0;


        for (Habit habit : habits) {

            if (habit.isCompletedOn(
                    getTodayDate()
            )) {

                completed++;
            }


            int streak =
                    calculateCurrentStreak(
                            habit
                    );


            if (streak > bestStreak) {

                bestStreak = streak;
            }
        }


        // -----------------------------------------------------
        // Habit count
        // -----------------------------------------------------

        tvHabitCount.setText(
                total +
                        (total == 1
                                ? " habit"
                                : " habits")
        );


        // -----------------------------------------------------
        // Best streak
        // -----------------------------------------------------

        tvBestStreak.setText(
                "🔥 " +
                        bestStreak +
                        (bestStreak == 1
                                ? " day"
                                : " days")
        );


        // -----------------------------------------------------
        // Percentage
        // -----------------------------------------------------

        int percentage;


        if (total == 0) {

            percentage = 0;

        } else {

            percentage =
                    (completed * 100) / total;
        }


        tvProgress.setText(
                percentage + "%"
        );


        tvProgressDescription.setText(
                completed +
                        " of " +
                        total +
                        " habits completed"
        );


        progressBar.setProgress(
                percentage
        );
    }


    // =========================================================
    // CURRENT STREAK
    // =========================================================

    private int calculateCurrentStreak(
            Habit habit
    ) {

        Calendar calendar =
                Calendar.getInstance();


        int streak = 0;


        String today =
                getTodayDate();


        /*
         * If today isn't complete, we allow the streak
         * to continue from yesterday.
         */
        if (!habit.isCompletedOn(today)) {

            calendar.add(
                    Calendar.DAY_OF_YEAR,
                    -1
            );


            String yesterday =
                    formatCalendarDate(
                            calendar
                    );


            /*
             * If yesterday isn't complete either,
             * there is no current streak.
             */
            if (!habit.isCompletedOn(
                    yesterday
            )) {

                return 0;
            }
        }


        /*
         * Reset calendar to today.
         */
        calendar =
                Calendar.getInstance();


        /*
         * If today isn't complete, start checking
         * from yesterday.
         */
        if (!habit.isCompletedOn(today)) {

            calendar.add(
                    Calendar.DAY_OF_YEAR,
                    -1
            );
        }


        while (true) {

            String date =
                    formatCalendarDate(
                            calendar
                    );


            if (habit.isCompletedOn(date)) {

                streak++;


                calendar.add(
                        Calendar.DAY_OF_YEAR,
                        -1
                );

            } else {

                break;
            }
        }


        return streak;
    }


    // =========================================================
    // GREETING
    // =========================================================

    private void updateGreeting() {

        Calendar calendar =
                Calendar.getInstance();


        int hour =
                calendar.get(
                        Calendar.HOUR_OF_DAY
                );


        String greeting;


        if (hour < 12) {

            greeting = "Good morning.";

        } else if (hour < 18) {

            greeting = "Good afternoon.";

        } else {

            greeting = "Good evening.";
        }


        tvGreeting.setText(
                greeting
        );
    }


    // =========================================================
    // DATE
    // =========================================================

    private void updateDate() {

        SimpleDateFormat format =
                new SimpleDateFormat(
                        "EEEE, MMMM d",
                        Locale.getDefault()
                );


        tvDate.setText(
                format.format(
                        new Date()
                )
        );
    }


    // =========================================================
    // DATE HELPERS
    // =========================================================

    private String getTodayDate() {

        return formatDate(
                new Date()
        );
    }


    private String formatDate(
            Date date
    ) {

        SimpleDateFormat format =
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.US
                );


        return format.format(date);
    }


    private String formatCalendarDate(
            Calendar calendar
    ) {

        return formatDate(
                calendar.getTime()
        );
    }
}