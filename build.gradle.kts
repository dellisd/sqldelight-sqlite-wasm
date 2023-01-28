plugins {
  kotlin("multiplatform") version "1.8.0"
  id("app.cash.sqldelight") version "2.0.0-alpha05"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
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
        implementation("app.cash.sqldelight:sqljs-driver:2.0.0-alpha05")
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
