
package com.example.classplanner.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "subjects")
public class Subject {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String name;

    // Màu ARGB của môn học
    public int colorInt;

    public Subject(String name, int colorInt) {
        this.name = name;
        this.colorInt = colorInt;
    }
}
