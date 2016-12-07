package com.example.king.customkeyboard;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText mEditText1,mEditText2;
    private KeyboardUtil keyboardUtil;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        keyboardUtil = new KeyboardUtil(this);
        imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        initViews();
    }

    private void initViews() {
        mEditText1 = (EditText) findViewById(R.id.edit_1);
        mEditText1.setOnClickListener(this);
        mEditText2 = (EditText) findViewById(R.id.edit_2);
        mEditText2.setOnClickListener(this);

//        keyboardUtil.attachTo(mEditText1,false);
//        keyboardUtil.attachTo(mEditText2,false);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.edit_1:
                mEditText1.setFocusableInTouchMode(true);
                mEditText1.setFocusable(true);
                mEditText1.requestFocus();
                mEditText1.requestFocusFromTouch();
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                if(keyboardUtil.isShowing()){
                    keyboardUtil.attachTo(mEditText1,false);
                }else {
                    keyboardUtil.showKeyboard();
                    keyboardUtil.attachTo(mEditText1,true);
                }

                break;
            case R.id.edit_2:
                mEditText2.setFocusableInTouchMode(true);
                mEditText2.setFocusable(true);
                mEditText2.requestFocus();
                mEditText2.requestFocusFromTouch();
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                if(keyboardUtil.isShowing()){
                    keyboardUtil.attachTo(mEditText2,false);
                }else {
                    keyboardUtil.showKeyboard();
                    keyboardUtil.attachTo(mEditText2,true);
                }
                break;
        }
    }
}
