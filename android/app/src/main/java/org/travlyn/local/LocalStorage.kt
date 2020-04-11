package org.travlyn.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson


/**
 * Class for reading, writing and deleting key value pairs on local device. Data is written in JSON
 * format so that serializable data objects can be stored.
 *
 * This class uses the SharedPreferences class in order to store the data.
 *
 * @since 1.0.0
 */
class LocalStorage(context: Context) {
    private val FILE_NAME = "org.travlyn.localstorage"

    var sharedPreferences: SharedPreferences

    init {
        sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Reads an object from the local storage.
     *
     * @param key key to read from
     * @return data object or null if key does not exist
     */
    inline fun <reified T> readObject(key: String): T? {
        // return null if value is null
        if (sharedPreferences.contains(key)) {
            val value: String = sharedPreferences.getString(key, "") ?: return null
            return Gson().fromJson(value, T::class.java)
        }
        return null
    }

    /**
     * Writes an data object to the local storage. The data object will be parsed into a JSON String
     * and then written into the SharedPreferences.
     *
     * @param key key to write to
     * @param value value to write
     * @return void
     */
    inline fun <reified T> writeObject(key: String, value: T?) {
        sharedPreferences.edit {
            putString(key, Gson().toJson(value))
        }
    }

    /**
     * Deletes an object from the local storage.
     *
     * @param key key to delete
     * @return void
     */
    fun deleteObject(key: String) {
        sharedPreferences.edit {
            remove(key)
        }
    }

    /**
     * Checks if the local storage contains an object with the key
     */
    fun contains(key: String): Boolean {
        return sharedPreferences.contains(key)
    }
}