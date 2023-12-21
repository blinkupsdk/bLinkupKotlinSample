package com.blinkup.clientsampleapp.adapter

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blinkup.clientsampleapp.R
import com.blinkupapp.sdk.Blinkup
import com.blinkupapp.sdk.data.exception.BlinkupException
import com.blinkupapp.sdk.data.model.Presence
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class EventsListAdapter(
    data: List<Presence>,
    val showLoading: () -> Unit,
    val hideLoading: () -> Unit
) : AbstractAdapter<RecyclerView.ViewHolder>() {

    var data = data
        set(value) {
            field = value
            checkedStates = data.map { false }.toMutableList()
            notifyDataSetChanged()
        }
    var checkedStates = data.map { false }.toMutableList()

    lateinit var context: Context

    class ViewHolder(view: View, private val onCheckChange: (Boolean, Int) -> Unit) :
        RecyclerView.ViewHolder(view) {
        private val nameView: TextView = view.findViewById(R.id.name)
        private val nameUnderlined: TextView = view.findViewById(R.id.nameUnderlined)
        private val isHere: ImageView = view.findViewById(R.id.isHere)
        private val root = view
        private val checkBox: CheckBox = view.findViewById(R.id.checkbox)

        fun bind(event: Presence, isChecked: Boolean, position: Int, viewType: ViewType) {
            nameView.text = event.place?.name
            nameUnderlined.text = event.place?.name
            isHere.visibility = if (event.isPresent == true) View.VISIBLE else View.GONE
            nameUnderlined.visibility = if (event.isPresent == true) View.VISIBLE else View.GONE
            nameView.visibility = if (event.isPresent == true) View.GONE else View.VISIBLE
            checkBox.isChecked = isChecked

            checkBox.setOnCheckedChangeListener { _, isBoxChecked ->
                onCheckChange(isBoxChecked, position)
            }
            when (viewType) {
                ViewType.TOP -> {
                    root.setBackgroundResource(R.drawable.rounded_corners_top)
                }

                ViewType.MIDDLE -> {
                    root.setBackgroundResource(R.drawable.bottom_border)
                }

                ViewType.BOTTOM -> {
                    root.setBackgroundResource(R.drawable.rounded_corners_bottom)
                }
            }
        }
    }

    enum class ViewType {
        TOP,
        MIDDLE,
        BOTTOM
    }

    class TailViewHolder(
        val view: View,
        val showLoading: () -> Unit,
        val hideLoading: () -> Unit,
        val context: Context
    ) :
        RecyclerView.ViewHolder(view), LocationListener {

        private val checkIn: Button = view.findViewById(R.id.presence_check_in)
        private val checkOut: Button = view.findViewById(R.id.presence_check_out)
        private val devDetails: Button = view.findViewById(R.id.dev_details)
        private lateinit var locationManager: LocationManager

        val inflater = LayoutInflater.from(view.context)
        val devLayout = inflater.inflate(R.layout.dev_details, null)

        val currentLat = devLayout.findViewById<TextView>(R.id.current_lat)
        val currentLong = devLayout.findViewById<TextView>(R.id.current_long)

        fun bind(checkedState: MutableList<Boolean>, data: List<Presence>, lifecycleOwner: LifecycleOwner, updateData: () -> Unit) {

            checkIn.setOnClickListener {

                var index = checkedState.indexOf(true)

                when (index) {
                    -1 -> {
                        Toast.makeText(view.context, "Please select a location first", Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        lifecycleOwner.lifecycleScope.launch (Dispatchers.IO){
                            try {
                                Blinkup.setUserAtEvent(true, data[index].place!!)
                                updateData()
                            } catch (e: BlinkupException) {
                                lifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                                    Toast.makeText(view.context, "An Error occured", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                }
            }

            checkOut.setOnClickListener {

                var index = checkedState.indexOf(true)

                when (index) {
                    -1 -> {
                        Toast.makeText(view.context, "Please select a location first", Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        lifecycleOwner.lifecycleScope.launch (Dispatchers.IO){
                            try {
                                Blinkup.setUserAtEvent(false, data[index].place!!)
                                updateData()
                            } catch (e: BlinkupException) {
                                lifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                                    Toast.makeText(view.context, "An Error occured", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                }
            }

            devDetails.setOnClickListener {

                showLoading()

                getLocation()

                val dialogBuilder = AlertDialog.Builder(view.context)

                val recyclerView = devLayout.findViewById<RecyclerView>(R.id.recycler_view)
                recyclerView.layoutManager = LinearLayoutManager(view.context)

                lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    try {

                        var eventList = Blinkup.getEvents()

                        val adapter = DevDetailsAdapter(eventList)

                        launch(Dispatchers.Main) {

                            adapter?.lifecycleOwner = lifecycleOwner

                            recyclerView.adapter = adapter

                            dialogBuilder.setView(devLayout)

                            dialogBuilder.setNegativeButton("Close") {
                                dialog, _ ->
                                dialog.cancel()
                                devLayout.parent?.let {
                                    (it as ViewGroup).removeView(devLayout)
                                }
                            }

                            dialogBuilder.create().show()

                            hideLoading()

                        }
                    } catch (e: BlinkupException){
                        //TODO
                    }
                }

            }
        }

        private fun getLocation() {
            locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if ((ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {

                val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

                if(lastKnownLocation != null) {
                    currentLat.text = lastKnownLocation.latitude.toString()
                    currentLong.text = lastKnownLocation.longitude.toString()
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1f, this)
            }

        }

        override fun onLocationChanged(location: Location) {

            currentLat.text = location.latitude.toString()
            currentLong.text = location.longitude.toString()

            Log.i("location", "onLocationChanged Lat: ${location.latitude}")
            Log.i("location", "onLocationChanged Long: ${location.longitude}")

        }

    }

    fun setAdapterContext(context: Context) {
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        context = parent.context
        return if (viewType == VIEW_TYPE_ITEM) {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.event_list_item, parent, false)
            ViewHolder(view) { isChecked, position ->
                checkedStates.forEachIndexed { index, _ ->
                    checkedStates[index] = if (index == position) isChecked else false
                }
            }
        } else {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.event_list_tail, parent, false)
            TailViewHolder(view, showLoading, hideLoading, context)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == data.size) VIEW_TYPE_TAIL else VIEW_TYPE_ITEM
    }

    override fun getItemCount(): Int {
        return data.size + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.bind(data[position], checkedStates[position], position, when (position) {
                0 -> ViewType.TOP
                data.size - 1 -> ViewType.BOTTOM
                else -> ViewType.MIDDLE
            })
        } else if (holder is TailViewHolder) {
            holder.bind(checkedStates, data, lifecycleOwner, ::updateData)
        }

    }

    fun updateData() {

        lifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            val newEventList = Blinkup.getEvents().map { Presence( place = it,
                isPresent = Blinkup.isUserAtEvent(it)) }
            data = newEventList
        }

    }

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_TAIL = 1
    }
}
