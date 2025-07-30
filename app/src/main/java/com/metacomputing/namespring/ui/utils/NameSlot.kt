package com.metacomputing.namespring.ui.utils

import android.content.Context
import android.view.LayoutInflater
import androidx.core.widget.doOnTextChanged
import com.metacomputing.namespring.databinding.ItemNameFirstBinding
import com.metacomputing.namespring.utils.emptyIfUnderscore

class NameSlot(
    context: Context,
    character: String? = null,
    hanja: String? = null
) {
    val binding = ItemNameFirstBinding.inflate(LayoutInflater.from(context)).apply {
        profileEditCharText.apply {
            setText(character?.emptyIfUnderscore())

            doOnTextChanged { _, _, _, _ ->
                profileEditHanjaText.text = ""
            }
        }

        profileEditHanjaText.text = hanja?.emptyIfUnderscore()
        profileEditHanjaCardview.setOnClickListener {
            if (profileEditCharText.text.isEmpty()) return@setOnClickListener

            HanjaSearchDialog.show(context,
                pronounce = profileEditCharText.text.toString(),
                currentHanja = profileEditHanjaText.text.toString()) { hanjaInfo ->
                profileEditHanjaText.text = hanjaInfo?.hanja ?: ""
            }
        }
    }
}