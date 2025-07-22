package com.android.yunchat.component

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.yunchat.config.UIConfig
import com.android.yunchat.screen.chat.view.ChatViewModel
import top.chengdongqing.weui.core.ui.components.popup.WePopup

@Composable
fun PopupFragment(
    title: String,
    visible: Boolean,
    draggable: Boolean,
    onClose: () -> Unit,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit,
) {
    val scrollState = rememberScrollState()

    WePopup(
        title = title,
        visible = visible,
        onClose = onClose,
        draggable = draggable
    ) {
        Spacer(modifier = Modifier.height(5.dp))
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.48f)
                .padding(15.dp, 0.dp, 15.dp, 15.dp)
                .verticalScroll(state = scrollState),
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment
        ) { content() }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
@SuppressLint("UnusedBoxWithConstraintsScope")
fun ChatEmojiPopup(
    visible: Boolean,
    draggable: Boolean,
    onClose: () -> Unit,
    chatViewModel: ChatViewModel = viewModel()
) {
    PopupFragment(
        title = "发送表情",
        visible = visible,
        onClose = onClose,
        draggable = draggable
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth()
        ) {
            val itemWidth = maxWidth / 8
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth(),
                maxItemsInEachRow = 8,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                UIConfig.chatEmojiCharList.forEach { item ->
                    Box(
                        modifier = Modifier
                            .height(35.dp)
                            .width(itemWidth)
                            .clickable(
                                onClick = {
                                    onClose()
                                    chatViewModel.inputValue.value += item
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item,
                            fontSize = 24.sp
                        )
                    }
                }
            }
        }
    }
}