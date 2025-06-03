package com.pw.contactappassignment.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentProviderOperation
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.view.View
import android.widget.Toast
import com.pw.contactappassignment.R
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.pw.contactappassignment.data.adapter.OnItemClicked
import com.pw.contactappassignment.data.adapter.SyncContactAdapter
import com.pw.contactappassignment.data.model.User
import com.pw.contactappassignment.data.remote.repo.ContactRepository
import com.pw.contactappassignment.databinding.ActivityFetchContactsBinding
import com.pw.contactappassignment.utils.RetrofitClient
import com.pw.contactappassignment.viewModel.ContactViewModel
import com.pw.contactappassignment.viewModel.ContactViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FetchContactsActivity : AppCompatActivity() , OnItemClicked{
    private lateinit var adapter: SyncContactAdapter
    private lateinit var viewModel: ContactViewModel
    private lateinit var bin: ActivityFetchContactsBinding
    private val EDIT_ITEM_REQUEST_CODE = 1001;
    private var listOfContacts: ArrayList<User> = ArrayList()


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bin = ActivityFetchContactsBinding.inflate(layoutInflater)
        setContentView(bin.root)

        val apiService = RetrofitClient.contactApi
        val repository = ContactRepository(apiService)
        val viewModelFactory = ContactViewModelFactory(repository)
        bin.pgLoading.visibility = View.VISIBLE
        viewModel = ViewModelProvider(this, viewModelFactory)[ContactViewModel::class.java]

        bin.lastSynced.text = "Last Synced: \n ${getSavedDateFromSharedPreferences(this)}"

        setupRecyclerView()
        setupButtonListener()
        observeContacts()
    }

    private fun setupRecyclerView() {
        bin.recyclerViewContacts.layoutManager = LinearLayoutManager(this)
    }

    private fun observeContacts() {
        viewModel.contacts.observe(this) { contacts ->
            listOfContacts.clear()
            listOfContacts.addAll(contacts)
            bin.pgLoading.visibility = View.GONE
            if (contacts.isNullOrEmpty()) {
                bin.tvNoDataFound.visibility = View.VISIBLE
                bin.recyclerViewContacts.visibility = View.GONE
                return@observe
            } else {
                bin.tvNoDataFound.visibility = View.GONE
                bin.recyclerViewContacts.visibility = View.VISIBLE
            }
            adapter = SyncContactAdapter(this, listOfContacts)
            bin.recyclerViewContacts.adapter = adapter
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupButtonListener() {
        bin.btnSync.setOnClickListener {
            saveCurrentDateToSharedPreferences(this)
            bin.lastSynced.text = "Last Synced: \n ${getSavedDateFromSharedPreferences(this)}"

            val contactsToSync =
                adapter.getContacts() // Make sure this method exists in SyncContactAdapter

            for (contact in contactsToSync) {
                addContactToDevice(contact.fullName ?: "", contact.phone ?: "")
            }
            showSuccessDialog()
        }
    }


    private fun addContactToDevice(name: String, phone: String) {
        val ops = ArrayList<ContentProviderOperation>()

        ops.add(
            ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build()
        )

        ops.add(
            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(
                    ContactsContract.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
                )
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                .build()
        )

        ops.add(
            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(
                    ContactsContract.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                )
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                .withValue(
                    ContactsContract.CommonDataKinds.Phone.TYPE,
                    ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
                )
                .build()
        )

        try {
            contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showSuccessDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.sync_success_dialog)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        // Optional: auto-dismiss after 2 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            dialog.dismiss()
            Toast.makeText(this, "Contact sync successful", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        }, 2000)
    }

    override fun onBackPressed() {
        setResult(RESULT_CANCELED)
        super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == EDIT_ITEM_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val position = data.getIntExtra("position", -1)

            if (position != -1) {
                val fullName = data.getStringExtra("fullName") ?: ""
                val phone = data.getStringExtra("phone") ?: ""

                listOfContacts[position].apply {
                    this.fullName = fullName
                    this.phone = phone
                }

                adapter.notifyItemChanged(position)
            }
        }
    }

    override fun onItemClick(
        position: Int,
        model: User
    ) {
        val intent = Intent(this, EditContactActivity::class.java).apply {
            putExtra("position", position)
            putExtra("fullName", model.fullName)
            putExtra("course", model.course)
            putExtra("phone", model.phone)
            putExtra("email", model.email)
            putExtra("id", model.id) // If you have an ID or other unique info
        }
        startActivityForResult(intent, EDIT_ITEM_REQUEST_CODE)
    }
    fun saveCurrentDateToSharedPreferences(context: Context) {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Format current date as a string (e.g., "2025-06-03")
        val currentDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())

        editor.putString("saved_date", currentDate)
        editor.apply()
    }
    fun getSavedDateFromSharedPreferences(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("saved_date", null)
    }


}
