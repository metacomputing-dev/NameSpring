package com.metacomputing.namespring.model.dto

import kotlinx.serialization.json.Json

object DTO {
    val JSON = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }
}