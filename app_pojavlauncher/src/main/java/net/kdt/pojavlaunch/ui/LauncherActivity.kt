package net.kdt.pojavlaunch.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import net.kdt.pojavlaunch.Tools
import net.kdt.pojavlaunch.authenticator.listener.DoneListener
import net.kdt.pojavlaunch.authenticator.listener.ErrorListener
import net.kdt.pojavlaunch.authenticator.listener.ProgressListener
import net.kdt.pojavlaunch.authenticator.microsoft.PresentedException
import net.kdt.pojavlaunch.extra.ExtraConstants
import net.kdt.pojavlaunch.extra.ExtraCore
import net.kdt.pojavlaunch.extra.ExtraListener
import net.kdt.pojavlaunch.value.MinecraftAccount
import net.kdt.pojavlaunch.R
import net.kdt.pojavlaunch.authenticator.microsoft.MicrosoftBackgroundLogin
import androidx.compose.material3.OutlinedTextField
import java.io.File
import net.kdt.pojavlaunch.PojavProfile
import android.content.Intent
import net.kdt.pojavlaunch.LauncherActivity as PojavLauncherActivity

class LauncherActivity : ComponentActivity() {

    private enum class Screen {
        HOME,
        MICROSOFT_LOGIN,
        OFFLINE_LOGIN
    }

