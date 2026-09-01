package net.kdt.pojavlaunch;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;
import static net.kdt.pojavlaunch.Tools.hasNoOnlineProfileDialog;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewTreeLifecycleOwner;
import androidx.lifecycle.ViewTreeViewModelStoreOwner;
import androidx.savedstate.ViewTreeSavedStateRegistryOwner;

import com.kdt.mcgui.ProgressLayout;
import com.kdt.mcgui.mcAccountSpinner;

import net.kdt.pojavlaunch.contracts.OpenDocumentWithExtension;
import net.kdt.pojavlaunch.extra.ExtraConstants;
import net.kdt.pojavlaunch.extra.ExtraCore;
import net.kdt.pojavlaunch.extra.ExtraListener;
import net.kdt.pojavlaunch.lifecycle.ContextAwareDoneListener;
import net.kdt.pojavlaunch.lifecycle.ContextExecutor;
import net.kdt.pojavlaunch.modloaders.modpacks.ModloaderInstallTracker;
import net.kdt.pojavlaunch.modloaders.modpacks.api.CommonApi;
import net.kdt.pojavlaunch.modloaders.modpacks.api.ModLoader;
import net.kdt.pojavlaunch.modloaders.modpacks.api.NotificationDownloadListener;
import net.kdt.pojavlaunch.modloaders.modpacks.imagecache.IconCacheJanitor;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;
import net.kdt.pojavlaunch.progresskeeper.ProgressKeeper;
import net.kdt.pojavlaunch.progresskeeper.TaskCountListener;
import net.kdt.pojavlaunch.services.ProgressServiceKeeper;
import net.kdt.pojavlaunch.settings.SettingsComposeFragment;
import net.kdt.pojavlaunch.tasks.AsyncMinecraftDownloader;
import net.kdt.pojavlaunch.tasks.AsyncVersionList;
import net.kdt.pojavlaunch.tasks.MinecraftDownloader;
import net.kdt.pojavlaunch.utils.DateUtils;
import net.kdt.pojavlaunch.utils.NotificationUtils;
import net.kdt.pojavlaunch.value.launcherprofiles.LauncherProfiles;
import net.kdt.pojavlaunch.value.launcherprofiles.MinecraftProfile;
import net.kdt.pojavlaunch.views.CenterCropVideoView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Map;

public class LauncherActivity extends BaseActivity {

    // =========================================================
    // Fragment tags
    // =========================================================

    private static final String HOME_COMPOSE_TAG =
            "HOME_COMPOSE";

    private static final String SETTINGS_COMPOSE_TAG =
            "SETTINGS_COMPOSE";


    /*
     * Mantido por compatibilidade caso alguma parte antiga
     * do Pojav ainda referencie essa constante.
     */
    public static final String SETTING_FRAGMENT_TAG =
            "SETTINGS_FRAGMENT";


    // =========================================================
    // Mod installer
    // =========================================================

    public final ActivityResultLauncher<Object> modInstallerLauncher =
            registerForActivityResult(
                    new OpenDocumentWithExtension("jar"),
                    data -> {
                        if (data != null) {
                            Tools.launchModInstaller(
                                    this,
                                    data
                            );
                        }
                    }
            );


    // =========================================================
    // Modpack gerenciado
    // =========================================================

    private static final String BUNDLED_MRPACK_ASSET =
            "cobblemon_online.mrpack";

    private volatile boolean mManagedPackReady = false;

    private volatile boolean mManagedPackPreparing = false;


    // =========================================================
    // Estado visual do launcher
    // =========================================================

    private boolean mLauncherLoading = false;


    // =========================================================
    // Views legadas que continuam existindo
    // =========================================================

    private CenterCropVideoView mBackgroundVideo;

    private mcAccountSpinner mAccountSpinner;

    private ProgressLayout mProgressLayout;


    // =========================================================
    // Infraestrutura interna
    // =========================================================

    private ProgressServiceKeeper mProgressServiceKeeper;

