package com.example.packingmate.viewmodel

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