    private fun openPojavLauncher() {
        Log.e(
            "ACTIVITY_FLOW",
            "VOU ABRIR: ${PojavLauncherActivity::class.java.name}"
        )

        val intent = Intent(
            this,
            PojavLauncherActivity::class.java
        )

        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var screen by remember { mutableStateOf(Screen.HOME) }
            DisposableEffect(Unit) {
                val listener = ExtraListener<Uri> { _, uri ->
                    Log.d("MS_LOGIN", "MICROSOFT_LOGIN_TODO received: $uri")

                    val code = uri.getQueryParameter("code")
                    if (code.isNullOrBlank()) {
                        runOnUiThread {
                            Toast.makeText(this@LauncherActivity, "OAuth code vazio", Toast.LENGTH_SHORT).show()
                        }
                        return@ExtraListener false
                    }

                    val progressListener = ProgressListener { step ->
                        Log.d("MS_LOGIN", "progress step=$step")
                    }

                    val doneListener = DoneListener { account: MinecraftAccount ->
                        runOnUiThread {
                            Log.d(
                                "MS_LOGIN",
                                "Login OK: ${account.username}"
                            )

                            PojavProfile.setCurrentProfile(
                                this@LauncherActivity,
                                account.username
                            )

                            Toast.makeText(
                                this@LauncherActivity,
                                "Login realizado como ${account.username}",
                                Toast.LENGTH_SHORT
                            ).show()

                            openPojavLauncher()
                        }
                    }

                    val errorListener = ErrorListener { err ->
                        runOnUiThread {
                            if (err is PresentedException) {
                                val cause = err.cause
                                if (cause == null) {
                                    Tools.dialog(
                                        this@LauncherActivity,
                                        getString(R.string.global_error),
                                        err.toString(this@LauncherActivity)
                                    )
                                } else {
                                    Tools.showError(
                                        this@LauncherActivity,
                                        err.toString(this@LauncherActivity),
                                        cause
                                    )
                                }
                            } else {
                                Tools.showError(this@LauncherActivity, err)
                                println("ERRO AKI: "+err.toString())
                            }
                        }
                    }

                    // Igual ao mcAccountSpinner:
                    MicrosoftBackgroundLogin(false, code)
                        .performLogin(progressListener, doneListener, errorListener)

                    false
                }

                ExtraCore.addExtraListener(ExtraConstants.MICROSOFT_LOGIN_TODO, listener)

                onDispose {
                    ExtraCore.removeExtraListenerFromValue(ExtraConstants.MICROSOFT_LOGIN_TODO, listener)
                }
            }
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    when (screen) {
                        Screen.OFFLINE_LOGIN -> {
                            OfflineLoginScreen(
                                onLogin = { username ->
                                    try {
                                        val account = createOfflineAccount(username)

                                        Toast.makeText(
                                            this@LauncherActivity,
                                            "Conta ${account.username} criada",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        openPojavLauncher()

                                    } catch (e: IllegalArgumentException) {
                                        Toast.makeText(
                                            this@LauncherActivity,
                                            e.message ?: "Nick inválido",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    } catch (e: Throwable) {
                                        Log.e(
                                            "OFFLINE_LOGIN",
                                            "Erro criando conta offline",
                                            e
                                        )

                                        Toast.makeText(
                                            this@LauncherActivity,
                                            "Erro ao criar conta offline",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                onCancel = {
                                    screen = Screen.HOME
                                }
                            )
                        }
                        Screen.HOME -> {
                            Column(Modifier.fillMaxSize()) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Hello World (UI nova)")
                                }
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Button(
                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = {
                                            screen = Screen.MICROSOFT_LOGIN
                                        }
                                    ) {
                                        Text("Entrar com Microsoft")
                                    }

                                    Spacer(Modifier.height(8.dp))

                                    Button(
                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = {
                                            screen = Screen.OFFLINE_LOGIN
                                        }
                                    ) {
                                        Text("Entrar offline")
                                    }
                                }
                            }
                        }

                        Screen.MICROSOFT_LOGIN -> {
                            MicrosoftLoginWebView(
                                onLoginUrlCaptured = { url ->
                                    ExtraCore.setValue(
                                        ExtraConstants.MICROSOFT_LOGIN_TODO,
                                        Uri.parse(url)
                                    )

                                    Toast.makeText(
                                        this@LauncherActivity,
                                        "Login iniciado...",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                onCancel = {
                                    screen = Screen.HOME
                                }
                            )
                        }
                    }
                }
            }
        }
    }
    private fun createOfflineAccount(username: String): MinecraftAccount {
        val nick = username.trim()

        val validUsername = Regex("^[a-zA-Z0-9_]{3,16}$")

        require(validUsername.matches(nick)) {
            "O nick deve ter entre 3 e 16 caracteres e usar apenas letras, números e _"
        }

        val accountDirectory = File(Tools.DIR_ACCOUNT_NEW)

        if (!accountDirectory.exists() && !accountDirectory.mkdirs()) {
            throw IllegalStateException("Não foi possível criar o diretório de contas")
        }

        val accountFile = File(accountDirectory, "$nick.json")

        require(!accountFile.exists()) {
            "Já existe uma conta com esse nick"
        }

        val account = MinecraftAccount().apply {
            this.username = nick

            accessToken = "0"
            clientToken = "0"

            profileId = "00000000-0000-0000-0000-000000000000"

            isMicrosoft = false
            msaRefreshToken = "0"

            xuid = null
            expiresAt = 0
        }

        account.save()

        PojavProfile.setCurrentProfile(
            this,
            account.username
        )

        return account
    }
}

@Composable
private fun MicrosoftLoginWebView(
    onLoginUrlCaptured: (String) -> Unit,
    onCancel: () -> Unit
) {
    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            webViewRef?.let { wv ->
                try {
                    wv.stopLoading()
                    wv.webViewClient = WebViewClient()
                    wv.destroy()
                } catch (_: Throwable) {}
            }
            webViewRef = null
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            WebView(ctx).apply {
                webViewRef = this

                val settings: WebSettings = this.settings
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true

                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                        return handleUrl(url)
                    }

                    override fun shouldOverrideUrlLoading(
                        view: WebView,
                        request: WebResourceRequest
                    ): Boolean {
                        return handleUrl(request.url.toString())
                    }

                    private fun handleUrl(url: String): Boolean {
                        if (url.startsWith("ms-xal-00000000402b5328")) {
                            onLoginUrlCaptured(url)
                            return true
                        }
                        if (url.contains("res=cancel")) {
                            onCancel()
                            return true
                        }
                        return false
                    }
                }

                CookieManager.getInstance().removeAllCookies { _ ->
                    clearHistory()
                    clearCache(true)
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
@Composable
private fun OfflineLoginScreen(
    onLogin: (String) -> Unit,
    onCancel: () -> Unit
) {
    var username by remember {
        mutableStateOf("")
    }

    val validUsername = remember {
        Regex("^[a-zA-Z0-9_]{3,16}$")
    }

    val isValid = validUsername.matches(username)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Login offline",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Escolha o nick que será usado no Minecraft."
        )

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = username,
            onValueChange = {
                if (it.length <= 16) {
                    username = it
                }
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Nick")
            },
            singleLine = true,
            isError = username.isNotEmpty() && !isValid,
            supportingText = {
                if (username.isNotEmpty() && !isValid) {
                    Text(
                        "Use de 3 a 16 caracteres: letras, números ou _"
                    )
                }
            }
        )

        Spacer(Modifier.height(16.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = isValid,
            onClick = {
                onLogin(username)
            }
        ) {
            Text("Entrar")
        }

        Spacer(Modifier.height(8.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onCancel
        ) {
            Text("Cancelar")
        }
    }
}