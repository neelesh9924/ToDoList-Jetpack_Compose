package com.neelesh.todolist.widgets

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun MyTextField(
    value: String,
    label: String,
    leadingIcon: @Composable () -> Unit = {},
    onValueChange: (String) -> Unit = {},
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        leadingIcon = leadingIcon,
        shape = RoundedCornerShape(22.dp),
        placeholder = { Text(label) },

        )
}