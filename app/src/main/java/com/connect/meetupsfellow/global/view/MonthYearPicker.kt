package com.connect.meetupsfellow.global.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.view.View
import android.widget.NumberPicker
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.R
import java.util.*

@Suppress("NAME_SHADOWING", "unused")
@SuppressLint("InflateParams")
/**
 * Instantiates a new month year picker.
 *
 * @param activity the activity
 */
class MonthYearPicker(private val activity: Activity, private val pickerType: String) {

    private var view: View? = null
    private var builder: AlertDialog.Builder? = null
    private var pickerDialog: AlertDialog? = null
    private var build = false
    private var dayNumberPicker: NumberPicker? = null
    private var monthNumberPicker: NumberPicker? = null
    private var yearNumberPicker: NumberPicker? = null

    /**
     * Gets the current year.
     *
     * @return the current year
     */
    var currentYear: Int = 0
        private set

    /**
     * Gets the current month.
     *
     * @return the current month
     */
    var currentMonth: Int = 0
        private set

    /**
     * Gets the current month.
     *
     * @return the current month
     */
    var currentDay: Int = 0
        private set

    /**
     * Gets the selected month.
     *
     * @return the selected month
     */
    val selectedDay: Int
        get() = dayNumberPicker!!.value

    /**
     * Gets the selected month.
     *
     * @return the selected month
     */
    val selectedMonth: Int
        get() = monthNumberPicker!!.value

    /**
     * Gets the selected month name.
     *
     * @return the selected month name
     */
    val selectedMonthName: String
        get() = MONTHS[monthNumberPicker!!.value]

    /**
     * Gets the selected month name.
     *
     * @return the selected month short name i.e Jan, Feb ...
     */
    val selectedMonthShortName: String
        get() = PICKER_DISPLAY_MONTHS_NAMES[monthNumberPicker!!.value]

    /**
     * Gets the selected year.
     *
     * @return the selected year
     */
    val selectedYear: Int
        get() = yearNumberPicker!!.value


    init {


        this.view = when (pickerType) {
            Constants.DatePicker.Day -> {
                activity.layoutInflater.inflate(R.layout.day_picker, null)
            }

            Constants.DatePicker.Month -> {
                activity.layoutInflater.inflate(R.layout.month_picker, null)
            }

            else -> {
                activity.layoutInflater.inflate(R.layout.year_picker, null)
            }
        }

    }

    /**
     * Builds the month year alert dialog.
     *
     * @param positiveButtonListener the positive listener
     * @param negativeButtonListener the negative listener
     */
    fun build(positiveButtonListener: DialogInterface.OnClickListener, negativeButtonListener: DialogInterface.OnClickListener) {
        this.build(-1, positiveButtonListener, negativeButtonListener)
    }

    @Suppress("UNUSED_PARAMETER")
            /**
     * Builds the month year alert dialog.
     *
     * @param selectedMonth          the selected month 0 to 11 (sets current moth if invalid
     * value)
     * @param selectedYear           the selected year 1970 to 2099 (sets current year if invalid
     * value)
     * @param positiveButtonListener the positive listener
     * @param negativeButtonListener the negative listener
     */
    fun build(selectedDate: Int, positiveButtonListener: DialogInterface.OnClickListener,
              negativeButtonListener: DialogInterface.OnClickListener) {

        builder = AlertDialog.Builder(activity)
        builder!!.setView(view)

        when (pickerType) {
            Constants.DatePicker.Day -> {
                setupDatePicker(Calendar.getInstance(), previousDate)
            }

            Constants.DatePicker.Month -> {
                setupMonthPicker(Calendar.getInstance(), previousMonth)
            }

            else -> {
                if (!this.build) {
                    setupYearPicker(Calendar.getInstance(), previousYear)
                }
            }
        }

        builder!!.setTitle(getTitle())

        builder!!.setPositiveButton(activity.getString(R.string.positive_button_text), positiveButtonListener)
        builder!!.setNegativeButton(activity.getString(R.string.negative_button_text), negativeButtonListener)
        build = true
        pickerDialog = builder!!.create()

    }

    private fun setupDatePicker(instance: Calendar, selectedDate: Int) {
        var selectedDate = selectedDate
        currentDay = instance.get(Calendar.DAY_OF_MONTH)
        instance.set(previousYear, previousMonth, previousDate)
        val days = instance.getActualMaximum(Calendar.DAY_OF_MONTH)

        if (selectedDate > days || selectedDate < 0) {
            selectedDate = currentDay
        }


        if (selectedDate == 0) {
            selectedDate = currentDay
        }

        dayNumberPicker = view!!.findViewById<View>(R.id.dayNumberPicker) as NumberPicker

        dayNumberPicker!!.minValue = 1
        dayNumberPicker!!.maxValue = days

        dayNumberPicker!!.value = selectedDate

        dayNumberPicker!!.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
    }

