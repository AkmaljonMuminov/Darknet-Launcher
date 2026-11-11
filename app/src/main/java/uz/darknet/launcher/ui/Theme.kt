package uz.darknet.launcher.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val NeonGreen = Color(0xFF00FF9C)
val NeonCyan = Color(0xFF00E5FF)
val DeepBlack = Color(0xFF05070A)
val Panel = Color(0xFF0D1117)

private val DarknetColors = darkColorScheme(
    primary = NeonGreen,
    secondary = NeonCyan,
    background = DeepBlack,
    surface = Panel,
    onPrimary = Color.Black,
    onBackground = Color(0xFFE6EDF3),
    onSurface = Color(0xFFE6EDF3),
)

@Composable
fun DarknetTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = DarknetColors, content = content)
}
