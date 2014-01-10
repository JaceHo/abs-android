package edu.hebtu.movingcampus.widget;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

public class CustomerSpinner extends Spinner implements
		android.widget.AdapterView.OnItemSelectedListener {
	private ArrayList<String> list;
	public int mPos;
	public String mSelection;

	public int getmPos() {
		return mPos;
	}

	public void setmPos(int mPos) {
		this.mPos = mPos;
	}

	public String getmSelection() {
		return mSelection;
	}

	public void setmSelection(String mSelection) {
		this.mSelection = mSelection;
	}

	public String getSeltext() {
		return mSelection;
	}

	public void setSeltext(String seltext) {
		this.mSelection = seltext;
	}

	public CustomerSpinner(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		setOnItemSelectedListener(this);
	}

	public ArrayList<String> getList() {
		return this.list;
	}

	public void setList(ArrayList<String> paramArrayList) {
		this.list = paramArrayList;
	}

	/**
	 * When the user selects an item in the spinner, this method is invoked by
	 * the callback chain. Android calls the item selected listener for the
	 * spinner, which invokes the onItemSelected method.
	 * 
	 * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android.widget.AdapterView,
	 *      android.view.View, int, long)
	 * @param parent
	 *            - the AdapterView for this listener
	 * @param v
	 *            - the View for this listener
	 * @param pos
	 *            - the 0-based position of the selection in the mLocalAdapter
	 * @param row
	 *            - the 0-based row number of the selection in the View
	 */
	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int pos, long row) {

		CustomerSpinner.this.mPos = pos;
		CustomerSpinner.this.mSelection = parent.getItemAtPosition(pos)
				.toString();
	}

	/**
	 * The definition of OnItemSelectedListener requires an override of
	 * onNothingSelected(), even though this implementation does not use it.
	 * 
	 * @param parent
	 *            - The View for this Listener
	 */
	@Override
	public void onNothingSelected(AdapterView<?> parent) {

		// do nothing

	}
}
