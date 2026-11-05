
package com.example.classplanner;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.classplanner.ui.timetable.TimetableFragment;
import com.example.classplanner.ui.tasks.TasksFragment;
import com.example.classplanner.ui.settings.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    private FrameLayout contentContainer;
    private BottomNavigationView bottomNavigation;
    private TextView txtAppTitle;

    private TimetableFragment timetableFragment;
    private TasksFragment tasksFragment;
    private SettingsFragment settingsFragment;

    private int currentScreen = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        requestNotificationPermission();
        contentContainer = findViewById(R.id.contentContainer);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        txtAppTitle = findViewById(R.id.txtAppTitle);

        timetableFragment = new TimetableFragment();
        tasksFragment = new TasksFragment();
        settingsFragment = new SettingsFragment();

        if (savedInstanceState != null) {
            currentScreen = savedInstanceState.getInt("screen", 0);
        }

        bottomNavigation.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_timetable) {
                showScreen(0);
                return true;
            }

            if (id == R.id.nav_tasks) {
                showScreen(1);
                return true;
            }

            if (id == R.id.nav_settings) {
                showScreen(2);
                return true;
            }

            return false;
        });

        bottomNavigation.setSelectedItemId(
                currentScreen == 0 ? R.id.nav_timetable :
                        currentScreen == 1 ? R.id.nav_tasks :
                                R.id.nav_settings
        );

        showScreen(currentScreen);
    }


    private void requestNotificationPermission() {

        if (Build.VERSION.SDK_INT >= 33) {

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.POST_NOTIFICATIONS
                        },
                        1001
                );
            }
        }
    }


    private void showScreen(int screen) {

        currentScreen = screen;

        View view;

        if (screen == 0) {

            view = timetableFragment.onCreateView(
                    getLayoutInflater(),
                    contentContainer,
                    null
            );

            txtAppTitle.setText("Thời khóa biểu");

        } else if (screen == 1) {

            view = tasksFragment.onCreateView(
                    getLayoutInflater(),
                    contentContainer,
                    null
            );

            txtAppTitle.setText("Nhiệm vụ");

        } else {

            view = settingsFragment.onCreateView(
                    getLayoutInflater(),
                    contentContainer,
                    null
            );

            txtAppTitle.setText("Cài đặt");
        }

        contentContainer.removeAllViews();
        contentContainer.addView(view);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("screen", currentScreen);
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            android.content.Intent data
    ) {

        super.onActivityResult(requestCode, resultCode, data);

        if (settingsFragment != null) {

            settingsFragment.onActivityResult(
                    requestCode,
                    resultCode,
                    data
            );
        }
    }

}
