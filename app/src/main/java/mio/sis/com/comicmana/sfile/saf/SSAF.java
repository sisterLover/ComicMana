package mio.sis.com.comicmana.sfile.saf;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.provider.DocumentFile;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Semaphore;

import mio.sis.com.comicmana.MainActivity;
import mio.sis.com.comicmana.other.SChar;
import mio.sis.com.comicmana.sfile.LocalStorage;
import mio.sis.com.comicmana.sfile.ReadWritable;
import mio.sis.com.comicmana.sfile.SFile;

/**
 * Created by Administrator on 2018/1/18.
 */

public class SSAF implements ReadWritable {
    private String BASE_SDCARD;
    private ArrayList<UriPair> availableSDCard;
    private Semaphore requestSemephore;

    void Lock() throws InterruptedException { requestSemephore.acquire(); }
    void Unlock() { requestSemephore.release(); }

    public SSAF() {
        BASE_SDCARD = Environment.getExternalStorageDirectory().toString();

        availableSDCard = new ArrayList<>();
        requestSemephore = new Semaphore(1);
    }
    /*
        return null when fail
     */
    public OutputStream OpenOutputStream(File file) throws FileNotFoundException {
        String sdcardPath = GetSDCardPath(file);
        if (sdcardPath == null) return null;
        //  內部儲存直接打開
        if (sdcardPath.compareToIgnoreCase(BASE_SDCARD) == 0) {
            return new FileOutputStream(file);
        }
        DocumentFile documentFile = GetDocumentFile(file);
        if(documentFile == null) return null;
        return MainActivity.MAIN_ACTIVITY.getContentResolver().openOutputStream(documentFile.getUri());
    }

    public boolean Delete(File file) {
        String sdcardPath = GetSDCardPath(file);
        if (sdcardPath == null) return false;
        //  內部儲存直接打開
        if (sdcardPath.compareToIgnoreCase(BASE_SDCARD) == 0) {
            return file.delete();
        }
        DocumentFile documentFile = GetDocumentFile(file);
        if(documentFile == null) return false;
        return documentFile.delete();
    }

    public boolean Rename(File src, File dest) {
        String[] sdcardPath = {GetSDCardPath(src), GetSDCardPath(dest)};
        if (sdcardPath[0] == null || sdcardPath[1] == null) return false;
        //  此程式用不到不同磁區的 rename
        if (sdcardPath[0].compareToIgnoreCase(sdcardPath[1]) != 0) return false;
        if (sdcardPath[0].compareToIgnoreCase(BASE_SDCARD) == 0) {
            //  內部儲存直接 rename
            return src.renameTo(dest);
        }
        DocumentFile documentFile = GetDocumentFile(src);
        if(documentFile == null) return false;
        return documentFile.renameTo(dest.getName());
    }

    private DocumentFile GetDocumentFile(File file) {
        String sdcardPath = GetSDCardPath(file);
        if(sdcardPath == null) return null;
        Uri uri = null;
        for(UriPair uriPair : availableSDCard) {
            if(sdcardPath.compareToIgnoreCase(uriPair.sdcardPath)==0) {
                uri = uriPair.uri;
                break;
            }
        }
        if(uri == null) return null;
        DocumentFile cur = DocumentFile.fromTreeUri(MainActivity.MAIN_ACTIVITY, uri),
                next = null;
        if(cur == null) return null;

        ArrayList<String> pathStack = new ArrayList<>();
        while(file.toString().compareToIgnoreCase(sdcardPath)!=0) {
            pathStack.add(file.getName());
            file = file.getParentFile();
        }
        Collections.reverse(pathStack);
        for(int i=0;i<pathStack.size();++i) {
            //Log.d("LS_TAG","Append " + pathStack.get(i));
            next = cur.findFile(pathStack.get(i));
            if(next != null) {
                //Log.d("LS_TAG", "Append found");
                cur = next;
                continue;
            }
            //Log.d("LS_TAG", "Append create");
            if(i == pathStack.size() - 1) {
                cur = cur.createFile("model/example", pathStack.get(i));
                /*String ext = SFile.GetExtension(pathStack.get(i));
                if(ext == null || ext.compareToIgnoreCase("cfg")==0) {
                    Log.d("LS_TAG", "cfg or null detected");
                    cur = cur.createFile("model/example", pathStack.get(i));
                }
                else {
                    cur = cur.createFile("image/png", pathStack.get(i));
                }*/
            }
            else {
                cur = cur.createDirectory(pathStack.get(i));
            }
            if(cur == null) {
                //Log.d("LS_TAG", "Append fail");
                return null;
            }
        }
        return cur;
    }

