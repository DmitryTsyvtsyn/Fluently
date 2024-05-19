package io.github.dmitrytsyvtsyn.interfunny.theme_settings

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.dmitrytsyvtsyn.interfunny.LocalSettingsViewModel
import io.github.dmitrytsyvtsyn.interfunny.R
import io.github.dmitrytsyvtsyn.interfunny.core.navigation.LocalNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterviewThemeSettingsScreen() {
    val navController = LocalNavController.current
    val viewModel = LocalSettingsViewModel.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.theme_settings),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = "")
                    }
                }
            )
        },
    ) { innerPadding ->
        val state by viewModel.state.collectAsState()

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = stringResource(id = R.string.choose_contrast),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            )

            Spacer(modifier = Modifier.size(8.dp))

            state.contrasts.forEach { contrast ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.changeContrast(contrast) }
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    RadioButton(selected = contrast == state.contrast, onClick = {
                        viewModel.changeContrast(contrast)
                    })
                    Spacer(modifier = Modifier.size(8.dp))
                    Column {
                        Text(
                            text = stringResource(id = contrast.title),
                            fontWeight = FontWeight.Medium,
                            fontSize = 17.sp
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = stringResource(id = contrast.description),
                        )
                    }
                }

                //val color = if (contrast == state.contrast) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary

//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .border(
//                            color = color,
//                            width = 4.dp,
//                            shape = RoundedCornerShape(24.dp)
//                        )
//                        .clickable(
//                            interactionSource = MutableInteractionSource(),
//                            indication = rememberRipple(bounded = true)
//                        ) {
//                            viewModel.changeContrast(contrast)
//                        }
//                        .padding(48.dp)
//                ) {
//                    Text(
//                        text = stringResource(id = contrast.title),
//                        modifier = Modifier.align(Alignment.Center)
//                    )
//                }
            }
        }
    }
}