package com.neelesh.todolist.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_items")
data class TodoItem(

    @PrimaryKey val id: String,

    @ColumnInfo(name = "task") val task: String,

    @ColumnInfo(name = "description") val description: String,

    @ColumnInfo(name = "content") val content: String = "",

    @ColumnInfo(name = "isCompleted") val isCompleted: Boolean,

    )
