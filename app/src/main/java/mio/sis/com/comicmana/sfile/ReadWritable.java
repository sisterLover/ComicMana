package mio.sis.com.comicmana.sfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2018/1/9.
 */

public interface ReadWritable {
    void WriteStream(DataOutputStream stream) throws IOException;
    void ReadStream(DataInputStream stream) throws IOException;
}
