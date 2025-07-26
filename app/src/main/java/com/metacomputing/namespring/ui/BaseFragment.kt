package com.metacomputing.namespring.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.metacomputing.namespring.R

abstract class BaseFragment: Fragment() {

    fun showDialog(
        @StringRes title: Int,
        @LayoutRes layoutRes: Int,
        @StringRes ok: Int? = R.string.ok,
        @StringRes cancel: Int? = R.string.cancel,
        onCreateLayout: ((layout: View) -> Unit)? = null,
        onPressedOk: ((layout: View) -> Unit)? = null
    )
    {
        val form = LayoutInflater.from(activity).inflate(layoutRes, null)
        AlertDialog.Builder(activity)
            .setTitle(title)
            .setView(form)
            .apply {
                ok?.let {
                    setPositiveButton(it) { dialog, _ ->
                        onPressedOk?.invoke(form)
                        dialog.dismiss()
                    }
                }
                cancel?.let {
                    setNegativeButton(it) { dialog, _ ->
                        dialog.dismiss()
                    }
                }
            }
            .create().apply {
                form?.post {
                    onCreateLayout?.invoke(form)
                }

                window?.setBackgroundDrawableResource(R.drawable.dialog_background)
                show()
            }
    }
}