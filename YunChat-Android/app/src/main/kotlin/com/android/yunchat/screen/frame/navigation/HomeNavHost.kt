package com.android.yunchat.screen.frame.navigation

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.yunchat.config.AppConfig
import com.android.yunchat.screen.authorize.AuthorizeScreen
import com.android.yunchat.screen.frame.home.HomeFrameScreen
import com.android.yunchat.state.GlobalState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.tencent.tauth.Tencent
import top.chengdongqing.weui.core.utils.isTrue

@Composable
@SuppressLint("PermissionLaunchedDuringComposition")
@OptIn(ExperimentalPermissionsApi::class)
fun HomeNavHost() {
    val navController = rememberNavController()
    GlobalState.navController.value = navController

    val permissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    } else null

    LaunchedEffect(Unit) {
        Tencent.setIsPermissionGranted(true);
        if (!(permissionState?.status?.isGranted.isTrue() || permissionState == null)) {
            permissionState.launchPermissionRequest()
        }
    }

    NavHost(
        startDestination = "home",
        navController = navController,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(AppConfig.animationSpec.toInt())
            )
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(AppConfig.animationSpec.toInt())
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(AppConfig.animationSpec.toInt())
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(AppConfig.animationSpec.toInt())
            )
        }
    ) {
        composable("home") {
            HomeFrameScreen()
        }

        composable("authorize") {
            AuthorizeScreen()
        }
    }
}