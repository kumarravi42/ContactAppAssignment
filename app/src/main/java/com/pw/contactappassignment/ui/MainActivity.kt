package com.pw.contactappassignment.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import com.pw.contactappassignment.R
import android.os.Handler
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.pw.contactappassignment.data.adapter.ContactAdapter
import com.pw.contactappassignment.data.model.Contact
import com.pw.contactappassignment.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var bin: ActivityMainBinding
    private lateinit var adapter: ContactAdapter
    private var allContacts = listOf<Contact>()
    private var doubleBackToExitPressedOnce: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bin = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bin.root)

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            loadContacts()
        } else {
            requestForPermission()
        }

        bin.addButton.setOnClickListener {
            val intent = Intent(this, AddContactActivity::class.java)
            startActivityForResult(intent, 102)
        }

        bin.syncButton.setOnClickListener {
            val intent = Intent(this@MainActivity, FetchContactsActivity::class.java)
            startActivityForResult(intent, 102)
        }

        bin.etSearchContact.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                p0: CharSequence?, p1: Int, p2: Int, p3: Int
            ) {
            }

            override fun onTextChanged(
                p0: CharSequence?, p1: Int, p2: Int, p3: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty()) {
                    val query = s.toString().trim().lowercase()
                    filterContacts(query)
                }
            }

        })
    }

    private fun requestForPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS),
            101
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadContacts()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 102 && resultCode == RESULT_OK) {
            loadContacts()
        }
    }

    private fun loadContacts() {
        allContacts = getContacts(this)
        adapter = ContactAdapter(this, allContacts)
        bin.recyclerView.layoutManager = LinearLayoutManager(this)
        bin.recyclerView.adapter = adapter

        bin.emptyText.visibility = if (allContacts.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun filterContacts(query: String) {
        val filteredList = if (query.isEmpty()) {
            allContacts
        } else {
            allContacts.filter { it.name?.lowercase()?.contains(query) == true }
        }

        adapter.updateData(filteredList)
        bin.emptyText.visibility = if (filteredList.isEmpty()) View.VISIBLE else View.GONE
    }

    @SuppressLint("Range")
    fun getContacts(context: Context): List<Contact> {
        val contactList = mutableListOf<Contact>()
        val contentResolver = context.contentResolver
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI

        val cursor = contentResolver.query(
            uri, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        val id = cursor?.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID) ?: -1
        val nameIndex =
            cursor?.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME) ?: -1
        val phoneIndex = cursor?.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER) ?: -1
        val photoIndex =
            cursor?.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI) ?: -1

        cursor?.use {
            while (it.moveToNext()) {
                val name = it.getString(nameIndex) ?: ""
                val phone = it.getString(phoneIndex) ?: ""
                val photoUri = it.getString(photoIndex) ?: ""
                val contact = Contact(id, name, phone, photoUri)
                contactList.add(contact)
            }
        }

        return contactList.distinctBy { it.name?.trim()?.lowercase() }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            finish()
            return
        }
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, R.string.double_back_press_msg, Toast.LENGTH_SHORT).show()
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }
}
