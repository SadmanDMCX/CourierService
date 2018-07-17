package service.courier.app.dmcx.courierservice.Utility;

import android.content.Context;
import android.util.Patterns;
import android.widget.Toast;

import java.util.regex.Pattern;

public class AppValidator {

    public static boolean empty(String value) {
        return value.equals("");
    }

    public static boolean validEmail(String value) {
        return Patterns.EMAIL_ADDRESS.matcher(value).matches();
    }

    public static boolean validPassword(String value) {
        return value.length() >= 6;
    }

}
