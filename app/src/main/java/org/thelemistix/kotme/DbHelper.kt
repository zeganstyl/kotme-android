package org.thelemistix.kotme

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

import android.content.res.AssetManager
import java.nio.charset.Charset

class DbHelper(val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        val am: AssetManager = context.assets
        val stream = am.open("db/kotme.sql")
        val s = stream.readBytes().toString(Charset.defaultCharset())
        db.execSQL(CREATE_TABLE)
        db.execSQL(s)
        println("dfkjsldkjfslkdjflkjsdfkj")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "kotme"

        const val DROP_TABLE = "DROP TABLE IF EXISTS task"

        const val CREATE_TABLE = """
CREATE TABLE IF NOT EXISTS task (
  id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  text TEXT,
  task TEXT
);
"""
    }
}
