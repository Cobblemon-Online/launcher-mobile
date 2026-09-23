package net.kdt.pojavlaunch.ui

import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import net.kdt.pojavlaunch.R


/*
 * ============================================================
 * CORES
 * ============================================================
 */

private val LoginPurple =
    Color(0xFF7806EA)

private val LoginPurpleDark =
    Color(0xFF26094F)

private val LoginPurpleBorder =
    Color(0xFF5B168A)

private val LoginBackgroundTop =
    Color(0xFF07131F)

private val LoginBackgroundBottom =
    Color(0xFF010610)

private val LoginMutedText =
    Color(0xFF99919F)


/*
 * ============================================================
 * FONTES
 * ============================================================
 */

private val LoginMinecraftFont =
    FontFamily(
        Font(R.font.minecraft_standard)
    )

private val LoginMinecraftBoldFont =
    FontFamily(
        Font(R.font.minecraft_standard_bold)
    )


/*
 * ============================================================
 * HOME LOGIN
 * ============================================================
 */

@Composable
fun LoginHomeScreen(
    onMicrosoftLogin: () -> Unit,
    onOfflineLogin: () -> Unit,
    onSocial: (Int) -> Unit
) {

    Box(
        modifier =
            Modifier.fillMaxSize()
    ) {

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(
                    R.drawable.ic_login_option_background
                ),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Color.Black.copy(alpha = 0.5f)
                    )
            )
        }

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        Color(
                            0x22000000
                        )
                    )
        )

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(
                        top = 28.dp,
                        bottom = 28.dp
                    ),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            LoginLogo()

            Spacer(
                modifier =
                    Modifier.height(
                        35.dp
                    )
            )

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(
                            horizontal = 40.dp
                        ),
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Box(
                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text =
                            "BEM-VINDO",
                        color =
                            Color.White,
                        fontFamily =
                            LoginMinecraftBoldFont,
                        fontSize =
                            26.sp,
                        letterSpacing =
                            0.08.em,
                        style =
                            TextStyle(
                                drawStyle =
                                    Stroke(
                                        width = 3f
                                    )
                            )
                    )

                    Text(
                        text =
                            "BEM-VINDO",
                        color =
                            Color.White,
                        fontFamily =
                            LoginMinecraftBoldFont,
                        fontSize =
                            26.sp,
                        letterSpacing =
                            0.08.em
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(
                            27.dp
                        )
                )

                WelcomeDescription()

                Spacer(
                    modifier =
                        Modifier.height(
                            35.dp
                        )
                )

                PurpleDivider()

                Spacer(
                    modifier =
                        Modifier.height(
                            35.dp
                        )
                )

                LoginButton(
                    text =
                        "Entrar com Microsoft",
                    background =
                        LoginPurple,
                    textColor =
                        Color.White,
                    onClick =
                        onMicrosoftLogin
                )

                Spacer(
                    modifier =
                        Modifier.height(
                            17.dp
                        )
                )

                LoginButton(
                    text =
                        "Entrar Offline",
                    background =
                        LoginPurpleDark,
                    textColor =
                        Color(
                            0xFFB5A8BE
                        ),
                    onClick =
                        onOfflineLogin
                )

                Spacer(
                    modifier =
                        Modifier.weight(
                            1f
                        )
                )

                LoginSocialBar(
                    onSocial =
                        onSocial
                )
            }
        }
    }
}


/*
 * ============================================================
 * LOGO
 * ============================================================
 */

