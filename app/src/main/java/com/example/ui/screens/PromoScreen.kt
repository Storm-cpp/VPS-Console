package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GooglePromoBanner
import com.example.ui.components.InlineWebAdBlock
import com.example.ui.theme.*

@Composable
fun PromoScreen(
    modifier: Modifier = Modifier
) {
    var couponCode by remember { mutableStateOf("") }
    var couponApplied by remember { mutableStateOf<Boolean?>(null) } // null = idle, true = success, false = invalid
    var selectedTab by remember { mutableStateOf(0) } // 0 = Offers, 1 = Affiliate Program

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
    ) {
        item {
            // Screen Header Tab
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                divider = {}
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Promotions & Offers", fontWeight = FontWeight.SemiBold) },
                    selectedContentColor = GoogleBlue,
                    unselectedContentColor = TextSecondary
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Affiliate Engine", fontWeight = FontWeight.SemiBold) },
                    selectedContentColor = GoogleBlue,
                    unselectedContentColor = TextSecondary
                )
            }
        }

        if (selectedTab == 0) {
            item {
                Text(
                    text = "Monetization Campaigns & Dynamic Adds",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            item {
                GooglePromoBanner()
            }

            item {
                InlineWebAdBlock()
            }

            item {
                // Voucher entry block
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                        .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        text = "Redeem Promotional Voucher",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Enter voucher key or sponsor tags to claim high-capacity nodes discounts.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = couponCode,
                            onValueChange = { couponCode = it },
                            placeholder = { Text("e.g. CLOUD_START_50") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    couponApplied = couponCode.uppercase() == "CLOUD_START_50"
                                }
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("voucher_input")
                        )

                        Button(
                            onClick = {
                                couponApplied = couponCode.uppercase() == "CLOUD_START_50"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GoogleBlue),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .height(44.dp)
                                .testTag("redeem_btn")
                        ) {
                            Text("Apply", color = MaterialTheme.colorScheme.background)
                        }
                    }

                    couponApplied?.let { applied ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (applied) GoogleGreen.copy(alpha = 0.12f) else GoogleRed.copy(alpha = 0.12f),
                            contentColor = if (applied) GoogleGreen else GoogleRed
                        ) {
                            Text(
                                text = if (applied) "✔ Voucher CLOUD_START_50 applied! 50% discount registered for next 6 checkout cycles." else "✗ Invalid or expired coupon code. Try 'CLOUD_START_50'.",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            item {
                // Partner Promotion Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Brush.linearGradient(colors = listOf(GoogleDarkSurface, GoogleDarkCard)))
                        .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(imageVector = Icons.Default.Star, contentDescription = "", tint = GoogleYellow, modifier = Modifier.size(16.dp))
                            Text("Partner Program Perks", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GoogleYellow)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Get co-marketing assets or place dynamic ad blocks directly on your cloud VPS hosts starting tonight. Elevate revenue ratios up to 24%.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        } else {
            // Affiliate program
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                        .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(GoogleBlue.copy(alpha = 0.13f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Notifications, contentDescription = "", tint = GoogleBlue, modifier = Modifier.size(28.dp))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Make Money Promoting VPS Services", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(
                        "Configure and share your unique referral link. You receive 35% commission on any VPS order placed.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = "https://vps-console.io/ref/promo_partner_99",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Your Unique Affiliate URI") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(GoogleDarkBg, RoundedCornerShape(8.dp))
                                .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Clicks", fontSize = 11.sp, color = TextSecondary)
                            Text("1,492", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GoogleBlue)
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(GoogleDarkBg, RoundedCornerShape(8.dp))
                                .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Active Referrals", fontSize = 11.sp, color = TextSecondary)
                            Text("38 Nodes", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GoogleGreen)
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(GoogleDarkBg, RoundedCornerShape(8.dp))
                                .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Paid/unpaid commission
                            Text("Earnings", fontSize = 11.sp, color = TextSecondary)
                            Text("\$148.50", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GoogleYellow)
                        }
                    }
                }
            }
        }
    }
}
