
package com.example.classplanner.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.classplanner.data.dao.ClassSessionDao;
import com.example.classplanner.data.dao.SubjectDao;
import com.example.classplanner.data.dao.TaskDao;

import com.example.classplanner.data.entity.ClassSession;
import com.example.classplanner.data.entity.Subject;
import com.example.classplanner.data.entity.Task;

@Database(
        entities = {
                Subject.class,
                ClassSession.class,
                Task.class
        },
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract SubjectDao subjectDao();

    public abstract ClassSessionDao classSessionDao();

    public abstract TaskDao taskDao();

    public static AppDatabase getInstance(Context context) {

        if (INSTANCE == null) {

            synchronized (AppDatabase.class) {

                if (INSTANCE == null) {

                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "class_planner_database"
                    ).build();

                }
            }
        }

        return INSTANCE;
    }
}
