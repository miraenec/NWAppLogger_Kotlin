package com.nextweb.nwapplogger.utils

import java.text.SimpleDateFormat
import java.util.Calendar

/**
 * @author nextweb
 *
 */
class CalendarUtil {

    companion object {
        val instance = CalendarUtil()
    }

    fun currentHour(): Int {
        val oCalendar = Calendar.getInstance()
        return oCalendar[Calendar.HOUR_OF_DAY]
    }

    fun currentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        return dateFormat.format(calendar.time)
    }

    fun currentTime(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return dateFormat.format(calendar.time)
    }

    fun currentTime2(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("HH:mm:ss")
        return dateFormat.format(calendar.time)
    }

    fun currentTimeForFileName(second: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.SECOND, second)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd-HHmm")
        return dateFormat.format(calendar.time)
    }

    fun currentTime(second: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.SECOND, second)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return dateFormat.format(calendar.time)
    }

    fun getTime(time: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return if (time == 0L) "" else dateFormat.format(calendar.time)
    }

    fun currentTimeForFileName(time: Long, second: Int): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        calendar.add(Calendar.SECOND, second)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd-HHmm")
        return dateFormat.format(calendar.time)
    }

    fun currentTime(time: Long, second: Int): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        calendar.add(Calendar.SECOND, second)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return dateFormat.format(calendar.time)
    }

    fun currentMinute(): Int {
        val oCalendar = Calendar.getInstance()
        return oCalendar[Calendar.MINUTE]
    }

    fun currentSecond(): Int {
        val oCalendar = Calendar.getInstance()
        return oCalendar[Calendar.SECOND]
    }

    fun currentMilliSecond(): Int {
        val oCalendar = Calendar.getInstance()
        return oCalendar[Calendar.MILLISECOND]
    }
}
