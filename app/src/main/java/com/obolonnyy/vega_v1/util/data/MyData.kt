package com.obolonnyy.vega_v1.util.data

/**
 * Created by Владимир on 03.09.2017.
 */
class MyData {
    companion object {

        val daysOfWeek = arrayListOf<String>("Понедельник",
                "Вторник",
                "Среда",
                "Четверг",
                "Пятница",
                "Суббота",
                "Воскресенье")

        val CHISLITEL = "Числитель"
        val ZNAMENATEL = "Знаменатель"


        //############## название таблиц для БД
        val SUBJECTS_TABLE_NAME = "Subjects"
        val CUSTOM_SUBJECTS_TABLE_NAME = "CustomSubjects"
        val PROFESSORS_TABLE_NAME = "Professors"
    }
}