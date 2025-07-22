package com.android.yunchat.screen.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import com.android.yunchat.R
import com.android.yunchat.config.AppConfig
import com.android.yunchat.config.UIConfig
import com.android.yunchat.layout.HomeDrawerLayout
import com.android.yunchat.screen.frame.navigation.HomeNavHost
import com.android.yunchat.screen.home.view.HomeViewModel
import com.android.yunchat.service.instance.routeServiceInStance
import com.android.yunchat.service.instance.sharedHomeFrameViewModel
import com.android.yunchat.state.GlobalState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import top.chengdongqing.weui.core.utils.showToast
import kotlin.system.exitProcess

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )
    var backPressedTime by remember { mutableLongStateOf(0L) }

    LaunchedEffect(Unit) {
        scope.launch {
            delay(AppConfig.animationSpec)
            homeViewModel.checkApplicationUpdate(context)
        }
    }

    BackHandler(enabled = true) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - backPressedTime > 2000) {
            backPressedTime = currentTime
            context.showToast("再按一次退出应用")
        } else exitProcess(0)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column(
                modifier = Modifier
                    .clickable(
                        onClick = {},
                        indication = null,
                        interactionSource = null,
                    )
                    .fillMaxHeight()
                    .fillMaxWidth(0.75f)
                    .background(MaterialTheme.colorScheme.onBackground)
            ) {
                HomeDrawerLayout(drawerState)
            }
        },
        gesturesEnabled = true,
    ) {
        Scaffold(
            topBar = { HomeTopBar() },
            bottomBar = { HomeBottomBar(drawerState) }
        ) { innerPadding ->
            Box (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                HomeNavHost()
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun HomeTopBar() {
    val groupCount = remember {
        derivedStateOf { sharedHomeFrameViewModel.groupList.value.size }
    }

    val navController = GlobalState.navController.value
    val appName = stringResource(R.string.app_name)

    val currentBackStackEntry = navController?.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.value?.destination?.route

    val homeTitle = when (currentRoute) {
        "home" -> "聊天首页（${groupCount.value}）"
        "agent" -> "$appName AI"
        "search" -> "搜索群聊"
        "authorize" -> "登录授权"
        else -> "其它页面"
    }

    if (currentRoute != "user") TopAppBar(
        title = {
            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = homeTitle,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.onBackground,
            titleContentColor = MaterialTheme.colorScheme.secondary,
            actionIconContentColor = MaterialTheme.colorScheme.secondary,
            navigationIconContentColor = MaterialTheme.colorScheme.secondary
        )
    )
}

@Composable
private fun HomeBottomBar(drawerState: DrawerState) {
    val scope = rememberCoroutineScope()
    val navController = GlobalState.navController.value
    val currentBackStackEntry = navController?.currentBackStackEntryAsState()?.value
    val currentRoute = currentBackStackEntry?.destination?.route

    NavigationBar(
        contentColor = MaterialTheme.colorScheme.primary,
        containerColor = MaterialTheme.colorScheme.onBackground,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp, 0.dp)
        ) {
            UIConfig.homeNavigationBarList.forEach {
                NavigationBarItem(
                    icon = {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            contentDescription = it.key,
                            painter = painterResource(it.icon),
                        )
                    },
                    label = { Text(it.name) },
                    selected = currentRoute == it.key,
                    onClick = {
                        if (it.key == "more") {
                            scope.launch {
                                if (drawerState.isClosed) {
                                    drawerState.open()
                                } else drawerState.close()
                            }
                        } else routeServiceInStance.navigationTo(it.key, true)
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}