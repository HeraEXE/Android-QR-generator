package net.herasevyan.qrgenerator

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import io.github.g0dkar.qrcode.QRCode
import java.io.FileNotFoundException

class QrGenerator(private val context: Context) {

    fun generate(str: String): Bitmap {
        context.openFileOutput("qr.png", Context.MODE_PRIVATE).use {
            QRCode(str).render().writeImage(it)
        }
        return readLast()
    }

    @Throws(FileNotFoundException::class)
    fun readLast(): Bitmap =
        BitmapFactory.decodeStream(context.openFileInput("qr.png"))
}