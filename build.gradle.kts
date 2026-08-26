plugins {
    id("com.android.application") version "8.1.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
}

// ❌ NO declares repositorios aquí, ya están en settings.gradle.kts
// ✅ Solo dejamos la tarea clean
tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
