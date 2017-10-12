package com.obolonnyy.vega_v1.util.data

import com.obolonnyy.vega_v1.util.exceptions.IllegalDateFormatException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Владимир on 30.08.2017.
 */
data class MyDateClass(val year: Int, val month: Int, val dayOfMonth: Int) : Comparable<MyDateClass> {
    var dayOfWeekInt: Int = 0
    var dayOfWeek: String = ""

    init{
        if (year < 2000) throw IllegalDateFormatException("Неверная дата. Год ${year} слишком маленький")
        if (month !in 1..12) throw IllegalDateFormatException("Неверная дата. Месяц должен быть от 1 до 12 ")
        if (dayOfMonth !in 1..31) throw IllegalDateFormatException("Неверная дата. Дней в месяце от 1 до 31")
        if (year %4 == 0 && month == 2 && dayOfMonth !in 1..29) throw IllegalDateFormatException("Неверная дата. В феврале високосного года от 1 до 29 дней")
        if (year %4 != 0 && month == 2 && dayOfMonth !in 1..28) throw IllegalDateFormatException("Неверная дата. В феврале от 1 до 28 дней")
        if (month == 4 && dayOfMonth !in 1..30) throw IllegalDateFormatException("Неверная дата. В апреле от 1 до 30 дней")
        if (month == 6 && dayOfMonth !in 1..30) throw IllegalDateFormatException("Неверная дата. В июне от 1 до 30 дней")
        if (month == 9 && dayOfMonth !in 1..30) throw IllegalDateFormatException("Неверная дата. В сентябре от 1 до 30 дней")
        if (month == 11 && dayOfMonth !in 1..30) throw IllegalDateFormatException("Неверная дата. В ноябре от 1 до 30 дней")

        val usialDate = Date((year - 1900), (month - 1), (dayOfMonth))
        dayOfWeekInt = usialDate.day
        dayOfWeek = MyData.daysOfWeek[dayOfWeekInt]
    }

    override fun compareTo(other: MyDateClass): Int {
        if(this.year != other.year)
            return (this.year - other.year)
        if(this.month != other.month)
            return (this.month - other.month)
        else
            return (this.dayOfMonth - other.dayOfMonth)
    }

    override fun toString() = "${this.dayOfMonth}.${this.month}.${this.year}"

    operator fun inc(): MyDateClass {
        var resultDate: MyDateClass
        try {
            resultDate = MyDateClass(dayOfMonth = (this.dayOfMonth + 1), month = this.month, year = this.year)
        } catch (e: IllegalDateFormatException) {
            try {
                resultDate = MyDateClass(dayOfMonth = 1, month = (this.month + 1), year = this.year)
            } catch (e: IllegalDateFormatException) {
                resultDate = MyDateClass(dayOfMonth = 1, month = 1, year = (this.year + 1))
            }
        }
        return resultDate
    }

    companion object {

        fun dateNow(): MyDateClass = MyDateClass(year = Date().year + 1900, month = Date().month + 1, dayOfMonth = Date().date)

        fun javaDateToMyDate(date: Date): MyDateClass = MyDateClass(year = date.year + 1900, month = date.month + 1, dayOfMonth = date.date)

        fun myDateToJavaDate(date: MyDateClass): Date = Date((date.year - 1900), (date.month - 1), (date.dayOfMonth))

        // На всякий случай. Вдруг пригодится брать не только дату, но и время
        fun getDateAndTimeNow(): String = Date().toString()

        fun dateParse(stringDate: String): MyDateClass {
            try {
                val simpleDateFormat = SimpleDateFormat(DateHelper.DF_SIMPLE_STRING)
                val parsedDate: Date = simpleDateFormat.parse(stringDate)
                // В стандартной реализации из года вычитается 1900. Не знаю, зачем.
                return javaDateToMyDate(parsedDate)
//                return MyDateClass(year = parsedDate.year + 1900, month = parsedDate.month + 1, dayOfMonth = parsedDate.date)
            } catch (e: IllegalArgumentException) {
                throw IllegalDateFormatException("Неверная дата. Формат даты должен быть" +
                        " ${DateHelper.DF_SIMPLE_STRING}")
            }
        }

        fun getDifferenceInWeeksFromNow(date: MyDateClass): Int{
//            val currentDate = Date().time
//            val beginningDate = myDateToJavaDate(date)
//            val diff = currentDate - beginningDate.time
//            // diff_in_days - разница в днях, начиная от начала учёбы
//            val diff_in_days = (diff / (24 * 60 * 60000)).toInt() - 1
//            val number_of_week = (diff_in_days + beginningDate.day) / 7 + 1
//            return number_of_week

//            val currentDate = myDateToJavaDate(getStartWeeksDate(dateNow())).time
//            val secondDate = myDateToJavaDate(getStartWeeksDate(date)).time
//            val diff = currentDate - secondDate
//            // diff_in_days - разница в днях
//            val diff_in_days = (diff / (24 * 60 * 60000)).toInt()
//            val number_of_week = (diff_in_days) / 7
//            return number_of_week

            return (getDifferenceInWeeks(date, dateNow()) + 1);
        }

        fun getDifferenceInWeeks(date: MyDateClass, date2: MyDateClass): Int {
            val firstDate = myDateToJavaDate(getStartWeeksDate(date)).time
            val secondDate = myDateToJavaDate(getStartWeeksDate(date2)).time
            val diff = secondDate - firstDate
            // diff_in_days - разница в днях
            val diff_in_days = (diff / (24 * 60 * 60000)).toInt()
            val number_of_week = (diff_in_days) / 7
            return number_of_week
        }

        fun getStartWeeksDate(date: MyDateClass): MyDateClass{
            val diff = date.dayOfWeekInt // 0 - воскресенье, 1 - понедельник
            val javaDate = Date((date.year - 1900), (date.month - 1), (date.dayOfMonth))
            val resultDate = Date(javaDate.time - diff*24*60*60000)
            return javaDateToMyDate(resultDate)
        }
    }
}
