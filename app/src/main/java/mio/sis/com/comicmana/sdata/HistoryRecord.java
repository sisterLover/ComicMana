package mio.sis.com.comicmana.sdata;

import android.os.SystemClock;

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

/**
 * Created by Administrator on 2018/1/18.
 */

public class HistoryRecord implements ReadWritable {
    static private final int MAX_RECORD = 200;
    static private Semaphore semaphore = new Semaphore(1);
    static private void Lock() throws InterruptedException { semaphore.acquire(); }
    static private void Unlock() { semaphore.release(); }

    private ArrayList<ComicSrc> comicSrcs;
    private boolean initialLoad;

    public HistoryRecord() {
        comicSrcs = new ArrayList<>();
        initialLoad = false;
    }

    public void PushRecord(ComicSrc comicSrc) {
        if(comicSrc.srcType != ComicSrc.SrcType.ST_LOCAL_FILE) return;
        boolean finished = false;
        try {
            while(!initialLoad) SystemClock.sleep(100);
            Lock();
            for(int i=0;i<comicSrcs.size();++i) {
                if(comicSrcs.get(i).Equal(comicSrc)) {
                    comicSrcs.remove(i);
                    comicSrcs.add(comicSrc);
                    finished = true;
                    break;
                }
            }
            if(!finished) {
                if(comicSrcs.size() < MAX_RECORD) {
                    comicSrcs.add(comicSrc);
                }
                else {
                    comicSrcs.set(0, comicSrc);
                }
            }
            Unlock();
        }
        catch (InterruptedException e) {
            return;
        }
        SaveRecord();
    }
    public ComicSrc[] GetHistory() {
        ComicSrc[] result = null;
        try {
            while(!initialLoad) SystemClock.sleep(100);
            Lock();
            int size = comicSrcs.size();
            if(size != 0) {
                result = new ComicSrc[size];
                for (int i = 0; i < size; ++i) {
                    result[i] = comicSrcs.get(size - i - 1);
                }
            }
            Unlock();
        }
        catch (InterruptedException e) {
            return null;
        }
        return result;
    }

    public void LoadRecord() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                InnerLoadRecrod();
            }
        }.start();
    }
    private void InnerLoadRecrod() {
        try {
            Lock();
            initialLoad = true;
            File historyFile = LocalStorage.GetHistoryFile();
            if (historyFile != null) {
                try {
                    FileInputStream fileInputStream = new FileInputStream(historyFile);
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
    }
    public void SaveRecord() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                InnerSaveRecord();
            }
        }.start();
    }
    private void InnerSaveRecord() {
        try {
            Lock();
            File historyFile = LocalStorage.GetHistoryFile();
            if (historyFile != null) {
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(historyFile);
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
        stream.writeInt(comicSrcs.size());
        for(int i=0;i<comicSrcs.size();++i) {
            comicSrcs.get(i).WriteStream(stream);
        }
    }

    @Override
    public void ReadStream(DataInputStream stream) throws IOException {
        int size = stream.readInt();
        comicSrcs.clear();
        for(int i=0;i<size;++i) {
            ComicSrc comicSrc = new ComicSrc();
            comicSrc.ReadStream(stream);
            comicSrcs.add(comicSrc);
        }
    }
}
