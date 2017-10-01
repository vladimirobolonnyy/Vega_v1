package com.obolonnyy.vega_v1.loadinfotoscreen

import android.app.Activity
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.androidessence.lib.RichTextView
import com.obolonnyy.vega_v1.R
import com.obolonnyy.vega_v1.app.GlobalSettings
import com.obolonnyy.vega_v1.util.data.MyData
import com.obolonnyy.vega_v1.util.data.MyDateClass
import com.obolonnyy.vega_v1.util.dataobjects.CustomSubjectsWithDate
import com.obolonnyy.vega_v1.util.dataobjects.Subjects
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.textColor
import java.util.*

/**
 * Created by Vladimir Obolonnyy on 16.09.2017.
 */
class SubjectsUI {

    companion object {

        private val LINEARLAYOUTID = 1573
        private val TEXTVIEWID = 3073
        private var whichWeek = 1
        private lateinit var threeWeeksSubjects: Array<Array<String>>
        private lateinit var emptyDays: Array<Int>
        private var currentWeek: Int = 0

        lateinit var activity: Activity

        fun setSchedule(subjects: ArrayList<Subjects>,
                        customSubjects: ArrayList<CustomSubjectsWithDate>) {

            val beginningStudyDate = MyDateClass.dateParse(GlobalSettings.getBeginningStudyDate(activity))
            currentWeek = MyDateClass.getDifferenceInWeeksFromNow(beginningStudyDate)
            val typeOfWeekNow = currentWeek % 2
            val n = MyData.subjectsTime.size * MyData.daysOfWeek.size
            threeWeeksSubjects = Array<Array<String>>(3, { Array<String>(n, { _ -> "" }) })

            for (subj in subjects) {
                val time = subj.timeInt
                val weekDay: Int = subj.dayOfWeekInt
                val subjID: Int = (weekDay+1) * MyData.subjectsTime.size + time

                if (typeOfWeekNow == 1) {
                    if (subj.chislOrZnamen == MyData.CHISLITEL)
                        threeWeeksSubjects[1][subjID] = subj.description
                    else {
                        threeWeeksSubjects[0][subjID] = subj.description
                        threeWeeksSubjects[2][subjID] = subj.description
                    }
                } else {
                    if (subj.chislOrZnamen == MyData.ZNAMENATEL)
                        threeWeeksSubjects[1][subjID] = subj.description
                    else {
                        threeWeeksSubjects[0][subjID] = subj.description
                        threeWeeksSubjects[2][subjID] = subj.description
                    }
                }
            }

            for (subj in customSubjects){
                val currentDate = MyDateClass.dateNow()
                val subjDate = subj.date
                var subjectsWeek = MyDateClass.getDifferenceInWeeks(currentDate, subjDate)

                //костыль от воскресенья
                if (currentDate.dayOfWeekInt == 0)
                    subjectsWeek++

                if (subjectsWeek in -1..1){
                    subjectsWeek++
                    val time = subj.timeInt
                    val weekDay: Int = subj.date.dayOfWeekInt
                    val subjID: Int = (weekDay) * MyData.subjectsTime.size + time
                    threeWeeksSubjects[subjectsWeek][subjID] = subj.description
                }
            }

            // Теперь у нас есть массив предметов на 3 недели.
            findEmptyDays()
            whichWeek = 1
        }


        fun showSubjects(){
            setUpButtons()
            val mainLinearLayout = activity.findViewById(R.id.subjects_linearlayout) as LinearLayout
            val numberDays = MyData.daysOfWeek.size - 1  // убираем воскресенье

            for (i in 1..numberDays){
                val daysTextView = createTitlesTextView(i)
                mainLinearLayout.addView(daysTextView)
                val middlelinearLayout = createMiddlelinearLayout(i)
                mainLinearLayout.addView(middlelinearLayout)
            }
        }

        private fun createMiddlelinearLayout(dayIndex: Int, localHide: Boolean = false): LinearLayout{
            val numberTime = MyData.subjectsTime.size
            val middlelinearLayout = LinearLayout(activity)

            middlelinearLayout.orientation = LinearLayout.VERTICAL
            middlelinearLayout.layoutParams = (ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT))
            middlelinearLayout.id = LINEARLAYOUTID + dayIndex

            if (!localHide) {
                if (GlobalSettings.getHideEmptyDays(activity)) {
                    if (isThisDayIsEmpty(dayIndex)) {
                        middlelinearLayout.visibility = View.GONE
                    }
                }
            }

            for (j in 0 until numberTime){
                val linearLayout = LinearLayout(activity)
                linearLayout.orientation = LinearLayout.HORIZONTAL
                linearLayout.layoutParams = (ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT))

                val timeTextView = createTimesTextView(MyData.subjectsTime[j])
                val descrTextView = createDescriptionTextView(
                        threeWeeksSubjects[whichWeek][dayIndex *numberTime + j], dayIndex *numberTime + j )

                linearLayout.addView(timeTextView)
                linearLayout.addView(descrTextView)
                middlelinearLayout.addView(linearLayout)
            }

