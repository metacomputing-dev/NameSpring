package com.metacomputing.namespring.ui.utils

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.graphics.drawable.toDrawable
import com.metacomputing.namespring.R

object ViewUtils {
    fun Resources.getColorDrawable(@ColorRes res: Int): ColorDrawable {
        return getColor(res, null).toDrawable()
    }

    fun showDialog(
        context: Context,
        @StringRes title: Int,
        bindingRoot: View,
        @StringRes ok: Int? = R.string.ok,
        @StringRes cancel: Int? = R.string.cancel,
        @StringRes neutral: Int? = null,
        onCreateLayout: ((layout: View) -> Unit)? = null,
        onPressedOk: ((layout: View) -> Unit)? = null,
        onNeutral: ((layout: View) -> Unit)? = null
    ) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setView(bindingRoot)
            .apply {
                ok?.let {
                    setPositiveButton(it) { dialog, _ ->
                        onPressedOk?.invoke(bindingRoot)
                        dialog.dismiss()
                    }
                }
                cancel?.let {
                    setNegativeButton(it) { dialog, _ ->
                        dialog.dismiss()
                    }
                }
                neutral?.let {
                    setNeutralButton(it) { dialog, _ ->
                        onNeutral?.invoke(bindingRoot)
                        dialog.dismiss()
                    }
                }
            }
            .create().apply {
                bindingRoot.post {
                    onCreateLayout?.invoke(bindingRoot)
                }

                window?.setBackgroundDrawableResource(R.drawable.dialog_background)
                show()
            }
    }
}

fun traverseViewTree(view: View, visitor: (View) -> Unit) {
    visitor(view)
    if (view is ViewGroup) {
        for (i in 0 until view.childCount) {
            traverseViewTree(view.getChildAt(i), visitor)
        }
    }
}