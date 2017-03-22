/*
 * Copyright (C) 2016 Shigehiro Soejima
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mightyfrog.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import java.lang.reflect.Field;

public class MarqueeToolbar extends Toolbar {
    private static final int MARQUEE_DELAY = 1200;

    private TextView mTitleTextView;
    private TextView mSubtitleTextView;

    private Runnable mTitleMarqueeRunner;
    private Runnable mSubtitleMarqueeRunner;

    private boolean mRunning;

    private int mDelay;
    private int mRepeatTitle;
    private int mRepeatSubtitle;
    private boolean mTitleMarqueeEnabled;
    private boolean mSubtitleMarqueeEnabled;

    public MarqueeToolbar(Context context) {
        this(context, null);
    }

    public MarqueeToolbar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarqueeToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray a =
                context.getTheme().obtainStyledAttributes(attrs, R.styleable.MarqueeToolbar, 0, 0);
        try {
            mDelay = a.getInt(R.styleable.MarqueeToolbar_startDelay, MARQUEE_DELAY);
            mRepeatTitle = a.getInt(R.styleable.MarqueeToolbar_titleRepeat, -1);
            mRepeatSubtitle = a.getInt(R.styleable.MarqueeToolbar_subtitleRepeat, -1);
            mTitleMarqueeEnabled = a.getBoolean(R.styleable.MarqueeToolbar_titleMarqueeEnabled, true);
            mSubtitleMarqueeEnabled = a.getBoolean(R.styleable.MarqueeToolbar_subtitleMarqueeEnabled, true);
            String titleText = a.getString(R.styleable.MarqueeToolbar_titleText);
            setTitle(titleText);
            String subtitleText = a.getString(R.styleable.MarqueeToolbar_subtitleText);
            setSubtitle(subtitleText);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        initTitleMarquee();
        initSubtitleMarquee();

        if (mTitleMarqueeEnabled) {
            mTitleMarqueeRunner = new Runnable() {
                @Override
                public void run() {
                    if (mTitleTextView != null) {
                        mTitleTextView.setSelected(true);
                    }
                }
            };
        }

        if (mSubtitleMarqueeEnabled) {
            mSubtitleMarqueeRunner = new Runnable() {
                @Override
                public void run() {
                    if (mSubtitleTextView != null)
                        mSubtitleTextView.setSelected(true);
                }
            };
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (!mRunning) {
            if (mTitleMarqueeEnabled && mTitleTextView != null) {
                mTitleTextView.postDelayed(mTitleMarqueeRunner, mDelay);
            }

            if (mSubtitleMarqueeEnabled && mSubtitleTextView != null) {
                mSubtitleTextView.postDelayed(mSubtitleMarqueeRunner, mDelay);
            }

            mRunning = true;
        }
    }

    /* PRIVATE METHODS */

    private void initTitleMarquee() {
        if (!mTitleMarqueeEnabled) {
            return;
        }

        try {
            Field field = Toolbar.class.getDeclaredField("mTitleTextView");
            field.setAccessible(true);
            mTitleTextView = (TextView) field.get(this);
            mTitleTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            mTitleTextView.setMarqueeRepeatLimit(mRepeatTitle);
        } catch (Exception e) {
            // NoSuchFieldException, IllegalAccessException shadowed
        }
    }

    private void initSubtitleMarquee() {
        if (!mSubtitleMarqueeEnabled) {
            return;
        }

        try {
            Field field = Toolbar.class.getDeclaredField("mSubtitleTextView");
            field.setAccessible(true);
            mSubtitleTextView = (TextView) field.get(this);
            mSubtitleTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            mSubtitleTextView.setMarqueeRepeatLimit(mRepeatSubtitle);
        } catch (Exception e) {
            // NoSuchFieldException, IllegalAccessException shadowed
        }
    }
}
