package gov.wa.wsdot.android.wsdot.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import gov.wa.wsdot.android.wsdot.api.response.ferries.FerryTerminalResponse
import gov.wa.wsdot.android.wsdot.db.tollrates.constant.TollRateRow
import gov.wa.wsdot.android.wsdot.db.tollrates.dynamic.TollTrip
import java.util.*
import kotlin.collections.ArrayList

class WsdotTypeConverters {

    // TollTrip list to name
    @TypeConverter
    fun tollTripFromString(value: String): MutableList<TollTrip> {
        val rowType = object : TypeToken<List<TollTrip>>() {}.type
        return Gson().fromJson<MutableList<TollTrip>>(value, rowType)
    }

    @TypeConverter
    fun tollTripToString(value: MutableList<TollTrip>): String {
        return Gson().toJson(value)
    }

    // TollRateRow list to name
    @TypeConverter
    fun tollRateRowFromString(value: String): List<TollRateRow> {
        val rowType = object : TypeToken<List<TollRateRow>>() {}.type
        return Gson().fromJson<List<TollRateRow>>(value, rowType)
    }

    @TypeConverter
    fun tollRateRowToString(value: List<TollRateRow>): String {
        return Gson().toJson(value)
    }

    // Long to date
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    // String to array name
    @TypeConverter
    fun fromString(value: String?): List<String>? {

        if (value == null) {
            return value
        }

        val array = value.split(",")
        val list = ArrayList<String>()

        for (s in array) {
            if (s.isNotEmpty()) {
                list.add(s)
            }
        }
        return list
    }

    @TypeConverter
    fun stringArrayToString(list: List<String>?): String? {

        if (list == null){
            return list
        }

        var value = ""
        for (i in list) {
            value += ",$i"
        }
        return value
    }

    @TypeConverter
    fun fromBulletinList(bulletins: List<FerryTerminalResponse.BulletinAlert>): String {
        return Gson().toJson(bulletins)
    }

    @TypeConverter
    fun toBulletinList(bulletinString: String): List<FerryTerminalResponse.BulletinAlert> {
        val bulletins = Gson().fromJson(bulletinString, Array<FerryTerminalResponse.BulletinAlert>::class.java) as Array<FerryTerminalResponse.BulletinAlert>
        return bulletins.toList()
    }




}
