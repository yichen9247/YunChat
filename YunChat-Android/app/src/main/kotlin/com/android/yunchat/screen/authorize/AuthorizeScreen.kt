package com.android.yunchat.screen.authorize

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.yunchat.R
import com.android.yunchat.component.PlainButton
import com.android.yunchat.request.model.UserRequestViewModel
import com.android.yunchat.state.GlobalState
import top.chengdongqing.weui.core.ui.components.cardlist.WeCardListItem

@Composable
fun AuthorizeScreen(
    userRequestViewModel: UserRequestViewModel = viewModel()
) {
    val scanCache by lazy {
        GlobalState.scanCache.value
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(65.dp, 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            tint = Color.Gray,
            modifier = Modifier
                .size(120.dp),
            contentDescription = "PC",
            painter = painterResource(R.drawable.ic_scan_pc)
        )
        Spacer(modifier = Modifier.height(35.dp))
        WeCardListItem("登录平台", scanCache?.platform, true)
        WeCardListItem("登录地址", scanCache?.address, true)
        Spacer(modifier = Modifier.height(35.dp))

        Button(
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(6.dp),
            onClick = {
                userRequestViewModel.fetchUserScanLogin(scanCache?.qid.toString(), 1)
            }
        ) {
            Text(
                text = "确认登录",
                fontSize = 16.sp,
                color = Color(0XFFFFFFFF)
            )
        }
        Spacer(modifier = Modifier.height(25.dp))
        PlainButton(
            text = "取消登录",
            onClick = {
                userRequestViewModel.fetchUserScanLogin(scanCache?.qid.toString(), 0)
            }
        )
    }
}