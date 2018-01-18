package mio.sis.com.comicmana.sdata;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import mio.sis.com.comicmana.sfile.LocalStorage;
import mio.sis.com.comicmana.sfile.ReadWritable;
import mio.sis.com.comicmana.sfile.SFile;

/**
 * Created by Administrator on 2018/1/9.
 * 儲存 APP 設定的 class
 * 在本地 SDCard 中儲存於 ComicMana.cfg
 */

public class ManaConfig implements ReadWritable {
    public ArrayList<File> containterDirs;


    public ManaConfig() {
        containterDirs = new ArrayList<>();
    }
    public void LoadConfig() {
        try {
            File configFile = LocalStorage.GetConfigFile();
            if (configFile != null) {
                FileInputStream fileInputStream = new FileInputStream(configFile);
                DataInputStream dataInputStream = new DataInputStream(fileInputStream);
                ReadStream(dataInputStream);
                dataInputStream.close();
                fileInputStream.close();
            }
        } catch (Exception e) {

        }
    }
    public void SaveConfig() {
        try {
            Log.d("LS_TAG", "Try writing config");
            File configFile = LocalStorage.GetConfigFile();
            if (configFile != null) {
                FileOutputStream fileOutputStream = new FileOutputStream(configFile);
                DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);
                WriteStream(dataOutputStream);
                dataOutputStream.close();
                fileOutputStream.close();
            }
        } catch (Exception e) {
            Log.d("LS_TAG", "Write fail");
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
    }

    @Override
    public void ReadStream(DataInputStream stream) throws IOException {
        int size = stream.readInt();
        containterDirs.clear();
        for(int i=0;i<size;++i) {
            String string = SFile.ReadStringFromStream(stream);
            containterDirs.add(new File(string));
        }
    }
}
