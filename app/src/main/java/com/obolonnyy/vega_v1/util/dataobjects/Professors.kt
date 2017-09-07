package com.obolonnyy.vega_v1.util.dataobjects

/**
 * Created by Владимир on 30.08.2017.
 */
data class Professors (
        val id: Int,
        val FIO: String,
        val scienceDegree: String = "",
        val email: String = "",
        val phone: String = "",
        val comment: String = ""
)