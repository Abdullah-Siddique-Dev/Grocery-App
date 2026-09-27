package com.example.groceryapp.data.network

import android.content.Context
import android.content.SharedPreferences

class InMemoryTokenProvider : TokenProvider {
    private var token: String? = null
    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        prefs = context.getSharedPreferences("smart_grocery_prefs", Context.MODE_PRIVATE)
        token = prefs?.getString("jwt_token", null)
    }

    override fun getToken(): String? = token

    override fun saveToken(token: String?) {
        this.token = token
        prefs?.edit()?.putString("jwt_token", token)?.apply()
    }

    fun saveRole(role: String?) {
        prefs?.edit()?.putString("user_role", role)?.apply()
    }

    fun getRole(): String? = prefs?.getString("user_role", null)

    override fun clearToken() {
        this.token = null
        prefs?.edit()?.remove("jwt_token")?.remove("user_role")?.apply()
    }
    
    companion object {
        @Volatile
        private var instance: InMemoryTokenProvider? = null
        fun getInstance(): InMemoryTokenProvider {
            return instance ?: synchronized(this) {
                instance ?: InMemoryTokenProvider().also { instance = it }
            }
        }
    }
}
