package io.github.dmitrytsyvtsyn.interfunny.interview_list.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.dmitrytsyvtsyn.interfunny.R
import kotlinx.collections.immutable.PersistentList

data class InterviewTabModel(
    val title: String,
    val onClick: () -> Unit
)

@Composable
fun InterviewTabs(tabs: PersistentList<InterviewTabModel>) {
    Row {
        tabs.forEachIndexed { index, tabModel ->
            when (index) {
                0 -> {
                    StartTab(tabModel = tabModel)
                }
                tabs.lastIndex -> {
                    EndTab(tabModel = tabModel)
                }
                else -> {
                    CenterTab(tabModel = tabModel)
                }
            }
        }
    }
}

@Composable
private fun RowScope.StartTab(tabModel: InterviewTabModel) {
    Box(
        modifier = Modifier
            .weight(1f)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = tabModel.onClick
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
                text = tabModel.title,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun RowScope.CenterTab(tabModel: InterviewTabModel) {
    val primaryColor = MaterialTheme.colorScheme.primary
    Box(
        modifier = Modifier
            .weight(1f)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = tabModel.onClick
            )
            .drawBehind {
                drawLine(
                    color = primaryColor,
                    start = Offset(x = 0f, y = size.height),
                    end = Offset(x = size.width, y = size.height),
                    strokeWidth = 8f
                )
            }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = tabModel.title,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = primaryColor
        )
    }

}

@Composable
private fun RowScope.EndTab(tabModel: InterviewTabModel) {
    Box(
        modifier = Modifier
            .weight(1f)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = tabModel.onClick
            )
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = tabModel.title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Icon(
                modifier = Modifier.size(16.dp),
                painter = painterResource(id = R.drawable.ic_forward_day),
                contentDescription = ""
            )
        }
    }
}