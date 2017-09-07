package com.obolonnyy.vega_v1.util.viewspages

import com.obolonnyy.vega_v1.util.dataobjects.Professors

/**
 * Created by Владимир on 31.08.2017.
 */
class ProfessorsPage {

    companion object {

        fun parseProfessors(values: List<List<Any>>): ArrayList<Professors>{
            var result = ArrayList<Professors>()
            var idx = 0

            for (row in values) {

                var FIO: String = ""
                var scienceDegree: String = ""
                var email: String = ""
                var phone: String = ""
                var comment: String = ""

                if (row.size >= 1) {
                    FIO = row[0].toString()
                    if (row.size >= 2) {
                        scienceDegree = row[1].toString()
                        if (row.size >= 3) email = row[2].toString()
                        if (row.size >= 4) phone = row[3].toString()
                        if (row.size == 5) comment = row[4].toString()
                    }
                }
                result.add(Professors(idx, FIO, scienceDegree, email, phone, comment))
                idx++
            }
            return result
        }
    }
}