    private fun setupMonthPicker(instance: Calendar, selectedMonth: Int) {
        var selectedMnth = selectedMonth
        currentMonth = instance.get(Calendar.MONTH)

        if (selectedMnth > 11 || selectedMnth < -1) {
            selectedMnth = currentMonth
        }


        if (selectedMnth == -1) {
            selectedMnth = currentMonth
        }

        monthNumberPicker = view!!.findViewById<View>(R.id.monthNumberPicker) as NumberPicker
        monthNumberPicker!!.displayedValues = PICKER_DISPLAY_MONTHS_NAMES

        monthNumberPicker!!.minValue = 0
        monthNumberPicker!!.maxValue = MONTHS.size - 1

        monthNumberPicker!!.value = selectedMnth

        monthNumberPicker!!.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
    }

    private fun setupYearPicker(instance: Calendar, selectedYear: Int) {
        var selectedYear = selectedYear
        if (!this.build) {
            currentYear = instance.get(Calendar.YEAR)

            if (selectedYear < MIN_YEAR || selectedYear > MAX_YEAR) {
                selectedYear = currentYear
            }

            if (selectedYear == -1) {
                selectedYear = currentYear
            }

            yearNumberPicker = view!!.findViewById<View>(R.id.yearNumberPicker) as NumberPicker
            yearNumberPicker!!.minValue = MIN_YEAR
            yearNumberPicker!!.maxValue = MAX_YEAR

            yearNumberPicker!!.value = selectedYear

            yearNumberPicker!!.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        }
    }

    private fun getTitle(): String {
        return when (pickerType) {
            Constants.DatePicker.Day -> {
                activity.getString(R.string.alert_dialog_title_date)
            }

            Constants.DatePicker.Month -> {
                activity.getString(R.string.alert_dialog_title_month)
            }

            else -> {
                activity.getString(R.string.alert_dialog_title_year)
            }
        }
    }

    /**
     * Show month year picker dialog.
     */
    fun show() {
        if (build) {
            pickerDialog!!.show()
        } else {
            throw IllegalStateException("Build picker before use")
        }
    }

    fun setDate(selectedDate: Int, selectedMonth: Int, selectedYear: Int) {
        previousDate = selectedDate
        previousMonth = selectedMonth
        previousYear = selectedYear
    }

    /**
     * Sets the month value changed listener.
     *
     * @param valueChangeListener the new month value changed listener
     */
    fun setMonthValueChangedListener(valueChangeListener: NumberPicker.OnValueChangeListener) {
        monthNumberPicker!!.setOnValueChangedListener(valueChangeListener)
    }

    /**
     * Sets the month value changed listener.
     *
     * @param valueChangeListener the new month value changed listener
     */
    fun setDayValueChangedListener(valueChangeListener: NumberPicker.OnValueChangeListener) {
        dayNumberPicker!!.setOnValueChangedListener(valueChangeListener)
    }

    /**
     * Sets the year value changed listener.
     *
     * @param valueChangeListener the new year value changed listener
     */
    fun setYearValueChangedListener(valueChangeListener: NumberPicker.OnValueChangeListener) {
        yearNumberPicker!!.setOnValueChangedListener(valueChangeListener)
    }

    /**
     * Sets the month wrap selector wheel.
     *
     * @param wrapSelectorWheel the new month wrap selector wheel
     */
    fun setDayWrapSelectorWheel(wrapSelectorWheel: Boolean) {
        dayNumberPicker!!.wrapSelectorWheel = wrapSelectorWheel
    }

    /**
     * Sets the month wrap selector wheel.
     *
     * @param wrapSelectorWheel the new month wrap selector wheel
     */
    fun setMonthWrapSelectorWheel(wrapSelectorWheel: Boolean) {
        monthNumberPicker!!.wrapSelectorWheel = wrapSelectorWheel
    }

    /**
     * Sets the year wrap selector wheel.
     *
     * @param wrapSelectorWheel the new year wrap selector wheel
     */
    fun setYearWrapSelectorWheel(wrapSelectorWheel: Boolean) {
        yearNumberPicker!!.wrapSelectorWheel = wrapSelectorWheel
    }

    companion object {

        private val year = Calendar.getInstance().get(Calendar.YEAR)
        private val MIN_YEAR = year - 70

        private val MAX_YEAR = year - 18

        private var previousDate = 0
        private var previousMonth = 0
        private var previousYear = 0

        private val PICKER_DISPLAY_MONTHS_NAMES = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

        private val MONTHS = arrayOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12")
    }

}
