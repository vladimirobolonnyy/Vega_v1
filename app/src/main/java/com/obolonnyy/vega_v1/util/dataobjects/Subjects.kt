package com.obolonnyy.vega_v1.util.dataobjects

import com.obolonnyy.vega_v1.util.data.MyData

/**
 * Created by Владимир on 03.09.2017.
 */

data class Subjects (
        val id: Int,
        val time: String,
        val dayOfWeekInt: Int, // 1 = понедельник, 2 = вторник
        val chislOrZnamen: String,
        val description: String

){
    val dayOfWeekString: String
    val timeInt: Int

    init {
        dayOfWeekString = MyData.daysOfWeek[dayOfWeekInt]
        timeInt = MyData.subjectsTime.indexOfFirst { it == time }
    }
}