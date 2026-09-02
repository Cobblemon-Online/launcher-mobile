package net.kdt.pojavlaunch

import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.ImageView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.droidsonroids.gif.GifDrawable
import pl.droidsonroids.gif.GifImageView
import java.net.HttpURLConnection
import java.net.URL

private val HomeMinecraftFont =
    FontFamily(
        Font(R.font.minecraft_standard)
    )

private val HomeMinecraftBoldFont =
    FontFamily(
        Font(R.font.minecraft_standard_bold)
    )

@Composable
fun LauncherHomeScreen(
    loading: Boolean,
    accountRefreshKey: Int,
    onPlay: () -> Unit,
    onSettings: () -> Unit,
    onSocial: (Int) -> Unit
) {

    val username =
        rememberCurrentUsername(
            refreshKey = accountRefreshKey
        )

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        /*
         * Overlay que existia sobre o vídeo:
         *
         * #44000000
         */
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color(0x44000000)
                )
        )

        /*
         * Equivalente ao antigo content_container:
         *
         * horizontal = 29dp
         * vertical   = 10dp
         */
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 29.dp,
                    vertical = 10.dp
                )
        ) {

            /*
             * =====================================
             * CONFIGURAÇÕES
             * =====================================
             */
            SettingsHomeButton(
                modifier = Modifier
                    .align(
                        Alignment.CenterStart
                    ),
                onClick = onSettings
            )

            /*
             * =====================================
             * JOGAR
             * =====================================
             */
            PlayHomeButton(
                modifier = Modifier
                    .align(
                        Alignment.Center
                    )
                    .offset(
                        y = 35.dp
                    ),
                enabled = !loading,
                onClick = onPlay
            )

            /*
             * =====================================
             * USUÁRIO
             * =====================================
             */
            if (username != null) {

                PlayerHeader(
                    username = username,
                    modifier = Modifier
                        .align(
                            Alignment.TopEnd
                        )
                )
            }

            /*
             * =====================================
             * BARRA INFERIOR
             * =====================================
             */
            HomeBottomBar(
                modifier = Modifier
                    .align(
                        Alignment.BottomCenter
                    ),
                loading = loading,
                onSocial = onSocial
            )
        }
    }
}

