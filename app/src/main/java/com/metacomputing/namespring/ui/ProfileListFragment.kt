package com.metacomputing.namespring.ui

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.metacomputing.namespring.R
import com.metacomputing.namespring.control.ProfileManager
import com.metacomputing.namespring.control.ProfileManager.initByMock
import com.metacomputing.namespring.model.viewmodel.Profile
import androidx.core.graphics.drawable.toDrawable

class ProfileListFragment: BaseFragment() {
    private lateinit var layout: View
    private lateinit var recyclerView: RecyclerView

    private lateinit var itemTouchHelper: ItemTouchHelper

    interface OnStartDragListener {
        fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
    }

    val dragListener = object: OnStartDragListener {
        override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
            itemTouchHelper.startDrag(viewHolder)
        }
    }

    inner class ProfileListAdapter(
        private val items: MutableList<Profile>,
        private val onMenuClick: (Int, Profile) -> Unit,
        private val dragStartListener: OnStartDragListener
    ) : RecyclerView.Adapter<ProfileListAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val cardView: CardView = view.findViewById(R.id.profile_list_item_cardview)
            val backgroundView: LinearLayout = view.findViewById(R.id.profile_list_item_background)
            val titleView: TextView = view.findViewById(R.id.profile_list_item_title)
            val nameView: TextView = view.findViewById(R.id.profile_list_item_name)
            val birthView: TextView = view.findViewById(R.id.profile_list_item_birthdate)
            val dragHandle: ImageView = view.findViewById(R.id.profile_list_item_drag_handle)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.profile_list_item, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount() = items.size

        @SuppressLint("ClickableViewAccessibility")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.cardView.setTag(R.id.tag_key_profile_item, items[position])
            if (ProfileManager.currentId == items[position].id) {
                holder.backgroundView.background =
                    resources.getColor(R.color.meta_blended_green).toDrawable()
            } else {
                holder.backgroundView.background =
                    resources.getColor(R.color.meta_light_green).toDrawable()
            }
            holder.cardView.setOnClickListener {
                val popup = PopupMenu(requireContext(), holder.cardView)
                popup.menuInflater.inflate(R.menu.profile_item_context_menu, popup.menu)
                popup.setOnMenuItemClickListener { menuItem ->
                    onMenuClick(menuItem.itemId, holder.cardView.getTag(R.id.tag_key_profile_item) as Profile)
                    true
                }
                popup.show()
            }
            holder.titleView.text = items[position].title.value
            holder.nameView.text = items[position].fullName
            holder.birthView.text = items[position].birthAsString
            holder.dragHandle.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    dragStartListener.onStartDrag(holder)
                }
                false
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
        initByMock(inflater.context) // TODO test code
        layout = inflater.inflate(R.layout.fragment_profile_list, container, false)
        layout.post {
            recyclerView = layout.findViewById(R.id.profile_list_recycler_view)
            val adapter = ProfileListAdapter(ProfileManager.profiles,
                onMenuClick = { menuItemId, profile ->
                    when (menuItemId) {
                        R.id.menu_item_profile_set_main -> ProfileManager.setCurrent(profile)
                        R.id.menu_item_profile_delete -> ProfileManager.remove(profile)
                        R.id.menu_item_profile_edit -> openEditProfileFragment(profile)
                    }
                    recyclerView.adapter?.notifyDataSetChanged()
                }, dragListener)

            recyclerView.layoutManager = LinearLayoutManager(inflater.context)
            recyclerView.adapter = adapter
            val callback = ProfileItemTouchHelperCallback(adapter)
            itemTouchHelper = ItemTouchHelper(callback)
            itemTouchHelper.attachToRecyclerView(recyclerView)
        }

        return layout
    }

    private fun openEditProfileFragment(profile: Profile) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, ProfileEditFragment(profile))
            .addToBackStack(null)
            .commit()
    }
}