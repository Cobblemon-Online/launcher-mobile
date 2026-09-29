package net.kdt.pojavlaunch

import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import net.kdt.pojavlaunch.views.CenterCropVideoView
import org.json.JSONObject
import pl.droidsonroids.gif.GifDrawable
import pl.droidsonroids.gif.GifImageView
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.InetSocketAddress
import java.net.Socket
import java.net.URL
import kotlin.math.roundToInt

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
    loadingProgress: Float,
    accountRefreshKey: Int,
    serverHost: String,
    serverPort: Int = 25565,
    onPlay: () -> Unit,
    onSettings: () -> Unit,
    onSocial: (Int) -> Unit
){

    val username =
        rememberCurrentUsername(
            refreshKey = accountRefreshKey
        )

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        HomeBackgroundVideo()

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
            Box(
                modifier =
                    Modifier.width(
                        100.dp
                    ),
                contentAlignment =
                    Alignment.Center
            ) {

                Image(
                    painter =
                        painterResource(
                            R.drawable.ic_launcher_logo
                        ),
                    contentDescription =
                        "Cobblemon Online",
                    modifier =
                        Modifier.fillMaxWidth(),
                    contentScale =
                        ContentScale.FillWidth
                )
            }

            /*
             * =====================================
             * SERVIDOR + CONFIGURAÇÕES
             * =====================================
             */
            /*
             * =====================================
             * CONFIGURAÇÕES — ESQUERDA
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
             * SERVIDOR — DIREITA
             * =====================================
             */
            ServerPlayersCard(
                host = serverHost,
                port = serverPort,
                modifier = Modifier
                    .align(
                        Alignment.CenterEnd
                    )
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
                loadingProgress = loadingProgress,
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
    loadingProgress: Float,
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

            LoadingBottomBar(
                progress = loadingProgress
            )

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
private fun LoadingBottomBar(
    progress: Float
) {

    val safeProgress =
        progress.coerceIn(
            0f,
            1f
        )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {

        /*
         * GIF com 2x a altura da barra.
         *
         * Barra = 45dp
         * GIF   = 90dp
         */
        val gifSize =
            maxHeight * 2f

        /*
         * Espaço que o GIF pode percorrer
         * sem ultrapassar a lateral direita.
         */
        val movementWidth =
            maxWidth - gifSize

        /*
         * =====================================
         * FUNDO
         * =====================================
         */
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color(0x66000000)
                )
        )

        /*
         * =====================================
         * PARTE PREENCHIDA
         * =====================================
         *
         * Termina onde o GIF começa.
         */
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(
                    movementWidth *
                            safeProgress
                )
                .background(
                    Color(0xFF5B2E9E)
                )
        )

        /*
         * =====================================
         * GIF
         * =====================================
         */
        AndroidView(
            factory = { context ->

                GifImageView(context).apply {

                    scaleType =
                        ImageView.ScaleType.FIT_CENTER

                    setImageResource(
                        R.drawable.loading_gif
                    )

                    (drawable as? GifDrawable)
                        ?.setFilterBitmap(false)
                }
            },

            modifier = Modifier
                .requiredSize(
                    gifSize
                )
                .align(
                    Alignment.CenterStart
                )
                .offset(
                    y = (-15).dp
                )
                .offset {

                    val gifSizePx =
                        constraints.maxHeight * 2

                    val movementWidthPx =
                        constraints.maxWidth -
                                gifSizePx

                    IntOffset(
                        x =
                            (
                                    movementWidthPx *
                                            safeProgress
                                    )
                                .roundToInt(),
                        y = 0
                    )
                }
        )
    }
}


/*
 * ============================================================
 * STATUS DO SERVIDOR
 * ============================================================
 *
 * Consulta diretamente o Server List Ping do Minecraft.
 * Não depende de API pública/terceiros.
 */
private data class MinecraftServerStatus(
    val onlinePlayers: Int,
    val maxPlayers: Int
)

private data class MinecraftServerStatusUiState(
    val loading: Boolean = true,
    val status: MinecraftServerStatus? = null
)

@Composable
private fun ServerPlayersCard(
    host: String,
    port: Int,
    modifier: Modifier = Modifier
) {

    val state =
        rememberMinecraftServerStatus(
            host = host,
            port = port
        )

    val valueText =
        when {
            state.loading ->
                "..."

            state.status == null ->
                "X"

            else ->
                state.status
                    .onlinePlayers
                    .toString()
        }

    val labelText =
        when {
            state.loading ->
                "VERIFICANDO"

            state.status == null ->
                "SERVIDOR OFFLINE"

            else ->
                "JOGADORES ONLINE"
        }

    val valueColor =
        when {
            state.loading ->
                Color(
                    0xFFB5A8BE
                )

            state.status == null ->
                Color(
                    0xFFE57373
                )

            else ->
                Color(
                    0xFF40FF00
                )
        }

    Box(
        modifier = modifier
            .width(
                260.dp
            )
            .height(
                56.dp
            )
            .background(
                Color(
                    0xD9342364
                ),
                RoundedCornerShape(
                    6.dp
                )
            )

            .padding(
                horizontal = 14.dp
            ),
        contentAlignment =
            Alignment.Center
    ) {

        Row(
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Text(
                text = labelText,
                color =
                    Color.White,
                fontFamily =
                    HomeMinecraftFont,
                fontSize =
                    9.sp,
                letterSpacing =
                    3.5.sp,
                maxLines =
                    1,
                overflow =
                    TextOverflow.Ellipsis
            )

            Spacer(
                modifier =
                    Modifier.width(
                        10.dp
                    )
            )

            Text(
                text = valueText,
                color = valueColor,
                fontFamily =
                    HomeMinecraftBoldFont,
                fontSize =
                    8.sp,
                maxLines =
                    1
            )
        }
    }
}

