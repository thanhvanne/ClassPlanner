
package com.example.classplanner.data.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "class_sessions",
        foreignKeys = {
                @ForeignKey(
                        entity = Subject.class,
                        parentColumns = "id",
                        childColumns = "subjectId",
                        onDelete = ForeignKey.RESTRICT
                )
        },
        indices = {@Index("subjectId")}
)
public class ClassSession {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long subjectId;

    // 1 = Thứ hai ... 7 = Chủ nhật
    public int dayOfWeek;

    // Số phút tính từ 00:00
    // Ví dụ 07:30 = 450
    public int startMinute;

    public int endMinute;

    public String room;

    public boolean alarmEnabled;

    public ClassSession(
            long subjectId,
            int dayOfWeek,
            int startMinute,
            int endMinute,
            String room,
            boolean alarmEnabled
    ) {
        this.subjectId = subjectId;
        this.dayOfWeek = dayOfWeek;
        this.startMinute = startMinute;
        this.endMinute = endMinute;
        this.room = room;
        this.alarmEnabled = alarmEnabled;
    }
}
