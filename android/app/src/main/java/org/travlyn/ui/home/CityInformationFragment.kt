package org.travlyn.ui.home

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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


