package com.moviestan.app.util;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;


public class TextViewWithFont extends TextView {
//
//    private float spacing = Spacing.NORMAL;
//    private CharSequence originalText = "";

    private static Typeface mTypeface;

    public TextViewWithFont(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(mTypeface == null) {
            mTypeface = Typeface.createFromAsset(getResources().getAssets(), "fonts/Lobster.otf");
        }
        this.setTypeface(mTypeface);
    }

    public TextViewWithFont(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if(mTypeface == null) {
            mTypeface = Typeface.createFromAsset(getResources().getAssets(), "fonts/Lobster.otf");
        }
        this.setTypeface(mTypeface);
    }

    public TextViewWithFont(Context context) {
        super(context);
        if(mTypeface == null) {
            mTypeface = Typeface.createFromAsset(getResources().getAssets(), "fonts/Lobster.otf");
        }
        this.setTypeface(mTypeface);
    }
//
//    public float getSpacing() {
//        return this.spacing;
//    }
//
//    public void setSpacing(float spacing) {
//        this.spacing = spacing;
//        applySpacing();
//    }
//
//    @Override
//    public void setText(CharSequence text, BufferType type) {
//        originalText = text;
//        applySpacing();
//    }
//
//    @Override
//    public CharSequence getText() {
//        return originalText;
//    }
//
//    private void applySpacing() {
//        if (this == null || this.originalText == null) return;
//        StringBuilder builder = new StringBuilder();
//        for(int i = 0; i < originalText.length(); i++) {
//            builder.append(originalText.charAt(i));
//            if(i+1 < originalText.length()) {
//                builder.append("\u00A0");
//            }
//        }
//        SpannableString finalText = new SpannableString(builder.toString());
//        if(builder.toString().length() > 1) {
//            for(int i = 1; i < builder.toString().length(); i+=2) {
//                finalText.setSpan(new ScaleXSpan((spacing+1)/10), i, i+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            }
//        }
//        super.setText(finalText, BufferType.SPANNABLE);
//    }
//
//    public class Spacing {
//        public final static float NORMAL = 0;
//    }
}