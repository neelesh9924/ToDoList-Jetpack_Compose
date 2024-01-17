package com.neelesh.todolist.user

data class SignInResult(
    val data: UserData?,
    val error: String?,
)


data class UserData(
    val userId: String,
    val userName: String?,
    val pfpUrl: String?,
)