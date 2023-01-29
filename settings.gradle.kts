rootProject.name = "sqldelight-sqlite-wasm"

includeBuild("sqldelight") {
  dependencySubstitution {
    substitute(module("app.cash.sqldelight:web-worker-driver")).using(project(":drivers:web-worker-driver"))
  }
}
