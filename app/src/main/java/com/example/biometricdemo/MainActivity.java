package com.example.biometricdemo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.crypto.KeyGenerator;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Executor newExecutor = Executors.newSingleThreadExecutor();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isBiometryAvailable()) {
                // support finger print but there are no one
                Toast.makeText(this, "You do not have a signature go to fingerprint to create create one", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(android.provider.Settings.ACTION_SECURITY_SETTINGS));

            } else {
                // support finger print and have one
                final BiometricPrompt myBiometricPrompt = new BiometricPrompt(MainActivity.this, newExecutor, new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                        } else {
                            Log.d(TAG, "An unrecoverable error occurred please try again");
                        }
                    }

                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        MainActivity.this.runOnUiThread(() -> Toast.makeText(getApplicationContext(), "onAuthenticationSucceeded", Toast.LENGTH_SHORT).show());

                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        MainActivity.this.runOnUiThread(() -> Toast.makeText(MainActivity.this, "onAuthenticationFailed", Toast.LENGTH_SHORT).show());

                    }


                });

                final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                        .setTitle("Title text goes here")
                        .setSubtitle("Subtitle goes here")
                        .setDescription("This is the description")
                        .setNegativeButtonText("Cancel")
                        .build();

                findViewById(R.id.launchAuthentication).setOnClickListener(v -> myBiometricPrompt.authenticate(promptInfo));
            }
        } else {

        }


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean isBiometryAvailable() {
        KeyStore keyStore;
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            return false;
        }

        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException |
                NoSuchProviderException e) {
            return false;
        }

        if (keyGenerator == null || keyStore == null) {
            return false;
        }

        try {
            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder("dummy_key",
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            return false;
        }
        return true;

    }
}
