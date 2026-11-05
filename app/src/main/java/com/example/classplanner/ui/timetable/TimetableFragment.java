
package com.example.classplanner.ui.timetable;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classplanner.R;
import com.example.classplanner.data.AppDatabase;
import com.example.classplanner.data.entity.ClassSession;
import com.example.classplanner.data.entity.Subject;
import com.example.classplanner.util.ColorPalette;
import com.example.classplanner.util.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TimetableFragment {

    private Context context;

    private AppDatabase database;

    private LinearLayout dayContainer;
    private RecyclerView recyclerSessions;
    private TextView txtSelectedDay;

    private SessionAdapter adapter;

    private int selectedDay = 1;

    private final String[] days = {
            "Thứ hai",
            "Thứ ba",
            "Thứ tư",
            "Thứ năm",
            "Thứ sáu",
            "Thứ bảy",
            "Chủ nhật"
    };

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup parent,
            android.os.Bundle savedInstanceState
    ) {

        context = parent.getContext();

        View view = inflater.inflate(
                R.layout.fragment_timetable,
                parent,
                false
        );

        database = AppDatabase.getInstance(context);

        dayContainer = view.findViewById(R.id.dayContainer);
        recyclerSessions = view.findViewById(R.id.recyclerSessions);
        txtSelectedDay = view.findViewById(R.id.txtSelectedDay);

        Button btnAdd = view.findViewById(R.id.btnAddSession);

        recyclerSessions.setLayoutManager(
                new LinearLayoutManager(context)
        );

        adapter = new SessionAdapter(
                new ArrayList<>(),
                this::showSessionOptions
        );

        recyclerSessions.setAdapter(adapter);

        createDayButtons();

        btnAdd.setOnClickListener(v -> showAddDialog());

        loadSessions();

        return view;
    }

    private void createDayButtons() {

        dayContainer.removeAllViews();

        for (int i = 0; i < days.length; i++) {

            final int day = i + 1;

            Button button = new Button(context);

            button.setText(days[i]);
            button.setAllCaps(false);

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                    );

            params.setMargins(4, 0, 4, 0);

            button.setLayoutParams(params);

            button.setOnClickListener(v -> {

                selectedDay = day;

                txtSelectedDay.setText(days[day - 1]);

                updateDayButtons();

                loadSessions();
            });

            dayContainer.addView(button);
        }

        updateDayButtons();

        txtSelectedDay.setText(days[selectedDay - 1]);
    }

    private void updateDayButtons() {

        for (int i = 0; i < dayContainer.getChildCount(); i++) {

            Button button = (Button) dayContainer.getChildAt(i);

            if (i + 1 == selectedDay) {

                button.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(
                                Color.rgb(66, 133, 244)
                        )
                );

                button.setTextColor(Color.WHITE);

            } else {

                button.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(
                                Color.rgb(235, 238, 245)
                        )
                );

                button.setTextColor(Color.DKGRAY);
            }
        }
    }

    private void loadSessions() {

        executor.execute(() -> {

            List<ClassSession> sessions =
                    database.classSessionDao().getByDay(selectedDay);

            List<SessionAdapter.SessionItem> items =
                    new ArrayList<>();

            for (ClassSession session : sessions) {

                Subject subject =
                        database.subjectDao().getById(session.subjectId);

                if (subject != null) {

                    items.add(
                            new SessionAdapter.SessionItem(
                                    session,
                                    subject
                            )
                    );
                }
            }

            if (!isContextAvailable()) return;

            recyclerSessions.post(() ->
                    adapter.setItems(items)
            );
        });
    }

    private boolean isContextAvailable() {

        return context != null;
    }

    private void showAddDialog() {

        LinearLayout layout = new LinearLayout(context);

        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 10);

        EditText edtSubject = new EditText(context);
        edtSubject.setHint("Tên môn học");

        EditText edtStart = new EditText(context);
        edtStart.setHint("Giờ bắt đầu (VD: 07:30)");

        EditText edtEnd = new EditText(context);
        edtEnd.setHint("Giờ kết thúc (VD: 09:10)");

        EditText edtRoom = new EditText(context);
        edtRoom.setHint("Phòng học");

        Spinner spinnerColor = new Spinner(context);

        String[] colors = {
                "Xanh dương",
                "Xanh lá",
                "Vàng",
                "Đỏ",
                "Tím",
                "Xanh ngọc",
                "Cam",
                "Nâu",
                "Hồng",
                "Xám xanh"
        };

        spinnerColor.setAdapter(
                new ArrayAdapter<>(
                        context,
                        android.R.layout.simple_spinner_dropdown_item,
                        colors
                )
        );

        layout.addView(edtSubject);
        layout.addView(edtStart);
        layout.addView(edtEnd);
        layout.addView(edtRoom);
        layout.addView(spinnerColor);

        new AlertDialog.Builder(context)
                .setTitle("Thêm buổi học")
                .setView(layout)
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Lưu", (dialog, which) -> {

                    String subjectName =
                            edtSubject.getText().toString().trim();

                    String start =
                            edtStart.getText().toString().trim();

                    String end =
                            edtEnd.getText().toString().trim();

                    String room =
                            edtRoom.getText().toString().trim();

                    int startMinute = parseTime(start);
                    int endMinute = parseTime(end);

                    if (subjectName.isEmpty()
                            || startMinute < 0
                            || endMinute <= startMinute) {

                        Toast.makeText(
                                context,
                                "Thông tin không hợp lệ",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    int color = ColorPalette.getColor(
                            spinnerColor.getSelectedItemPosition()
                    );

                    saveSession(
                            subjectName,
                            startMinute,
                            endMinute,
                            room,
                            color
                    );
                })
                .show();
    }

    private int parseTime(String value) {

        try {

            String[] parts = value.split(":");

            if (parts.length != 2) return -1;

            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);

            if (hour < 0 || hour > 23
                    || minute < 0 || minute > 59) {
                return -1;
            }

            return hour * 60 + minute;

        } catch (Exception e) {

            return -1;
        }
    }

    private void saveSession(
            String subjectName,
            int start,
            int end,
            String room,
            int color
    ) {

        executor.execute(() -> {

            Subject subject =
                    database.subjectDao().findByName(subjectName);

            long subjectId;

            if (subject == null) {

                subjectId = database.subjectDao().insert(
                        new Subject(subjectName, color)
                );

            } else {

                subjectId = subject.id;
            }

            ClassSession session = new ClassSession(
                    subjectId,
                    selectedDay,
                    start,
                    end,
                    room,
                    true
            );

            database.classSessionDao().insert(session);

            loadSessions();
        });
    }

    private void showSessionOptions(
            SessionAdapter.SessionItem item
    ) {

        String[] options = {
                "Xóa buổi học"
        };

        new AlertDialog.Builder(context)
                .setTitle(item.subject.name)
                .setItems(options, (dialog, which) -> {

                    if (which == 0) {

                        executor.execute(() -> {

                            database.classSessionDao().delete(
                                    item.session
                            );

                            loadSessions();
                        });
                    }
                })
                .setNegativeButton("Đóng", null)
                .show();
    }
}
