package com.example.groceryapp.data.network

interface TokenProvider {
    fun getToken(): String?
    fun saveToken(token: String?)
    fun clearToken()
}
