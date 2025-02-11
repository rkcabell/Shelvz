package com.example.shelvz.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.Log
import com.example.shelvz.data.model.UserFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

suspend fun extractPdfThumbnail(context: Context, file: UserFile): Bitmap? {
    return withContext(Dispatchers.IO) {
        val localFile = File(context.filesDir, "user_files/${file.name}")
        if (!localFile.exists()) return@withContext null

        var parcelFileDescriptor: ParcelFileDescriptor? = null
        var pdfRenderer: PdfRenderer? = null
        var bitmap: Bitmap? = null

        try {
            // Open the PDF file
            parcelFileDescriptor = ParcelFileDescriptor.open(localFile, ParcelFileDescriptor.MODE_READ_ONLY)
            pdfRenderer = PdfRenderer(parcelFileDescriptor)

            // Render the first page
            val page = pdfRenderer.openPage(0)

            // Calculate the dimensions for the thumbnail (256x256)
            val scale = 256f / page.width.coerceAtLeast(page.height)
            val width = (page.width * scale).toInt()
            val height = (page.height * scale).toInt()

            // Create a scaled bitmap
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(bitmap)
            canvas.drawColor(android.graphics.Color.WHITE) // Set a white background
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

            // Close the page
            page.close()
        } catch (e: Exception) {
            Log.e("PdfThumbnail", "Error extracting PDF thumbnail: ${e.message}")
        } finally {
            // Clean up resources
            pdfRenderer?.close()
            parcelFileDescriptor?.close()
        }

        return@withContext bitmap
    }
}