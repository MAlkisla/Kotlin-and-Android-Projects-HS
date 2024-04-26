package engine.repository

import engine.entity.Quiz
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface QuizRepository : JpaRepository<Quiz, Int> {
    fun findQuizById(id: Int): Quiz?
}