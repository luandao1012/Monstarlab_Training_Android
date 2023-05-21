package com.example.roomdatabase.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    @ColumnInfo(name = "date")
    val date: Long,
    @ColumnInfo(name = "content")
    var content: String,
    @ColumnInfo(name = "type")
    val type: Type,
    @ColumnInfo(name = "alarm_time")
    var alarmTime: Long? = null,
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean? = null
)

enum class Type {
    DIARY, REMINDER
}