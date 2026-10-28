package com.example.financial.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class FinancialReminderWorker(
    context: Context,
    workerParameters: WorkerParameters,
) : Worker(context, workerParameters) {

    override fun doWork(): Result {
        FinancialNotification.showReminder(applicationContext)
        return Result.success()
    }
}