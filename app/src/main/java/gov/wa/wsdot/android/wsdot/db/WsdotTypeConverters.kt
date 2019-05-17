package gov.wa.wsdot.android.wsdot.db

import androidx.room.TypeConverter
import java.util.*

class WsdotTypeConverters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

}
