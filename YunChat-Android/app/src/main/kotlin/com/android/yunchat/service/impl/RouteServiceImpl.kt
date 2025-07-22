import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.android.yunchat.service.RouteService
import com.android.yunchat.state.GlobalState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @name 路由服务实现类
 * @author yichen9247
 */
class RouteServiceImpl : RouteService {
    /**
     * @name 导航回退
     */
    override fun navigationBack() {
        CoroutineScope(Dispatchers.Main).launch {
            navController?.popBackStack()
        }
    }

    /**
     * @name 导航到指定页面
     * @param route 路由
     * @param allowRepeat 是否允许重复
     * @description 若要跳转到的路由与当前路由一致，则不进行任何操作，保持在当前路由
     * @description 若允许重复，则会清除路由栈中所有路由，否则会清除路由栈中所有路由，直到当前路由为止
     */
    override fun navigationTo(
        route: String,
        allowRepeat: Boolean
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            if (route != navController?.currentDestination?.route) {
                navController?.let { nc ->
                    try {
                        nc.navigate(route, NavOptions.Builder()
                            .setLaunchSingleTop(true)
                            .setPopUpTo(route, allowRepeat)
                            .build())
                    } catch (_: IllegalArgumentException) {
                        if (route != nc.currentDestination?.route) nc.navigate(route)
                    }
                }
            }
        }
    }

    companion object {
        private val navController: NavController? get() = GlobalState.navController.value
    }
}