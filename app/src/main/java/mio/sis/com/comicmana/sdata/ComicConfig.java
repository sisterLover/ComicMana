package mio.sis.com.comicmana.sdata;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mio.sis.com.comicmana.sfile.ReadWritable;
import mio.sis.com.comicmana.sfile.SFile;

/**
 * Created by Administrator on 2018/1/13.
 * 儲存漫畫訊息的檔案，儲存於 [comic directory]/mana.cfg 中
 */

public class ComicConfig implements ReadWritable {
    public String name;
    public STime lastOpenTime;

    @Override
    public void WriteStream(DataOutputStream stream) throws IOException {
        SFile.WriteStringToStream(name, stream);
        lastOpenTime.WriteStream(stream);
    }

    @Override
    public void ReadStream(DataInputStream stream) throws IOException {
        name = SFile.ReadStringFromStream(stream);
        lastOpenTime.ReadStream(stream);
    }
}
