package me.mauromax.rutaconfiable.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.mauromax.rutaconfiable.ui.theme.Outline
import me.mauromax.rutaconfiable.ui.theme.OutlineVariant
import me.mauromax.rutaconfiable.ui.theme.PrimaryLight
import me.mauromax.rutaconfiable.ui.theme.Secondary
import me.mauromax.rutaconfiable.ui.theme.SurfaceSoft

@Composable
fun StepProgressBarCard(
    currentStep: Int = 2,
    totalSteps: Int = 2,
    stepTitle: String = "Verificación final",
    progress: Float = 1.0f,
    modifier: Modifier = Modifier
) {
    AppCard(modifier.fillMaxWidth(), spacing = 8.dp) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Text(
                "PASO $currentStep DE $totalSteps", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                color = Secondary, letterSpacing = 0.5.sp
            )
            Hint(stepTitle)
        }
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(6.dp),
            color = Secondary,
            trackColor = SurfaceSoft
        )
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
        onValueChange = { if (it.length <= 6 && it.all { c -> c.isDigit() }) onCodeChange(it) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier.fillMaxWidth(),
        decorationBox = {
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                for (i in 0 until 6) {
                    val char = codeValue.getOrNull(i)
                    val isFocused = codeValue.length == i

                    Surface(
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = SurfaceSoft,
                        border = BorderStroke(
                            if (isFocused) 1.5.dp else 1.dp,
                            if (isFocused) PrimaryLight else OutlineVariant
                        )
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = char?.toString() ?: "•",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (char != null) PrimaryLight else Outline,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    )
}
