import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    kotlin("plugin.serialization") version "2.3.21"  // ← era 2.0.0
    id("app.cash.sqldelight") version "2.0.2"
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")  // ← era 1.8.1
            implementation(libs.kotlinx.datetime)

            implementation("io.ktor:ktor-client-core:3.4.3")                        // ← era 3.0.0
            implementation("io.ktor:ktor-client-logging:3.4.3")                     // ← era 3.0.0
            implementation("io.ktor:ktor-client-content-negotiation:3.4.3")         // ← era 3.0.0

            implementation("app.cash.sqldelight:runtime:2.0.2")
            implementation("app.cash.sqldelight:coroutines-extensions:2.0.2")
        }

        androidMain.dependencies {
            implementation("io.ktor:ktor-client-okhttp:3.4.3")                      // ← era 3.0.0
            implementation("app.cash.sqldelight:android-driver:2.0.2")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2") // ← era 1.8.1
        }

        jvmMain.dependencies {
            implementation("io.ktor:ktor-client-cio:3.4.3")                         // ← era okhttp 3.0.0
            implementation("app.cash.sqldelight:sqlite-driver:2.0.2")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.10.2") // ← era 1.8.1
            implementation("org.xerial:sqlite-jdbc:3.45.3.0")
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.example.sicemultiplataform.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}

sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set("com.example.sicemultiplataform.db")
            srcDirs("src/commonMain/sqldelight")
        }
    }
}