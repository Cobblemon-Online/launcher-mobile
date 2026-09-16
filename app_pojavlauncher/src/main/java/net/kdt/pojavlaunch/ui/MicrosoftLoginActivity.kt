package net.kdt.pojavlaunch.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.DisposableEffect
import net.kdt.pojavlaunch.LauncherActivity as PojavLauncherActivity
import net.kdt.pojavlaunch.PojavProfile
import net.kdt.pojavlaunch.R
import net.kdt.pojavlaunch.Tools
import net.kdt.pojavlaunch.authenticator.listener.DoneListener
import net.kdt.pojavlaunch.authenticator.listener.ErrorListener
import net.kdt.pojavlaunch.authenticator.listener.ProgressListener
import net.kdt.pojavlaunch.authenticator.microsoft.MicrosoftBackgroundLogin
import net.kdt.pojavlaunch.authenticator.microsoft.PresentedException
import net.kdt.pojavlaunch.extra.ExtraConstants
import net.kdt.pojavlaunch.extra.ExtraCore
import net.kdt.pojavlaunch.extra.ExtraListener
import net.kdt.pojavlaunch.value.MinecraftAccount

class MicrosoftLoginActivity : ComponentActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContent {

            DisposableEffect(Unit) {

                val listener =
                    ExtraListener<Uri> { _, uri ->

                        val code =
                            uri.getQueryParameter(
                                "code"
                            )

                        if (
                            code.isNullOrBlank()
                        ) {

                            runOnUiThread {

                                Toast.makeText(
                                    this@MicrosoftLoginActivity,
                                    "OAuth code vazio",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            return@ExtraListener false
                        }


                        val progressListener =
                            ProgressListener { step ->

                                Log.d(
                                    "MS_LOGIN",
                                    "progress step=$step"
                                )
                            }


                        val doneListener =
                            DoneListener {
                                    account: MinecraftAccount ->

                                runOnUiThread {

                                    PojavProfile.setCurrentProfile(
                                        this@MicrosoftLoginActivity,
                                        account.username
                                    )

                                    Toast.makeText(
                                        this@MicrosoftLoginActivity,
                                        "Login realizado como ${account.username}",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    openPojavLauncher()
                                }
                            }


                        val errorListener =
                            ErrorListener { err ->

                                runOnUiThread {

                                    if (
                                        err is PresentedException
                                    ) {

                                        val cause =
                                            err.cause

                                        if (
                                            cause == null
                                        ) {

                                            Tools.dialog(
                                                this@MicrosoftLoginActivity,
                                                getString(
                                                    R.string.global_error
                                                ),
                                                err.toString(
                                                    this@MicrosoftLoginActivity
                                                )
                                            )

                                        } else {

                                            Tools.showError(
                                                this@MicrosoftLoginActivity,
                                                err.toString(
                                                    this@MicrosoftLoginActivity
                                                ),
                                                cause
                                            )
                                        }

                                    } else {

                                        Tools.showError(
                                            this@MicrosoftLoginActivity,
                                            err
                                        )
                                    }
                                }
                            }


                        MicrosoftBackgroundLogin(
                            false,
                            code
                        ).performLogin(
                            progressListener,
                            doneListener,
                            errorListener
                        )

                        false
                    }


                ExtraCore.addExtraListener(
                    ExtraConstants.MICROSOFT_LOGIN_TODO,
                    listener
                )


                onDispose {

                    ExtraCore.removeExtraListenerFromValue(
                        ExtraConstants.MICROSOFT_LOGIN_TODO,
                        listener
                    )
                }
            }


            MicrosoftLoginWebView(

                onLoginUrlCaptured = { url ->

                    ExtraCore.setValue(
                        ExtraConstants.MICROSOFT_LOGIN_TODO,
                        Uri.parse(url)
                    )
                },

                onCancel = {

                    finish()
                }
            )
        }
    }


    private fun openPojavLauncher() {

        startActivity(
            Intent(
                this,
                PojavLauncherActivity::class.java
            )
        )

        finishAffinity()
    }
}