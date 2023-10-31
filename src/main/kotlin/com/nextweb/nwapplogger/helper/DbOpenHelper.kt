package com.nextweb.nwapplogger.helper

import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.nextweb.nwapplogger.utils.CalendarUtil
import com.nextweb.nwapplogger.utils.Utils

/**
* @author nextweb
*
*/
class DbOpenHelper {

    private var mDB: SQLiteDatabase? = null
    private var mDBHelper: DatabaseHelper? = null

    companion object {
        val instance = DbOpenHelper()
    }

    private class DatabaseHelper  // 생성자
        (mCtx: Context?, name: String?, factory: CursorFactory?, version: Int) :
        SQLiteOpenHelper(mCtx, name, factory, version) {

        companion object {
            const val DATABASE_NAME = "appEvent_NWLogger.db"
            const val TABLE_NAME = "tb_usage_logger"
            const val DATABASE_VERSION = 2
        }

        // 최초 DB를 만들때 한번만 호출된다.
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL("create table $TABLE_NAME (id integer primary key autoincrement, url_str text, update_dt text);")
        }

        // 버전이 업데이트 되었을 경우 DB를 다시 만들어 준다.
        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("drop table if exists $TABLE_NAME")
            onCreate(db)
        }
    }

    @Throws(SQLException::class)
    fun open(mCtx: Context?): DbOpenHelper? {
        try {
            mDBHelper =
                DatabaseHelper(mCtx, DatabaseHelper.DATABASE_NAME, null, DatabaseHelper.DATABASE_VERSION)
            mDB = mDBHelper!!.writableDatabase
            return this
        } catch (e: SQLiteException) {
            // e.printStackTrace();
            if (Utils.isDebug) Log.e("DbOpenHelper", "Can't get writable Database")
        }
        return null
    }

    fun close() {
        if (mDB != null) mDB!!.close()
    }

    fun insert(urlStr: String) {
        val qry =
            "insert into $DatabaseHelper.TABLE_NAME ($urlStr, update_dt) values('$urlStr','${CalendarUtil.instance.currentTime()}');"
        if (Utils.isDebug) Log.i("insert", qry)
        mDB!!.execSQL(qry)
    }

    fun selectAll(): Cursor? {
        val qry = "select id, url_str, update_dt from $DatabaseHelper.TABLE_NAME;"
        if (Utils.isDebug) Log.i("selectAll", qry)
        return mDB!!.rawQuery(qry, null)
    }

    fun delete(id: Int) {
        val qry = "delete from $DatabaseHelper.TABLE_NAME where id=$id ;"
        if (Utils.isDebug) Log.i("delete", qry)
        mDB!!.execSQL(qry)
    }
}