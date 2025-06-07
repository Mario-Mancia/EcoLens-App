package com.example.ecolens.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ecolens.data.local.dao.UserDao

class LoginViewModelFactory(
    private val userDao: UserDao,
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(userDao, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}