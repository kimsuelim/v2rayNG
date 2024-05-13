plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")

    id("io.sentry.android.gradle") version "4.4.1"
}

android {
    namespace = "com.v2ray.ang"
    compileSdk = 34

    defaultConfig {
        applicationId = "net.giantpanda.app"
        minSdk = 21
        targetSdk = 34
        versionCode = 20
        versionName = "0.20.0"
        multiDexEnabled = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")

            buildConfigField("String", "API_HOST_URL", "\"${System.getenv("API_HOST_URL")}\"")
            buildConfigField("String", "ADMIN_USERNAME", "\"${System.getenv("ADMIN_USERNAME")}\"")
            buildConfigField("String", "ADMIN_PASSWORD", "\"${System.getenv("ADMIN_PASSWORD")}\"")
        }

        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true

            buildConfigField("String", "API_HOST_URL", project.properties["DEBUG_API_URL"].toString())
            buildConfigField("String", "ADMIN_USERNAME", project.properties["ADMIN_USERNAME"].toString())
            buildConfigField("String", "ADMIN_PASSWORD", project.properties["ADMIN_PASSWORD"].toString())
        }

        create("staging") {
            initWith(getByName("debug"))
            applicationIdSuffix = ".staging"
            buildConfigField("String", "API_HOST_URL", "\"${System.getenv("API_HOST_URL")}\"")
            buildConfigField("String", "ADMIN_USERNAME", "\"${System.getenv("ADMIN_USERNAME")}\"")
            buildConfigField("String", "ADMIN_PASSWORD", "\"${System.getenv("ADMIN_PASSWORD")}\"")
        }

        create("internal") {
            initWith(getByName("debug"))
            applicationIdSuffix = ".test"
            buildConfigField("String", "API_HOST_URL", project.properties["INTERNAL_API_URL"].toString())
        }
    }

    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("libs")
        }
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    splits {
        abi {
            isEnable = true
            reset()
            include("arm64-v8a", "x86_64")
            isUniversalApk = false
        }
    }

    applicationVariants.all {
        val variant = this
        val versionCodes =
            mapOf("armeabi-v7a" to 1, "arm64-v8a" to 2, "x86" to 3, "x86_64" to 4)

        variant.outputs
            .map { it as com.android.build.gradle.internal.api.ApkVariantOutputImpl }
            .forEach { output ->
                val abi = if (output.getFilter("ABI") != null)
                    output.getFilter("ABI")
                else
                    "all"

                output.outputFileName = "panda_${variant.versionName}_${abi}.apk"
                if (versionCodes.containsKey(abi)) {
                    output.versionCodeOverride =
                        (1000000 * versionCodes[abi]!!).plus(variant.versionCode)
                } else {
                    return@forEach
                }
            }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.12"
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar","*.jar"))))
    testImplementation("junit:junit:4.13.2")

    // Androidx
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("androidx.multidex:multidex:2.0.1")
    implementation("androidx.viewpager2:viewpager2:1.1.0-beta02")

    // Androidx ktx
    implementation("androidx.activity:activity-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    //kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.23")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")

    implementation("com.tencent:mmkv-static:1.3.4")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("io.reactivex:rxjava:1.3.8")
    implementation("io.reactivex:rxandroid:1.2.1")
    implementation("com.tbruyelle.rxpermissions:rxpermissions:0.9.4@aar")
    implementation("me.drakeet.support:toastcompat:1.1.0")
    implementation("com.blacksquircle.ui:editorkit:2.9.0")
    implementation("com.blacksquircle.ui:language-base:2.9.0")
    implementation("com.blacksquircle.ui:language-json:2.9.0")
    implementation("io.github.g00fy2.quickie:quickie-bundled:1.9.0")
    implementation("com.google.zxing:core:3.5.3")

    implementation("androidx.work:work-runtime-ktx:2.8.1")
    implementation("androidx.work:work-multiprocess:2.8.1")

    // Splash Screen
    implementation("androidx.core:core-splashscreen:1.2.0-alpha01")

    // Auth
    implementation("com.auth0.android:jwtdecode:2.0.2")

    // Compose
    val composeBom = platform("androidx.compose:compose-bom:2024.04.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.activity:activity-compose")
    implementation("androidx.compose.runtime:runtime")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.foundation:foundation-layout")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.runtime:runtime-livedata")
    implementation("androidx.compose.ui:ui-tooling")
}

sentry {
    org.set("imc-5b")
    projectName.set("android")
    includeSourceContext.set(true)
}
