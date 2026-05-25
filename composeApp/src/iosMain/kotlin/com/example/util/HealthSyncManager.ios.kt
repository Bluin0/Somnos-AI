package com.example.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.api.SleepExtractedData
import platform.HealthKit.HKHealthStore
import platform.HealthKit.HKObjectType
import platform.HealthKit.HKCategoryTypeIdentifierSleepAnalysis
import platform.HealthKit.HKSampleQuery
import platform.HealthKit.HKCategorySample
import platform.HealthKit.HKQuery
import platform.HealthKit.HKSample
import platform.HealthKit.predicateForSamplesWithStartDate
import platform.Foundation.NSDate
import platform.Foundation.NSPredicate
import platform.Foundation.NSDateFormatter
import platform.Foundation.timeIntervalSince1970
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import kotlin.math.roundToInt

class IosHealthSyncManager : HealthSyncManager {
    private val healthStore = if (HKHealthStore.isHealthDataAvailable()) HKHealthStore() else null

    override val isSupported: Boolean
        get() = healthStore != null

    override fun requestPermission(onResult: (Boolean) -> Unit) {
        val store = healthStore
        if (store == null) {
            onResult(false)
            return
        }

        val sleepType = HKObjectType.categoryTypeForIdentifier(HKCategoryTypeIdentifierSleepAnalysis)
        if (sleepType == null) {
            onResult(false)
            return
        }

        val readTypes = setOf(sleepType)
        store.requestAuthorizationToShareTypes(
            typesToShare = null,
            readTypes = readTypes,
            completion = { success, error ->
                dispatch_async(dispatch_get_main_queue()) {
                    onResult(success && error == null)
                }
            }
        )
    }

    override fun fetchSleepData(onResult: (SleepExtractedData?) -> Unit) {
        val store = healthStore
        if (store == null) {
            onResult(null)
            return
        }

        val sleepType = HKObjectType.categoryTypeForIdentifier(HKCategoryTypeIdentifierSleepAnalysis)
        if (sleepType == null) {
            onResult(null)
            return
        }

        val now = NSDate()
        val yesterday = NSDate.dateWithTimeIntervalSince1970(now.timeIntervalSince1970 - 24.0 * 3600.0)
        val predicate = HKQuery.predicateForSamplesWithStartDate(yesterday, now, 0UL)

        val query = HKSampleQuery(
            sampleType = sleepType,
            predicate = predicate,
            limit = 0UL,
            sortDescriptors = null,
            resultsHandler = { _, results, error ->
                if (error != null || results == null) {
                    dispatch_async(dispatch_get_main_queue()) {
                        onResult(null)
                    }
                    return@HKSampleQuery
                }

                val sleepSamples = results.filterIsInstance<HKCategorySample>()
                if (sleepSamples.isEmpty()) {
                    dispatch_async(dispatch_get_main_queue()) {
                        onResult(null)
                    }
                    return@HKSampleQuery
                }

                var minBedtime: Double? = null
                var maxWakeup: Double? = null

                var awakeSeconds = 0.0
                var remSeconds = 0.0
                var deepSeconds = 0.0
                var essentialSeconds = 0.0

                for (sample in sleepSamples) {
                    val startVal = sample.startDate.timeIntervalSince1970
                    val endVal = sample.endDate.timeIntervalSince1970
                    val duration = endVal - startVal

                    if (duration <= 0) continue

                    val currentMin = minBedtime
                    if (currentMin == null || startVal < currentMin) {
                        minBedtime = startVal
                    }
                    val currentMax = maxWakeup
                    if (currentMax == null || endVal > currentMax) {
                        maxWakeup = endVal
                    }

                    // Sleep analysis values:
                    // Awake = 2, REM = 5, Deep = 4, Core/Asleep = 3 or 1
                    when (sample.value) {
                        2L -> awakeSeconds += duration
                        5L -> remSeconds += duration
                        4L -> deepSeconds += duration
                        3L, 1L -> essentialSeconds += duration
                    }
                }

                val finalMinBedtime = minBedtime
                val finalMaxWakeup = maxWakeup
                if (finalMinBedtime == null || finalMaxWakeup == null) {
                    dispatch_async(dispatch_get_main_queue()) {
                        onResult(null)
                    }
                    return@HKSampleQuery
                }

                val timeFormatter = NSDateFormatter().apply {
                    dateFormat = "HH:mm"
                }
                val bedtimeStr = timeFormatter.stringFromDate(NSDate.dateWithTimeIntervalSince1970(finalMinBedtime))
                val wakeupStr = timeFormatter.stringFromDate(NSDate.dateWithTimeIntervalSince1970(finalMaxWakeup))

                val awakeMinTotal = (awakeSeconds / 60.0).roundToInt()
                val remMinTotal = (remSeconds / 60.0).roundToInt()
                val deepMinTotal = (deepSeconds / 60.0).roundToInt()
                val essentialMinTotal = (essentialSeconds / 60.0).roundToInt()

                val awakeH = awakeMinTotal / 60
                val awakeM = awakeMinTotal % 60

                val remH = remMinTotal / 60
                val remM = remMinTotal % 60

                val deepH = deepMinTotal / 60
                val deepM = deepMinTotal % 60

                val essentialH = essentialMinTotal / 60
                val essentialM = essentialMinTotal % 60

                val sleepMinTotal = remMinTotal + deepMinTotal + essentialMinTotal
                val sleepH = sleepMinTotal / 60
                val sleepM = sleepMinTotal % 60

                val totalMin = awakeMinTotal + sleepMinTotal
                val awakePct = if (totalMin > 0) (awakeMinTotal.toFloat() / totalMin * 100f) else 0f
                val remPct = if (totalMin > 0) (remMinTotal.toFloat() / totalMin * 100f) else 0f
                val deepPct = if (totalMin > 0) (deepMinTotal.toFloat() / totalMin * 100f) else 0f
                val essentialPct = if (totalMin > 0) (essentialMinTotal.toFloat() / totalMin * 100f) else 0f

                val sleepData = SleepExtractedData(
                    bedtime = bedtimeStr,
                    wakeupTime = wakeupStr,
                    sleepDurationHours = sleepH,
                    sleepDurationMinutes = sleepM,
                    awakeHours = awakeH,
                    awakeMinutes = awakeM,
                    remHours = remH,
                    remMinutes = remM,
                    essentialHours = essentialH,
                    essentialMinutes = essentialM,
                    deepHours = deepH,
                    deepMinutes = deepM,
                    awakePercentage = awakePct,
                    remPercentage = remPct,
                    essentialPercentage = essentialPct,
                    deepPercentage = deepPct
                )

                dispatch_async(dispatch_get_main_queue()) {
                    onResult(sleepData)
                }
            }
        )

        store.executeQuery(query)
    }
}

@Composable
actual fun rememberHealthSyncManager(): HealthSyncManager {
    return remember { IosHealthSyncManager() }
}
