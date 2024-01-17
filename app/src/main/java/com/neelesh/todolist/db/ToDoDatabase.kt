package com.neelesh.todolist.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.neelesh.todolist.models.TodoItem

@Database(entities = [TodoItem::class], version = 1)
abstract class ToDoDatabase : RoomDatabase() {

    abstract fun todoDao(): ToDoDao

}