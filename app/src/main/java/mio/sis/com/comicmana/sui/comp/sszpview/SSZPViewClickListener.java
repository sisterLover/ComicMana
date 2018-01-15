package mio.sis.com.comicmana.sui.comp.sszpview;

/**
 * Created by Administrator on 2018/1/13.
 */

public interface SSZPViewClickListener {
    /*
        click (x, y) while SSZPView have size (width, height)
     */
    void OnClick(int x, int y, int width, int height);
}
