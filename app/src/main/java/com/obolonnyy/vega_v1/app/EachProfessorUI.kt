package com.obolonnyy.vega_v1.app

import android.support.design.widget.AppBarLayout
import com.obolonnyy.vega_v1.R
import com.obolonnyy.vega_v1.util.dataobjects.Professors
import org.jetbrains.anko.*

/**
 * Created by Vladimir Obolonnyy on 12.09.2017.
 */
class EachProfessorUI(professor: Professors) : AnkoComponent<EachProfessorActivity> {

    private lateinit var prof: Professors

    init {
        prof = professor
    }

    override fun createView(ui: AnkoContext<EachProfessorActivity>) = with(ui){

        verticalLayout {
            background.setAlpha(20)

            include<AppBarLayout>(R.layout.each_professor_appbarlayout)

            textView("Телефон: " + prof.phone).lparams(width = wrapContent) {
                horizontalMargin = dip(5)
                topMargin = dip(10)
            }
            textView("email: " + prof.email).lparams(width = wrapContent) {
                horizontalMargin = dip(5)
                topMargin = dip(10)
            }
            textView("Степень: " + prof.scienceDegree).lparams(width = wrapContent) {
                horizontalMargin = dip(5)
                topMargin = dip(10)
            }
            textView("Комментарий: " + prof.comment).lparams(width = wrapContent) {
                horizontalMargin = dip(5)
                topMargin = dip(10)
            }
        }
    }
}