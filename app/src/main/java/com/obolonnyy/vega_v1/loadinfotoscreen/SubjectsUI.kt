package com.obolonnyy.vega_v1.loadinfotoscreen

import android.app.Activity
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.obolonnyy.vega_v1.R
import com.obolonnyy.vega_v1.app.GlobalSettings
import com.obolonnyy.vega_v1.util.data.MyData
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
        private var witchWeek = 1
        private lateinit var threeWeeksSubjects: Array<Array<String>>
        private lateinit var emptyDays: Array<Int>

        lateinit var activity: Activity

        fun setSchedule(numberOfWeeks: Int, subjects: ArrayList<Subjects>,
                        customSubjects: ArrayList<CustomSubjectsWithDate>) {
            val typeOfWeekNow = numberOfWeeks % 2
            val n = MyData.subjectsTime.size * MyData.daysOfWeek.size
            threeWeeksSubjects = Array<Array<String>>(3, { Array<String>(n, { _ -> "" }) })

            for (subj in subjects) {
                val time = subj.timeInt
                val weekDay: Int = subj.dayOfWeekInt
                val subjID: Int = (weekDay) * MyData.subjectsTime.size + time

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

            }

            // Теперь у нас есть массив предметов на 3 недели.
            // ToDo добавить кастомные предметы
            // ToDo добавить настройку по автоматическому скрытию пустых дней
            findEmptyDays()
            witchWeek = 1
        }

        fun showSubjects(){
            setUpButtons()
            val mainLinearLayout = activity.findViewById(R.id.subjects_linearlayout) as LinearLayout
            val numberTime = MyData.subjectsTime.size
            val numberDays = MyData.daysOfWeek.size - 1 // убираем воскресенье

            for (i in 0 until numberDays){
                // День недели
                val daysTextView = createTitlesTextView(i)
                mainLinearLayout.addView(daysTextView)

                val middlelinearLayout = createMiddlelinearLayout(i)

                for (j in 0 until numberTime){
                    val linearLayout = LinearLayout(activity)
                    linearLayout.orientation = LinearLayout.HORIZONTAL
                    linearLayout.layoutParams = (ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT))

                    val timeTextView = createTimesTextView(MyData.subjectsTime[j])
                    val descrTextView = createDescriptionTextView(
                            threeWeeksSubjects[witchWeek][i*numberTime + j],i*numberTime + j )

                    linearLayout.addView(timeTextView)
                    linearLayout.addView(descrTextView)
                    middlelinearLayout.addView(linearLayout)
                }
                mainLinearLayout.addView(middlelinearLayout)
            }
        }

        private fun updateSubjects(weekNumber: Int, threeWeeksSubjects: Array<Array<String>>){
            val t = MyData.subjectsTime.size -1
            val d = MyData.daysOfWeek.size - 2 // убираем воскресенье
            val n = t*d
            for (i in 0 until n){
                val id = TEXTVIEWID + i
                val tv = activity.findViewById(id) as TextView
                tv.text = threeWeeksSubjects[weekNumber][i]
            }
        }

        private fun createTitlesTextView(index: Int): TextView {
            val tv = TextView(activity)
            tv.text = MyData.daysOfWeek[index+1]
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
            tv.setPadding(10,10,10,10)
            tv.textSize = 12f
            return tv
        }

        private fun createDescriptionTextView(text: String, index: Int): TextView {
            val tv = TextView(activity)
            tv.text = text
            tv.setPadding(5,0,30,0)
            tv.textSize = 13f
            tv.id = TEXTVIEWID + index
            return tv
        }

        private fun createMiddlelinearLayout(index: Int): LinearLayout{
            val middlelinearLayout = LinearLayout(activity)
            middlelinearLayout.orientation = LinearLayout.VERTICAL
            middlelinearLayout.layoutParams = (ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT))
            middlelinearLayout.id = LINEARLAYOUTID + index

            if (GlobalSettings.getHideEmptyDays(activity)) {
                if (isThisDayIsEmpty(index)) {
                    middlelinearLayout.visibility = View.GONE
                }
            }
            return middlelinearLayout
        }

        private fun isThisDayIsEmpty(dayIndex: Int) = (emptyDays[dayIndex] == 0)

        private fun findEmptyDays() {
            val result = arrayOf(0,0,0,0,0,0)
            val days = 6
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

            for (each in buttons){
                val btn = each as Button
                setEnableAndBlue(btn)
                btn.setPadding(20,0,20,0)
            }

            setDisableAndGray(buttons[witchWeek])

            buttons[0].setOnClickListener {
                witchWeek = 0
                setDisableAndGray(buttons[0])
                setEnableAndBlue(buttons[1])
                setEnableAndBlue(buttons[2])
                updateSubjects(0, threeWeeksSubjects)
            }
            buttons[1].setOnClickListener {
                witchWeek = 1
                setDisableAndGray(buttons[1])
                setEnableAndBlue(buttons[0])
                setEnableAndBlue(buttons[2])
                updateSubjects(1, threeWeeksSubjects)
            }
            buttons[2].setOnClickListener {
                witchWeek = 2
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
    }
}