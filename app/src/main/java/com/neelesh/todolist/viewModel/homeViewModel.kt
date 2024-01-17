package com.neelesh.todolist.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neelesh.todolist.db.ToDoDao
import com.neelesh.todolist.models.TodoItem
import com.neelesh.todolist.user.SignInResult
import com.neelesh.todolist.user.SignInState
import com.neelesh.todolist.user.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val todoDao: ToDoDao,
) : ViewModel() {

    /*TodoList Flow*/
    private val _listOfTasks = MutableLiveData<List<TodoItem>>(listOf())
    val listOfTasks: LiveData<List<TodoItem>> = _listOfTasks

    private val _inCompleteCount = MutableLiveData(0)
    val inCompleteCount: LiveData<Int> = _inCompleteCount


    init {
        /*On init get the list of tasks from the DB*/
        getTasks()
    }

    private fun getTasks() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                todoDao.getAll().collect {
                    _listOfTasks.postValue(it)
                    _inCompleteCount.postValue(it.count { todo -> !todo.isCompleted })
                }
            }
        }
    }

    fun addTask(task: String, description: String) {
        val newTask = TodoItem(
            id = (_listOfTasks.value?.size ?: 0).toString(),
            task = task,
            description = description,
            isCompleted = false
        )
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                todoDao.insert(newTask)
            }
        }
    }

    fun toggleTaskCompletion(item: TodoItem) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                todoDao.update(item.copy(isCompleted = !item.isCompleted))
            }
        }
    }

    fun deleteTask(item: TodoItem) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                todoDao.delete(item)
            }
        }
    }

    /*SignIn State Flow*/
    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    private val _userData = MutableLiveData<UserData>()
    val userData: LiveData<UserData> = _userData

    fun onSignInResult(result: SignInResult) {
        _state.update {
            it.copy(
                isSignedIn = result.data != null,
                signInError = result.error
            )
        }
    }

    fun resetState() {
        _state.update { SignInState() }
    }

    fun setUserData(userData: UserData?) {
        _userData.value = userData
    }
}