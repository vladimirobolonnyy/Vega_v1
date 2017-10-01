package com.obolonnyy.vega_v1.util.viewspages

import com.obolonnyy.vega_v1.util.data.MyData
import com.obolonnyy.vega_v1.util.data.MyData.Companion.ZNAMENATEL
import com.obolonnyy.vega_v1.util.dataobjects.CustomSubjects
import com.obolonnyy.vega_v1.util.dataobjects.Subjects

/**
 * Created by Владимир on 03.09.2017.
 */
class SubjectsPage {

    companion object {

        fun parseSubjects (values: List<List<Any>>): ArrayList<Subjects>{

            var result = ArrayList<Subjects>()
            var idx = 0

            for (row in values) {

                var time: String = ""
                var dayOfWeek: Int = 0
                var chislOrZnamen: String = ""
                var description: String = ""

                if (row.size >= 2) {
                    time = row[0].toString()
                    dayOfWeek = idx / 7
                    chislOrZnamen = MyData.CHISLITEL
                    description = row[1].toString()
                    if (description != ""){
                        result.add(Subjects(id = idx, time = time, chislOrZnamen = chislOrZnamen,
                                dayOfWeekInt = dayOfWeek, description = description))
                    }

                    if (row.size == 3){
                        time = row[0].toString()
                        dayOfWeek = idx / 7
                        chislOrZnamen = ZNAMENATEL
                        description = row[2].toString()
                        if (description != ""){
                            result.add(Subjects(id = idx, time = time, chislOrZnamen = chislOrZnamen,
                                    dayOfWeekInt = dayOfWeek, description = description))
                        }
                    }
                }
                idx++
            }
            return result
    }

        fun parseCustomSubjects(values: List<List<Any>>): ArrayList<CustomSubjects> {
            var result = ArrayList<CustomSubjects>()
            var idx = 0

            var time: String = ""
            var description: String = ""
            var date: String = ""

            for (row in values) {
                if (row.size == 3) {
                    time = row[0].toString()
                    date = row[1].toString()
                    description = row[2].toString()
                    result.add(CustomSubjects(id = idx, time = time, stringDate = date, description = description))
                }
                idx++
            }
            return result
        }
    }
}