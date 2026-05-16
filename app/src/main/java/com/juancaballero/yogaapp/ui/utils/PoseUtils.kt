package com.juancaballero.yogaapp.ui.utils

import com.google.mlkit.vision.pose.PoseLandmark
import kotlin.math.atan2

object PoseUtils {
    // Calcula el ángulo entre 3 puntos (en grados)
    fun getAngle(firstPoint: PoseLandmark, midPoint: PoseLandmark, lastPoint: PoseLandmark): Double {
        var result = Math.toDegrees(
            (atan2(lastPoint.position.y - midPoint.position.y, lastPoint.position.x - midPoint.position.x) -
                    atan2(firstPoint.position.y - midPoint.position.y, firstPoint.position.x - midPoint.position.x)).toDouble()
        )
        result = Math.abs(result)
        if (result > 180) result = 360 - result
        return result
    }

    // Ejemplo: Validar Postura "Cobra"
    // Brazos estirados (>160°) y espalda arqueada
    fun validateCobra(pose: com.google.mlkit.vision.pose.Pose): Pair<Boolean, String> {
        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
        val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)

        if (leftShoulder != null && leftElbow != null && leftWrist != null) {
            val armAngle = getAngle(leftShoulder, leftElbow, leftWrist)
            return if (armAngle > 160) {
                Pair(true, "¡Perfecto! Mantén la posición")
            } else {
                Pair(false, "Estira un poco más los brazos")
            }
        }
        return Pair(false, "Ubícate frente a la cámara")
    }
}