package com.example.medic;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class cDiag extends Dialog {

    private String info;
    private Button yes, no;
    private TextView missing;
    private final AddNew.OnDialogClickListener listener;

    public cDiag(Activity a, String info, AddNew.OnDialogClickListener listener) {
        super(a);
        this.info = info;
        this.listener = listener;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popwin);
        yes = findViewById(R.id.yesBut);
        no = findViewById(R.id.noBut);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDialogClick(view);
                dismiss();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDialogClick(view);
                dismiss();
            }
        });

        missing = findViewById(R.id.missingInfo);
        missing.setText(info);

    }


}
