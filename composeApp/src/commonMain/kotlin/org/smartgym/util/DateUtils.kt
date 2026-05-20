package org.smartgym.util

private const val MILLIS_PER_DAY = 86_400_000L

data class DateParts(val day: Int, val month: Int, val year: Int)

fun maskDateInput(value: String): String {
    val digits = value.filter(Char::isDigit).take(8)
    if (digits.length <= 2) return digits

    val day = digits.take(2)
    if (digits.length <= 4) {
        val month = digits.drop(2)
        return "$day/$month"
    }

    val month = digits.drop(2).take(2)

    val year = digits.drop(4)
    return "$day/$month/$year"
}

fun formatDateToUi(value: String): String {
    val trimmed = value.trim()
    if (trimmed.isBlank()) return ""

    // Já está em dd/MM/yyyy
    parseDateDdMmYyyy(trimmed)?.let { return formatDateDdMmYyyy(it) }

    // Extrai apenas a parte de data de datetime (ex: "2026-05-19T10:30:00" -> "2026-05-19")
    val datePart = trimmed.substringBefore('T').substringBefore(' ')

    val iso = datePart.split('-')
    if (iso.size == 3) {
        val year = iso[0].toIntOrNull()
        val month = iso[1].toIntOrNull()
        val day = iso[2].toIntOrNull()
        if (year != null && month != null && day != null) {
            val parts = DateParts(day = day, month = month, year = year)
            if (isValidDate(parts)) return formatDateDdMmYyyy(parts)
        }
    }

    return maskDateInput(trimmed)
}

fun formatDateToBackend(value: String): String {
    val parts = parseDateDdMmYyyy(value.trim()) ?: return value.trim()
    return "%04d-%02d-%02d".format(parts.year, parts.month, parts.day)
}

fun isValidUiDate(value: String): Boolean = parseDateDdMmYyyy(value) != null

fun parseDateDdMmYyyy(value: String): DateParts? {
    val parts = value.split('/')
    if (parts.size != 3) return null

    val day = parts[0].toIntOrNull() ?: return null
    val month = parts[1].toIntOrNull() ?: return null
    val year = parts[2].toIntOrNull() ?: return null

    val dateParts = DateParts(day = day, month = month, year = year)
    return dateParts.takeIf(::isValidDate)
}

fun formatDateDdMmYyyy(parts: DateParts): String {
    return "%02d/%02d/%04d".format(parts.day, parts.month, parts.year)
}

fun dateToEpochMillis(parts: DateParts): Long {
    val days = daysFromCivil(parts.year, parts.month, parts.day)
    return days * MILLIS_PER_DAY
}

fun epochMillisToDateParts(epochMillis: Long): DateParts {
    val days = floorDiv(epochMillis, MILLIS_PER_DAY)
    return civilFromDays(days)
}

private fun isValidDate(parts: DateParts): Boolean {
    if (parts.year !in 1900..2100) return false
    if (parts.month !in 1..12) return false
    val maxDay = daysInMonth(parts.month, parts.year)
    return parts.day in 1..maxDay
}

private fun daysInMonth(month: Int, year: Int): Int {
    return when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (isLeapYear(year)) 29 else 28
        else -> 0
    }
}

private fun isLeapYear(year: Int): Boolean {
    return (year % 4 == 0 && year % 100 != 0) || year % 400 == 0
}

// Algorithm adapted from Howard Hinnant's civil date conversions.
private fun daysFromCivil(year: Int, month: Int, day: Int): Long {
    var y = year
    val m = month
    y -= if (m <= 2) 1 else 0
    val era = floorDiv(y, 400)
    val yoe = y - era * 400
    val mp = m + if (m > 2) -3 else 9
    val doy = (153 * mp + 2) / 5 + day - 1
    val doe = yoe * 365 + yoe / 4 - yoe / 100 + doy
    return (era * 146097L + doe - 719468L)
}

// Algorithm adapted from Howard Hinnant's civil date conversions.
private fun civilFromDays(days: Long): DateParts {
    var z = days + 719468L
    val era = floorDiv(z, 146097L)
    val doe = z - era * 146097L
    val yoe = (doe - doe / 1460L + doe / 36524L - doe / 146096L) / 365L
    var y = yoe.toInt() + era.toInt() * 400
    val doy = doe - (365L * yoe + yoe / 4L - yoe / 100L)
    val mp = (5L * doy + 2L) / 153L
    val d = (doy - (153L * mp + 2L) / 5L + 1L).toInt()
    val m = (mp + if (mp < 10L) 3L else -9L).toInt()
    y += if (m <= 2) 1 else 0
    return DateParts(day = d, month = m, year = y)
}

private fun floorDiv(value: Long, divisor: Long): Long {
    var result = value / divisor
    if (value xor divisor < 0 && result * divisor != value) {
        result -= 1
    }
    return result
}

private fun floorDiv(value: Int, divisor: Int): Int {
    var result = value / divisor
    if (value xor divisor < 0 && result * divisor != value) {
        result -= 1
    }
    return result
}

