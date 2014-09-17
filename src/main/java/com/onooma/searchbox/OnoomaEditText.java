package com.onooma.searchbox;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

/**
 * Phe
 * Created by onooma on 9/17/14.
 */
class OnoomaEditText extends EditText {

    private OnCancelListener onCancelListener;

    public void setOnCancelListener(OnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
    }

    public OnoomaEditText(Context context) {
        super(context);
        if(!isInEditMode())
            init(context);
    }

    public OnoomaEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(!isInEditMode())
            init(context);
    }

    public OnoomaEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if(!isInEditMode())
            init(context);
    }

    private void init(Context context){}

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK)
            if(onCancelListener != null)
                onCancelListener.onCancel();
        return super.onKeyPreIme(keyCode, event);
    }

    public interface OnCancelListener {
        public void onCancel();
    }
}
