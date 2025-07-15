package com.metacomputing.namespring.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.metacomputing.namespring.R
import com.metacomputing.namespring.control.ProfileManager
import com.metacomputing.namespring.control.ProfileManager.initByMock
import com.metacomputing.namespring.model.viewmodel.Profile

class ProfileListFragment: Fragment() {
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
        private val dragStartListener: OnStartDragListener
    ) : RecyclerView.Adapter<ProfileListAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initByMock(inflater.context) // TODO test code
        layout = inflater.inflate(R.layout.fragment_profile_management, container, false)
        layout.post {
            recyclerView = layout.findViewById(R.id.profile_list_recycler_view)
            val profiles = ProfileManager.profiles
            val adapter = ProfileListAdapter(profiles, dragListener)

            recyclerView.layoutManager = LinearLayoutManager(inflater.context)
            recyclerView.adapter = adapter
            val callback = ProfileItemTouchHelperCallback(adapter)
            itemTouchHelper = ItemTouchHelper(callback)
            itemTouchHelper.attachToRecyclerView(recyclerView)
        }


        return layout
    }
}