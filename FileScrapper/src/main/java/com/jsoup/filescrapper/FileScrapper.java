package com.jsoup.filescrapper;

import android.app.DownloadManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class FileScrapper {

    private static boolean Return;

    /**
     * This method return's the final download of link of the file which is given by the provider.
     * @param provider The file-hosting service from which you want to download file.
     * @param link The download link of the file.
     * @return {@link String} - The final link to download the file. <code>null</code> mostly when link is not valid.
     * @throws DriveException  If the provider is {@link Provider#DRIVE} & file size is bigger. (When drive cannot scan for virus for large files)
     */
    public static @Nullable String getDownloadLink(Provider provider, String link) throws DriveException {
        if (provider == Provider.MEDIAFIRE) {
            if (!link.startsWith("https://www.mediafire.com/file") & !link.endsWith("file"))
                throw new IllegalArgumentException("The download link is not of valid MEDIAFIRE file");

            try {
                Document document = Jsoup.connect("https://www.mediafire.com/file/iki1wqopv689fpz/thumb.jpg/file").get();
                Element element = document.getElementById("downloadButton");
                return element.attr("href");
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } else if (provider == Provider.DRIVE){
            if (!link.startsWith("https://drive.google.com/file/d/"))
                throw new IllegalArgumentException("The download link is not of valid DRIVE file");
            if (!link.startsWith("https://drive.google.com/u/0/uc?id="))
                link = link.replace("https://drive.google.com/file/d/","https://drive.google.com/u/0/uc?id=").replace("/view","&export=download");

            try {
                Jsoup.connect(link).get();
                throw new DriveException("Your file may be bigger in size. Google drive scans large files like .apk, .exe ,.dll etc.. before downloading them & then redirect to a new URL. User have to press \"Download anyway\"button to download the file. this button make a ajax request which end up downloading the file.Unfortunately, this library is based on JSOUP that doesn't supports ajax requests.We suggest you to upload large files on mediafire or anonfiles.");
            } catch (IOException e){
                return link;
            }
        } else if (provider == Provider.ANONFILES){
            if (!link.startsWith("https://anonfiles.com"))
                throw new IllegalArgumentException("The download link is not of valid ANONFILES file");

            try {
                Document document = Jsoup.connect(link).get();
                Element element = document.getElementById("download-url");
                return element.attr("href");
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            throw new IllegalArgumentException("Invalid provider. Select provider from Provider enum. Example - Provider.ME");
        }
    }


    /**
     * This method download file from the link synchronously.
     * @param provider The file-hosting service from which you want to download file.
     * @param link The download link of the file.
     * @param download_directory The directory path where the file is being downloaded.
     * @return <code>boolean</code> - <code>true</code> if file is downloaded successfully, <code>false</code> otherwise.
     * @throws DriveException - If the provider is {@link Provider#DRIVE} & file size is bigger. (When drive cannot scan for virus for large files)
     */
    public static synchronized boolean download(Provider provider,String link,String download_directory) throws DriveException {
        try {
            URL url = new URL(getDownloadLink(provider, link));
            InputStream in = url.openStream();
            String filename = url.openConnection().getHeaderField("Content-Disposition");
            if (filename != null && filename.contains("filename=\"")){
                filename = filename.substring(filename.indexOf("filename=\"") + 10, filename.length() - 1);
            } else {
                String date = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(new Date());
                filename = "filescrapper_" + date;
            }
            File file = new File(download_directory,filename);
            if (!file.createNewFile()){
                int count = 0;
                for (String name : file.getParentFile().list()){
                    if (name.startsWith(filename))
                        count ++;
                }
                file = new File(file.getPath() + String.format(Locale.ENGLISH,"(%d)",count));
            }
            FileOutputStream out = new FileOutputStream(file);
            int read;
            byte[] bytes = new byte[1024];
            while ((read = in.read(bytes)) != -1){
                out.write(bytes,0,read);
            }
            out.flush();
            in.close();
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            return false;
        }
    }


    /**
     * This method download's the file in a background thread. Calling this is equivalent to calling {@link FileScrapper#download(Provider, String, String)} in a separate thread
     * @param provider The file-hosting service from which you want to download file.
     * @param link The download link of the file.
     * @param download_directory The directory path where the file is being downloaded.
     * @return <code>boolean</code> - <code>true</code> if file is downloaded successfully, <code>false</code> otherwise.
     */
    public static boolean downloadInBackground(Provider provider,String link,String download_directory) {
        new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(getDownloadLink(provider, link));
                    InputStream in = url.openStream();
                    String filename = url.openConnection().getHeaderField("Content-Disposition");
                    if (filename != null && filename.contains("filename=\"")){
                        filename = filename.substring(filename.indexOf("filename=\"") + 10, filename.length() - 1);
                    } else {
                        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(new Date());
                        filename = "filescrapper_" + date;
                    }
                    File file = new File(download_directory,filename);
                    if (!file.createNewFile()){
                        int count = 0;
                        for (String name : file.getParentFile().list()){
                            if (name.startsWith(filename))
                                count ++;
                        }
                        file = new File(file.getPath() + String.format(Locale.ENGLISH,"(%d)",count));
                    }
                    FileOutputStream out = new FileOutputStream(file);
                    int read;
                    byte[] bytes = new byte[1024];
                    while ((read = in.read(bytes)) != -1){
                        out.write(bytes,0,read);
                    }
                    out.flush();
                    in.close();
                    out.close();
                    Return = true;
                } catch (IOException | DriveException e) {
                    e.printStackTrace();
                    Return = false;
                }
            }
        }.start();
        return Return;
    }

    /**
     * This method download's the file synchronously in a separate thread. This method also shows an progress dialog while downloading the file. You don't need to put this in thread or async task
     * @param provider The file-hosting service from which you want to download file.
     * @param link The download link of the file.
     * @param download_directory The directory path where the file is being downloaded.
     * @return <code>boolean</code> - <code>true</code> if file is downloaded successfully, <code>false</code> otherwise.
     */
    public static synchronized boolean downloadWithProgress(Context context, Provider provider, String link, String download_directory) {
        Downloader downloader = new Downloader(context,provider);
        downloader.execute(link,download_directory);
        try {
            return downloader.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Checks the internet connection.
     * @param context The context of the activity.
     * @return <code>true</code> if internet is connected, <code>false</code> otherwise.
     */
    public static boolean isInternetOn(@NonNull Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    /**
     * This method's use android {@link DownloadManager} to download the file. This also shows the notification of the download.
     * @param context The context of the activity
     * @param provider The file-hosting service from which you want to download file.
     * @param link The download link of the file.
     * @param download_directory The directory path where the file is being downloaded.
     * @param title The title for downloading notification.
     * @param description The description for downloading notification.
     */
    public static void downloadWithNotification(@NonNull Context context, Provider provider, String link, String download_directory,String title,String description) throws DriveException {
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(getDownloadLink(provider, link)))
                .setDescription(title)
                .setTitle(description)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setVisibleInDownloadsUi(false)
                .setDestinationUri(Uri.fromFile(new File(download_directory)));
        manager.enqueue(request);
    }
}
