
package com.example.classplanner.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.RingtoneManager;

public class AppPreferences {

    private static final String PREF_NAME =
            "class_planner_settings";

    private static final String KEY_NOTIFICATION_SOUND =
            "notification_sound";

    private static final String KEY_CLASS_ALARM_SOUND =
            "class_alarm_sound";

    private static final String KEY_CLASS_ALARM_LEAD =
            "class_alarm_lead";

    private static final String KEY_DEADLINE_ONE_DAY =
            "deadline_one_day";

    private static final String KEY_DEADLINE_ONE_HOUR =
            "deadline_one_hour";

    private final SharedPreferences preferences;

    public AppPreferences(Context context) {

        preferences = context.getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
        );
    }

    public String getNotificationSoundUri() {

        return preferences.getString(
                KEY_NOTIFICATION_SOUND,
                RingtoneManager.getDefaultUri(
                        RingtoneManager.TYPE_NOTIFICATION
                ).toString()
        );
    }

    public void setNotificationSoundUri(String uri) {

        preferences.edit()
                .putString(KEY_NOTIFICATION_SOUND, uri)
                .apply();
    }

    public String getClassAlarmSoundUri() {

        return preferences.getString(
                KEY_CLASS_ALARM_SOUND,
                RingtoneManager.getDefaultUri(
                        RingtoneManager.TYPE_ALARM
                ).toString()
        );
    }

    public void setClassAlarmSoundUri(String uri) {

        preferences.edit()
                .putString(KEY_CLASS_ALARM_SOUND, uri)
                .apply();
    }

    public int getClassAlarmLeadMinutes() {

        return preferences.getInt(
                KEY_CLASS_ALARM_LEAD,
                15
        );
    }

    public void setClassAlarmLeadMinutes(int minutes) {

        preferences.edit()
                .putInt(KEY_CLASS_ALARM_LEAD, minutes)
                .apply();
    }

    public boolean isDeadlineOneDayEnabled() {

        return preferences.getBoolean(
                KEY_DEADLINE_ONE_DAY,
                true
        );
    }

    public void setDeadlineOneDayEnabled(boolean enabled) {

        preferences.edit()
                .putBoolean(KEY_DEADLINE_ONE_DAY, enabled)
                .apply();
    }

    public boolean isDeadlineOneHourEnabled() {

        return preferences.getBoolean(
                KEY_DEADLINE_ONE_HOUR,
                true
        );
    }

    public void setDeadlineOneHourEnabled(boolean enabled) {

        preferences.edit()
                .putBoolean(KEY_DEADLINE_ONE_HOUR, enabled)
                .apply();
    }

    public void resetSettings() {

        preferences.edit().clear().apply();
    }
}
