package com.neelesh.todolist.ui.pages

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import coil.compose.AsyncImage
import com.neelesh.todolist.R
import com.neelesh.todolist.models.TodoItem
import com.neelesh.todolist.ui.theme.MyTextStyles
import com.neelesh.todolist.ui.theme.PurpleGrey40
import com.neelesh.todolist.viewModel.HomeViewModel
import kotlinx.coroutines.launch

@Composable
fun HomePage(
    onPfpCardClicked: () -> Unit,
) {
    //Todo: add a calendar logic to look for tasks that are completed on that day, and when today
    // is selected it will show today completed, and all previous non completed tasks
    // and when a date is selected it will show all completed tasks on that day and all non completed tasks of that day
    /*Variables*/
    val scrollState = rememberScrollState()

    val homeViewModel = hiltViewModel<HomeViewModel>()

    /*Observables*/
    val listOfTasks by homeViewModel.listOfTasks.observeAsState(listOf())

    val inCompleteCount by homeViewModel.inCompleteCount.observeAsState(0)

    val userData by homeViewModel.userData.observeAsState(null)

    /*Dialogs*/
    var showAddNoteDialog: Boolean by remember { mutableStateOf(false) }
    var inputField1 by remember { mutableStateOf("") }
    var inputField2 by remember { mutableStateOf("") }

    if (showAddNoteDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddNoteDialog = false
            },
            title = {
                Text(text = "Add New Task")
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = inputField1,
                        onValueChange = { inputField1 = it },
                        label = { Text("Task Title") },
                        shape = RoundedCornerShape(22.dp),
                    )
                    OutlinedTextField(
                        value = inputField2,
                        onValueChange = { inputField2 = it },
                        label = { Text("Task Description") },
                        shape = RoundedCornerShape(22.dp),
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        homeViewModel.addTask(inputField1, inputField2)
                        showAddNoteDialog = false
                        inputField1 = ""
                        inputField2 = ""
                    }
                ) {
                    Text("Okay")
                }
            }
        )
    }

    var showSignOutDialog: Boolean by remember { mutableStateOf(false) }

    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = {
                showSignOutDialog = false
            },
            title = {
                Text(text = "Sign Out")
            },
            text = {
                Text(text = "Are you sure you want to sign out?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onPfpCardClicked()
                        showSignOutDialog = false
                    }
                ) {
                    Text("Okay")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showSignOutDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    /*UI*/
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Card(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
                .clickable {
                    if (userData == null) {
                        onPfpCardClicked()
                    } else {
                        showSignOutDialog = true
                    }
                },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
            ),
        ) {
            Column(
                modifier = Modifier
                    .padding(2.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (userData?.pfpUrl != null) {
                        AsyncImage(
                            model = userData!!.pfpUrl,
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .padding(vertical = 10.dp, horizontal = 8.dp)
                                .size(50.dp)
                                .clip(CircleShape)
                                .border(1.dp, PurpleGrey40, CircleShape)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .padding(vertical = 10.dp, horizontal = 8.dp)
                                .size(50.dp)
                                .clip(CircleShape)
                                .border(1.dp, PurpleGrey40, CircleShape)
                        )
                    }
                    Column {
                        Text(
                            userData?.userName ?: "Sign In",
                            style = MyTextStyles.h5,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                        Text(
                            userData?.userId ?: "Sign In to sync your tasks",
                            style = MyTextStyles.caption,
                        )
                    }

                }
            }
        }
        Text(
            modifier = Modifier.padding(top = 12.dp, start = 14.dp),
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                    )
                ) {
                    append("Hello,\t")
                }
                withStyle(
                    style = SpanStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = PurpleGrey40
                    )
                ) {
                    append(
                        if (userData?.userName != null)
                            "${userData?.userName?.split(" ")?.get(0) ?: (userData?.userName)}!"
                        else
                            "User!"
                    )
                }
                withStyle(
                    style = SpanStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PurpleGrey40
                    )
                ) {
                    append("\t👋")
                }
            },
            style = MyTextStyles.h4
        )
        Text(
            modifier = Modifier.padding(top = 4.dp, start = 14.dp),
            text = "Hope you are doing well.",
            style = MyTextStyles.h5,
        )
        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    "Your Tasks",
                    style = MyTextStyles.h4
                )
                Text(
                    "You have $inCompleteCount tasks to complete today",
                    style = MyTextStyles.h5,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .padding(top = 12.dp)
                .verticalScroll(scrollState)
                .weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 50.dp)
                        .padding(2.dp)
                        .background(
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            showAddNoteDialog = true
                        },
                    contentAlignment = Alignment.Center

                ) {
                    Text(
                        "+ Add New",
                        style = MyTextStyles.h4,
                    )
                }
                Column(
                    modifier = Modifier
                        .padding(2.dp)
                ) {
                    repeat(listOfTasks.size) {
                        TodoCard(
                            todoInfo = TodoItem(
                                id = it.toString(),
                                task = listOfTasks[it].task,
                                description = listOfTasks[it].description,
                                isCompleted = listOfTasks[it].isCompleted
                            ),
                            callback = { item ->
                                homeViewModel.toggleTaskCompletion(item)
                            },
                            deleteRequest = { item ->
                                homeViewModel.deleteTask(item)
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun TodoCard(
    todoInfo: TodoItem,
    callback: (item: TodoItem) -> Unit,
    deleteRequest: (item: TodoItem) -> Unit,
) {
    var checkboxValue by remember {
        mutableStateOf(todoInfo.isCompleted)
    }

    var showDeleteConfirmationDialog by remember {
        mutableStateOf(false)
    }

    val composableScope = rememberCoroutineScope()


    if (showDeleteConfirmationDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteConfirmationDialog = false
            },
            title = {
                Text(text = "Delete Task")
            },
            text = {
                Text(text = "Are you sure you want to delete this task?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        deleteRequest(todoInfo)
                        showDeleteConfirmationDialog = false
                    }
                ) {
                    Text("Okay")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDeleteConfirmationDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    val swipeState = rememberSwipeableState(SwipeState.Hidden)

    val anchors = mapOf(0f to SwipeState.Hidden, -200f to SwipeState.Shown)

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .swipeable(
                state = swipeState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.5f) },
                orientation = Orientation.Horizontal
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        )

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = checkboxValue,
                onCheckedChange = {
                    checkboxValue = it
                    Log.d("TAG", "TodoCard: ${todoInfo.id} $checkboxValue")
                    callback(todoInfo)
                },
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Column {
                Text(
                    "Task: ${todoInfo.task}",
                    style = MyTextStyles.h4
                )
                Text(
                    todoInfo.description,
                    style = MyTextStyles.subtitle2,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            if (swipeState.currentValue == SwipeState.Shown) {
                Button(
                    onClick = {
                        showDeleteConfirmationDialog = true
                        composableScope.launch {
                            swipeState.animateTo(SwipeState.Hidden)
                        }
                    },
                    modifier = Modifier
                        .background(Color.Transparent)
                        .clip(CircleShape),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier
                            .size(26.dp)
                            .background(Color.Transparent)
                            .clip(CircleShape),
                        tint = Color.Red.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }


}

enum class SwipeState {
    Shown, Hidden
}
