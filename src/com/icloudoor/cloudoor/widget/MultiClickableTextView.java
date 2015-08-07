package com.icloudoor.cloudoor.widget;

import android.content.Context;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;


/**
 * 允许多个可点击Span的自定义TextView
 * Created by Derrick Guan on 8/5/15.
 */
public class MultiClickableTextView extends TextView {

    private IOnSpanClickListener mListener;


    public MultiClickableTextView(Context context) {
        super(context);
        init();
    }

    public MultiClickableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MultiClickableTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void setTextHTML(String html) {
        CharSequence sequence = Html.fromHtml(html);
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
        URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
        for (URLSpan span : urls) {
            makeLinkClickable(strBuilder, span);
        }
        setText(strBuilder);
    }

    protected void makeLinkClickable(SpannableStringBuilder strBuilder, final URLSpan span) {
        int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onSpanClick(span.getURL());
                }
            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }

    public void setIOnSpanClickListener(IOnSpanClickListener l) {
        mListener = l;
    }

    public interface IOnSpanClickListener {
        public void onSpanClick(String url);
    }
}
