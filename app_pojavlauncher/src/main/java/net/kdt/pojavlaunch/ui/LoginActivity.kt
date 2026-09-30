package net.kdt.pojavlaunch.ui

import android.content.Intent
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import net.kdt.pojavlaunch.utils.LocaleUtils

class LoginActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleUtils.setLocale(newBase))
    }

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        setContent {

            MaterialTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent
                ) {

                    LoginHomeScreen(

                        onMicrosoftLogin = {

                            startActivity(
                                Intent(
                                    this,
                                    MicrosoftLoginActivity::class.java
                                )
                            )
                        },

                        onOfflineLogin = {

                            startActivity(
                                Intent(
                                    this,
                                    OfflineLoginActivity::class.java
                                )
                            )
                        },

                        onSocial = { urlResource ->

                            openSocialLink(
                                urlResource
                            )
                        }
                    )
                }
            }
        }
    }

    private fun openSocialLink(
        urlResource: Int
    ) {

        try {

            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(
                        getString(
                            urlResource
                        )
                    )
                )
            )

        } catch (
            e: Exception
        ) {

            Log.e(
                "LOGIN_UI",
                "Não foi possível abrir link",
                e
            )
        }
    }
}
