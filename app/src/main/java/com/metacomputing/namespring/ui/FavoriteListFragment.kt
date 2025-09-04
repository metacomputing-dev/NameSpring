package com.metacomputing.namespring.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.metacomputing.namespring.R
import com.metacomputing.namespring.control.FavoriteManager
import com.metacomputing.namespring.control.SeedProxy
import com.metacomputing.namespring.control.TaskManager
import com.metacomputing.namespring.databinding.FragmentFavoriteListBinding
import com.metacomputing.namespring.model.viewmodel.Profile
import com.metacomputing.namespring.databinding.ListItemProfileBinding
import com.metacomputing.namespring.model.report.NamingReport

class FavoriteListFragment: BaseFragment() {
    private lateinit var binding: FragmentFavoriteListBinding

    inner class FavoriteListAdapter : RecyclerView.Adapter<FavoriteListAdapter.ViewHolder>() {
        private val items: MutableList<Profile>
            get() = FavoriteManager.favorites.value.toMutableList()

        inner class ViewHolder(val binding: ListItemProfileBinding)
            : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(ListItemProfileBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false)
            )
        }

        override fun getItemCount() = items.size

        @SuppressLint("ClickableViewAccessibility")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val profile = items[position]

            with (holder.binding) {
                profileListItemTitle.text = profile.title
                profileListItemName.text = profile.fullNamePrettyString
                profileListItemBirthdate.text = profile.birthAsString
                profileListItemDragHandle.visibility = View.GONE
                favoriteListItemDelete.visibility = View.VISIBLE
                favoriteListItemDelete.apply {
                    visibility = View.VISIBLE
                    setOnClickListener {
                        FavoriteManager.remove(profile)
                    }
                }
                profileListItemCardview.apply {
                    setTag(R.id.tag_key_profile_item, profile)
                    setOnClickListener {
                        (getTag(R.id.tag_key_profile_item) as? Profile)?.let { profile ->
                            TaskManager.launch("NamingReport from favorite",
                                block = {
                                    val reports = SeedProxy.makeNamingReport(profile)
                                    if (reports.isNotEmpty()) {
                                        openNamingReportFragment(reports.first())
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteListBinding.inflate(LayoutInflater.from(context)).apply {
            favoriteListRecyclerView.apply {
                layoutManager = LinearLayoutManager(inflater.context)
                adapter = FavoriteListAdapter()
                FavoriteManager.observeFavorites(viewLifecycleOwner) {
                    adapter?.notifyDataSetChanged()
                }
            }
        }
        return binding.root
    }

    private fun openNamingReportFragment(report: NamingReport) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, NamingReportFragment(report))
            .addToBackStack(null)
            .commit()
    }
}