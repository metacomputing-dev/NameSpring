package com.metacomputing.namespring.ui.util

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.metacomputing.namespring.R
import com.metacomputing.namespring.control.TaskManager
import com.metacomputing.namespring.model.viewmodel.getHanjaAt
import com.metacomputing.namespring.ui.BaseFragment
import com.metacomputing.seed.Seed
import com.metacomputing.seed.model.HanjaSearchResult

object HanjaSearchDialogUtil {
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

    private class HanjaInfoListAdapter(
        private val items: MutableList<HanjaInfo>,
        private val onHanjaSelected: (HanjaInfo) -> Unit
    ) : RecyclerView.Adapter<HanjaInfoListAdapter.ViewHolder>() {
        private var filteredItems = items.toMutableList()

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val cardView: CardView = view.findViewById(R.id.search_hanja_item_cardview)
            val hanjaView: TextView = view.findViewById(R.id.search_hanja_item_title)
            val meaningView: TextView = view.findViewById(R.id.search_hanja_item_meaning)

            fun bind(hanjaInfo: HanjaInfo) {
                cardView.setTag(R.id.tag_key_hanja_search_list_item, hanjaInfo)
                cardView.setOnClickListener {
                    cardView.getTag(R.id.tag_key_hanja_search_list_item)?.let {
                        onHanjaSelected.invoke(it as HanjaInfo)
                    }
                }
                hanjaView.text = hanjaInfo.hanja
                meaningView.text = hanjaInfo.meaning
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_search_hanja, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount() = filteredItems.size

        @SuppressLint("ClickableViewAccessibility")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(filteredItems[position])
        }

        @SuppressLint("NotifyDataSetChanged")
        fun filter(query: String) {
            filteredItems = if (query.isEmpty()) {
                items.toMutableList()
            } else {
                items.filter { it.meaning.contains(query) }.toMutableList()
            }
            if (filteredItems.size == 0) {
                filteredItems = items
            }
            notifyDataSetChanged()
        }
    }

    @SuppressLint("SetTextI18n")
    fun show(fragment: BaseFragment, pronounce: String, currentHanja: String? = null, onSelected: (HanjaInfo) -> Unit) {
        var tvHanjaSelection: TextView? = null
        var tvHanjaMeaning: TextView? = null
        var tvQuery: EditText? = null
        var recyclerView: RecyclerView? = null

        fragment.showDialog(R.string.search_hanja_title, R.layout.popup_hanja_search,
            onCreateLayout = { layout ->
                tvHanjaSelection = layout.findViewById(R.id.tv_show_hanja_selection)
                tvHanjaMeaning = layout.findViewById(R.id.tv_show_hanja_meaning)
                tvQuery = layout.findViewById<EditText>(R.id.et_hanja_query).apply {
                    doOnTextChanged { _, _, _, _ ->
                        val query = tvQuery?.text.toString()
                        if (query.isNotEmpty()) {
                            (recyclerView?.adapter as? HanjaInfoListAdapter)?.filter(query)
                        }
                    }
                }

                currentHanja?.let {
                    tvHanjaSelection?.text = it.getHanjaAt(0)
                }

                recyclerView = layout.findViewById<RecyclerView>(R.id.hanja_list_recycler_view).apply {
                    layoutManager = LinearLayoutManager(layout.context)
                }
                var hanjaInfoList: MutableList<HanjaSearchResult>? = null
                TaskManager.launch(fragment.requireContext(), "Search Hanja by $pronounce",
                    block = {
                        hanjaInfoList = Seed.searchHanjaByKorean(pronounce).toMutableList()
                    },
                    onSuccess = {
                        hanjaInfoList?.let {
                            recyclerView?.adapter = HanjaInfoListAdapter(
                                HanjaInfo.makeListFromSearchResult(it).toMutableList()) { selectedHanjaInfo ->
                                tvHanjaSelection?.text = selectedHanjaInfo.hanja
                                tvHanjaMeaning?.text = selectedHanjaInfo.meaning
                            }
                            it.firstOrNull { hanjaInfo -> hanjaInfo.hanja == currentHanja }?.let { matched ->
                                tvHanjaMeaning?.text = matched.meaning
                            }
                        }
                    })
            },
            onPressedOk = { _ ->
                onSelected.invoke(HanjaInfo(tvHanjaSelection?.text.toString(), tvHanjaMeaning?.text.toString(), pronounce))
            }
        )
    }
}