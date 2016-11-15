package info.futureme.abs.example;

import android.graphics.Bitmap;

import org.junit.Test;

import info.futureme.abs.example.conf.MVSConstants;
import info.futureme.abs.util.BitmapHelper;

/**
 * Created by Jeffrey on 01/11/2016.
 */

public class ImageShrinkTest {

    @Test
    public void shrink(){
        Bitmap scaledBitmap =
                BitmapHelper.safeDecodeScaledBitmapFromFileSystem("~/Download/IMG_20161101_100553.jpg", MVSConstants.LIMITS.IMAGE_UPLOAD_MAX_WIDTH,
                        MVSConstants.LIMITS.IMAGE_UPLOAD_MAX_HEIGHT);
        BitmapHelper.shrinkBitmap(scaledBitmap, MVSConstants.LIMITS.IMAGE_UPLOAD_MAX_KB, null);
    }
}
