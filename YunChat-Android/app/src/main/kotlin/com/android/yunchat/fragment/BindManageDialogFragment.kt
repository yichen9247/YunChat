package com.android.yunchat.fragment

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.DialogFragment
import com.android.yunchat.R
import com.android.yunchat.layout.UserBindingLayout
import com.android.yunchat.service.instance.activityServiceInstance
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import top.chengdongqing.weui.core.ui.theme.WeUITheme

class BindManageDialogFragment : DialogFragment() {
    var onDismissRequest: (() -> Unit)? = null

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissRequest?.invoke()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activityServiceInstance.initTencentService(requireContext()) // 初始化腾讯登录SDK
        return MaterialAlertDialogBuilder(requireContext(), R.style.DialogTheme).apply {
            setTitle("绑定管理")
            setView(ComposeView(requireContext()).apply {
                setContent {
                    WeUITheme {
                        UserBindingLayout({
                            dismiss()
                        })
                    }
                }
            })
        }.create()
    }
}