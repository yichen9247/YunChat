plugins {
    alias(libs.plugins.weui.android.compose.application)
}

android {
    namespace = "com.android.yunchat"

    buildFeatures {
        compose = true
    }

    defaultConfig {
        targetSdk = 34
        versionCode = 2504151
        multiDexEnabled = true
        versionName = "2.3.2-B21"
        applicationId = "com.android.yunchat"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }

        android {
            defaultConfig {
                ndk {
                    abiFilters.addAll(listOf("arm64-v8a", "x86_64"))
                }
            }
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            isCrunchPngs = false
            isMinifyEnabled = false
            isJniDebuggable = false
            isShrinkResources = false
            isPseudoLocalesEnabled = false
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    packaging {
        resources {
            excludes += "/META-INF/*.kotlin_module"
            excludes += "/META-INF/*.version"
            excludes += "/META-INF/proguard/*"
            excludes += "/META-INF/services/*"
            excludes += "**/attach_hotspot_windows.dll"
            excludes += "**/*.proto"
            excludes += "/META-INF/{AL2.0,LGPL2.1}"

            jniLibs {
                useLegacyPackaging = true
            }
        }

        dex {
            useLegacyPackaging = true
        }
    }
}

dependencies {
    implementation(libs.mmkv)
    implementation(libs.gson)
    implementation(libs.okhttp)
    implementation(libs.xutil.sub)
    implementation(libs.xutil.core)
    implementation(libs.app.updater)
    implementation(libs.reorderable)
    implementation(libs.coil.compose)
    implementation(libs.splashscreen)
    implementation(libs.socket.io.client)
    implementation(libs.bundles.retrofit)
    implementation(libs.lifecycle.common)
    implementation(libs.runtime.livedata)
    implementation(libs.lifecycle.service)
    implementation(libs.navigation.compose)
    implementation(libs.material.icons.core)
    implementation(libs.accompanist.permissions)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.multidex.multidex2)

    implementation(project(":core:utils"))
    implementation(project(":core:ui:theme"))
    implementation(project(":core:data:model"))
    implementation(project(":core:ui:components"))
    implementation("com.google.android.material:material:1.4.0")
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
}