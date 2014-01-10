package edu.hebtu.movingcampus.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import edu.hebtu.movingcampus.config.Constants;
import edu.hebtu.movingcampus.enums.NewsType;
import edu.hebtu.movingcampus.subject.base.TitleNews;
import edu.hebtu.movingcampus.subjects.AllInOneCardNewsdump;
import edu.hebtu.movingcampus.subjects.LibraryNewsdump;
import edu.hebtu.movingcampus.subjects.LocalNewsSubject;
import edu.hebtu.movingcampus.subjects.NewsSubject;

/**
 * @author hippo
 * @version 1.0
 * @created 14-Nov-2013 9:13:32 AM
 */
public class IPreference {
	/**
	 * Double checked locking not work on java1.4 and earlier!
	 */
	private static volatile IPreference instance;
	private static  String serilizeFile = Constants.PREFER_FILE + ".db";
	private List<TitleNews> topics = new ArrayList<TitleNews>();

	//单例模式
	private IPreference(Activity context) {
		try {
			instance=load(context);
		} catch (OptionalDataException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(instance==null) instance = this;
		else return;
		// TODO,settings,for debug
		SharedPreferences pre = context.getSharedPreferences(
				Constants.PREFER_FILE, 0);
		SharedPreferences.Editor editor= pre.edit();
		//本地信息
		editor.putInt(Constants.LIB_DAYS, 10);
		editor.putInt(Constants.BALANCE_LOWEAST, 10);
		editor.commit();
		// 本地信息,第一个，默认显示
		LocalNewsSubject localNewsSubject = new LocalNewsSubject(context);
		localNewsSubject
				.addLocalSubject(new AllInOneCardNewsdump(context)); // 10元之内,提醒
		localNewsSubject.addLocalSubject(new LibraryNewsdump(context)); // 10天之内过期的书
		topics.add(localNewsSubject);

		for (int i = 1; i < NewsType.values().length; i++) {
			if (pre.getBoolean("news_" + i, true)) {
				topics.add(new NewsSubject(NewsType.values()[i], context));// 每次接受20条|默认,学校新闻...
			}
		}
	}

	/**
	 * Double checked locking not work on java1.4 or earlier!
	 */
	public static IPreference getInstance(Activity context) {
		if (instance == null) {
			synchronized (IPreference.class) {
				if (instance == null)
					instance = new IPreference(context);
			}
		}
		return instance;
	}

	/**
	 * IPreference 的序列化流
	 * 
	 * @param filename
	 * @throws IOException
	 * @parm context
	 */
	public static void save(Activity context) throws IOException {
		FileOutputStream fos = context.openFileOutput(serilizeFile,
				Context.MODE_PRIVATE);
		ObjectOutputStream oo = new ObjectOutputStream(fos);
		oo.writeObject(IPreference.getInstance(context));
		fos.close();
		oo.close();
		Log.d("board", "saved in" + serilizeFile);
	}

	/**
	 * IPreference的反序列化,
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws OptionalDataException
	 * @parm context
	 */
	public static IPreference load(Context context) throws OptionalDataException,
			ClassNotFoundException, IOException {
	
		IPreference  obj=null;
		if(new File(serilizeFile).exists()){	
			FileInputStream fis = context.openFileInput(serilizeFile);
			ObjectInputStream oi = new ObjectInputStream(fis);
			obj = (IPreference) oi.readObject();
			oi.close();
			fis.close();
			Log.d("board", "deserialize: " + instance.toString());
		}
		return obj;
	}

	public List<TitleNews> getTopics() {
		return topics;
	}

	public void addTopic(TitleNews top) {
		topics.add(top);
	}

	public void setTopics(List<TitleNews> topics) {
		this.topics = topics;
	}

	public TitleNews getTitledNewsByID(long id) {
		for (TitleNews n : topics)
			if (n.getId() == id)
				return n;
		return null;
	}

	public void removeTitledNewsById(int id) {
		topics.remove(getTitledNewsByID(id));
	}
}