package com.metacomputing.namespring.ui.utils

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.metacomputing.namespring.R
import com.metacomputing.namespring.control.SeedProxy
import com.metacomputing.namespring.control.TaskManager
import com.metacomputing.namespring.databinding.ListItemSearchHanjaBinding
import com.metacomputing.namespring.databinding.PopupHanjaSearchBinding
import com.metacomputing.namespring.utils.getHanjaAt
import com.metacomputing.seed.model.HanjaSearchResult

object HanjaSearchDialog {
    data class HanjaInfo(
        val hanja: String,
        val meaning: String,
        val pronounce: String
    ) {
        companion object {
            private fun fromSearchResultItem(hanjaResult: HanjaSearchResult): HanjaInfo {
                return HanjaInfo(hanjaResult.hanja, hanjaResult.meaning, hanjaResult.korean)
            }

            fun makeListFromSearchResult(searchResultList: List<HanjaSearchResult>): List<HanjaInfo> {
                return ArrayList<HanjaInfo>().apply {
                    searchResultList.forEach {
                        add(fromSearchResultItem(it))
                    }
                }
            }
        }
    }

    class HanjaInfoListAdapter(
        private val items: MutableList<HanjaInfo>,
        private val onHanjaSelected: (HanjaInfo) -> Unit
    ) : RecyclerView.Adapter<HanjaInfoListAdapter.ViewHolder>() {
        private var filteredHanjaList = items.toMutableList()

        inner class ViewHolder(val binding: ListItemSearchHanjaBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(ListItemSearchHanjaBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

        override fun getItemCount() = filteredHanjaList.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.binding.apply {
                val hanjaInfo = filteredHanjaList[position]

                searchHanjaItemCardview.apply {
                    setTag(R.id.tag_key_hanja_search_list_item, hanjaInfo)
                    setOnClickListener {
                        getTag(R.id.tag_key_hanja_search_list_item)?.let {
                            onHanjaSelected.invoke(it as HanjaInfo)
                        }
                    }
                    searchHanjaItemTitle.text = hanjaInfo.hanja
                    searchHanjaItemMeaning.text = hanjaInfo.meaning
                }
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        fun filter(query: String) {
            filteredHanjaList = if (query.isEmpty()) {
                items.toMutableList()
            } else {
                items.filter { it.meaning.contains(query) }.toMutableList()
            }
            if (filteredHanjaList.size == 0) {
                filteredHanjaList = items
            }
            notifyDataSetChanged()
        }
    }

    fun show(context: Context, pronounce: String, currentHanja: String? = null, onSelected: (HanjaInfo?) -> Unit) {
        val binding = PopupHanjaSearchBinding.inflate(LayoutInflater.from(context))
        ViewUtils.showDialog(context, R.string.search_hanja_title, binding.root,
            onCreateLayout = { _ ->
                with (binding) {
                    etHanjaQuery.apply {
                        doOnTextChanged { _, _, _, _ ->
                            if (text.isNotEmpty()) {
                                (hanjaListRecyclerView.adapter as? HanjaInfoListAdapter)?.filter(text.toString())
                            }
                        }
                    }
                    tvShowHanjaSelection.text =
                        if (currentHanja.isNullOrEmpty()) ""
                        else currentHanja.getHanjaAt(0)
                    hanjaListRecyclerView.layoutManager = LinearLayoutManager(context)
                    var hanjaInfoList: MutableList<HanjaSearchResult>? = null
                    TaskManager.launch("Search Hanja by $pronounce",
                        block = {
                            hanjaInfoList = SeedProxy.getHanjaInfoByPronounce(pronounce).toMutableList()
                        },
                        onSuccess = {
                            hanjaInfoList?.let {
                                hanjaListRecyclerView.adapter = HanjaInfoListAdapter(
                                    HanjaInfo.makeListFromSearchResult(it).toMutableList()) { selectedHanjaInfo ->
                                    tvShowHanjaSelection.text = selectedHanjaInfo.hanja
                                    tvShowHanjaMeaning.text = selectedHanjaInfo.meaning
                                }
                                it.firstOrNull { hanjaInfo -> hanjaInfo.hanja == currentHanja }?.let { matched ->
                                    tvShowHanjaMeaning.text = matched.meaning
                                }
                            }
                        })
                }
            },
            onPressedOk = { _ ->
                onSelected.invoke(
                    HanjaInfo(
                        binding.tvShowHanjaSelection.text.toString(),
                        binding.tvShowHanjaMeaning.text.toString(),
                        pronounce)
                )
            },
            neutral = R.string.delete,
            onNeutral = { _ ->
                onSelected.invoke(null)
            }
        )
    }
}