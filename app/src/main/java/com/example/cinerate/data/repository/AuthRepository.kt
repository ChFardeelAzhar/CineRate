package com.example.cinerate.data.repository

import com.example.cinerate.data.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun signup(fullName: String, email: String, password: String): Result<User>
    suspend fun logout()
    suspend fun getCurrentUser(): User?
    suspend fun isLoggedIn(): Boolean
}
