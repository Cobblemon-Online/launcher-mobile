package net.kdt.pojavlaunch.modloaders.modpacks.api;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;

import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.utils.DownloadUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class ModDownloader {
    private static final ThreadLocal<byte[]> sThreadLocalBuffer = new ThreadLocal<>();
    private final ThreadPoolExecutor mDownloadPool = new ThreadPoolExecutor(4,4,100, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>());
    private final AtomicBoolean mTerminator = new AtomicBoolean(false);
    private final AtomicLong mDownloadSize = new AtomicLong(0);
    private final Object mExceptionSyncPoint = new Object();
    private final File mDestinationDirectory;
    private final boolean mUseFileCount;
    private IOException mFirstIOException;
    private long mTotalSize;

    public ModDownloader(File destinationDirectory) {
        this(destinationDirectory, false);
    }

    public ModDownloader(File destinationDirectory, boolean useFileCount) {
        this.mDownloadPool.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        this.mDestinationDirectory = destinationDirectory;
        this.mUseFileCount = useFileCount;
    }

    public void submitDownload(int fileSize, String relativePath, @Nullable String downloadHash, String... url) {
        if(mUseFileCount) mTotalSize += 1;
        else mTotalSize += fileSize;
        mDownloadPool.execute(new DownloadTask(url, new File(mDestinationDirectory, relativePath), downloadHash));
    }

    public void submitDownload(FileInfoProvider infoProvider) {
        if(!mUseFileCount) throw new RuntimeException("This method can only be used in a file-counting ModDownloader");
        mTotalSize += 1;
        mDownloadPool.execute(new FileInfoQueryTask(infoProvider));
    }

    public void awaitFinish(Tools.DownloaderFeedback feedback) throws IOException {
        try {
            mDownloadPool.shutdown();
            while(!mDownloadPool.awaitTermination(20, TimeUnit.MILLISECONDS) && !mTerminator.get()) {
                feedback.updateProgress((int) mDownloadSize.get(), (int) mTotalSize);
            }
            if(mTerminator.get()) {
                mDownloadPool.shutdownNow();
                synchronized (mExceptionSyncPoint) {
                    if(mFirstIOException == null) mExceptionSyncPoint.wait();
                    throw mFirstIOException;
                }
            }
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static byte[] getThreadLocalBuffer() {
        byte[] buffer = sThreadLocalBuffer.get();
        if(buffer != null) return buffer;
        buffer = new byte[8192];
        sThreadLocalBuffer.set(buffer);
        return buffer;
    }

    private void downloadFailed(IOException exception) {
        mTerminator.set(true);
        synchronized (mExceptionSyncPoint) {
            if(mFirstIOException == null) {
                mFirstIOException = exception;
                mExceptionSyncPoint.notify();
            }
        }
    }

    class FileInfoQueryTask implements Runnable {
        private final FileInfoProvider mFileInfoProvider;
        public FileInfoQueryTask(FileInfoProvider fileInfoProvider) {
            this.mFileInfoProvider = fileInfoProvider;
        }
        @Override
        public void run() {
            try {
                FileInfo fileInfo = mFileInfoProvider.getFileInfo();
                if(fileInfo == null) return;
                new DownloadTask(new String[]{fileInfo.url},
                        new File(mDestinationDirectory, fileInfo.relativePath), fileInfo.sha1).run();
            }catch (IOException e) {
                downloadFailed(e);
            }
        }
    }

    class DownloadTask implements Runnable, Tools.DownloaderFeedback {

        private static final String TAG = "ModDownloader";

        private final String[] mDownloadUrls;
        private final File mDestination;
        private final String mSha1;

        private int last = 0;

        public DownloadTask(
                String[] downloadUrls,
                File downloadDestination,
                String downloadHash
        ) {
            this.mDownloadUrls = downloadUrls;
            this.mDestination = downloadDestination;
            this.mSha1 = downloadHash;
        }

        @Override
        public void run() {
            IOException lastException = null;

            for (String sourceUrl : mDownloadUrls) {
                try {
                    downloadAndValidate(sourceUrl);

                    // Deu certo. Não precisa testar outras URLs.
                    return;

                } catch (InterruptedIOException e) {
                    // Outra tarefa já falhou e o pool está sendo cancelado.
                    // Isso NÃO é motivo para crashar o aplicativo.
                    Thread.currentThread().interrupt();

                    if (!mTerminator.get()) {
                        downloadFailed(e);
                    }

                    return;

                } catch (IOException e) {
                    lastException = e;
                }
            }

            if (lastException == null) {
                lastException = new IOException(
                        "No download URL succeeded for " + mDestination.getName()
                );
            }

            downloadFailed(lastException);
        }

        private void downloadAndValidate(String sourceUrl) throws IOException {

            /*
             * Arquivos sem SHA1 continuam usando o comportamento simples:
             * se já existem, não baixa novamente.
             */
            if (mSha1 == null) {
                if (mDestination.exists()) {
                    return;
                }

                IOException exception = tryDownload(sourceUrl);

                if (exception != null) {
                    throw exception;
                }

                return;
            }


            /*
             * 1. Arquivo já existe e corresponde ao MRPACK.
             */
            if (mDestination.isFile()
                    && Tools.compareSHA1(mDestination, mSha1)) {

                return;
            }


            /*
             * 2. Pode ser um arquivo já baixado anteriormente cujo SHA
             *    do MRPACK ficou desatualizado.
             *
             *    Antes de baixar novamente, pergunta ao próprio Modrinth.
             */
            if (mDestination.isFile()
                    && verifyAgainstCurrentModrinthMetadata(sourceUrl)) {

                return;
            }


            /*
             * 3. Faz o download normalmente.
             *
             * tryDownload já possui retry para ERROS DE REDE.
             * Não precisamos baixar 5 vezes só porque o SHA do MRPACK
             * ficou antigo.
             */
            IOException downloadException = tryDownload(sourceUrl);

            if (downloadException != null) {
                throw downloadException;
            }


            /*
             * 4. Primeiro tenta a validação oficial do próprio MRPACK.
             */
            if (Tools.compareSHA1(mDestination, mSha1)) {
                return;
            }


            /*
             * 5. SHA do MRPACK não corresponde.
             *
             * Se veio diretamente do CDN do Modrinth, consulta os
             * metadados ATUAIS da versão.
             */
            if (verifyAgainstCurrentModrinthMetadata(sourceUrl)) {
                return;
            }


            /*
             * Nem o MRPACK nem a API atual validaram o arquivo.
             * Nesse caso é uma falha real.
             */
            throw new DownloadUtils.SHA1VerificationException(
                    "SHA1 verification failed"
                            + " | file=" + mDestination.getAbsolutePath()
                            + " | mrpackSha1=" + mSha1
                            + " | source=" + sourceUrl
            );
        }

        /**
         * Valida novamente um arquivo usando os metadados atuais
         * retornados pela API oficial do Modrinth.
         *
         * Só funciona para URLs oficiais:
         *
         * https://cdn.modrinth.com/data/{project}/versions/{version}/{file}
         */
        private boolean verifyAgainstCurrentModrinthMetadata(String sourceUrl) {

            if (!isModrinthCdnUrl(sourceUrl)) {
                return false;
            }

            try {
                Uri uri = Uri.parse(sourceUrl);

                List<String> segments = uri.getPathSegments();

                int versionsIndex = segments.indexOf("versions");

                if (versionsIndex < 0
                        || versionsIndex + 1 >= segments.size()) {

                    return false;
                }

                String versionId = segments.get(versionsIndex + 1);

                String apiUrl =
                        "https://api.modrinth.com/v2/version/"
                                + Uri.encode(versionId);

                String json = DownloadUtils.downloadString(apiUrl);

                JSONObject versionObject = new JSONObject(json);

                JSONArray files = versionObject.optJSONArray("files");

                if (files == null) {
                    return false;
                }

                for (int i = 0; i < files.length(); i++) {

                    JSONObject fileObject = files.getJSONObject(i);

                    String apiFilename =
                            fileObject.optString("filename", "");

                    String apiFileUrl =
                            fileObject.optString("url", "");

                    /*
                     * Normalmente a URL será exatamente igual.
                     *
                     * O filename é usado como fallback porque URLs podem
                     * diferir apenas na codificação de caracteres como +.
                     */
                    boolean sameFile =
                            sourceUrl.equals(apiFileUrl)
                                    || mDestination.getName().equals(apiFilename);

                    if (!sameFile) {
                        continue;
                    }

                    JSONObject hashes =
                            fileObject.optJSONObject("hashes");

                    if (hashes == null) {
                        return false;
                    }

                    String currentSha1 =
                            hashes.optString("sha1", "");

                    long currentSize =
                            fileObject.optLong("size", -1L);

                    if (currentSha1.isEmpty()
                            || currentSize < 0) {

                        return false;
                    }

                    long downloadedSize =
                            mDestination.length();

                    boolean sizeMatches =
                            downloadedSize == currentSize;

                    boolean shaMatches =
                            Tools.compareSHA1(
                                    mDestination,
                                    currentSha1
                            );

                    if (sizeMatches && shaMatches) {

                        Log.w(
                                TAG,
                                "MRPACK contém SHA1 desatualizado."
                                        + "\nArquivo: "
                                        + mDestination.getName()
                                        + "\nSHA1 do MRPACK: "
                                        + mSha1
                                        + "\nSHA1 atual Modrinth: "
                                        + currentSha1
                                        + "\nTamanho atual: "
                                        + currentSize
                                        + "\nVersão Modrinth: "
                                        + versionId
                                        + "\nArquivo aceito após validação pela API."
                        );

                        return true;
                    }

                    Log.e(
                            TAG,
                            "Arquivo NÃO passou na validação atual do Modrinth."
                                    + "\nArquivo: "
                                    + mDestination.getName()
                                    + "\nEsperado pela API: "
                                    + currentSha1
                                    + "\nTamanho API: "
                                    + currentSize
                                    + "\nTamanho baixado: "
                                    + downloadedSize
                    );

                    return false;
                }

            } catch (IOException | JSONException e) {

                Log.w(
                        TAG,
                        "Não foi possível validar o arquivo pela API do Modrinth: "
                                + mDestination.getName(),
                        e
                );
            }

            return false;
        }

        private boolean isModrinthCdnUrl(String sourceUrl) {
            try {
                Uri uri = Uri.parse(sourceUrl);

                return "https".equalsIgnoreCase(uri.getScheme())
                        && "cdn.modrinth.com".equalsIgnoreCase(
                        uri.getHost()
                );

            } catch (Exception e) {
                return false;
            }
        }

        /**
         * Faz retry apenas para erros REAIS de download.
         */
        private IOException tryDownload(String sourceUrl) {

            IOException exception = null;

            for (int i = 0; i < 5; i++) {

                try {
                    DownloadUtils.downloadFileMonitored(
                            sourceUrl,
                            mDestination,
                            getThreadLocalBuffer(),
                            this
                    );

                    if (mUseFileCount) {
                        mDownloadSize.addAndGet(1);
                    }

                    return null;

                } catch (InterruptedIOException e) {

                    /*
                     * shutdownNow() interrompe os outros downloads quando
                     * uma tarefa falha.
                     *
                     * Antes esse InterruptedIOException virava
                     * InterruptedException -> RuntimeException -> crash.
                     */
                    Thread.currentThread().interrupt();

                    return e;

                } catch (IOException e) {

                    exception = e;

                    Log.w(
                            TAG,
                            "Falha baixando "
                                    + sourceUrl
                                    + " (tentativa "
                                    + (i + 1)
                                    + "/5)",
                            e
                    );
                }

                if (!mUseFileCount) {
                    mDownloadSize.addAndGet(-last);
                    last = 0;
                }
            }

            return exception;
        }

        @Override
        public void updateProgress(int curr, int max) {

            if (mUseFileCount) {
                return;
            }

            mDownloadSize.addAndGet(curr - last);

            last = curr;
        }
    }

    public static class FileInfo {
        public final String url;
        public final String relativePath;
        public final String sha1;

        public FileInfo(String url, String relativePath, @Nullable String sha1) {
            this.url = url;
            this.relativePath = relativePath;
            this.sha1 = sha1;
        }
    }

    public interface FileInfoProvider {
        FileInfo getFileInfo() throws IOException;
    }
}
