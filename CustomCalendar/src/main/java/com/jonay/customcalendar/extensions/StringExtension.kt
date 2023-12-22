package com.jonay.customcalendar.extensions

import java.util.Locale

fun String.capitalizeFirstCharacter(): String = this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }