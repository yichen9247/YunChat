package top.chengdongqing.weui.core.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.xuexiang.xutil.XUtil

fun getIsVpnConnected(): Boolean {
    val connectivityManager = XUtil.getContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true
}