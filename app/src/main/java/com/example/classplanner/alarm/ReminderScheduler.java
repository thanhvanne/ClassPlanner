
package com.example.classplanner.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import java.util.Calendar;

public class ReminderScheduler {

    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_MESSAGE = "message";
    public static final String EXTRA_SOUND_URI = "sound_uri";
    public static final String EXTRA_NOTIFICATION_ID = "notification_id";

    public static final String EXTRA_RECURRING = "recurring";
    public static final String EXTRA_DAY_OF_WEEK = "day_of_week";
    public static final String EXTRA_START_MINUTE = "start_minute";
    public static final String EXTRA_ROOM = "room";
    public static final String EXTRA_LEAD_MINUTES = "lead_minutes";
    public static final String EXTRA_ALARM_ID = "alarm_id";

    private static PendingIntent createPendingIntent(
            Context context,
            int requestCode,
            Intent intent
    ) {
        return PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT |
                        PendingIntent.FLAG_IMMUTABLE
        );
    }

    private static void setAlarm(
            Context context,
            long triggerAtMillis,
            int requestCode,
            Intent intent
    ) {
        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager == null) return;

        PendingIntent pendingIntent =
                createPendingIntent(context, requestCode, intent);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                        && !alarmManager.canScheduleExactAlarms()) {

                    alarmManager.setAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            triggerAtMillis,
                            pendingIntent
                    );

                } else {
                    alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            triggerAtMillis,
                            pendingIntent
                    );
                }

            } else {
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        pendingIntent
                );
            }

        } catch (SecurityException e) {
            alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
            );
        }
    }

    // Tính thời điểm nhắc trước giờ học tiếp theo.

    private static long getNextClassReminderTime(
            int dayOfWeek,
            int startMinute,
            int leadMinutes
    ) {
        Calendar now = Calendar.getInstance();
        Calendar target = Calendar.getInstance();

        // ClassSession: Thứ 2 = 1, ..., Chủ nhật = 7.
        // Calendar: Chủ nhật = 1, Thứ 2 = 2, ..., Thứ 7 = 7.
        int calendarDay = dayOfWeek == 7 ? 1 : dayOfWeek + 1;

        target.set(Calendar.DAY_OF_WEEK, calendarDay);
        target.set(Calendar.HOUR_OF_DAY, startMinute / 60);
        target.set(Calendar.MINUTE, startMinute % 60);
        target.set(Calendar.SECOND, 0);
        target.set(Calendar.MILLISECOND, 0);

        target.add(Calendar.MINUTE, -leadMinutes);

        if (!target.after(now)) {
            target.add(Calendar.DAY_OF_YEAR, 7);
        }

        return target.getTimeInMillis();
    }


    // Lên lịch nhắc giờ học hàng tuần.
    public static void scheduleWeeklyClass(
            Context context,
            int sessionId,
            String subjectName,
            int dayOfWeek,
            int startMinute,
            String room,
            int leadMinutes,
            String soundUri
    ) {
        long triggerTime = getNextClassReminderTime(
                dayOfWeek,
                startMinute,
                leadMinutes
        );

        int requestCode = 100000 + sessionId;

        Intent intent = new Intent(context, ReminderReceiver.class);

        intent.putExtra(EXTRA_TITLE, "Sắp đến giờ học");
        intent.putExtra(
                EXTRA_MESSAGE,
                subjectName + "\nPhòng: " + room
        );

        intent.putExtra(EXTRA_SOUND_URI, soundUri);
        intent.putExtra(EXTRA_NOTIFICATION_ID, requestCode);
        intent.putExtra(EXTRA_ALARM_ID, sessionId);

        intent.putExtra(EXTRA_RECURRING, true);
        intent.putExtra(EXTRA_DAY_OF_WEEK, dayOfWeek);
        intent.putExtra(EXTRA_START_MINUTE, startMinute);
        intent.putExtra(EXTRA_ROOM, room);
        intent.putExtra(EXTRA_LEAD_MINUTES, leadMinutes);

        setAlarm(
                context,
                triggerTime,
                requestCode,
                intent
        );
    }

    // Lên lịch nhắc deadline.
    public static void scheduleDeadline(
            Context context,
            int taskId,
            String taskTitle,
            long deadlineMillis,
            int reminderMinutes,
            String soundUri
    ) {
        long triggerTime =
                deadlineMillis - reminderMinutes * 60L * 1000L;

        if (triggerTime <= System.currentTimeMillis()) {
            return;
        }

        int requestCode = 200000
                + taskId * 100
                + reminderMinutes;

        Intent intent = new Intent(context, ReminderReceiver.class);

        intent.putExtra(EXTRA_TITLE, "Nhắc nhở deadline");
        intent.putExtra(
                EXTRA_MESSAGE,
                taskTitle + "\nSắp đến hạn hoàn thành nhiệm vụ."
        );

        intent.putExtra(EXTRA_SOUND_URI, soundUri);
        intent.putExtra(EXTRA_NOTIFICATION_ID, requestCode);
        intent.putExtra(EXTRA_RECURRING, false);

        setAlarm(
                context,
                triggerTime,
                requestCode,
                intent
        );
    }

    // Hủy lịch nhắc.
    public static void cancelAlarm(
            Context context,
            int requestCode
    ) {
        Intent intent = new Intent(context, ReminderReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_NO_CREATE |
                        PendingIntent.FLAG_IMMUTABLE
        );

        if (pendingIntent == null) return;

        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }

        pendingIntent.cancel();
    }

    public static void cancelClassAlarm(
            Context context,
            int sessionId
    ) {
        cancelAlarm(context, 100000 + sessionId);
    }
}