@Composable
private fun rememberMinecraftServerStatus(
    host: String,
    port: Int
): MinecraftServerStatusUiState {

    var state by
    remember(
        host,
        port
    ) {
        mutableStateOf(
            MinecraftServerStatusUiState()
        )
    }

    LaunchedEffect(
        host,
        port
    ) {

        while (true) {

            val status =
                withContext(
                    Dispatchers.IO
                ) {
                    queryMinecraftServerStatus(
                        host = host,
                        port = port
                    )
                }

            state =
                MinecraftServerStatusUiState(
                    loading = false,
                    status = status
                )

            /*
             * Mesmo intervalo usado pelo launcher desktop:
             * 5 minutos.
             */
            delay(
                5 * 60 * 1000L
            )
        }
    }

    return state
}

private fun queryMinecraftServerStatus(
    host: String,
    port: Int
): MinecraftServerStatus? {

    if (
        host.isBlank()
        || port !in 1..65535
    ) {
        return null
    }

    val socket =
        Socket()

    return try {

        socket.connect(
            InetSocketAddress(
                host,
                port
            ),
            4000
        )

        socket.soTimeout =
            4000

        val input =
            DataInputStream(
                BufferedInputStream(
                    socket.getInputStream()
                )
            )

        val output =
            DataOutputStream(
                BufferedOutputStream(
                    socket.getOutputStream()
                )
            )

        /*
         * Handshake para o estado STATUS.
         *
         * Protocol 47 é mantido por compatibilidade com a
         * consulta usada pelo launcher desktop anterior.
         */
        val handshakeBuffer =
            ByteArrayOutputStream()

        val handshake =
            DataOutputStream(
                handshakeBuffer
            )

        writeVarInt(
            handshake,
            47
        )

        writeMinecraftString(
            handshake,
            host
        )

        handshake.writeShort(
            port
        )

        writeVarInt(
            handshake,
            1
        )

        handshake.flush()

        writeMinecraftPacket(
            output = output,
            packetId = 0x00,
            payload =
                handshakeBuffer
                    .toByteArray()
        )

        /*
         * Status Request.
         */
        writeMinecraftPacket(
            output = output,
            packetId = 0x00,
            payload =
                byteArrayOf()
        )

        output.flush()

        /*
         * Status Response:
         * packet length -> packet id -> JSON length -> JSON.
         */
        val packetLength =
            readVarInt(
                input
            )

        if (
            packetLength <= 0
            || packetLength > 1_048_576
        ) {
            return null
        }

        val packetId =
            readVarInt(
                input
            )

        if (
            packetId != 0x00
        ) {
            return null
        }

        val jsonLength =
            readVarInt(
                input
            )

        if (
            jsonLength <= 0
            || jsonLength > 1_048_576
        ) {
            return null
        }

        val jsonBytes =
            ByteArray(
                jsonLength
            )

        input.readFully(
            jsonBytes
        )

        val response =
            JSONObject(
                String(
                    jsonBytes,
                    Charsets.UTF_8
                )
            )

        val players =
            response.getJSONObject(
                "players"
            )

        MinecraftServerStatus(
            onlinePlayers =
                players.getInt(
                    "online"
                ),
            maxPlayers =
                players.getInt(
                    "max"
                )
        )

    } catch (
        exception: Exception
    ) {

        null

    } finally {

        try {
            socket.close()
        } catch (
            exception: Exception
        ) {
            // Nada a fazer.
        }
    }
}

private fun writeMinecraftPacket(
    output: DataOutputStream,
    packetId: Int,
    payload: ByteArray
) {

    val packetBuffer =
        ByteArrayOutputStream()

    val packet =
        DataOutputStream(
            packetBuffer
        )

    writeVarInt(
        packet,
        packetId
    )

    packet.write(
        payload
    )

    packet.flush()

    val packetBytes =
        packetBuffer
            .toByteArray()

    writeVarInt(
        output,
        packetBytes.size
    )

    output.write(
        packetBytes
    )
}

private fun writeMinecraftString(
    output: DataOutputStream,
    value: String
) {

    val bytes =
        value.toByteArray(
            Charsets.UTF_8
        )

    writeVarInt(
        output,
        bytes.size
    )

    output.write(
        bytes
    )
}

private fun writeVarInt(
    output: DataOutputStream,
    value: Int
) {

    var current =
        value

    while (true) {

        if (
            (current and 0xFFFFFF80.toInt()) == 0
        ) {

            output.writeByte(
                current
            )

            return
        }

        output.writeByte(
            (current and 0x7F) or 0x80
        )

        current =
            current ushr 7
    }
}

private fun readVarInt(
    input: DataInputStream
): Int {

    var result =
        0

    var numRead =
        0

    while (true) {

        val read =
            input.readUnsignedByte()

        val value =
            read and 0x7F

        result =
            result or
                    (
                            value shl
                                    (7 * numRead)
                            )

        numRead++

        if (
            numRead > 5
        ) {
            throw IllegalStateException(
                "VarInt inválido recebido do servidor"
            )
        }

        if (
            (read and 0x80) == 0
        ) {
            return result
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
@Composable
private fun HomeBackgroundVideo() {

    val context = LocalContext.current

    AndroidView(
        factory = {

            CenterCropVideoView(context).apply {

                setVideoURI(
                    Uri.parse(
                        "android.resource://${context.packageName}/${R.raw.launcher_background}"
                    )
                )

                setOnPreparedListener { player ->

                    player.isLooping = true
                    player.setVolume(0f, 0f)

                    setVideoSize(
                        player.videoWidth,
                        player.videoHeight
                    )

                    start()
                }
            }
        },

        modifier = Modifier
            .fillMaxSize()
    )
}
