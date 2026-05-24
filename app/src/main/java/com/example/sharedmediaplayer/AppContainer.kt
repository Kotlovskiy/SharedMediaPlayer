package com.example.sharedmediaplayer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AppContainer(
    topBar: @Composable () -> Unit = {},
    horizontalPadding: Dp = 16.dp,
    verticalPadding: Dp = 0.dp,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        topBar()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        ) {
            content()
        }
    }
}
