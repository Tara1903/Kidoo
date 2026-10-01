package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.model.Product
import com.example.data.model.ProductReview
import com.example.ui.theme.KidooCardBorder
import com.example.ui.theme.KidooPrimary
import com.example.ui.theme.KidooPrimaryFixed
import com.example.ui.theme.KidooSecondaryContainer
import com.example.ui.theme.KidooSurfaceContainerLowest

/**
 * Modal dialog displaying product star ratings, customer review list,
 * and an interactive review submission form.
 */
@Composable
fun ProductReviewsDialog(
  product: Product,
  onDismiss: () -> Unit,
  onSubmitReview: (rating: Int, author: String, comment: String) -> Unit,
  modifier: Modifier = Modifier
) {
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    ProductReviewsContent(
      product = product,
      onDismiss = onDismiss,
      onSubmitReview = onSubmitReview,
      modifier = modifier
    )
  }
}

/**
 * Content container for ProductReviewsDialog. Can be tested directly or embedded in a bottom sheet/dialog.
 */
@Composable
fun ProductReviewsContent(
  product: Product,
  onDismiss: () -> Unit,
  onSubmitReview: (rating: Int, author: String, comment: String) -> Unit,
  modifier: Modifier = Modifier
) {
  var isWritingReview by remember { mutableStateOf(false) }
  var userRating by remember { mutableIntStateOf(5) }
  var authorName by remember { mutableStateOf("") }
  var reviewComment by remember { mutableStateOf("") }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  Surface(
    modifier = modifier
      .fillMaxWidth(0.94f)
      .fillMaxHeight(0.88f)
      .clip(RoundedCornerShape(24.dp))
      .testTag("product_reviews_dialog"),
    color = MaterialTheme.colorScheme.background,
    shadowElevation = 8.dp
  ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        // Dialog Top Bar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(KidooSurfaceContainerLowest)
            .padding(horizontal = 16.dp, vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            AsyncImage(
              model = product.imageUrl,
              contentDescription = product.title,
              modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(10.dp)),
              contentScale = ContentScale.Crop
            )

            Column {
              Text(
                text = product.title,
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp
                ),
                maxLines = 1,
                color = MaterialTheme.colorScheme.onSurface
              )
              DisplayStarRating(
                rating = product.averageRating,
                reviewCount = product.totalReviewsCount,
                starSize = 13.dp
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("close_reviews_dialog_button")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close reviews",
              tint = MaterialTheme.colorScheme.onSurface
            )
          }
        }

        HorizontalDivider(color = KidooCardBorder)

        LazyColumn(
          modifier = Modifier
            .weight(1f)
            .padding(horizontal = 16.dp),
          verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          item {
            Spacer(modifier = Modifier.height(8.dp))
          }

          // Interactive Review Submission Form (shown at top when toggled for instant accessibility)
          if (isWritingReview) {
            item {
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("write_review_form_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = KidooSurfaceContainerLowest),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, KidooPrimary)
              ) {
                Column(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                  verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                  Text(
                    text = "Leave Your Rating & Review",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                  )

                  // Star Rating Picker
                  Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                      text = "Select Star Rating: $userRating of 5",
                      style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    InteractiveStarRating(
                      selectedRating = userRating,
                      onRatingChanged = { userRating = it }
                    )
                  }

                  // Reviewer Name Input
                  OutlinedTextField(
                    value = authorName,
                    onValueChange = { authorName = it },
                    label = { Text("Your Name") },
                    singleLine = true,
                    modifier = Modifier
                      .fillMaxWidth()
                      .testTag("review_author_input"),
                    shape = RoundedCornerShape(12.dp)
                  )

                  // Review Comment Input
                  OutlinedTextField(
                    value = reviewComment,
                    onValueChange = { reviewComment = it },
                    label = { Text("Your Review / Feedback") },
                    placeholder = { Text("What did you like about this item?") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier
                      .fillMaxWidth()
                      .testTag("review_comment_input"),
                    shape = RoundedCornerShape(12.dp)
                  )

                  if (errorMessage != null) {
                    Text(
                      text = errorMessage ?: "",
                      style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.error
                    )
                  }

                  // Submit Button
                  Button(
                    onClick = {
                      if (authorName.isBlank()) {
                        errorMessage = "Please enter your name."
                        return@Button
                      }
                      if (reviewComment.isBlank()) {
                        errorMessage = "Please write a brief comment."
                        return@Button
                      }
                      errorMessage = null
                      onSubmitReview(userRating, authorName.trim(), reviewComment.trim())
                      isWritingReview = false
                      authorName = ""
                      reviewComment = ""
                    },
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(48.dp)
                      .testTag("submit_product_review_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = KidooPrimary),
                    shape = RoundedCornerShape(12.dp)
                  ) {
                    Text(
                      text = "Submit Review",
                      style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                  }
                }
              }
            }
          }

          // Star Rating Breakdown Section
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = KidooSurfaceContainerLowest),
              border = androidx.compose.foundation.BorderStroke(1.dp, KidooCardBorder)
            ) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Column {
                    Text(
                      text = String.format("%.1f", product.averageRating),
                      style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 36.sp
                      ),
                      color = MaterialTheme.colorScheme.onSurface
                    )
                    DisplayStarRating(
                      rating = product.averageRating,
                      starSize = 16.dp,
                      showNumericScore = false
                    )
                    Text(
                      text = "Based on ${product.totalReviewsCount} ratings",
                      style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }

                  // Write Review Trigger Button
                  Button(
                    onClick = { isWritingReview = !isWritingReview },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                      containerColor = if (isWritingReview) KidooPrimaryFixed else KidooPrimary,
                      contentColor = if (isWritingReview) KidooPrimary else Color.White
                    ),
                    modifier = Modifier.testTag("write_review_toggle_button")
                  ) {
                    Icon(
                      imageVector = Icons.Default.RateReview,
                      contentDescription = null,
                      modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = if (isWritingReview) "Cancel" else "Rate & Review",
                      style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                  }
                }

                HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.5f))

                // 5-Star to 1-Star Progress Bars
                val reviews = product.reviews
                val total = if (reviews.isNotEmpty()) reviews.size else 1
                for (star in 5 downTo 1) {
                  val count = reviews.count { it.rating == star }
                  val fraction = if (reviews.isNotEmpty()) count.toFloat() / total else if (star >= 4) 0.65f else 0.15f

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    Text(
                      text = "$star ★",
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                      color = MaterialTheme.colorScheme.onSurfaceVariant,
                      modifier = Modifier.width(26.dp)
                    )
                    LinearProgressIndicator(
                      progress = { fraction },
                      modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                      color = StarGoldColor,
                      trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Text(
                      text = if (reviews.isNotEmpty()) "$count" else "${(fraction * 100).toInt()}%",
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant,
                      modifier = Modifier.width(36.dp)
                    )
                  }
                }
              }
            }
          }

          // Verified Customer Reviews Title
          item {
            Text(
              text = "Customer Reviews (${product.reviews.size})",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
          }

          if (product.reviews.isEmpty()) {
            item {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "No reviews yet. Be the first to rate this product!",
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          } else {
            items(product.reviews) { review ->
              ReviewItemCard(review = review)
            }
          }

          item {
            Spacer(modifier = Modifier.height(16.dp))
          }
        }
      }
    }
  }

@Composable
private fun ReviewItemCard(
  review: ProductReview,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = KidooSurfaceContainerLowest),
    border = androidx.compose.foundation.BorderStroke(1.dp, KidooCardBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(CircleShape)
              .background(KidooPrimaryFixed),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = review.author.take(1).uppercase(),
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = KidooPrimary
            )
          }

          Text(
            text = review.author,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )

          if (review.isVerifiedPurchase) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(2.dp),
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFE8F5E9))
                .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Verified Purchase",
                tint = Color(0xFF2E7D32),
                modifier = Modifier.size(12.dp)
              )
              Text(
                text = "Verified",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 10.sp,
                  fontWeight = FontWeight.SemiBold
                ),
                color = Color(0xFF2E7D32)
              )
            }
          }
        }

        Text(
          text = review.date,
          style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      DisplayStarRating(
        rating = review.rating.toDouble(),
        showNumericScore = false,
        starSize = 14.dp
      )

      Text(
        text = review.comment,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface
      )
    }
  }
}
