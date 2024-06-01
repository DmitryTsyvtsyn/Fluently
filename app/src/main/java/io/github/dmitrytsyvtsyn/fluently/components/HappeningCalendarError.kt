package io.github.dmitrytsyvtsyn.fluently.components

import android.Manifest
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import io.github.dmitrytsyvtsyn.fluently.R

@Composable
fun HappeningCalendarError(launcher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.size(32.dp))

        Image(
            painter = painterResource(id = R.drawable.ic_permission_denied),
            contentDescription = "",
            modifier = Modifier
                .width(300.dp)
                .padding(start = 16.dp, end = 16.dp)
                .clip(RoundedCornerShape(32.dp)),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.size(16.dp))

        Text(
            stringResource(id = R.string.calendar_permission_denied),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp),
            textAlign = TextAlign.Center,
            fontSize = 23.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.size(32.dp))

        Button(
            onClick = {
                launcher.launch(arrayOf(
                    Manifest.permission.READ_CALENDAR,
                    Manifest.permission.WRITE_CALENDAR
                ))
            },
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                stringResource(id = R.string.request_access_again),
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}