package com.metacomputing.namespring.ui.utils

import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.FloatRange
import com.metacomputing.namespring.R
import androidx.core.view.isGone
import androidx.core.view.isVisible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ProgressManager {
    enum class Type {
        MAIN,
        BACKGROUND
    }

    interface ProgressProvider {
        fun onLoadMain(): ViewGroup
        fun onLoadBackground(): ViewGroup
    }

    class ProgressHandler(
        private val containerProvider: () -> ViewGroup,
        private val spinnerImageViewProvider: () -> ImageView,
        private val textViewProvider: () -> TextView?
    ) {
        private val stateMap: HashMap<String, Boolean> = HashMap()
        private val showState: Boolean
            get() {
                return stateMap.filter { v -> v.value }.isNotEmpty()
            }

        fun show(caller: String = "", content: String = "", progression: Float? = null, enable: Boolean = true) {
            CoroutineScope(Dispatchers.Main).launch {
                stateMap[caller] = enable
                containerProvider.invoke().run {
                    if (showState) {
                        if (isGone) {
                            visibility = View.VISIBLE
                            playLoadAnimation(spinnerImageViewProvider.invoke(), true)
                        }
                        textViewProvider.invoke()?.text = buildContentString(content, progression)
                    } else {
                        if (isVisible) {
                            playLoadAnimation(spinnerImageViewProvider.invoke(), false)
                        }
                        visibility = View.GONE
                    }
                }
            }
        }

        private fun buildContentString(content: String, progression: Float? = null): String {
            var ret = "$contentHeaderString $content"
            progression?.run {
                ret += " (${(progression * 100).toInt()}%)"
            }
            return ret
        }

        private fun playLoadAnimation(spinnerImageView: ImageView, play: Boolean = true) {
            if (play) {
                spinnerImageView.startAnimation(
                    RotateAnimation(0f, 360f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f
                    ).apply {
                        duration = 600
                        repeatCount = Animation.INFINITE
                        interpolator = LinearInterpolator()
                    }
                )
            } else {
                spinnerImageView.clearAnimation()
            }
        }
    }

    private lateinit var rootViewProvider: ProgressProvider
    private var contentHeaderString: String? = null
    private var mainProgressHandler: ProgressHandler? = null
    private var backgroundProgressHandler: ProgressHandler? = null

    fun initialize(viewProvider: ProgressProvider) {
        rootViewProvider = viewProvider.also {
            contentHeaderString = it.onLoadMain().resources.getString(R.string.loading)
        }
        mainProgressHandler = ProgressHandler(
            containerProvider = rootViewProvider::onLoadMain,
            spinnerImageViewProvider = { rootViewProvider.onLoadMain().findViewById(R.id.main_spinner) },
            textViewProvider = { rootViewProvider.onLoadMain().findViewById(R.id.main_spinner_description) },
        )
        backgroundProgressHandler = ProgressHandler(
            containerProvider = rootViewProvider::onLoadBackground,
            spinnerImageViewProvider = { rootViewProvider.onLoadBackground().findViewById(R.id.background_spinner) },
            textViewProvider = { null },
        )
    }

    fun show(type: Type, caller: String = "", content: String = "",
             @FloatRange(from =0.0, to= 1.0) progression: Float? = null,
             enable: Boolean = true) {
        CoroutineScope(Dispatchers.Main).launch {
            when (type) {
                Type.BACKGROUND -> backgroundProgressHandler?.show(caller, content, progression, enable)
                Type.MAIN -> mainProgressHandler?.show(caller, content, progression, enable)
            }
        }
    }

    fun hide(type: Type, caller: String) {
        show(type, caller, enable = false)
    }
}