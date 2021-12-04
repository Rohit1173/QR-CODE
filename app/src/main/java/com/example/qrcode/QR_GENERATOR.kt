package com.example.qrcode

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.example.qrcode.databinding.FragmentQrGeneratorBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.launch
import java.io.IOException


class QR_GENERATOR : Fragment() {
    private var _binding: FragmentQrGeneratorBinding?=null
    private val binding get() = _binding!!
    lateinit var resolve: ContentResolver
    lateinit var bitmap: Bitmap
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentQrGeneratorBinding.inflate(inflater, container, false)
        resolve = requireActivity().getContentResolver()
        binding.qrTxt.doOnTextChanged { text, start, before, count ->
            val data = text.toString().trim()
            if (data.isEmpty()) {
                Toast.makeText(activity?.getApplicationContext(), "THIS FIELD COULDN'T BE EMPTY", Toast.LENGTH_LONG).show()
            } else {
                val writer = QRCodeWriter()
                try {
                    val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 512, 512)
                    val width = bitMatrix.width
                    val height = bitMatrix.height
                    bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
                    for (x in 0 until width) {
                        for (y in 0 until height) {
                            bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                        }
                    }
                    binding.qrImg.setImageBitmap(bitmap)
                } catch (e: Exception) {

                }
            }
        }
        binding.save.setOnClickListener {
            lifecycleScope.launch {
                if (saveImagetoStorage(bitmap)) {
                    Toast.makeText(activity?.getApplicationContext(),"Photo Saved Succesfully", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }


        return binding.root
    }

    private fun saveImagetoStorage(bmp: Bitmap): Boolean {
        val image_location: Uri =
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (bmp != null) {
                put(MediaStore.Images.Media.WIDTH, bmp.width)
                put(MediaStore.Images.Media.HEIGHT, bmp.height)
            }
        }
        return try {
            resolve.insert(image_location, contentValues).also {
                if (it != null) {
                    resolve.openOutputStream(it).use { outputStream ->
                        if (bmp != null) {
                            if (!bmp.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)) {
                                throw IOException("Failed to save Bitmap")
                            }
                        }
                    }
                }
            } ?: throw IOException("Failed to create Media Store entry")
            true

        } catch (e: IOException) {
            e.printStackTrace()
            false
        }

    }
}