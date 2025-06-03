package com.example.myzoo.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.myzoo.ui.theme.TropicOnBackground
import com.example.myzoo.ui.theme.TropicTurquoise
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.myzoo.ui.theme.TropicLime
import androidx.compose.ui.window.Dialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.ZoneId
import java.util.Date

@Composable
fun FilterRow(label: String, content: @Composable () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, modifier = Modifier.weight(1f), color = TropicOnBackground, style = MaterialTheme.typography.bodyLarge)
        Box(Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
            content()
        }
    }
}

@Composable
fun <T> DropdownSelector(
    label: String,
    options: List<Pair<T, String>>,
    selected: T?,
    onSelected: (T?) -> Unit,
    width: Dp = 180.dp
) {
    var expanded by remember { mutableStateOf(false) }
    var buttonOffset by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }
    var buttonHeight by remember { mutableStateOf(0) }
    Box(Modifier.width(width)) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier
                .width(width)
                .onGloballyPositioned { coords ->
                    buttonOffset = coords.positionInParent()
                    buttonHeight = coords.size.height
                },
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent, contentColor = Color.Gray),
            border = BorderStroke(1.dp, TropicTurquoise),
        ) {
            Text(
                selected?.let { options.find { it.first == selected }?.second } ?: label,
                color = if (selected == null) Color.Gray else TropicTurquoise
            )
        }
        if (expanded) {
            Popup(
                alignment = Alignment.TopStart,
                offset = IntOffset(0, buttonHeight),
                properties = PopupProperties(focusable = true, dismissOnClickOutside = true),
                onDismissRequest = { expanded = false }
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.Transparent,
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .width(width)
                        .border(1.dp, Color(0x22000000), shape = RoundedCornerShape(24.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFFFFFFFF), Color(0xFFFCFFFE))
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                ) {
                    Column {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelected(null)
                                    expanded = false
                                }
                                .padding(horizontal = 20.dp, vertical = 16.dp)
                        ) {
                            Text(
                                label,
                                color = if (selected == null) TropicTurquoise else TropicOnBackground,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        options.forEach { (value, text) ->
                            val isSelected = selected == value
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .background(if (isSelected) TropicLime.copy(alpha = 0.18f) else Color.Transparent)
                                    .clickable {
                                        onSelected(value)
                                        expanded = false
                                    }
                                    .padding(horizontal = 20.dp, vertical = 16.dp)
                            ) {
                                Text(
                                    text,
                                    color = if (isSelected) TropicTurquoise else TropicOnBackground,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    initialDate: String,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val open = remember { mutableStateOf(true) }
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val today = LocalDate.now()
    val initialMillis = try {
        if (initialDate.isNullOrBlank()) {
            today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        } else {
            LocalDate.parse(initialDate, formatter).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }
    } catch (_: Exception) { today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() }
    val state = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
    if (open.value) {
        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme.copy(
                surface = Color.White,
                primary = TropicTurquoise,
                onPrimary = Color.White
            ),
            shapes = MaterialTheme.shapes.copy(medium = RoundedCornerShape(24.dp))
        ) {
            DatePickerDialog(
                onDismissRequest = { open.value = false; onDismiss() },
                confirmButton = {
                    Button(onClick = {
                        open.value = false
                        val millis = state.selectedDateMillis
                        if (millis != null) {
                            val date = LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
                            onDateSelected(date.format(formatter))
                        } else {
                            onDateSelected("")
                        }
                    }, colors = ButtonDefaults.buttonColors(containerColor = TropicTurquoise)) { Text("Ок") }
                },
                dismissButton = {
                    TextButton(onClick = { open.value = false; onDismiss() }, colors = ButtonDefaults.textButtonColors(contentColor = TropicTurquoise)) { Text("Отмена") }
                },
                shape = RoundedCornerShape(24.dp)
            ) {
                DatePicker(state = state)
            }
        }
    }
} 
 
 
 