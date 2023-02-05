import de.undercouch.gradle.tasks.download.Download

plugins {
  kotlin("multiplatform") version "1.8.0"
  id("app.cash.sqldelight") version "2.0.0-SNAPSHOT"
  id("org.jetbrains.compose") version "1.3.0"
  id("de.undercouch.download") version "5.3.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
  maven("https://oss.sonatype.org/content/repositories/snapshots")
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
        implementation("app.cash.sqldelight:web-worker-driver:2.0.0-SNAPSHOT")
        implementation("app.cash.sqldelight:coroutines-extensions:2.0.0-SNAPSHOT")
        implementation(compose.web.core)
        implementation(compose.runtime)
      }

      resources.srcDir(layout.buildDirectory.dir("sqlite"))
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

// See https://sqlite.org/download.html for the latest wasm build version
val sqlite = 3400000

val sqliteDownload = tasks.register("sqliteDownload", Download::class.java) {
  src("https://sqlite.org/2022/sqlite-wasm-$sqlite.zip")
  dest(layout.buildDirectory.dir("tmp"))
  onlyIfModified(true)
}

val sqliteUnzip = tasks.register("sqliteUnzip", Copy::class.java) {
  dependsOn(sqliteDownload)
  from(zipTree(layout.buildDirectory.dir("tmp/sqlite-wasm-$sqlite.zip"))) {
    include("sqlite-wasm-$sqlite/jswasm/**")
    exclude("**/*worker1*")

    eachFile {
      relativePath = RelativePath(true, *relativePath.segments.drop(2).toTypedArray())
    }
  }
  into(layout.buildDirectory.dir("sqlite"))
  includeEmptyDirs = false
}

tasks.named("jsProcessResources").configure {
  dependsOn(sqliteUnzip)
}
