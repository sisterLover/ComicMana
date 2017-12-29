package mio.sis.com.comicmana.sui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatTextView;

/**
 * Created by Administrator on 2017/12/29.
 */

public class HintText extends AppCompatTextView {
    static public HintText STANDARD_HINT = null;

    public HintText(Context context) {
        super(context);
        Initialize();
    }
    public HintText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Initialize();
    }
    public HintText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Initialize();
    }
    void Initialize() {
        STANDARD_HINT = this;
    }

    public void UpdateText(final String string) {
        post(new Runnable() {
            @Override
            public void run() {
                setText(string);
            }
        });
    }
}
