package com.obolonnyy.vega_v1.util.dataobjects

import com.obolonnyy.vega_v1.R

/**
 * Created by Владимир on 04.09.2017.
 */
enum class Screens (val screen: String){
    MAIN ("MAIN"),
    SUBJECTS ("SUBJECTS"),
    PROFESSORS ("PROFESSORS"),
    EXAMS ("EXAMS"),
    SETTINGS ("SETTINGS"),
    ABOUT ("ABOUT"),

    SCREENS ("SCREENS")
}

enum class ScreensRId (val screen: Int){
    MAIN (R.id.Main_Acti),
    SUBJECTS (R.id.Subjects_Acti),
    PROFESSORS (R.id.Professors_Acti),
    EXAMS (R.id.Exams_Acti),
    SETTINGS (R.id.Settings_Acti),
    ABOUT (R.id.About_Acti),
}