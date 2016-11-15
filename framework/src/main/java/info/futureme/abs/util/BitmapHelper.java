package info.futureme.abs.util;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import info.futureme.abs.biz.ContextManager;

public class BitmapHelper {
    // Decode

    private static final ThreadLocal<Options> sDecodeOptionsLocal = new ThreadLocal<Options>();

    private static Options getOptions() {
        Options op = sDecodeOptionsLocal.get();
        if (op == null) {
            op = new Options();
            op.inDither = false;
            op.inScaled = false;
            op.inSampleSize = 1;
            op.inTempStorage = new byte[16 * 1024];
            sDecodeOptionsLocal.set(op);
        }
        return op;
    }

    public static final int WRAP_CONTENT = -1;

    /**
     * @param maxWidth  the maximum width size or {@link #WRAP_CONTENT}
     * @param maxHeight the maximum height size or {@link #WRAP_CONTENT}
     * @param config    {@link Config#RGB_565} or {@link Config#ARGB_8888}
     */
    public static Bitmap decodeBitmap(int pathType, String filePath, int maxWidth, int maxHeight, Rect outPaddings, Config config) {
        InputStream stream = FileHelper.open(pathType, filePath);
        if (stream == null) {
            return null;
        }

        Bitmap bitmap = decodeBitmap(stream, maxWidth, maxHeight, outPaddings, config);

        try {
            stream.close();
        } catch (IOException e) {
        }
        return bitmap;
    }

    /**
     * @param maxWidth  the maximum width size or {@link #WRAP_CONTENT}
     * @param maxHeight the maximum height size or {@link #WRAP_CONTENT}
     * @param config    {@link Config#RGB_565} or {@link Config#ARGB_8888}
     */
    public static Bitmap decodeBitmap(InputStream stream, int maxWidth, int maxHeight, Rect outPaddings, Config config) {
        // 1) decode
        Options op = getOptions();
        op.inJustDecodeBounds = false;
        op.inPreferredConfig = config;
        op.inSampleSize = 4;
        Bitmap bitmap = BitmapFactory.decodeStream(stream, outPaddings, op);
        bitmap.setDensity(Bitmap.DENSITY_NONE);

        // 2) ensure size
        return ensureBitmapSize(bitmap, maxWidth, maxHeight);
    }

    /**
     * @param maxWidth  the maximum width size or {@link #WRAP_CONTENT}
     * @param maxHeight the maximum height size or {@link #WRAP_CONTENT}
     * @param config    {@link Config#RGB_565} or {@link Config#ARGB_8888}
     */
    public static Bitmap decodeBitmap(byte[] data, int offset, int length, int maxWidth, int maxHeight, Config config) {
        // 1) decode
        Options op = getOptions();
        op.inJustDecodeBounds = false;
        op.inPreferredConfig = config;
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, offset, length, op);
        bitmap.setDensity(Bitmap.DENSITY_NONE);

