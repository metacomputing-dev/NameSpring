package com.metacomputing.namespring.ui.utils

import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import com.metacomputing.namespring.control.UserDataManager
import com.metacomputing.namespring.model.report.NamingReport
import com.metacomputing.namespring.model.token.ComponentDetailToken

class ToggleCard(
    val card: ViewGroup,
    val toggle: View,
    val detail: View,
    val namingReport: NamingReport,
    val componentIdx: Int,
    hideOnInit: Boolean = true
) {
    init {
        if (hideOnInit) {
            detail.visibility = View.GONE
        }
        toggle.setOnClickListener {
            if (availableOrPurchase()) {
                val delay = if (detail.isVisible) 0L else 200L
                val transition = AutoTransition().apply { duration = delay }

                TransitionManager.beginDelayedTransition(card, transition)
                if (!detail.isVisible) {
                    detail.alpha = 0f
                    detail.translationY = -8f
                    detail.animate().alpha(1f).translationY(0f).setDuration(delay*4L).start()
                }
                detail.isVisible = !detail.isVisible
            }
        }
    }

    fun availableOrPurchase(): Boolean {
        if (!UserDataManager.hasPurchased(getPurchaseId())) {
            val success = UserDataManager.spendSeed(
                ComponentDetailToken(
                    getPurchaseId(),
                    history = "Purchased component detail(idx=$componentIdx) from $namingReport")
            )
            if (success) {
                Toast.makeText(card.context,
                    "Purchased component detail(idx=$componentIdx) from $namingReport",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        return true
    }

    fun getPurchaseId(): String { // TODO make unique
        return namingReport.getNameAsPretty() + "_" + componentIdx
    }
}