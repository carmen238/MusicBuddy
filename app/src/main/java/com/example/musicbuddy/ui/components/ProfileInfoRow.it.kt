package com.example.musicbuddy.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicbuddy.ui.auth.AuthViewModel
import com.example.musicbuddy.ui.theme.AppColors
import com.example.musicbuddy.ui.components.getErrorMessage
import com.example.musicbuddy.ui.components.Validators

@Composable
fun ProfileInfoRow(
    label: String,
    value: String,
    field: String,
    userId: String?,
    authViewModel: AuthViewModel,
    validator: ((String) -> Boolean)? = null
) {
    var isEditing by remember { mutableStateOf(false) }
    var textValue by remember(value) { mutableStateOf(value) }

    //val isValid = textValue.isNotBlank()
    // Validazione
    val isValid = if (validator != null) validator(textValue) else value.isNotBlank()
    //val hasError = value.isNotBlank() && !isValid

    Column(
        horizontalAlignment = Alignment.End // Centra il testo sotto l'oggetto
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = label,
                fontSize = 14.sp,
                color = Color.Gray
            )

            Row(verticalAlignment = Alignment.CenterVertically) {

                if (isEditing) {

                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "save",
                        tint = AppColors.PrimaryGreen,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable(enabled = isValid) {
                                isEditing = false
                                if (isValid) {
                                    authViewModel.updateUserField(
                                        userId,
                                        field,
                                        textValue
                                    )
                                }
                            }
                    )

                    Spacer(Modifier.width(8.dp))

                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "cancel",
                        tint = AppColors.ErrorRed,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable {
                                isEditing = false
                                textValue = value
                            }
                    )

                } else {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "edit",
                        tint = AppColors.PrimaryGreen,
                        modifier = Modifier
                            .size(18.dp)
                            .clickable { isEditing = true }
                    )
                }

                Spacer(Modifier.width(12.dp))

                if (isEditing) {
                    BasicTextField(
                        value = textValue,
                        onValueChange = { textValue = it },
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )
                    )
                } else {
                    Text(
                        text = value,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                }
            }
        }
        // Messaggio di errore
        if (!isValid) {
            Text(
                text = getErrorMessage(label),
                color = Color(0xFFE53935),
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp, start = 8.dp)
            )
        }
    }
}