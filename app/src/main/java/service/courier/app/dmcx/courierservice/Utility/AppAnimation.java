package service.courier.app.dmcx.courierservice.Utility;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import service.courier.app.dmcx.courierservice.Activity.MainActivity;
import service.courier.app.dmcx.courierservice.R;

public class AppAnimation {

    public static void rotateAnimationRight(View view) {
        Animation rotate = AnimationUtils.loadAnimation(MainActivity.instance, R.anim.rotate_center_right);
        view.clearAnimation();
        view.setAnimation(rotate);
        view.animate();
    }

    public static void rotateAnimationLeft(View view) {
        Animation rotate = AnimationUtils.loadAnimation(MainActivity.instance, R.anim.rotate_center_left);
        view.clearAnimation();
        view.setAnimation(rotate);
        view.animate();
    }

}
