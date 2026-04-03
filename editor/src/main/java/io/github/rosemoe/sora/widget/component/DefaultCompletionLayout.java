/*
 *    sora-editor - the awesome code editor for Android
 *    https://github.com/Rosemoe/sora-editor
 *    Copyright (C) 2020-2024  Rosemoe
 *
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License, or (at your option) any later version.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 *     USA
 *
 *     Please contact Rosemoe by email 2073412493@qq.com if you need
 *     additional information or have any questions
 */
package io.github.rosemoe.sora.widget.component;

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.Outline;
import android.graphics.drawable.GradientDrawable;
import android.os.SystemClock;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;

public class DefaultCompletionLayout implements CompletionLayout {

    private ListView listView;
    private ProgressBar progressBar;
    private LinearLayout rootView;
    private EditorAutoCompletion editorAutoCompletion;

    @Override
    public void setEditorCompletion(@NonNull EditorAutoCompletion completion) {
        editorAutoCompletion = completion;
    }

    @Override
    public void setEnabledAnimation(boolean enabledAnimation) {
        if (enabledAnimation) {
            var transition = new LayoutTransition();
            transition.enableTransitionType(LayoutTransition.CHANGING);
            transition.enableTransitionType(LayoutTransition.APPEARING);
            transition.enableTransitionType(LayoutTransition.DISAPPEARING);
            transition.enableTransitionType(LayoutTransition.CHANGE_APPEARING);
            transition.enableTransitionType(LayoutTransition.CHANGE_DISAPPEARING);
            transition.addTransitionListener(new LayoutTransition.TransitionListener() {
                @Override
                public void startTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {

                }

                @Override
                public void endTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
                    if (view != listView) {
                        return;
                    }
                    view.requestLayout();
                }
            });
            rootView.setLayoutTransition(transition);
            listView.setLayoutTransition(transition);
        } else {
            rootView.setLayoutTransition(null);
            listView.setLayoutTransition(null);
        }
    }

    @NonNull
    @Override
    public View inflate(@NonNull Context context) {
        var rootLayout = new LinearLayout(context);
        rootView = rootLayout;
        listView = new ListView(context);
        progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);

        rootLayout.setOrientation(LinearLayout.VERTICAL);

        setEnabledAnimation(false);

        rootLayout.addView(progressBar, new LinearLayout.LayoutParams(-1, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, context.getResources().getDisplayMetrics())));
        rootLayout.addView(listView, new LinearLayout.LayoutParams(-1, -1));

        progressBar.setIndeterminate(true);
        var progressBarLayoutParams = (LinearLayout.LayoutParams) progressBar.getLayoutParams();

        progressBarLayoutParams.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -8, context.getResources().getDisplayMetrics());
        progressBarLayoutParams.bottomMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -8, context.getResources().getDisplayMetrics());
        progressBarLayoutParams.leftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, context.getResources().getDisplayMetrics());
        progressBarLayoutParams.rightMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, context.getResources().getDisplayMetrics());

        // 创建与 EditorTextActionWindow 一样的 GradientDrawable
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.RECTANGLE);
        // 圆角半径12dp
        gd.setCornerRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, context.getResources().getDisplayMetrics()));

        // 初始背景颜色（稍后在onApplyColorScheme中更新）
        gd.setColor(0xFFFFFFFF); // 临时白色背景

        // 初始边框（稍后在onApplyColorScheme中更新）
        int strokeWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, context.getResources().getDisplayMetrics());
        gd.setStroke(strokeWidth, 0xFFCCCCCC); // 临时灰色边框

        rootLayout.setBackground(gd);

        // 设置圆角裁剪
        setRootViewOutlineProvider(rootView);

        // 添加分割线（1像素高度）
        listView.setDividerHeight(1);
        setLoading(true);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            try {
                editorAutoCompletion.select(position);
            } catch (Exception e) {
                e.printStackTrace(System.err);
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        return rootLayout;
    }

    @Override
    public void onApplyColorScheme(@NonNull EditorColorScheme colorScheme) {
        if (editorAutoCompletion == null || editorAutoCompletion.getEditor() == null) {
            return;
        }

        CodeEditor editor = editorAutoCompletion.getEditor();

        // 创建与 EditorTextActionWindow 完全一样的 GradientDrawable
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.RECTANGLE);
        // 圆角半径8dp
        gd.setCornerRadius(8 * editor.getDpUnit());

        // 背景颜色
        gd.setColor(colorScheme.getColor(EditorColorScheme.TEXT_ACTION_WINDOW_BACKGROUND));

        // 添加边框 - 使用与文本操作框相同的边框颜色
        int strokeWidth = (int) (1 * editor.getDpUnit());
        int strokeColor = colorScheme.getColor(EditorColorScheme.TEXT_ACTION_WINDOW_STROKE_COLOR);
        gd.setStroke(strokeWidth, strokeColor);

        rootView.setBackground(gd);

        // 更新圆角裁剪，确保与背景圆角一致
        setRootViewOutlineProvider(rootView);

        // 设置分割线颜色
        if (listView != null) {
            // 使用文本次要颜色作为分割线颜色，或者创建一个新的颜色定义
            int dividerColor = colorScheme.getColor(EditorColorScheme.COMPLETION_WND_TEXT_SECONDARY);
            // 设置透明度为0.2，使分割线更柔和
            dividerColor = (dividerColor & 0x00FFFFFF) | 0x20000000;
            listView.setDivider(new android.graphics.drawable.ColorDrawable(dividerColor));
            // 设置分割线高度为1dp
            listView.setDividerHeight((int) (1 * editor.getDpUnit()));
        }
    }

    @Override
    public void setLoading(boolean state) {
        progressBar.setVisibility(state ? View.VISIBLE : View.GONE);
    }

    @NonNull
    @Override
    public ListView getCompletionList() {
        return listView;
    }

    /**
     * Perform motion events
     */
    private void performScrollList(int offset) {
        var adpView = getCompletionList();

        long down = SystemClock.uptimeMillis();
        var ev = MotionEvent.obtain(down, down, MotionEvent.ACTION_DOWN, 0, 0, 0);
        adpView.onTouchEvent(ev);
        ev.recycle();

        ev = MotionEvent.obtain(down, down, MotionEvent.ACTION_MOVE, 0, offset, 0);
        adpView.onTouchEvent(ev);
        ev.recycle();

        ev = MotionEvent.obtain(down, down, MotionEvent.ACTION_CANCEL, 0, offset, 0);
        adpView.onTouchEvent(ev);
        ev.recycle();
    }

    private void setRootViewOutlineProvider(View rootView) {
        if (editorAutoCompletion == null || editorAutoCompletion.getEditor() == null) {
            return;
        }

        CodeEditor editor = editorAutoCompletion.getEditor();

        rootView.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                // 设置与GradientDrawable完全一致的圆角轮廓
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(),
                        8 * editor.getDpUnit());
            }
        });
        rootView.setClipToOutline(true);
    }

    @Override
    public void ensureListPositionVisible(int position, int increment) {
        listView.post(() -> {
            // Used for reset scroll position
            if (position == 0 && increment == 0) {
                listView.setSelectionFromTop(0, 0);
                return;
            }
            while (listView.getFirstVisiblePosition() + 1 > position && listView.canScrollList(-1)) {
                performScrollList(increment / 2);
            }
            while (listView.getLastVisiblePosition() - 1 < position && listView.canScrollList(1)) {
                performScrollList(-increment / 2);
            }
        });
    }
}