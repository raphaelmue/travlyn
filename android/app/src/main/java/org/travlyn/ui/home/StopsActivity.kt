package org.travlyn.ui.home

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.android.synthetic.main.activity_stops.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.travlyn.R
import org.travlyn.api.CityApi
import org.travlyn.api.model.City
import org.travlyn.api.model.Stop
import java.util.*


class StopsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stops)
        val toolbar: Toolbar = findViewById(R.id.stopsListToolbar)
        setSupportActionBar(toolbar)

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
}

private class StopListViewAdapter(private val stops: List<Stop>, private val context: Context) :
    RecyclerView.Adapter<StopListViewAdapter.ViewHolder>(), Filterable {

    private val cityApi = CityApi()
    private val filter = StopFilter(this)
    private var filteredStops: MutableList<Stop> = stops.toMutableList()

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
        var stopListImageView: ImageView = itemView.findViewById(R.id.stopListImageView)
        var stopListName: TextView = itemView.findViewById(R.id.stopListName)
        var stopListRatingBar: RatingBar = itemView.findViewById(R.id.stopListRatingBar)
        var stopListRatingTextView: TextView = itemView.findViewById(R.id.stopListRatingTextView)
        var stopListDescription: TextView = itemView.findViewById(R.id.stopListDescription)
        var stopListProgressIndicator: ProgressBar =
            itemView.findViewById(R.id.stopListProgressIndicator)
        var stopListTimeEffortTextView: TextView =
            itemView.findViewById(R.id.stopListTimeEffortTextView)
        var stopListPricingTextView: TextView = itemView.findViewById(R.id.stopListPricingTextView)
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
                    if (stop.name?.toLowerCase(Locale.ROOT)?.contains(filterPattern)!!) {
                        filteredStops.add(stop)
                    }
                }
            }

            results.values = filteredStops
            results.count = filteredStops.size
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            this.adapter.notifyDataSetChanged()
        }

    }
}