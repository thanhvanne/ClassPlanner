
package com.example.classplanner.data.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "tasks",
        foreignKeys = {
                @ForeignKey(
                        entity = Subject.class,
                        parentColumns = "id",
                        childColumns = "subjectId",
                        onDelete = ForeignKey.SET_NULL
                )
        },
        indices = {@Index("subjectId")}
)
public class Task {

    @PrimaryKey(autoGenerate = true)
    public long id;

    // Có thể null nếu nhiệm vụ không thuộc môn cụ thể
    public Long subjectId;

    public String title;

    // Deadline tính bằng milliseconds
    public long deadlineMillis;

    public boolean isCompleted;

    public Task(
            Long subjectId,
            String title,
            long deadlineMillis
    ) {
        this.subjectId = subjectId;
        this.title = title;
        this.deadlineMillis = deadlineMillis;
        this.isCompleted = false;
    }
}
