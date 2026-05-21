package com.example.`data`

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class SleepDatabase_Impl : SleepDatabase() {
  private val _sleepDao: Lazy<SleepDao> = lazy {
    SleepDao_Impl(this)
  }


  private val _chatDao: Lazy<ChatDao> = lazy {
    ChatDao_Impl(this)
  }


  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(2,
        "d6a2b16895a9b89cf2f08611a3bfad47", "f5ed8e206973f0bb6e9a246ed19337aa") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `sleep_entries` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `dateString` TEXT NOT NULL, `bedtime` TEXT NOT NULL, `wakeupTime` TEXT NOT NULL, `sleepDurationHours` INTEGER NOT NULL, `sleepDurationMinutes` INTEGER NOT NULL, `awakeHours` INTEGER NOT NULL, `awakeMinutes` INTEGER NOT NULL, `remHours` INTEGER NOT NULL, `remMinutes` INTEGER NOT NULL, `essentialHours` INTEGER NOT NULL, `essentialMinutes` INTEGER NOT NULL, `deepHours` INTEGER NOT NULL, `deepMinutes` INTEGER NOT NULL, `awakePercentage` REAL NOT NULL, `remPercentage` REAL NOT NULL, `essentialPercentage` REAL NOT NULL, `deepPercentage` REAL NOT NULL, `medicationLogged` INTEGER NOT NULL, `notes` TEXT NOT NULL, `sleepScore` INTEGER NOT NULL, `guessedScore` INTEGER, `leaderboardOptIn` INTEGER NOT NULL, `leaderboardNickname` TEXT NOT NULL, `timestamp` INTEGER NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `chat_messages` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `sender` TEXT NOT NULL, `text` TEXT NOT NULL, `timestamp` INTEGER NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'd6a2b16895a9b89cf2f08611a3bfad47')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `sleep_entries`")
        connection.execSQL("DROP TABLE IF EXISTS `chat_messages`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsSleepEntries: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsSleepEntries.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSleepEntries.put("dateString", TableInfo.Column("dateString", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSleepEntries.put("bedtime", TableInfo.Column("bedtime", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSleepEntries.put("wakeupTime", TableInfo.Column("wakeupTime", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSleepEntries.put("sleepDurationHours", TableInfo.Column("sleepDurationHours",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSleepEntries.put("sleepDurationMinutes", TableInfo.Column("sleepDurationMinutes",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSleepEntries.put("awakeHours", TableInfo.Column("awakeHours", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSleepEntries.put("awakeMinutes", TableInfo.Column("awakeMinutes", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSleepEntries.put("remHours", TableInfo.Column("remHours", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSleepEntries.put("remMinutes", TableInfo.Column("remMinutes", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSleepEntries.put("essentialHours", TableInfo.Column("essentialHours", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSleepEntries.put("essentialMinutes", TableInfo.Column("essentialMinutes", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSleepEntries.put("deepHours", TableInfo.Column("deepHours", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSleepEntries.put("deepMinutes", TableInfo.Column("deepMinutes", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSleepEntries.put("awakePercentage", TableInfo.Column("awakePercentage", "REAL",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSleepEntries.put("remPercentage", TableInfo.Column("remPercentage", "REAL", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSleepEntries.put("essentialPercentage", TableInfo.Column("essentialPercentage",
            "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSleepEntries.put("deepPercentage", TableInfo.Column("deepPercentage", "REAL", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSleepEntries.put("medicationLogged", TableInfo.Column("medicationLogged", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSleepEntries.put("notes", TableInfo.Column("notes", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSleepEntries.put("sleepScore", TableInfo.Column("sleepScore", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSleepEntries.put("guessedScore", TableInfo.Column("guessedScore", "INTEGER", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSleepEntries.put("leaderboardOptIn", TableInfo.Column("leaderboardOptIn", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSleepEntries.put("leaderboardNickname", TableInfo.Column("leaderboardNickname",
            "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSleepEntries.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysSleepEntries: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesSleepEntries: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoSleepEntries: TableInfo = TableInfo("sleep_entries", _columnsSleepEntries,
            _foreignKeysSleepEntries, _indicesSleepEntries)
        val _existingSleepEntries: TableInfo = read(connection, "sleep_entries")
        if (!_infoSleepEntries.equals(_existingSleepEntries)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |sleep_entries(com.example.data.SleepEntry).
              | Expected:
              |""".trimMargin() + _infoSleepEntries + """
              |
              | Found:
              |""".trimMargin() + _existingSleepEntries)
        }
        val _columnsChatMessages: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsChatMessages.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChatMessages.put("sender", TableInfo.Column("sender", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChatMessages.put("text", TableInfo.Column("text", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChatMessages.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysChatMessages: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesChatMessages: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoChatMessages: TableInfo = TableInfo("chat_messages", _columnsChatMessages,
            _foreignKeysChatMessages, _indicesChatMessages)
        val _existingChatMessages: TableInfo = read(connection, "chat_messages")
        if (!_infoChatMessages.equals(_existingChatMessages)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |chat_messages(com.example.data.ChatMessage).
              | Expected:
              |""".trimMargin() + _infoChatMessages + """
              |
              | Found:
              |""".trimMargin() + _existingChatMessages)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "sleep_entries",
        "chat_messages")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(SleepDao::class, SleepDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ChatDao::class, ChatDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override
      fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun sleepDao(): SleepDao = _sleepDao.value

  public override fun chatDao(): ChatDao = _chatDao.value
}
