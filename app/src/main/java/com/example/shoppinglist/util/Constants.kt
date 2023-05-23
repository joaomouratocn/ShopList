package com.example.shoppinglist.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

//constante para passa o id da lista para as activitys
const val LIST_ID = "LIST_ID"

//chave datastore
val SUM_VALUES = booleanPreferencesKey("sum_values")

val Context.dataStore:DataStore<Preferences> by preferencesDataStore(name = "preferences")

