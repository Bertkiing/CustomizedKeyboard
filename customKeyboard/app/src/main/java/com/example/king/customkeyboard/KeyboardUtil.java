package com.example.king.customkeyboard;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.text.Editable;
import android.text.InputType;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Administrator on 2016/11/24.
 */
public class KeyboardUtil implements View.OnClickListener {
    private Activity mActivity;
    private KeyboardView keyboardView;
    private Keyboard numberKeyBoard;//数字键盘
    private EditText editText;
    private View viewContainer;

    public KeyboardUtil(Activity activity) {
        this.mActivity = activity;
        numberKeyBoard = new Keyboard(activity, R.xml.layout_numbers_key);
    }

    private KeyboardView.OnKeyboardActionListener listener = new KeyboardView.OnKeyboardActionListener() {
        @Override
        public void onPress(int primaryCode) {

        }

        @Override
        public void onRelease(int primaryCode) {

        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            keyboardAction(primaryCode, editText);
        }

        @Override
        public void onText(CharSequence text) {
        }

        @Override
        public void swipeLeft() {

        }

        @Override
        public void swipeRight() {

        }

        @Override
        public void swipeDown() {

        }

        @Override
        public void swipeUp() {

        }
    };

    /**
     * 处理回退键，完成键等
     * @param primaryCode
     */
    private void keyboardAction(int primaryCode, EditText editText) {
        Editable editable = editText.getText();
        int start = editText.getSelectionStart();
        View currentFocus = mActivity.getWindow().getCurrentFocus();
//        if(currentFocus==null || currentFocus.getClass()!= EditText.class) return;

//        EditText editText =(EditText) currentFocus;
//        Editable editable =editText.getText();
//        int start = editText.getSelectionStart();


        if (primaryCode == Keyboard.KEYCODE_DONE) {
            hideKeyboard();
        } else if (primaryCode == Keyboard.KEYCODE_DELETE) {//回退
            if (editText.hasFocus()) {
                if (editable != null && start > 0) {
                    editable.delete(start - 1, start);
                }
            }

        } else {
            editable.insert(start, Character.toString((char) primaryCode));
        }
    }