    private String GetSDCardPath(File file) {
        String fileString = file.toString();
        if(fileString.compareToIgnoreCase(BASE_SDCARD)==0) return fileString;
        if(fileString.compareToIgnoreCase("/")==0) return null;
        File curFile = file, lastFile = file;
        while(true) {
            lastFile = curFile;
            curFile = curFile.getParentFile();
            if (curFile == null) break;
            fileString = curFile.toString();
            if (fileString.compareToIgnoreCase(BASE_SDCARD) == 0) return fileString;
            if (fileString.compareToIgnoreCase("/storage") == 0) return lastFile.toString();
            if (fileString.compareToIgnoreCase("/") == 0) break;
        }
        return null;
    }
    public boolean HavePermission(File file) {
        String sdcardPath = GetSDCardPath(file);
        //Log.d("LS_TAG", "Asking Permission for " + file.toString());
        if(sdcardPath == null) return false;
        //Log.d("LS_TAG", "SDCardPath = "+ sdcardPath);
        return IsSDCardAvaibale(sdcardPath);
    }
    public void RequestPermission() {
        try {
            Lock();
            MainActivity.SetRequestFlag();
            //  send request
            MainActivity.MAIN_ACTIVITY.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MainActivity.MAIN_ACTIVITY.startActivityForResult(
                            new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE),
                            MainActivity.SDCARD_REQUEST);
                }
            });

            while(MainActivity.TestRequestFlag()) {
                SystemClock.sleep(200);
            }
            Unlock();
        }
        catch (InterruptedException e) {
        }
    }
    private String SDStringToUriString(String sdString) {
        return "content://com.android.externalstorage.documents/tree/" + sdString + "%3A";
    }
    public void PushPermission(Uri uri) {
        ArrayList<File> sdcardDirs = SFile.GetSDCardDirs();
        sdcardDirs.remove(0);   //  don't care about /sdcard
        //Log.d("LS_TAG", "Uri income : " + uri.toString());
        for(File file : sdcardDirs) {
            //Log.d("LS_TAG", "Compare with " + SDStringToUriString(file.getName()));

            if(uri.toString().compareToIgnoreCase(SDStringToUriString(file.getName()))==0) {
                if(!IsSDCardAvaibale(file.toString())) {
                    UriPair uriPair = new UriPair();
                    uriPair.sdcardPath = file.toString();
                    uriPair.uri = uri;
                    availableSDCard.add(uriPair);
                    SaveSSAF();
                }
            }
        }
    }
    private boolean IsSDCardAvaibale(String sdcardPath) {
        if(sdcardPath.compareToIgnoreCase(BASE_SDCARD) == 0) return true;
        for (UriPair uriPair : availableSDCard) {
            if (sdcardPath.compareToIgnoreCase(uriPair.sdcardPath) == 0) return true;
        }
        return false;
    }

    private class UriPair {
        String sdcardPath;
        Uri uri;
    }

    public void LoadSSAF() {
        /*
        new Thread() {
            @Override
            public void run() {
                super.run();
                InnerLoadSSAF();
            }
        }.start();
        */
    }
    private void InnerLoadSSAF() {
        try {
            Lock();
            File SSAFFile = LocalStorage.GetSSAFFile();
            if (SSAFFile != null) {
                try {
                    FileInputStream fileInputStream = new FileInputStream(SSAFFile);
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
    public void SaveSSAF() {
        /*
        new Thread() {
            @Override
            public void run() {
                super.run();
                InnerSaveSSAF();
            }
        }.start();
        */
    }
    private void InnerSaveSSAF() {
        try {
            Lock();
            File SSAFFile = LocalStorage.GetSSAFFile();
            if (SSAFFile != null) {
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(SSAFFile);
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
        /*stream.writeInt(availableSDCard.size());
        for(String string : availableSDCard) {
            SFile.WriteStringToStream(string, stream);
        }*/
    }

    @Override
    public void ReadStream(DataInputStream stream) throws IOException {
        /*int size = stream.readInt();
        for(int i=0;i<size;++i) {
            String path = SFile.ReadStringFromStream(stream);
            availableSDCard.add(path);
        }*/
    }
}