@Composable
private fun SettingsHomeButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    val interactionSource =
        remember {
            MutableInteractionSource()
        }

    val pressed by
    interactionSource.collectIsPressedAsState()

    Box(
        modifier = modifier
            .width(189.dp)
            .height(51.dp)
            .clickable(
                interactionSource =
                    interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment =
            Alignment.Center
    ) {

        /*
         * Background original
         */
        AndroidDrawable(
            drawableRes = R.drawable.bg_settings_button,
            modifier = Modifier.fillMaxSize()
        )

        /*
         * Conteúdo
         */
        Row(
            verticalAlignment =
                Alignment.CenterVertically,
            horizontalArrangement =
                Arrangement.Center
        ) {

            Image(
                painter =
                    painterResource(
                        R.drawable.ic_settings_1
                    ),
                contentDescription =
                    "Configurações",
                modifier =
                    Modifier.size(18.dp),
                contentScale =
                    ContentScale.Fit
            )

            Spacer(
                modifier =
                    Modifier.width(8.dp)
            )

            Text(
                text = "CONFIGURAÇÕES",
                color = Color.White,
                fontFamily =
                    HomeMinecraftFont,
                fontSize = 10.sp
            )
        }

        /*
         * Foreground do XML:
         *
         * android:foreground="@drawable/play_button_pressed"
         */
        if (pressed) {
            AndroidDrawable(
                drawableRes = R.drawable.play_button_pressed,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun PlayHomeButton(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    onClick: () -> Unit
) {

    val interactionSource =
        remember {
            MutableInteractionSource()
        }

    val pressed by
    interactionSource.collectIsPressedAsState()

    Box(
        modifier = modifier
            .width(193.dp)
            .height(59.dp)
            .clickable(
                enabled = enabled,
                interactionSource =
                    interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment =
            Alignment.Center
    ) {

        /*
         * Background
         */
        AndroidDrawable(
            drawableRes = R.drawable.bg_play_button,
            modifier = Modifier.fillMaxSize()
        )

        /*
         * =====================================
         * POKÉBOLAS
         * =====================================
         *
         * O GIF continua GifImageView porque
         * painterResource não anima GifDrawable.
         */
        Box(
            modifier = Modifier
                .width(174.dp)
                .height(41.dp)
                .offset(y = (-1).dp)
                .clipToBounds(),
            contentAlignment = Alignment.Center
        ) {

            AndroidView(
                factory = { context ->

                    GifImageView(context).apply {

                        scaleType =
                            ImageView.ScaleType.FIT_CENTER

                        setImageResource(
                            R.drawable.pokeballs
                        )

                        (drawable as? GifDrawable)
                            ?.setFilterBitmap(false)
                    }
                },
                modifier = Modifier
                    .requiredSize(193.dp)
                    .offset(y = 20.dp)
                    .alpha(0.6f)
            )
        }

        /*
         * =====================================
         * BRILHO
         * =====================================
         */
        AndroidView(
            factory = { context ->

                GifImageView(context).apply {

                    scaleType =
                        ImageView.ScaleType.FIT_XY

                    setImageResource(
                        R.drawable.brilho
                    )
                }
            },
            modifier =
                Modifier.fillMaxSize()
        )

        /*
         * =====================================
         * TEXTO
         * =====================================
         */
        Text(
            text = "JOGAR",
            color = Color.White,
            fontFamily =
                HomeMinecraftBoldFont,
            fontSize = 17.sp,
            modifier =
                Modifier.offset(
                    y = (-1).dp
                )
        )

        /*
         * Foreground pressionado
         */
        if (pressed) {
            AndroidDrawable(
                drawableRes = R.drawable.play_button_pressed,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun PlayerHeader(
    username: String,
    modifier: Modifier = Modifier
) {

    val avatar =
        rememberMinecraftAvatar(
            username = username
        )

    Row(
        modifier = modifier
            .height(32.dp),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Text(
            text =
                username.uppercase(),
            color =
                Color.White,
            fontFamily =
                HomeMinecraftBoldFont,
            fontSize =
                9.sp,
            letterSpacing =
                0.57.em,
            maxLines =
                1,
            overflow =
                TextOverflow.Ellipsis
        )

        Spacer(
            modifier =
                Modifier.width(8.dp)
        )

        if (avatar != null) {

            Image(
                bitmap = avatar,
                contentDescription =
                    "Skin do jogador",
                modifier =
                    Modifier.size(32.dp),
                contentScale =
                    ContentScale.Fit,
                filterQuality =
                    FilterQuality.None
            )

        } else {

            Spacer(
                modifier =
                    Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun HomeBottomBar(
    modifier: Modifier = Modifier,
    loading: Boolean,
    onSocial: (Int) -> Unit
) {

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(45.dp)
            .background(
                Color(0xBF342364),
                RectangleShape
            ),
        contentAlignment =
            Alignment.Center
    ) {

        if (loading) {

            LoadingBottomBar()

        } else {

            SocialBar(
                onSocial = onSocial
            )
        }
    }
}

@Composable
private fun SocialBar(
    onSocial: (Int) -> Unit
) {

    Row(
        verticalAlignment =
            Alignment.CenterVertically,
        horizontalArrangement =
            Arrangement.spacedBy(
                8.dp
            )
    ) {

        SocialButton(
            icon =
                R.drawable.ic_instagram_grey_1,
            description =
                "Instagram",
            onClick = {
                onSocial(
                    R.string.social_instagram_url
                )
            }
        )

        SocialButton(
            icon =
                R.drawable.ic_discord_grey_1,
            description =
                "Discord",
            onClick = {
                onSocial(
                    R.string.social_discord_url
                )
            }
        )

        SocialButton(
            icon =
                R.drawable.ic_tiktok_grey_1,
            description =
                "TikTok",
            onClick = {
                onSocial(
                    R.string.social_tiktok_url
                )
            }
        )

        SocialButton(
            icon =
                R.drawable.ic_twitter_grey_1,
            description =
                "Twitter",
            onClick = {
                onSocial(
                    R.string.social_x_url
                )
            }
        )

        SocialButton(
            icon =
                R.drawable.ic_youtube_grey_1,
            description =
                "YouTube",
            onClick = {
                onSocial(
                    R.string.social_youtube_url
                )
            }
        )
    }
}

@Composable
private fun SocialButton(
    icon: Int,
    description: String,
    onClick: () -> Unit
) {

    val resources = LocalContext.current.resources
    val iconBitmap = remember(icon) {
        requireNotNull(BitmapFactory.decodeResource(resources, icon)) {
            "Não foi possível decodificar o ícone social: $description"
        }.asImageBitmap()
    }

    Image(
        bitmap = iconBitmap,
        contentDescription =
            description,
        modifier = Modifier
            .size(23.dp)
            .clickable(
                onClick = onClick
            ),
        contentScale =
            ContentScale.Fit
    )
}

@Composable
private fun LoadingBottomBar() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = 12.dp
            ),
        contentAlignment =
            Alignment.Center
    ) {

        androidx.compose.foundation.layout.Column(
            modifier =
                Modifier.fillMaxWidth(),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text = "Carregando...",
                color = Color.White,
                fontFamily =
                    HomeMinecraftFont,
                fontSize = 7.sp
            )

            Spacer(
                modifier =
                    Modifier.height(2.dp)
            )

            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )
        }
    }
}

/*
 * ============================================================
 * CONTA ATUAL
 * ============================================================
 *
 * Importante:
 * leitura ocorre em Dispatchers.IO, não na UI thread.
 */
@Composable
private fun rememberCurrentUsername(
    refreshKey: Int
): String? {

    val context =
        LocalContext.current.applicationContext

    var username by
    remember {
        mutableStateOf<String?>(
            null
        )
    }

    LaunchedEffect(
        refreshKey
    ) {

        username =
            withContext(
                Dispatchers.IO
            ) {

                PojavProfile
                    .getCurrentProfileContent(
                        context,
                        null
                    )
                    ?.username
            }
    }

    return username
}

/*
 * ============================================================
 * AVATAR
 * ============================================================
 */
@Composable
private fun rememberMinecraftAvatar(
    username: String
): ImageBitmap? {

    var avatar by
    remember(username) {
        mutableStateOf<ImageBitmap?>(
            null
        )
    }

    LaunchedEffect(
        username
    ) {

        avatar =
            withContext(
                Dispatchers.IO
            ) {

                loadMinecraftAvatarBitmap(
                    username
                )
                    ?.asImageBitmap()
            }
    }

    return avatar
}

private fun loadMinecraftAvatarBitmap(
    username: String
): android.graphics.Bitmap? {

    var connection:
            HttpURLConnection? = null

    return try {

        val avatarUri =
            Uri.Builder()
                .scheme("https")
                .authority(
                    "mc-heads.net"
                )
                .appendPath("head")
                .appendPath(username)
                .appendPath("left")
                .build()

        connection =
            URL(
                avatarUri.toString()
            )
                .openConnection()
                    as HttpURLConnection

        connection.connectTimeout =
            5000

        connection.readTimeout =
            5000

        connection.requestMethod =
            "GET"

        if (
            connection.responseCode
            != HttpURLConnection.HTTP_OK
        ) {
            null

        } else {

            connection
                .inputStream
                .use {
                    BitmapFactory
                        .decodeStream(it)
                }
        }

    } catch (
        exception: Exception
    ) {

        null

    } finally {

        connection?.disconnect()
    }
}
@Composable
private fun AndroidDrawable(
    drawableRes: Int,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { context ->
            ImageView(context).apply {
                scaleType = ImageView.ScaleType.FIT_XY
                setImageResource(drawableRes)
            }
        },
        update = { imageView ->
            imageView.setImageResource(drawableRes)
        },
        modifier = modifier
    )
}
