package com.dicoding.picodiploma.storyapp.customview

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.dicoding.picodiploma.storyapp.R

class MyPasswordEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {
    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        if (text.toString().length < 8) {
            setError(context.getString(R.string.error_password_invalid), null)
        } else {
            setError(null, null)
        }
    }

    fun getInput(): String {
        return text?.toString() ?: ""
    }

    fun isValid(): Boolean {
        return getInput().length >= 8
    }
}