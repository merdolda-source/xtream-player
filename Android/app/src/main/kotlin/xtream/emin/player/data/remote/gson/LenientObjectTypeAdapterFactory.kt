// Android/app/src/main/kotlin/com/xtream/player/data/remote/gson/LenientObjectTypeAdapterFactory.kt
package xtream.emin.player.data.remote.gson

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

/**
 * Some Xtream Codes panels serialize an empty object field (e.g. `"info": {}`)
 * as an empty array (`"info": []`) instead. Gson then throws
 * "Expected BEGIN_OBJECT but was BEGIN_ARRAY" for any data-class-typed field,
 * crashing the whole response parse. This factory wraps every non-collection
 * object adapter so a stray array in that spot is skipped and treated as null
 * rather than propagating the exception.
 */
object LenientObjectTypeAdapterFactory : TypeAdapterFactory {

    override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        val rawType = type.rawType
        val skip = rawType.isPrimitive ||
            rawType.isArray ||
            rawType.isEnum ||
            rawType.isInterface ||
            String::class.java.isAssignableFrom(rawType) ||
            Number::class.java.isAssignableFrom(rawType) ||
            Boolean::class.java.isAssignableFrom(rawType) ||
            Collection::class.java.isAssignableFrom(rawType) ||
            Map::class.java.isAssignableFrom(rawType)
        if (skip) return null

        val delegate = gson.getDelegateAdapter(this, type)
        return object : TypeAdapter<T>() {
            override fun write(out: JsonWriter, value: T) = delegate.write(out, value)

            override fun read(reader: JsonReader): T? {
                if (reader.peek() == JsonToken.BEGIN_ARRAY) {
                    reader.skipValue()
                    return null
                }
                return delegate.read(reader)
            }
        }
    }
}
