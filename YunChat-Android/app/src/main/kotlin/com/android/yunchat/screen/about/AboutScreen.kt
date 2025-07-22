package com.android.yunchat.screen.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.yunchat.R
import com.android.yunchat.activity.PrivacyActivity
import com.android.yunchat.component.SubPageToolbar
import com.android.yunchat.screen.home.view.HomeViewModel
import com.android.yunchat.service.instance.activityServiceInstance
import com.android.yunchat.service.instance.systemServiceInstance
import com.xuexiang.xutil.app.AppUtils.getAppVersionName

@Composable
fun AboutScreen() {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            SubPageToolbar(
                name = "关于软件",
                onBack = { activityServiceInstance.intentActivityBack(context) },
            )
        },
        bottomBar = {
            AboutFooter()
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            AboutScreenBody()
        }
    }
}

@Composable
private fun AboutScreenBody() {
    Column (
        modifier = Modifier
            .fillMaxHeight(1f)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))
        Image(
            modifier = Modifier
                .size(90.dp),
            painter = painterResource(R.drawable.yunchat),
            contentDescription = stringResource(R.string.app_name)
        )
        Spacer(modifier = Modifier.height(15.dp))
        Text(
            fontSize = 26.sp,
            color = MaterialTheme.colorScheme.secondary,
            text = stringResource(R.string.app_name)
        )
        Spacer(modifier = Modifier.height(15.dp))
        Text(
            fontSize = 16.sp,
            text = "版本号：${getAppVersionName()}",
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun AboutFooter(
    homeViewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current

    Column (
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.Center
        ) {
            AboutBottomText(
                text = "检查更新",
                onClick = {
                    homeViewModel.checkApplicationUpdate(context, true)
                }
            )
            Spacer(modifier = Modifier.width(5.dp))
            AboutBottomText(
                text = "用户协议与隐私政策",
                onClick = {
                    activityServiceInstance.intentActivityAboutContext(
                        context = context,
                        activity = PrivacyActivity::class.java
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        AboutBottomText(
            text = "@2025 yichen9247 All rights reserved",
            onClick = {
                systemServiceInstance.openUrlInBrowser(
                    context.getString(R.string.www_author_url)
                )
            }
        )
        Spacer(modifier = Modifier.height(25.dp))
    }
}


@Composable
private fun AboutBottomText(
    text: String,
    onClick: () -> Unit = {}
) {
    Text(
        text = text,
        fontSize = 13.sp,
        modifier = Modifier
            .clickable(
                interactionSource = null,
                indication = null,
                onClick = onClick
            ),
        color = MaterialTheme.colorScheme.secondary
    )
}