            return middlelinearLayout
        }

        private fun updateSubjects(weekNumber: Int, threeWeeksSubjects: Array<Array<String>>){
            val t = MyData.subjectsTime.size
            val d = MyData.daysOfWeek.size - 1 // убираем воскресенье
            val n = t*d
            for (i in t until n){
                val id = TEXTVIEWID + i
                val tv = activity.findViewById(id) as RichTextView
                val text = threeWeeksSubjects[weekNumber][i]
                tv.text = text
                val (start, end) = findNumbers(text)
                if (start != end)
                    tv.colorSpan(start, end, RichTextView.ColorFormatType.FOREGROUND, Color.RED)
            }
        }

        private fun createTitlesTextView(index: Int): TextView {
            val tv = TextView(activity)
            tv.text = MyData.daysOfWeek[index]
            tv.id = index
            tv.setPadding(15,10,0,5)
            tv.setTextColor(R.color.myBlue)
            tv.textSize = 20f
            tv.onClick {
                val linearLayout = activity.findViewById(LINEARLAYOUTID + index)
                if (linearLayout.visibility == 0)
                    linearLayout.visibility = View.GONE
                else
                    linearLayout.visibility = 0
            }
            return tv
        }

        private fun createTimesTextView(text: String): TextView {
            val tv = TextView(activity)
            tv.text = text
            tv.setPadding(15,10,20,10)
            tv.textSize = 12f
            return tv
        }

        private fun createDescriptionTextView(text: String, index: Int): RichTextView {
            val tv = RichTextView(activity)
            tv.text = text
            tv.setPadding(5,0,30,0)
            tv.textSize = 13f
            tv.id = TEXTVIEWID + index

            val (start, end) = findNumbers(text)
            if (start != end)
                tv.colorSpan(start, end, RichTextView.ColorFormatType.FOREGROUND, Color.RED)
            return tv
        }

        private fun isThisDayIsEmpty(dayIndex: Int) = (emptyDays[dayIndex] == 0)

        private fun findEmptyDays() {
            val result = arrayOf(0,0,0,0,0,0,0)
            val days = 7
            val times = MyData.subjectsTime.size

            for (i in 0 until days){
                for (j in 0 until times){
                    if (threeWeeksSubjects[0][i*times + j] != "") {
                        result[i]++
                        break
                    }
                    if (threeWeeksSubjects[1][i*times + j] != "") {
                        result[i]++
                        break
                    }
                }
            }
            emptyDays = result
        }

        private fun setUpButtons(){
            val buttons = arrayListOf(activity.findViewById(R.id.subject_previousweek),
            activity.findViewById(R.id.subject_currentweek),
            activity.findViewById(R.id.subject_nextweek))

            var i = -1
            for (each in buttons){
                val btn = each as Button
                setEnableAndBlue(btn)
                btn.text = ((currentWeek + i).toString() + " неделя")
                btn.setPadding(20,0,20,0)
                i++
            }

            setDisableAndGray(buttons[whichWeek])

            buttons[0].setOnClickListener {
                whichWeek = 0
                setDisableAndGray(buttons[0])
                setEnableAndBlue(buttons[1])
                setEnableAndBlue(buttons[2])
                updateSubjects(0, threeWeeksSubjects)
            }
            buttons[1].setOnClickListener {
                whichWeek = 1
                setDisableAndGray(buttons[1])
                setEnableAndBlue(buttons[0])
                setEnableAndBlue(buttons[2])
                updateSubjects(1, threeWeeksSubjects)
            }
            buttons[2].setOnClickListener {
                whichWeek = 2
                setDisableAndGray(buttons[2])
                setEnableAndBlue(buttons[0])
                setEnableAndBlue(buttons[1])
                updateSubjects(2, threeWeeksSubjects)
            }
        }

        private fun setDisableAndGray(button: View){
            button.backgroundColor = Color.parseColor(activity.getString(R.color.myBlue))
            button.isEnabled = false
//            (button as Button).textColor = Color.parseColor(activity.getString(R.color.myGold))
        }

        private fun setEnableAndBlue(button: View){
            button.backgroundColor = Color.parseColor(activity.getString(R.color.colorPrimary))
            button.isEnabled = true
            (button as Button).textColor = Color.parseColor(activity.getString(R.color.myWhite))
        }

        private fun findNumbers(text: String): Pair<Int, Int> {
            var start = 0
            while (start < text.length && !Character.isDigit(text[start])) {
                start++
            }
            var end = start
            while (end < text.length && Character.isDigit(text[end])){
                end++
            }
            // увеличим на еще одну букву, чтобы всякие аудитории типа 428ю были закрашены
            if ((end > 0) and (end < text.length))
                end++
            if ((start != 0) and (end != 0))
                return Pair(start, (end))
            else
                return (Pair(0,0))
        }

        fun createTodayScrollView(): ScrollView {
            val today = MyDateClass.dateNow()
            val dayIndex = today.dayOfWeekInt
            val daysTextView = SubjectsUI.createTitlesTextView(dayIndex)
            val middleLinearLayout = createMiddlelinearLayout(dayIndex, true)

            val mainLinearLayout = LinearLayout(activity)
            mainLinearLayout.orientation = LinearLayout.VERTICAL
            mainLinearLayout.layoutParams = (ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT))

            mainLinearLayout.addView(daysTextView)
            mainLinearLayout.addView(middleLinearLayout)

            val scrollview = ScrollView(activity)
            scrollview.addView(mainLinearLayout)

            return scrollview
        }
    }
}