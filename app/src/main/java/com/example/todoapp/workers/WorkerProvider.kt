package com.example.todoapp.workers

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.todoapp.di.component.AppScope
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AppScope
class WorkerProvider @Inject constructor(
    private val workManager: WorkManager
) {
    fun setupWorkers() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val dataSyncRequest = PeriodicWorkRequest.Builder(
            DataSynchronizationWorker::class.java,
            8,
            TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "dataSyncWork",
            ExistingPeriodicWorkPolicy.KEEP,
            dataSyncRequest
        )
    }
}
