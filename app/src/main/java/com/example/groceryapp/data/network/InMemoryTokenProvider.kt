package com.example.groceryapp.data.network

class InMemoryTokenProvider : TokenProvider {
    private var token: String? = null

    override fun getToken(): String? = token

    override fun saveToken(token: String?) {
        this.token = token
    }

    override fun clearToken() {
        this.token = null
    }
    
    companion object {
        private var instance: InMemoryTokenProvider? = null
        fun getInstance(): InMemoryTokenProvider {
            if (instance == null) {
                instance = InMemoryTokenProvider()
            }
            return instance!!
        }
    }
}
