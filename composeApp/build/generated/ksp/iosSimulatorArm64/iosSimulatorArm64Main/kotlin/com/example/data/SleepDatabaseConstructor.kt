package com.example.`data`

import androidx.room.RoomDatabaseConstructor

public actual object SleepDatabaseConstructor : RoomDatabaseConstructor<SleepDatabase> {
  actual override fun initialize(): SleepDatabase = com.example.`data`.SleepDatabase_Impl()
}
