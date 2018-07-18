package service.courier.app.dmcx.courierservice.Variables;

import android.support.v4.app.Fragment;

import service.courier.app.dmcx.courierservice.Dialog.AppDialog;
import service.courier.app.dmcx.courierservice.Firebase.AppFirebase;
import service.courier.app.dmcx.courierservice.LocalDatabase.LocalDB;

public class Vars {

    public static final String SECURITY_CODE = "SADMAN";
    public static final String TASK_CODE = "task-new-111123jkajsdahsd";

    public static final String PREFS_NAME = "CourierServiceAppLocalDB";
    public static final String PREFS_ISUSERADMIN = "IsUserIsAdmin";

    public static AppFirebase appFirebase;
    public static AppDialog appDialog;
    public static Fragment currentFragment;
    public static LocalDB localDB;
    public static boolean isUserAdmin;

    public static void reset() {
        appFirebase = null;
        appDialog = null;
        currentFragment = null;
        localDB = null;
        isUserAdmin = false;
    }

}
