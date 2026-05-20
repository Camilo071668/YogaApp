package com.juancaballero.yogaapp.ui.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import com.juancaballero.yogaapp.ui.theme.ZenFlowOrange
import com.juancaballero.yogaapp.ui.screens.updateProgress // Conexión a tu lógica de Firebase
import kotlinx.coroutines.delay
import java.util.concurrent.Executors

@SuppressLint("UnsafeOptInUsageError")
@OptIn(ExperimentalGetImage::class)
@Composable
fun AIPoseScreen(exerciseName: String, durationMinutes: Int = 5, onBack: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var feedbackText by remember { mutableStateOf("Buscando cuerpo...") }
    var currentPose by remember { mutableStateOf<Pose?>(null) }
    var isPoseCorrect by remember { mutableStateOf(false) }
    var secondsLeft by remember { mutableIntStateOf(durationMinutes * 60) }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasCameraPermission = granted }
    )

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    // El temporizador avanza únicamente si la IA detecta que haces la postura correctamente
    LaunchedEffect(isPoseCorrect, secondsLeft) {
        if (isPoseCorrect && secondsLeft > 0) {
            delay(1000L)
            secondsLeft -= 1
        }
    }

    val options = PoseDetectorOptions.Builder()
        .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
        .build()
    val poseDetector = remember { PoseDetection.getClient(options) }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        if (hasCameraPermission) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        val imageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()

                        imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                            val mediaImage = imageProxy.image
                            if (mediaImage != null) {
                                val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                                poseDetector.process(inputImage)
                                    .addOnSuccessListener { pose ->
                                        currentPose = pose
                                        val result = PoseUtils.validatePose(exerciseName, pose)
                                        isPoseCorrect = result.first
                                        feedbackText = result.second
                                    }
                                    .addOnCompleteListener { imageProxy.close() }
                            }
                        }

                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_FRONT_CAMERA,
                                preview,
                                imageAnalysis
                            )
                        } catch (e: Exception) { e.printStackTrace() }
                    }, ContextCompat.getMainExecutor(ctx))
                    previewView
                }
            )

            currentPose?.let { pose ->
                SkeletonOverlay(pose)
            }

            // UI del Reloj
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 48.dp, end = 24.dp)
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Timer, contentDescription = null, tint = ZenFlowOrange, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    val min = secondsLeft / 60
                    val sec = secondsLeft % 60
                    Text(
                        text = String.format("%d:%02d", min, sec),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            // Panel de Control
            Card(
                modifier = Modifier.align(Alignment.BottomCenter).padding(24.dp).fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isPoseCorrect) Color(0xFFE8F5E9).copy(alpha = 0.9f) else Color.White.copy(alpha = 0.9f)
                )
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(exerciseName.uppercase(), fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = feedbackText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isPoseCorrect) Color(0xFF2E7D32) else ZenFlowOrange
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            val minutesTrained = durationMinutes - (secondsLeft / 60)
                            if (minutesTrained > 0) {
                                updateProgress(minutesTrained)
                                Toast.makeText(context, "Saved: +$minutesTrained min", Toast.LENGTH_SHORT).show()
                            }
                            onBack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ZenFlowOrange)
                    ) {
                        Text("Terminar Sesión")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Se requiere permiso de cámara para la IA", color = Color.White)
                Button(onClick = { launcher.launch(Manifest.permission.CAMERA) }) {
                    Text("Conceder Permiso")
                }
            }
        }
    }
}

@Composable
fun SkeletonOverlay(pose: Pose) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        pose.allPoseLandmarks.forEach { landmark ->
            val scaleX = canvasWidth / 480f
            val scaleY = canvasHeight / 640f

            drawCircle(
                color = Color.Green,
                radius = 12f,
                center = Offset(landmark.position.x * scaleX, landmark.position.y * scaleY)
            )
        }
    }
}