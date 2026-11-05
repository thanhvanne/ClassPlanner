
package com.example.classplanner.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.classplanner.data.entity.Subject;

import java.util.List;

@Dao
public interface SubjectDao {

    @Insert
    long insert(Subject subject);

    @Update
    void update(Subject subject);

    @Delete
    void delete(Subject subject);

    @Query("SELECT * FROM subjects ORDER BY name ASC")
    List<Subject> getAll();

    @Query("SELECT * FROM subjects WHERE id = :id LIMIT 1")
    Subject getById(long id);

    @Query(
            "SELECT * FROM subjects " +
                    "WHERE LOWER(TRIM(name)) = LOWER(TRIM(:name)) LIMIT 1"
    )
    Subject findByName(String name);
}
