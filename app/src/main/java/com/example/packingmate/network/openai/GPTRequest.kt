package com.example.packingmate.network.openai

data class GPTResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)