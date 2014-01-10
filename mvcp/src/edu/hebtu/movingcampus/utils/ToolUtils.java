package edu.hebtu.movingcampus.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import org.xmlpull.v1.XmlPullParser;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.Xml;
import edu.hebtu.movingcampus.entity.UpdataInfo;

public class ToolUtils {
	private Context context;

	public ToolUtils(Activity paramActivity) {
		this.context = paramActivity;
	}
	/**
	 * Return the size of a directory in bytes
	 */
	public static long dirSize(File dir) {

	    if (dir.exists()) {
	        long result = 0;
	        File[] fileList = dir.listFiles();
	        for(int i = 0; i < fileList.length; i++) {
	            // Recursive call if it's a directory
	            if(fileList[i].isDirectory()) {
	                result += dirSize(fileList [i]);
	            } else {
	                // Sum the file size in bytes
	                result += fileList[i].length();
	            }
	        }
	        return result; // return the file size
	    }
	    return 0;
	}
	public File getFileFromServer(String paramString1,
			ProgressDialog paramProgressDialog, String paramString2)
			throws Exception {
		if (Environment.getExternalStorageState().equals("mounted")) {
			HttpURLConnection localHttpURLConnection = (HttpURLConnection) new URL(
					paramString1).openConnection();
			localHttpURLConnection.setConnectTimeout(5000);
			paramProgressDialog.setMax(localHttpURLConnection
					.getContentLength());
			InputStream localInputStream = localHttpURLConnection
					.getInputStream();
			try {
				File localFile = new File(
						Environment.getExternalStorageDirectory()
								+ File.separator + "mvcpdownload"
								+ File.separator, paramString2);
				if (!localFile.getParentFile().exists())
					localFile.getParentFile().mkdirs();
				FileOutputStream localFileOutputStream = new FileOutputStream(
						localFile);
				BufferedInputStream localBufferedInputStream = new BufferedInputStream(
						localInputStream);
				byte[] arrayOfByte = new byte[1024];
				int i = 0;
				while (true) {
					int j = localBufferedInputStream.read(arrayOfByte);
					if (j == -1) {
						localFileOutputStream.close();
						localBufferedInputStream.close();
						localInputStream.close();
						localHttpURLConnection.disconnect();
						return localFile;
					}
					localFileOutputStream.write(arrayOfByte, 0, j);
					i += j;
					paramProgressDialog.setProgress(i);
				}
			} catch (Exception e) {
				Log.i("ex:", e.toString());
			}
		}
		return null;
	}

	public UpdataInfo getUpdataInfo(InputStream paramInputStream)
			throws Exception {
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(paramInputStream, "utf-8");
		UpdataInfo localUpdataInfo = new UpdataInfo(this.context);
		int eventType = parser.getEventType();
		String text = null;

		try {
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String tagname = parser.getName();
				switch (eventType) {
				case XmlPullParser.START_TAG:
					break;
				case XmlPullParser.TEXT:
					text = parser.getText();
					break;
				case XmlPullParser.END_TAG:
					if (tagname.equalsIgnoreCase("version")) {
						localUpdataInfo.setVersion(text);
					} else if (tagname.equalsIgnoreCase("url")) {
						localUpdataInfo.setUrl(text);
					} else if (tagname.equalsIgnoreCase("information")) {
						localUpdataInfo.setInformation(text);
					}
					break;
				default:
					break;
				}
				eventType = parser.next();
			}

		} catch (Exception e) {
			Log.i("xml parser", "while parse xml from server," + e.toString());
		}
		return localUpdataInfo;
	}

	public static int clearCacheFolder(final File dir, final int numDays) {

		int deletedFiles = 0;
		if (dir != null && dir.isDirectory()) {
			try {
				for (File child : dir.listFiles()) {

					// first delete subdirectories recursively
					if (child.isDirectory()) {
						deletedFiles += clearCacheFolder(child, numDays);
					}

					// then delete the files and subdirectories in this dir
					// only empty directories can be deleted, so subdirs have
					// been done first
					if (child.lastModified() < new Date().getTime() - numDays
							* DateUtils.DAY_IN_MILLIS) {
						if (child.delete()) {
							deletedFiles++;
						}
					}
				}
			} catch (Exception e) {
				Log.e("ATTENTION!",
						String.format("Failed to clean the cache, error %s",
								e.getMessage()));
			}
		}
		return deletedFiles;
	}

	public static void clearCache(final Context context, final int numDays) {
		Log.i("ADVL", String.format(
				"Starting cache prune, deleting files older than %d days",
				numDays));
		int numDeletedFiles = clearCacheFolder(context.getCacheDir(), numDays);
		Log.i("ADVL", String.format(
				"Cache pruning completed, %d files deleted", numDeletedFiles));
	}

	public int getVersionCode() throws Exception {
		return this.context.getPackageManager().getPackageInfo(
				this.context.getPackageName(), 0).versionCode;
	}
}
