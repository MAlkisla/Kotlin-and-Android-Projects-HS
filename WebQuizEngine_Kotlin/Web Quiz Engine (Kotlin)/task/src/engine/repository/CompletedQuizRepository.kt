package engine.repository

import engine.entity.CompletedQuiz
import engine.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CompletedQuizRepository : JpaRepository<CompletedQuiz, Long> {
    fun findAllByUserOrderByCompletedAtDesc(user: User, pageable: Pageable): Page<CompletedQuiz>?
    fun deleteAllById(id: Int)
}