package com.jsoup.filescrapper;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.jsoup.filescrapper.FileScrapper.getDownloadLink;

public class Downloader extends AsyncTask<String, Integer,Boolean> {

    private final WeakReference<Context> context;
    private final Provider provider;
    private ProgressDialog dialog;

    protected Downloader(Context context, Provider provider){
        this.context = new WeakReference<>(context);
        this.provider = provider;
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(context.get());
        dialog.setTitle("Downloading");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setProgressPercentFormat(NumberFormat.getPercentInstance());
        dialog.show();
    }

    @Override
    protected Boolean doInBackground(String... string) {
        try {
            String link = string[0];
            String filepath = string[1];
            URL url = new URL(getDownloadLink(provider,link));
            int size = url.openConnection().getContentLength();
            InputStream in = url.openStream();
            String filename = url.openConnection().getHeaderField("Content-Disposition");
            if (filename != null && filename.contains("filename=\"")){
                filename = filename.substring(filename.indexOf("filename=\"") + 10, filename.length() - 1);
            } else {
                String date = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(new Date());
                filename = "filescrapper_" + date;
            }
            File file = new File(filename,filename);
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
            int progress = 0;
            while ((read = in.read(bytes)) != -1){
                out.write(bytes,0,read);
                progress += read;
                publishProgress(progress * 100 / size);
            }
            out.flush();
            in.close();
            out.close();
            dialog.dismiss();
            return true;
        } catch (IOException | DriveException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        int progress = values[0];
        dialog.setProgress(progress);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        dialog.dismiss();
    }
}
