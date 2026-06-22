package com.erdin.player.common.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Xtream panels report `exp_date` as a Unix epoch in seconds, but some
 * trial/unlimited accounts return null, blank or a non-numeric sentinel
 * value instead - this formats either case into something safe to show.
 */
object ExpiryFormatter {

    fun format(expDate: String?): String? {
        val epochSeconds = expDate?.toLongOrNull() ?: return null
        if (epochSeconds <= 0) return null
        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return formatter.format(Date(epochSeconds * 1000))
    }

    fun isExpired(expDate: String?): Boolean {
        val epochSeconds = expDate?.toLongOrNull() ?: return false
        if (epochSeconds <= 0) return false
        return epochSeconds * 1000 < System.currentTimeMillis()
    }
}
