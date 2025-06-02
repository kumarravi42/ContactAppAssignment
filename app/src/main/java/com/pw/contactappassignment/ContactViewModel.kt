package com.pw.contactappassignment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ContactViewModel(private val repository: ContactRepository) : ViewModel() {
    val contacts = MutableLiveData<List<Contact>>()

    fun loadFromApi() {
        viewModelScope.launch {
            contacts.value = repository.fetchContacts()
        }
    }
}
