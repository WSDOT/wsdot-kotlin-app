package gov.wa.wsdot.android.wsdot.util

import org.json.JSONException
import org.json.JSONArray
import android.content.SharedPreferences

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

}