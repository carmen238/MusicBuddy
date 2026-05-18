package com.example.musicbuddy.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicbuddy.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuField(
    options: List<String>,
    label: String,
    placeholder: String,
    onOptionSelected: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Stato per gestire l'apertura/chiusura del menu
    var expanded by remember { mutableStateOf(false) }
    // Stato per memorizzare l'opzione selezionata
    var selectedOption by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = AppColors.DarkText,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(6.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                // Aggancia il campo di testo al box del menu
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                readOnly = true, // Impedisce la digitazione da tastiera
                value = selectedOption,
                onValueChange = {},
                label = {
                    Text(
                        text = placeholder,
                        modifier = Modifier.align(Alignment.Start).alpha(0.7f),
                        fontSize = 14.sp
                    )
                },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(text = option) },
                        onClick = {
                            selectedOption = option
                            expanded = false
                            // Restituisce il valore al form principale
                            onOptionSelected(option)
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
    }
}