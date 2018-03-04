package com.obolonnyy.vega_v1.app

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by Vladimir Obolonnyy on 17.09.2017.
 */
class GlobalSettings {

    companion object {
        private lateinit var sPref: SharedPreferences

        var animation: String = "animation"
        var hideEmptyDays: String = "hideEmptyDays"
        var beginningStudyDate: String = "beginningStudyDate"


        fun setAnimation(ctx: Context, value: Boolean){
            sPref = ctx.getSharedPreferences("myFileForData", 0)
            val editor = sPref.edit()
            editor.putBoolean(animation, value)
            editor.apply()
        }

        fun setHideEmptyDays(ctx: Context, value: Boolean){
            sPref = ctx.getSharedPreferences("myFileForData", 0)
            val editor = sPref.edit()
            editor.putBoolean(hideEmptyDays, value)
            editor.apply()
        }

        fun getAnimation(ctx: Context): Boolean{
            sPref = ctx.getSharedPreferences("myFileForData", 0)
            return sPref.getBoolean(animation, true)
        }

        fun getHideEmptyDays(ctx: Context): Boolean{
            sPref = ctx.getSharedPreferences("myFileForData", 0)
            return sPref.getBoolean(hideEmptyDays, true)
        }

        fun getBeginningStudyDate(ctx: Context): String{
            sPref = ctx.getSharedPreferences("myFileForData", 0)
            return sPref.getString(beginningStudyDate, "01.09.2017")
        }
    }
}