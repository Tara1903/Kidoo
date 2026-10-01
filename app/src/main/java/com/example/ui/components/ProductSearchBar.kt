package com.example.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.KidooCardBorder
import com.example.ui.theme.KidooPrimary
import com.example.ui.theme.KidooSurfaceContainerLowest

/**
 * Interactive search bar at the top of the product screen to filter items by name.
 */
@Composable
fun ProductSearchBar(
  query: String,
  onQueryChange: (String) -> Unit,
  onClearQuery: () -> Unit,
  modifier: Modifier = Modifier,
  placeholderText: String = "Search products by name..."
) {
  val focusManager = LocalFocusManager.current

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .height(50.dp)
      .clip(RoundedCornerShape(16.dp))
      .border(1.dp, KidooCardBorder, RoundedCornerShape(16.dp))
      .testTag("product_search_bar_surface"),
    color = KidooSurfaceContainerLowest,
    shadowElevation = 1.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = Icons.Default.Search,
        contentDescription = "Search products",
        tint = KidooPrimary,
        modifier = Modifier.size(20.dp)
      )

      Spacer(modifier = Modifier.width(10.dp))

      BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
          .weight(1f)
          .testTag("product_search_input_field"),
        textStyle = MaterialTheme.typography.bodyMedium.copy(
          color = MaterialTheme.colorScheme.onSurface,
          fontSize = 15.sp
        ),
        singleLine = true,
        cursorBrush = SolidColor(KidooPrimary),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
          focusManager.clearFocus()
        }),
        decorationBox = { innerTextField ->
          if (query.isEmpty()) {
            Text(
              text = placeholderText,
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
              fontSize = 14.sp
            )
          }
          innerTextField()
        }
      )

      if (query.isNotEmpty()) {
        IconButton(
          onClick = {
            onClearQuery()
            focusManager.clearFocus()
          },
          modifier = Modifier
            .size(36.dp)
            .testTag("product_search_clear_button")
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Clear search query",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }
  }
}
