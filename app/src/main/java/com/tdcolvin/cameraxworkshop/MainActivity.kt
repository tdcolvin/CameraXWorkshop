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
import androidx.camera.core.ImageCapture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    var zoomLevel by remember { mutableFloatStateOf(0.5f) }
    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    val imageCaptureUseCase = remember { ImageCapture.Builder().build() }

    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreview(
            modifier = Modifier.fillMaxSize(),
            zoomLevel = zoomLevel,
            lensFacing = lensFacing,
            imageCaptureUseCase = imageCaptureUseCase
        )

        ControlButtons(
            modifier = Modifier.align(Alignment.BottomCenter),
            onZoom = { newZoomLevel -> zoomLevel = newZoomLevel },
            onSelectCamera = { newLensFacing -> lensFacing = newLensFacing },
            onCapture = {
                /*
                    TODO
                    Take a photo using the imageCaptureUseCase above.
                */
            }
        )
    }
}

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    lensFacing: Int,
    zoomLevel: Float,
    imageCaptureUseCase: ImageCapture
) {
    /*
       TODO:

       1. Create a PreviewView
       -----------------------
       Because PreviewView isn't a Composable, we need to wrap it in an AndroidView.
       This is a Composable which displays an older-style Android Views widget.

       Hint:
         AndroidView(
            modifier = ...
            factory = { context ->
               <instantiate your PreviewView here>
            }
         )


       Now run the code. You should find it shows a blank screen.
       Why's that? Because you haven't linked the PreviewView up to a PreviewUseCase...


       2. Create a PreviewUseCase
       --------------------------
       This tells the PreviewView you created earlier what to display.

       Hint: remember {  } it!
       The class you need is androidx.camera.core.Preview.Builder. You don't need any options,
       just build() it.


       3. Draw onto the PreviewView
       ----------------------------
       Then, tell your PreviewUseCase to draw onto the surface of the PreviewView.
       Hint: set the use case's surface provider to be the PreviewView's surface provider:

       previewUseCase.surfaceProvider = ...

       You only need to do this once, when you are creating the PreviewView.


       Now run the code again. Another blank screen?
       This time, it's because you need to bind it all together using a CameraProvider
       instance.


       4. Get a CameraProvider instance.
       ---------------------------------
       The specific kind of CameraProvider we're going to get is a ProcessCameraProvider.
       We ask for it using:
       cameraProvider = ProcessCameraProvider.awaitInstance(localContext)

       How can you run all the code in this step just once when the CameraPreview
       composable appears in the composition?
       Hint: LaunchedEffect(...) {  }


       5. Bind it all together
       -----------------------

       Build a CameraSelector:
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(...)
                .build()

       and then bind it all together using the CameraProvider, passing in the CameraSelector
       you've just created:

            cameraProvider.bindToLifecycle(
                localContext as LifecycleOwner,
                cameraSelector,
                previewUseCase, imageCaptureUseCase
            )


      Run it! You should now have a working preview!!!

      See if you can make the zoom and camera selector buttons work.
     */
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