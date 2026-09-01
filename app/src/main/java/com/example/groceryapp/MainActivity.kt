package com.example.groceryapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.groceryapp.navigation.AppNavigation
import com.example.groceryapp.navigation.Screen
import com.example.groceryapp.ui.theme.SmartGroceryAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartGroceryAppTheme {
                val navController = rememberNavController()
                
                LaunchedEffect(intent) {
                    processIntent(intent, navController)
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        AppNavigation(navController = navController)
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    private fun processIntent(intent: Intent, navController: NavHostController) {
        val orderId = intent.getStringExtra("orderId")
        if (!orderId.isNullOrBlank()) {
            navController.navigate(Screen.OrderDetails.createRoute(orderId)) {
                launchSingleTop = true
            }
            intent.removeExtra("orderId")
        }
    }
}
