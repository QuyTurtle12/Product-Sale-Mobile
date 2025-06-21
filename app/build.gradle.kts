plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.product_sale_app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.product_sale_app"
        minSdk = 33
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            buildConfigField("boolean", "DEBUG", "true")
        }
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11

        // Enable desugaring of java.time, Date.from, etc.
        isCoreLibraryDesugaringEnabled = true
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation("androidx.recyclerview:recyclerview:1.3.0")

    // Core-library desugaring for java.time, Date.from, etc.
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")

    // Google Maps SDK
    implementation ("com.google.android.gms:play-services-maps:18.1.0")
    // Location services
    implementation ("com.google.android.gms:play-services-location:21.0.1")

    implementation ("androidx.cardview:cardview:1.0.0")

    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")

    implementation(libs.glide.core)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converterGson)
    implementation(libs.okhttp.core)
    implementation(libs.okhttp.loggingInterceptor)
    implementation(libs.gson.core)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.palette)
    implementation(libs.androidx.media3.common)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
