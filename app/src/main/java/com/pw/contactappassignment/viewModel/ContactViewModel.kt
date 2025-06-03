package com.pw.contactappassignment.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pw.contactappassignment.data.remote.repo.ContactRepository
import com.pw.contactappassignment.data.model.User
import kotlinx.coroutines.launch

class ContactViewModel(private val repository: ContactRepository) : ViewModel() {
    private val _contacts = MutableLiveData<List<User>>()
    val contacts: LiveData<List<User>> = _contacts

    init {
        loadFromApi()
    }

    fun loadFromApi() {
        viewModelScope.launch {
            _contacts.postValue(repository.fetchContacts())
        }
    }
}
