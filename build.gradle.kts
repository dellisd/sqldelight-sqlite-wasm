plugins {
  kotlin("multiplatform") version "1.8.0"
  id("app.cash.sqldelight") version "2.0.0-alpha05"
  id("org.jetbrains.compose") version "1.3.0-rc05"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
  maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
  google()
}

compose {
  kotlinCompilerPlugin.set("androidx.compose.compiler:compiler:1.4.0")
}

kotlin {
  js {
    binaries.executable()
    browser {
      commonWebpackConfig {
        cssSupport {
          enabled.set(true)
        }
      }
    }
  }

  sourceSets {
    val jsMain by getting {
      dependencies {
        implementation("app.cash.sqldelight:web-worker-driver")
        implementation("app.cash.sqldelight:coroutines-extensions:2.0.0-alpha05")
        implementation(compose.web.core)
        implementation(compose.runtime)
      }
    }

    val jsTest by getting {
      dependencies {
        implementation(kotlin("test"))
      }
    }
  }
}

sqldelight {
  databases {
    create("Database") {
      packageName.set("org.example.db")
      generateAsync.set(true)
    }
  }
}
