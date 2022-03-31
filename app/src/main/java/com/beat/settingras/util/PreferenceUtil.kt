package com.beat.settingras.util

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import org.json.JSONArray
import org.json.JSONException
import java.util.*

/**
 * Preference Utility.
 */
object PreferenceUtil {
    private const val PREF_VALUE_DEFAULT_STRING = ""
    private const val PREF_VALUE_DEFAULT_INT = 0
    private const val PREF_VALUE_DEFAULT_BOOL = false
    private const val PREF_VALUE_DEFAULT_FLOAT = 0.0f
    private const val PREF_VALUE_DEFAULT_LONG: Long = 0

    /**
     * Gets preference value for String.
     */
    fun getString(context: Context?, key: String?): String? {
        return getString(context, key, PREF_VALUE_DEFAULT_STRING)
    }

    /**
     * Gets preference value for String with default value.
     */
    fun getString(context: Context?, key: String?, def: String?): String? {
        val pref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return pref.getString(key, def)
    }

    /**
     * Sets preference value for String.
     */
    fun setString(context: Context?, key: String?, value: String?) {
        val editor: SharedPreferences.Editor =
            PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.putString(key, value)
        editor.commit()
    }

    /**
     * Gets preference value for Integer.
     */
    fun getInt(context: Context?, key: String?): Int {
        return getInt(context, key, PREF_VALUE_DEFAULT_INT)
    }

    /**
     * Gets preference value for Integer with default value.
     */
    fun getInt(context: Context?, key: String?, def: Int): Int {
        val pref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return pref.getInt(key, def)
    }

    /**
     * Sets preference value for Integer.
     */
    fun setInt(context: Context?, key: String?, value: Int) {
        val editor: SharedPreferences.Editor =
            PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.putInt(key, value)
        editor.commit()
    }

    /**
     * Gets preference value for Boolean.
     */
    fun getBoolean(context: Context?, key: String?): Boolean {
        return getBoolean(context, key, PREF_VALUE_DEFAULT_BOOL)
    }

    /**
     * Gets preference value for Boolean with default value.
     */
    fun getBoolean(context: Context?, key: String?, def: Boolean): Boolean {
        val pref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return pref.getBoolean(key, def)
    }

    /**
     * Sets preference value for Boolean.
     */
    fun setBoolean(context: Context?, key: String?, value: Boolean) {
        val editor: SharedPreferences.Editor =
            PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.putBoolean(key, value)
        editor.commit()
    }

    /**
     * Gets preference value for Integer.
     */
    fun getLong(context: Context?, key: String?): Long {
        return getLong(context, key, PREF_VALUE_DEFAULT_LONG)
    }

    /**
     * Gets preference value for Long with default value.
     */
    fun getLong(context: Context?, key: String?, def: Long): Long {
        val pref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return pref.getLong(key, def)
    }

    /**
     * Sets preference value for float.
     */
    fun setFloat(context: Context?, key: String?, value: Float) {
        val editor: SharedPreferences.Editor =
            PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.putFloat(key, value)
        editor.commit()
    }

    /**
     * Gets preference value for float.
     */
    fun getFloat(context: Context?, key: String?): Float {
        val pref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return pref.getFloat(key, PREF_VALUE_DEFAULT_FLOAT)
    }

    /**
     * Gets preference value for float with default value.
     */
    fun getFloat(context: Context?, key: String?, def: Float): Float {
        val pref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return pref.getFloat(key, def)
    }

    /**
     * Sets preference value for Integer.
     */
    fun setLong(context: Context?, key: String?, value: Long) {
        val editor: SharedPreferences.Editor =
            PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.putLong(key, value)
        editor.commit()
    }

    fun remove(context: Context?, key: String?) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().remove(key).commit()
    }

    fun setIntegerArrayPref(context: Context?, key: String?, values: ArrayList<Int?>) {
        val editor: SharedPreferences.Editor =
            PreferenceManager.getDefaultSharedPreferences(context).edit()
        val a = JSONArray()
        for (i in values.indices) {
            a.put(values[i])
        }
        if (!values.isEmpty()) {
            editor.putString(key, a.toString())
        } else {
            editor.putString(key, null)
        }
        editor.commit()
    }

    fun getIntegerArrayPref(context: Context?, key: String?): ArrayList<Int> {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val json: String? = prefs.getString(key, null)
        val urls = ArrayList<Int>()
        if (json != null) {
            try {
                val a = JSONArray(json)
                for (i in 0 until a.length()) {
                    val url: Int = a.getInt(i)
                    urls.add(url)
                }
            } catch (e: JSONException) {
            }
        }
        return urls
    }
}