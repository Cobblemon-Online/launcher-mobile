package net.kdt.pojavlaunch.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import net.kdt.pojavlaunch.LauncherActivity as PojavLauncherActivity
import net.kdt.pojavlaunch.PojavProfile
import net.kdt.pojavlaunch.Tools
import net.kdt.pojavlaunch.value.MinecraftAccount
import java.io.File

class OfflineLoginActivity : ComponentActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContent {

            MaterialTheme {

                OfflineLoginScreen(

                    onLogin = { username ->

                        try {

                            val account =
                                createOfflineAccount(
                                    username
                                )

                            Toast.makeText(
                                this,
                                "Conta ${account.username} criada",
                                Toast.LENGTH_SHORT
                            ).show()

                            openPojavLauncher()

                        } catch (
                            e: IllegalArgumentException
                        ) {

                            Toast.makeText(
                                this,
                                e.message ?: "Nick inválido",
                                Toast.LENGTH_SHORT
                            ).show()

                        } catch (
                            e: Throwable
                        ) {

                            Log.e(
                                "OFFLINE_LOGIN",
                                "Erro criando conta offline",
                                e
                            )

                            Toast.makeText(
                                this,
                                "Erro ao criar conta offline",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },

                    onCancel = {

                        finish()
                    }
                )
            }
        }
    }


    private fun openPojavLauncher() {

        val intent =
            Intent(
                this,
                PojavLauncherActivity::class.java
            )

        startActivity(intent)

        finishAffinity()
    }


    private fun createOfflineAccount(
        username: String
    ): MinecraftAccount {

        val nick =
            username.trim()

        val validUsername =
            Regex(
                "^[a-zA-Z0-9_]{3,16}$"
            )

        require(
            validUsername.matches(
                nick
            )
        ) {
            "O nick deve ter entre 3 e 16 caracteres e usar apenas letras, números e _"
        }

        val accountDirectory =
            File(
                Tools.DIR_ACCOUNT_NEW
            )

        if (
            !accountDirectory.exists()
            && !accountDirectory.mkdirs()
        ) {
            throw IllegalStateException(
                "Não foi possível criar o diretório de contas"
            )
        }

        val accountFile =
            File(
                accountDirectory,
                "$nick.json"
            )

        require(
            !accountFile.exists()
        ) {
            "Já existe uma conta com esse nick"
        }

        val account =
            MinecraftAccount().apply {

                this.username =
                    nick

                accessToken =
                    "0"

                clientToken =
                    "0"

                profileId =
                    "00000000-0000-0000-0000-000000000000"

                isMicrosoft =
                    false

                msaRefreshToken =
                    "0"

                xuid =
                    null

                expiresAt =
                    0
            }

        account.save()

        PojavProfile.setCurrentProfile(
            this,
            account.username
        )

        return account
    }
}