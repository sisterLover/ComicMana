package mio.sis.com.comicmana.sdata;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import mio.sis.com.comicmana.sfile.LocalStorage;
import mio.sis.com.comicmana.sfile.ReadWritable;
import mio.sis.com.comicmana.sfile.SFile;
import mio.sis.com.comicmana.snet.inst.LocalComicSiteHelper;

/**
 * Created by Administrator on 2018/1/9.
 * 儲存 APP 設定的 class
 * 在本地 SDCard 中儲存於 ComicMana.cfg
 */

public class ManaConfig implements ReadWritable {
    static private Semaphore semaphore = new Semaphore(1);
    private void Lock() throws InterruptedException { semaphore.acquire(); }
    private void Unlock() { semaphore.release(); }

    public ArrayList<File> containterDirs;
    public File safeModeComicDir;


    public ManaConfig() {
        containterDirs = new ArrayList<>();
        safeModeComicDir = null;
    }

    public void LoadConfig() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                InnerLoadConfig();
            }
        }.start();
    }

    private void InnerLoadConfig() {
        try {
            Lock();
            File configFile = LocalStorage.GetConfigFile();
            if (configFile != null) {
                try {
                    FileInputStream fileInputStream = new FileInputStream(configFile);
                    DataInputStream dataInputStream = new DataInputStream(fileInputStream);
                    ReadStream(dataInputStream);
                    dataInputStream.close();
                    fileInputStream.close();
                } catch (Exception e) {

                }
            }
            Unlock();
        } catch (InterruptedException e) {

        }
        /*Log.d("LS_TAG", "ReadConfig with " + containterDirs.size() + " dirs");
        for(File file : containterDirs) {
            Log.d("LS_TAG", file.toString());
        }*/
        LocalComicSiteHelper.LoadComicDir();
    }
    public void SaveConfig() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                InnerSaveConfig();
            }
        }.start();
    }
    private void InnerSaveConfig() {
        try {
            Lock();
            File configFile = LocalStorage.GetConfigFile();
            if (configFile != null) {
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(configFile);
                    DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);
                    WriteStream(dataOutputStream);
                    dataOutputStream.close();
                    fileOutputStream.close();
                } catch (Exception e) {

                }
            }
            Unlock();
        } catch (InterruptedException e) {

        }
    }

    @Override
    public void WriteStream(DataOutputStream stream) throws IOException {
        if(containterDirs == null) {
            stream.writeInt(0);
            return;
        }
        stream.writeInt(containterDirs.size());
        for(File file : containterDirs) {
            SFile.WriteStringToStream(file.toString(), stream);
        }
        SFile.WriteStringToStream(safeModeComicDir.toString(), stream);
    }

    @Override
    public void ReadStream(DataInputStream stream) throws IOException {
        int size = stream.readInt();
        containterDirs.clear();
        for(int i=0;i<size;++i) {
            String string = SFile.ReadStringFromStream(stream);
            containterDirs.add(new File(string));
        }
        String string = SFile.ReadStringFromStream(stream);
        safeModeComicDir = new File(string);
    }
}
