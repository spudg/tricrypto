package com.spudg.tricrypto

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class CashHandler(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    companion object {

        private const val DATABASE_VERSION = 6
        private const val DATABASE_NAME = "triCryptoCash.db"
        private const val TABLE_CASH = "cash"

        private const val KEY_AMOUNT = "amount"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createCashTable =
            ("CREATE TABLE $TABLE_CASH($KEY_AMOUNT TEXT)")
        db?.execSQL(createCashTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_CASH")
        onCreate(db)
    }

    fun getCashBal(): String {
        var cash = "-1"
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_CASH", null)

        if (cursor.moveToFirst()) {
            do {
                cash = cursor.getString(cursor.getColumnIndex(KEY_AMOUNT))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return cash

    }

    fun setInitial() {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_AMOUNT, "500000")
        db.insert(TABLE_CASH, null, values)

    }

    fun addCash(amount: String) {
        val db = this.writableDatabase
        val currentBalance = this.getCashBal().toFloat()
        val values = ContentValues()
        values.put(KEY_AMOUNT, (currentBalance + amount.toFloat()).toString())
        db.update(TABLE_CASH, values, "$KEY_AMOUNT=amount", null)
    }

    fun takeCash(amount: String) {
        val db = this.writableDatabase
        val currentBalance = this.getCashBal().toFloat()
        val values = ContentValues()
        values.put(KEY_AMOUNT, (currentBalance - amount.toFloat()).toString())
        db.update(TABLE_CASH, values, "$KEY_AMOUNT=amount", null)
    }


}
