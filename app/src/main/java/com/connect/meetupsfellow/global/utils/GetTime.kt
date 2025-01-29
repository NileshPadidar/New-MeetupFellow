package com.connect.meetupsfellow.global.utils

import com.connect.meetupsfellow.constants.Constants

object GetTime {
    fun convertTime(seconds: Long): String {
        var milliseconds = seconds
        val secondsInMilli = 1000L
        val minutesInMilli = secondsInMilli * 60
        val hoursInMilli = minutesInMilli * 60
        val daysInMilli = hoursInMilli * 24

        val elapsedDays = milliseconds / daysInMilli
        milliseconds %= daysInMilli

        val elapsedHours = milliseconds / hoursInMilli
        milliseconds %= hoursInMilli

        val elapsedMinutes = milliseconds / minutesInMilli
        milliseconds %= minutesInMilli

        val elapsedSeconds = milliseconds / secondsInMilli

        return when {
            elapsedDays > 0 -> countDays(elapsedDays)
            elapsedHours == 1.toLong() -> "$elapsedHours hr ago"
            elapsedHours > 1 -> "$elapsedHours hrs ago"
            elapsedMinutes == 1.toLong() -> "$elapsedMinutes min ago"
            elapsedMinutes > 1 -> "$elapsedMinutes mins ago"
            else -> "$elapsedSeconds Just now"
//            else -> "Just now"
        }
    }

    private fun countDays(elapsedDays: Long): String {
        when (range(elapsedDays.toInt())) {

            Constants.Time.DAY -> return "Yesterday"

            Constants.Time.DAYS -> return "$elapsedDays days ago"

            Constants.Time.WEEK_ONE -> return "1 week ago"

            Constants.Time.WEEK_TWO -> return "2 weeks ago"

            Constants.Time.WEEK_THREE -> return "3 weeks ago"

            Constants.Time.ONE_MONTH -> return "1 month ago"

            Constants.Time.TWO_MONTH -> return "2 months ago"

            Constants.Time.THREE_MONTH -> return "3 months ago"

            Constants.Time.FOUR_MONTH -> return "4 months ago"

            Constants.Time.FIVE_MONTH -> return "5 months ago"

            Constants.Time.SIX_MONTH -> return "6 months ago"

            Constants.Time.SEVEN_MONTH -> return "7 months ago"

            Constants.Time.EIGHT_MONTH -> return "8 months ago"

            Constants.Time.NINE_MONTH -> return "9 months ago"

            Constants.Time.TEN_MONTH -> return "10 months ago"

            Constants.Time.ELEVEN_MONTH -> return "11 months ago"

            Constants.Time.TWELVE_MONTH -> return "1 year ago"

            else -> return "more than year"
        }
    }

    private fun range(days: Int): Int = when {
        days <= 1 -> Constants.Time.DAY
        days in 2..7 -> Constants.Time.DAYS
        days in 7..13 -> Constants.Time.WEEK_ONE
        days in 14..20 -> Constants.Time.WEEK_TWO
        days in 21..29 -> Constants.Time.WEEK_THREE
        days in 31..59 -> Constants.Time.ONE_MONTH
        days in 61..89 -> Constants.Time.TWO_MONTH
        days in 91..119 -> Constants.Time.THREE_MONTH
        days in 121..149 -> Constants.Time.FOUR_MONTH
        days in 151..179 -> Constants.Time.FIVE_MONTH
        days in 181..209 -> Constants.Time.SIX_MONTH
        days in 211..239 -> Constants.Time.SEVEN_MONTH
        days in 241..269 -> Constants.Time.EIGHT_MONTH
        days in 271..299 -> Constants.Time.NINE_MONTH
        days in 301..329 -> Constants.Time.TEN_MONTH
        days in 331..359 -> Constants.Time.ELEVEN_MONTH
        else -> Constants.Time.TWELVE_MONTH
    }

}