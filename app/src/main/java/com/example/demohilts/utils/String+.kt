package com.example.demohilts.utils

import java.util.*

fun String.formatDateWithTZ() =
    this.toUpperCase(Locale.US)
        .replace("T", " ")
        .replace("Z", "")
