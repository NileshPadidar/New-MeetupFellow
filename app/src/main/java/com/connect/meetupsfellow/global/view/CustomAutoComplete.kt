package com.connect.meetupsfellow.global.view

import android.content.Context
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import android.util.AttributeSet

/**
 * Created by Jammwal on 26-03-2018.
 */
class CustomAutoComplete : AppCompatAutoCompleteTextView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun replaceText(text: CharSequence) {

    }

    override fun dismissDropDown() {

    }
}