package engine.entity

import jakarta.persistence.*
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(name = "users")
@SequenceGenerator(name = "users_generator", allocationSize = 1)
data class User(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_generator")
    val id: Int? = null,

    @Column(unique = true)
    val email: String,

    @Column
    private val password: String,

    @Transient
    @ElementCollection(fetch = FetchType.EAGER)
    private val authorities: MutableCollection<SimpleGrantedAuthority>? = mutableListOf(),

    @Column
    @OneToMany(fetch = FetchType.EAGER)
    val quizzes: MutableList<Quiz> = mutableListOf()

) : UserDetails {

    override fun getAuthorities(): MutableCollection<SimpleGrantedAuthority>? = authorities

    override fun getPassword(): String = password

    override fun getUsername(): String = email

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true

}