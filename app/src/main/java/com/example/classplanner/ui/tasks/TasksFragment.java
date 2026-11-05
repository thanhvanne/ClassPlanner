
package com.example.classplanner.ui.tasks;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classplanner.R;
import com.example.classplanner.data.AppDatabase;
import com.example.classplanner.data.entity.Subject;
import com.example.classplanner.data.entity.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TasksFragment {

    private Context context;

    private AppDatabase database;

    private RecyclerView recyclerTasks;
    private Spinner spinnerFilter;

    private TaskAdapter adapter;

    private List<Subject> subjects = new ArrayList<>();

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    private long selectedSubjectId = -1;

    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup parent,
            android.os.Bundle savedInstanceState
    ) {

        context = parent.getContext();

        View view = inflater.inflate(
                R.layout.fragment_tasks,
                parent,
                false
        );

        database = AppDatabase.getInstance(context);

        recyclerTasks = view.findViewById(R.id.recyclerTasks);
        spinnerFilter = view.findViewById(R.id.spinnerTaskFilter);

        recyclerTasks.setLayoutManager(
                new LinearLayoutManager(context)
        );

        adapter = new TaskAdapter(
                new ArrayList<>(),
                this::updateTask,
                this::showTaskOptions
        );

        recyclerTasks.setAdapter(adapter);

        view.findViewById(R.id.btnAddTask)
                .setOnClickListener(v -> showAddTaskDialog());

        loadSubjects();

        return view;
    }

    private void loadSubjects() {

        executor.execute(() -> {

            subjects = database.subjectDao().getAll();

            if (context == null) return;

            recyclerTasks.post(() -> {

                List<String> names = new ArrayList<>();

                names.add("Tất cả môn học");

                for (Subject subject : subjects) {
                    names.add(subject.name);
                }

                ArrayAdapter<String> spinnerAdapter =
                        new ArrayAdapter<>(
                                context,
                                android.R.layout.simple_spinner_item,
                                names
                        );

                spinnerAdapter.setDropDownViewResource(
                        android.R.layout.simple_spinner_dropdown_item
                );

                spinnerFilter.setAdapter(spinnerAdapter);

                spinnerFilter.setOnItemSelectedListener(
                        new android.widget.AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(
                                    android.widget.AdapterView<?> parent,
                                    View view,
                                    int position,
                                    long id
                            ) {

                                if (position == 0) {
                                    selectedSubjectId = -1;
                                } else {
                                    selectedSubjectId =
                                            subjects.get(position - 1).id;
                                }

                                loadTasks();
                            }

                            @Override
                            public void onNothingSelected(
                                    android.widget.AdapterView<?> parent
                            ) {
                            }
                        }
                );
            });
        });
    }

    private void loadTasks() {

        executor.execute(() -> {

            List<Task> allTasks =
                    database.taskDao().getAll();

            List<TaskAdapter.TaskItem> items =
                    new ArrayList<>();

            for (Task task : allTasks) {

                if (selectedSubjectId != -1
                        && (task.subjectId == null
                        || task.subjectId != selectedSubjectId)) {
                    continue;
                }

                Subject subject = null;

                if (task.subjectId != null) {
                    subject = database.subjectDao()
                            .getById(task.subjectId);
                }

                items.add(
                        new TaskAdapter.TaskItem(task, subject)
                );
            }

            if (recyclerTasks != null) {

                recyclerTasks.post(() ->
                        adapter.setItems(items)
                );
            }
        });
    }

    private void showAddTaskDialog() {

        LinearLayoutBuilder builder =
                new LinearLayoutBuilder(context);

        EditText edtTitle = builder.addEditText(
                "Tên nhiệm vụ"
        );

        Spinner spinnerSubject = builder.addSpinner();

        Calendar calendar = Calendar.getInstance();

        final long[] deadline = {
                calendar.getTimeInMillis()
        };

        android.widget.Button btnDeadline =
                new android.widget.Button(context);

        btnDeadline.setText("Chọn ngày và giờ deadline");

        btnDeadline.setOnClickListener(v -> {

            Calendar current = Calendar.getInstance();

            new DatePickerDialog(
                    context,
                    (datePicker, year, month, day) -> {

                        Calendar chosen = Calendar.getInstance();

                        chosen.set(
                                year,
                                month,
                                day,
                                23,
                                59,
                                0
                        );

                        chosen.set(Calendar.MILLISECOND, 0);

                        new TimePickerDialog(
                                context,
                                (timePicker, hour, minute) -> {

                                    chosen.set(
                                            Calendar.HOUR_OF_DAY,
                                            hour
                                    );

                                    chosen.set(
                                            Calendar.MINUTE,
                                            minute
                                    );

                                    deadline[0] =
                                            chosen.getTimeInMillis();

                                    btnDeadline.setText(
                                            String.format(
                                                    "%02d/%02d/%04d %02d:%02d",
                                                    day,
                                                    month + 1,
                                                    year,
                                                    hour,
                                                    minute
                                            )
                                    );
                                },
                                23,
                                59,
                                true
                        ).show();
                    },
                    current.get(Calendar.YEAR),
                    current.get(Calendar.MONTH),
                    current.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        builder.layout.addView(btnDeadline);

        List<String> subjectNames = new ArrayList<>();

        subjectNames.add("Không thuộc môn học");

        for (Subject subject : subjects) {
            subjectNames.add(subject.name);
        }

        spinnerSubject.setAdapter(
                new ArrayAdapter<>(
                        context,
                        android.R.layout.simple_spinner_dropdown_item,
                        subjectNames
                )
        );

        new AlertDialog.Builder(context)
                .setTitle("Thêm nhiệm vụ")
                .setView(builder.layout)
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Lưu", (dialog, which) -> {

                    String title =
                            edtTitle.getText().toString().trim();

                    if (title.isEmpty()) {

                        Toast.makeText(
                                context,
                                "Hãy nhập tên nhiệm vụ",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    Long subjectId = null;

                    int position =
                            spinnerSubject.getSelectedItemPosition();

                    if (position > 0) {
                        subjectId = subjects.get(position - 1).id;
                    }

                    long finalSubjectId =
                            subjectId == null ? -1 : subjectId;

                    executor.execute(() -> {

                        Task task = new Task(
                                finalSubjectId == -1
                                        ? null
                                        : finalSubjectId,
                                title,
                                deadline[0]
                        );

                        database.taskDao().insert(task);

                        loadTasks();
                    });
                })
                .show();
    }

    private void updateTask(Task task, boolean completed) {

        task.isCompleted = completed;

        executor.execute(() -> {

            database.taskDao().update(task);

            loadTasks();
        });
    }

    private void showTaskOptions(Task task) {

        new AlertDialog.Builder(context)
                .setTitle(task.title)
                .setItems(
                        new String[]{"Xóa nhiệm vụ"},
                        (dialog, which) -> {

                            executor.execute(() -> {

                                database.taskDao().delete(task);

                                loadTasks();
                            });
                        }
                )
                .setNegativeButton("Đóng", null)
                .show();
    }

    private static class LinearLayoutBuilder {

        android.widget.LinearLayout layout;

        LinearLayoutBuilder(Context context) {

            layout = new android.widget.LinearLayout(context);

            layout.setOrientation(
                    android.widget.LinearLayout.VERTICAL
            );

            layout.setPadding(40, 10, 40, 10);
        }

        EditText addEditText(String hint) {

            EditText editText = new EditText(
                    layout.getContext()
            );

            editText.setHint(hint);

            layout.addView(editText);

            return editText;
        }

        Spinner addSpinner() {

            Spinner spinner = new Spinner(
                    layout.getContext()
            );

            layout.addView(spinner);

            return spinner;
        }
    }
}
