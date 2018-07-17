package service.courier.app.dmcx.courierservice.Variables;

import android.support.v4.app.Fragment;

import service.courier.app.dmcx.courierservice.Dialog.AppDialog;
import service.courier.app.dmcx.courierservice.Firebase.AppFirebase;

public class Vars {

    public static final String SECURITY_CODE = "SADMAN";

    public static AppFirebase appFirebase;
    public static AppDialog appDialog;
    public static Fragment currentFragment;
    public static boolean isUserAdmin;

    public static void reset() {
        appFirebase = null;
        appDialog = null;
        currentFragment = null;
        isUserAdmin = false;
    }

}
