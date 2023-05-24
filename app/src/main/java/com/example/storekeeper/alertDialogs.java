package com.example.storekeeper;

import android.content.Context;
import android.graphics.Color;

import com.amrdeveloper.lottiedialog.LottieDialog;
import com.example.storekeeper.newInserts.product_CreateNew;

public class alertDialogs {
    public void launchSuccess(Context context,String msg) {
        if (msg.equals(""))
            msg = "Επιτυχής εισαγωγή";
        LottieDialog dialog = new LottieDialog(context)
                .setAnimation(R.raw.success)
                .setAnimationRepeatCount(LottieDialog.INFINITE)
                .setAutoPlayAnimation(true)
                .setDialogBackground(Color.TRANSPARENT)
                .setMessage(msg);
        dialog.show();
    }

    public void launchFail(Context context,String msg) {
        if (msg.equals(""))
            msg = "Αποτυχία εισαγωγής";
        LottieDialog dialog = new LottieDialog(context)
                .setAnimation(R.raw.fail)
                .setAnimationRepeatCount(LottieDialog.INFINITE)
                .setAutoPlayAnimation(true)
                .setDialogBackground(Color.TRANSPARENT)
                .setMessage(msg);
        dialog.show();
    }
}
