package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalExtendedColors
import com.example.ui.theme.PastelLavender
import com.example.ui.theme.PastelLavenderLight
import com.example.ui.theme.PastelMint
import com.example.ui.theme.PastelMintLight
import com.example.ui.theme.PastelPeach
import com.example.ui.theme.PastelPeachLight
import com.example.ui.theme.PastelPink
import com.example.ui.theme.PastelPinkLight
import com.example.ui.theme.PastelSkyBlue
import com.example.ui.theme.PastelSkyBlueLight
import com.example.ui.theme.PastelYellow
import com.example.ui.theme.PastelYellowLight

fun getCategoryPastelColors(category: String, colorHex: String? = null): Pair<Color, Color> {
    if (!colorHex.isNullOrBlank()) {
        try {
            val baseColor = Color(android.graphics.Color.parseColor(colorHex))
            return Pair(baseColor.copy(alpha = 0.16f), baseColor)
        } catch (_: Exception) {}
    }
    return when (category.lowercase()) {
        "work" -> Pair(PastelSkyBlueLight, PastelSkyBlue)
        "study" -> Pair(PastelPinkLight, PastelPink)
        "fitness" -> Pair(PastelMintLight, PastelMint)
        "home" -> Pair(PastelPeachLight, PastelPeach)
        "groceries" -> Pair(PastelLavenderLight, PastelLavender)
        "personal" -> Pair(PastelLavenderLight, PastelLavender)
        else -> Pair(PastelYellowLight, PastelYellow)
    }
}

fun getIconForName(name: String): ImageVector {
    return when (name.lowercase()) {
        "target", "work", "briefcase" -> Icons.Default.Adjust
        "book", "school", "study" -> Icons.Default.MenuBook
        "sun", "morning" -> Icons.Default.WbSunny
        "dumbbell", "fitness", "sport" -> Icons.Default.FitnessCenter
        "cart", "shopping", "groceries", "shopping_cart" -> Icons.Default.ShoppingCart
        "flight", "travel", "plane" -> Icons.Default.FlightTakeoff
        "sparkles", "cleaning", "clean" -> Icons.Default.AutoAwesome
        "star", "goals" -> Icons.Default.Star
        "camera", "content", "video" -> Icons.Default.Videocam
        "moon", "night", "reset" -> Icons.Default.NightlightRound
        "home" -> Icons.Default.Home
        "person", "personal" -> Icons.Default.Person
        "clock", "time" -> Icons.Default.AccessTime
        "priority" -> Icons.Default.Flag
        "repeat" -> Icons.Default.Repeat
        "notifications" -> Icons.Default.Notifications
        else -> Icons.Default.CheckCircleOutline
    }
}

@Composable
fun PastelPill(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = PastelLavenderLight,
    textColor: Color = PastelLavender,
    icon: ImageVector? = null,
    horizontalPadding: Dp = 10.dp,
    verticalPadding: Dp = 4.dp
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = text,
                color = textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun PriorityBadge(priority: String, modifier: Modifier = Modifier) {
    val (bgColor, textColor, label) = when (priority.uppercase()) {
        "HIGH" -> Triple(Color(0xFFFFECEC), Color(0xFFFF4D4D), "High")
        "LOW" -> Triple(Color(0xFFEBF3FE), Color(0xFF3B82F6), "Low")
        else -> Triple(Color(0xFFFEF9E8), Color(0xFFF59E0B), "Medium")
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
