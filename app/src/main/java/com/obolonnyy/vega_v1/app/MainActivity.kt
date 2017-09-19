package com.obolonnyy.vega_v1.app

import android.Manifest
import android.accounts.AccountManager
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.view.*
import android.widget.*
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
import com.obolonnyy.vega_v1.loadinfotoscreen.SubjectsUI
import com.obolonnyy.vega_v1.loadinfotoscreen.SubjectsUI.Companion.setSchedule
import com.obolonnyy.vega_v1.loadinfotoscreen.SubjectsUI.Companion.showSubjects
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

companion object {
    lateinit var subjects: ArrayList<Subjects>
    lateinit var customSubjects: ArrayList<CustomSubjectsWithDate>
    lateinit var professors: ArrayList<Professors>
    lateinit var date: MyDateClass
    var numberOfWeeks: Int = 0

    internal val REQUEST_ACCOUNT_PICKER = 1000
    internal val REQUEST_AUTHORIZATION = 1001
    internal val REQUEST_GOOGLE_PLAY_SERVICES = 1002
    internal const val REQUEST_PERMISSION_GET_ACCOUNTS = 1003

    private val BUTTON_TEXT = "Call Google Sheets API"
    private val PREF_ACCOUNT_NAME = "accountName"
    private val SCOPES = arrayOf(SheetsScopes.SPREADSHEETS_READONLY)
    private val spreadsheetId = "1oThzOBek1DUIAVBIdqTR1tkUnQJJTcbPR0lWZ3n5Z0k"
}

    private lateinit var viewGroup: ViewGroup
    private lateinit var textViewMain: TextView
//    private lateinit var sPref: SharedPreferences

    private val mDBhelper = MyDatabaseOpenHelper(this)

    private lateinit var mCredential: GoogleAccountCredential
    private var mOutputText: TextView? = null
    private var mCallApiButton: Button? = null
    private lateinit var mProgress: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        /*################ мой код ##############*/

        loadAllFromDatabase()
        loadMainPage()
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
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        return if (id == R.id.action_settings) {
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

        when (id){
            R.id.Main_Acti -> {
                initializeContentFor(R.layout.content_main)
                loadMainPage()
                startCircularRevealAnimation(findViewById(R.id.content_main))
            }
            R.id.Subjects_Acti -> {
                initializeContentFor(R.layout.content_subjects)
                loadSubjects()
                startCircularRevealAnimation(findViewById(R.id.content_subjects))
            }
            R.id.Professors_Acti -> {
                initializeContentFor(R.layout.content_professors)
                loadProfessors()
                startCircularRevealAnimation(findViewById(R.id.content_professors))
            }
            R.id.Exams_Acti -> {
                initializeContentFor(R.layout.content_exams)
                loadExamsPage()
                startCircularRevealAnimation(findViewById(R.id.content_exams))
            }
            R.id.Settings_Acti -> {
                initializeContentFor(R.layout.content_settings)
                loadSettingsPage()
                startCircularRevealAnimation(findViewById(R.id.content_settings))
            }
            R.id.About_Acti -> {
                initializeContentFor(R.layout.content_about)
                loadAboutPage()
                startCircularRevealAnimation(findViewById(R.id.content_about))
            }
            else -> {}
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
        val workingView = findViewById(R.id.content_main)
        var x = 0
        var y = 0
        val maxX = workingView.width
        val maxY = workingView.height

        val rand = Random().nextInt(10)
        when(rand) {
            0,1 -> {}
            2,3 -> { x = maxX; y = 0; }
            4,5 -> { x = 0; y = maxY; }
            6,7 -> { x = maxX; y = maxY; }
            else -> { x = maxX/2; y = maxY/2; }
        }
        val startRadius = 0
        val endRadius = Math.hypot(workingView.width.toDouble(), workingView.height.toDouble()).toInt()
        val anim = ViewAnimationUtils.createCircularReveal(v, x, y, startRadius.toFloat(),
                endRadius.toFloat())
        anim.duration = 500
        anim.start()
    }

    /*##########################*/
    /*##########  Main  ########*/
    /*##########################*/
    private fun loadMainPage(){

        textViewMain = findViewById(R.id.TextViewMain) as TextView
        date = MyDateClass.dateNow()
        var message = date.toString() + "  " + date.dayOfWeek + "\n"

        val beginningStudyDate = MyDateClass.dateParse(GlobalSettings.getBeginningStudyDate(this))
        numberOfWeeks = MyDateClass.getDifferenceInWeeksFromNow(beginningStudyDate)
        message += "\n week:= " + numberOfWeeks

        if ((numberOfWeeks % 2) == 0)
            message += "\n" + MyData.ZNAMENATEL
        else
            message += "\n" +MyData.CHISLITEL

        textViewMain.text = message

        val fab = findViewById(R.id.main_fab) as FloatingActionButton
        fab.setPadding(100,100,100,100)
        fab.setOnClickListener{ view ->
            initializeContentFor(R.layout.content_subjects)
            startCircularRevealAnimation(findViewById(R.id.content_subjects))
            loadSubjects()
        }
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
        loadAllFromDatabase()

        val names = professors.map { it.FIO }
        val listView = findViewById(R.id.professors_listView) as ListView
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, names)
        listView.setOnItemClickListener(AdapterView.OnItemClickListener { _, _, pos, _ ->
            run {
                val intent = Intent(this, EachProfessorActivity::class.java)
                intent.putExtra("professorsPos", pos)
                startActivity(intent)
            }
        })
        listView.setAdapter(adapter)
    }

    /*##########################*/
    /*####### Subjects #######*/
    /*##########################*/
    private fun loadSubjects(){
        loadAllFromDatabase()

        SubjectsUI.activity = this
        setSchedule(numberOfWeeks, subjects, customSubjects)
        showSubjects()
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

        mCredential = GoogleAccountCredential.usingOAuth2(
                applicationContext, Arrays.asList(*MainActivity.SCOPES))
                .setBackOff(ExponentialBackOff())

        val newTextView = TextView(this)
        val text = ("animation  = ${GlobalSettings.getAnimation(this)}\n"+
                "Дата начала учебы = ${GlobalSettings.getBeginningStudyDate(this)}\n"+
                "Скрывать расписание? = ${GlobalSettings.getHideEmptyDays(this)}\n" +
                "Очистить кэш")
        newTextView.text = text
        activityLayout.addView(newTextView)

        val sv1 = Switch(this)
        val sv2 = Switch(this)
        activityLayout.addView(sv1)
        activityLayout.addView(sv2)
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
                    .setApplicationName("vega_v1")
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
                mLastError = e
                cancel(true)
                return null
            }

        }

        private val dataFromApi: List<String>
            @Throws(IOException::class)
            get() {
                val results = java.util.ArrayList<String>()
                testConnection()
                results.add("Проверка соединения успешно завершена.")
                getProfessorsFromGoogle()
                results.add("Преподователи успешно загрузились.")
                getSubjectsFromGoogle()
                results.add("Предметы успешно загрузились.")
                getCustomSubjectsFromGoogle()
                results.add("Доп. предметы успешно загрузились.")
                return results
            }

        @Throws(IOException::class)
        private fun testConnection(){
            val testspreadsheetId = "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms"
            val testrange = "Class Data!A2:E"
            val testresponse = this.mService!!.spreadsheets().values()
                    .get(testspreadsheetId, testrange)
                    .execute()
            val testvalues = testresponse.getValues()
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
}