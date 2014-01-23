package edu.hebtu.movingcampus.subject.base;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import edu.hebtu.movingcampus.activity.base.Observer;
import edu.hebtu.movingcampus.adapter.base.AdapterBase;

/**
 * topic, user's single interest data collection
 * 
 * @author hippo
 * @version 1.0
 * @created 14-Nov-2013 9:13:33 AM
 */
public abstract class Subject implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Boolean enable = true;
	protected volatile List<Observer> observers = new LinkedList<Observer>();

	public Subject() {
	}

	/**
	 * 
	 * @param enable
	 *            enable
	 * @return
	 */
	public void enable(Boolean enable) {
		this.enable = enable;
	}

	public abstract String getTag();

	public abstract Boolean mesureChange(Activity context);

	public void notifyObservers(Activity context) {
		for (Observer o : observers)
			if (mesureChange(context))
				o.update();
	}

	/**
	 * 
	 * @param observer
	 *            observer
	 */
	public Boolean registObserver(Observer observer) {
		for (Observer o : observers)
			if (o.equals(observer))
				return false;
		observers.add(observer);
		return true;
	}

	/**
	 * 
	 * @param observer
	 *            observer
	 */
	public Boolean unRegistObserver(Observer observer) {
		for (Observer o : observers)
			if (o.equals(observer)) {
				observers.remove(observer);
				return true;
			}
		return false;
	}
}