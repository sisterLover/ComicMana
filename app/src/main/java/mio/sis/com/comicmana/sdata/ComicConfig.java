package mio.sis.com.comicmana.sdata;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mio.sis.com.comicmana.sfile.SFile;

/**
 * Created by Administrator on 2017/12/24.
 */

public class ComicConfig {
    /*
        description for comic page
     */
    public String thumbnail;
    public int chapter, page;
    /*
        章節, 頁數
        only when chapter is 1, page is valid

        for ST_NET_EX, chapter always be 1
    */

    //  funtion
    void ReadFromFile(DataInputStream stream) throws IOException {
        thumbnail = SFile.ReadStringFromStream(stream);
        chapter = stream.readInt();
        page = stream.readInt();
    }
    void WriteFromFile(DataOutputStream stream) throws IOException {
        if(thumbnail==null) thumbnail = new String("");
        SFile.WriteStringToStream(thumbnail, stream);
        stream.writeInt(chapter);
        stream.writeInt(page);
    }
}
