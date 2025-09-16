@file:UseSerializers(
    LocaleAsLanguageTagSerializer::class,
    CalendarAsEpochMillisSerializer::class
)

package com.metacomputing.namespring.model.dto

import com.metacomputing.namespring.model.data.Profile
import com.metacomputing.namespring.model.data.Profile.Companion.Gender
import com.metacomputing.namespring.utils.getOrEmpty
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

private class LocaleAsLanguageTagSerializer : KSerializer<Locale> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocaleAsLanguageTag", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Locale) {
        encoder.encodeString(value.toLanguageTag())
    }

    override fun deserialize(decoder: Decoder): Locale {
        return Locale.forLanguageTag(decoder.decodeString())
    }
}

private class CalendarAsEpochMillisSerializer : KSerializer<Calendar> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("CalendarAsEpochMillis", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: Calendar) {
        encoder.encodeLong(value.timeInMillis)
    }

    override fun deserialize(decoder: Decoder): Calendar {
        val millis = decoder.decodeLong()
        return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = millis }
    }
}

@Serializable
data class DTOProfile(
    val title: String,
    val locale: Locale,
    val birthDate: Calendar,
    @param:Gender val gender: String,
    val firstName: String,
    val firstNameHanja: String,
    val familyName: String,
    val familyNameHanja: String,
    val id: String) {
    companion object {
        fun from(jsonString: String): DTOProfile {
            return DTO.JSON.decodeFromString<DTOProfile>(jsonString)
        }

        fun from(profile: Profile): DTOProfile {
            with (profile) {
                return DTOProfile(
                    title = title.getOrEmpty(),
                    locale = locale,
                    birthDate = birthDate,
                    gender = gender.getOrEmpty(),
                    firstName = firstName.getOrEmpty(),
                    firstNameHanja = firstNameHanja.getOrEmpty(),
                    familyName = familyName.getOrEmpty(),
                    familyNameHanja = familyNameHanja.getOrEmpty(),
                    id = id,
                )
            }
        }
    }

    val jsonString: String
        get() = DTO.JSON.encodeToString(serializer<DTOProfile>(), this)

    fun toProfile(): Profile {
        return Profile(
            title,
            locale,
            birthDate,
            gender,
            firstName,
            firstNameHanja,
            familyName,
            familyNameHanja,
            id
        )
    }
}