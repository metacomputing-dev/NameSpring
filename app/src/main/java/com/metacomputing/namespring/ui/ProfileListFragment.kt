package com.metacomputing.namespring.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.metacomputing.namespring.R
import com.metacomputing.namespring.control.ProfileManager
import com.metacomputing.namespring.model.viewmodel.Profile
import com.metacomputing.namespring.databinding.FragmentProfileListBinding
import com.metacomputing.namespring.databinding.ListItemProfileBinding
import com.metacomputing.namespring.ui.utils.ViewUtils.getColorDrawable

class ProfileListFragment: BaseFragment() {
    private lateinit var binding: FragmentProfileListBinding
    private lateinit var itemTouchHelper: ItemTouchHelper

    interface OnStartDragListener {
        fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
    }

    private val dragListener = object: OnStartDragListener {
        override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
            itemTouchHelper.startDrag(viewHolder)
        }
    }

    inner class ProfileListAdapter(
        private val items: MutableList<Profile>,
        private val onMenuClick: (Int, Profile) -> Unit,
        private val dragStartListener: OnStartDragListener
    ) : RecyclerView.Adapter<ProfileListAdapter.ViewHolder>() {

        inner class ViewHolder(val binding: ListItemProfileBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(ListItemProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

        override fun getItemCount() = items.size

        @SuppressLint("ClickableViewAccessibility")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            with (holder.binding) {
                profileListItemTitle.text = items[position].title.value
                profileListItemName.text = items[position].fullNamePrettyString
                profileListItemBirthdate.text = items[position].birthAsString
                profileListItemDragHandle.setOnTouchListener { _, event ->
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        dragStartListener.onStartDrag(holder)
                    }
                    false
                }
                profileListItemCardview.apply {
                    setTag(R.id.tag_key_profile_item, items[position])
                    setOnClickListener {
                        PopupMenu(requireContext(), this).apply {
                            menuInflater.inflate(R.menu.profile_item_context_menu, menu)
                            setOnMenuItemClickListener { menuItem ->
                                onMenuClick(
                                    menuItem.itemId,
                                    getTag(R.id.tag_key_profile_item) as Profile
                                )
                                true
                            }
                            show()
                        }
                    }
                }
                profileListItemBackground.background =
                    if (ProfileManager.mainProfile?.id == items[position].id)
                        resources.getColorDrawable(R.color.meta_blended_green)
                    else
                        resources.getColorDrawable(R.color.meta_light_green)
            }
        }

        fun moveItem(from: Int, to: Int) {
            if (from == to) return
            val item = items.removeAt(from)
            items.add(to, item)
            notifyItemMoved(from, to)
        }
    }

    private class ProfileItemTouchHelperCallback(
        private val adapter: ProfileListAdapter
    ): ItemTouchHelper.Callback() {

        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            return makeMovementFlags(dragFlags, 0)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            adapter.moveItem(viewHolder.adapterPosition, target.adapterPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            TODO("Not yet implemented")
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileListBinding.inflate(LayoutInflater.from(context)).apply {
            btnProfileNew.setOnClickListener {
                openEditProfileFragment(
                    Profile.new(requireContext()).apply {
                        ProfileManager.add(this, true)
                    }
                )
            }
            profileListRecyclerView.apply {
                layoutManager = LinearLayoutManager(inflater.context)
                adapter = ProfileListAdapter(
                    ProfileManager.profiles.value.toMutableList(),
                    onMenuClick = { menuItemId, profile ->
                        when (menuItemId) {
                            R.id.menu_item_profile_set_main -> ProfileManager.mainProfile = profile
                            R.id.menu_item_profile_delete -> ProfileManager.remove(profile)
                            R.id.menu_item_profile_edit -> openEditProfileFragment(profile)
                        }
                        adapter?.notifyDataSetChanged()
                    }, dragListener
                ).also {
                    itemTouchHelper = ItemTouchHelper(ProfileItemTouchHelperCallback(it))
                    itemTouchHelper.attachToRecyclerView(profileListRecyclerView)
                }
            }
        }
        return binding.root
    }

    private fun openEditProfileFragment(profile: Profile) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, ProfileEditFragment(profile))
            .addToBackStack(null)
            .commit()
    }
}