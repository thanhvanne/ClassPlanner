
package com.example.classplanner.ui.timetable;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classplanner.R;
import com.example.classplanner.data.entity.ClassSession;
import com.example.classplanner.data.entity.Subject;
import com.example.classplanner.util.DateTimeUtils;

import java.util.List;

public class SessionAdapter
        extends RecyclerView.Adapter<SessionAdapter.ViewHolder> {

    public static class SessionItem {

        public ClassSession session;
        public Subject subject;

        public SessionItem(
                ClassSession session,
                Subject subject
        ) {
            this.session = session;
            this.subject = subject;
        }
    }

    public interface OnMoreClickListener {

        void onMoreClick(SessionItem item);
    }

    private List<SessionItem> items;

    private final OnMoreClickListener listener;

    public SessionAdapter(
            List<SessionItem> items,
            OnMoreClickListener listener
    ) {

        this.items = items;
        this.listener = listener;
    }

    public void setItems(List<SessionItem> items) {

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
                        R.layout.item_class_session,
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

        SessionItem item = items.get(position);

        holder.txtSubject.setText(item.subject.name);

        holder.txtTime.setText(
                DateTimeUtils.formatTime(
                        item.session.startMinute
                )
                        + " - "
                        + DateTimeUtils.formatTime(
                        item.session.endMinute
                )
        );

        holder.txtRoom.setText(
                item.session.room == null
                        || item.session.room.isEmpty()
                        ? "Chưa có phòng học"
                        : "Phòng " + item.session.room
        );

        holder.viewColor.setBackgroundColor(
                item.subject.colorInt
        );

        holder.txtDeadline.setText(
                "Xem nhiệm vụ trong mục Nhiệm vụ"
        );

        holder.btnMore.setOnClickListener(v -> {

            if (listener != null) {
                listener.onMoreClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {

        return items == null ? 0 : items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtSubject;
        TextView txtTime;
        TextView txtRoom;
        TextView txtDeadline;

        View viewColor;

        ImageButton btnMore;

        ViewHolder(@NonNull View itemView) {

            super(itemView);

            txtSubject =
                    itemView.findViewById(R.id.txtSubjectName);

            txtTime =
                    itemView.findViewById(R.id.txtSessionTime);

            txtRoom =
                    itemView.findViewById(R.id.txtRoom);

            txtDeadline =
                    itemView.findViewById(R.id.txtDeadlineStatus);

            viewColor =
                    itemView.findViewById(R.id.viewSubjectColor);

            btnMore =
                    itemView.findViewById(R.id.btnSessionMore);
        }
    }
}
