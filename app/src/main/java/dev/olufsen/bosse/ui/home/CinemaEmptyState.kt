package dev.olufsen.bosse.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.HighQuality
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.MovieFilter
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.olufsen.bosse.R

private val Bg = Color(0xFF131313)
private val OnSurface = Color(0xFFE5E2E1)
private val OnSurfaceVariant = Color(0xFFD0C6AB)
private val PrimaryFixedDim = Color(0xFF00DAF3)
private val PrimaryContainer = Color(0xFFFFD478)
private val SurfaceContainerHigh = Color(0xFF2A2A2A)
private val OutlineVariant = Color(0xFF4D4732)
private val OnPrimary = Color(0xFF3F2E00)
private val GoldStart = Color(0xFFFABD00)
private val GoldEnd = Color(0xFFFFD478)

private const val TheaterImageUrl =
    "https://lh3.googleusercontent.com/aida-public/AB6AXuBL-GlBCAyMyYYEp7blqGdryNH8W-5VLZZj7SMS6nXgbFzHnnBtVOrPPSTbCWTjkPE5tVVkoX8MdLMG7AcUkcWPDKDrfEU_JHr3lDQToFgSFk5ZBUcSOiD-P8sVEYPbAdCK6-nxg9hmOh9D27_mc63g11Iy0pBuO1GJd7Woyx_HPJD-et--VRExCXBcvXmofLfZciiu4Gw6WZ3dP9q08rZvy-01jBY949v9mbKBb2tm6B0_Hon4QxMhqgVaEsGPorFCc8c9EkuIJuQ"

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun CinemaEmptyState(
    scanning: Boolean,
    onChooseLibrary: () -> Unit,
    onAddTmdbKey: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Bg),
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(TheaterImageUrl)
                    .crossfade(400)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .scale(1.1f),
                alpha = 0.4f,
            )
            BoxWithConstraints(Modifier.fillMaxSize()) {
                val density = LocalDensity.current
                val wPx = with(density) { maxWidth.toPx() }
                val hPx = with(density) { maxHeight.toPx() }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color.Transparent, Bg),
                                center = Offset(wPx * 0.5f, hPx * 0.35f),
                                radius = maxOf(wPx, hPx) * 0.85f,
                            ),
                        ),
                )
            }
        }

        Text(
            text = stringResource(R.string.empty_state_watermark),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(48.dp)
                .offset(x = 8.dp, y = 16.dp),
            color = PrimaryFixedDim.copy(alpha = 0.2f),
            fontSize = 72.sp,
            fontWeight = FontWeight.Black,
            maxLines = 1,
        )

        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = maxHeight)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
            if (scanning) {
                Text(
                    text = stringResource(R.string.updating_library),
                    color = PrimaryContainer,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 16.dp),
                )
            }

            Box(
                modifier = Modifier.padding(bottom = 48.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(PrimaryContainer.copy(alpha = 0.12f), Color.Transparent),
                            ),
                            shape = CircleShape,
                        ),
                )
                Box(
                    modifier = Modifier
                        .size(128.dp)
                        .clip(CircleShape)
                        .background(SurfaceContainerHigh.copy(alpha = 0.4f))
                        .border(1.dp, Color(0xFFFFF5E7).copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.MovieFilter,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = PrimaryFixedDim,
                    )
                }
            }

            Text(
                text = stringResource(R.string.empty_state_title),
                color = OnSurface,
                fontSize = 44.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                lineHeight = 48.sp,
                modifier = Modifier.padding(bottom = 16.dp),
            )
            Text(
                text = stringResource(R.string.empty_state_subtitle),
                color = OnSurfaceVariant.copy(alpha = 0.8f),
                fontSize = 20.sp,
                lineHeight = 28.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 40.dp),
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                val goldBrush = Brush.linearGradient(
                    colors = listOf(GoldStart, GoldEnd),
                    start = Offset(0f, 0f),
                    end = Offset(800f, 800f),
                )
                Button(
                    onClick = onChooseLibrary,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = ButtonDefaults.shape(RoundedCornerShape(12.dp)),
                    colors = ButtonDefaults.colors(
                        containerColor = Color.Transparent,
                        contentColor = OnPrimary,
                    ),
                ) {
                    Box(
                        modifier = Modifier
                            .background(goldBrush, RoundedCornerShape(12.dp))
                            .padding(horizontal = 24.dp, vertical = 14.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Icon(
                                Icons.Outlined.FolderOpen,
                                contentDescription = null,
                                modifier = Modifier.size(26.dp),
                                tint = OnPrimary,
                            )
                            Text(
                                stringResource(R.string.pick_library_folder),
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                            )
                        }
                    }
                }
                Button(
                    onClick = onAddTmdbKey,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .border(1.dp, OutlineVariant.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                    shape = ButtonDefaults.shape(RoundedCornerShape(12.dp)),
                    colors = ButtonDefaults.colors(
                        containerColor = SurfaceContainerHigh,
                        contentColor = OnSurface,
                    ),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Icon(
                            Icons.Outlined.Key,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = OnSurfaceVariant,
                        )
                        Text(
                            stringResource(R.string.empty_state_tmdb_cta),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(56.dp))

            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val cols = if (maxWidth > 900.dp) 3 else 1
                Column(
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    if (cols == 3) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(24.dp),
                        ) {
                            FeatureBlock(
                                icon = { Icon(Icons.Outlined.AutoAwesome, null, tint = Color(0xFFFFF5E7), modifier = Modifier.size(22.dp)) },
                                title = stringResource(R.string.empty_state_feature_meta_title),
                                body = stringResource(R.string.empty_state_feature_meta_body),
                                modifier = Modifier.weight(1f),
                            )
                            FeatureBlock(
                                icon = { Icon(Icons.Outlined.HighQuality, null, tint = Color(0xFFFFF5E7), modifier = Modifier.size(22.dp)) },
                                title = stringResource(R.string.empty_state_feature_4k_title),
                                body = stringResource(R.string.empty_state_feature_4k_body),
                                modifier = Modifier.weight(1f),
                            )
                            FeatureBlock(
                                icon = { Icon(Icons.Outlined.Devices, null, tint = Color(0xFFFFF5E7), modifier = Modifier.size(22.dp)) },
                                title = stringResource(R.string.empty_state_feature_devices_title),
                                body = stringResource(R.string.empty_state_feature_devices_body),
                                modifier = Modifier.weight(1f),
                            )
                        }
                    } else {
                        FeatureBlock(
                            icon = { Icon(Icons.Outlined.AutoAwesome, null, tint = Color(0xFFFFF5E7), modifier = Modifier.size(22.dp)) },
                            title = stringResource(R.string.empty_state_feature_meta_title),
                            body = stringResource(R.string.empty_state_feature_meta_body),
                        )
                        FeatureBlock(
                            icon = { Icon(Icons.Outlined.HighQuality, null, tint = Color(0xFFFFF5E7), modifier = Modifier.size(22.dp)) },
                            title = stringResource(R.string.empty_state_feature_4k_title),
                            body = stringResource(R.string.empty_state_feature_4k_body),
                        )
                        FeatureBlock(
                            icon = { Icon(Icons.Outlined.Devices, null, tint = Color(0xFFFFF5E7), modifier = Modifier.size(22.dp)) },
                            title = stringResource(R.string.empty_state_feature_devices_title),
                            body = stringResource(R.string.empty_state_feature_devices_body),
                        )
                    }
                }
            }
            }
        }
    }
}

@Composable
private fun FeatureBlock(
    icon: @Composable () -> Unit,
    title: String,
    body: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(12.dp),
    ) {
        icon()
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = title,
            color = OnSurface.copy(alpha = 0.4f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = body,
            color = OnSurface.copy(alpha = 0.4f),
            fontSize = 13.sp,
            lineHeight = 18.sp,
        )
    }
}
