package engine.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UserDTO(

    @field:Email(regexp = ".+@.+\\..+")
    @field:NotBlank
    val email: String,

    @field:Size(min = 5)
    val password: String

)