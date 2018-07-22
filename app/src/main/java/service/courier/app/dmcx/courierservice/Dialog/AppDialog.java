package service.courier.app.dmcx.courierservice.Dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;

public class AppDialog {

    private AppDialog instance;
    private AlertDialog alertDialog;

    public AppDialog create(Context context, View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        builder.setCancelable(false);

        alertDialog = builder.create();
        instance = this;

        return instance;
    }

    public void transparent() {
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void show() {
        alertDialog.show();
    }

    public void dismiss() {
        alertDialog.dismiss();
    }
}