    private ModloaderInstallTracker mInstallTracker;

    private NotificationManager mNotificationManager;


    // =========================================================
    // Permissões
    // =========================================================

    private ActivityResultLauncher<String>
            mRequestNotificationPermissionLauncher;

    private ActivityResultLauncher<String>
            mRequestMicrophonePermissionLauncher;

    private WeakReference<Runnable>
            mRequestNotificationPermissionRunnable;

    private WeakReference<Runnable>
            mRequestMicrophonePermissionRunnable;


    // =========================================================
    // Listener de configurações
    // =========================================================

    private final ExtraListener<String> mBackPreferenceListener =
            (key, value) -> {

                if ("true".equals(value)) {
                    showHome();
                }

                return false;
            };


    // =========================================================
    // Compatibilidade com LAUNCH_GAME antigo
    // =========================================================

    private final ExtraListener<Boolean> mLaunchGameListener =
            (key, value) -> {

                launchGame();

                return false;
            };


    // =========================================================
    // Atualiza Compose quando existem tarefas
    // =========================================================

    private final TaskCountListener mTaskCountListener =
            taskCount -> {

                Tools.runOnUiThread(() -> {

                    boolean loading =
                            taskCount > 0;

                    setLoading(
                            loading
                    );

                    if (
                            loading
                                    && mNotificationManager != null
                    ) {

                        mNotificationManager.cancel(
                                NotificationUtils
                                        .NOTIFICATION_ID_GAME_START
                        );
                    }
                });
            };


    // =========================================================
    // BaseActivity
    // =========================================================

    @Override
    protected boolean shouldIgnoreNotch() {

        return getResources()
                .getConfiguration()
                .orientation
                == ORIENTATION_PORTRAIT;
    }


    @Override
    public boolean setFullscreen() {
        return false;
    }

    public void deleteAccountFromCompose(
            String username
    ) {
        mAccountSpinner.removeAccount(username);
    }

    // =========================================================
    // onCreate
    // =========================================================

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(
                savedInstanceState
        );
        Log.d(
                "RENDER_TEST",
                "Hardware accelerated: "
                        + getWindow()
                        .getDecorView()
                        .isHardwareAccelerated()
        );

        /*
         * Necessário porque essa Activity/BaseActivity antiga
         * não estava instalando automaticamente os ViewTree owners
         * esperados pelo Compose.
         */
        View decorView =
                getWindow()
                        .getDecorView();

        ViewTreeLifecycleOwner.set(
                decorView,
                this
        );

        ViewTreeViewModelStoreOwner.set(
                decorView,
                this
        );

        ViewTreeSavedStateRegistryOwner.set(
                decorView,
                this
        );


        Log.d(
                "LAUNCHER_UI",
                "Abrindo launcher Compose"
        );


        setContentView(
                R.layout.activity_pojav_launcher
        );


        bindViews();

        setupBackgroundVideo();

        setupPermissions();

        setupLauncherInfrastructure();


        /*
         * FragmentManager restaura sozinho o Fragment atual
         * após recriação da Activity.
         *
         * Só criamos a Home na primeira abertura.
         */
        if (savedInstanceState == null) {

            showHome();
        }


