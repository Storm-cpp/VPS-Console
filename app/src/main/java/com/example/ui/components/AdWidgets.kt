package com.example.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderColor
import com.example.ui.theme.GoogleBlue
import com.example.ui.theme.GoogleGreen
import com.example.ui.theme.GoogleYellow

data class CloudPromo(
    val title: String,
    val description: String,
    val badge: String,
    val badgeColor: Color,
    val actionText: String,
    val redirectUrl: String
)

@Composable
fun GooglePromoBanner(
    modifier: Modifier = Modifier,
    onActionClicked: (CloudPromo) -> Unit = {}
) {
    val promos = remember {
        listOf(
            CloudPromo(
                title = "Get \$300 Free Cloud Credits",
                description = "Register as a professional administrator today and receive testing credits for any scalable high-vCPU nodes.",
                badge = "DEAL",
                badgeColor = GoogleBlue,
                actionText = "Claim Credits",
                redirectUrl = "https://google.com"
            ),
            CloudPromo(
                title = "Scale with Enterprise NVMe Store",
                description = "Upgrade your standard VPS orders to hyper-fast Gen4 NVMe arrays with a 15% recurring discount.",
                badge = "UPGRADE",
                badgeColor = GoogleGreen,
                actionText = "View Storage plans",
                redirectUrl = "https://example.com"
            ),
            CloudPromo(
                title = "Deploy Global Load Balancing",
                description = "Distribute connection traffic between Frankfurt, Oregon and Tokyo droplets using Google Anycast IP routing.",
                badge = "NEW FEATURE",
                badgeColor = GoogleYellow,
                actionText = "Configure Router",
                redirectUrl = "https://example.com"
            )
        )
    }

    var currentIndex by remember { mutableStateOf(0) }
    val promo = promos[currentIndex]

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surfaceVariant,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
            .padding(16.dp)
            .animateContentSize()
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    color = promo.badgeColor.copy(alpha = 0.15f),
                    contentColor = promo.badgeColor,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = promo.badge,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Sponsored",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Ad",
                        tint = GoogleBlue,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = promo.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = promo.description,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { onActionClicked(promo) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoogleBlue,
                        contentColor = MaterialTheme.colorScheme.background
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("promo_action_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(promo.actionText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                TextButton(
                    onClick = {
                        currentIndex = (currentIndex + 1) % promos.size
                    },
                    modifier = Modifier.testTag("next_promo")
                ) {
                    Text("Next Offert →", fontSize = 12.sp, color = GoogleBlue)
                }
            }
        }
    }
}

@Composable
fun InlineWebAdBlock(
    modifier: Modifier = Modifier
) {
    var dismissed by remember { mutableStateOf(false) }
    if (dismissed) return

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ad_block_card"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(GoogleYellow.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Ad Icon",
                    tint = GoogleYellow,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Ad",
                        color = GoogleYellow,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .border(1.dp, GoogleYellow, RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                    Text(
                        text = "Intel Xeon 3rd Gen Nodes",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "VPS droplets starting at $3.50/mo. Absolute speed.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            IconButton(
                onClick = { dismissed = true },
                modifier = Modifier
                    .size(28.dp)
                    .testTag("dismiss_ad")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
