
package com.example.classplanner.ui.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.classplanner.R;
import com.example.classplanner.settings.AppPreferences;

public class SettingsFragment {

    public static final int REQUEST_NOTIFICATION_SOUND = 1001;
    public static final int REQUEST_CLASS_ALARM_SOUND = 1002;

    private Context context;

    private AppPreferences preferences;

    private Spinner spinnerAlarmLead;

    private CheckBox checkDeadlineOneDay;
    private CheckBox checkDeadlineOneHour;

    private Button btnNotificationSound;
    private Button btnClassAlarmSound;

    private final int[] leadMinutes = {
            0, 5, 10, 15, 20, 30, 45, 60
    };

    private final String[] leadLabels = {
            "Không báo trước",
            "5 phút",
            "10 phút",
            "15 phút",
            "20 phút",
            "30 phút",
            "45 phút",
            "60 phút"
    };

    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup parent,
            android.os.Bundle savedInstanceState
    ) {

        context = parent.getContext();

        preferences = new AppPreferences(context);

        View view = inflater.inflate(
                R.layout.fragment_settings,
                parent,
                false
        );

        spinnerAlarmLead =
                view.findViewById(R.id.spinnerAlarmLead);

        checkDeadlineOneDay =
                view.findViewById(R.id.checkDeadlineOneDay);

        checkDeadlineOneHour =
                view.findViewById(R.id.checkDeadlineOneHour);

        btnNotificationSound =
                view.findViewById(R.id.btnNotificationSound);

        btnClassAlarmSound =
                view.findViewById(R.id.btnClassAlarmSound);

        Button btnReset =
                view.findViewById(R.id.btnResetSettings);

        setupSpinner();

        loadSettings();

        btnNotificationSound.setOnClickListener(v ->
                openRingtonePicker(
                        REQUEST_NOTIFICATION_SOUND,
                        RingtoneManager.TYPE_NOTIFICATION
                )
        );

        btnClassAlarmSound.setOnClickListener(v ->
                openRingtonePicker(
                        REQUEST_CLASS_ALARM_SOUND,
                        RingtoneManager.TYPE_ALARM
                )
        );

        spinnerAlarmLead.setOnItemSelectedListener(
                new android.widget.AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            android.widget.AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {

                        preferences.setClassAlarmLeadMinutes(
                                leadMinutes[position]
                        );
                    }

                    @Override
                    public void onNothingSelected(
                            android.widget.AdapterView<?> parent
                    ) {
                    }
                }
        );

        checkDeadlineOneDay.setOnCheckedChangeListener(
                (button, checked) ->
                        preferences.setDeadlineOneDayEnabled(checked)
        );

        checkDeadlineOneHour.setOnCheckedChangeListener(
                (button, checked) ->
                        preferences.setDeadlineOneHourEnabled(checked)
        );

        btnReset.setOnClickListener(v -> {

            new AlertDialog.Builder(context)
                    .setTitle("Khôi phục cài đặt")
                    .setMessage(
                            "Mày có chắc muốn khôi phục tất cả cài đặt mặc định không?"
                    )
                    .setNegativeButton("Hủy", null)
                    .setPositiveButton("Khôi phục", (dialog, which) -> {

                        preferences.resetSettings();

                        loadSettings();

                        Toast.makeText(
                                context,
                                "Đã khôi phục cài đặt",
                                Toast.LENGTH_SHORT
                        ).show();
                    })
                    .show();
        });

        return view;
    }

    private void setupSpinner() {

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        context,
                        android.R.layout.simple_spinner_item,
                        leadLabels
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerAlarmLead.setAdapter(adapter);
    }

    private void loadSettings() {

        int savedMinutes =
                preferences.getClassAlarmLeadMinutes();

        int selectedPosition = 3;

        for (int i = 0; i < leadMinutes.length; i++) {

            if (leadMinutes[i] == savedMinutes) {
                selectedPosition = i;
                break;
            }
        }

        spinnerAlarmLead.setSelection(selectedPosition);

        checkDeadlineOneDay.setChecked(
                preferences.isDeadlineOneDayEnabled()
        );

        checkDeadlineOneHour.setChecked(
                preferences.isDeadlineOneHourEnabled()
        );

        btnNotificationSound.setText(
                "Âm thanh thông báo: "
                        + getRingtoneTitle(
                        preferences.getNotificationSoundUri()
                )
        );

        btnClassAlarmSound.setText(
                "Nhạc chuông đi học: "
                        + getRingtoneTitle(
                        preferences.getClassAlarmSoundUri()
                )
        );
    }

    private String getRingtoneTitle(String uriString) {

        try {

            Uri uri = Uri.parse(uriString);

            Ringtone ringtone =
                    RingtoneManager.getRingtone(context, uri);

            if (ringtone != null) {
                return ringtone.getTitle(context);
            }

        } catch (Exception ignored) {
        }

        return "Mặc định";
    }

    private void openRingtonePicker(
            int requestCode,
            int ringtoneType
    ) {

        Intent intent = new Intent(
                RingtoneManager.ACTION_RINGTONE_PICKER
        );

        intent.putExtra(
                RingtoneManager.EXTRA_RINGTONE_TYPE,
                ringtoneType
        );

        intent.putExtra(
                RingtoneManager.EXTRA_RINGTONE_TITLE,
                ringtoneType == RingtoneManager.TYPE_ALARM
                        ? "Chọn nhạc chuông đi học"
                        : "Chọn âm thanh thông báo"
        );

        intent.putExtra(
                RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
                Uri.parse(
                        ringtoneType == RingtoneManager.TYPE_ALARM
                                ? preferences.getClassAlarmSoundUri()
                                : preferences.getNotificationSoundUri()
                )
        );

        intent.putExtra(
                RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT,
                true
        );

        intent.putExtra(
                RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT,
                true
        );

        ((Activity) context).startActivityForResult(
                intent,
                requestCode
        );
    }

    public void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ) {

        if (resultCode != Activity.RESULT_OK || data == null) {
            return;
        }

        Uri uri = data.getParcelableExtra(
                RingtoneManager.EXTRA_RINGTONE_PICKED_URI
        );

        if (uri == null) {
            return;
        }

        String uriString = uri.toString();

        if (requestCode == REQUEST_NOTIFICATION_SOUND) {

            preferences.setNotificationSoundUri(uriString);

        } else if (requestCode == REQUEST_CLASS_ALARM_SOUND) {

            preferences.setClassAlarmSoundUri(uriString);
        }

        loadSettings();
    }
}
