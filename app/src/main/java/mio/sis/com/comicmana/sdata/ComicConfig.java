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
    public ComicPosition lastPosition;
    public STime lastOpenTime;

    @Override
    public void WriteStream(DataOutputStream stream) throws IOException {
        SFile.WriteStringToStream(name, stream);
        lastPosition.WriteStream(stream);
        lastOpenTime.WriteStream(stream);
    }

    @Override
    public void ReadStream(DataInputStream stream) throws IOException {
        name = SFile.ReadStringFromStream(stream);
        lastPosition = new ComicPosition();
        lastPosition.ReadStream(stream);
        lastOpenTime = new STime();
        lastOpenTime.ReadStream(stream);
    }
}
