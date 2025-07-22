plugins {
    alias(libs.plugins.weui.android.compose.library)
}

android {
    namespace = "top.chengdongqing.weui.core.ui.components"
}

dependencies {
    implementation(libs.gson)
    implementation(libs.coil.compose)
    implementation(libs.bundles.camerax)
    implementation(libs.androidx.media3.ui)
    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.media3.exoplayer)

    implementation(libs.zxing.core)
    implementation(libs.mlkit.barcode.scanning)

    implementation(project(":core:utils"))
    implementation(project(":core:ui:theme"))
    implementation(project(":core:data:model"))
    implementation(project(":core:data:repository"))
}