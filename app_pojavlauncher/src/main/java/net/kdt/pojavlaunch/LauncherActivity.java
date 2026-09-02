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
import net.kdt.pojavlaunch.modpacks.ManagedModpack;
import net.kdt.pojavlaunch.modpacks.ManagedModpackCatalog;
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
import java.util.List;
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
    // Preferences do launcher gerenciado
    // =========================================================

    private static final String PREF_MANAGED_PACK_ID =
            "managed_pack_id";


    // =========================================================
    // Estado dos modpacks gerenciados
    // =========================================================

    /*
     * ID do pack que está sendo preparado neste momento.
     *
     * null = nenhum pack sendo preparado.
     */
    private volatile String mPreparingManagedPackId =
            null;


    /*
     * ID do pack que está efetivamente pronto e cujo
     * MinecraftProfile foi selecionado.
     *
     * null = nenhum pack pronto nesta execução ainda.
     */
    private volatile String mReadyManagedPackId =
            null;


    // =========================================================
    // Estado visual
    // =========================================================

    private boolean mLauncherLoading =
            false;


    // =========================================================
    // Views legadas
    // =========================================================

    private CenterCropVideoView mBackgroundVideo;

    private mcAccountSpinner mAccountSpinner;

    private ProgressLayout mProgressLayout;


    // =========================================================
    // Infraestrutura
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
    // Listener de configurações
    // =========================================================

    private final ExtraListener<String> mBackPreferenceListener =
            (key, value) -> {

                if ("true".equals(value)) {

                    if (
                            getSupportFragmentManager()
                                    .getBackStackEntryCount()
                                    > 0
                    ) {

                        getSupportFragmentManager()
                                .popBackStack();

                    } else {

                        showSettings();
                    }
                }

                return false;
            };


    // =========================================================
    // Compatibilidade LAUNCH_GAME
    // =========================================================

    private final ExtraListener<Boolean> mLaunchGameListener =
            (key, value) -> {

                launchGame();

                return false;
            };


    // =========================================================
    // Tasks
    // =========================================================

    private final TaskCountListener mTaskCountListener =
            taskCount -> {

                Tools.runOnUiThread(
                        () -> {

                            boolean loading =
                                    taskCount > 0;

                            setLoading(
                                    loading
                            );


                            if (
                                    loading
                                            && mNotificationManager
                                            != null
                            ) {

                                mNotificationManager.cancel(
                                        NotificationUtils
                                                .NOTIFICATION_ID_GAME_START
                                );
                            }
                        }
                );
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
         * Necessário para Compose dentro dessa Activity
         * herdada do Pojav.
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
         * O FragmentManager restaura o Fragment após
         * recriação da Activity.
         */
        if (
                savedInstanceState
                        == null
        ) {

            showHome();
        }


        /*
         * Prepara automaticamente o pack selecionado.
         */
        prepareSelectedManagedModpack();
    }


    // =========================================================
    // Views
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


    public void deleteAccountFromCompose(
            String username
    ) {

        mAccountSpinner.removeAccount(
                username
        );
    }


    // =========================================================
    // Bridges de modpack para Compose
    // =========================================================

    /*
     * Lista que a tela Compose pode utilizar para
     * renderizar os cards.
     */
    public List<ManagedModpack>
    getManagedModpacksFromCompose() {

        return ManagedModpackCatalog
                .getPacks();
    }


    /*
     * Retorna o ID atualmente selecionado.
     */
    public String
    getSelectedManagedModpackId() {

        return getSelectedManagedModpack()
                .getId();
    }


    /*
     * Usuário selecionou outro modpack na UI.
     */
    public void selectManagedModpackFromCompose(
            String packId
    ) {

        ManagedModpack pack =
                ManagedModpackCatalog
                        .getById(
                                packId
                        );


        if (pack == null) {

            Log.e(
                    "MANAGED_PACK",
                    "Modpack desconhecido: "
                            + packId
            );

            return;
        }


        ManagedModpack currentPack =
                getSelectedManagedModpack();


        /*
         * Já está selecionado.
         *
         * Mesmo assim verificamos se precisa preparar.
         */
        if (
                currentPack
                        .getId()
                        .equals(
                                pack.getId()
                        )
        ) {

            prepareManagedModpack(
                    pack
            );

            return;
        }


        Log.d(
                "MANAGED_PACK",
                "Selecionando "
                        + pack.getName()
                        + " ["
                        + pack.getId()
                        + "]"
        );


        LauncherPreferences
                .DEFAULT_PREF
                .edit()
                .putString(
                        PREF_MANAGED_PACK_ID,
                        pack.getId()
                )
                .apply();


        /*
         * O pack anteriormente pronto não representa
         * mais a seleção atual.
         */
        mReadyManagedPackId =
                null;


        /*
         * Se outro pack estiver sendo preparado,
         * prepareManagedModpack() não iniciará outro
         * processo simultaneamente.
         *
         * Quando o atual terminar, ele verifica a nova
         * seleção e prepara este.
         */
        prepareManagedModpack(
                pack
        );
    }


    /*
     * Compatibilidade temporária.
     *
     * Se alguma tela antiga ainda chamar
     * installBundledModpack(), não quebra o build.
     *
     * Agora ele simplesmente prepara o pack selecionado.
     */
    @Deprecated
    public void installBundledModpack() {

        prepareSelectedManagedModpack();
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

        } catch (
                Exception e
        ) {

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

        Fragment currentFragment =
                getSupportFragmentManager()
                        .findFragmentById(
                                R.id.container_fragment
                        );


        /*
         * Home já está aberta.
         */
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
                     * Não inicia atrás das configurações.
                     */
                    if (
                            !isSettingsOpen()
                    ) {

                        mBackgroundVideo.start();
                    }
                }
        );
    }


    private void pauseBackgroundVideo() {

        if (
                mBackgroundVideo
                        != null
                        && mBackgroundVideo
                        .isPlaying()
        ) {

            mBackgroundVideo.pause();
        }
    }


    private void resumeBackgroundVideo() {

        if (
                mBackgroundVideo
                        != null
                        && !mBackgroundVideo
                        .isPlaying()
                        && !isSettingsOpen()
        ) {

            mBackgroundVideo.start();
        }
    }


    // =========================================================
    // Loading
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

            LauncherHomeComposeFragment
                    homeFragment =
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

        if (
                mAccountSpinner
                        != null
        ) {

            mAccountSpinner
                    .reloadAccountSelection();
        }


        Fragment fragment =
                getSupportFragmentManager()
                        .findFragmentById(
                                R.id.container_fragment
                        );


        if (
                fragment
                        instanceof LauncherHomeComposeFragment
        ) {

            LauncherHomeComposeFragment
                    homeFragment =
                    (LauncherHomeComposeFragment)
                            fragment;


            homeFragment.refreshAccount();
        }
    }


    // =========================================================
    // Modpack - seleção
    // =========================================================

    private ManagedModpack
    getSelectedManagedModpack() {

        String selectedId =
                LauncherPreferences
                        .DEFAULT_PREF
                        .getString(
                                PREF_MANAGED_PACK_ID,
                                ManagedModpackCatalog
                                        .DEFAULT_PACK_ID
                        );


        ManagedModpack pack =
                ManagedModpackCatalog
                        .getById(
                                selectedId
                        );


        /*
         * Preference inválida ou pack removido
         * do catálogo.
         */
        if (
                pack == null
        ) {

            pack =
                    ManagedModpackCatalog
                            .getDefault();


            LauncherPreferences
                    .DEFAULT_PREF
                    .edit()
                    .putString(
                            PREF_MANAGED_PACK_ID,
                            pack.getId()
                    )
                    .apply();
        }


        return pack;
    }


    private void
    prepareSelectedManagedModpack() {

        ManagedModpack selectedPack =
                getSelectedManagedModpack();


        prepareManagedModpack(
                selectedPack
        );
    }


    // =========================================================
    // Modpack - preparação
    // =========================================================

    private void prepareManagedModpack(
            ManagedModpack pack
    ) {

        if (
                pack == null
        ) {

            return;
        }


        String packId =
                pack.getId();


        /*
         * Esse mesmo pack já está pronto.
         */
        if (
                packId.equals(
                        mReadyManagedPackId
                )
        ) {

            Log.d(
                    "MANAGED_PACK",
                    pack.getName()
                            + " já está pronto"
            );

            return;
        }


        /*
         * Já existe um pack sendo preparado.
         *
         * Não instalamos dois packs simultaneamente.
         */
        if (
                mPreparingManagedPackId
                        != null
        ) {

            Log.d(
                    "MANAGED_PACK",
                    "Já preparando "
                            + mPreparingManagedPackId
                            + ". "
                            + packId
                            + " será verificado depois."
            );

            return;
        }


        mPreparingManagedPackId =
                packId;


        setLoading(
                true
        );


        Log.d(
                "MANAGED_PACK",
                "Preparando "
                        + pack.getName()
                        + " ["
                        + packId
                        + "]"
        );


        PojavApplication
                .sExecutorService
                .execute(
                        () -> {

                            File modpackFile =
                                    null;


                            try {

                                // =================================
                                // Hash do MRPACK
                                // =================================

                                String expectedHash =
                                        calculateManagedMrpackSha1(
                                                pack
                                        );


                                Log.d(
                                        "MANAGED_PACK",
                                        "SHA-1 "
                                                + pack.getName()
                                                + ": "
                                                + expectedHash
                                );


                                // =================================
                                // Carrega profiles
                                // =================================

                                LauncherProfiles.load();


                                String profileKey =
                                        findManagedModpackProfile(
                                                expectedHash
                                        );


                                // =================================
                                // Ainda não instalado
                                // =================================

                                if (
                                        profileKey
                                                == null
                                ) {

                                    Log.d(
                                            "MANAGED_PACK",
                                            pack.getName()
                                                    + " ainda não instalado"
                                    );


                                    modpackFile =
                                            copyManagedMrpackToCache(
                                                    pack
                                            );


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


                                    if (
                                            loaderInfo
                                                    == null
                                    ) {

                                        throw new IOException(
                                                "O MRPACK não retornou "
                                                        + "um ModLoader válido"
                                        );
                                    }


                                    // =================================
                                    // Instala arquivos do pack
                                    // =================================

                                    loaderInfo
                                            .getDownloadTask(
                                                    new NotificationDownloadListener(
                                                            LauncherActivity.this,
                                                            loaderInfo
                                                    )
                                            )
                                            .run();


                                    // =================================
                                    // Verifica loader
                                    // =================================

                                    String loaderVersionId =
                                            loaderInfo
                                                    .getVersionId();


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
                                                "Loader não foi instalado "
                                                        + "corretamente: "
                                                        + loaderVersionId
                                        );
                                    }


                                    // =================================
                                    // Recarrega profiles
                                    // =================================

                                    LauncherProfiles.load();


                                    profileKey =
                                            findManagedModpackProfile(
                                                    expectedHash
                                            );


                                    if (
                                            profileKey
                                                    == null
                                    ) {

                                        throw new IOException(
                                                "O pack foi importado, "
                                                        + "mas o profile "
                                                        + "não foi encontrado"
                                        );
                                    }
                                }


                                Log.d(
                                        "MANAGED_PACK",
                                        "Profile encontrado para "
                                                + pack.getName()
                                                + ": "
                                                + profileKey
                                );


                                // =================================
                                // Confere seleção atual
                                // =================================

                                ManagedModpack
                                        currentlySelectedPack =
                                        getSelectedManagedModpack();


                                /*
                                 * O usuário pode ter trocado de pack
                                 * enquanto esse estava instalando.
                                 *
                                 * Nesse caso o pack foi instalado,
                                 * mas NÃO selecionamos seu profile.
                                 */
                                if (
                                        packId.equals(
                                                currentlySelectedPack
                                                        .getId()
                                        )
                                ) {

                                    selectManagedProfile(
                                            profileKey
                                    );


                                    mReadyManagedPackId =
                                            packId;


                                    Log.d(
                                            "MANAGED_PACK",
                                            pack.getName()
                                                    + " pronto para jogar"
                                    );

                                } else {

                                    Log.d(
                                            "MANAGED_PACK",
                                            pack.getName()
                                                    + " terminou de preparar, "
                                                    + "mas agora o selecionado é "
                                                    + currentlySelectedPack
                                                    .getName()
                                    );
                                }


                            } catch (
                                    IOException
                                    | NoSuchAlgorithmException e
                            ) {

                                Log.e(
                                        "MANAGED_PACK",
                                        "Falha preparando "
                                                + pack.getName(),
                                        e
                                );


                                /*
                                 * Só mostramos erro se esse ainda
                                 * for o pack selecionado.
                                 *
                                 * Se o usuário trocou de pack durante
                                 * o processo, apenas seguimos para o novo.
                                 */
                                ManagedModpack
                                        currentlySelectedPack =
                                        getSelectedManagedModpack();


                                if (
                                        packId.equals(
                                                currentlySelectedPack
                                                        .getId()
                                        )
                                ) {

                                    Tools.showErrorRemote(
                                            LauncherActivity.this,
                                            R.string
                                                    .modpack_install_download_failed,
                                            e
                                    );
                                }


                            } finally {

                                // =================================
                                // Limpa MRPACK temporário
                                // =================================

                                if (
                                        modpackFile
                                                != null
                                                && modpackFile
                                                .exists()
                                ) {

                                    //noinspection ResultOfMethodCallIgnored
                                    modpackFile.delete();
                                }


                                mPreparingManagedPackId =
                                        null;


                                // =================================
                                // Verifica se seleção mudou
                                // =================================

                                ManagedModpack
                                        currentlySelectedPack =
                                        getSelectedManagedModpack();


                                /*
                                 * Só inicia automaticamente outro
                                 * processo se o usuário tiver realmente
                                 * mudado de pack durante a preparação.
                                 *
                                 * Isso evita loop infinito caso a
                                 * instalação do mesmo pack falhe.
                                 */
                                boolean selectionChanged =
                                        !packId.equals(
                                                currentlySelectedPack
                                                        .getId()
                                        );


                                if (
                                        selectionChanged
                                ) {

                                    Log.d(
                                            "MANAGED_PACK",
                                            "Seleção mudou para "
                                                    + currentlySelectedPack
                                                    .getName()
                                                    + ". Preparando novo pack."
                                    );


                                    runOnUiThread(
                                            () ->
                                                    prepareSelectedManagedModpack()
                                    );

                                } else {

                                    runOnUiThread(
                                            () ->
                                                    setLoading(
                                                            false
                                                    )
                                    );
                                }
                            }
                        }
                );
    }


    // =========================================================
    // Modpack - cache
    // =========================================================

    private File copyManagedMrpackToCache(
            ManagedModpack pack
    ) throws IOException {

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
                        pack.getId()
                                + "_import.mrpack"
                );


        Log.d(
                "MANAGED_PACK",
                "Copiando asset "
                        + pack.getAssetFile()
                        + " para "
                        + destination
                        .getAbsolutePath()
        );


        try (
                InputStream input =
                        getAssets().open(
                                pack.getAssetFile()
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


    // =========================================================
    // Modpack - localizar profile
    // =========================================================

    private String findManagedModpackProfile(
            String expectedHash
    ) {

        if (
                LauncherProfiles
                        .mainProfileJson
                        == null
                        || LauncherProfiles
                        .mainProfileJson
                        .profiles
                        == null
        ) {

            return null;
        }


        for (
                Map.Entry<
                        String,
                        MinecraftProfile
                        > entry :
                LauncherProfiles
                        .mainProfileJson
                        .profiles
                        .entrySet()
        ) {

            MinecraftProfile profile =
                    entry.getValue();


            if (
                    profile
                            == null
                            || profile.gameDir
                            == null
                            || profile.lastVersionId
                            == null
            ) {

                continue;
            }


            /*
             * CommonApi cria a instância usando o hash
             * do MRPACK.
             */
            if (
                    !profile.gameDir
                            .endsWith(
                                    expectedHash
                            )
            ) {

                continue;
            }


            String relativeGameDir =
                    profile.gameDir
                            .startsWith("./")
                            ? profile.gameDir
                            .substring(2)
                            : profile.gameDir;


            File instanceDirectory =
                    new File(
                            Tools.DIR_GAME_HOME,
                            relativeGameDir
                    );


            if (
                    !instanceDirectory
                            .isDirectory()
            ) {

                continue;
            }


            return entry.getKey();
        }


        return null;
    }


    // =========================================================
    // Modpack - selecionar profile
    // =========================================================

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
                "MANAGED_PACK",
                "Profile selecionado: "
                        + profileKey
                        + " | versão: "
                        + (
                        profile
                                != null
                                ? profile.lastVersionId
                                : "null"
                )
        );
    }


    // =========================================================
    // Modpack - SHA-1
    // =========================================================

    private String calculateManagedMrpackSha1(
            ManagedModpack pack
    ) throws
            IOException,
            NoSuchAlgorithmException {

        MessageDigest digest =
                MessageDigest.getInstance(
                        "SHA-1"
                );


        try (
                InputStream input =
                        getAssets().open(
                                pack.getAssetFile()
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
                byte b :
                hash
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

        // =========================================
        // Modpack selecionado
        // =========================================

        ManagedModpack selectedPack =
                getSelectedManagedModpack();


        /*
         * O botão Jogar só pode prosseguir se
         * O PACK ATUALMENTE SELECIONADO estiver pronto.
         */
        if (
                !selectedPack
                        .getId()
                        .equals(
                                mReadyManagedPackId
                        )
        ) {

            Log.d(
                    "MANAGED_PACK",
                    "Tentativa de jogar com "
                            + selectedPack.getName()
                            + " ainda não preparado"
            );


            prepareManagedModpack(
                    selectedPack
            );


            return;
        }


        // =========================================
        // Tasks em andamento
        // =========================================

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


        // =========================================
        // Profile selecionado
        // =========================================

        String selectedProfile =
                LauncherPreferences
                        .DEFAULT_PREF
                        .getString(
                                LauncherPreferences
                                        .PREF_KEY_CURRENT_PROFILE,
                                ""
                        );


        if (
                LauncherProfiles
                        .mainProfileJson
                        == null
                        || LauncherProfiles
                        .mainProfileJson
                        .profiles
                        == null
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
                profile
                        == null
                        || profile.lastVersionId
                        == null
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


        // =========================================
        // Conta
        // =========================================

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


        // =========================================
        // Versão
        // =========================================

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


            if (
                    mcVersion
                            != null
            ) {

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


            if (
                    isOlderThan13
            ) {

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


        // =========================================
        // Loading
        // =========================================

        setLoading(
                true
        );


        // =========================================
        // Pipeline original do Pojav
        // =========================================

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

                            if (
                                    !isAllowed
                            ) {

                                handleNoNotificationPermission();

                                return;
                            }


                            Runnable runnable =
                                    Tools.getWeakReference(
                                            mRequestNotificationPermissionRunnable
                                    );


                            if (
                                    runnable
                                            != null
                            ) {

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
                                            && runnable
                                            != null
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

        return Build.VERSION.SDK_INT
                < 33
                || ContextCompat
                .checkSelfPermission(
                        this,
                        Manifest.permission
                                .POST_NOTIFICATIONS
                )
                != PackageManager
                .PERMISSION_DENIED;
    }


    public boolean checkForMicrophonePermission() {

        return ContextCompat
                .checkSelfPermission(
                        this,
                        Manifest.permission
                                .RECORD_AUDIO
                )
                != PackageManager
                .PERMISSION_DENIED;
    }


    public void askForNotificationPermission(
            Runnable onSuccessRunnable
    ) {

        if (
                Build.VERSION.SDK_INT
                        < 33
        ) {

            return;
        }


        if (
                onSuccessRunnable
                        != null
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
                onSuccessRunnable
                        != null
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
                mInstallTracker
                        != null
        ) {

            mInstallTracker.attach();
        }


        if (
                !isSettingsOpen()
        ) {

            resumeBackgroundVideo();
        }
    }


    @Override
    protected void onPause() {

        pauseBackgroundVideo();


        ContextExecutor.clearActivity();


        if (
                mInstallTracker
                        != null
        ) {

            mInstallTracker.detach();
        }


        super.onPause();
    }


    @Override
    protected void onDestroy() {

        if (
                mBackgroundVideo
                        != null
        ) {

            mBackgroundVideo.stopPlayback();
        }


        if (
                mProgressLayout
                        != null
        ) {

            mProgressLayout.cleanUpObservers();


            ProgressKeeper.removeTaskCountListener(
                    mProgressLayout
            );
        }


        if (
                mProgressServiceKeeper
                        != null
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

        super.onAttachedToWindow();


        LauncherPreferences
                .computeNotchSize(
                        this
                );
    }
}