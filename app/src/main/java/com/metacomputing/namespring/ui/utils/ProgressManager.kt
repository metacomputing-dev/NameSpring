package com.metacomputing.namespring.ui.utils

import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.FloatRange
import com.metacomputing.namespring.R
import androidx.core.view.isGone
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ProgressManager {
    enum class Type {
        MAIN,
        BACKGROUND
    }
    private var rootViewProvider: (() -> LinearLayout)? = null
    private var mainStateMap = HashMap<String, Boolean>()
    private var backgroundStateMap = HashMap<String, Boolean>()
    private var contentHeaderString: String? = null
    val mainTypeShowState: Boolean
        get() {
            return mainStateMap.filter { v -> v.value }.isNotEmpty()
        }
    val isBackgroundTypeShowing: Boolean
        get() {
            return backgroundStateMap.filter { v -> v.value }.isNotEmpty()
        }

    fun initialize(viewProvider: (() -> LinearLayout)) {
        rootViewProvider = viewProvider.also {
            contentHeaderString = it.invoke().resources.getString(R.string.loading)
        }
    }

    fun show(type: Type, caller: String = "", content: String = "",
             @FloatRange(from =0.0, to= 1.0) progression: Float? = null,
             enable: Boolean = true) {
        when (type) {
            Type.BACKGROUND -> {

            }
            Type.MAIN -> {
                CoroutineScope(Dispatchers.Main).launch {
                    mainStateMap[caller] = enable
                    rootViewProvider?.invoke()?.run {
                        if (mainTypeShowState) {
                            if (isGone) {
                                visibility = View.VISIBLE
                                findViewById<ImageView>(R.id.main_spinner).startAnimation(
                                    RotateAnimation(0f, 360f,
                                        Animation.RELATIVE_TO_SELF, 0.5f,
                                        Animation.RELATIVE_TO_SELF, 0.5f
                                    ).apply {
                                        duration = 600
                                        repeatCount = Animation.INFINITE
                                        interpolator = LinearInterpolator()
                                    }
                                )
                            }
                            findViewById<TextView>(R.id.main_spinner_description).text = buildContentString(content, progression)
                        } else {
                            visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    fun hide(type: Type, caller: String) {
        show(type, caller, enable = false)
    }

    private fun buildContentString(content: String, progression: Float? = null): String {
        var ret = "$contentHeaderString $content"
        progression?.run {
            ret += " (${(progression * 100).toInt()}%)"
        }
        return ret
    }
}