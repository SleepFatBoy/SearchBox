package com.onooma.searchbox;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Phe
 * Created by onooma on 9/17/14.
 */
public class OnoomaSearchBoxView  extends LinearLayout {

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

        editText.setOnCancelListener(new OnoomaEditText.OnCancelListener() {
            @Override
            public void onCancel() {
                if (onSearchListener != null) {
                    editText.setText("");
                    onSearchListener.onCancel();
                }
            }
        });
        editText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!doSearch)
                    showKeyboard();
                else
                    doSearch = false;
            }
        });
        editText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    if (editText.getText().length() > 0)
                        addTag(String.valueOf(editText.getText()));
                    doSearch = true;
                    editText.setText("");

                    int count = container.getChildCount();
                    if (count > 0) {
                        String query = "";
                        for (int i = 0; i < count; i++) {
                            View view = container.getChildAt(i);
                            query += view.getTag() + " ";
                        }
                        query = query.trim();
                        hideKeyboard();
                        if(onSearchListener != null)
                            onSearchListener.onSearch(query);
                    }
                }
                return false;
            }
        });
    }

    public void addTag(String text) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View tag = inflater.inflate(R.layout.onooma_search_box_tag_layout, null);
        tag.setRotationY(180);
        TextView textView = (TextView) tag.findViewById(R.id.onooma_search_box_tag_layout_text_view);
        if(typeface != null)
            textView.setTypeface(typeface);
        textView.setText(text);
        ImageView imageView = (ImageView) tag.findViewById(R.id.onooma_search_box_tag_layout_delete_image_view);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                container.removeView(tag);
                editText.performClick();
            }
        });
        tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.performClick();
            }
        });
        tag.setTag(text);
        container.addView(tag);
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
    }
}
