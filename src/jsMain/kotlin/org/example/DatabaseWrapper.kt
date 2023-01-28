package org.example

import org.example.db.Database

class DatabaseWrapper {
  fun withDatabase(block: (Database) -> Unit) {
  }
}
