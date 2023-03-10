# sqldelight-sqlite-wasm

This is a proof-of-concept showing SQLDelight running with the [SQLite project's WebAssembly](https://sqlite.org/wasm/doc/trunk/index.md) build.

This project uses the [Origin-Private FileSystem storage](https://sqlite.org/wasm/doc/trunk/persistence.md#opfs) method to persist the database.

## Demo

https://dellisd.github.io/sqldelight-sqlite-wasm

## Build Requirements

This repository is set up to build as-is right away, however it does a couple of special things.

1. This project currently uses SNAPSHOT builds of SQLDelight.
2. SQLite's WebAssembly builds are not distributed through NPM, so a couple of custom tasks are set up in [`build.gradle.kts`](build.gradle.kts) to download and extract the required static resources.

## Using the sqlite-wasm driver

Copy the [`sqlite.worker.js`](src/jsMain/resources/sqlite.worker.js) file.

In code, load the SQLDelight worker driver with this worker script.

```kotlin 
val worker = Worker(js("""new URL("sqlite.worker.js", import.meta.url)""").unsafeCast<String>())
val driver: SqlDriver = WebWorkerDriver(worker)

// Use your driver!
Database.Schema.awaitCreate(driver)
```

Make sure that you've also followed the build requirements section to ensure that the static sqlite-wasm files are also in your project's resources.
