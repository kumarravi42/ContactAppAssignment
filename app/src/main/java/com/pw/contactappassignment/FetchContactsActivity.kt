package com.pw.contactappassignment

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pw.contactappassignment.databinding.ActivityFetchContactsBinding
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FetchContactsActivity : AppCompatActivity() {
    private lateinit var adapter: ContactAdapter
    private lateinit var viewModel: ContactViewModel
    private lateinit var bin: ActivityFetchContactsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bin = ActivityFetchContactsBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_fetch_contacts)

        intiView()
        getAllContactsData()
        btnListener()

//        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewContacts)

//        adapter = ContactAdapter(contacts)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//        recyclerView.adapter = adapter

//        val retrofit = Retrofit.Builder()
//            .baseUrl("https://android-dev-assignment.onrender.com/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()

//        val api = retrofit.create(ContactApiService::class.java)
//        val repository = ContactRepository(api)
//        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
//            override fun <T : ViewModel> create(modelClass: Class<T>): T {
//                @Suppress("UNCHECKED_CAST")
//                return ContactViewModel(repository) as T
//            }
//        })[ContactViewModel::class.java]
//
//        viewModel.contacts.observe(this) { contacts ->
//            adapter.updateList(contacts ?: emptyList())
//        }
//
//        syncButton.setOnClickListener {
//            viewModel.loadFromApi()
//        }

//        addButton.setOnClickListener {
//            startActivity(Intent(this, AddContactActivity::class.java))
//        }

        // Properly add text change listener to filter contacts by name
//        searchEditText.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                val query = s?.toString() ?: ""
//                val filtered = viewModel.contacts.value?.filter {
//                    it.name.contains(query, ignoreCase = true)
//                } ?: emptyList()
//                adapter.updateList(filtered)
//            }
//            override fun afterTextChanged(s: Editable?) {}
//        })
    }

    private fun getAllContactsData() {

    }

    private fun btnListener() {
        bin.btnSync.setOnClickListener{
            Toast.makeText(this, "Contact sync successful", Toast.LENGTH_SHORT).show()
        }
    }

    private fun intiView() {

    }
}