        prepareBundledModpack();
    }


    // =========================================================
    // Views legadas
    // =========================================================

    private void bindViews() {

        mBackgroundVideo =
                findViewById(
                        R.id.background_video
                );

        mAccountSpinner =
                findViewById(
                        R.id.account_spinner
                );

        mProgressLayout =
                findViewById(
                        R.id.progress_layout
                );
    }


    // =========================================================
    // Bridges para Compose
    // =========================================================

    public void launchGameFromCompose() {
        launchGame();
    }


    public void showSettingsFromCompose() {
        showSettings();
    }


    public void showHomeFromSettings() {
        showHome();
    }


    public void openSocialLinkFromCompose(
            int urlResource
    ) {

        openSocialLink(
                urlResource
        );
    }


    public boolean isLauncherLoading() {
        return mLauncherLoading;
    }


    // =========================================================
    // Redes sociais
    // =========================================================

    private void openSocialLink(
            int urlResource
    ) {

        String url =
                getString(
                        urlResource
                );

        Intent intent =
                new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(url)
                );

        try {

            startActivity(
                    intent
            );

        } catch (Exception e) {

            Log.e(
                    "LAUNCHER_UI",
                    "Não foi possível abrir o link: "
                            + url,
                    e
            );
        }
    }


    // =========================================================
    // Navegação Compose
    // =========================================================

    private void showHome() {

        /*
         * Se a Home já estiver aberta, não recria.
         */
        Fragment currentFragment =
                getSupportFragmentManager()
                        .findFragmentById(
                                R.id.container_fragment
                        );

        if (
                currentFragment
                        instanceof LauncherHomeComposeFragment
        ) {

            resumeBackgroundVideo();

            return;
        }


        getSupportFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.container_fragment,
                        new LauncherHomeComposeFragment(),
                        HOME_COMPOSE_TAG
                )
                .commit();


        resumeBackgroundVideo();
    }


    private void showSettings() {

        Fragment currentFragment =
                getSupportFragmentManager()
                        .findFragmentById(
                                R.id.container_fragment
                        );

        if (
                currentFragment
                        instanceof SettingsComposeFragment
        ) {
            return;
        }


        pauseBackgroundVideo();


        getSupportFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.container_fragment,
                        new SettingsComposeFragment(),
                        SETTINGS_COMPOSE_TAG
                )
                .commit();
    }


    private boolean isSettingsOpen() {

        Fragment fragment =
                getSupportFragmentManager()
                        .findFragmentById(
                                R.id.container_fragment
                        );

        return fragment
                instanceof SettingsComposeFragment;
    }


    // =========================================================
    // Vídeo
    // =========================================================

    private void setupBackgroundVideo() {

        Uri videoUri =
                Uri.parse(
                        "android.resource://"
                                + getPackageName()
                                + "/"
                                + R.raw.launcher_background
                );


        mBackgroundVideo.setVideoURI(
                videoUri
        );


        mBackgroundVideo.setOnPreparedListener(
                mediaPlayer -> {

                    mediaPlayer.setLooping(
                            true
                    );

                    mediaPlayer.setVolume(
                            0f,
                            0f
                    );


                    mBackgroundVideo.setVideoSize(
                            mediaPlayer.getVideoWidth(),
                            mediaPlayer.getVideoHeight()
                    );


                    /*
                     * Importante:
                     *
                     * Se o usuário já tiver aberto Settings
                     * enquanto o vídeo carregava, não deixa
                     * onPrepared() religar o vídeo atrás dela.
                     */
                    if (!isSettingsOpen()) {

                        mBackgroundVideo.start();
                    }
                }
        );
    }


    private void pauseBackgroundVideo() {

        if (
                mBackgroundVideo != null
                        && mBackgroundVideo.isPlaying()
        ) {

            mBackgroundVideo.pause();
        }
    }


    private void resumeBackgroundVideo() {

        if (
                mBackgroundVideo != null
                        && !mBackgroundVideo.isPlaying()
                        && !isSettingsOpen()
        ) {

            mBackgroundVideo.start();
        }
    }


    // =========================================================
    // Loading Compose
    // =========================================================

    private void setLoading(
            boolean loading
    ) {

        mLauncherLoading =
                loading;


        Fragment fragment =
                getSupportFragmentManager()
                        .findFragmentById(
                                R.id.container_fragment
                        );


        if (
                fragment
                        instanceof LauncherHomeComposeFragment
        ) {

            LauncherHomeComposeFragment homeFragment =
                    (LauncherHomeComposeFragment)
                            fragment;

            homeFragment.setLoadingState(
                    loading
            );
        }
    }


    // =========================================================
    // Conta
    // =========================================================

    public void refreshAccountSelection() {

        if (mAccountSpinner != null) {

            mAccountSpinner
                    .reloadAccountSelection();
        }


        /*
         * Se a Home estiver visível, manda ela reler
         * a conta atual.
         */
        Fragment fragment =
                getSupportFragmentManager()
                        .findFragmentById(
                                R.id.container_fragment
                        );


        if (
                fragment
                        instanceof LauncherHomeComposeFragment
        ) {

            LauncherHomeComposeFragment homeFragment =
                    (LauncherHomeComposeFragment)
                            fragment;

            homeFragment.refreshAccount();
        }
    }


    // =========================================================
    // Modpack
    // =========================================================

    public void installBundledModpack() {
        prepareBundledModpack();
    }


    private void prepareBundledModpack() {

        if (mManagedPackPreparing) {
            return;
        }


        mManagedPackPreparing =
                true;


        setLoading(
                true
        );


        PojavApplication
                .sExecutorService
                .execute(() -> {

                    File modpackFile =
                            null;

                    try {

                        // =========================================
                        // 1. Hash do MRPACK embutido
                        // =========================================

                        String expectedHash =
                                calculateBundledMrpackSha1();


                        Log.d(
                                "COBBLEMON_PACK",
                                "MRPACK embutido SHA-1: "
                                        + expectedHash
                        );


                        // =========================================
                        // 2. Carrega launcher_profiles
                        // =========================================

                        LauncherProfiles.load();


                        // =========================================
                        // 3. Verifica instalação existente
                        // =========================================

                        String profileKey =
                                findBundledModpackProfile(
                                        expectedHash
                                );


                        // =========================================
                        // 4. Importa caso ainda não exista
                        // =========================================

                        if (profileKey == null) {

                            Log.d(
                                    "COBBLEMON_PACK",
                                    "Pack ainda não instalado. Importando..."
                            );


                            modpackFile =
                                    copyBundledMrpackToCache();


                            CommonApi commonApi =
                                    new CommonApi(
                                            getString(
                                                    R.string
                                                            .curseforge_api_key
                                            )
                                    );


                            Uri modpackUri =
                                    Uri.fromFile(
                                            modpackFile
                                    );


                            ModLoader loaderInfo =
                                    commonApi.importModpack(
                                            LauncherActivity.this,
                                            modpackUri
                                    );


                            if (loaderInfo == null) {

                                throw new IOException(
                                        "O MRPACK não retornou um ModLoader válido"
                                );
                            }


                            loaderInfo
                                    .getDownloadTask(
                                            new NotificationDownloadListener(
                                                    this,
                                                    loaderInfo
                                            )
                                    )
                                    .run();


                            String loaderVersionId =
                                    loaderInfo.getVersionId();


                            File loaderVersionJson =
                                    new File(
                                            Tools.DIR_HOME_VERSION,
                                            loaderVersionId
                                                    + "/"
                                                    + loaderVersionId
                                                    + ".json"
                                    );


                            if (
                                    !loaderVersionJson
                                            .isFile()
                            ) {

                                throw new IOException(
                                        "Fabric não foi instalado corretamente: "
                                                + loaderVersionId
                                );
                            }


                            /*
                             * O importador alterou launcher_profiles.
                             * Recarrega para pegar o estado atualizado.
                             */
                            LauncherProfiles.load();


                            profileKey =
                                    findBundledModpackProfile(
                                            expectedHash
                                    );


                            if (profileKey == null) {

                                throw new IOException(
                                        "O pack foi importado, mas o profile não foi encontrado"
                                );
                            }
                        }


                        // =========================================
                        // 5. Seleciona o profile
                        // =========================================

                        selectManagedProfile(
                                profileKey
                        );


                        mManagedPackReady =
                                true;


                        Log.d(
                                "COBBLEMON_PACK",
                                "Cobblemon Online pronto para jogar"
                        );


                    } catch (
                            IOException
                            | NoSuchAlgorithmException e
                    ) {

                        mManagedPackReady =
                                false;


                        Log.e(
                                "COBBLEMON_PACK",
                                "Falha preparando Cobblemon Online",
                                e
                        );


                        Tools.showErrorRemote(
                                this,
                                R.string
                                        .modpack_install_download_failed,
                                e
                        );


                    } catch (
                            IllegalArgumentException e
                    ) {

                        mManagedPackReady =
                                false;


                        Log.e(
                                "COBBLEMON_PACK",
                                "MRPACK inválido",
                                e
                        );


                        Tools.showError(
                                this,
                                R.string.not_modpack_file,
                                e
                        );


                    } finally {

                        if (
                                modpackFile != null
                                        && modpackFile.exists()
                        ) {

                            //noinspection ResultOfMethodCallIgnored
                            modpackFile.delete();
                        }


                        mManagedPackPreparing =
                                false;


                        runOnUiThread(
                                () ->
                                        setLoading(
                                                false
                                        )
                        );
                    }
                });
    }


    private String findBundledModpackProfile(
            String expectedHash
    ) {

        if (
                LauncherProfiles.mainProfileJson == null
                        || LauncherProfiles
                        .mainProfileJson
                        .profiles == null
        ) {

            return null;
        }


        for (
                Map.Entry<String, MinecraftProfile> entry :
                LauncherProfiles
                        .mainProfileJson
                        .profiles
                        .entrySet()
        ) {

            MinecraftProfile profile =
                    entry.getValue();


            if (
                    profile == null
                            || profile.gameDir == null
                            || profile.lastVersionId == null
            ) {

                continue;
            }


            if (
                    !profile.gameDir.endsWith(
                            expectedHash
                    )
            ) {

                continue;
            }


            String relativeGameDir =
                    profile.gameDir.startsWith("./")
                            ? profile.gameDir.substring(2)
                            : profile.gameDir;


            File instanceDirectory =
                    new File(
                            Tools.DIR_GAME_HOME,
                            relativeGameDir
                    );


            if (
                    !instanceDirectory.isDirectory()
            ) {

                continue;
            }


            return entry.getKey();
        }


        return null;
    }


    private void selectManagedProfile(
            String profileKey
    ) {

        LauncherPreferences
                .DEFAULT_PREF
                .edit()
                .putString(
                        LauncherPreferences
                                .PREF_KEY_CURRENT_PROFILE,
                        profileKey
                )
                .apply();


        MinecraftProfile profile =
                LauncherProfiles
                        .mainProfileJson
                        .profiles
                        .get(
                                profileKey
                        );


        Log.d(
                "COBBLEMON_PACK",
                "Profile selecionado: "
                        + profileKey
                        + " | "
                        + (
                        profile != null
                                ? profile.lastVersionId
                                : "null"
                )
        );
    }


    private File copyBundledMrpackToCache()
            throws IOException {

        File cacheDirectory =
                Tools.DIR_CACHE;


        if (
                !cacheDirectory.exists()
        ) {

            //noinspection ResultOfMethodCallIgnored
            cacheDirectory.mkdirs();
        }


        File destination =
                new File(
                        cacheDirectory,
                        "cobblemon_online_import.mrpack"
                );


        try (
                InputStream input =
                        getAssets().open(
                                BUNDLED_MRPACK_ASSET
                        );

                FileOutputStream output =
                        new FileOutputStream(
                                destination
                        )
        ) {

            byte[] buffer =
                    new byte[262144];


            int read;


            while (
                    (read = input.read(buffer))
                            != -1
            ) {

                output.write(
                        buffer,
                        0,
                        read
                );
            }


            output.flush();
        }


        return destination;
    }


    private String calculateBundledMrpackSha1()
            throws IOException,
            NoSuchAlgorithmException {

        MessageDigest digest =
                MessageDigest.getInstance(
                        "SHA-1"
                );


        try (
                InputStream input =
                        getAssets().open(
                                BUNDLED_MRPACK_ASSET
                        )
        ) {

            byte[] buffer =
                    new byte[262144];


            int read;


            while (
                    (read = input.read(buffer))
                            != -1
            ) {

                digest.update(
                        buffer,
                        0,
                        read
                );
            }
        }


        byte[] hash =
                digest.digest();


        StringBuilder result =
                new StringBuilder(
                        hash.length * 2
                );


        for (
                byte b : hash
        ) {

            result.append(
                    String.format(
                            "%02x",
                            b
                    )
            );
        }


        return result.toString();
    }


    // =========================================================
    // Inicialização interna
    // =========================================================

    private void setupLauncherInfrastructure() {

        IconCacheJanitor.runJanitor();


        getWindow()
                .setBackgroundDrawable(
                        null
                );


        checkNotificationPermission();


        mNotificationManager =
                (NotificationManager)
                        getSystemService(
                                Context.NOTIFICATION_SERVICE
                        );


        // =========================================
        // Progress
        // =========================================

        ProgressKeeper
                .addTaskCountListener(
                        mTaskCountListener
                );


        ProgressKeeper
                .addTaskCountListener(
                        mProgressServiceKeeper =
                                new ProgressServiceKeeper(
                                        this
                                )
                );


        ProgressKeeper
                .addTaskCountListener(
                        mProgressLayout
                );


        // =========================================
        // Eventos
        // =========================================

        ExtraCore.addExtraListener(
                ExtraConstants.BACK_PREFERENCE,
                mBackPreferenceListener
        );


        ExtraCore.addExtraListener(
                ExtraConstants.LAUNCH_GAME,
                mLaunchGameListener
        );


        // =========================================
        // Lista de versões
        // =========================================

        new AsyncVersionList()
                .getVersionList(
                        versions ->
                                ExtraCore.setValue(
                                        ExtraConstants.RELEASE_TABLE,
                                        versions
                                ),
                        false
                );


        // =========================================
        // Mod loaders
        // =========================================

        mInstallTracker =
                new ModloaderInstallTracker(
                        this
                );


        // =========================================
        // Processos internos
        // =========================================

        mProgressLayout.observe(
                ProgressLayout.DOWNLOAD_MINECRAFT
        );

        mProgressLayout.observe(
                ProgressLayout.UNPACK_RUNTIME
        );

        mProgressLayout.observe(
                ProgressLayout.INSTALL_MODPACK
        );

        mProgressLayout.observe(
                ProgressLayout.AUTHENTICATE_MICROSOFT
        );

        mProgressLayout.observe(
                ProgressLayout.DOWNLOAD_VERSION_LIST
        );
    }


    // =========================================================
    // Jogar
    // =========================================================

    private void launchGame() {

        /*
         * Modpack ainda não ficou pronto.
         */
        if (!mManagedPackReady) {

            if (!mManagedPackPreparing) {

                prepareBundledModpack();
            }

            return;
        }


        /*
         * Alguma tarefa já está rodando.
         */
        if (
                mProgressLayout
                        .hasProcesses()
        ) {

            Toast.makeText(
                    this,
                    R.string.tasks_ongoing,
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        /*
         * Profile/instância selecionada.
         */
        String selectedProfile =
                LauncherPreferences
                        .DEFAULT_PREF
                        .getString(
                                LauncherPreferences
                                        .PREF_KEY_CURRENT_PROFILE,
                                ""
                        );


        if (
                LauncherProfiles.mainProfileJson == null
                        || LauncherProfiles
                        .mainProfileJson
                        .profiles == null
                        || !LauncherProfiles
                        .mainProfileJson
                        .profiles
                        .containsKey(
                                selectedProfile
                        )
        ) {

            Toast.makeText(
                    this,
                    R.string.error_no_version,
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        MinecraftProfile profile =
                LauncherProfiles
                        .mainProfileJson
                        .profiles
                        .get(
                                selectedProfile
                        );


        if (
                profile == null
                        || profile.lastVersionId == null
                        || "Unknown".equals(
                        profile.lastVersionId
                )
        ) {

            Toast.makeText(
                    this,
                    R.string.error_no_version,
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        /*
         * Conta.
         *
         * Mantemos mcAccountSpinner internamente porque
         * o pipeline legado ainda depende dele.
         */
        if (
                mAccountSpinner
                        .getSelectedAccount()
                        == null
        ) {

            Intent intent =
                    new Intent(
                            this,
                            net.kdt.pojavlaunch
                                    .ui
                                    .LauncherActivity
                                    .class
                    );


            startActivity(
                    intent
            );

            finish();

            return;
        }


        String normalizedVersionId =
                AsyncMinecraftDownloader
                        .normalizeVersionId(
                                profile.lastVersionId
                        );


        JMinecraftVersionList.Version mcVersion =
                AsyncMinecraftDownloader
                        .getListedVersion(
                                normalizedVersionId
                        );


        // =========================================
        // Conta demo
        // =========================================

        if (
                mAccountSpinner
                        .getSelectedAccount()
                        .isDemo()
        ) {

            boolean isOlderThan13 =
                    true;


            if (mcVersion != null) {

                try {

                    isOlderThan13 =
                            DateUtils.dateBefore(
                                    DateUtils.parseReleaseDate(
                                            mcVersion.releaseTime
                                    ),
                                    2012,
                                    6,
                                    22
                            );

                } catch (
                        ParseException ignored
                ) {
                }
            }


            if (isOlderThan13) {

                hasNoOnlineProfileDialog(
                        this,
                        getString(
                                R.string.global_error
                        ),
                        getString(
                                R.string
                                        .demo_versions_supported
                        )
                );

                return;
            }
        }


        /*
         * Compose muda barra social -> loading
         * imediatamente.
         */
        setLoading(
                true
        );


        /*
         * Pipeline original do Pojav.
         */
        new MinecraftDownloader()
                .start(
                        this,
                        mcVersion,
                        normalizedVersionId,
                        new ContextAwareDoneListener(
                                this,
                                normalizedVersionId
                        )
                );
    }


    // =========================================================
    // Permissões
    // =========================================================

    private void setupPermissions() {

        mRequestNotificationPermissionLauncher =
                registerForActivityResult(
                        new ActivityResultContracts
                                .RequestPermission(),
                        isAllowed -> {

                            if (!isAllowed) {

                                handleNoNotificationPermission();

                                return;
                            }


                            Runnable runnable =
                                    Tools.getWeakReference(
                                            mRequestNotificationPermissionRunnable
                                    );


                            if (runnable != null) {

                                runnable.run();
                            }
                        }
                );


        mRequestMicrophonePermissionLauncher =
                registerForActivityResult(
                        new ActivityResultContracts
                                .RequestPermission(),
                        isAllowed -> {

                            Runnable runnable =
                                    Tools.getWeakReference(
                                            mRequestMicrophonePermissionRunnable
                                    );


                            if (
                                    isAllowed
                                            && runnable != null
                            ) {

                                runnable.run();
                            }
                        }
                );
    }


    private void checkNotificationPermission() {

        if (
                LauncherPreferences
                        .PREF_SKIP_NOTIFICATION_PERMISSION_CHECK
                        || checkForNotificationPermission()
        ) {

            return;
        }


        if (
                ActivityCompat
                        .shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission
                                        .POST_NOTIFICATIONS
                        )
        ) {

            showNotificationPermissionReasoning();

            return;
        }


        askForNotificationPermission(
                null
        );
    }


    private void showNotificationPermissionReasoning() {

        new AlertDialog.Builder(
                this
        )
                .setTitle(
                        R.string
                                .notification_permission_dialog_title
                )
                .setMessage(
                        R.string
                                .notification_permission_dialog_text
                )
                .setPositiveButton(
                        android.R.string.ok,
                        (dialog, which) ->
                                askForNotificationPermission(
                                        null
                                )
                )
                .setNegativeButton(
                        android.R.string.cancel,
                        (dialog, which) ->
                                handleNoNotificationPermission()
                )
                .show();
    }


    private void handleNoNotificationPermission() {

        LauncherPreferences
                .PREF_SKIP_NOTIFICATION_PERMISSION_CHECK =
                true;


        LauncherPreferences
                .DEFAULT_PREF
                .edit()
                .putBoolean(
                        LauncherPreferences
                                .PREF_KEY_SKIP_NOTIFICATION_CHECK,
                        true
                )
                .apply();


        Toast.makeText(
                this,
                R.string.notification_permission_toast,
                Toast.LENGTH_LONG
        ).show();
    }


    public boolean checkForNotificationPermission() {

        return Build.VERSION.SDK_INT < 33
                || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_DENIED;
    }


    public boolean checkForMicrophonePermission() {

        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
        ) != PackageManager.PERMISSION_DENIED;
    }


    public void askForNotificationPermission(
            Runnable onSuccessRunnable
    ) {

        if (
                Build.VERSION.SDK_INT < 33
        ) {

            return;
        }


        if (
                onSuccessRunnable != null
        ) {

            mRequestNotificationPermissionRunnable =
                    new WeakReference<>(
                            onSuccessRunnable
                    );
        }


        mRequestNotificationPermissionLauncher
                .launch(
                        Manifest.permission
                                .POST_NOTIFICATIONS
                );
    }


    public void askForMicrophonePermission(
            Runnable onSuccessRunnable
    ) {

        if (
                onSuccessRunnable != null
        ) {

            mRequestMicrophonePermissionRunnable =
                    new WeakReference<>(
                            onSuccessRunnable
                    );
        }


        mRequestMicrophonePermissionLauncher
                .launch(
                        Manifest.permission
                                .RECORD_AUDIO
                );
    }


    // =========================================================
    // Lifecycle
    // =========================================================

    @Override
    protected void onResume() {

        super.onResume();


        ContextExecutor.setActivity(
                this
        );


        if (
                mInstallTracker != null
        ) {

            mInstallTracker.attach();
        }


        /*
         * Não religa vídeo caso a Activity esteja mostrando
         * Settings.
         */
        if (!isSettingsOpen()) {

            resumeBackgroundVideo();
        }
    }


    @Override
    protected void onPause() {

        pauseBackgroundVideo();


        ContextExecutor.clearActivity();


        if (
                mInstallTracker != null
        ) {

            mInstallTracker.detach();
        }


        super.onPause();
    }


    @Override
    protected void onDestroy() {

        if (
                mBackgroundVideo != null
        ) {

            mBackgroundVideo.stopPlayback();
        }


        if (
                mProgressLayout != null
        ) {

            mProgressLayout.cleanUpObservers();


            ProgressKeeper.removeTaskCountListener(
                    mProgressLayout
            );
        }


        if (
                mProgressServiceKeeper != null
        ) {

            ProgressKeeper.removeTaskCountListener(
                    mProgressServiceKeeper
            );
        }


        ProgressKeeper.removeTaskCountListener(
                mTaskCountListener
        );


        ExtraCore.removeExtraListenerFromValue(
                ExtraConstants.BACK_PREFERENCE,
                mBackPreferenceListener
        );


        ExtraCore.removeExtraListenerFromValue(
                ExtraConstants.LAUNCH_GAME,
                mLaunchGameListener
        );


        super.onDestroy();
    }


    // =========================================================
    // Voltar
    // =========================================================

    @Override
    public void onBackPressed() {

        if (
                isSettingsOpen()
        ) {

            showHome();

            return;
        }


        super.onBackPressed();
    }


    // =========================================================
    // Notch
    // =========================================================

    @Override
    public void onAttachedToWindow() {

        LauncherPreferences
                .computeNotchSize(
                        this
                );
    }
}

