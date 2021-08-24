plugins {
    id("org.jetbrains.kotlin.kapt")
    id("com.android.application")
    id("kotlin-android")
    id("dagger.hilt.android.plugin")
    kotlin("plugin.serialization") version "1.5.21"
    id("androidx.navigation.safeargs.kotlin")
}

val lifecycleVersion = "2.3.1"
val room_version = "2.3.0"
val nav_version = "2.3.5"
val dagger_version = "2.38.1"
val ktor_version = "1.6.1"
val composeVersion = "1.0.1"
val workVersion = "2.4.0"
val codeHighlighterVersion = "v2.0.0"
val markwon_version = "4.6.0"

android {
    compileSdk = 30

    defaultConfig {
        applicationId = "com.kotme"
        minSdk = 21
        targetSdk = 30
        versionCode = 1
        versionName = "1.0"

        setProperty("archivesBaseName", "kotme")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true",
                    "room.expandProjection" to "true"
                )
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.21")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.0")

    implementation("androidx.core:core-ktx:1.6.0")
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.0")
    implementation("androidx.annotation:annotation:1.2.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.fragment:fragment-ktx:1.3.6")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.security:security-crypto:1.1.0-alpha03")
    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")
    implementation("androidx.work:work-runtime-ktx:$workVersion")

    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    kapt("androidx.room:room-compiler:$room_version")

    implementation("com.google.android.material:material:1.4.0")

    implementation("org.slf4j:slf4j-simple:1.7.30")

    implementation("pl.droidsonroids.gif:android-gif-drawable:1.2.19")

    implementation("com.github.markusressel.KodeHighlighter:core:$codeHighlighterVersion")
    implementation("com.github.markusressel.KodeHighlighter:kotlin:$codeHighlighterVersion")

    implementation("com.google.dagger:hilt-android:$dagger_version")
    kapt("com.google.dagger:hilt-android-compiler:$dagger_version")

    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-android:$ktor_version")
    implementation("io.ktor:ktor-client-auth:$ktor_version")
    implementation("io.ktor:ktor-client-logging-jvm:$ktor_version")
    implementation("io.ktor:ktor-client-serialization:$ktor_version")

    implementation("io.noties.markwon:core:$markwon_version")
    implementation("io.noties.markwon:syntax-highlight:$markwon_version") {
        exclude(group = "org.jetbrains", module = "annotations-java5")
    }

    implementation("com.kotme:kotme-common:0.0.0")
}