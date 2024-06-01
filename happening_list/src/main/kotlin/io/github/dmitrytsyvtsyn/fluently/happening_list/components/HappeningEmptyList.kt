package io.github.dmitrytsyvtsyn.fluently.happening_list.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.dmitrytsyvtsyn.fluently.happening_list.R

@Composable
internal fun HappeningEmptyList() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier.size(32.dp))

        Image(
            painter = painterResource(id = R.drawable.ic_relax),
            contentDescription = "",
            modifier = Modifier
                .width(300.dp)
                .padding(start = 16.dp, end = 16.dp)
                .clip(RoundedCornerShape(32.dp)),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.size(16.dp))

        Text(
            text = stringResource(id = R.string.today_not_interviews),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp),
            textAlign = TextAlign.Center,
            lineHeight = 30.sp,
            fontSize = 23.sp,
            fontWeight = FontWeight.Medium
        )
    }
}