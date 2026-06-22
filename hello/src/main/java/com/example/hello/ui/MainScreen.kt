package com.example.hello.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.core_ui.theme.Typography
import com.example.hello.R

@Composable
fun HelloScreen(
    modifier: Modifier = Modifier,
    onCreateRoom: () -> Unit,
    onJoinRoom: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier
            .height(1.dp)
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.primary)
        )
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                modifier = Modifier.fillMaxWidth().heightIn(min = 60.dp, max = 120.dp),
                onClick = {
                    onCreateRoom()
                }
            ) {
                Text(
                    text = stringResource(R.string.create_room),
                    textAlign = TextAlign.Center,
                    style = Typography.titleLarge
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            OutlinedButton(
                modifier = Modifier.fillMaxWidth().heightIn(min = 60.dp, max = 120.dp),
                onClick = {
                    onJoinRoom()
                }
            ) {
                Text(
                    text = stringResource(R.string.join_room),
                    textAlign = TextAlign.Center,
                    style = Typography.titleLarge
                )
            }

            Spacer(modifier = Modifier
                .height(1.dp)
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.primary)
            )
        }
    }

}
