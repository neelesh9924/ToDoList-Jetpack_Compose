package com.neelesh.todolist

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import com.neelesh.todolist.ui.pages.HomePage
import com.neelesh.todolist.ui.theme.ToDoListTheme
import com.neelesh.todolist.user.GoogleAuthUIClient
import com.neelesh.todolist.viewModel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var googleAuthUIClient: GoogleAuthUIClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ToDoListTheme(
                dynamicColor = false,
                darkTheme = true
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val homeViewModel = hiltViewModel<HomeViewModel>()

                    val launcher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.StartIntentSenderForResult(),
                        onResult = {
                            if (it.resultCode == RESULT_OK) {
                                lifecycleScope.launch {
                                    val signInResult =
                                        googleAuthUIClient.signInWithIntent(
                                            intent = it.data ?: return@launch
                                        )

                                    homeViewModel.onSignInResult(signInResult)
                                }
                            }
                        }
                    )

                    LaunchedEffect(key1 = Unit, block = {
                        if (googleAuthUIClient.getSignedInUserData() != null) {
                            homeViewModel.setUserData(googleAuthUIClient.getSignedInUserData()!!)
                        }
                    })

                    val signInState by homeViewModel.state.collectAsState()

                    LaunchedEffect(key1 = signInState) {
                        if (signInState.isSignedIn) {
                            Toast.makeText(
                                applicationContext,
                                "Signed In",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            homeViewModel.setUserData(googleAuthUIClient.getSignedInUserData()!!)
                        } else if (signInState.signInError != null) {
                            Toast.makeText(
                                applicationContext,
                                signInState.signInError,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            homeViewModel.resetState()
                        }
                    }

                    HomePage(
                        onPfpCardClicked = {

                            lifecycleScope.launch {
                                if (googleAuthUIClient.getSignedInUserData() == null) {
                                    val signInIntentSender = googleAuthUIClient.signIn()

                                    launcher.launch(
                                        IntentSenderRequest.Builder(
                                            signInIntentSender ?: return@launch
                                        )
                                            .build()
                                    )
                                } else {
                                    googleAuthUIClient.signOut()
                                    homeViewModel.setUserData(null)
                                    homeViewModel.resetState()
                                }
                            }
                        }
                    )
                }
            }
        }
    }

}