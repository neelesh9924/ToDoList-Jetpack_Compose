package com.neelesh.todolist.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.neelesh.todolist.models.TodoItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ToDoDao {

    @Query("SELECT * FROM todo_items")
    fun getAll(): Flow<List<TodoItem>>

    @Insert
    fun insert(vararg todoItem: TodoItem)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(todoItem: TodoItem)

    @Delete
    fun delete(todoItem: TodoItem)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun updateAllFromFirebase(id: String): Flow<List<TodoItem>>


}