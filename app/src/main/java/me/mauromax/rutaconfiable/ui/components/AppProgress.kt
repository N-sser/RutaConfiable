package me.mauromax.rutaconfiable.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.mauromax.rutaconfiable.ui.theme.*

@Composable
fun StepProgressBarCard(
    currentStep: Int = 2,
    totalSteps: Int = 2,
    stepTitle: String = "Verificación final",
    progress: Float = 1.0f,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFC4C6CE)),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PASO $currentStep DE $totalSteps",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Secondary,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = stepTitle,
                    fontSize = 11.sp,
                    color = Color(0xFF74777E)
                )
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = Secondary,
                trackColor = Color(0xFFEFF4FF)
            )
        }
    }
}

@Composable
fun SixDigitCodeInput(
    codeValue: String,
    onCodeChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = codeValue,
        onValueChange = {
            if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                onCodeChange(it)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier.fillMaxWidth(),
        decorationBox = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (i in 0 until 6) {
                    val char = codeValue.getOrNull(i)
                    val isFocused = codeValue.length == i

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFEFF4FF),
                        border = BorderStroke(
                            width = if (isFocused) 1.5.dp else 1.dp,
                            color = if (isFocused) PrimaryLight else Color(0xFFC4C6CE)
                        )
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = char?.toString() ?: "•",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (char != null) PrimaryLight else Color(0xFF74777E),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    )
}
