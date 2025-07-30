package com.example.packingmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.example.packingmate.data.repository.TripRepository
import com.example.packingmate.data.repository.OpenAIRepository


class TripViewModelFactory(
    private val repository: TripRepository,
    private val openAiRepository: OpenAIRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TripViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TripViewModel(repository, openAiRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
