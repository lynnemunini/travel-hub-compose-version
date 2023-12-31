package com.grayseal.travelhubcompose.ui.screens.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.grayseal.travelhubcompose.data.model.TravelItem
import com.grayseal.travelhubcompose.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStreamReader

/**
 * ViewModel class for managing travel entries.
 * This class provides methods to fetch travel entries from a JSON file and retrieve
 * entries by their IDs.
 */
class EntriesViewModel : ViewModel() {

    /**
     * Retrieves all travel entries.
     *
     * @param context The Android application context.
     * @param callback A callback function to handle the list of travel entries once fetched.
     */
    fun getAllEntries(context: Context, callback: (List<TravelItem>) -> Unit) {
        // Allow asynchronous execution without blocking the main thread and affecting UI performance
        viewModelScope.launch {
            val entries = withContext(Dispatchers.IO) {
                // Read the json file
                val jsonFile = context.resources.openRawResource(R.raw.listings)
                val reader = InputStreamReader(jsonFile)

                val listType = object : TypeToken<List<TravelItem>>() {}.type
                // Use Gson library to deserialize the JSON content
                Gson().fromJson<List<TravelItem>>(reader, listType)
            }
            callback(entries)
        }
    }

    /**
     * Retrieves a travel entry by its unique ID.
     *
     * @param id The ID of the travel entry to retrieve.
     * @param context The Android application context.
     * @param callback A callback function to handle the retrieved travel entry.
     */
    fun getEntryById(id: String, context: Context, callback: (TravelItem?) -> Unit) {
        viewModelScope.launch {
            val entries = withContext(Dispatchers.IO) {
                val jsonFile = context.resources.openRawResource(R.raw.listings)
                val reader = InputStreamReader(jsonFile)

                val listType = object : TypeToken<List<TravelItem>>() {}.type
                Gson().fromJson<List<TravelItem>>(reader, listType)
            }

            // Find the entry with the specified id
            val entry = entries.find { it._id == id }

            callback(entry)
        }
    }
}