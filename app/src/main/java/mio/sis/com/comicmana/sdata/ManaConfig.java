package mio.sis.com.comicmana.sdata;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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
