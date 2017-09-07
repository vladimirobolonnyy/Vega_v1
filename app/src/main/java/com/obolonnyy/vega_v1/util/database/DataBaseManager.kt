package com.obolonnyy.vega_v1.util.database

import android.database.sqlite.SQLiteDatabase
import com.obolonnyy.vega_v1.util.data.MyData
import com.obolonnyy.vega_v1.util.data.MyDateClass
import com.obolonnyy.vega_v1.util.dataobjects.CustomSubjects
import com.obolonnyy.vega_v1.util.dataobjects.CustomSubjectsWithDate
import com.obolonnyy.vega_v1.util.dataobjects.Professors
import com.obolonnyy.vega_v1.util.dataobjects.Subjects
import org.jetbrains.anko.db.*

/**
 * Created by Владимир on 30.08.2017.
 */
class DataBaseManager {

    companion object {

        fun saveProfessors(database: SQLiteDatabase, professors: ArrayList<Professors>) {

            //Очищаем бд беред сохранением
            database.transaction {
                dropTable(MyData.PROFESSORS_TABLE_NAME, true)
            }
            MyDatabaseOpenHelper.createPersonsTable(database)

            try {
                for (each: Professors in professors) {
                    database.insert(
                            MyData.PROFESSORS_TABLE_NAME,
                            "id" to each.id,
                            "FIO" to each.FIO,
                            "scienceDegree" to each.scienceDegree,
                            "email" to each.email,
                            "phone" to each.phone,
                            "comment" to each.comment
                    )
                }
            } catch (e: Exception) {
                println("Поймано исключение в saveProfessors \n" + e)
            }
        }

        fun saveSubjects(database: SQLiteDatabase, subjects: ArrayList<Subjects>) {
            //Очищаем бд беред сохранением
            database.transaction {
                dropTable(MyData.SUBJECTS_TABLE_NAME, true)
            }
            MyDatabaseOpenHelper.createSubjectsTable(database)

            try {
                for (each: Subjects in subjects) {
                    database.insert(
                            MyData.SUBJECTS_TABLE_NAME,
                            "id" to each.id,
                            "time" to each.time,
                            "dayOfWeek" to each.dayOfWeek,
                            "chislOrZnamen" to each.chislOrZnamen,
                            "description" to each.description
                            )
                }
            } catch (e: Exception) {
                println("Поймано исключение в saveSubjects \n" + e)
            }
        }

        fun saveCustomSubjects(database: SQLiteDatabase, customSubjects: ArrayList<CustomSubjects>) {
            //Очищаем бд беред сохранением
            database.transaction {
                dropTable(MyData.CUSTOM_SUBJECTS_TABLE_NAME, true)
            }

            MyDatabaseOpenHelper.createCustomSubjectsTable(database)

            try {
                for (each: CustomSubjects in customSubjects) {
                    database.insert(
                            MyData.CUSTOM_SUBJECTS_TABLE_NAME,
                            "id" to each.id,
                            "time" to each.time,
                            "description" to each.description,
                            "stringDate" to each.stringDate
                    )
                }
            } catch (e: Exception) {
                println("Поймано исключение в saveCustomSubjects \n" + e)
            }
        }

        fun loadProfessors(database: SQLiteDatabase): ArrayList<Professors> {
            val rowParser = classParser<Professors>()
            val result = ArrayList<Professors>(database.select(MyData.PROFESSORS_TABLE_NAME).parseList(rowParser))
            return result
        }

        fun loadSubjects(database: SQLiteDatabase): ArrayList<Subjects> {
            val rowParser = classParser<Subjects>()
            val result = ArrayList<Subjects>(database.select(MyData.SUBJECTS_TABLE_NAME).parseList(rowParser))
            return result
        }

        fun loadCustomSubjects(database: SQLiteDatabase): ArrayList<CustomSubjectsWithDate> {
            val rowParser = classParser<CustomSubjects>()
            val result = ArrayList<CustomSubjects>(database.select(MyData.CUSTOM_SUBJECTS_TABLE_NAME).parseList(rowParser))

            var result2 = ArrayList<CustomSubjectsWithDate>()

            for (each in result){
                result2.add(CustomSubjectsWithDate(each.id, each.time, each.description, each.stringDate, MyDateClass.dateParse(each.stringDate)))
            }

            return result2
        }
    }
}