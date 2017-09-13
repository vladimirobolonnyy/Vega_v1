package com.obolonnyy.vega_v1.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.obolonnyy.vega_v1.R
import com.obolonnyy.vega_v1.util.database.MyDatabaseOpenHelper
import com.obolonnyy.vega_v1.util.database.database
import org.jetbrains.anko.find
import org.jetbrains.anko.setContentView


class EachProfessorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pos = intent.getIntExtra("professorsPos", -1)
        if (pos != -1){
            val professors = MyDatabaseOpenHelper(this).loadProfessors(database.readableDatabase)
            val prof = professors[pos]

            EachProfessorUI(prof).setContentView(this)

            val toolbar = find<Toolbar>(R.id.each_professor_toolbar)
            toolbar.setTitle(parse(prof.FIO))
            setSupportActionBar(toolbar)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }

    private fun parse(FIO: String): String{
        val parsed = FIO.split(" ")
        return (parsed[0] + " " + parsed[1][0] + ". " + parsed[2][0] + ".")
    }
}
