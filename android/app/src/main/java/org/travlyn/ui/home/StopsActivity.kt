package org.travlyn.ui.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.stepstone.apprating.AppRatingDialog
import com.stepstone.apprating.listener.RatingDialogListener
import kotlinx.android.synthetic.main.activity_stops.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.travlyn.R
import org.travlyn.api.CityApi
import org.travlyn.api.StopApi
import org.travlyn.api.TripApi
import org.travlyn.api.UserApi
import org.travlyn.api.model.*
import org.travlyn.components.SelectionToolbar
import org.travlyn.local.Application
import org.travlyn.local.LocalStorage
import org.travlyn.ui.trips.CreateTripActivity
import java.util.*


class StopsActivity : AppCompatActivity(), RatingDialogListener, Application {

    private var clickedStop: Stop? = null

    private lateinit var stopListSelectionToolbar: SelectionToolbar<Stop>
    private val CREATE_TRIP_ACTIVITY_CODE: Int = 1

    private var city: City? = null

    private lateinit var cityApi: CityApi


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stops)
        val toolbar: Toolbar = findViewById(R.id.stopsListToolbar)
        setSupportActionBar(toolbar)
        stopListSelectionToolbar = findViewById(R.id.stopListSelectionToolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        cityApi = CityApi(this)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        if (intent != null && intent.extras != null) {
            city = Gson().fromJson(intent.extras.get("city") as String?, City::class.java)
            initListView()
            if (city != null && city!!.unfetchedStops!!) {
                Toast.makeText(this, getString(R.string.swipe_to_update_stops), Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun initListView() {
        if (this.city != null) {
            stopListSwipeRefreshLayout.setOnRefreshListener {
                updateCity()
            }

            val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            stopsListView.layoutManager = layoutManager

            stopsListView.adapter = StopListViewAdapter(city!!.stops!!.toList(), this)

            stopListNumberOfResultsTextView.text =
                this.getString(R.string.number_of_stop_found, city!!.stops?.size)

        }
    }

    private fun updateCity() {
        if (city != null) {
            if (this.city!!.unfetchedStops!!) {
                CoroutineScope(Dispatchers.IO).launch {
                    city = cityApi.getCity(city!!.name!!)
                    withContext(Dispatchers.Main) {
                        stopsListView.adapter =
                            StopListViewAdapter(city!!.stops!!.toList(), this@StopsActivity)
                        stopListNumberOfResultsTextView.text =
                            getString(R.string.number_of_stop_found, city!!.stops?.size)
                        stopListSwipeRefreshLayout.isRefreshing = false
                    }
                }
            } else {
                stopListSwipeRefreshLayout.isEnabled = false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.menuInflater.inflate(R.menu.stops_menu, menu)

        val item: MenuItem = menu!!.findItem(R.id.searchStopsMenuItem)
        stopListSearchView.setMenuItem(item)

        stopListSearchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                performFiltering(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                performFiltering(newText)
                return true
            }
        })

        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CREATE_TRIP_ACTIVITY_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val trip: Trip = Gson().fromJson(data?.getStringExtra("trip"), Trip::class.java)
                }
            }
        }
    }

    private fun performFiltering(query: String) {
        (stopsListView.adapter as StopListViewAdapter).filter.filter(query) { count ->
            stopListNumberOfResultsTextView.text = this.getString(
                R.string.number_of_stop_found,
                count
            )
        }
    }

    private fun handleRateStop(stop: Stop) {
        clickedStop = stop
        AppRatingDialog.Builder()
            .setPositiveButtonText(getString(R.string.rate))
            .setNegativeButtonText(getString(R.string.cancel))
            .setNoteDescriptions(
                listOf(
                    getString(R.string.very_bad_rating),
                    getString(R.string.not_good_rating),
                    getString(R.string.okay_rating),
                    getString(R.string.very_good_rating),
                    getString(R.string.excellent_rating)
                )
            )
            .setDefaultRating(2)
            .setTitle(getString(R.string.rate_city, stop.name))
            .setDescription(getString(R.string.request_rate_city))
            .setCommentInputEnabled(true)
            .setStarColor(R.color.colorAccent)
            .setHint(getString(R.string.request_write_comment))
            .setCommentBackgroundColor(R.color.light_gray)
            .setCancelable(true)
            .setCanceledOnTouchOutside(true)
            .create(this)
            .show()
    }

    override fun onPositiveButtonClicked(rate: Int, comment: String) {
        val rating = Rating(
            user = LocalStorage(this).readObject("user"),
            description = comment,
            rating = rate / 5.0
        )

        val stopApi = StopApi(application = this)
        val context = this
        CoroutineScope(Dispatchers.IO).launch {
            stopApi.rateStop(clickedStop!!.id!!, rating)
            clickedStop = null

            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    getString(R.string.successfully_rated_city),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun handleAddStopToTrip(stop: Stop) {
        // TODO waiting for API to implement that functionality
    }

    override fun onNegativeButtonClicked() {
        // stays empty as no action needed
    }

    override fun onNeutralButtonClicked() {
        // stays empty as no action needed
    }

    override fun getContext(): Context {
        return this
    }

    private inner class StopListViewAdapter(
        private var stops: List<Stop>,
        private val context: Context
    ) : RecyclerView.Adapter<StopListViewAdapter.ViewHolder>(), Filterable {

        private val cityApi = CityApi(application = context as StopsActivity)
        private val filter = StopFilter(this)
        private var filteredStops: MutableList<Stop> = stops.toMutableList()

        private var isSelectable = false

        init {
            stopListSelectionToolbar.setOnCloseListener {
                isSelectable = false
                notifyDataSetChanged()
            }

            stopListSelectionToolbar.setCheckListener { selectedElements ->
                isSelectable = false
                notifyDataSetChanged()

                val stops = selectedElements.toMutableList()

                CoroutineScope(Dispatchers.IO).launch {
                    openTripDialog(fetchUsersTrips(), stops)
                }
            }
        }

        private suspend fun fetchUsersTrips(): List<Trip> {
            val user: User? = LocalStorage(context).readObject("user")
            if (user != null) {
                return UserApi(context as StopsActivity).getTripsByUserId(user.id!!).toList()
            }
            return emptyList()
        }

        private suspend fun openTripDialog(trips: List<Trip>, stops: List<Stop>) =
            withContext(Dispatchers.Main) {
                val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                builder.setTitle(context.getString(R.string.trips))

                val tripNames: MutableList<String> =
                    trips.map { it.name.toString() }.toMutableList()
                tripNames.add(0, context.getString(R.string.create_trip))
                builder.setItems(tripNames.toTypedArray()) { _, which ->
                    when (which) {
                        0 -> {
                            val intent = Intent(context, CreateTripActivity::class.java)
                            intent.putExtras(
                                bundleOf(
                                    "cityId" to city!!.id,
                                    "stopIds" to stops.map { stop -> stop.id }.toTypedArray()
                                )
                            )
                            startActivityForResult(intent, CREATE_TRIP_ACTIVITY_CODE)

                        }
                        else -> {
                            val trip: Trip = trips[which - 1]
                            CoroutineScope(Dispatchers.IO).launch {
                                updateTrip(trip, stops.toMutableList())
                            }
                        }
                    }
                }

                val dialog: AlertDialog = builder.create()
                dialog.show()
            }

        private suspend fun updateTrip(trip: Trip, stops: MutableList<Stop>) {
            for (tripStop in trip.stops!!) {
                if (!stops.any { _stop -> _stop.id == tripStop.id }) {
                    stops.add(tripStop)
                }
            }
            trip.stops = stops.toTypedArray()

            TripApi(context as StopsActivity).updateTrip(trip)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.stop_list_view, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return filteredStops.size
        }

        fun updateStops(stops: List<Stop>) {
            this.stops = stops
            filteredStops = this.stops.toMutableList();
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val stop: Stop = filteredStops[position]

            holder.stopListName.text = stop.name
            holder.stopListDescription.text = stop.description

            holder.stopListRateButton.setOnClickListener {
                handleRateStop(stop)
            }

            holder.stopListAddToTripButton.setOnClickListener {
                handleAddStopToTrip(stop)
            }

            holder.stopListCheckBox.visibility = if (isSelectable) View.VISIBLE else View.GONE
            holder.stopListCardView.setOnLongClickListener {
                if (LocalStorage(context).contains("user")) {
                    isSelectable = !isSelectable
                    stopListSelectionToolbar.toggleToolbar()
                    notifyDataSetChanged()
                } else {
                    Toast.makeText(context, R.string.error_not_signed_in, Toast.LENGTH_LONG).show()
                }
                return@setOnLongClickListener true
            }

            holder.stopListCardView.setOnClickListener {
                if (isSelectable) {
                    holder.stopListCheckBox.isChecked = !holder.stopListCheckBox.isChecked
                    if (holder.stopListCheckBox.isChecked)
                        stopListSelectionToolbar.addSelectedElement(stop)
                    else stopListSelectionToolbar.removeSelectedElement(stop)
                }
            }
            holder.stopListCheckBox.setOnClickListener {
                if (isSelectable) {
                    if (holder.stopListCheckBox.isChecked)
                        stopListSelectionToolbar.addSelectedElement(stop)
                    else stopListSelectionToolbar.removeSelectedElement(stop)
                }
            }

            val stringIdentifier = context.resources.getIdentifier(
                "category_" + stop.category?.name, "string", context.packageName
            )
            holder.stopListCategoryTextView.text =
                if (stringIdentifier > 0) context.getString(stringIdentifier) else stop.category?.name

            if (stop.averageRating == null || stop.averageRating <= 0) {
                holder.stopListRatingTextView.text = context.getString(R.string.no_value)
            } else {
                holder.stopListRatingBar.rating = stop.averageRating.toFloat() * 5f
                holder.stopListRatingTextView.text =
                    context.getString(R.string.rating_value, stop.averageRating * 5f)
            }

            if (stop.timeEffort == null || stop.timeEffort <= 0) {
                holder.stopListTimeEffortTextView.text = context.getString(R.string.no_value)
            } else {
                holder.stopListTimeEffortTextView.text =
                    context.getString(R.string.hours_unit, stop.timeEffort)
            }

            if (stop.pricing == null || stop.pricing <= 0) {
                holder.stopListPricingTextView.text = context.getString(R.string.no_value)
            } else {
                holder.stopListPricingTextView.text = stop.pricing.toString()
            }

            if (holder.stopListImageView.drawable == null) {
                CoroutineScope(Dispatchers.Main).launch {
                    if (stop.image != null) {
                        val bitmap: Bitmap? = cityApi.getImage(stop.image)
                        if (bitmap != null) {
                            holder.stopListImageView.setImageBitmap(bitmap)
                            holder.stopListProgressIndicator.visibility = View.GONE
                        }
                    }
                }
            }
        }

        override fun getFilter(): Filter {
            return this.filter
        }

        private inner class ViewHolder internal constructor(itemView: View) :
            RecyclerView.ViewHolder(itemView) {
            var stopListCardView: CardView = itemView.findViewById(R.id.stopListCardView)
            var stopListCheckBox: CheckBox = itemView.findViewById(R.id.stopListCheckBox)
            var stopListImageView: ImageView = itemView.findViewById(R.id.stopListImageView)
            var stopListName: TextView = itemView.findViewById(R.id.stopListName)
            var stopListRatingBar: RatingBar = itemView.findViewById(R.id.stopListRatingBar)
            var stopListRatingTextView: TextView =
                itemView.findViewById(R.id.stopListRatingTextView)
            var stopListDescription: TextView =
                itemView.findViewById(R.id.stopListDescription)
            var stopListProgressIndicator: ProgressBar =
                itemView.findViewById(R.id.stopListProgressIndicator)
            var stopListCategoryTextView: TextView =
                itemView.findViewById(R.id.stopListCategoryTextView)
            var stopListTimeEffortTextView: TextView =
                itemView.findViewById(R.id.stopListTimeEffortTextView)
            var stopListPricingTextView: TextView =
                itemView.findViewById(R.id.stopListPricingTextView)

            var stopListRateButton: Button = itemView.findViewById(R.id.stopListRateButton)
            var stopListAddToTripButton: Button =
                itemView.findViewById(R.id.stopListAddToTripButton)
        }

        private inner class StopFilter(var adapter: StopListViewAdapter) : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                if (constraint != null && constraint.isEmpty()) {
                    filteredStops = stops.toMutableList()
                } else {
                    filteredStops.clear()
                    val filterPattern =
                        constraint.toString().toLowerCase(Locale.ROOT).trim { it <= ' ' }
                    for (stop in stops) {
                        val stringIdentifier = context.resources.getIdentifier(
                            "category_" + stop.category?.name, "string", context.packageName
                        )
                        if (context.getString(stringIdentifier).toLowerCase(Locale.ROOT)
                                .contains(filterPattern) or stop.name?.toLowerCase(Locale.ROOT)
                                ?.contains(filterPattern)!!
                        ) {
                            filteredStops.add(stop)
                        }
                    }
                }

                results.values = filteredStops
                results.count = filteredStops.size
                return results
            }

            override fun publishResults(
                constraint: CharSequence?,
                results: FilterResults?
            ) {
                this.adapter.notifyDataSetChanged()
            }

        }


    }

}
