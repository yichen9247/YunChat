package com.android.yunchat.screen.voice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.yunchat.component.SubPageToolbar
import com.android.yunchat.config.UIConfig
import com.android.yunchat.service.instance.storageServiceInstance
import top.chengdongqing.weui.core.ui.components.radio.WeRadioGroup
import top.chengdongqing.weui.core.ui.components.switch.WeSwitch

@Composable
fun VoiceScreen() {
    Scaffold(
        topBar = {
            SubPageToolbar("通知设置")
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            NoticeBody()
        }
    }
}

@Composable
private fun NoticeBody() {
    LazyColumn (
        modifier = Modifier
            .padding(15.dp)
            .fillMaxWidth()
    ) {
        item { NoticeSwitch() }
        item { Spacer(Modifier.height(15.dp)) }
        item { NoticeAudio() }
    }
}

@Composable
private fun NoticeSwitch() {
    val noticeOpened = remember { mutableStateOf(
        storageServiceInstance.mmkv.decodeBool("voice", false)
    ) }
    Row(
        modifier = Modifier
            .height(55.dp)
            .fillMaxWidth()
            .background(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
            .padding(15.dp, 0.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "声音提醒",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.secondary
        )
        WeSwitch(checked = noticeOpened.value) {
            noticeOpened.value = it
            storageServiceInstance.mmkv.encode("voice", it)
        }
    }
}

@Composable
private fun NoticeAudio() {
    var audioValue by remember { mutableStateOf(
        storageServiceInstance.mmkv.decodeString("audio", "default")
    ) }
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .background(
                shape = RoundedCornerShape(4.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
            .padding(15.dp, 0.dp, 15.dp, 10.dp),
    ) {
        WeRadioGroup(
            value = audioValue,
            options = UIConfig.audioOptionList
        ) {
            audioValue = it
            storageServiceInstance.mmkv.encode("audio", it)
        }
    }
    Spacer(modifier = Modifier.height(15.dp))
}