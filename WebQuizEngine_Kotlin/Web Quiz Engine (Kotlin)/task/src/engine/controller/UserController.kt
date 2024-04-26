package engine.controller

import engine.config.BCryptConfig
import engine.dto.UserDTO
import engine.entity.User
import engine.repository.UserRepository
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/register")
class UserController(
    private val userRepository: UserRepository,
    private val bCryptConfig: BCryptConfig
) {

    @PostMapping
    fun createUser(@Valid @RequestBody newUser: UserDTO): ResponseEntity<String> {
        val user: User? = userRepository.findByEmail(newUser.email)

        return if (user == null) {
            val newUserEntity = User(
                email = newUser.email,
                password = bCryptConfig.bCryptPasswordEncoder().encode(newUser.password)
            )

            userRepository.save(newUserEntity)
            ResponseEntity.ok("User registered successfully.")
        } else {
            ResponseEntity.badRequest().body("Already exsisted")
        }
    }

}