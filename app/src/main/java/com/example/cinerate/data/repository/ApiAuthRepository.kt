package com.example.cinerate.data.repository

import com.example.cinerate.data.local.TokenDataStore
import com.example.cinerate.data.model.User
import com.example.cinerate.data.remote.api.AuthApiService
import com.example.cinerate.data.remote.dto.LoginRequest
import com.example.cinerate.data.remote.dto.RegisterRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiAuthRepository @Inject constructor(
    private val api: AuthApiService,
    private val tokenDataStore: TokenDataStore
) : AuthRepository {

    private var currentUser: User? = null

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val response = api.login(LoginRequest(email, password))
            tokenDataStore.saveToken(response.accessToken)
            val userResponse = api.getCurrentUser("Bearer ${response.accessToken}")
            val user = User(
                id = userResponse.id,
                fullName = userResponse.email.substringBefore("@"), // API user doesn't have name
                email = userResponse.email,
                passwordHash = ""
            )
            currentUser = user
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signup(fullName: String, email: String, password: String): Result<User> {
        return try {
            val response = api.register(RegisterRequest(email, password))
            val user = User(
                id = response.id,
                fullName = fullName,
                email = response.email,
                passwordHash = ""
            )
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        tokenDataStore.clearToken()
        currentUser = null
    }

    override suspend fun getCurrentUser(): User? {
        if (currentUser != null) return currentUser
        
        val token = tokenDataStore.getToken() ?: return null
        return try {
            val userResponse = api.getCurrentUser("Bearer $token")
            val user = User(
                id = userResponse.id,
                fullName = userResponse.email.substringBefore("@"),
                email = userResponse.email,
                passwordHash = ""
            )
            currentUser = user
            user
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun isLoggedIn(): Boolean {
        val token = tokenDataStore.getToken()
        return !token.isNullOrEmpty()
    }
}
