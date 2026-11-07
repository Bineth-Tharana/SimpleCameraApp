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
import androidx.compose.ui.layout.ContentScale
import androidx.core.content.FileProvider
import android.content.Context
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

    var capturedImage by remember {
        mutableStateOf<Bitmap?>(null)
    }

    val context = androidx.compose.ui.platform.LocalContext.current

    // Open camera and capture image
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

        // Display captured image
        if (capturedImage != null) {

            Image(
                bitmap = capturedImage!!.asImageBitmap(),
                contentDescription = "Captured Photograph",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp),
                contentScale = ContentScale.Crop
            )

        } else {

            Text(
                text = "No photograph captured yet"
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Capture button
        Button(
            onClick = {
                cameraLauncher.launch(null)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Open Camera")
        }

        Spacer(modifier = Modifier.height(15.dp))

        // Share button
        Button(
            onClick = {
                capturedImage?.let {
                    shareImage(context, it)
                }
            },
            enabled = capturedImage != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Share Photograph")
        }
    }
}

// Share captured photograph
fun shareImage(context: Context, bitmap: Bitmap) {

    val imageFile = File(context.cacheDir, "captured_image.jpg")

    FileOutputStream(imageFile).use { output ->
        bitmap.compress(
            Bitmap.CompressFormat.JPEG,
            100,
            output
        )
    }

    val imageUri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        imageFile
    )

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/jpeg"
        putExtra(Intent.EXTRA_STREAM, imageUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(
        Intent.createChooser(
            shareIntent,
            "Share Photograph"
        )
    )
}
