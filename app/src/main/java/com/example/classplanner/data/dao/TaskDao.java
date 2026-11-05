
package com.example.classplanner.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.classplanner.data.entity.Task;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert
    long insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Query(
            "SELECT * FROM tasks " +
                    "ORDER BY isCompleted ASC, deadlineMillis ASC"
    )
    List<Task> getAll();

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    Task getById(long id);

    @Query(
            "SELECT * FROM tasks " +
                    "WHERE isCompleted = 0 " +
                    "ORDER BY deadlineMillis ASC"
    )
    List<Task> getIncomplete();

    @Query(
            "SELECT * FROM tasks WHERE subjectId = :subjectId"
    )
    List<Task> getBySubject(long subjectId);
}
