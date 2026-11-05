
package com.example.classplanner.alarm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.classplanner.MainActivity;
import com.example.classplanner.R;

public class ReminderReceiver extends BroadcastReceiver {

    private static final String CHANNEL_PREFIX = "classplanner_reminder_";

    @Override
    public void onReceive(Context context, Intent intent) {

        String title = intent.getStringExtra(
                ReminderScheduler.EXTRA_TITLE
        );

        String message = intent.getStringExtra(
                ReminderScheduler.EXTRA_MESSAGE
        );

        String soundUri = intent.getStringExtra(
                ReminderScheduler.EXTRA_SOUND_URI
        );

        int notificationId = intent.getIntExtra(
                ReminderScheduler.EXTRA_NOTIFICATION_ID,
                1000
        );

        if (title == null) title = "Class Planner";
        if (message == null) message = "Bạn có một lời nhắc.";

        createNotificationChannel(context, soundUri);

        Intent openAppIntent = new Intent(context, MainActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(
                context,
                notificationId,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT |
                        PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(
                        context,
                        getChannelId(soundUri)
                )
                        .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setStyle(
                                new NotificationCompat.BigTextStyle()
                                        .bigText(message)
                        )
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setCategory(NotificationCompat.CATEGORY_ALARM)
                        .setAutoCancel(true)
                        .setContentIntent(contentIntent);

        // Android dưới phiên bản 8 sử dụng âm thanh trực tiếp.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {

            if ("silent".equals(soundUri)) {
                builder.setSound(null);

            } else if (soundUri != null && !soundUri.isEmpty()) {
                builder.setSound(Uri.parse(soundUri));

            } else {
                builder.setDefaults(NotificationCompat.DEFAULT_SOUND);
            }
        }

        try {
            NotificationManagerCompat.from(context).notify(
                    notificationId,
                    builder.build()
            );
        } catch (SecurityException ignored) {
            // Người dùng chưa cấp quyền thông báo.
        }

        // Nếu đây là nhắc giờ học, lên lịch cho tuần tiếp theo.
        boolean recurring = intent.getBooleanExtra(
                ReminderScheduler.EXTRA_RECURRING,
                false
        );

        if (recurring) {

            int sessionId = intent.getIntExtra(
                    ReminderScheduler.EXTRA_ALARM_ID,
                    -1
            );

            int dayOfWeek = intent.getIntExtra(
                    ReminderScheduler.EXTRA_DAY_OF_WEEK,
                    2
            );

            int startMinute = intent.getIntExtra(
                    ReminderScheduler.EXTRA_START_MINUTE,
                    0
            );

            String room = intent.getStringExtra(
                    ReminderScheduler.EXTRA_ROOM
            );

            int leadMinutes = intent.getIntExtra(
                    ReminderScheduler.EXTRA_LEAD_MINUTES,
                    15
            );

            if (sessionId >= 0) {
                ReminderScheduler.scheduleWeeklyClass(
                        context,
                        sessionId,
                        extractSubjectName(message),
                        dayOfWeek,
                        startMinute,
                        room == null ? "" : room,
                        leadMinutes,
                        soundUri
                );
            }
        }
    }

    private String extractSubjectName(String message) {
        if (message == null) return "Môn học";

        int lineBreak = message.indexOf('\n');

        if (lineBreak >= 0) {
            return message.substring(0, lineBreak);
        }

        return message;
    }

    private String getChannelId(String soundUri) {
        String soundKey = soundUri == null ? "default" : soundUri;
        return CHANNEL_PREFIX + Math.abs(soundKey.hashCode());
    }

    private void createNotificationChannel(
            Context context,
            String soundUri
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        String channelId = getChannelId(soundUri);

        NotificationManager manager =
                context.getSystemService(NotificationManager.class);

        if (manager == null) return;

        // Không tạo lại nếu kênh đã tồn tại.
        if (manager.getNotificationChannel(channelId) != null) {
            return;
        }

        NotificationChannel channel = new NotificationChannel(
                channelId,
                "Nhắc nhở Class Planner",
                NotificationManager.IMPORTANCE_HIGH
        );

        channel.setDescription("Thông báo giờ học và deadline");

        if ("silent".equals(soundUri)) {

            channel.setSound(null, null);

        } else {

            Uri sound = (soundUri != null && !soundUri.isEmpty())
                    ? Uri.parse(soundUri)
                    : android.provider.Settings.System.DEFAULT_NOTIFICATION_URI;

            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            channel.setSound(sound, attributes);
        }

        channel.enableVibration(true);

        manager.createNotificationChannel(channel);
    }
}
