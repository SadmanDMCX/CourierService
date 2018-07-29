package service.courier.app.dmcx.courierservice.Dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

    public AppDialog create(Context context, String title, String message, String potitiveText, String negativeText, DialogInterface.OnClickListener onPositiveClickListener, DialogInterface.OnClickListener onNegativeClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);

        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(potitiveText, onPositiveClickListener);
        builder.setNegativeButton(negativeText, onNegativeClickListener);

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

    public void hide() {
        alertDialog.hide();
    }

    public void dismiss() {
        alertDialog.dismiss();
    }
}
