package com.nkwazi_tech.tuit_app.Classes;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.nkwazi_tech.tuit_app.R;

import java.util.Objects;

public class dialog_class {

    public static void dialog(String title, String main, String src, Context context) {
        TextInputLayout textInputLayout3;
        TextView title_txt, user_balance, main_data;
        Dialog topup_Dialog;
        Button submit;
        ProgressBar progressBar;

        topup_Dialog = new Dialog(context);

        topup_Dialog.setContentView(R.layout.dialog_topup);
        Objects.requireNonNull(topup_Dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        Window window = topup_Dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        user_balance = topup_Dialog.findViewById(R.id.title4);
        submit = topup_Dialog.findViewById(R.id.button2);
        textInputLayout3 = topup_Dialog.findViewById(R.id.textInputLayout3);
        title_txt = topup_Dialog.findViewById(R.id.title5);
        main_data = topup_Dialog.findViewById(R.id.title3);
        progressBar = topup_Dialog.findViewById(R.id.progressBar12);

        submit.setVisibility(View.GONE);
        textInputLayout3.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        title_txt.setText(title);
        main_data.setText(main);
        user_balance.setText("Do not show again");

        if (src.equals("video")){
            user_balance.setOnClickListener(v -> {
                SharedPrefManager.getInstance(context).setDialog_sub(true);
                topup_Dialog.dismiss();
            });
        }else{
            user_balance.setOnClickListener(v -> {
                SharedPrefManager.getInstance(context).setDialog_vid(true);
                topup_Dialog.dismiss();
            });
        }

        Boolean state_vid =   SharedPrefManager.getInstance(context).getDialog_vid()
                ,state_sub=SharedPrefManager.getInstance(context).getDialog_sub();

        if (src.equals("video") && !state_sub){
            topup_Dialog.show();
        }else if (!src.equals("video") && !state_vid){
            topup_Dialog.show();
        }
    }
}
