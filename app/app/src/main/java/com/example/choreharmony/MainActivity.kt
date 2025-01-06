package com.example.choreharmony

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.choreharmony.repository.UserRepository
import com.example.choreharmony.ui.theme.ChoreHarmonyTheme
import com.example.choreharmony.views.App
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChoreHarmonyTheme {
                App()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        userRepository.resetUser()
    }
}
