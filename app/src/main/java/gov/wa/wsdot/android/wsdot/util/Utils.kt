package gov.wa.wsdot.android.wsdot.util

import org.json.JSONException
import org.json.JSONArray
import android.content.SharedPreferences
import android.util.Log

object Utils {

    fun loadOrderedIntList(key: String, sharedPreferences: SharedPreferences): ArrayList<Int> {
        val arrayList = mutableListOf<Int>()
        return try {
            val jsonArray = JSONArray(sharedPreferences.getString(key, "[]"))
            for (i in 0 until jsonArray.length()) {
                arrayList.add(jsonArray.getInt(i))
            }
            arrayList as ArrayList<Int>
        } catch (e: JSONException) {
            arrayList as ArrayList<Int>
        }
    }

    fun saveOrderedIntList(receivedAlerts: ArrayList<Int>, key: String, sharedPreferences: SharedPreferences) {

        // keep list size <= 10
        while (receivedAlerts.size > 10) {
            receivedAlerts.remove(0)
        }

        try {
            val jsonArray =  JSONArray(receivedAlerts)
            val editor = sharedPreferences.edit()
            editor.putString(key, jsonArray.toString())
            editor.apply()
        } catch (e: JSONException) {
            Log.e("Utils", "failed to save int list to shared prefs")
        }
    }

}