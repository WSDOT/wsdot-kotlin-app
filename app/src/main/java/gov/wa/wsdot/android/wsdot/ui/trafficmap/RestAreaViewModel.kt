package gov.wa.wsdot.android.wsdot.ui.trafficmap

import androidx.lifecycle.*
import gov.wa.wsdot.android.wsdot.R
import gov.wa.wsdot.android.wsdot.model.RestAreaItem
import gov.wa.wsdot.android.wsdot.util.AbsentLiveData
import org.json.JSONArray
import org.json.JSONException
import javax.inject.Inject

class RestAreaViewModel @Inject constructor(): ViewModel() {

    private val _restAreaData: MutableLiveData<RestAreaData> = MutableLiveData()

    // Creates a live data using the rest area json provided by the main activity/fragment
    // utilizes liveData builder to move work onto a coroutine
    val restAreas: LiveData<List<RestAreaItem>> = Transformations
        .switchMap(_restAreaData) { jsonString ->
            jsonString.ifExists {
                liveData {
                    emit(getRestAreas(it))
                }
            }
        }

    data class RestAreaData(val jsonString: String) {
        fun <T> ifExists(f: (String) -> LiveData<T>): LiveData<T> {
            return if (jsonString.isEmpty()) {
                AbsentLiveData.create()
            } else {
                f(jsonString)
            }
        }
    }

    fun setRestAreaData(data: String) {
        _restAreaData.value = RestAreaData(data)
    }

    private fun getRestAreas(jsonString: String): List<RestAreaItem> {

        val restAreas = arrayListOf<RestAreaItem>()

        val restarea = R.drawable.restarea
        val restarea_dump = R.drawable.restarea_dump

        var restAreasJSON: JSONArray? = null

        try {
            restAreasJSON = JSONArray(jsonString)

            for (i in 0 until restAreasJSON.length()) {

                val restAreaJSON = restAreasJSON.getJSONObject(i)

                val amenitiesJSON = restAreaJSON.getJSONArray("amenities")
                val amenities = arrayListOf<String>()
                for (j in 0 until amenitiesJSON.length()) {
                    amenities.add(amenitiesJSON.getString(j))
                }

                var item = RestAreaItem(
                    restAreaJSON.getString("location"),
                    restAreaJSON.getString("route"),
                    restAreaJSON.getInt("milepost"),
                    restAreaJSON.getString("direction"),
                    restAreaJSON.getDouble("latitude"),
                    restAreaJSON.getDouble("longitude"),
                    (if (restAreaJSON.getBoolean("hasDump")) restarea_dump else restarea),
                    restAreaJSON.getString("notes"),
                    amenities
                )

                restAreas.add(item)
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return restAreas

    }

}