package com.example.myapplication.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.myapplication.model.Transaction

class DBHandler(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "contas"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "transactions"
        private const val KEY_ID = "id"
        private const val KEY_CRED_DEB = "cre_deb"
        private const val KEY_DESCRICAO = "descricao"
        private const val KEY_VALOR = "valor"
        private const val KEY_DATA = "data"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(createTableQuery())
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }


    fun limparTable() {
        writableDatabase.use { db ->
            db.execSQL("DELETE FROM $TABLE_NAME")
        }
    }

    fun adicionarTransacao(transacao: Transaction): Long {
        return writableDatabase.use { db ->
            val values = ContentValues().apply {
                put(KEY_CRED_DEB, transacao.cre_deb)
                put(KEY_DESCRICAO, transacao.descricao)
                put(KEY_VALOR, transacao.valor)
                put(KEY_DATA, transacao.data)
            }
            db.insert(TABLE_NAME, null, values)
        }
    }

    private fun createTableQuery(): String {
        return """
            CREATE TABLE $TABLE_NAME (
                $KEY_ID INTEGER PRIMARY KEY,
                $KEY_CRED_DEB TEXT,
                $KEY_DESCRICAO TEXT,
                $KEY_VALOR REAL,
                $KEY_DATA TEXT
            )
        """.trimIndent()
    }


    fun buscarTransacoes(): List<Transaction> {
        val transactions = mutableListOf<Transaction>()

        readableDatabase.use { db ->
            db.query(TABLE_NAME, null, null, null, null, null, null).use { cursor ->
                while (cursor.moveToNext()) {
                    transactions.add(cursorParaTransacao(cursor))
                }
            }
        }
        return transactions
    }

    fun calculaSaldo(): Double {
        val transactions = buscarTransacoes()

        val totalCredito = transactions.filter { it.cre_deb == "Crédito" }.sumOf { it.valor }
        val totalDebito = transactions.filter { it.cre_deb == "Débito" }.sumOf { it.valor }

        return totalCredito - totalDebito
    }
    private fun cursorParaTransacao(cursor: Cursor): Transaction {
        return Transaction(
            id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
            cre_deb = cursor.getString(cursor.getColumnIndexOrThrow(KEY_CRED_DEB)),
            descricao = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRICAO)),
            valor = cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_VALOR)),
            data = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATA))
        )
    }
}
