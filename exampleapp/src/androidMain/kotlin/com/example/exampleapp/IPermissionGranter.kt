package com.example.exampleapp

/**
 * Interface that defines the contract for requesting permissions in a suspendable manner.
 * Classes that implement this interface are responsible for handling permission requests
 * and returning the result asynchronously.
 */
interface IPermissionGranter {

    /**
     * Requests a set of permissions from the user.
     *
     * This function suspends until the permissions have been granted or denied by the user.
     * It returns a map where the keys are the requested permission strings and the values
     * are booleans indicating whether the permission was granted (`true`) or denied (`false`).
     *
     * @param permission An array of permission strings that are being requested. Each permission
     * string represents a specific permission such as `android.permission.CAMERA`.
     *
     * @return A [Map] where the keys are permission strings and the values are booleans
     * indicating whether each permission was granted or denied.
     *
     * @throws SecurityException If there is an issue during the permission request process.
     */
    suspend fun askForPermission(permission: Array<String>): Map<String, Boolean>
}
