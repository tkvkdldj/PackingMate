package com.example.packingmate.openai

data class GPTResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)