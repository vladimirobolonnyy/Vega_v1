package com.obolonnyy.vega_v1.util.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.obolonnyy.vega_v1.util.data.MyData
import com.obolonnyy.vega_v1.util.dataobjects.CustomSubjects
import com.obolonnyy.vega_v1.util.dataobjects.CustomSubjectsWithDate
import com.obolonnyy.vega_v1.util.dataobjects.Professors
import com.obolonnyy.vega_v1.util.dataobjects.Subjects
import org.jetbrains.anko.db.ManagedSQLiteOpenHelper
import org.jetbrains.anko.db.SqlType
import org.jetbrains.anko.db.TEXT
import org.jetbrains.anko.db.createTable

/**
 * Created by Владимир on 20.08.2017.
 */

class MyDatabaseOpenHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "MyDatabase", null, 1) {
    companion object {

        private var instance: MyDatabaseOpenHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): MyDatabaseOpenHelper {
            if (instance == null) {
                instance = MyDatabaseOpenHelper(ctx.getApplicationContext())
            }
            return instance!!
        }

        fun createPersonsTable(database: SQLiteDatabase){
            database.createTable(tableName = MyData.PROFESSORS_TABLE_NAME,
                    ifNotExists = true,
                    columns = *arrayOf(
                            "id" to SqlType.create("INTEGER PRIMARY KEY AUTOINCREMENT"),
                            "FIO" to TEXT,
                            "scienceDegree" to TEXT,
                            "email" to TEXT,
                            "phone" to TEXT,
                            "comment" to TEXT
                    )
            )
        }

        fun createSubjectsTable(database: SQLiteDatabase){
            database.createTable(tableName = MyData.SUBJECTS_TABLE_NAME,
                    ifNotExists = true,
                    columns = *arrayOf(
                            "id" to SqlType.create("INTEGER PRIMARY KEY AUTOINCREMENT"),
                            "time" to TEXT,
                            "dayOfWeek" to TEXT,
                            "chislOrZnamen" to TEXT,
                            "description" to TEXT
                    )
            )
        }

        fun createCustomSubjectsTable(database: SQLiteDatabase) {
            database.createTable(tableName = MyData.CUSTOM_SUBJECTS_TABLE_NAME,
                    ifNotExists = true,
                    columns = *arrayOf(
                            "id" to SqlType.create("INTEGER PRIMARY KEY AUTOINCREMENT"),
                            "time" to TEXT,
                            "description" to TEXT,
                            "stringDate" to TEXT
                    )
            )
        }
    }

    override fun onCreate(database: SQLiteDatabase) {
        // Here you create tables
        createPersonsTable(database)
        createSubjectsTable(database)
        createCustomSubjectsTable(database)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Here you can upgrade tables, as usual
    }

    fun saveProfessors(database: SQLiteDatabase, professors: ArrayList<Professors>){
        DataBaseManager.saveProfessors(database, professors)
    }

    fun loadProfessors(database: SQLiteDatabase): ArrayList<Professors>{
        return DataBaseManager.loadProfessors(database)
    }

    fun saveSubjects(database: SQLiteDatabase, subjects: ArrayList<Subjects>){
        DataBaseManager.saveSubjects(database, subjects)
    }

    fun loadSubjects(database: SQLiteDatabase) : ArrayList<Subjects>{
        return DataBaseManager.loadSubjects(database)
    }

    fun saveCustomSubjects(database: SQLiteDatabase, customSubjects: ArrayList<CustomSubjects>){
        DataBaseManager.saveCustomSubjects(database, customSubjects)
    }

    fun loadCustomSubjects(database: SQLiteDatabase) : ArrayList<CustomSubjectsWithDate>{
        return DataBaseManager.loadCustomSubjects(database)
    }
}
val Context.database: MyDatabaseOpenHelper
    get() = MyDatabaseOpenHelper.getInstance(getApplicationContext())