package com.obolonnyy.vega_v1.app

import android.support.v7.widget.Toolbar
import com.obolonnyy.vega_v1.R
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * Created by Vladimir Obolonnyy on 12.09.2017.
 */
class EachProfessorUI : AnkoComponent<EachProfessorActivity> {
    override fun createView(ui: AnkoContext<EachProfessorActivity>) = with(ui){
        verticalLayout {
//            val v = findViewById(R.id.each_professor_toolbar)
            include<Toolbar>(R.id.each_professor_toolbar)
//            val toolbar = find<Toolbar>(R.id.each_professor_toolbar)
//            toolbar(R.id.toolbar)
            val name = editText()
            button("Say Hello") {
                onClick { toast("Hello, ${name.text}!") }
            }
        }
    }
}