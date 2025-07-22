package com.android.yunchat.service.impl

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.LayoutInflater
import androidx.fragment.app.FragmentActivity
import com.android.yunchat.R
import com.android.yunchat.activity.LoginActivity
import com.android.yunchat.activity.SearchActivity
import com.android.yunchat.enmu.InputDialogType
import com.android.yunchat.fragment.BindManageDialogFragment
import com.android.yunchat.model.UpdateInfoModel
import com.android.yunchat.request.model.GroupRequestViewModel
import com.android.yunchat.service.DialogService
import com.android.yunchat.service.instance.activityServiceInstance
import com.android.yunchat.service.instance.fileServiceInstance
import com.android.yunchat.service.instance.storageServiceInstance
import com.android.yunchat.service.instance.systemServiceInstance
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.xuexiang.xutil.XUtil
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import top.chengdongqing.weui.core.ui.components.dialog.DialogState
import top.chengdongqing.weui.core.ui.components.toast.ToastState

/**
 * @name 弹窗服务实现类
 * @author yichen9247
 */
class DialogServiceImpl : DialogService {
    /**
     * @name 打开退出登录弹窗
     * @param context 上下文
     */
    override fun openExitLoginDialog(context: Context) {
        createBaseDialog(context)
            .setTitle("退出登录")
            .setMessage(context.getString(R.string.exit_login_tip))
            .setPositiveButton("确认退出") { _, _ ->
                storageServiceInstance.mmkv.let {
                    it.remove("uid")
                    it.remove("token")
                }
                activityServiceInstance.intentActivityAboutContext(context, LoginActivity::class.java)
                (context as Activity).finish()
            }
            .show()
    }

    /**
     * @name 打开电池优化弹窗
     * @param context 上下文
     */
    override fun openBatteryOptimizationDialog(context: Context) {
        val activity = context as? Activity ?: return
        createBaseDialog(activity)
            .setTitle("打开设置")
            .setMessage(activity.getString(R.string.battery_optimization))
            .setPositiveButton("前往设置") { _, _ ->
                activity.startActivity(
                    Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                )
            }
            .setOnDismissListener {
                activityServiceInstance.intentActivityBack(activity)
            }
            .show()
    }

    /**
     * @name 打开清除缓存弹窗
     * @param context 上下文
     */
    override fun openClearAppCacheDialog(context: Context) {
        createBaseDialog(context)
            .setTitle("清除缓存")
            .setMessage(context.getString(R.string.cache_option, systemServiceInstance.getApplicationCacheSize()))
            .setPositiveButton("立即清理") { _, _ ->
                systemServiceInstance.clearApplicationCache()
            }
            .show()
    }

    /**
     * @name 打开忘记密码弹窗
     * @param dialogState 弹窗状态
     */
    override fun openForgetPasswordDialog(dialogState: DialogState) {
        val context = XUtil.getContext()
        dialogState.show(
            title = "忘记密码",
            okText = "联系管理员",
            content = context.getString(R.string.reset_password_tip),
            cancelText = "知道了",
            onOk = {
                systemServiceInstance.openUrlInBrowser(context.getString(R.string.www_qq_qun))
            }
        )
    }

    /**
     * @name 打开更新应用弹窗
     * @param data 更新信息
     * @param context 上下文
     */
    override fun openUpdateApplicationDialog(
        context: Context,
        data: UpdateInfoModel
    ) {
        createBaseDialog(context)
            .setTitle("有新版本可以更新")
            .setMessage(context.getString(R.string.update_option, data.version))
            .setPositiveButton("立即更新") { _, _ ->
                systemServiceInstance.updateApplication(data)
            }
            .show()
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun openExitGroupDialog(
        gid: Int,
        name: String,
        context: Context
    ) {
        createBaseDialog(context)
            .setTitle("退出群聊")
            .setMessage(context.getString(R.string.exit_group_tip, name))
            .setPositiveButton("确认退出") { _, _ ->
                GlobalScope.launch {
                    GroupRequestViewModel().fetchExitGroup(gid) {
                        activityServiceInstance.intentActivityBack(context)
                    }
                }
            }
            .show()
    }

    /**
     * @name 打开保存图片弹窗
     * @param url 图片地址
     * @param toastState 提示状态
     * @param dialogState 弹窗状态
     */
    @OptIn(DelicateCoroutinesApi::class)
    override fun openSaveImageDialog(
        url: String,
        toastState: ToastState,
        dialogState: DialogState
    ) {
        dialogState.show(
            okText = "保存",
            cancelText = "取消",
            onOk = {
                GlobalScope.launch {
                    fileServiceInstance.saveImage(
                        content = url,
                        toastState = toastState
                    )
                }
            },
            title = "保存图片到相册",
        )
    }

    /**
     * @name 打开搜索群聊弹窗
     * @param context 上下文
     */
    @SuppressLint("InflateParams")
    override fun openSearchGroupDialog(context: Context) {
        showInputDialog(
            title = "搜索群聊",
            context = context,
            initialValue = null,
            positiveText = "确认搜索",
            type = InputDialogType.SEARCH_GROUP,
        ) { inputValue ->
            activityServiceInstance.intentActivityAboutContext(
                context = context,
                activity = SearchActivity::class.java
            ) {
                putString("value", inputValue)
            }
        }
    }

    /**
     * @name 打开修改昵称弹窗
     * @param context 上下文
     */
    override fun openEditUserNickDialog(
        nick: String,
        context: Context,
        callback: (String) -> Unit
    ) {
        showInputDialog(
            title = "修改昵称",
            context = context,
            initialValue = nick,
            positiveText = "确认修改",
            type = InputDialogType.EDIT_NICK
        ) { inputValue ->
            callback(inputValue)
        }
    }

    /**
     * @name 打开修改密码弹窗
     * @param context 上下文
     */
    override fun openEditPasswordDialog(
        context: Context,
        callback: (String) -> Unit
    ) {
        showInputDialog(
            title = "修改密码",
            context = context,
            initialValue = null,
            positiveText = "确认修改",
            type = InputDialogType.EDIT_PASSWORD
        ) { inputValue ->
            callback(inputValue)
        }
    }

    fun openUserBindManageDialog(context: Context) {
        if (context is FragmentActivity) {
            BindManageDialogFragment().show(context.supportFragmentManager, "BindManageDialog")
        }
    }

    /**
     * 创建基础对话框（带取消按钮）
     */
    private fun createBaseDialog(context: Context): MaterialAlertDialogBuilder {
        return MaterialAlertDialogBuilder(context, R.style.DialogTheme)
            .setNegativeButton("取消") { dialog, _ -> dialog.dismiss() }
    }

    private fun showInputDialog(
        title: String,
        context: Context,
        positiveText: String,
        initialValue: String?,
        type: InputDialogType,
        onPositiveClick: (String) -> Unit
    ) {
        val view = LayoutInflater.from(context).inflate(R.layout.input_layout, null)
        val inputField = view.findViewById<TextInputEditText>(R.id.input_value)
        when (type) {
            InputDialogType.EDIT_PASSWORD -> {
                inputField.hint = "请输入要修改的密码"
                inputField.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            InputDialogType.EDIT_NICK -> inputField.hint = "请输入要修改的昵称"
            InputDialogType.SEARCH_GROUP -> inputField.hint = "请输入群聊名称"
        }
        initialValue?.let { inputField.setText(it) }
        createBaseDialog(context)
            .setView(view)
            .setTitle(title)
            .setPositiveButton(positiveText) { _, _ ->
                onPositiveClick(inputField.text?.toString() ?: "")
            }
            .show()
    }
}