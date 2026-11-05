
package com.example.classplanner.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtils {

    public static String formatTime(int totalMinutes) {

        int hour = totalMinutes / 60;
        int minute = totalMinutes % 60;

        return String.format(
                Locale.getDefault(),
                "%02d:%02d",
                hour,
                minute
        );
    }

    public static String formatDateTime(long millis) {

        SimpleDateFormat format = new SimpleDateFormat(
                "dd/MM/yyyy HH:mm",
                Locale.getDefault()
        );

        return format.format(new Date(millis));
    }

    public static String getCountdown(long deadlineMillis) {

        long remaining =
                deadlineMillis - System.currentTimeMillis();

        if (remaining <= 0) {
            return "Đã quá hạn";
        }

        long totalMinutes = remaining / (60 * 1000);

        long days = totalMinutes / (24 * 60);
        long hours = (totalMinutes % (24 * 60)) / 60;
        long minutes = totalMinutes % 60;

        if (days > 0) {
            return "Còn " + days + " ngày " + hours + " giờ";
        }

        if (hours > 0) {
            return "Còn " + hours + " giờ " + minutes + " phút";
        }

        return "Còn " + minutes + " phút";
    }
}
