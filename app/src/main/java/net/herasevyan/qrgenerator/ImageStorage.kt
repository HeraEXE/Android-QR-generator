package net.herasevyan.qrgenerator

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.contentValuesOf

class ImageStorage(private val context: Context) {

    fun save(name: String, bitmap: Bitmap) {
        val images = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        val content = contentValuesOf(
            MediaStore.Images.Media.DISPLAY_NAME to "$name.png",
            MediaStore.Images.Media.MIME_TYPE to "images/*",

            )
        val uri = context.contentResolver.insert(images, content)
        uri?.let { context.contentResolver.openOutputStream(it) }.use { output ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
        }
    }
}