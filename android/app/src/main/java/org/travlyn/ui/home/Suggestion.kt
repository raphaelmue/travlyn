package org.travlyn.ui.home

import android.os.Parcel
import android.os.Parcelable.Creator
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion

class Suggestion : SearchSuggestion {
    private var name: String
    var isHistory = true

    constructor(suggestion: String) {
        name = suggestion
    }

    constructor(source: Parcel) {
        name = source.readString()
        isHistory = source.readInt() != 0
    }

    override fun getBody(): String {
        return name
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeInt(if (isHistory) 1 else 0)
    }

    companion object {
        @JvmField val CREATOR: Creator<Suggestion?> = object : Creator<Suggestion?> {
            override fun createFromParcel(`in`: Parcel): Suggestion? {
                return Suggestion(`in`)
            }

            override fun newArray(size: Int): Array<Suggestion?> {
                return arrayOfNulls(size)
            }
        }
    }
}