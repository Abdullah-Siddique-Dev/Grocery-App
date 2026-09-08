package com.example.groceryapp.services

import at.favre.lib.crypto.bcrypt.BCrypt
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.groceryapp.models.*
import com.example.groceryapp.repositories.UserRepository
import io.ktor.server.application.*
import java.time.Instant
import java.util.*

class AuthService(
    private val userRepository: UserRepository = UserRepository(),
    private val jwtSecret: String,
    private val jwtIssuer: String,
    private val jwtAudience: String
) {

    suspend fun register(request: RegisterRequest): Result<AuthResponse> {
        val cleanEmail = request.email.trim().lowercase()
        val cleanName = request.name.trim()
        val cleanPhone = request.phoneNumber.trim()
        val cleanAddress = request.address.trim()

        if (userRepository.findByEmail(cleanEmail) != null) {
            return Result.failure(Exception("Email already exists"))
        }

        val passwordHash = BCrypt.withDefaults().hashToString(12, request.password.toCharArray())
        
        val user = User(
            name = cleanName,
            email = cleanEmail,
            passwordHash = passwordHash,
            phoneNumber = cleanPhone,
            address = Address(
                fullName = cleanName,
                phoneNumber = cleanPhone,
                addressLine = cleanAddress,
                city = "",
                postalCode = ""
            ),
            createdAt = Instant.now().toString()
        )

        val createdUser = userRepository.create(user)
        val token = generateToken(createdUser)

        return Result.success(AuthResponse(createdUser.toDto(), token))
    }

    suspend fun login(request: LoginRequest): Result<AuthResponse> {
        val cleanEmail = request.email.trim().lowercase()
        val user = userRepository.findByEmail(cleanEmail) ?: return Result.failure(Exception("Invalid email or password"))
        
        val verification = BCrypt.verifyer().verify(request.password.toCharArray(), user.passwordHash)
        if (!verification.verified) {
            return Result.failure(Exception("Invalid email or password"))
        }

        val token = generateToken(user)
        return Result.success(AuthResponse(user.toDto(), token))
    }

    private fun generateToken(user: User): String {
        return JWT.create()
            .withAudience(jwtAudience)
            .withIssuer(jwtIssuer)
            .withSubject(user.id)
            .withClaim("role", user.role.name)
            .withExpiresAt(Date(System.currentTimeMillis() + 3600000 * 24)) // 24 hours
            .sign(Algorithm.HMAC256(jwtSecret))
    }
}
