plugins {
    alias(libs.plugins.weui.android.compose.library)
}

android {
    namespace = "top.chengdongqing.weui.core.utils"
}

dependencies {
    implementation(libs.amap)
    implementation(libs.pinyin)
    implementation(libs.toasty)
    implementation(libs.xutil.sub)
    implementation(libs.xutil.core)
    implementation(libs.accompanist.permissions)

    implementation(project(":core:ui:theme"))
    implementation(project(":core:data:model"))
}