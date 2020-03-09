package org.travlyn.ui.home

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andrefrsousa.superbottomsheet.SuperBottomSheetFragment
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_city_information.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.travlyn.MainActivity
import org.travlyn.R
import org.travlyn.api.CityApi
import org.travlyn.api.model.City
import org.travlyn.api.model.Stop


class CityInformationFragment : SuperBottomSheetFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        return inflater.inflate(R.layout.fragment_city_information, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.getColor(R.color.white)
            ?.let { cityCollapsingToolbar.setCollapsedTitleTextColor(it) }

        cityDescriptionTextView.setOnClickListener {
            cityDescriptionTextView.maxLines = if (cityDescriptionTextView.maxLines == 4) {
                cityDescriptionTextView.ellipsize = null
                Integer.MAX_VALUE
            } else {
                cityDescriptionTextView.ellipsize = TextUtils.TruncateAt.END
                4
            }
        }

        val city: City? = Gson().fromJson(arguments?.get("city") as String?, City::class.java)
        if (city != null) {
            cityInformationToolbar.title = city.name
            cityCollapsingToolbar.title = city.name
            cityDescriptionTextView.text = city.description
            initializeStops(city)

            CoroutineScope(Dispatchers.IO).launch {
                if (city.image != null) {
                    withContext(Dispatchers.Main) {
                        cityThumbnailImageView.setImageBitmap(
                            CityApi(application = activity as MainActivity).getImage(
                                city.image
                            )
                        )
                    }
                }
            }
        }
    }

    private fun initializeStops(city: City?) {
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        stopCardRecyclerView.layoutManager = layoutManager

        if (context != null && city != null && city.stops != null) {
            val adapter = StopCardViewAdapter(city.stops.toList(), context!!)
            stopCardRecyclerView.adapter = adapter
        }
    }

    override fun getDim(): Float {
        return 0f
    }

    companion object {
        @JvmStatic
        fun newInstance(city: City?) = CityInformationFragment().apply {
            arguments = Bundle().apply {
                putString("city", Gson().toJson(city))
            }
        }
    }

}

private class StopCardViewAdapter(private val stops: List<Stop>, private val context: Context) :
    RecyclerView.Adapter<StopCardViewAdapter.ViewHolder>() {

    private val cityApi = CityApi(application = context as MainActivity)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.stop_card_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return stops.size;
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val stop: Stop = stops[position]

        holder.stopCardName.text = stop.name
        holder.stopCardDescription.text = stop.description

        if (holder.stopCardImageView.drawable == null) {
            CoroutineScope(Dispatchers.IO).launch {
                if (stop.image != null) {
                    withContext(Dispatchers.Main) {
                        val bitmap: Bitmap? = cityApi.getImage(stop.image)
                        if (bitmap != null) {
                            holder.stopCardImageView.setImageBitmap(bitmap)
                            holder.stopCardProgressIndicator.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var stopCardImageView: ImageView = itemView.findViewById(R.id.stopCardImageView)
        var stopCardName: TextView = itemView.findViewById(R.id.stopCardName)
        var stopCardDescription: TextView = itemView.findViewById(R.id.stopCardDescription)
        var stopCardProgressIndicator: ProgressBar =
            itemView.findViewById(R.id.stopCardProgressIndicator)
    }
}


