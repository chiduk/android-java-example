package kr.co.toyappfactory.gosung.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import kr.co.toyappfactory.gosung.R;

/**
 * Created by chiduk on 2016. 7. 1..
 */
public class DialogBox {
    private Context context;

    public static void show(Context context, int resourceId){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setMessage(resourceId).setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog alert = alertDialog.create();
        alert.show();

    }

}
