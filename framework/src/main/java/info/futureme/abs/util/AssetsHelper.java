package info.futureme.abs.util;

import android.content.res.AssetManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import info.futureme.abs.biz.ContextManager;

public class AssetsHelper {
    /**
     * Copy assets to specified path.
     *
     * @param assetFolder Asset folder name, can be "" or path ends with "/".
     * @param destPath    An exists path that can be accessed, ends with "/" or not.
     */
    public static boolean copyAssetFolderTo(String assetFolder, String destPath) {
        boolean bRet = true;
        if (assetFolder.endsWith("/")) {
            assetFolder = assetFolder.substring(0, assetFolder.length() - 1);
        }

        String[] files = listAssetsFrom(assetFolder);
        if (!destPath.endsWith("/")) {
            destPath += "/";
        }

        for (String file : files) {
            if (!new File(destPath + file).exists()) {
                String assetFile;
                if (assetFolder.equals("")) {
                    assetFile = file;
                } else {
                    assetFile = assetFolder + "/" + file;
                }
                bRet &= copyFile(assetFile, destPath + file, false);
            }
        }

        return bRet;
    }

    public static boolean copyFile(String assetPath, String destFile, boolean override) {
        if (!override && new File(destFile).exists()) {
            return true;
        }

        boolean bRet = true;
        try {
            AssetManager assetManager = ContextManager.assetManager();
            InputStream inputStream = assetManager.open(assetPath);
            OutputStream outputStream = new FileOutputStream(destFile);
            copyTo(inputStream, outputStream);

            inputStream.close();
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            Assert.d(e);
            bRet = false;
        }

        return bRet;
    }

    /**
     * List all file names in the specified asset folder.
     *
     * @param folder Asset folder name, can not ends with "/".
     * @return Asset file name array.
     */
    public static String[] listAssetsFrom(String folder) {
        AssetManager assetManager = ContextManager.assetManager();
        String[] files = null;
        try {
            files = assetManager.list(folder);
        } catch (Exception e) {
            Assert.d(e);
        }
        return files;
    }

    private static void copyTo(InputStream input, OutputStream output) {
        byte[] buffer = new byte[4 * 1024];
        int read;
        try {
            while ((read = input.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }
        } catch (IOException e) {
            Assert.d(e);
        }
    }
}