@Composable
private fun LoginLogo() {

    Box(
        modifier =
            Modifier.width(
                150.dp
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
}


/*
 * ============================================================
 * TEXTO DE BOAS-VINDAS
 * ============================================================
 */

@Composable
private fun WelcomeDescription() {

    Column(
        modifier =
            Modifier.fillMaxWidth(),
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        LoginDescriptionLine(
            parts =
                listOf(
                    DescriptionPart(
                        "SUA JORNADA ",
                        Color.White
                    ),

                    DescriptionPart(
                        "COBBLEMON",
                        LoginPurple
                    )
                )
        )

        Spacer(
            modifier =
                Modifier.height(
                    7.dp
                )
        )

        LoginDescriptionLine(
            parts =
                listOf(
                    DescriptionPart(
                        "ONLINE",
                        LoginPurple
                    ),

                    DescriptionPart(
                        " COMEÇA AQUI!",
                        Color.White
                    )
                )
        )

        Spacer(
            modifier =
                Modifier.height(
                    7.dp
                )
        )

        LoginDescriptionLine(
            parts =
                listOf(
                    DescriptionPart(
                        "PREPARE-SE PARA UMA",
                        Color.White
                    )
                )
        )

        Spacer(
            modifier =
                Modifier.height(
                    7.dp
                )
        )

        LoginDescriptionLine(
            parts =
                listOf(
                    DescriptionPart(
                        "AVENTURA ÚNICA",
                        LoginPurple
                    ),

                    DescriptionPart(
                        " NO MUNDO",
                        Color.White
                    )
                )
        )

        Spacer(
            modifier =
                Modifier.height(
                    7.dp
                )
        )

        LoginDescriptionLine(
            parts =
                listOf(
                    DescriptionPart(
                        "DO COBBLEMON! AQUI.",
                        Color.White
                    )
                )
        )
    }
}


private data class DescriptionPart(
    val text: String,
    val color: Color
)


@Composable
private fun LoginDescriptionLine(
    parts: List<DescriptionPart>
) {

    Row(
        horizontalArrangement =
            Arrangement.Center,
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        parts.forEach {
                part ->

            Text(
                text =
                    part.text,
                color =
                    part.color,
                fontFamily =
                    LoginMinecraftFont,
                fontSize =
                    12.sp,
                letterSpacing =
                    0.20.em,
                maxLines =
                    1
            )
        }
    }
}


/*
 * ============================================================
 * DIVISOR
 * ============================================================
 */

@Composable
private fun PurpleDivider() {

    Row(
        verticalAlignment =
            Alignment.CenterVertically,
        horizontalArrangement =
            Arrangement.Center
    ) {

        Box(
            modifier =
                Modifier
                    .width(
                        47.dp
                    )
                    .height(
                        2.dp
                    )
                    .background(
                        LoginPurple
                    )
        )

        Spacer(
            modifier =
                Modifier.width(
                    8.dp
                )
        )

        Text(
            text =
                "✦",
            color =
                LoginPurple,
            fontSize =
                8.sp
        )

        Spacer(
            modifier =
                Modifier.width(
                    5.dp
                )
        )

        Text(
            text =
                "✦",
            color =
                LoginPurple,
            fontSize =
                8.sp
        )

        Spacer(
            modifier =
                Modifier.width(
                    5.dp
                )
        )

        Text(
            text =
                "✦",
            color =
                LoginPurple,
            fontSize =
                8.sp
        )

        Spacer(
            modifier =
                Modifier.width(
                    8.dp
                )
        )

        Box(
            modifier =
                Modifier
                    .width(
                        47.dp
                    )
                    .height(
                        2.dp
                    )
                    .background(
                        LoginPurple
                    )
        )
    }
}


/*
 * ============================================================
 * BOTÃO LOGIN
 * ============================================================
 */

@Composable
private fun LoginButton(
    text: String,
    background: Color,
    textColor: Color,
    onClick: () -> Unit
) {

    val interactionSource =
        remember {
            MutableInteractionSource()
        }

    val pressed by
    interactionSource
        .collectIsPressedAsState()

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(
                    42.dp
                )
                .alpha(
                    if (
                        pressed
                    ) {

                        0.75f

                    } else {

                        1f
                    }
                )
                .background(
                    background
                )
                .clickable(
                    interactionSource =
                        interactionSource,
                    indication =
                        null,
                    onClick =
                        onClick
                ),
        contentAlignment =
            Alignment.Center
    ) {

        Text(
            text =
                text,
            color =
                textColor,
            fontFamily =
                LoginMinecraftFont,
            fontSize =
                13.sp
        )
    }
}


/*
 * ============================================================
 * REDES SOCIAIS
 * ============================================================
 */

@Composable
private fun LoginSocialBar(
    onSocial: (Int) -> Unit
) {

    Row(
        verticalAlignment =
            Alignment.CenterVertically,
        horizontalArrangement =
            Arrangement.spacedBy(
                19.dp
            )
    ) {

        LoginSocialButton(
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

        LoginSocialButton(
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

        LoginSocialButton(
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

        LoginSocialButton(
            icon =
                R.drawable.ic_twitter_grey_1,
            description =
                "X",
            onClick = {

                onSocial(
                    R.string.social_x_url
                )
            }
        )

        LoginSocialButton(
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
private fun LoginSocialButton(
    icon: Int,
    description: String,
    onClick: () -> Unit
) {

    Image(
        painter =
            painterResource(
                icon
            ),
        contentDescription =
            description,
        modifier =
            Modifier
                .size(
                    30.dp
                )
                .clickable(
                    onClick =
                        onClick
                ),
        contentScale =
            ContentScale.Fit
    )
}


/*
 * ============================================================
 * MICROSOFT LOGIN
 * ============================================================
 */

@Composable
fun MicrosoftLoginWebView(
    onLoginUrlCaptured: (String) -> Unit,
    onCancel: () -> Unit
) {

    var webViewRef by
    remember {
        mutableStateOf<WebView?>(
            null
        )
    }

    DisposableEffect(Unit) {

        onDispose {

            webViewRef?.let {
                    webView ->

                try {

                    webView.stopLoading()

                    webView.webViewClient =
                        WebViewClient()

                    webView.destroy()

                } catch (
                    _: Throwable
                ) {

                }
            }

            webViewRef =
                null
        }
    }

    AndroidView(
        modifier =
            Modifier.fillMaxSize(),

        factory = {
                context ->

            WebView(
                context
            ).apply {

                webViewRef =
                    this

                val settings:
                        WebSettings =
                    this.settings

                settings.javaScriptEnabled =
                    true

                settings.domStorageEnabled =
                    true

                webViewClient =
                    object :
                        WebViewClient() {

                        override fun shouldOverrideUrlLoading(
                            view: WebView,
                            url: String
                        ): Boolean {

                            return handleUrl(
                                url
                            )
                        }

                        override fun shouldOverrideUrlLoading(
                            view: WebView,
                            request: WebResourceRequest
                        ): Boolean {

                            return handleUrl(
                                request
                                    .url
                                    .toString()
                            )
                        }

                        private fun handleUrl(
                            url: String
                        ): Boolean {

                            if (
                                url.startsWith(
                                    "ms-xal-00000000402b5328"
                                )
                            ) {

                                onLoginUrlCaptured(
                                    url
                                )

                                return true
                            }

                            if (
                                url.contains(
                                    "res=cancel"
                                )
                            ) {

                                onCancel()

                                return true
                            }

                            return false
                        }
                    }

                CookieManager
                    .getInstance()
                    .removeAllCookies {

                        clearHistory()

                        clearCache(
                            true
                        )

                        clearFormData()

                        loadUrl(
                            "https://login.live.com/oauth20_authorize.srf" +
                                    "?client_id=00000000402b5328" +
                                    "&response_type=code" +
                                    "&scope=service%3A%3Auser.auth.xboxlive.com%3A%3AMBI_SSL" +
                                    "&redirect_url=https%3A%2F%2Flogin.live.com%2Foauth20_desktop.srf"
                        )
                    }
            }
        }
    )
}


/*
 * ============================================================
 * LOGIN OFFLINE
 * ============================================================
 */

@Composable
fun OfflineLoginScreen(
    onLogin: (String) -> Unit,
    onCancel: () -> Unit
) {

    var username by
    remember {
        mutableStateOf("")
    }

    val validUsername =
        remember {
            Regex(
                "^[a-zA-Z0-9_]{3,16}$"
            )
        }

    val isValid =
        validUsername.matches(
            username
        )

    Box(
        modifier =
            Modifier.fillMaxSize()
    ) {

        /*
         * FUNDO IGUAL AO DA TELA DE OPÇÕES
         */
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(
                    R.drawable.ic_login_option_background
                ),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Color.Black.copy(alpha = 0.5f)
                    )
            )
        }

        /*
         * OVERLAY IGUAL AO DA TELA DE OPÇÕES
         */
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        Color(0x22000000)
                    )
        )

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = 12.dp,
                        vertical = 28.dp
                    ),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            /*
             * LOGO IGUAL À HOME DE LOGIN
             */
            LoginLogo()

            Spacer(
                modifier =
                    Modifier.height(
                        28.dp
                    )
            )

            /*
             * CARD CENTRAL
             */
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(
                            color =
                                Color(0xCC1A1A1A),
                            shape =
                                RoundedCornerShape(
                                    10.dp
                                )
                        )
                        .border(
                            width = 1.dp,
                            color = Color(0x33FFFFFF),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(
                            horizontal = 18.dp,
                            vertical = 18.dp
                        )
            ) {

                Column(
                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    /*
                     * TOPO DO CARD
                     */
                    Box(
                        modifier =
                            Modifier.fillMaxWidth()
                    ) {

                        Text(
                            text = "←",
                            color = Color.White,
                            fontFamily =
                                LoginMinecraftBoldFont,
                            fontSize = 18.sp,
                            modifier =
                                Modifier
                                    .align(
                                        Alignment.CenterStart
                                    )
                                    .clickable(
                                        onClick = onCancel
                                    )
                        )

                        Text(
                            text = "LOGIN",
                            color = Color.White,
                            fontFamily =
                                LoginMinecraftBoldFont,
                            fontSize = 20.sp,
                            letterSpacing = 0.08.em,
                            modifier =
                                Modifier.align(
                                    Alignment.Center
                                )
                        )
                    }

                    Spacer(
                        modifier =
                            Modifier.height(
                                22.dp
                            )
                    )

                    Text(
                        text = "Nome de usuário",
                        color = Color.White,
                        fontFamily =
                            LoginMinecraftFont,
                        fontSize = 11.sp
                    )

                    Spacer(
                        modifier =
                            Modifier.height(
                                8.dp
                            )
                    )

                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(
                                    42.dp
                                )
                                .background(
                                    Color(0x22000000)
                                )
                                .border(
                                    width = 1.dp,
                                    color =
                                        if (
                                            username.isNotEmpty()
                                            && !isValid
                                        ) {
                                            Color(0xFFB94747)
                                        } else {
                                            Color.White
                                        }
                                )
                                .padding(
                                    horizontal = 10.dp
                                ),
                        contentAlignment =
                            Alignment.CenterStart
                    ) {

                        if (
                            username.isEmpty()
                        ) {

                            Text(
                                text =
                                    "Nome de usuário",
                                color =
                                    Color(0xFF9D9D9D),
                                fontFamily =
                                    LoginMinecraftFont,
                                fontSize =
                                    10.sp
                            )
                        }

                        BasicTextField(
                            value =
                                username,
                            onValueChange = {

                                if (
                                    it.length <= 16
                                ) {
                                    username = it
                                }
                            },
                            modifier =
                                Modifier.fillMaxWidth(),
                            singleLine = true,
                            textStyle =
                                TextStyle(
                                    color = Color.White,
                                    fontFamily =
                                        LoginMinecraftFont,
                                    fontSize = 10.sp
                                ),
                            cursorBrush =
                                SolidColor(
                                    LoginPurple
                                )
                        )
                    }

                    if (
                        username.isNotEmpty()
                        && !isValid
                    ) {

                        Spacer(
                            modifier =
                                Modifier.height(
                                    8.dp
                                )
                        )

                        Text(
                            text =
                                "Use de 3 a 16 caracteres: letras, números ou _",
                            color =
                                Color(0xFFFF6D6D),
                            fontFamily =
                                LoginMinecraftFont,
                            fontSize =
                                8.sp,
                            modifier =
                                Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(
                        modifier =
                            Modifier.height(
                                18.dp
                            )
                    )

                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(
                                    42.dp
                                )
                                .background(
                                    LoginPurple
                                )
                                .clickable(
                                    enabled = isValid,
                                    onClick = {
                                        onLogin(
                                            username.trim()
                                        )
                                    }
                                ),
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(
                            text = "Entrar",
                            color = Color.White,
                            fontFamily =
                                LoginMinecraftFont,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(
                modifier =
                    Modifier.weight(
                        1f
                    )
            )

            /*
             * REDES EMBAIXO, IGUAL À TELA DE OPÇÕES
             */
            LoginSocialBar(
                onSocial = { }
            )
        }
    }
}

/*
 * ============================================================
 * BOTÃO OFFLINE
 * ============================================================
 */

@Composable
private fun OfflineActionButton(
    text: String,
    enabled: Boolean,
    primary: Boolean,
    onClick: () -> Unit
) {

    val interactionSource =
        remember {
            MutableInteractionSource()
        }

    val pressed by
    interactionSource
        .collectIsPressedAsState()

    val background =
        when {

            !enabled -> {

                Color(
                    0xFF22152F
                )
            }

            primary -> {

                LoginPurple
            }

            else -> {

                LoginPurpleDark
            }
        }

    val textColor =
        when {

            !enabled -> {

                Color(
                    0xFF665A6C
                )
            }

            primary -> {

                Color.White
            }

            else -> {

                Color(
                    0xFFB5A8BE
                )
            }
        }

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(
                    43.dp
                )
                .alpha(
                    if (
                        pressed
                        && enabled
                    ) {

                        0.75f

                    } else {

                        1f
                    }
                )
                .background(
                    background
                )
                .clickable(
                    enabled =
                        enabled,
                    interactionSource =
                        interactionSource,
                    indication =
                        null,
                    onClick =
                        onClick
                ),
        contentAlignment =
            Alignment.Center
    ) {

        Text(
            text =
                text,
            color =
                textColor,
            fontFamily =
                LoginMinecraftFont,
            fontSize =
                11.sp
        )
    }
}