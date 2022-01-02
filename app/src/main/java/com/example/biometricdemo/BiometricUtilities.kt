package com.example.biometricdemo

import android.content.Context
import android.os.Build
import androidx.core.hardware.fingerprint.FingerprintManagerCompat

object BiometricUtilities {
    fun isBiometricPromptEnabled(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
    }
    fun isSdkVersionSupported(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }
    fun isHardwareSupported(context: Context): Boolean {
        val fingerprintManager = FingerprintManagerCompat.from(context)
        return fingerprintManager.isHardwareDetected
    }
    fun isFingerprintAvailable(context: Context): Boolean {
        val fingerprintManager = FingerprintManagerCompat.from(context)
        return fingerprintManager.hasEnrolledFingerprints()
    }
}