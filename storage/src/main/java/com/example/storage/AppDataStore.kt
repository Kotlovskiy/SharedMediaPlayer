package com.example.storage

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AppDataStore @Inject constructor(context: Context) {

    private val dataStore = PreferenceDataStoreFactory.create {
        context.preferencesDataStoreFile(DATA_STORE_NAME)
    }

    suspend fun getString(key: Preferences.Key<String>) : String? {
        return withContext(Dispatchers.IO) {
            dataStore.data.first()[key]
        }
    }

    suspend fun setString(key: Preferences.Key<String>, value: String) {
        return withContext(Dispatchers.IO) {
            dataStore.edit {
                it[key] = value
            }
        }
    }

    suspend fun clearString(key: Preferences.Key<String>) {
        withContext(Dispatchers.IO) {
            dataStore.edit {
                it.remove(key)
            }
        }
    }

    fun observeString(key: Preferences.Key<String>) : Flow<String?> {
        return dataStore.data.map { it[key] }
    }

    companion object {
        private const val DATA_STORE_NAME = "datastore"
    }
}