        // 2) ensure size
        return ensureBitmapSize(bitmap, maxWidth, maxHeight);
    }

    private static Bitmap ensureBitmapSize(Bitmap bitmap, int maxWidth, int maxHeight) {
        if (maxWidth != WRAP_CONTENT || maxHeight != WRAP_CONTENT) {
            // 1) calc size
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            float scale = Math.max(//
                    maxWidth != WRAP_CONTENT ? ((float) maxWidth / width) : 0, //
                    maxHeight != WRAP_CONTENT ? ((float) maxHeight / height) : 0);
            if (maxWidth == WRAP_CONTENT) {
                width = Math.round(width * scale);
            } else if (maxHeight == WRAP_CONTENT) {
                height = Math.round(height * scale);
            }

            // 2) do scale
            if (scale != 1f) {
                Bitmap tmp = bitmap;
                bitmap = createScaledBitmap(tmp, width, height);
                tmp.recycle();
            }
        }
        return bitmap;
    }

    /**
     * Read bitmap info (size, mimetype) without create Bitmap object.<br/>
     * ATTENTION: The returned value is readonly.
     */
    public static Options readBitmapInfo(int pathType, String filePath) {
        InputStream stream = FileHelper.open(pathType, filePath);
        if (stream == null) {
            return null;
        }
        Options op = BitmapHelper.readBitmapInfo(stream);
        try {
            stream.close();
        } catch (IOException e) {
        }

        return op;
    }

    /**
     * Read bitmap info (size, mimetype) without create Bitmap object.<br/>
     * ATTENTION:<br/>
     * 1) The stream need to be re-opened after this.<br/>
     * 2) The returned value is readonly.<br/>
     */
    public static Options readBitmapInfo(InputStream stream) {
        Options op = getOptions();
        op.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(stream, null, op);
        return op;
    }

    // Scale

    private static final Matrix SCALE_MATRIX;
    private static final Paint SCALE_PAINT;

    static {
        SCALE_MATRIX = new Matrix();
        SCALE_PAINT = new Paint();
        SCALE_PAINT.setFilterBitmap(true);
        SCALE_PAINT.setAntiAlias(true);
    }

    public static Bitmap createScaledBitmap(Bitmap source, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, source.getConfig());
        bitmap.setDensity(Bitmap.DENSITY_NONE);

        /**
         * The only way to detach a bitmap from a canvas is to invoke
         * Canvas.setBitmap(null), but it only works on 4.x (throw
         * NullPointerException otherwise). So we can't reuse the canvas,
         * otherwise the bitmap will be kept referenced.
         */
        Canvas canvas = new Canvas();
        int oldWidth = source.getWidth();
        int oldHeight = source.getHeight();
        if (width != oldWidth || height != oldHeight) {
            SCALE_MATRIX.setScale((float) width / oldWidth, (float) height / oldHeight);
            canvas.setMatrix(SCALE_MATRIX);
        } else {
            canvas.setMatrix(null);
        }

        canvas.setBitmap(bitmap);
        canvas.drawBitmap(source, 0, 0, SCALE_PAINT);

        return bitmap;
    }


    public static Bitmap shrinkBitmapScreen(Bitmap bitmap, Activity activity) {
        Bitmap orig = bitmap;
        bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()/2, bitmap.getHeight()/2, true);
        orig.recycle();
        orig = bitmap;
        int top = WindowUtils.getStatusBarHeight(activity);
        bitmap = Bitmap.createBitmap(bitmap, 0, top / 2, bitmap.getWidth(), bitmap.getHeight() - top / 2);
        orig.recycle();
        System.gc();
        Runtime.getRuntime().gc();
        return bitmap;
    }

