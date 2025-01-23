package com.example.shelvz.ui.pdf

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.shelvz.data.model.UserFile
import com.rajat.pdfviewer.compose.PdfRendererViewCompose

@Composable
fun PdfViewerScreen(userFile: UserFile) {
    val context = LocalContext.current

    PdfRendererViewCompose(
        modifier = Modifier.fillMaxSize(),
        uri = Uri.parse(userFile.uri)
    )
}