package com.onooma.searchbox;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Phe
 * Created by onooma on 9/17/14.
 */
public class OnoomaSearchBoxView  extends LinearLayout {

    private static final String TAG = OnoomaSearchBoxView.class.getSimpleName();
    private LinearLayout container;
    private OnoomaEditText editText;
    private Typeface typeface = null;

    private OnSearchListener onSearchListener;

    private volatile boolean doSearch = false;

    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
        if(editText != null)
            editText.setTypeface(typeface);
    }

    public void setOnSearchListener(OnSearchListener onSearchListener) {
        this.onSearchListener = onSearchListener;
    }

    public OnoomaSearchBoxView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if(!isInEditMode())
            init(context);
    }

    public OnoomaSearchBoxView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(!isInEditMode())
            init(context);
    }

    public OnoomaSearchBoxView(Context context) {
        this(context, null);
        if(!isInEditMode())
            init(context);
    }

    private void init(Context context){
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.onooma_search_box_layout, this, true);

        RelativeLayout view = (RelativeLayout) getChildAt(0);
        container = (LinearLayout) view.getChildAt(0);
        editText = (OnoomaEditText) view.getChildAt(1);

        container.setRotationY(180);

        if(typeface != null)
            editText.setTypeface(typeface);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "afterTextChanged");
                if (onSearchListener != null)
                    onSearchListener.onText(s.toString());
            }
        });
        editText.setOnCancelListener(new OnoomaEditText.OnCancelListener() {
            @Override
            public void onCancel() {
                Log.d(TAG, "setOnCancelListener");
                editText.setText("");
                editText.clearFocus();
            }
        });
        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d(TAG, "onFocusChange " + hasFocus);
                if(hasFocus){
                    showKeyboard();
                    if (onSearchListener != null)
                        onSearchListener.onStart();
                }else {
                    hideKeyboard();
                    if(!doSearch) {
                        if (onSearchListener != null)
                            onSearchListener.onCancel();
                    }else{
                        doSearch = false;
                        String query = getQuery();
                        if(onSearchListener != null)
                            onSearchListener.onSearch(query);
                    }
                }
            }
        });
        editText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick");
                editText.requestFocus();
            }
        });
        editText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.d(TAG, "onKey " + keyCode + " ,KeyEvent " + event);
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    if (editText.getText().length() > 0)
                        addTag(String.valueOf(editText.getText()));
                    editText.setText("");
                    doSearch = true;
                    editText.clearFocus();
                }
                return false;
            }
        });
    }

    public void addTag(String text) {
        text = text.trim();
        if(text.length() == 0)
            return;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View tag = inflater.inflate(R.layout.onooma_search_box_tag_layout, null);
        tag.setRotationY(180);
        TextView textView = (TextView) tag.findViewById(R.id.onooma_search_box_tag_layout_text_view);
        if(typeface != null)
            textView.setTypeface(typeface);
        textView.setText(text);
//        ImageView imageView = (ImageView) tag.findViewById(R.id.onooma_search_box_tag_layout_delete_image_view);
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                container.removeView(tag);
//                editText.performClick();
//            }
//        });
        tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                container.removeView(tag);
                editText.performClick();
            }
        });
        tag.setTag(text);
        container.addView(tag);
    }

    public String getQuery(){
        String query = "";

        int count = container.getChildCount();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                View view = container.getChildAt(i);
                query += view.getTag() + " ";
            }
            query = query.trim();
        }

        return query;
    }

    public void doSearch(String text){
        text = text.trim();
        if(text.length() > 0) {
            addTag(text);
            editText.setText("");
            doSearch = true;
            editText.clearFocus();
        }
    }

    public void start(){
        editText.requestFocus();
    }

    public void clear(){
        container.removeAllViewsInLayout();
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void showKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    public interface OnSearchListener {
        public void onSearch(String query);
        public void onCancel();
        public void onStart();
        public void onText(String query);
    }
}
