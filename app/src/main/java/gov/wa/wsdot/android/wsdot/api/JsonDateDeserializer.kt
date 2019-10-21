package gov.wa.wsdot.android.wsdot.api

import com.google.gson.JsonParseException
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonDeserializer
import java.lang.reflect.Type
import java.util.*

class JsonDateDeserializer : JsonDeserializer<Date> {
    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Date {
        val s = json.asJsonPrimitive.asString
        val l = java.lang.Long.parseLong(s.substring(s.indexOf("(") + 1, s.indexOf("-")))
        return Date(l)
    }
}