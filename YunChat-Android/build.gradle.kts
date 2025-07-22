// Top-level build file where you can add configuration options common to all sub-projects/modules.
@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.com.android.application) apply false
    alias(libs.plugins.org.jetbrains.kotlin.android) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.com.google.devtools.ksp) apply false
    alias(libs.plugins.room) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
}
true // Needed to make the Suppress annotation work for the plugins block