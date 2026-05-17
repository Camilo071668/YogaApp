package com.juancaballero.yogaapp.ui.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.juancaballero.yogaapp.ui.utils.PoseUtils
import java.util.concurrent.Executors

@SuppressLint("UnsafeOptInUsageError")
@OptIn(ExperimentalGetImage::class)
@Composable
fun AIPoseScreen(exerciseName: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // ESTADOS
    var feedbackText by remember { mutableStateOf("Buscando cuerpo...") }
    var currentPose by remember { mutableStateOf<Pose?>(null) }
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    // LANZADOR DE PERMISOS
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasCameraPermission = granted }
    )

    // PEDIR PERMISO AL INICIAR
    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    val options = PoseDetectorOptions.Builder()
        .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
        .build()
    val poseDetector = remember { PoseDetection.getClient(options) }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        if (hasCameraPermission) {
            // 1. VISTA DE CÁMARA
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
                                        // AQUÍ LLAMAS A TU LÓGICA DE IA (PoseUtils)
                                        val result = PoseUtils.validateCobra(pose)
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

            // 2. ESQUELETO
            currentPose?.let { pose ->
                SkeletonOverlay(pose)
            }

            // 3. UI DE FEEDBACK
            Card(
                modifier = Modifier.align(Alignment.BottomCenter).padding(24.dp).fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f))
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(exerciseName.uppercase(), fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = feedbackText, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ZenFlowOrange)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = ZenFlowOrange)) {
                        Text("Terminar Sesión")
                    }
                }
            }
        } else {
            // SI NO HAY PERMISO, MOSTRAR MENSAJE
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
        pose.allPoseLandmarks.forEach { landmark ->
            drawCircle(
                color = Color.Green,
                radius = 10f,
                center = Offset(landmark.position.x, landmark.position.y)
            )
        }
    }
}