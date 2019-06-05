package gov.wa.wsdot.android.wsdot.db

import androidx.room.TypeConverter
import java.util.*
import kotlin.collections.ArrayList

class WsdotTypeConverters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }


    @TypeConverter
    fun fromString(value: String?): List<String>? {

        if (value == null) {
            return value
        }

        val array = value.split(",")
        val list = ArrayList<String>()

        for (s in array) {
            if (!s.isEmpty()) {
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

}
