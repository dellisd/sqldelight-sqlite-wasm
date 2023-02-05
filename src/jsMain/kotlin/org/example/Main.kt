package org.example

import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.Worker

fun main() {
  val worker = Worker(js("""new URL("sqlite.worker.js", import.meta.url)""").unsafeCast<String>())
  val driver = WebWorkerDriver(worker)
  val wrapper = DatabaseWrapper(driver)

  renderComposable(rootElementId = "root") {
    val notes = remember {
      flow {
        emitAll(wrapper.withDatabase { database ->
          database.notesQueries.getAll().asFlow().mapToList(Dispatchers.Main)
        })
      }
    }

    val scope = rememberCoroutineScope()

    P { Text("Write a note here and save it to the database:") }
    NoteWriter(onSubmit = { content ->
      scope.launch {
        wrapper.withDatabase { database ->
          database.notesQueries.add(content)
        }
      }
    })
    NotesList(notes)
  }
}
