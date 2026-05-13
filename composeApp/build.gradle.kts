import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    kotlin("plugin.serialization") version "2.3.21"  // ← coincidir con kotlin version
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    jvm()

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
            implementation("com.squareup.okhttp3:okhttp:4.12.0")
            implementation("app.cash.sqldelight:android-driver:2.0.2")
            implementation("androidx.security:security-crypto:1.1.0-alpha06")
            implementation("androidx.work:work-runtime-ktx:2.9.0")
            implementation("com.google.code.gson:gson:2.10.1")
            implementation("io.ktor:ktor-client-okhttp:3.4.3")       // ← 3.4.3
            implementation("io.ktor:ktor-client-logging:3.4.3")      // ← 3.4.3
            implementation("androidx.compose.material:material-icons-extended:1.7.0")
            implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")
            implementation("androidx.lifecycle:lifecycle-viewmodel:2.8.0")
        }

        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(projects.shared)
            implementation(libs.kotlinx.datetime)                     // ← desde toml, solo aquí
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
            implementation("io.ktor:ktor-client-core:3.4.3")          // ← 3.4.3
            implementation("io.ktor:ktor-client-content-negotiation:3.4.3")
            implementation("org.jetbrains.compose.material:material-icons-extended:1.6.11")
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation("io.ktor:ktor-client-cio:3.4.3")           // ← CIO para Desktop
            implementation("io.ktor:ktor-client-logging:3.4.3")
            // kotlinx-datetime eliminado aquí — ya viene de commonMain
        }
    }
}

android {
    namespace = "com.example.sicemultiplataform"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.example.sicemultiplataform"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
    debugImplementation(libs.compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "com.example.sicemultiplataform.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.example.sicemultiplataform"
            packageVersion = "1.0.0"
        }
    }
}