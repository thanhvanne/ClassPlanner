
package com.example.classplanner.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.classplanner.data.entity.ClassSession;

import java.util.List;

@Dao
public interface ClassSessionDao {

    @Insert
    long insert(ClassSession session);

    @Update
    void update(ClassSession session);

    @Delete
    void delete(ClassSession session);

    @Query(
            "SELECT * FROM class_sessions " +
                    "WHERE dayOfWeek = :day " +
                    "ORDER BY startMinute ASC"
    )
    List<ClassSession> getByDay(int day);

    @Query(
            "SELECT * FROM class_sessions " +
                    "ORDER BY dayOfWeek ASC, startMinute ASC"
    )
    List<ClassSession> getAll();

    @Query(
            "SELECT * FROM class_sessions WHERE id = :id LIMIT 1"
    )
    ClassSession getById(long id);

    @Query(
            "SELECT * FROM class_sessions WHERE subjectId = :subjectId"
    )
    List<ClassSession> getBySubject(long subjectId);
}
