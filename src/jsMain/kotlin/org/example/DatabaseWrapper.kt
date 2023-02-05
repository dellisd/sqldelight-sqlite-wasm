package org.example

import app.cash.sqldelight.async.coroutines.await
import app.cash.sqldelight.async.coroutines.awaitCreate
import app.cash.sqldelight.async.coroutines.awaitMigrate
import app.cash.sqldelight.async.coroutines.awaitQuery
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import org.example.db.Database

class DatabaseWrapper(private val driver: WebWorkerDriver) {
  private var database: Database? = null

  suspend fun initializeDatabase() {
    if (database != null) return

    database = Database(driver).also { migrateIfNeeded() }
  }

  suspend fun <R> withDatabase(block: suspend (Database) -> R): R {
    initializeDatabase()
    return block(database!!)
  }

  private suspend fun migrateIfNeeded() {
    val oldVersion =
      driver.awaitQuery(null, "PRAGMA $VERSION_PRAGMA", mapper = { cursor ->
        if (cursor.next()) {
          cursor.getLong(0)?.toInt()
        } else {
          null
        }
      }, 0) ?: 0

    val newVersion = Database.Schema.version

    if (oldVersion == 0) {
      Database.Schema.awaitCreate(driver)
      driver.await(null, "PRAGMA $VERSION_PRAGMA=$newVersion", 0)
    } else if (oldVersion < newVersion) {
      Database.Schema.awaitMigrate(driver, oldVersion, newVersion)
      driver.await(null, "PRAGMA $VERSION_PRAGMA=$newVersion", 0)
    }
  }

  companion object {
    private const val VERSION_PRAGMA = "user_version"
  }
}