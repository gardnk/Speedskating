package com.example.gard.speedskating;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;


public class LoadingDialog extends ProgressDialog {
    public LoadingDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        setContentView(R.layout.loading);
    }
}
