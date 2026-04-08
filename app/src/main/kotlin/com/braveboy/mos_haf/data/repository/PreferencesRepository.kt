package com.braveboy.mos_haf.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.flow.map

class PreferencesRepository(private val dataStore: DataStore<Preferences>) {

    suspend fun saveSetting(key: String, value: String) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = value
        }
    }

    suspend fun readSetting(key: String): String? {
        return dataStore.data.map { preferences ->
            preferences[stringPreferencesKey(key)]
        }.firstOrNull()
    }

    suspend fun readSettingAsFlow(key: String): Flow<String?> {
        return flowOf(dataStore.data.first()[stringPreferencesKey(key)])
    }
}
