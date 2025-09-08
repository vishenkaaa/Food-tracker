package com.example.presentation.camera

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class CameraService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var imageCapture: ImageCapture? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var camera: Camera? = null
    private val lensFacing = CameraSelector.LENS_FACING_BACK
    private var cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    suspend fun initializeCamera(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView
    ): Result<Unit> {
        return try {
            val cameraProvider = getCameraProvider()
            this.cameraProvider = cameraProvider

            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build()

            cameraProvider.unbindAll()

            camera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun capturePhoto(): Result<Uri> {
        val imageCapture = imageCapture ?: return Result.failure(
            IllegalStateException("Camera not initialized")
        )

        val photoFile = createImageFile()
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        return suspendCancellableCoroutine { continuation ->
            imageCapture.takePicture(
                outputFileOptions,
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        val uri = Uri.fromFile(photoFile)
                        continuation.resume(Result.success(uri))
                    }

                    override fun onError(exception: ImageCaptureException) {
                        continuation.resume(Result.failure(exception))
                    }
                }
            )
        }
    }

    private fun hasCamera(lensFacing: Int): Boolean {
        return try {
            cameraProvider?.hasCamera(
                CameraSelector.Builder()
                    .requireLensFacing(lensFacing)
                    .build()
            ) == true
        } catch (e: Exception) {
            false
        }
    }

    fun hasFlashUnit(): Boolean {
        return camera?.cameraInfo?.hasFlashUnit() == true
    }

    fun toggleFlash(): Result<Unit> {
        return try {
            val camera = camera ?: return Result.failure(
                IllegalStateException("Camera not initialized")
            )

            val currentTorchState = camera.cameraInfo.torchState.value

            val newTorchState = when (currentTorchState) {
                TorchState.ON -> false
                TorchState.OFF -> true
                else -> return Result.failure(IllegalStateException("Torch state unknown"))
            }

            camera.cameraControl.enableTorch(newTorchState)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun releaseCamera() {
        cameraProvider?.unbindAll()
        cameraProvider = null
        cameraExecutor.shutdown()
        camera = null
        imageCapture = null
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "FOOD_${timeStamp}_"
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    private suspend fun getCameraProvider(): ProcessCameraProvider {
        return suspendCancellableCoroutine { continuation ->
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener({
                runCatching {
                    cameraProviderFuture.get()
                }.onSuccess { provider ->
                    continuation.resume(provider)
                }.onFailure { exception ->
                    continuation.resumeWithException(exception)
                }
            }, ContextCompat.getMainExecutor(context))
        }
    }
}