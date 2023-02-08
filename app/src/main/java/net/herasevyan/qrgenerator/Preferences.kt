package net.herasevyan.qrgenerator

import android.content.SharedPreferences

class SharedPrefs(private val sharedPreferences: SharedPreferences) : SharedPreferences by sharedPreferences {

    fun save(key: String, item: Int) {
        with(edit()) {
            putInt(key, item)
            apply()
        }
    }

    fun save(key: String, item: Long) {
        with(edit()) {
            putLong(key, item)
            apply()
        }
    }

    fun save(key: String, item: Float) {
        with(edit()) {
            putFloat(key, item)
            apply()
        }
    }

    fun save(key: String, item: Boolean) {
        with(edit()) {
            putBoolean(key, item)
            apply()
        }
    }

    fun save(key: String, item: String) {
        with(edit()) {
            putString(key, item)
            apply()
        }
    }

    fun save(key: String, item: Set<String>) {
        with(edit()) {
            putStringSet(key, item)
            apply()
        }
    }
}