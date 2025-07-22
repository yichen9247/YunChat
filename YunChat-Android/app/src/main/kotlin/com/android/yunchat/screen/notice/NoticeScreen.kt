package com.android.yunchat.screen.notice

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
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.yunchat.component.CustomDivider
import com.android.yunchat.component.SubPageToolbar
import com.android.yunchat.model.NoticeItemModel
import com.android.yunchat.model.ResultListModel
import com.android.yunchat.screen.notice.view.NoticeViewModel
import top.chengdongqing.weui.core.DefaultLoadPage

@Composable
fun NoticeScreen(
    noticeViewModel: NoticeViewModel = viewModel()
) {
    DisposableEffect(Unit) {
        onDispose {
            noticeViewModel.noticeData.value = ResultListModel(
                total = 0,
                items = emptyList()
            )
        }
    }

    Scaffold(
        topBar = {
            SubPageToolbar("通知公告")
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            when {
                noticeViewModel.isLoading.value -> DefaultLoadPage()
                else -> NoticeScreenBody(noticeViewModel)
            }
        }
    }
}

@Composable
private fun NoticeScreenBody(noticeViewModel: NoticeViewModel) {
    LazyColumn (
        modifier = Modifier
            .padding(15.dp)
            .fillMaxSize()
    ) {
        noticeViewModel.noticeData.value.items.forEach {
            item { NoticeItem(it) }
        }
    }
}

@Composable
private fun NoticeItem(notice: NoticeItemModel) {
    SelectionContainer {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
        ) {
            Row(
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth()
                    .padding(15.dp, 0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    maxLines = 1,
                    fontSize = 18.sp,
                    text = notice.title,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            CustomDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp, 15.dp)
            ) {
                Text(
                    fontSize = 14.sp,
                    color = Color.Gray,
                    text = notice.content
                )
            }
            CustomDivider()
            Row(
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth()
                    .padding(15.dp, 0.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    maxLines = 1,
                    fontSize = 14.sp,
                    text = "平台管理员",
                    color = Color.Gray,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    maxLines = 1,
                    fontSize = 14.sp,
                    text = notice.time,
                    color = Color.Gray,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(15.dp))
}