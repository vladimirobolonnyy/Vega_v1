package com.obolonnyy.vega_v1.util.data

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Владимир on 30.08.2017.
 */

object DateHelper {
    const val DF_SIMPLE_STRING = "dd.MM.yyyy"
    @JvmField val DF_SIMPLE_FORMAT = object : ThreadLocal<DateFormat>() {
        override fun initialValue(): DateFormat {
            return SimpleDateFormat(DF_SIMPLE_STRING, Locale.US)
        }
    }
}
