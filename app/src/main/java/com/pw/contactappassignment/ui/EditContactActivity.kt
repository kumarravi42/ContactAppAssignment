package com.pw.contactappassignment.ui

import android.app.Dialog
import android.content.ContentProviderOperation
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.text.Editable
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pw.contactappassignment.R
import com.pw.contactappassignment.databinding.ActivityEditContactBinding
import java.io.File
import java.io.FileOutputStream

class EditContactActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditContactBinding
    private var id: Int = 0
    private var selectedImagePath: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        btnListener()
    }

    private fun initView() {

        val fullName = intent.getStringExtra("fullName") ?: ""
        id = intent.getIntExtra("position", -1) ?: -1
        val phone = intent.getStringExtra("phone")
        val nameParts = fullName.trim().split(" ")
        val firstName = nameParts.getOrNull(0) ?: ""
        val surName = nameParts.getOrNull(1) ?: ""

        binding.editNameInput.setText(firstName)
        binding.surName.setText(surName)
        binding.editPhoneInput.setText(phone)
        binding.companyName.setText("pw")


    }

    private fun btnListener() {
        binding.editImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 100)
        }
        binding.saveBtn.setOnClickListener {
            val ops = ArrayList<ContentProviderOperation>().apply {
                add(
                    ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build()
                )

                val name = binding.editNameInput.text.toString()
                    .trim() + " " + binding.surName.text.toString().trim()

                add(
                    ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0).withValue(
                            ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
                        ).withValue(
                            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name
                        ).build()
                )


                add(
                    ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0).withValue(
                            ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE
                        ).withValue(
                            ContactsContract.CommonDataKinds.Organization.COMPANY,
                            binding.companyName.text.toString()
                        ).build()
                )
                add(
                    ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0).withValue(
                            ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                        ).withValue(
                            ContactsContract.CommonDataKinds.Phone.NUMBER,
                            binding.editPhoneInput.text.toString()
                        ).withValue(
                            ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
                        ).build()
                )
            }

            contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
            val intent = Intent()
            intent.putExtra("position", id)
            intent.putExtra(
                "fullName",
                binding.editNameInput.text.toString() + " " + binding.surName.text.toString()
            )
            intent.putExtra("phone", binding.editPhoneInput.text.toString())
            intent.putExtra("company", binding.companyName.text.toString())
            setResult(RESULT_OK, intent)
            finish()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            val uri = data?.data
            uri?.let {
                // Show the selected image
                binding.editImageView.setImageURI(uri)

                // Save the image to internal storage and get the file path
                val filePath =
                    saveImageToFile(this, uri, "profile_image_${System.currentTimeMillis()}.png")

//                 Store the file path in the database
                if (filePath != null) {
                    selectedImagePath = filePath
                } else {
                    Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveImageToFile(context: Context, imageUri: Uri, fileName: String): String? {
        return try {
            // First, get the dimensions of the image without loading it fully into memory
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds =
                    true  // This will load only the image's dimensions, not the whole bitmap
            }

            // Decode the image file to get its dimensions
            context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream, null, options)
            }

            // Calculate the sample size (scale factor) to downsize the image
            val requiredWidth = 300  // Desired width
            val requiredHeight = 300  // Desired height
            val scaleFactor = calculateInSampleSize(options, requiredWidth, requiredHeight)

            // Now decode the image with the correct sample size
            val finalOptions = BitmapFactory.Options().apply {
                inSampleSize = scaleFactor  // Set the scale factor to reduce the size
            }

            // Decode the image to a scaled bitmap
            val bitmap = context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream, null, finalOptions)
            }

            // Check if the bitmap is null
            if (bitmap == null) {
                return null
            }

            // Save the scaled bitmap to internal storage
            val file = File(context.filesDir, fileName)
            val outputStream = FileOutputStream(file)

            // Compress the bitmap to reduce its quality (e.g., 80% quality)
            bitmap.compress(
                Bitmap.CompressFormat.JPEG, 80, outputStream
            ) // Adjust the quality (0 to 100)
            outputStream.flush()
            outputStream.close()

            // Return the file path
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()  // Log the error for debugging
            null  // Return null if saving failed
        }
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int
    ): Int {
        // Raw width and height of image
        val height = options.outHeight
        val width = options.outWidth

        // Initializing scale factor to 1
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both width and height larger than required
            val halfHeight = height / 2
            val halfWidth = width / 2

            // Keep halving the width/height until it's smaller than the required dimensions
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

}