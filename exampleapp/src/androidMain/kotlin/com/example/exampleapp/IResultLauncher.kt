package com.example.exampleapp

import android.content.Intent
import androidx.activity.result.ActivityResult

/**
 * An interface for launching activities and handling their results.
 */
interface IResultLauncher {

    /**
     * Launches an activity with the given [intent] and returns the result as a [Result].
     *
     * @param intent The intent to launch the activity with.
     * @return [Result] The result of the activity launch.
     */
    suspend fun launch(intent: Intent): Result

    /**
     * A sealed class representing the results of an activity launch.
     */
    sealed class Result {
        /**
         * Represents a case where no result launcher is available.
         */
        data object NoResultLauncher : Result()

        /**
         * Represents a case where no external application is available to respond to our intent
         */
        data object NoActivityToHandle : Result()

        /**
         * Represents a successful result from the launched activity.
         *
         * @property intent The result data returned from the activity.
         */
        data class ResultBack(val intent: ActivityResult) : Result()
    }
}
