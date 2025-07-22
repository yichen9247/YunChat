package com.android.yunchat.screen.privacy

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.yunchat.component.SubPageToolbar
import com.android.yunchat.config.PolicyConfig

@Composable
fun PrivacyScreen() {
    Scaffold(
        topBar = {
            SubPageToolbar("隐私政策")
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            LazyColumn {
                item { PrivacyScreenBody() }
            }
        }
    }
}

@Composable
private fun PrivacyScreenBody() {
    SelectionContainer {
        Text(
            fontSize = 16.sp,
            lineHeight = 28.sp,
            modifier = Modifier
                .padding(15.dp),
            textAlign = TextAlign.Justify,
            text = PolicyConfig.userPolicy,
            color = Color(0xFF999999)
        )
    }
}