package com.obolonnyy.vega_v1.app

import android.Manifest
import android.accounts.AccountManager
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.view.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.sheets.v4.SheetsScopes
import com.obolonnyy.vega_v1.R
import com.obolonnyy.vega_v1.util.data.MyData
import com.obolonnyy.vega_v1.util.data.MyDateClass
import com.obolonnyy.vega_v1.util.database.MyDatabaseOpenHelper
import com.obolonnyy.vega_v1.util.database.database
import com.obolonnyy.vega_v1.util.dataobjects.CustomSubjectsWithDate
import com.obolonnyy.vega_v1.util.dataobjects.Professors
import com.obolonnyy.vega_v1.util.dataobjects.Subjects
import com.obolonnyy.vega_v1.util.viewspages.ProfessorsPage
import com.obolonnyy.vega_v1.util.viewspages.SubjectsPage
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, EasyPermissions.PermissionCallbacks {

    private lateinit var viewGroup: ViewGroup
    private lateinit var textViewMain: TextView
    private lateinit var subjects: ArrayList<Subjects>
    private lateinit var customSubjects: ArrayList<CustomSubjectsWithDate>
    private lateinit var professors: ArrayList<Professors>
    private lateinit var sPref: SharedPreferences

    private val mDBhelper = MyDatabaseOpenHelper(this)

    lateinit var mCredential: GoogleAccountCredential
    private var mOutputText: TextView? = null
    private var mCallApiButton: Button? = null
    lateinit var mProgress: ProgressDialog

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

//        val screen = sPref.getInt(Screens.SCREENS.screen, R.id.Main_Acti)
//        onNavigationItemSelected(screen)

//        val fab = findViewById(R.id.floatingActionButton) as FloatingActionButton
//        fab.setOnClickListener(View.OnClickListener { startCircularRevealAnimation() })
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
//            val intent = Intent(this, SettingsActivity::class.java)
//            startActivity(intent)
            initializeContentFor(R.layout.content_settings)
            loadSettingsPage()
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
            startCircularRevealAnimation(findViewById(R.id.content_main))
        } else if (id == R.id.Subjects_Acti) {
            initializeContentFor(R.layout.content_subjects)
            loadSubjects()
        } else if (id == R.id.Professors_Acti) {
            initializeContentFor(R.layout.content_professors)
            loadProfessors()
            startCircularRevealAnimation(findViewById(R.id.content_professors))

        } else if (id == R.id.Exams_Acti) {
            initializeContentFor(R.layout.content_exams)
            loadExamsPage()
            startCircularRevealAnimation(findViewById(R.id.content_exams))

        } else if (id == R.id.Settings_Acti) {
            initializeContentFor(R.layout.content_settings)
            loadSettingsPage()
//            val intent = Intent(this, SettingsActivity::class.java)
//            startActivity(intent)
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
    /*##########  Other  ########*/
    /*##########################*/


    // Custom method to create a circular reveal animation for specified view
    private fun startCircularRevealAnimation(v: View) {
        // Get the specified view center x
        val x = 0
        val y = 0

        val startRadius = 0
        val endRadius = Math.hypot(2000.0,2000.0).toInt()
//        val v = findViewById(R.id.content_main)

        val anim = ViewAnimationUtils.createCircularReveal(v, x, y, startRadius.toFloat(), endRadius.toFloat())
        anim.setDuration(3000)
        anim.setStartDelay(80)
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
//                findViewById(R.id.floatingActionButton).setBackgroundColor(0)
//                findViewById(R.id.content_main).visibility(View.INVISIBLE)
//                findViewById(R.id.content_main).setBackgroundColor(R.color.myOrange)
            }

            override fun onAnimationEnd(animation: Animator) {
                v.setVisibility(View.VISIBLE)

            }
        })
        anim.start()
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
        textViewProfessors.setText(message)
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

    /*##########################*/
    /*########  Settings  ######*/
    /*##########################*/


    private fun loadSettingsPage(){

        val activityLayout = findViewById(R.id.settingsLinearLayout) as LinearLayout
        //        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
        //                LinearLayout.LayoutParams.MATCH_PARENT,
        //                LinearLayout.LayoutParams.MATCH_PARENT);
        //        activityLayout.setLayoutParams(lp);
        activityLayout.orientation = LinearLayout.VERTICAL
        activityLayout.setPadding(16, 16, 16, 16)

        val tlp = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)

        mCallApiButton = Button(this)
        mCallApiButton!!.text = MainActivity.BUTTON_TEXT
        mCallApiButton!!.setOnClickListener {
            mCallApiButton!!.isEnabled = false
            mOutputText!!.text = ""
            getResultsFromApi()
            mCallApiButton!!.isEnabled = true
        }
        activityLayout.addView(mCallApiButton)

        mOutputText = TextView(this)
        mOutputText!!.layoutParams = tlp
        mOutputText!!.setPadding(16, 16, 16, 16)
        mOutputText!!.isVerticalScrollBarEnabled = true
        mOutputText!!.movementMethod = ScrollingMovementMethod()
        mOutputText!!.text = "Click the \'${MainActivity.BUTTON_TEXT}\' button to test the API."
        activityLayout.addView(mOutputText)

        mProgress = ProgressDialog(this)
        mProgress.setMessage("Calling Google Sheets API ...")

        //        setContentView(activityLayout);

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                applicationContext, Arrays.asList(*MainActivity.SCOPES))
                .setBackOff(ExponentialBackOff())

    }


    private fun getResultsFromApi() {
        if (!isGooglePlayServicesAvailable) {
            acquireGooglePlayServices()
        } else if (mCredential.selectedAccountName == null) {
            chooseAccount()
        } else if (!isDeviceOnline) {
            mOutputText!!.text = "No network connection available."
        } else {
            MakeRequestTask(mCredential).execute()
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private fun chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            val accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null)
            if (accountName != null) {
                mCredential.selectedAccountName = accountName
                getResultsFromApi()
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER)
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS)
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     * activity result.
     * @param data Intent (containing result data) returned by incoming
     * activity result.
     */
    override fun onActivityResult(
            requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_GOOGLE_PLAY_SERVICES -> if (resultCode != Activity.RESULT_OK) {
                mOutputText!!.text = "This app requires Google Play Services. Please install " + "Google Play Services on your device and relaunch this app."
            } else {
                getResultsFromApi()
            }
            REQUEST_ACCOUNT_PICKER -> if (resultCode == Activity.RESULT_OK && data != null &&
                    data.extras != null) {
                val accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
                if (accountName != null) {
                    val settings = getPreferences(Context.MODE_PRIVATE)
                    val editor = settings.edit()
                    editor.putString(PREF_ACCOUNT_NAME, accountName)
                    editor.apply()
                    mCredential.selectedAccountName = accountName
                    getResultsFromApi()
                }
            }
            REQUEST_AUTHORIZATION -> if (resultCode == Activity.RESULT_OK) {
                getResultsFromApi()
            }
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     * requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     * which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this)
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     * permission
     * @param list The requested permission list. Never null.
     */
    override fun onPermissionsGranted(requestCode: Int, list: List<String>) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     * permission
     * @param list The requested permission list. Never null.
     */
    override fun onPermissionsDenied(requestCode: Int, list: List<String>) {
        // Do nothing.
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private val isDeviceOnline: Boolean
        get() {
            val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connMgr.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }

    /**
     * Check that Google Play services APK is installed and up to stringDate.
     * @return true if Google Play Services is available and up to
     * stringDate on this device; false otherwise.
     */
    private val isGooglePlayServicesAvailable: Boolean
        get() {
            val apiAvailability = GoogleApiAvailability.getInstance()
            val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this)
            return connectionStatusCode == ConnectionResult.SUCCESS
        }

    /**
     * Attempt to resolve a missing, out-of-stringDate, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private fun acquireGooglePlayServices() {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this)
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode)
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of stringDate.
     * @param connectionStatusCode code describing the presence (or lack of)
     * Google Play Services on this device.
     */
    internal fun showGooglePlayServicesAvailabilityErrorDialog(
            connectionStatusCode: Int) {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val dialog = apiAvailability.getErrorDialog(
                this@MainActivity,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES)
        dialog.show()
    }

    /**
     * An asynchronous task that handles the Google Sheets API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private inner class MakeRequestTask internal constructor(credential: GoogleAccountCredential) : AsyncTask<Void, Void, List<String>>() {
        private var mService: com.google.api.services.sheets.v4.Sheets? = null
        private var mLastError: Exception? = null

        init {
            val transport = AndroidHttp.newCompatibleTransport()
            val jsonFactory = JacksonFactory.getDefaultInstance()
            mService = com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("MyTimeTable_v2")
                    .build()
        }

        /**
         * Background task to call Google Sheets API.
         * @param params no parameters needed for this task.
         */
        override fun doInBackground(vararg params: Void): List<String>? {
            try {
                return dataFromApi
            } catch (e: Exception) {
                println("Oshibka" + e)

                mLastError = e
                cancel(true)
                return null
            }

        }

        /**
         * Fetch a list of names and majors of students in a sample spreadsheet:
         * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
         * @return List of names and majors
         * @throws IOException
         */
        private val dataFromApi: List<String>
            @Throws(IOException::class)
            get() {
                val results = java.util.ArrayList<String>()
                try {
                    getProfessorsFromGoogle()
                    results.add("Преподователи успешно загрузились.")
                } catch (e: Exception) {
                    results.add("Преподователи не загрузились =( \n Ошибка: $e")
                }

                try {
                    getSubjectsFromGoogle()
                    results.add("Предметы успешно загрузились.")
                } catch (e: Exception) {
                    results.add("Предметы не загрузились =( \n Ошибка: $e")
                }

                try {
                    getCustomSubjectsFromGoogle()
                    results.add("доп. предметы успешно загрузились.")
                } catch (e: Exception) {
                    results.add("доп. предметы не загрузились =( \n Ошибка: $e")
                }

                return results
            }

        @Throws(IOException::class)
        private fun getProfessorsFromGoogle() {
            // Грузим преподователей
            val range = "Professors!A2:E"
            val response = this.mService!!.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute()
            val values = response.getValues()
            val professors = ProfessorsPage.parseProfessors(values)
            mDBhelper.saveProfessors(mDBhelper.writableDatabase, professors)
        }

        @Throws(IOException::class)
        private fun getSubjectsFromGoogle() {
            // Грузим предметы
            val range = "Schedule!A2:C"
            val response = this.mService!!.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute()
            val values = response.getValues()
            val subjects = SubjectsPage.parseSubjects(values)
            mDBhelper.saveSubjects(mDBhelper.writableDatabase, subjects)
        }

        @Throws(IOException::class)
        private fun getCustomSubjectsFromGoogle() {
            // Грузим доп. предметы
            val range = "Custom schedule!A2:C"
            val response = this.mService!!.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute()
            val values = response.getValues()
            val customSubjects = SubjectsPage.parseCustomSubjects(values)
            mDBhelper.saveCustomSubjects(mDBhelper.writableDatabase, customSubjects)
        }


        override fun onPreExecute() {
            mOutputText!!.text = ""
            mProgress.show()
        }

        override fun onPostExecute(output: List<String>?) {
            mProgress.hide()
            val output2 = output?.toMutableList()
            if (output2 == null || output.size == 0) {
                mOutputText!!.text = "No results returned."
            } else {
                output2.add(0, "Data retrieved using the Google Sheets API:")
                mOutputText!!.text = TextUtils.join("\n", output2)
            }
        }

        override fun onCancelled() {
            mProgress.hide()
            if (mLastError != null) {
                if (mLastError is GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            (mLastError as GooglePlayServicesAvailabilityIOException)
                                    .connectionStatusCode)
                } else if (mLastError is UserRecoverableAuthIOException) {
                    startActivityForResult(
                            (mLastError as UserRecoverableAuthIOException).intent,
                            MainActivity.REQUEST_AUTHORIZATION)
                } else {
                    mOutputText!!.text = "The following error occurred:\n" + mLastError!!.message
                }
            } else {
                mOutputText!!.text = "Request cancelled."
            }
        }
    }

    companion object {

        internal val REQUEST_ACCOUNT_PICKER = 1000
        internal val REQUEST_AUTHORIZATION = 1001
        internal val REQUEST_GOOGLE_PLAY_SERVICES = 1002
        internal const val REQUEST_PERMISSION_GET_ACCOUNTS = 1003

        private val BUTTON_TEXT = "Call Google Sheets API"
        private val PREF_ACCOUNT_NAME = "accountName"
        private val SCOPES = arrayOf(SheetsScopes.SPREADSHEETS_READONLY)
        private val spreadsheetId = "1oThzOBek1DUIAVBIdqTR1tkUnQJJTcbPR0lWZ3n5Z0k"
    }
}