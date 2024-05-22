package io.github.dmitrytsyvtsyn.interfunny.interview_list.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.dmitrytsyvtsyn.interfunny.R

@Composable
fun InterviewTabs(
    prevDate: String,
    prevClick: () -> Unit,
    nowDate: String,
    nowClick: () -> Unit,
    nextDate: String,
    nextClick: () -> Unit
) {
    Row {
        val color1 = MaterialTheme.colorScheme.primary
        val color2 = MaterialTheme.colorScheme.onBackground
        Box(
            modifier = Modifier
                .weight(1f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(),
                    onClick = prevClick
                )
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = Modifier.size(16.dp),
                    painter = painterResource(id = R.drawable.ic_back_day),
                    contentDescription = ""
                )
                Text(
                    text = prevDate,
                    color = color2,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(),
                    onClick = nowClick
                )
                .drawBehind {
                    drawLine(
                        color = color1,
                        start = Offset(x = 0f, y = size.height),
                        end = Offset(x = size.width, y = size.height),
                        strokeWidth = 8f
                    )
                }
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = nowDate,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = color1
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(),
                    onClick = nextClick
                )
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = nextDate,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = color2
                )
                Icon(
                    modifier = Modifier.size(16.dp),
                    painter = painterResource(id = R.drawable.ic_forward_day),
                    contentDescription = ""
                )
            }
        }
    }
}