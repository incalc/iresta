package io.github.incalc.iresta.utils

import java.text.DateFormat
import java.util.*


object DateUtils {
    fun getDaysOfWeek(month: Int, date: Int): List<Date> {
        val days = mutableListOf<Date>()
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, 2019)
        calendar.set(Calendar.MONTH, month - 1)
        calendar.set(Calendar.DATE, date)
        days += calendar.time
        for (i in 0..6) {
            calendar.add(Calendar.DATE, 1)
            days += calendar.time
        }
        return days
    }

    fun format(date: Date): String {
        return DateFormat.getDateInstance(DateFormat.FULL).format(date)
    }

    fun formatAll(dates: List<Date>): List<String> {
        return dates.map { format(it) }
    }
}
