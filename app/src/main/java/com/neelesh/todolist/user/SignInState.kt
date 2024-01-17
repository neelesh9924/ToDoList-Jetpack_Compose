package com.neelesh.todolist.user

data class SignInState(
    val isSignedIn: Boolean = false,
    val signInError: String? = null,
)
