package com.example.fareplansa

import android.graphics.Color

/**
 * Central place for all budget-status logic.
 * Every screen that shows a trip budget uses this to stay consistent.
 */
object BudgetAlertHelper {

    // Thresholds (percentages)
    const val WARN_THRESHOLD = 70      // Amber
    const val CRITICAL_THRESHOLD = 90  // Red
    const val NOTIFY_THRESHOLD_1 = 80  // First push notification
    const val NOTIFY_THRESHOLD_2 = 90  // Second push notification
    const val NOTIFY_THRESHOLD_3 = 100 // Third push notification

    // Colours matching your design palette
    private const val GREEN  = 0xFF2ECC71.toInt()
    private const val AMBER  = 0xFFF39C12.toInt()
    private const val RED    = 0xFFE74C3C.toInt()

    /**
     * Returns the percentage of the budget that has been spent (0-100).
     */
    fun spentPercent(trip: Trip): Int {
        if (trip.totalBudget <= 0.0) return 0
        val spent = trip.totalBudget - trip.remainingBudget
        return ((spent / trip.totalBudget) * 100).toInt().coerceIn(0, 100)
    }

    /**
     * Returns the traffic-light colour for the current spend percentage.
     */
    fun progressColor(trip: Trip): Int {
        val p = spentPercent(trip)
        return when {
            p >= CRITICAL_THRESHOLD -> RED
            p >= WARN_THRESHOLD -> AMBER
            else -> GREEN
        }
    }

    /**
     * Returns a short user-facing message for the status banner.
     * Returns null if we're safely under budget (no banner needed).
     */
    fun statusMessage(trip: Trip): String? {
        val p = spentPercent(trip)
        val overBy = trip.totalBudget - trip.remainingBudget - trip.totalBudget

        return when {
            p >= 100 -> "OVER BUDGET by R%,.2f".format(-trip.remainingBudget)
            p >= CRITICAL_THRESHOLD -> "Critical: %.0f%% of budget used".format(p.toDouble())
            p >= WARN_THRESHOLD -> "Warning: %.0f%% of budget used".format(p.toDouble())
            else -> null
        }
    }

    /**
     * Returns true if a push notification should be sent at this threshold.
     * Used by the Cloud Function but also referenceable client-side.
     */
    fun notificationThresholdCrossed(trip: Trip): Int? {
        val p = spentPercent(trip)
        return when {
            p >= NOTIFY_THRESHOLD_3 -> NOTIFY_THRESHOLD_3
            p >= NOTIFY_THRESHOLD_2 -> NOTIFY_THRESHOLD_2
            p >= NOTIFY_THRESHOLD_1 -> NOTIFY_THRESHOLD_1
            else -> null
        }
    }
}