//    public static File shrink(File file){
//        final File[] res = new File[1];
//        final CountDownLatch latch = new CountDownLatch(1);
//        Luban.get(ContextManager.context())
//                .load(file)                     //传人要压缩的图片
//                .putGear(Luban.THIRD_GEAR)      //设定压缩档次，默认三挡
//                .setCompressListener(new OnCompressListener() { //设置回调
//
//                    @Override
//                    public void onStart() {
//                        // TODO 压缩开始前调用，可以在方法内启动 loading UI
//                    }
//                    @Override
//                    public void onSuccess(File file) {
//                        // TODO 压缩成功后调用，返回压缩后的图片文件
//                        res[0] = file;
//                        latch.countDown();
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        // TODO 当压缩过去出现问题时调用
//                        latch.countDown();
//                    }
//                }).launch();    //启动压缩
//        try {
//            latch.await(30, TimeUnit.SECONDS);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        return res[0];
//    }



    public static boolean shrinkBitmap(Bitmap bmpPic, int KB, File file) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmpPic.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int compressQuality = 98; // quality decreasing by 5 every loop. (start from 95)
        int streamLength = baos.toByteArray().length;
        DLog.w("length:", "" + streamLength);
        while (streamLength >= KB * 1024) {
            baos = new ByteArrayOutputStream();
            DLog.d("shrink:", "Quality: " + compressQuality);
            bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, baos);
            if(compressQuality < 2)
                break;
            else if(compressQuality < 8)
                compressQuality -= 1;
            else
                compressQuality -= 2;
            byte[] bmpPicByteArray = baos.toByteArray();
            streamLength = bmpPicByteArray.length;
            DLog.d("shrink:", "Size: " + streamLength);
        }

        FileOutputStream out = null;
        try {
            file.createNewFile();

            byte[] bitmapdata = baos.toByteArray();

            DLog.i("bytes", ""+bitmapdata.length);
            out = new FileOutputStream(file);
            out.write(bitmapdata);
            // PNG is a lossless format, the compression factor (100) is ignored
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setExifRotation(int degrees, File file) {
        try {
            degrees %= 360;
            if (degrees < 0) degrees += 360;

            int orientation = ExifInterface.ORIENTATION_NORMAL;
            switch (degrees) {
                case 0:
                    orientation = ExifInterface.ORIENTATION_NORMAL;
                    break;
                case 90:
                    orientation = ExifInterface.ORIENTATION_ROTATE_90;
                    break;
                case 180:
                    orientation = ExifInterface.ORIENTATION_ROTATE_180;
                    break;
                case 270:
                    orientation = ExifInterface.ORIENTATION_ROTATE_270;
                    break;
            }

            saveExifData(ExifInterface.TAG_ORIENTATION,
                    Integer.toString(orientation), file);
        } catch (Exception ex) {
            DLog.p(ex);
        }
    }

    private static void saveExifData(String tag, String value, File file) throws IOException {
        ExifInterface mExif = new ExifInterface(file.getAbsolutePath());
        mExif.setAttribute(tag, value);
        mExif.saveAttributes();
        Log.d("EXIF value", mExif.getAttribute(ExifInterface.TAG_ORIENTATION));
    }


    public static Bitmap rotateBy(Bitmap img, int degree){
        Matrix matrix = new Matrix();
        matrix.postRotate(degree); /*翻转degree度*/
        int width = img.getWidth();
        int height =img.getHeight();
        img = Bitmap.createBitmap(img, 0, 0, width, height, matrix, true);
        return img;
    }


    /*

            读取照片exif信息中的旋转角度
     @param path 照片路径
    @return角度
     */
    public static int readPictureDegree(String path) {
        int degree  = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static Bitmap toBitmap(byte[] data) {
        Assert.d(null != data);

        Bitmap bitmap = null;

        try {
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        } catch (OutOfMemoryError e) {
        }

        return bitmap;
    }

    public static boolean toFile(Bitmap source, File file) {
        FileOutputStream out = null;
        try {
            file.createNewFile();


            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            source.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();

            DLog.i("bytes", ""+bitmapdata.length);
            out = new FileOutputStream(file);
            out.write(bitmapdata);
            // PNG is a lossless format, the compression factor (100) is ignored
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    * 图片压缩方法02：获得缩略图
    */
    public static Bitmap getThumbnail(int id, int w, int h) {
// 获得原图
        Bitmap beforeBitmap = BitmapFactory.decodeResource(
                ContextManager.context().getResources(), id);
// 获得缩略图
        Bitmap afterBitmap = ThumbnailUtils
                .extractThumbnail(beforeBitmap, w, h);
        return afterBitmap;

    }

    /*
    * 图片压缩方法02：获得缩略图
    */
    public static Bitmap getThumbnail(String path, int w, int h) {
// 获得原图

        Bitmap beforeBitmap = safeDecodeScaledBitmapFromFileSystem(path, w, h);
// 获得缩略图
        Bitmap afterBitmap = ThumbnailUtils
                .extractThumbnail(beforeBitmap, w, h);
        return afterBitmap;
    }

    public static Bitmap safeDecodeScaledBitmapFromFileSystem(String filePath,
                                                              int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }


    /**
     * 图片压缩03
     *
     * 要操作的图片的大小
     * @param newWidth
     * 图片指定的宽度
     * @param newHeight
     * 图片指定的高度
     * @return
     */
    public static Bitmap compressBitmap(Bitmap beforeBitmap, double newWidth, double newHeight) {
// 图片原有的宽度和高度
        float beforeWidth = beforeBitmap.getWidth();
        float beforeHeight = beforeBitmap.getHeight();

// 计算宽高缩放率
        float scaleWidth = 0;
        float scaleHeight = 0;
        if (beforeWidth > beforeHeight) {
            scaleWidth = ((float) newWidth) / beforeWidth;
            scaleHeight = ((float) newHeight) / beforeHeight;
        } else {
            scaleWidth = ((float) newWidth) / beforeHeight;
            scaleHeight = ((float) newHeight) / beforeWidth;
        }

// 矩阵对象
        Matrix matrix = new Matrix();
// 缩放图片动作 缩放比例
        matrix.postScale(scaleWidth, scaleHeight);
// 创建一个新的Bitmap 从原始图像剪切图像
        Bitmap afterBitmap = Bitmap.createBitmap(beforeBitmap, 0, 0,
                (int) beforeWidth, (int) beforeHeight, matrix, true);
        return afterBitmap;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

}
