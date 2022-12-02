package com.example.managingphotoapp

import androidx.lifecycle.ViewModel

import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomsheet.BottomSheetDialog

import android.content.Context

import android.graphics.Bitmap
import android.util.Log
import java.io.ByteArrayOutputStream
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.content.ContextCompat

import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import com.example.managingphotoapp.databinding.LayoutBottomBinding
import java.io.File
import java.lang.Exception


class MainActivityViewModel : ViewModel() {

    var dialog: BottomSheetDialog? = null
    var imageList = ArrayList<Uri>()
    var resultLauncher: ActivityResultLauncher<Intent>? = null
    val imagelistMutable: MutableLiveData<List<Uri>> = MutableLiveData<List<Uri>>()
    var bottomBinding: LayoutBottomBinding? = null

    fun callCamera(view: View) {

        bottomBinding?.tvPicFromGallery?.setOnClickListener {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            resultLauncher?.launch(galleryIntent)
        }

        bottomBinding?.tvPicFromCamera?.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            resultLauncher?.launch(cameraIntent)
        }

        bottomBinding?.tvShare?.setOnClickListener {
            val installed = appInstalledOrNot(view)
            if (installed) {
                try {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data =
                        Uri.parse("http://api.whatsapp.com/send?phone=" + "+91" + "9600795173" + "&text=" + "hi");
                    view.context.startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } else {
                Toast.makeText(
                    view.context,
                    "Whats app not installed on your device",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        dialog?.setCancelable(true)

        if (imageList.size == 6) {
            Toast.makeText(view.context, "Maximum images already captured", Toast.LENGTH_LONG)
                .show()
        } else dialog?.show()
    }

    fun getLiveData(): LiveData<List<Uri>> {
        return imagelistMutable
    }


    private fun appInstalledOrNot(view: View): Boolean {
        val packageManager: PackageManager = view.context.packageManager
        val app_installed: Boolean = try {
            packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
        return app_installed
    }

    fun onActivityResult(result: ActivityResult, mainActivity: MainActivity) {
        var uri: Uri? = null
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            if (result.data?.data == null) {
                uri = getImageUri(mainActivity, result.data?.extras?.get("data") as Bitmap)
            } else {
                uri = result.data?.data
            }
            imageList.add(uri!!)
            imagelistMutable.value = imageList
            dialog?.dismiss()

        }

    }

    private fun getImageUri(context: Context, inImage: Bitmap): Uri {
        val file = File(context.cacheDir, "CUSTOM NAME")
        file.delete()
        file.createNewFile()
        val fileOutputStream = file.outputStream()
        val byteArrayOutputStream = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val bytearray = byteArrayOutputStream.toByteArray()
        fileOutputStream.write(bytearray)
        fileOutputStream.flush()
        fileOutputStream.close()
        byteArrayOutputStream.close()
        return file.toUri()
    }


}