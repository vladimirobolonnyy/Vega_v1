package com.obolonnyy.vega_v1.util.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.obolonnyy.vega_v1.util.data.MyData
import com.obolonnyy.vega_v1.util.dataobjects.CustomSubjects
import com.obolonnyy.vega_v1.util.dataobjects.CustomSubjectsWithDate
import com.obolonnyy.vega_v1.util.dataobjects.Professors
import com.obolonnyy.vega_v1.util.dataobjects.Subjects
import org.jetbrains.anko.db.*

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

        fun createProfessorsTable(database: SQLiteDatabase){
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
                            "dayOfWeekInt" to INTEGER,
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
        createProfessorsTable(database)
        createSubjectsTable(database)
        createCustomSubjectsTable(database)

        fullDefaultInfo(database)
    }

    private fun fullDefaultInfo(database: SQLiteDatabase) {
        database.insert(
                MyData.PROFESSORS_TABLE_NAME,
                "id" to 1,
                "FIO" to "Иван Иванович Иванов",
                "scienceDegree" to "Старший преподаватель",
                "email" to "ivan@gmail.com",
                "phone" to "+7 909 999 9999",
                "comment" to "Любит коньяк"
        )
           database.insert(
                MyData.PROFESSORS_TABLE_NAME,
                "id" to 2,
                "FIO" to "Петров Петр Петрович",
                "scienceDegree" to "Доцент",
                "email" to "petrov@yandex.ru",
                "phone" to "+7 907 777 77 77",
                "comment" to "Много говорит"
        )
        database.insert(
                MyData.PROFESSORS_TABLE_NAME,
                "id" to 3,
                "FIO" to "Сидоров Сидр Сидорович",
                "scienceDegree" to "PHD",
                "email" to "sidrsidorov@gmail.com",
                "phone" to "+7 905 555 55 55",
                "comment" to "Отмечает на лекциях"
        )

        database.insert(
                MyData.SUBJECTS_TABLE_NAME,
                "id" to 1,
                "time" to "10:15-11:50",
                "dayOfWeekInt" to 1,
                "chislOrZnamen" to "Числитель",
                "description" to "Сидоров Сидр Сидорович преподает науку о сидрах"
        )
        database.insert(
                MyData.SUBJECTS_TABLE_NAME,
                "id" to 2,
                "time" to "13:50-15:25",
                "dayOfWeekInt" to 3,
                "chislOrZnamen" to "Знаменатель",
                "description" to "Иванов Иван Иванович рассказывает об ивах"
        )
        database.insert(
                MyData.SUBJECTS_TABLE_NAME,
                "id" to 3,
                "time" to "10:15-11:50",
                "dayOfWeekInt" to 5,
                "chislOrZnamen" to "Числитель",
                "description" to "Петров Петр Петрович рассказывает о Петре первом, сыне его славнов, богатстве нажитом и заслугах великих"
        )

        database.insert(
                MyData.CUSTOM_SUBJECTS_TABLE_NAME,
                "id" to 1,
                "time" to "10:15-11:50",
                "description" to "Лабы какие-то",
                "stringDate" to "26.09.2017"
        )
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