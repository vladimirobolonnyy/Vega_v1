package com.obolonnyy.vega_v1.util.exceptions

/**
 * Created by Владимир on 30.08.2017.
 */

class IllegalDateFormatException(message: String="Неверная дата."): Exception() {
    init{
        println(message)
    }
}