    /**
     * 显示键盘
     */
    public void showKeyboard() {


        if (viewContainer == null) {
            viewContainer = mActivity.getLayoutInflater().inflate(R.layout.keyboard_layout, null);
        } else {
            if (viewContainer.getParent() != null){
                ((ViewGroup)viewContainer.getParent()).removeView(viewContainer);
            }
        }

        FrameLayout frameLayout = (FrameLayout) mActivity.getWindow().getDecorView();
        frameLayout.setFitsSystemWindows(true);

        KeyboardView keyboardView = (KeyboardView) viewContainer.findViewById(R.id.keyboardView);
        LinearLayout history = (LinearLayout) viewContainer.findViewById(R.id.ly_history_record);
        TextView record_1 = (TextView) history.findViewById(R.id.record_1);
        TextView record_2 = (TextView) history.findViewById(R.id.record_2);
        TextView record_3 = (TextView) history.findViewById(R.id.record_3);
        ImageButton btn_record = (ImageButton) history.findViewById(R.id.btn_record);
        ImageButton btn_measure = (ImageButton) viewContainer.findViewById(R.id.btn_measure);
        ImageButton btn_done = (ImageButton) viewContainer.findViewById(R.id.btn_done);

        record_1.setOnClickListener(this);
        record_2.setOnClickListener(this);
        record_3.setOnClickListener(this);
        btn_record.setOnClickListener(this);
        btn_measure.setOnClickListener(this);
        btn_done.setOnClickListener(this);

        //激光测量按钮

        this.keyboardView = keyboardView;
        this.keyboardView.setKeyboard(numberKeyBoard);
        this.keyboardView.setEnabled(true);
        this.keyboardView.setPreviewEnabled(false);
        this.keyboardView.setOnKeyboardActionListener(listener);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        Point navigationBarSize = getNavigationBarSize(mActivity);
        lp.bottomMargin = navigationBarSize.y;
        lp.gravity = Gravity.BOTTOM;
        frameLayout.addView(viewContainer, lp);
        viewContainer.setAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.down2up));


    }

    /**
     * 隐藏键盘
     */
    public void hideKeyboard() {
        if (viewContainer != null && viewContainer.getParent() != null) {
            ((ViewGroup) viewContainer.getParent()).removeView(viewContainer);
            viewContainer.setVisibility(View.GONE);
            viewContainer=null;
        }
    }

    public boolean isShowing() {
        if (viewContainer == null)
            return false;
        return viewContainer.getVisibility() == View.VISIBLE;
    }


    /**
     * 隐藏系统键盘
     *
     * @param editText
     */
    public static void hideSystemSoftKeyboard(EditText editText) {
        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= 11) {
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus;
                setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(editText, false);

            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            editText.setInputType(InputType.TYPE_NULL);
        }
    }


    public void attachTo(EditText editText, boolean isAuto) {
        this.editText = editText;
        hideSystemSoftKeyboard(this.editText);
        setAutoShowOnFocs(isAuto);
    }


    public void registerEditText(int resId){
       EditText editText = (EditText) mActivity.findViewById(resId);

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    showKeyboard();
                }else {
                    hideKeyboard();
                }
            }
        });

        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                EditText editText1 = (EditText) v;
                int inputType = editText1.getInputType();
                editText1.setInputType(InputType.TYPE_NULL);
                editText1.onTouchEvent(event);
                editText1.setInputType(inputType);

                return true;
            }
        });

        // Disable spell check (hex strings look like words to Android)
        editText.setInputType( editText.getInputType() | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS );
    }




    public void setAutoShowOnFocs(boolean enable) {
        if (editText == null)
            return;
        if (enable)
            editText.setOnFocusChangeListener(onFocusChangeListener1);
        else
            editText.setOnFocusChangeListener(onFocusChangeListener1);
    }

    View.OnFocusChangeListener onFocusChangeListener1 = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus)
                showKeyboard();
            else
                hideKeyboard();
        }
    };

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_measure://测量按钮
                Toast.makeText(mActivity, "测量", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_record://历史记录
                Toast.makeText(mActivity, "历史记录", Toast.LENGTH_SHORT).show();
                break;
            case R.id.record_1:
                Toast.makeText(mActivity, "记录1", Toast.LENGTH_SHORT).show();
                break;
            case R.id.record_2:
                Toast.makeText(mActivity, "记录2", Toast.LENGTH_SHORT).show();
                break;
            case R.id.record_3:
                Toast.makeText(mActivity, "记录3", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_done:
                Toast.makeText(mActivity, "Done", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public  int dip2px(float dipValue) {
        float scale = mActivity.getResources().getDisplayMetrics().density;
        return Math.round(dipValue * scale);
    }



    //--------------解决虚拟键的问题------------------------

    public static Point getNavigationBarSize(Context context) {
        Point appUsableSize =getAppUsableScreenSize(context);
        Point realScreenSize =getRealScreenSize(context);
// navigation bar on the right
        if(appUsableSize.x< realScreenSize.x) {
            return new Point(realScreenSize.x- appUsableSize.x,appUsableSize.y);
        }
// navigation bar at the bottom
        if(appUsableSize.y< realScreenSize.y) {
            return new Point(appUsableSize.x,realScreenSize.y- appUsableSize.y);
        }
// navigation bar is not present
        return new Point();
    }
    public static Point getAppUsableScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size =new Point();
        display.getSize(size);
        return size;
    }
    public static Point getRealScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        if(Build.VERSION.SDK_INT>=17) {
            display.getRealSize(size);
        }else if(Build.VERSION.SDK_INT>=14) {
            try{
                size.x= (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                size.y= (Integer) Display.class.getMethod("getRawHeight").invoke(display);
            }catch(IllegalAccessException e) {}catch(InvocationTargetException e) {}catch(NoSuchMethodException e) {}
        }
        return size;
    }

}
