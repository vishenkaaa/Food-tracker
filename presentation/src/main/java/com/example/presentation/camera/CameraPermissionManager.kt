package com.example.presentation.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.Permissions
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CameraPermissionManager @Inject constructor(
    @ApplicationContext private val context: Context
){
    companion object{
        val REQUIRED_PERMISSIONS = buildList {
            add((Manifest.permission.CAMERA))
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                add(Manifest.permission.READ_MEDIA_IMAGES)
            else
                add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }.toTypedArray()
    }

    fun hasCameraPermission(): Boolean{
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasStoragePermission(): Boolean{
        return if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU)
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        else
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasAllPermissions(): Boolean{
        return REQUIRED_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun getMissingPermissions(): List<String>{
        return REQUIRED_PERMISSIONS.filter { permission ->
            ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
        }
    }

    fun isCameraAvailable(): Boolean{
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }
}