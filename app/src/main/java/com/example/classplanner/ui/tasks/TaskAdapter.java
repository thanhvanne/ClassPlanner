
package com.example.classplanner.ui.tasks;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classplanner.R;
import com.example.classplanner.data.entity.Subject;
import com.example.classplanner.data.entity.Task;
import com.example.classplanner.util.DateTimeUtils;

import java.util.List;

public class TaskAdapter
        extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    public static class TaskItem {

        public Task task;
        public Subject subject;

        public TaskItem(Task task, Subject subject) {
            this.task = task;
            this.subject = subject;
        }
    }

    public interface OnTaskUpdateListener {
        void onUpdate(Task task, boolean completed);
    }

    public interface OnMoreClickListener {
        void onMoreClick(Task task);
    }

    private List<TaskItem> items;

    private final OnTaskUpdateListener updateListener;

    private final OnMoreClickListener moreListener;

    public TaskAdapter(
            List<TaskItem> items,
            OnTaskUpdateListener updateListener,
            OnMoreClickListener moreListener
    ) {

        this.items = items;
        this.updateListener = updateListener;
        this.moreListener = moreListener;
    }

    public void setItems(List<TaskItem> items) {

        this.items = items;

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_task,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        TaskItem item = items.get(position);

        Task task = item.task;

        holder.txtTitle.setText(task.title);

        holder.txtSubject.setText(
                item.subject == null
                        ? "Không thuộc môn học"
                        : item.subject.name
        );

        holder.txtDeadline.setText(
                DateTimeUtils.formatDateTime(
                        task.deadlineMillis
                )
        );

        holder.txtCountdown.setText(
                DateTimeUtils.getCountdown(
                        task.deadlineMillis
                )
        );

        holder.checkCompleted.setOnCheckedChangeListener(null);

        holder.checkCompleted.setChecked(task.isCompleted);

        if (task.isCompleted) {

            holder.txtTitle.setPaintFlags(
                    holder.txtTitle.getPaintFlags()
                            | Paint.STRIKE_THRU_TEXT_FLAG
            );

            holder.txtCountdown.setText("Đã hoàn thành");

            holder.txtCountdown.setTextColor(Color.GRAY);

        } else {

            holder.txtTitle.setPaintFlags(
                    holder.txtTitle.getPaintFlags()
                            & (~Paint.STRIKE_THRU_TEXT_FLAG)
            );

            holder.txtCountdown.setTextColor(
                    task.deadlineMillis < System.currentTimeMillis()
                            ? Color.RED
                            : Color.rgb(217, 119, 6)
            );
        }

        holder.checkCompleted.setOnCheckedChangeListener(
                (buttonView, checked) -> {

                    if (updateListener != null) {
                        updateListener.onUpdate(task, checked);
                    }
                }
        );

        holder.btnMore.setOnClickListener(v -> {

            if (moreListener != null) {
                moreListener.onMoreClick(task);
            }
        });
    }

    @Override
    public int getItemCount() {

        return items == null ? 0 : items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox checkCompleted;

        TextView txtTitle;
        TextView txtSubject;
        TextView txtDeadline;
        TextView txtCountdown;

        ImageButton btnMore;

        ViewHolder(@NonNull View itemView) {

            super(itemView);

            checkCompleted =
                    itemView.findViewById(R.id.checkTaskCompleted);

            txtTitle =
                    itemView.findViewById(R.id.txtTaskTitle);

            txtSubject =
                    itemView.findViewById(R.id.txtTaskSubject);

            txtDeadline =
                    itemView.findViewById(R.id.txtTaskDeadline);

            txtCountdown =
                    itemView.findViewById(R.id.txtTaskCountdown);

            btnMore =
                    itemView.findViewById(R.id.btnTaskMore);
        }
    }
}
