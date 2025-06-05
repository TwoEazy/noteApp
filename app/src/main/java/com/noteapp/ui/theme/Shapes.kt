package com.noteapp.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

// Additional custom shapes for special components
object CustomShapes {
    val buttonShape = RoundedCornerShape(12.dp)
    val cardShape = RoundedCornerShape(16.dp)
    val textFieldShape = RoundedCornerShape(12.dp)
    val dialogShape = RoundedCornerShape(20.dp)
    val bottomSheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
}