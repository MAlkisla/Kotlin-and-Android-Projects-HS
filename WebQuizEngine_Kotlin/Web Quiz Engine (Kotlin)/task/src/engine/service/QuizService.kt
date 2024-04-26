package engine.service

import engine.repository.CompletedQuizRepository
import engine.dto.AnswerDTO
import engine.dto.NewQuizDTO
import engine.dto.ResponseDTO
import engine.entity.CompletedQuiz
import engine.entity.Quiz
import engine.entity.User
import engine.repository.QuizRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class QuizService(
    private val quizRepository: QuizRepository,
    private val completedQuizRepository: CompletedQuizRepository
) {

    private val rightAnswerResponse: ResponseDTO =
        ResponseDTO(true, "Congratulations, you're right!")

    private val wrongAnswerResponse: ResponseDTO =
        ResponseDTO(false, "Wrong answer! Please, try again.")

    fun solveQuiz(id: Int, answer: AnswerDTO, user: User): ResponseDTO? {
        val quiz: Quiz? = getQuiz(id)

        return if (quiz == null) null else {
            if (answer.answer == quiz.answer) {
                completedQuizRepository.save(
                    CompletedQuiz(
                        user = user,
                        id = id
                    )
                )

                rightAnswerResponse
            } else wrongAnswerResponse
        }
    }

    fun getQuiz(id: Int): Quiz? = quizRepository.findQuizById(id)

    fun createQuiz(newQuiz: NewQuizDTO, user: User): Quiz {
        val quiz = Quiz(
            creator = user,
            title = newQuiz.title,
            text = newQuiz.text,
            options = newQuiz.options,
            answer = newQuiz.answer ?: listOf()
        )

        quizRepository.save(quiz)

        return quiz
    }

    fun getAllQuizzes(page: Int): Page<Quiz> =
        quizRepository.findAll(PageRequest.of(page, 10))

    @Transactional
    fun deleteQuiz(id: Int, user: User): HttpStatus {
        val quiz: Quiz = getQuiz(id) ?: return HttpStatus.NOT_FOUND

        return if (quiz.creator?.id == user.id) {
            quizRepository.delete(quiz)
            completedQuizRepository.deleteAllById(id)

            HttpStatus.NO_CONTENT
        } else {
            HttpStatus.FORBIDDEN
        }
    }

    fun getAllCompletionsByUser(page: Int, user: User): Page<CompletedQuiz>? =
        completedQuizRepository.findAllByUserOrderByCompletedAtDesc(user, PageRequest.of(page, 10))

}