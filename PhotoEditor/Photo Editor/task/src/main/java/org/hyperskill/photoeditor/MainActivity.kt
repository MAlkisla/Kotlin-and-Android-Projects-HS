package org.hyperskill.photoeditor

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.get
import kotlinx.coroutines.*
import org.hyperskill.photoeditor.databinding.ActivityMainBinding
import kotlin.math.pow

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var originalImage: Bitmap
    private var lastJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpListeners()
        originalImage = createBitmap()
        binding.ivPhoto.setImageBitmap(originalImage)
    }

    private fun setUpListeners() {
        binding.apply {
            btnGallery.setOnClickListener {
                val myIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(myIntent)
            }
            slBrightness.addOnChangeListener { _, value, _ ->
                applyImageFilters(
                    selectedBright = value.toInt(),
                    selectedContrast = slContrast.value,
                    selectedSaturation = slSaturation.value,
                    selectedGamma = slGamma.value
                )
            }
            slContrast.addOnChangeListener { _, value, _ ->
                applyImageFilters(
                    selectedBright = slBrightness.value.toInt(),
                    selectedContrast = value,
                    selectedSaturation = slSaturation.value,
                    selectedGamma = slGamma.value
                )
            }

            slSaturation.addOnChangeListener { _, value, _ ->
                applyImageFilters(
                    selectedBright = slBrightness.value.toInt(),
                    selectedContrast = slContrast.value,
                    selectedSaturation = value,
                    selectedGamma = slGamma.value
                )
            }
            slGamma.addOnChangeListener { _, value, _ ->
                applyImageFilters(
                    selectedBright = slBrightness.value.toInt(),
                    selectedContrast = slContrast.value,
                    selectedSaturation = slSaturation.value,
                    selectedGamma = value
                )
            }
            btnSave.setOnClickListener {
                saveImage()
            }
        }
    }

    private fun applyImageFilters(
        selectedBright: Int,
        selectedContrast: Float,
        selectedSaturation: Float,
        selectedGamma: Float
    ) {
        lastJob?.cancel()
        lastJob = GlobalScope.launch(Dispatchers.Default) {
            var resultBitmap: Bitmap?
            val brightnessPairResult = async {
                applyBrightnessFilter(originalImage, selectedBright)
            }.await()
            resultBitmap = applyContrastFilter(brightnessPairResult.first, selectedContrast, brightnessPairResult.second)
            resultBitmap = applySaturationFilter(resultBitmap, selectedSaturation)
            resultBitmap = applyGammaFilter(resultBitmap, selectedGamma)
            runOnUiThread {
                binding.ivPhoto.setImageBitmap(resultBitmap)
            }
        }
    }

    private fun applyGammaFilter(inputBitmap: Bitmap, selectedGamma: Float): Bitmap {
        val resultImage = inputBitmap.copy(inputBitmap.config, true)
        for (x in 0 until inputBitmap.width) {
            for (y in 0 until inputBitmap.height) {
                val oldColor = inputBitmap[x, y]
                val oldRed = Color.red(oldColor)
                val oldGreen = Color.green(oldColor)
                val oldBlue = Color.blue(oldColor)
                val newRed = (255 * (oldRed.toDouble() / 255).pow(selectedGamma.toDouble())).toInt()
                val newGreen = (255 * (oldGreen.toDouble() / 255).pow(selectedGamma.toDouble())).toInt()
                val newBlue = (255 * (oldBlue.toDouble() / 255).pow(selectedGamma.toDouble())).toInt()
                resultImage.setPixel(x, y, Color.rgb(newRed, newGreen, newBlue))
            }
        }
        return resultImage
    }

    private fun applySaturationFilter(inputBitmap: Bitmap, selectedSaturation: Float): Bitmap {
        val alpha: Double = (255.0 + selectedSaturation) / (255.0 - selectedSaturation)
        val resultImage = inputBitmap.copy(inputBitmap.config, true)
        for (x in 0 until inputBitmap.width) {
            for (y in 0 until inputBitmap.height) {
                val oldColor = inputBitmap[x, y]
                val oldRed = Color.red(oldColor)
                val oldGreen = Color.green(oldColor)
                val oldBlue = Color.blue(oldColor)
                val rgbAvg = (oldRed + oldGreen + oldBlue) / 3
                val newRed = (alpha * (Color.red(oldColor) - rgbAvg) + rgbAvg).toInt().coerceIn(0, 255)
                val newGreen = (alpha * (Color.green(oldColor) - rgbAvg) + rgbAvg).toInt().coerceIn(0, 255)
                val newBlue = (alpha * (Color.blue(oldColor) - rgbAvg) + rgbAvg).toInt().coerceIn(0, 255)
                resultImage.setPixel(x, y, Color.rgb(newRed, newGreen, newBlue))
            }
        }
        return resultImage
    }

    private fun applyContrastFilter(
        inputBitmap: Bitmap,
        selectedContrast: Float,
        totalBrightSum: Long
    ): Bitmap {
        val avgBright = (totalBrightSum / (inputBitmap.width * inputBitmap.height)).toInt()
        val alpha: Double = (255.0 + selectedContrast) / (255.0 - selectedContrast)
        val resultImage = inputBitmap.copy(inputBitmap.config, true)
        for (x in 0 until inputBitmap.width) {
            for (y in 0 until inputBitmap.height) {
                val oldColor = inputBitmap[x, y]
                val newRed = (alpha * (Color.red(oldColor) - avgBright) + avgBright).toInt().coerceIn(0, 255)
                val newGreen = (alpha * (Color.green(oldColor) - avgBright) + avgBright).toInt().coerceIn(0, 255)
                val newBlue = (alpha * (Color.blue(oldColor) - avgBright) + avgBright).toInt().coerceIn(0, 255)
                resultImage.setPixel(x, y, Color.rgb(newRed, newGreen, newBlue))
            }
        }
        return resultImage
    }

    private fun applyBrightnessFilter(inputBitmap: Bitmap, selectedBright: Int): Pair<Bitmap, Long> {
        var totalBrightSum: Long = 0
        val resultBitmap = inputBitmap.copy(inputBitmap.config, true)
        for (x in 0 until inputBitmap.width) {
            for (y in 0 until inputBitmap.height) {
                val pixelColor = inputBitmap[x, y]
                val newRed = (Color.red(pixelColor) + selectedBright).coerceIn(0, 255)
                val newGreen = (Color.green(pixelColor) + selectedBright).coerceIn(0, 255)
                val newBlue = (Color.blue(pixelColor) + selectedBright).coerceIn(0, 255)
                totalBrightSum += (newRed + newGreen + newBlue) / 3
                resultBitmap.setPixel(x, y, Color.rgb(newRed, newGreen, newBlue))
            }
        }
        return Pair(resultBitmap, totalBrightSum)
    }

    private fun saveImage() {
        if (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            val bitmap: Bitmap = binding.ivPhoto.drawable.toBitmap()
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.ImageColumns.WIDTH, bitmap.width)
                put(MediaStore.Images.ImageColumns.HEIGHT, bitmap.height)
            }

            val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: return

            contentResolver.openOutputStream(uri)?.use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }
        } else {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Toast.makeText(this, "Storage permission is granted", Toast.LENGTH_SHORT).show()
                }

                ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) -> {
                    AlertDialog.Builder(this)
                        .setTitle("Permission required")
                        .setMessage("This app needs permission to access this feature.")
                        .setPositiveButton("Grant") { _, _ ->
                            ActivityCompat.requestPermissions(
                                this,
                                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                MEDIA_REQUEST_CODE
                            )
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                }

                else -> {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ),
                        MEDIA_REQUEST_CODE
                    )
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        findViewById<Button>(R.id.btnSave).callOnClick()
    }

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val photoUri = result.data?.data ?: return@registerForActivityResult
                setSelectedImage(photoUri)
            }
        }

    private fun setSelectedImage(photoUri: Uri) {
        binding.ivPhoto.setImageURI(photoUri)
        originalImage = binding.ivPhoto.drawable.toBitmap()
    }

    fun createBitmap(): Bitmap {
        val width = 200
        val height = 100
        val pixels = IntArray(width * height)

        for (y in 0 until height) {
            for (x in 0 until width) {
                val index = y * width + x
                val R = x % 100 + 40
                val G = y % 100 + 80
                val B = (x + y) % 100 + 120
                pixels[index] = Color.rgb(R, G, B)
            }
        }
        val bitmapOut = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        bitmapOut.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmapOut
    }

    private fun hasPermission(manifestPermission: String): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkSelfPermission(manifestPermission) == PackageManager.PERMISSION_GRANTED
        } else {
            PermissionChecker.checkSelfPermission(
                this,
                manifestPermission
            ) == PermissionChecker.PERMISSION_GRANTED
        }
    }

    companion object {
        private const val MEDIA_REQUEST_CODE = 0
    }
}
