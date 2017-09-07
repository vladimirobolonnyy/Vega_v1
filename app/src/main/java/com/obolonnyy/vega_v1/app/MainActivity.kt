package com.obolonnyy.vega_v1.app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.obolonnyy.vega_v1.R
import com.obolonnyy.vega_v1.util.data.MyData
import com.obolonnyy.vega_v1.util.data.MyDateClass
import com.obolonnyy.vega_v1.util.database.MyDatabaseOpenHelper
import com.obolonnyy.vega_v1.util.database.database
import com.obolonnyy.vega_v1.util.dataobjects.CustomSubjectsWithDate
import com.obolonnyy.vega_v1.util.dataobjects.Professors
import com.obolonnyy.vega_v1.util.dataobjects.Screens
import com.obolonnyy.vega_v1.util.dataobjects.Subjects


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var viewGroup: ViewGroup
    private lateinit var textViewMain: TextView
    private lateinit var subjects: ArrayList<Subjects>
    private lateinit var customSubjects: ArrayList<CustomSubjectsWithDate>
    private lateinit var professors: ArrayList<Professors>
    private lateinit var sPref: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.setDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        /*################ мой код ##############*/

        sPref = getSharedPreferences("myFileForData", Context.MODE_PRIVATE)

        loadAllFromDatabase()
        loadMainPage()

        val screen = sPref.getInt(Screens.SCREENS.screen, R.id.Main_Acti)
        onNavigationItemSelected(screen)
    }

    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        return if (id == R.id.action_settings) {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            true
        } else super.onOptionsItemSelected(item)

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId
        return onNavigationItemSelected(id)
    }

    private fun onNavigationItemSelected(id: Int): Boolean {
        if (id == R.id.Main_Acti) {
            initializeContentFor(R.layout.content_main)
            loadMainPage()
        } else if (id == R.id.Subjects_Acti) {
            initializeContentFor(R.layout.content_subjects)
            loadSubjects()
        } else if (id == R.id.Professors_Acti) {
            initializeContentFor(R.layout.content_professors)
            loadProfessors()
        } else if (id == R.id.Exams_Acti) {
            initializeContentFor(R.layout.content_exams)
            loadExamsPage()
        } else if (id == R.id.Settings_Acti) {
//            initializeContentFor(R.layout.content_settings)
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        } else if (id == R.id.About_Acti) {
            initializeContentFor(R.layout.content_about)
            loadAboutPage()
        }

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun initializeContentFor(to: Int) {
        viewGroup = findViewById(R.id.content_main) as ViewGroup
        viewGroup.removeAllViews()
        viewGroup.addView(View.inflate(this, to, null))
    }

    /*##########################*/
    /*##########  Main  ########*/
    /*##########################*/
    private fun loadMainPage(){
        textViewMain = findViewById(R.id.TextViewMain) as TextView
        val date = MyDateClass.dateNow()
        var message = date.toString() + "  " + date.dayOfWeek + "\n"


        val beginningStudyDate = MyDateClass.dateParse(sPref.getString("beginningStudyDate", "01.09.2017"))
        val numberOfWeeks = MyDateClass.getDifferenceInWeeks(beginningStudyDate)
        message += "\n week:= " + numberOfWeeks

        if (numberOfWeeks % 2 == 0)
            message += "\n" + MyData.ZNAMENATEL
        else
            message += "\n" +MyData.CHISLITEL


        textViewMain.setText(message)

    }

    private fun loadAllFromDatabase() {
        val mDBhelper = MyDatabaseOpenHelper(this)

        subjects = mDBhelper.loadSubjects(database.readableDatabase)
        customSubjects = mDBhelper.loadCustomSubjects(database.readableDatabase)
        professors = mDBhelper.loadProfessors(database.readableDatabase)

    }

    /*##########################*/
    /*####### Professors #######*/
    /*##########################*/
    private fun loadProfessors(){
        var message: String = ""
        for (each in professors){
            message += each.toString() + "\n"+ "\n"
        }
        val textViewProfessors: TextView = findViewById(R.id.textViewProfessors) as TextView
        textViewProfessors.setText(message);
    }

    /*##########################*/
    /*####### Subjects #######*/
    /*##########################*/
    private fun loadSubjects(){
        var message: String = ""
        for (each in subjects){
            message += each.toString() + "\n"+ "\n"
        }

        message += "################ \n\n"

        for (each in customSubjects){
            message += each.toString() + "\n"+ "\n"
        }
        val textViewSubjects: TextView = findViewById(R.id.textViewSubjects) as TextView
        textViewSubjects.setText(message)
    }

    /*##########################*/
    /*##########  About  ########*/
    /*##########################*/
    private fun loadAboutPage() {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /*##########################*/
    /*##########  Exams  ########*/
    /*##########################*/
    private fun loadExamsPage() {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}