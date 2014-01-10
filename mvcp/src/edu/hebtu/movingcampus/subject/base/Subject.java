package edu.hebtu.movingcampus.subject.base;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import edu.hebtu.movingcampus.activity.base.Observer;

/**
 * topic, user's single interest data collection
 * 
 * @author hippo
 * @version 1.0
 * @created 14-Nov-2013 9:13:33 AM
 */
public abstract class Subject {

	protected Boolean enable = true;
	protected List<Observer> observers = new LinkedList<Observer>();
	protected static Activity ac;

	public Subject(Activity ac) {
		Subject.ac = ac;
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

	public abstract Boolean mesureChange();

	public void notifyObservers() {
		for (Observer o : observers)
			if (mesureChange())
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