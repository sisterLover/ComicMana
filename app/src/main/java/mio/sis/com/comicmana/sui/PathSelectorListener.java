package mio.sis.com.comicmana.sui;

import java.io.File;

/**
 * Created by Administrator on 2017/12/24.
 */

public interface PathSelectorListener {
    void OnPathSelect(File file);
    void OnCancel();
}
