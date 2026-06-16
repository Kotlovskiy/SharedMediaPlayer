package com.example.hello.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hello.R

@Composable
fun HelloScreen(
    modifier: Modifier = Modifier,
    onCreateRoom: () -> Unit,
    onJoinRoom: () -> Unit
) {
    Column(
        modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            modifier = Modifier.fillMaxWidth().height(48.dp),
            onClick = {
                onCreateRoom()
            }
        ) {
            Text(
                modifier = Modifier.fillMaxSize(),
                text = stringResource(R.string.create_room),
                textAlign = TextAlign.Center,
                fontSize = 24.sp
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            modifier = Modifier.fillMaxWidth().height(48.dp),
            onClick = {
                onJoinRoom()
            }
        ) {
            Text(
                modifier = Modifier.fillMaxSize(),
                text = stringResource(R.string.join_room),
                textAlign = TextAlign.Center,
                fontSize = 24.sp
            )
        }
    }
}
