
package com.example.classplanner.alarm;

import android.content.Context;

import com.example.classplanner.data.AppDatabase;
import com.example.classplanner.data.entity.ClassSession;
import com.example.classplanner.data.entity.Subject;
import com.example.classplanner.data.entity.Task;
import com.example.classplanner.settings.AppPreferences;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AlarmManagerHelper {

    private static final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    public static void scheduleAll(Context context) {

        Context appContext = context.getApplicationContext();

        executor.execute(() -> {

            AppDatabase db = AppDatabase.getDatabase(appContext);

            AppPreferences preferences =
                    new AppPreferences(appContext);

            List<ClassSession> sessions =
                    db.classSessionDao().getAll();

            List<Subject> subjects =
                    db.subjectDao().getAll();

            List<Task> tasks =
                    db.taskDao().getIncomplete();

            int leadMinutes = preferences.getClassLeadMinutes();

            String classSound =
                    preferences.getClassAlarmSoundUri();

            String notificationSound =
                    preferences.getNotificationSoundUri();

            for (ClassSession session : sessions) {

                if (!session.alarmEnabled) continue;

                Subject subject = null;

                for (Subject s : subjects) {
                    if (s.id == session.subjectId) {
                        subject = s;
                        break;
                    }
                }

                if (subject == null) continue;

                ReminderScheduler.scheduleWeeklyClass(
                        appContext,
                        session.id,
                        subject.name,
                        session.dayOfWeek,
                        session.startMinute,
                        session.room,
                        leadMinutes,
                        classSound
                );
            }

            for (Task task : tasks) {

                if (task.deadlineMillis <= System.currentTimeMillis()) {
                    continue;
                }

                if (preferences.isDeadlineOneDayEnabled()) {

                    ReminderScheduler.scheduleDeadline(
                            appContext,
                            task.id,
                            task.title,
                            task.deadlineMillis,
                            24 * 60,
                            notificationSound
                    );
                }

                if (preferences.isDeadlineOneHourEnabled()) {

                    ReminderScheduler.scheduleDeadline(
                            appContext,
                            task.id,
                            task.title,
                            task.deadlineMillis,
                            60,
                            notificationSound
                    );
                }
            }
        });
    }
}
