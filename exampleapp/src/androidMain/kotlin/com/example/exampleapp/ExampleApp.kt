package com.example.exampleapp

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.rpc.CoreServiceClient
import com.example.rpc.api.LoggerWrapper
import org.koin.android.ext.koin.androidContext
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.binds
import org.koin.dsl.module
import org.koin.ksp.generated.module
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ExampleApp :
    Application(),
    IResultLauncher,
    IPermissionGranter {

    /**
     * ActivityResultLauncher for launching activities and returning results asynchronously.
     */
    private var resultLauncher: ActivityResultLauncher<Intent>? = null

    /**
     * ActivityResultLauncher for requesting multiple permissions asynchronously.
     */
    private var permissionLauncher: ActivityResultLauncher<Array<String>>? = null

    /**
     * Continuation for resuming a coroutine once an activity result is returned.
     */
    private var currentLaunchContinuation: Continuation<ActivityResult>? = null

    /**
     * Continuation for resuming a coroutine once the permissions are granted or denied.
     */
    private var currentPermissionContinuation: Continuation<Map<String, Boolean>>? = null

    /**
     * Dependency Injection module that binds this application class as [IResultLauncher] and [IPermissionGranter]
     * so they can be used throughout the application.
     *
     * Also registers the LoggerWrapper for use throughout the application.
     */
    private val androidAppModule: Module = module {
        single { this@ExampleApp } binds arrayOf(IResultLauncher::class)
        single { this@ExampleApp } binds arrayOf(IPermissionGranter::class)
        single { LoggerWrapper { println(it) } }
    }

    private var currentActivity: ComponentActivity? = null

    /**
     * Instance of [KoinApplication] that handles dependency injection using the Koin framework.
     */
    private lateinit var koinApplication: KoinApplication

    /**
     * Custom [ActivityLifecycleCallbacks] used to manage the lifecycle of [ActivityResultLauncher] instances.
     * Registers the necessary launchers when an activity is created.
     */
    private val customLifecycleCallbacks = object : ActivityLifecycleCallbacks {
        /**
         * Called when an activity is created. Registers the [resultLauncher] and [permissionLauncher]
         * for the current [ComponentActivity].
         *
         * @param activity The activity that was created.
         * @param savedInstanceState The saved state of the activity, if any.
         */
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            // Cast the activity to ComponentActivity and register result launchers.
            (activity as? ComponentActivity)?.let {
                currentActivity = it
                // Register for activity result to launch intents and resume coroutine with the result.
                resultLauncher =
                    it.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                        currentLaunchContinuation?.resume(result)
                    }

                // Register for permission requests and resume coroutine with the result.
                permissionLauncher =
                    it.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
                        currentPermissionContinuation?.resume(result).also {
                            currentPermissionContinuation = null
                        }
                    }
            }

            // Set the default uncaught exception handler to a custom handler.
//            Thread.setDefaultUncaughtExceptionHandler(CrashExceptionHandler(activity, koinApplication.koin.get()))
        }

        override fun onActivityResumed(activity: Activity) {
            currentActivity = activity as? ComponentActivity
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                currentPermissionContinuation?.resume(mapOf(android.Manifest.permission.MANAGE_EXTERNAL_STORAGE to Environment.isExternalStorageManager()))
                    .also {
                        currentPermissionContinuation = null
                    }
            } else {
                currentPermissionContinuation?.resume(
                    mapOf(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE to true,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE to true
                    )
                ).also {
                    currentPermissionContinuation = null
                }
            }
        }

        // Other lifecycle callback methods that are required but not used.
        override fun onActivityStarted(activity: Activity) {}
        override fun onActivityPaused(activity: Activity) {}
        override fun onActivityStopped(activity: Activity) {}
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
        override fun onActivityDestroyed(activity: Activity) {}
    }

    /**
     * Called when the application is created. Initializes Koin with the necessary modules.
     */
    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(customLifecycleCallbacks)

        // Start Koin dependency injection with the required modules.
        koinApplication = startKoin {
            androidContext(this@ExampleApp)
            modules(
                androidAppModule,
                ExampleAppModule().module,
                CoreServiceClient().module
            )
        }
    }

    /**
     * Launches an activity using the provided [intent] and returns the result asynchronously.
     * This method suspends until the result is available.
     *
     * @param intent The intent to launch the activity with.
     * @return An [IResultLauncher.Result] indicating the result of the activity.
     */
    override suspend fun launch(intent: Intent): IResultLauncher.Result = resultLauncher?.let { launcher ->
        // Suspend coroutine until the activity result is returned.
        val result = suspendCoroutine {
            currentLaunchContinuation = it
            launcher.launch(intent)
        }
        // Return the result as an IResultLauncher.Result
        IResultLauncher.Result.ResultBack(result).also {
            currentLaunchContinuation = null
        }
    } ?: IResultLauncher.Result.NoResultLauncher

    /**
     * Requests a set of permissions from the user and returns the results as a map.
     * This method suspends until the permission result is available.
     *
     * @param permission An array of permission strings to request.
     * @return A map of permissions and whether they were granted or denied.
     */
    override suspend fun askForPermission(permission: Array<String>): Map<String, Boolean> {
        // Check to see if we already have the permission.
        val permissions =
            permission.filter { checkSelfPermission(it) != android.content.pm.PackageManager.PERMISSION_GRANTED }
        if (permissions.isEmpty()) {
            // All permissions are already granted.
            return permission.associateWith { true }
        }

        val permissionResult =
            if (permissions.contains(android.Manifest.permission.MANAGE_EXTERNAL_STORAGE) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Check if the app already has permission to manage all files.
                if (Environment.isExternalStorageManager()) {
                    // The app already has the required permission.
                    return mapOf(android.Manifest.permission.MANAGE_EXTERNAL_STORAGE to true)
                }

                // Construct the intent to request permission. Replace "net.tactware.qrg" with your application's package name.
                val uri = Uri.parse("package:${this.packageName}")

                val result = suspendCoroutine {
                    currentPermissionContinuation = it
                    // Start the activity that asks the user to grant permission for file management.
                    currentActivity?.startActivity(
                        Intent(
                            Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                            uri
                        )
                    )
                }
                result
            } else {
                // Suspend coroutine until the permission request result is returned.
                val result = suspendCoroutine {
                    currentPermissionContinuation = it
                    permissionLauncher?.launch(permission)
                }
                result
            }

        currentPermissionContinuation = null
        // Return the result of the permission request.
        return permissionResult
    }
}
