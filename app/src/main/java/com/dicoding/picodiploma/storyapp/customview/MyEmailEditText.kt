package com.dicoding.picodiploma.storyapp.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText
import com.dicoding.picodiploma.storyapp.R

class MyEmailEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

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
                if (text.isNullOrEmpty() || !Patterns.EMAIL_ADDRESS.matcher(text).matches()) {
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

    fun getInput(): String {
        return text?.toString() ?: ""
    }

    fun isValid(): Boolean {
        return error.isNullOrEmpty()
    }
}