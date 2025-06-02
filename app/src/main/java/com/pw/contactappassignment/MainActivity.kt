package com.pw.contactappassignment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pw.contactappassignment.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var bin: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bin = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bin.root)

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED
            ){
            setList()
        }else{
            requestForPermission()
        }

        bin.addButton.setOnClickListener {
            val intent = Intent(this, AddContactActivity::class.java)
            startActivity(intent)
        }
        bin.syncButton.setOnClickListener{
            startActivity(Intent(this@MainActivity, FetchContactsActivity::class.java))
        }
    }

    private fun requestForPermission(){
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS), 101)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setList()
        }
    }


    private fun setList() {
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val contacts = getContacts(this)
        val adapter = ContactAdapter(this, contacts)
        recyclerView.adapter = adapter

        if (contacts.isEmpty()) {
            bin.emptyText.visibility = View.VISIBLE
        }else{
            bin.emptyText.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
    }

    fun getContacts(context: Context): List<Contact> {
        val contactList = mutableListOf<Contact>()

        val contentResolver = context.contentResolver
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI

        val cursor = contentResolver.query(
            uri,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        val id = cursor?.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID) ?: -1
        val nameIndex = cursor?.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME) ?: -1
        val phoneIndex = cursor?.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER) ?: -1
        val photoIndex = cursor?.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI) ?: -1

        cursor?.use {
            while (it.moveToNext()) {
                val name = it.getString(nameIndex) ?: ""
                val phone = it.getString(phoneIndex) ?: ""
                val photoUri = it.getString(photoIndex) ?:"" // Can be null
                val contact = Contact(id, name, phone, photoUri)
                    contactList.add(contact)
            }
        }

        return contactList.distinctBy { it.name?.trim()?.lowercase() }
    }




}