package com.spudg.tricrypto

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class HoldingHandler(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    companion object {

        private const val DATABASE_VERSION = 3
        private const val DATABASE_NAME = "triCryptoHoldings.db"
        private const val TABLE_HOLDINGS = "holdings"

        private const val KEY_SYMBOL = "symbol"
        private const val KEY_COST = "cost"
        private const val KEY_AMOUNT = "amount"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createHoldingsTable =
            ("CREATE TABLE $TABLE_HOLDINGS($KEY_SYMBOL TEXT,$KEY_COST TEXT,$KEY_AMOUNT TEXT)")
        db?.execSQL(createHoldingsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_HOLDINGS")
        onCreate(db)
    }

    fun buy(holding: HoldingModel) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_SYMBOL, holding.symbol)
        values.put(KEY_COST, holding.cost)
        values.put(KEY_AMOUNT, holding.amount)
        try {
            db.update(TABLE_HOLDINGS, values, "$KEY_SYMBOL=${holding.symbol}", null)
        } catch (e: Exception){
            db.insert(TABLE_HOLDINGS, null, values)
        }
        db.close()
    }

    fun sell(holding: HoldingModel) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_SYMBOL, holding.symbol)
        values.put(KEY_COST, holding.cost)
        values.put(KEY_AMOUNT, holding.amount)
        try {
            db.update(TABLE_HOLDINGS, values, "$KEY_SYMBOL=${holding.symbol}", null)
        } catch (e: Exception){
            db.insert(TABLE_HOLDINGS, null, values)
        }
        db.close()
    }

    fun getCost(symbolInput: String): String {
        var cost = "0"
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_HOLDINGS", null)

        if (cursor.moveToFirst()) {
            do {
                val symbol = cursor.getString(cursor.getColumnIndex(KEY_SYMBOL))
                if (symbol == symbolInput) {
                    cost = cursor.getString(cursor.getColumnIndex(KEY_COST))
                }
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return cost

    }

    fun getAmount(symbolInput: String): String {
        var amount = "0"
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_HOLDINGS", null)

        if (cursor.moveToFirst()) {
            do {
                val symbol = cursor.getString(cursor.getColumnIndex(KEY_SYMBOL))
                if (symbol == symbolInput) {
                    amount = cursor.getString(cursor.getColumnIndex(KEY_AMOUNT))
                }
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return amount

    }


}
