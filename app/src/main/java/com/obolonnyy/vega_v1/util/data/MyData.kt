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

        val subjectsTime = arrayListOf<String>("08:30-10:05" +
                "10:15-11:50",
                "12:00-13:35",
                "13:50-15:25",
                "15:40-17:15",
                "17:25-19:00",
                "19:10-20:45")


        //############## название таблиц для БД
        val SUBJECTS_TABLE_NAME = "Subjects"
        val CUSTOM_SUBJECTS_TABLE_NAME = "CustomSubjects"
        val PROFESSORS_TABLE_NAME = "Professors"
    }
}