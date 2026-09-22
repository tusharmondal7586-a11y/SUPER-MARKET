package com.example.game.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

object GameAudioEngine {
    private var isMuted = false
    private var isHapticsEnabled = true
    private val scope = CoroutineScope(Dispatchers.Default)

    fun setMuted(muted: Boolean) {
        isMuted = muted
    }

    fun isMuted(): Boolean = isMuted

    fun setHapticsEnabled(enabled: Boolean) {
        isHapticsEnabled = enabled
    }

    fun isHapticsEnabled(): Boolean = isHapticsEnabled

    fun playPop(context: Context? = null) {
        triggerHaptic(context, 20)
        if (isMuted) return
        scope.launch {
            // Rapid pitch drop pop
            val sampleRate = 44100
            val durationMs = 80
            val numSamples = (durationMs * sampleRate) / 1000
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val progress = i.toDouble() / numSamples
                val freq = 800.0 - (progress * 400.0)
                val envelope = 1.0 - progress
                val angle = 2.0 * PI * i * freq / sampleRate
                buffer[i] = (sin(angle) * Short.MAX_VALUE * envelope * 0.7).toInt().toShort()
            }
            playPcm(buffer, sampleRate)
        }
    }

    fun playMergeSuccess(context: Context? = null) {
        triggerHaptic(context, 40)
        if (isMuted) return
        scope.launch {
            // 2-tone bright chord (Pop + Bell chime)
            val sampleRate = 44100
            val durationMs = 200
            val numSamples = (durationMs * sampleRate) / 1000
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val progress = i.toDouble() / numSamples
                val envelope = (1.0 - progress) * (1.0 - progress)
                val f1 = 523.25 // C5
                val f2 = 659.25 // E5
                val f3 = 783.99 // G5
                val v1 = sin(2.0 * PI * i * f1 / sampleRate)
                val v2 = sin(2.0 * PI * i * f2 / sampleRate)
                val v3 = sin(2.0 * PI * i * f3 / sampleRate)
                buffer[i] = (((v1 + v2 + v3) / 3.0) * Short.MAX_VALUE * envelope * 0.8).toInt().toShort()
            }
            playPcm(buffer, sampleRate)
        }
    }

    fun playChaChing(context: Context? = null) {
        triggerHaptic(context, 35)
        if (isMuted) return
        scope.launch {
            // Cash register ding-ding!
            val sampleRate = 44100
            val durationMs = 260
            val numSamples = (durationMs * sampleRate) / 1000
            val buffer = ShortArray(numSamples)

            val split = numSamples / 2
            for (i in 0 until numSamples) {
                val isFirst = i < split
                val localIndex = if (isFirst) i else (i - split)
                val localTotal = if (isFirst) split else (numSamples - split)
                val progress = localIndex.toDouble() / localTotal
                val freq = if (isFirst) 987.77 else 1318.51 // B5 -> E6
                val envelope = (1.0 - progress)
                val angle = 2.0 * PI * i * freq / sampleRate
                buffer[i] = (sin(angle) * Short.MAX_VALUE * envelope * 0.75).toInt().toShort()
            }
            playPcm(buffer, sampleRate)
        }
    }

    fun playHappyCustomer(context: Context? = null) {
        triggerHaptic(context, 25)
        if (isMuted) return
        scope.launch {
            val sampleRate = 44100
            val notes = doubleArrayOf(523.25, 659.25, 783.99, 1046.50) // C5, E5, G5, C6
            val noteDurationMs = 60
            val totalSamples = (notes.size * noteDurationMs * sampleRate) / 1000
            val buffer = ShortArray(totalSamples)

            val samplesPerNote = (noteDurationMs * sampleRate) / 1000
            for (n in notes.indices) {
                val freq = notes[n]
                val offset = n * samplesPerNote
                for (i in 0 until samplesPerNote) {
                    val progress = i.toDouble() / samplesPerNote
                    val envelope = 1.0 - progress * 0.8
                    val angle = 2.0 * PI * i * freq / sampleRate
                    buffer[offset + i] = (sin(angle) * Short.MAX_VALUE * envelope * 0.65).toInt().toShort()
                }
            }
            playPcm(buffer, sampleRate)
        }
    }

    fun playLevelUp(context: Context? = null) {
        triggerHaptic(context, 70)
        if (isMuted) return
        scope.launch {
            val sampleRate = 44100
            val notes = doubleArrayOf(440.0, 554.37, 659.25, 880.0) // A4, C#5, E5, A5
            val noteDurationMs = 90
            val totalSamples = (notes.size * noteDurationMs * sampleRate) / 1000
            val buffer = ShortArray(totalSamples)

            val samplesPerNote = (noteDurationMs * sampleRate) / 1000
            for (n in notes.indices) {
                val freq = notes[n]
                val offset = n * samplesPerNote
                for (i in 0 until samplesPerNote) {
                    val progress = i.toDouble() / samplesPerNote
                    val envelope = 1.0 - (progress * 0.6)
                    val angle = 2.0 * PI * i * freq / sampleRate
                    buffer[offset + i] = (sin(angle) * Short.MAX_VALUE * envelope * 0.75).toInt().toShort()
                }
            }
            playPcm(buffer, sampleRate)
        }
    }

    private fun playPcm(buffer: ShortArray, sampleRate: Int) {
        try {
            val minBufSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2.coerceAtLeast(minBufSize))
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            // Release after playing
            scope.launch {
                kotlinx.coroutines.delay(600)
                try {
                    audioTrack.stop()
                    audioTrack.release()
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {
            // Audio output may be disabled or unavailable
        }
    }

    private fun triggerHaptic(context: Context?, durationMs: Long) {
        if (!isHapticsEnabled || context == null) return
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                manager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            if (vibrator?.hasVibrator() == true) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(durationMs)
                }
            }
        } catch (_: Exception) {}
    }
}
