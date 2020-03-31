package org.travlyn.ui.home

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
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
import org.travlyn.api.model.City
import org.travlyn.api.model.Rating
import org.travlyn.api.model.Stop
import org.travlyn.components.SelectionToolbar
import org.travlyn.local.LocalStorage
import java.util.*


class StopsActivity : AppCompatActivity(), RatingDialogListener {

    private var clickedStop: Stop? = null

    private lateinit var stopListSelectionToolbar: SelectionToolbar<Stop>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stops)
        val toolbar: Toolbar = findViewById(R.id.stopsListToolbar)
        setSupportActionBar(toolbar)
        stopListSelectionToolbar = findViewById(R.id.stopListSelectionToolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        if (intent != null && intent.extras != null) {
            val city: City? =
                Gson().fromJson(intent.extras.get("city") as String?, City::class.java)
            val stopsListView: RecyclerView = findViewById(R.id.stopsListView)

            if (city != null) {
                val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                stopsListView.layoutManager = layoutManager

                stopsListView.adapter = StopListViewAdapter(city.stops!!.toList(), this)

                stopListNumberOfResultsTextView.text =
                    this.getString(R.string.number_of_stop_found, city.stops.size)
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

        val stopApi = StopApi()
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

    private inner class StopListViewAdapter(
        private val stops: List<Stop>,
        private val context: Context
    ) :
        RecyclerView.Adapter<StopListViewAdapter.ViewHolder>(), Filterable {

        private val cityApi = CityApi()
        private val filter = StopFilter(this)
        private var filteredStops: MutableList<Stop> = stops.toMutableList()

        private var isSelectable = false

        init {
            stopListSelectionToolbar.setOnCloseListener {
                isSelectable = false
                notifyDataSetChanged()
            }

            stopListSelectionToolbar.setCheckListener { selectedElements ->
                // TODO add stops to trip
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.stop_list_view, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return filteredStops.size
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
                isSelectable = !isSelectable

                stopListSelectionToolbar.toggleToolbar()
                notifyDataSetChanged()
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
                    context.getString(R.string.hours_unit, stop.timeEffort.toString())
            }

            if (stop.pricing == null || stop.pricing <= 0) {
                holder.stopListPricingTextView.text = context.getString(R.string.no_value)
            } else {
                holder.stopListPricingTextView.text = stop.pricing.toString()
            }

            if (holder.stopListImageView.drawable == null) {
                CoroutineScope(Dispatchers.IO).launch {
                    if (stop.image != null) {
                        withContext(Dispatchers.Main) {
                            val bitmap: Bitmap? = cityApi.getImage(stop.image)
                            if (bitmap != null) {
                                holder.stopListImageView.setImageBitmap(bitmap)
                                holder.stopListProgressIndicator.visibility = View.GONE
                            }
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

        private inner class StopFilter(var adapter: StopListViewAdapter) :
            Filter() {
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
                        if (stop.name?.toLowerCase(Locale.ROOT)?.contains(filterPattern)!! ||
                            context.getString(stringIdentifier).toLowerCase(Locale.ROOT)
                                .contains(filterPattern)
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
