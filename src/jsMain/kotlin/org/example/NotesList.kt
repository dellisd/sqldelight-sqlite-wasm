package org.example

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.example.db.Notes
import org.jetbrains.compose.web.dom.B
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text

@Composable
fun NotesList(wrapper: DatabaseWrapper) {
  val notes by
    flow {
      emitAll(
        wrapper.withDatabase { database ->
          database.notesQueries.getAll()
        }
          .asFlow()
          .mapToList(Dispatchers.Main)
      )
  }.collectAsState(emptyList())

  console.dir(notes)

  notes.forEach { note ->
    Div {
      Div {
        B { Text(note.creation) }
        P { Text(note.content) }
      }
    }
  }
}
