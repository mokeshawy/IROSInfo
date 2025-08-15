plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.irosinfo"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.irosinfo"
        minSdk = 24
        targetSdk = 36
        versionCode = 6
        versionName = "1.5.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
            isMinifyEnabled = false
            isShrinkResources = false
        }
        getByName("release") {
            //signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.txt")
        }
    }

    flavorDimensions.add("version")
    productFlavors {
        create("staging") {
            dimension = "version"
            versionNameSuffix = ".stage"
            applicationIdSuffix = ".stage"
        }

        create("live") {
            dimension = "version"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        //noinspection DataBindingWithoutKapt
        dataBinding = true
        viewBinding = true
    }

}

dependencies {

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))

    implementation("androidx.test.ext:junit-ktx:1.3.0")

    implementation(project(path = ":common"))
    implementation(project(path = ":common:bases"))
    implementation(project(path = ":common:storage"))
    implementation(project(path = ":common:camera"))
    implementation(project(path = ":common:connectivity"))
    implementation(project(path = ":common:crash_reporting"))
    implementation(project(path = ":common:extensions"))
    implementation(project(path = ":common:utils"))

    api("com.google.dagger:hilt-android:2.57")
    kapt("com.google.dagger:hilt-android-compiler:2.57")
    kapt("androidx.hilt:hilt-compiler:1.2.0")
    api("androidx.hilt:hilt-navigation-fragment:1.2.0")
}