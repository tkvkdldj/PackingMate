package com.example.packingmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.packingmate.data.db.DBHelper
import com.example.packingmate.data.repository.OpenAIRepository

class TripViewModelFactory(
    private val openAiRepository: OpenAIRepository,
    private val dbHelper: DBHelper
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TripViewModel::class.java)) {
            return TripViewModel(openAiRepository, dbHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
