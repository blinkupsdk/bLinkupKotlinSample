package com.blinkup.clientsampleapp.adapter

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blinkup.clientsampleapp.R
import com.blinkupapp.sdk.Blinkup
import com.blinkupapp.sdk.data.exception.BlinkupException
import com.blinkupapp.sdk.data.model.Connection
import com.blinkupapp.sdk.data.model.ConnectionRequest
import com.blinkupapp.sdk.data.model.ContactResult
import com.blinkupapp.sdk.data.model.Presence
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EventsListAdapter(
    data: List<Presence>,
    val showLoading: () -> Unit,
    val hideLoading: () -> Unit
) :
    AbstractAdapter<EventsListAdapter.ViewHolder>() {

    var data = data
        set(value) {
            field = value
            checkedStates = data.map { false }.toMutableList()
            notifyDataSetChanged()
        }
    var checkedStates = data.map { false }.toMutableList()

    class ViewHolder(view: View, private val onCheckChange: (Boolean, Int) -> Unit) :
        RecyclerView.ViewHolder(view) {
        private val nameView: TextView = view.findViewById(R.id.name)
        private val nameUnderlined: TextView = view.findViewById(R.id.nameUnderlined)
        private val isHere: ImageView = view.findViewById(R.id.isHere)
        private val root = view
        private val checkBox: CheckBox = view.findViewById(R.id.checkbox)

        fun bind(event: Presence, isChecked: Boolean, position: Int) {
            nameView.text = event.place?.name
            nameUnderlined.text = event.place?.name
            isHere.visibility = if (event.isPresent == true) View.VISIBLE else View.GONE
            nameUnderlined.visibility = if (event.isPresent == true) View.VISIBLE else View.GONE
            checkBox.isChecked = isChecked

            checkBox.setOnCheckedChangeListener { _, isBoxChecked ->
                onCheckChange(isBoxChecked, position)
            }
        }
    }

    class TailViewHolder(
        val view: View,
        val lifecycleOwner: LifecycleOwner,
        val showLoading: () -> Unit,
        val hideLoading: () -> Unit
    ) :
        RecyclerView.ViewHolder(view) {

        fun bind() {

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.event_list_item, parent, false)
            ViewHolder(view) { isChecked, position ->
                checkedStates.forEachIndexed { index, _ ->
                    checkedStates[index] = if (index == position) isChecked else false
                }
            }
        } else {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.user_list_tail, parent, false)
            TailViewHolder(view, lifecycleOwner, showLoading, hideLoading)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == data.size) VIEW_TYPE_TAIL else VIEW_TYPE_ITEM
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            (holder as ViewHolder).bind(data[position], checkedStates[position], position)
        }else if (holder is TailViewHolder){
            holder.bind()
        }

    }

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_TAIL = 1
    }
}
