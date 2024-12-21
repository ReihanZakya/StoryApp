package com.dicoding.aplikasistoryapp.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.dicoding.aplikasistoryapp.R

class MyEditText @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : AppCompatEditText(context, attrs), View.OnTouchListener {

    private var clearButtonImage: Drawable
    private var errorTextView: AppCompatTextView? = null
    private var validationType: ValidationType = ValidationType.NONE

    init {
        clearButtonImage = ContextCompat.getDrawable(context, R.drawable.ic_close_24) as Drawable
        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) showClearButton() else hideClearButton()
                validateInput(s.toString())
            }

            override fun afterTextChanged(s: Editable) {
                // Do nothing.
            }
        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = context.getString(R.string.fill_field)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    private fun showClearButton() {
        setButtonDrawables(endOfTheText = clearButtonImage)
    }

    private fun hideClearButton() {
        setButtonDrawables()
    }

    private fun setButtonDrawables(startOfTheText: Drawable? = null, topOfTheText:Drawable? = null, endOfTheText:Drawable? = null, bottomOfTheText: Drawable? = null){
        setCompoundDrawablesWithIntrinsicBounds(startOfTheText, topOfTheText, endOfTheText, bottomOfTheText)
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val clearButtonStart: Float
            val clearButtonEnd: Float
            var isClearButtonClicked = false

            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                clearButtonEnd = (clearButtonImage.intrinsicWidth + paddingStart).toFloat()
                when {
                    event.x < clearButtonEnd -> isClearButtonClicked = true
                }
            } else {
                clearButtonStart = (width - paddingEnd - clearButtonImage.intrinsicWidth).toFloat()
                when {
                    event.x > clearButtonStart -> isClearButtonClicked = true
                }
            }
            if (isClearButtonClicked) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        clearButtonImage = ContextCompat.getDrawable(context,   R.drawable.ic_close_24) as Drawable
                        showClearButton()
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        clearButtonImage = ContextCompat.getDrawable(context, R.drawable.ic_close_24) as Drawable
                        when {
                            text != null -> text?.clear()
                        }
                        hideClearButton()
                        return true
                    }
                    else -> return false
                }
            } else return false
        }
        return false
    }

    private fun validateInput(input: String) {
        val errorMessage = when (validationType) {
            ValidationType.NAME -> if (input.isEmpty()) context.getString(R.string.name_empty) else null
            ValidationType.EMAIL -> if (input.isEmpty()) {
                context.getString(R.string.email_empty)
            } else if (!Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
                context.getString(R.string.email_invalid)
            } else null
            ValidationType.PASSWORD -> if (input.isEmpty()) {
                context.getString(R.string.password_empty)
            } else if (input.length < 8) {
                context.getString(R.string.password_less_than_8)
            } else null
            ValidationType.NONE -> null
        }
        setCustomError(errorMessage)
    }

    fun setCustomError(message: String?) {
        errorTextView?.apply {
            text = message
            visibility = if (message.isNullOrEmpty()) View.GONE else View.VISIBLE
        }
    }

    fun attachErrorTextView(textView: AppCompatTextView) {
        errorTextView = textView
    }

    fun setValidationType(type: ValidationType) {
        validationType = type
    }

    enum class ValidationType {
        NONE, NAME, EMAIL, PASSWORD
    }
}