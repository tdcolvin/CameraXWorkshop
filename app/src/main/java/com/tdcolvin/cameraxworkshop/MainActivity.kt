package com.tdcolvin.cameraxworkshop

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.core.CameraSelector
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import com.tdcolvin.cameraxworkshop.ui.permission.WithPermission
import com.tdcolvin.cameraxworkshop.ui.theme.CameraXWorkshopTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CameraXWorkshopTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WithPermission(
                        modifier = Modifier.padding(innerPadding),
                        permission = Manifest.permission.CAMERA
                    ) {
                        CameraAppScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun CameraAppScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        //add the preview view here

        ControlButtons(
            modifier = Modifier.align(Alignment.BottomCenter),
            onZoom = { zoomLevel -> },
            onSelectCamera = { cameraFacing ->  },
            onCapture = { }
        )
    }
}

@Composable
fun ControlButtons(
    modifier: Modifier = Modifier,
    onZoom: (zoomLevel: Float) -> Unit,
    onSelectCamera: (cameraFacing: Int) -> Unit,
    onCapture: () -> Unit
) {
    Column(modifier = modifier) {
        Row {
            Button(onClick = { onSelectCamera(CameraSelector.LENS_FACING_FRONT) }) {
                Text("Front camera")
            }
            Button(onClick = { onSelectCamera(CameraSelector.LENS_FACING_BACK) }) {
                Text("Back camera")
            }
        }

        Row {
            Button(onClick = { onZoom(0.0f) }) {
                Text("Zoom 0.0")
            }
            Button(onClick = { onZoom(0.5f) }) {
                Text("Zoom 0.5")
            }
            Button(onClick = { onZoom(1.0f) }) {
                Text("Zoom 1.0")
            }
        }

        Button(onClick = onCapture) {
            Text("Take Photo")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CameraScreenPreview() {
    CameraXWorkshopTheme {
        CameraAppScreen()
    }
}

fun Uri.shareAsImage(context: Context) {
    val contentUri = FileProvider.getUriForFile(context, "com.tdcolvin.cameraxworkshop.fileprovider", toFile())
    val shareIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_STREAM, contentUri)
        type = "image/jpeg"
    }
    context.startActivity(Intent.createChooser(shareIntent, null))
}