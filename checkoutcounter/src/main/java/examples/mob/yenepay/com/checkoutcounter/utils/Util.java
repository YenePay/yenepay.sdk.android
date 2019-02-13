package examples.mob.yenepay.com.checkoutcounter.utils;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import org.ocpsoft.prettytime.PrettyTime;

import java.io.File;
import java.util.Date;
import java.util.Locale;

import examples.mob.yenepay.com.checkoutcounter.R;
import examples.mob.yenepay.com.checkoutcounter.StoreApp;

public class Util {
    public static String getFormattedAmt(double amt){
        return String.format("%1$,.2f", amt);
    }

    public static Bitmap getProductImage(String imageName){
        if(TextUtils.isEmpty(imageName)){
            return BitmapFactory.decodeResource(StoreApp.getContext().getResources(), R.drawable.top_image_2);
        }
        File imageFile = ImagePicker.getImageFile(imageName);
        if(!imageFile.exists()){
            return BitmapFactory.decodeResource(StoreApp.getContext().getResources(), R.drawable.top_image_2);
        }
        return BitmapFactory.decodeFile(imageFile.getAbsolutePath());
    }

    public static String getHumanRedableTimeFormat(Date date){
        if(date == null){
            return "";
        }
        Locale currentLocal = Locale.US;
        PrettyTime prettyTime = new PrettyTime(new Date(), currentLocal);
        return prettyTime.format(date);
    }
}
