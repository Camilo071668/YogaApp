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
            exerciseName.contains("Morning", ignoreCase = true) || exerciseName.contains("Cobra", ignoreCase = true) -> {
                validateCobra(pose)
            }
            exerciseName.contains("Pause", ignoreCase = true) || exerciseName.contains("Flexibility", ignoreCase = true) -> {
                validateTreePose(pose)
            }
            exerciseName.contains("Office", ignoreCase = true) || exerciseName.contains("Chair", ignoreCase = true) -> {
                validateChairPose(pose)
            }
            exerciseName.contains("Strength", ignoreCase = true) || exerciseName.contains("Power", ignoreCase = true) -> {
                validatePlank(pose)
            }
            exerciseName.contains("Night", ignoreCase = true) || exerciseName.contains("Relaxation", ignoreCase = true) -> {
                validateWarriorPose(pose)
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

    // --- NUEVA POSE: SILLA (Para Office Pause) ---
    private fun validateChairPose(pose: Pose): Pair<Boolean, String> {
        val hip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
        val knee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
        val ankle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)
        val wrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
        val shoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)

        if (hip != null && knee != null && ankle != null && wrist != null && shoulder != null) {
            val kneeAngle = getAngle(hip, knee, ankle)
            val areArmsUp = wrist.position.y < shoulder.position.y

            return when {
                kneeAngle > 145 -> Pair(false, "Baja más la cadera (como sentado)")
                !areArmsUp -> Pair(false, "Sube los brazos al cielo")
                else -> Pair(true, "¡Silla perfecta! Mantén la fuerza")
            }
        }
        return Pair(false, "Ubícate de lado para la Silla")
    }

    // --- NUEVA POSE: PLANCHA (Para Strength / Power) ---
    private fun validatePlank(pose: Pose): Pair<Boolean, String> {
        val shoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val hip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
        val knee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)

        if (shoulder != null && hip != null && knee != null) {
            val bodyLine = getAngle(shoulder, hip, knee)
            // Una plancha debe estar casi recta (cerca de 180 grados)
            return if (bodyLine > 165) Pair(true, "¡Plancha sólida! Mantén el abdomen firme")
            else Pair(false, "No bajes ni subas mucho la cadera")
        }
        return Pair(false, "Ponte en posición de tabla (Plancha)")
    }

    // --- NUEVA POSE FÁCIL: ESTIRAMIENTO LATERAL (Para Night Relaxation) ---
    private fun validateSideStretch(pose: Pose): Pair<Boolean, String> {
        val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
        val rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
        val head = pose.getPoseLandmark(PoseLandmark.NOSE)

        if (leftWrist != null && rightWrist != null && head != null) {
            // Chequeamos si al menos una mano está por encima de la cabeza
            // (En coordenadas de pantalla, menor Y significa más arriba)
            val isLeftArmUp = leftWrist.position.y < head.position.y
            val isRightArmUp = rightWrist.position.y < head.position.y

            return if (isLeftArmUp || isRightArmUp) {
                Pair(true, "¡Bien! Estira tu costado y respira...")
            } else {
                Pair(false, "Sube un brazo sobre tu cabeza y inclínate")
            }
        }
        return Pair(false, "Ubícate frente a la cámara")
    }
}

