package com.layon.myapplication.androidgooglemapsgetuserlocation

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class Permissions {

    companion object {
        fun validatePermissions(
            permissions: Array<String>,
            activity: Activity,
            requestCode: Int
        ): Boolean {
            if (Build.VERSION.SDK_INT >= 23) {
                val listOfPermissions = arrayListOf<String>()

                /* Iterate through the permissions passed, checking one by one if you already have the permission released */
                for (permission in permissions) {
                    val hasPermission = ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED

                    if (!hasPermission) listOfPermissions.add(permission)
                }

                /*I f the list is empty, it is not necessary to request permission */
                if (listOfPermissions.isEmpty()) return true

                val newPermissions = arrayOf(listOfPermissions.size.toString())
                listOfPermissions.toArray(newPermissions)

                //Request the permission
                ActivityCompat.requestPermissions(activity, newPermissions, requestCode)
            }
            return true
        }
    }
}