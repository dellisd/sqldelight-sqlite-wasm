package org.example

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.TextArea

@Composable
fun NoteWriter(onSubmit: (String) -> Unit) {
  var text by remember { mutableStateOf("") }

  Div {
    Div {
      TextArea(attrs = {
        placeholder("Note content")
        onChange {
          text = it.value
        }
      })
    }
    Div {
      Button(attrs = {
        onClick { onSubmit(text) }
      }) {
        Text("Save")
      }
    }
  }
}
