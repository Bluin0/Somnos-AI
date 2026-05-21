package com.example.`data`

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class ChatDao_Impl(
  __db: RoomDatabase,
) : ChatDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfChatMessage: EntityInsertAdapter<ChatMessage>
  init {
    this.__db = __db
    this.__insertAdapterOfChatMessage = object : EntityInsertAdapter<ChatMessage>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `chat_messages` (`id`,`sender`,`text`,`timestamp`) VALUES (nullif(?, 0),?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ChatMessage) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindText(2, entity.sender)
        statement.bindText(3, entity.text)
        statement.bindLong(4, entity.timestamp)
      }
    }
  }

  public override suspend fun insertMessage(message: ChatMessage): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfChatMessage.insert(_connection, message)
  }

  public override fun getAllMessagesFlow(): Flow<List<ChatMessage>> {
    val _sql: String = "SELECT * FROM chat_messages ORDER BY timestamp ASC"
    return createFlow(__db, false, arrayOf("chat_messages")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfSender: Int = getColumnIndexOrThrow(_stmt, "sender")
        val _cursorIndexOfText: Int = getColumnIndexOrThrow(_stmt, "text")
        val _cursorIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _result: MutableList<ChatMessage> = mutableListOf()
        while (_stmt.step()) {
          val _item: ChatMessage
          val _tmpId: Int
          _tmpId = _stmt.getLong(_cursorIndexOfId).toInt()
          val _tmpSender: String
          _tmpSender = _stmt.getText(_cursorIndexOfSender)
          val _tmpText: String
          _tmpText = _stmt.getText(_cursorIndexOfText)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_cursorIndexOfTimestamp)
          _item = ChatMessage(_tmpId,_tmpSender,_tmpText,_tmpTimestamp)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllMessages(): List<ChatMessage> {
    val _sql: String = "SELECT * FROM chat_messages ORDER BY timestamp ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfSender: Int = getColumnIndexOrThrow(_stmt, "sender")
        val _cursorIndexOfText: Int = getColumnIndexOrThrow(_stmt, "text")
        val _cursorIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _result: MutableList<ChatMessage> = mutableListOf()
        while (_stmt.step()) {
          val _item: ChatMessage
          val _tmpId: Int
          _tmpId = _stmt.getLong(_cursorIndexOfId).toInt()
          val _tmpSender: String
          _tmpSender = _stmt.getText(_cursorIndexOfSender)
          val _tmpText: String
          _tmpText = _stmt.getText(_cursorIndexOfText)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_cursorIndexOfTimestamp)
          _item = ChatMessage(_tmpId,_tmpSender,_tmpText,_tmpTimestamp)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearChat() {
    val _sql: String = "DELETE FROM chat_messages"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
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
