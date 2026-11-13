package com.example.data

import com.example.model.IslamicQuizCategory
import com.example.model.IslamicQuizQuestion

object IslamicQuizCatalog {
    val questions: List<IslamicQuizQuestion> =
        IslamicQuizBankPart1.questions + IslamicQuizBankPart2.questions + IslamicQuizBankPart3.questions

    private val recentlyAskedIds = ArrayDeque<String>()

    fun getRandomQuestion(category: IslamicQuizCategory? = null): IslamicQuizQuestion {
        val pool = if (category != null) questions.filter { it.category == category } else questions
        val fresh = pool.filterNot { it.id in recentlyAskedIds }.ifEmpty { pool }
        val chosen = fresh.random()
        recentlyAskedIds.addLast(chosen.id)
        while (recentlyAskedIds.size > 20) recentlyAskedIds.removeFirst()
        return chosen
    }
}
