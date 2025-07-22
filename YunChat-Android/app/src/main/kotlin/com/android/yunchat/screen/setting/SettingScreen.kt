package com.android.yunchat.screen.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.yunchat.component.ArrowRightIcon
import com.android.yunchat.component.SubPageToolbar
import com.android.yunchat.config.UIConfig
import kotlinx.coroutines.launch

@Composable
fun SettingScreen() {
    Scaffold(
        topBar = {
            SubPageToolbar("软件设置")
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            SettingScreenBody()
        }
    }
}

@Composable
private fun SettingScreenBody() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    fun handleMenuClick(key: String) {

    }

    LazyColumn(
        modifier = Modifier
            .padding(15.dp, 0.dp)
    ) {
        item { Spacer(modifier = Modifier.height(7.5.dp)) }
        UIConfig.settingProjectList.forEach {
            item {
                SettingItem(
                    name = it.name,
                    icon = painterResource(id = it.icon),
                    click = {
                        scope.launch {
                            it.call(context)
                            handleMenuClick(it.key)
                        }
                    }
                )
            }
        }
        item { Spacer(modifier = Modifier.height(7.5.dp)) }
    }
}

@Composable
private fun SettingItem(
    name: String,
    icon: Painter,
    click: () -> Unit
) {
    Spacer(modifier = Modifier.height(7.5.dp))
    Box(
        modifier = Modifier
            .height(50.dp)
            .background(
                shape = RoundedCornerShape(4.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
            .clickable(
                onClick = click
            )
            .fillMaxWidth()
    ) {
        Row (
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp, 0.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = icon,
                    modifier = Modifier
                        .size(20.dp),
                    contentDescription = name,
                    tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = name,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            ArrowRightIcon()
        }
    }
    Spacer(modifier = Modifier.height(7.5.dp))
}