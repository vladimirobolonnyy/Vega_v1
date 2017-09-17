package com.obolonnyy.vega_v1.util.dataobjects

import com.obolonnyy.vega_v1.util.data.MyData
import com.obolonnyy.vega_v1.util.data.MyDateClass

/**
 * Created by Владимир on 03.09.2017.
 */
data class CustomSubjectsWithDate (
        val id: Int,
        val time: String,
        val description: String,
        val stringDate: String,
        val date: MyDateClass
){
    val timeInt: Int

    init {
        timeInt = MyData.subjectsTime.indexOfFirst { it == time }
    }
}