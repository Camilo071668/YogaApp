package com.juancaballero.yogaapp.ui.utils

import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import kotlin.math.atan2

object PoseUtils {

    fun getAngle(firstPoint: PoseLandmark, midPoint: PoseLandmark, lastPoint: PoseLandmark): Double {
        var result = Math.toDegrees(
            (atan2(lastPoint.position.y - midPoint.position.y, lastPoint.position.x - midPoint.position.x) -
                    atan2(firstPoint.position.y - midPoint.position.y, firstPoint.position.x - midPoint.position.x)).toDouble()
        )
        result = Math.abs(result)
        if (result > 180) result = 360 - result
        return result
    }

    fun validatePose(exerciseName: String, pose: Pose): Pair<Boolean, String> {
        return when {
            exerciseName.contains("Stretch", ignoreCase = true) || exerciseName.contains("Cobra", ignoreCase = true) -> {
                validateCobra(pose)
            }
            exerciseName.contains("Pause", ignoreCase = true) || exerciseName.contains("Flexibility", ignoreCase = true) -> {
                validateTreePose(pose)
            }
            else -> {
                validateWarriorPose(pose)
            }
        }
    }

    private fun validateCobra(pose: Pose): Pair<Boolean, String> {
        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
        val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)

        if (leftShoulder != null && leftElbow != null && leftWrist != null) {
            val armAngle = getAngle(leftShoulder, leftElbow, leftWrist)
            return if (armAngle > 150) {
                Pair(true, "¡Perfecto! Mantén la posición de Cobra")
            } else {
                Pair(false, "Estira un poco más los brazos")
            }
        }
        return Pair(false, "Ubícate frente a la cámara (Cobra)")
    }

    private fun validateTreePose(pose: Pose): Pair<Boolean, String> {
        val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
        val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
        val leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)

        if (leftWrist != null && leftShoulder != null && leftKnee != null && leftHip != null && leftAnkle != null) {
            val isHandAboveHead = leftWrist.position.y < leftShoulder.position.y
            val kneeAngle = getAngle(leftHip, leftKnee, leftAnkle)

            return when {
                !isHandAboveHead -> Pair(false, "Sube las manos por encima de tu cabeza")
                kneeAngle > 140 -> Pair(false, "Dobla una rodilla apoyando el pie en tu pierna")
                else -> Pair(true, "¡Excelente postura del Árbol! Mantén el equilibrio")
            }
        }
        return Pair(false, "Ponte de pie frente a la cámara")
    }

    private fun validateWarriorPose(pose: Pose): Pair<Boolean, String> {
        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
        val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
        val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
        val rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
        val rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)

        if (leftShoulder != null && leftElbow != null && leftWrist != null &&
            rightShoulder != null && rightElbow != null && rightWrist != null) {

            val leftArmAngle = getAngle(leftShoulder, leftElbow, leftWrist)
            val rightArmAngle = getAngle(rightShoulder, rightElbow, rightWrist)

            return if (leftArmAngle > 140 && rightArmAngle > 140) {
                Pair(true, "¡Guerrero perfecto! Mantén tus brazos firmes")
            } else {
                Pair(false, "Estira ambos brazos horizontalmente")
            }
        }
        return Pair(false, "Ubícate frente a la cámara de cuerpo completo")
    }
}