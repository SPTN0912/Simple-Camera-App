
package com.example.simplecameraapp

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CameraApp()
        }
    }
}

@Composable
fun CameraApp() {

    val context = LocalContext.current

    // Store the captured photograph
    var capturedImage by remember {
        mutableStateOf<Bitmap?>(null)
    }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->

        if (bitmap != null) {
            capturedImage = bitmap
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Simple Camera App",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(25.dp))

        // Display captured photograph
        if (capturedImage != null) {

            Image(
                bitmap = capturedImage!!.asImageBitmap(),
                contentDescription = "Captured Photograph",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )

        } else {

            Text(text = "No photograph captured yet")

        }

        Spacer(modifier = Modifier.height(25.dp))

        // Open Camera Button
        Button(
            onClick = {
                cameraLauncher.launch(null)
            }
        ) {
            Text("Open Camera")
        }

        Spacer(modifier = Modifier.height(15.dp))

        // Share Button
        Button(
            onClick = {

                capturedImage?.let { bitmap ->

                    val file = File(
                        context.cacheDir,
                        "captured_photo.jpg"
                    )

                    FileOutputStream(file).use { output ->
                        bitmap.compress(
                            Bitmap.CompressFormat.JPEG,
                            100,
                            output
                        )
                    }

                    val imageUri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    )

                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "image/jpeg"
                        putExtra(Intent.EXTRA_STREAM, imageUri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }

                    context.startActivity(
                        Intent.createChooser(
                            shareIntent,
                            "Share Photo"
                        )
                    )
                }
            },
            enabled = capturedImage != null
        ) {
            Text("Share Photo")
        }
    }
}
