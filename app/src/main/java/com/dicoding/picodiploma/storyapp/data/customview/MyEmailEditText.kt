package com.dicoding.picodiploma.storyapp.data.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.dicoding.picodiploma.storyapp.R

class MyEmailEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {
    private val regexEmail = "^[A-Za-z0-9+_.-]+@(.+)\$".toRegex()

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No Action
            }

            override fun onTextChanged(
                text: CharSequence?,
                start: Int,
                lengthBefore: Int,
                lengthAfter: Int
            ) {
                if (!regexEmail.matches(text.toString())) {
                    error = context.getString(R.string.error_email_invalid)
                } else {
                    setError(null, null)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // No Action
            }
        })
    }
}