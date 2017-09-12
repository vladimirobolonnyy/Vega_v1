package com.obolonnyy.vega_v1.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.TextView
import com.obolonnyy.vega_v1.R
import com.obolonnyy.vega_v1.util.database.MyDatabaseOpenHelper
import com.obolonnyy.vega_v1.util.database.database

class EachProfessorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_each_professor)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val textView = findViewById(R.id.eachProfessor) as TextView
        val pos = intent.getIntExtra("professorsPos", -1)

        if (pos != -1){
            val professors = MyDatabaseOpenHelper(this).loadProfessors(database.readableDatabase)
            val professor = professors[pos]

            this.title = professor.FIO

            val message = professor.FIO + "\n" +
                    "email:= " + professor.email + "\n" +
                    "phone:= " + professor.phone + "\n" +
                    "comment:= " + professor.comment + "\n" +
                    "scienceDegree:= " + professor.scienceDegree + "\n"
            textView.text = message
        } else {
            textView.text = ("Error with loading. Get id = ${pos}")
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
