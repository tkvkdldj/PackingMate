package com.example.packingmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.packingmate.db.AppDatabase
import com.example.packingmate.repository.OpenAIRepository

class TripViewModelFactory(
    private val db: AppDatabase,
    private val openAiRepository: OpenAIRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TripViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TripViewModel(db, openAiRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
