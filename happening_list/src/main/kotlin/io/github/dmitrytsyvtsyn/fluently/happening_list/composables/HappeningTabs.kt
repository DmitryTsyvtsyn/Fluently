package io.github.dmitrytsyvtsyn.fluently.happening_list.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.github.dmitrytsyvtsyn.fluently.core.theme.FluentlyTheme
import io.github.dmitrytsyvtsyn.fluently.core.theme.composables.FluentlyIcon
import io.github.dmitrytsyvtsyn.fluently.core.theme.composables.FluentlyText
import io.github.dmitrytsyvtsyn.fluently.happening_list.R
import io.github.dmitrytsyvtsyn.fluently.happening_list.models.HappeningTabModel
import kotlinx.collections.immutable.PersistentList

@Composable
internal fun HappeningTabs(tabs: PersistentList<HappeningTabModel>) {
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
private fun RowScope.StartTab(tabModel: HappeningTabModel) {
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
            FluentlyIcon(
                modifier = Modifier.size(16.dp),
                painter = painterResource(id = R.drawable.ic_back_day),
                contentDescription = ""
            )
            FluentlyText(
                text = tabModel.title,
                style = FluentlyTheme.typography.caption1
            )
        }
    }
}

@Composable
private fun RowScope.CenterTab(tabModel: HappeningTabModel) {
    val primaryColor = FluentlyTheme.colors.primaryColor
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
        FluentlyText(
            text = tabModel.title,
            style = FluentlyTheme.typography.caption1,
            color = primaryColor
        )
    }

}

@Composable
private fun RowScope.EndTab(tabModel: HappeningTabModel) {
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
            FluentlyText(
                text = tabModel.title,
                style = FluentlyTheme.typography.caption1
            )
            FluentlyIcon(
                modifier = Modifier.size(16.dp),
                painter = painterResource(id = R.drawable.ic_forward_day),
                contentDescription = ""
            )
        }
    }
}