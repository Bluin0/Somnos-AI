package com.example.`data`

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import kotlin.Boolean
import kotlin.Float
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class SleepDao_Impl(
  __db: RoomDatabase,
) : SleepDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfSleepEntry: EntityInsertAdapter<SleepEntry>
  init {
    this.__db = __db
    this.__insertAdapterOfSleepEntry = object : EntityInsertAdapter<SleepEntry>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `sleep_entries` (`id`,`dateString`,`bedtime`,`wakeupTime`,`sleepDurationHours`,`sleepDurationMinutes`,`awakeHours`,`awakeMinutes`,`remHours`,`remMinutes`,`essentialHours`,`essentialMinutes`,`deepHours`,`deepMinutes`,`awakePercentage`,`remPercentage`,`essentialPercentage`,`deepPercentage`,`medicationLogged`,`notes`,`sleepScore`,`guessedScore`,`leaderboardOptIn`,`leaderboardNickname`,`timestamp`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: SleepEntry) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindText(2, entity.dateString)
        statement.bindText(3, entity.bedtime)
        statement.bindText(4, entity.wakeupTime)
        statement.bindLong(5, entity.sleepDurationHours.toLong())
        statement.bindLong(6, entity.sleepDurationMinutes.toLong())
        statement.bindLong(7, entity.awakeHours.toLong())
        statement.bindLong(8, entity.awakeMinutes.toLong())
        statement.bindLong(9, entity.remHours.toLong())
        statement.bindLong(10, entity.remMinutes.toLong())
        statement.bindLong(11, entity.essentialHours.toLong())
        statement.bindLong(12, entity.essentialMinutes.toLong())
        statement.bindLong(13, entity.deepHours.toLong())
        statement.bindLong(14, entity.deepMinutes.toLong())
        statement.bindDouble(15, entity.awakePercentage.toDouble())
        statement.bindDouble(16, entity.remPercentage.toDouble())
        statement.bindDouble(17, entity.essentialPercentage.toDouble())
        statement.bindDouble(18, entity.deepPercentage.toDouble())
        val _tmp: Int = if (entity.medicationLogged) 1 else 0
        statement.bindLong(19, _tmp.toLong())
        statement.bindText(20, entity.notes)
        statement.bindLong(21, entity.sleepScore.toLong())
        val _tmpGuessedScore: Int? = entity.guessedScore
        if (_tmpGuessedScore == null) {
          statement.bindNull(22)
        } else {
          statement.bindLong(22, _tmpGuessedScore.toLong())
        }
        val _tmp_1: Int = if (entity.leaderboardOptIn) 1 else 0
        statement.bindLong(23, _tmp_1.toLong())
        statement.bindText(24, entity.leaderboardNickname)
        statement.bindLong(25, entity.timestamp)
      }
    }
  }

  public override suspend fun insertEntry(entry: SleepEntry): Long = performSuspending(__db, false,
      true) { _connection ->
    val _result: Long = __insertAdapterOfSleepEntry.insertAndReturnId(_connection, entry)
    _result
  }

  public override fun getAllEntries(): Flow<List<SleepEntry>> {
    val _sql: String = "SELECT * FROM sleep_entries ORDER BY dateString DESC"
    return createFlow(__db, false, arrayOf("sleep_entries")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfDateString: Int = getColumnIndexOrThrow(_stmt, "dateString")
        val _cursorIndexOfBedtime: Int = getColumnIndexOrThrow(_stmt, "bedtime")
        val _cursorIndexOfWakeupTime: Int = getColumnIndexOrThrow(_stmt, "wakeupTime")
        val _cursorIndexOfSleepDurationHours: Int = getColumnIndexOrThrow(_stmt,
            "sleepDurationHours")
        val _cursorIndexOfSleepDurationMinutes: Int = getColumnIndexOrThrow(_stmt,
            "sleepDurationMinutes")
        val _cursorIndexOfAwakeHours: Int = getColumnIndexOrThrow(_stmt, "awakeHours")
        val _cursorIndexOfAwakeMinutes: Int = getColumnIndexOrThrow(_stmt, "awakeMinutes")
        val _cursorIndexOfRemHours: Int = getColumnIndexOrThrow(_stmt, "remHours")
        val _cursorIndexOfRemMinutes: Int = getColumnIndexOrThrow(_stmt, "remMinutes")
        val _cursorIndexOfEssentialHours: Int = getColumnIndexOrThrow(_stmt, "essentialHours")
        val _cursorIndexOfEssentialMinutes: Int = getColumnIndexOrThrow(_stmt, "essentialMinutes")
        val _cursorIndexOfDeepHours: Int = getColumnIndexOrThrow(_stmt, "deepHours")
        val _cursorIndexOfDeepMinutes: Int = getColumnIndexOrThrow(_stmt, "deepMinutes")
        val _cursorIndexOfAwakePercentage: Int = getColumnIndexOrThrow(_stmt, "awakePercentage")
        val _cursorIndexOfRemPercentage: Int = getColumnIndexOrThrow(_stmt, "remPercentage")
        val _cursorIndexOfEssentialPercentage: Int = getColumnIndexOrThrow(_stmt,
            "essentialPercentage")
        val _cursorIndexOfDeepPercentage: Int = getColumnIndexOrThrow(_stmt, "deepPercentage")
        val _cursorIndexOfMedicationLogged: Int = getColumnIndexOrThrow(_stmt, "medicationLogged")
        val _cursorIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _cursorIndexOfSleepScore: Int = getColumnIndexOrThrow(_stmt, "sleepScore")
        val _cursorIndexOfGuessedScore: Int = getColumnIndexOrThrow(_stmt, "guessedScore")
        val _cursorIndexOfLeaderboardOptIn: Int = getColumnIndexOrThrow(_stmt, "leaderboardOptIn")
        val _cursorIndexOfLeaderboardNickname: Int = getColumnIndexOrThrow(_stmt,
            "leaderboardNickname")
        val _cursorIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _result: MutableList<SleepEntry> = mutableListOf()
        while (_stmt.step()) {
          val _item: SleepEntry
          val _tmpId: Int
          _tmpId = _stmt.getLong(_cursorIndexOfId).toInt()
          val _tmpDateString: String
          _tmpDateString = _stmt.getText(_cursorIndexOfDateString)
          val _tmpBedtime: String
          _tmpBedtime = _stmt.getText(_cursorIndexOfBedtime)
          val _tmpWakeupTime: String
          _tmpWakeupTime = _stmt.getText(_cursorIndexOfWakeupTime)
          val _tmpSleepDurationHours: Int
          _tmpSleepDurationHours = _stmt.getLong(_cursorIndexOfSleepDurationHours).toInt()
          val _tmpSleepDurationMinutes: Int
          _tmpSleepDurationMinutes = _stmt.getLong(_cursorIndexOfSleepDurationMinutes).toInt()
          val _tmpAwakeHours: Int
          _tmpAwakeHours = _stmt.getLong(_cursorIndexOfAwakeHours).toInt()
          val _tmpAwakeMinutes: Int
          _tmpAwakeMinutes = _stmt.getLong(_cursorIndexOfAwakeMinutes).toInt()
          val _tmpRemHours: Int
          _tmpRemHours = _stmt.getLong(_cursorIndexOfRemHours).toInt()
          val _tmpRemMinutes: Int
          _tmpRemMinutes = _stmt.getLong(_cursorIndexOfRemMinutes).toInt()
          val _tmpEssentialHours: Int
          _tmpEssentialHours = _stmt.getLong(_cursorIndexOfEssentialHours).toInt()
          val _tmpEssentialMinutes: Int
          _tmpEssentialMinutes = _stmt.getLong(_cursorIndexOfEssentialMinutes).toInt()
          val _tmpDeepHours: Int
          _tmpDeepHours = _stmt.getLong(_cursorIndexOfDeepHours).toInt()
          val _tmpDeepMinutes: Int
          _tmpDeepMinutes = _stmt.getLong(_cursorIndexOfDeepMinutes).toInt()
          val _tmpAwakePercentage: Float
          _tmpAwakePercentage = _stmt.getDouble(_cursorIndexOfAwakePercentage).toFloat()
          val _tmpRemPercentage: Float
          _tmpRemPercentage = _stmt.getDouble(_cursorIndexOfRemPercentage).toFloat()
          val _tmpEssentialPercentage: Float
          _tmpEssentialPercentage = _stmt.getDouble(_cursorIndexOfEssentialPercentage).toFloat()
          val _tmpDeepPercentage: Float
          _tmpDeepPercentage = _stmt.getDouble(_cursorIndexOfDeepPercentage).toFloat()
          val _tmpMedicationLogged: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_cursorIndexOfMedicationLogged).toInt()
          _tmpMedicationLogged = _tmp != 0
          val _tmpNotes: String
          _tmpNotes = _stmt.getText(_cursorIndexOfNotes)
          val _tmpSleepScore: Int
          _tmpSleepScore = _stmt.getLong(_cursorIndexOfSleepScore).toInt()
          val _tmpGuessedScore: Int?
          if (_stmt.isNull(_cursorIndexOfGuessedScore)) {
            _tmpGuessedScore = null
          } else {
            _tmpGuessedScore = _stmt.getLong(_cursorIndexOfGuessedScore).toInt()
          }
          val _tmpLeaderboardOptIn: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_cursorIndexOfLeaderboardOptIn).toInt()
          _tmpLeaderboardOptIn = _tmp_1 != 0
          val _tmpLeaderboardNickname: String
          _tmpLeaderboardNickname = _stmt.getText(_cursorIndexOfLeaderboardNickname)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_cursorIndexOfTimestamp)
          _item =
              SleepEntry(_tmpId,_tmpDateString,_tmpBedtime,_tmpWakeupTime,_tmpSleepDurationHours,_tmpSleepDurationMinutes,_tmpAwakeHours,_tmpAwakeMinutes,_tmpRemHours,_tmpRemMinutes,_tmpEssentialHours,_tmpEssentialMinutes,_tmpDeepHours,_tmpDeepMinutes,_tmpAwakePercentage,_tmpRemPercentage,_tmpEssentialPercentage,_tmpDeepPercentage,_tmpMedicationLogged,_tmpNotes,_tmpSleepScore,_tmpGuessedScore,_tmpLeaderboardOptIn,_tmpLeaderboardNickname,_tmpTimestamp)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteEntryById(id: Int) {
    val _sql: String = "DELETE FROM sleep_entries WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id.toLong())
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteOldRecords(cutoffTimestamp: Long) {
    val _sql: String = "DELETE FROM sleep_entries WHERE timestamp < ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, cutoffTimestamp)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
