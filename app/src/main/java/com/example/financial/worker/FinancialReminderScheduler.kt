package com.example.financial.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object FinancialReminderScheduler {
    private const val REMINDER_WORK_NAME = "financial_reminder_after_exit"

    fun schedule(context: Context) {
        val reminderRequest = PeriodicWorkRequestBuilder<FinancialReminderWorker>(
            24,
            TimeUnit.HOURS
        )
            .setInitialDelay(24, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            REMINDER_WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            reminderRequest
        )
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(REMINDER_WORK_NAME)
    }
}
