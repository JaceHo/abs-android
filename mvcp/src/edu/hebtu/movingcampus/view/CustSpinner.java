package edu.hebtu.movingcampus.view;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import edu.hebtu.movingcampus.R;

/**
 * Displays an Android spinner widget backed by data in an array. The array is
 * loaded from the strings.xml resources file.
 */
public class CustSpinner extends Spinner {
	/**
	 * Fields to contain the current position and display contents of the
	 * spinner
	 */
	protected int mPos;
	protected String mSelection;

	/**
	 * ArrayAdapter connects the spinner widget to array-based data.
	 */
	protected ArrayAdapter<String> mAdapter;

	/**
	 * The initial position of the spinner when it is first installed.
	 */
	public static final int DEFAULT_POSITION = 2;

	/**
	 * The name of a properties file that stores the position and selection when
	 * the activity is not loaded.
	 */
	public static final String PREFERENCES_FILE = "SpinnerPrefs";

	/**
	 * These values are used to read and write the properties file.
	 * PROPERTY_DELIMITER delimits the key and value in a Java properties file.
	 * The "marker" strings are used to write the properties into the file
	 */
	public static final String PROPERTY_DELIMITER = "=";

	/**
	 * The key or label for "position" in the preferences file
	 */
	public static final String POSITION_KEY = "Position";

	/**
	 * The key or label for "selection" in the preferences file
	 */
	public static final String SELECTION_KEY = "Selection";

	public static final String POSITION_MARKER = POSITION_KEY
			+ PROPERTY_DELIMITER;

	public static final String SELECTION_MARKER = SELECTION_KEY
			+ PROPERTY_DELIMITER;

	private static Context context = null;

	private static View paramView = null;

	private ArrayList<String> list;

	public ArrayList<String> getList() {
		return list;
	}

	public void setList(ArrayList<String> list, int all, int item) {
		this.list = list; /*
						 * Create a backing mLocalAdapter for the Spinner from a
						 * list of the planets. The list is defined by XML in
						 * the strings.xml file.
						 */
		if (paramView == null)
			paramView = LayoutInflater.from(CustSpinner.context).inflate(
					R.layout.option_item, null);

		this.mAdapter = new ArrayAdapter<String>(context, all, item);
		// this.mAdapter = ArrayAdapter.createFromResource(this,
		// R.array.Planets,
		// android.R.layout.simple_spinner_dropdown_item);

		/*
		 * Attach the mLocalAdapter to the spinner.
		 */

		setAdapter(this.mAdapter);

		/*
		 * Create a listener that is triggered when Android detects the user has
		 * selected an item in the Spinner.
		 */

		OnItemSelectedListener spinnerListener = new myOnItemSelectedListener(
				context, this.mAdapter);

		/*
		 * Attach the listener to the Spinner.
		 */

		setOnItemSelectedListener(spinnerListener);

		/*
		 * To demonstrate a failure in the preConditions test, uncomment the
		 * following line. The test will fail because the selection listener for
		 * the Spinner is not set.
		 */
		// spinner.setOnItemSelectedListener(null);

	}

	public CustSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);

		CustSpinner.context = context;
	}

	/**
	 * A callback listener that implements the
	 * {@link android.widget.AdapterView.OnItemSelectedListener} interface For
	 * views based on adapters, this interface defines the methods available
	 * when the user selects an item from the View.
	 * 
	 */
	public class myOnItemSelectedListener implements OnItemSelectedListener {

		/*
		 * provide local instances of the mLocalAdapter and the mLocalContext
		 */

		ArrayAdapter<String> mLocalAdapter;
		Context mLocalContext;

		/**
		 * Constructor
		 * 
		 * @param c
		 *            - The activity that displays the Spinner.
		 * @param ad
		 *            - The Adapter view that controls the Spinner. Instantiate
		 *            a new listener object.
		 */
		public myOnItemSelectedListener(Context c, ArrayAdapter<String> ad) {

			this.mLocalContext = c;
			this.mLocalAdapter = ad;

		}

		/**
		 * When the user selects an item in the spinner, this method is invoked
		 * by the callback chain. Android calls the item selected listener for
		 * the spinner, which invokes the onItemSelected method.
		 * 
		 * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android.widget.AdapterView,
		 *      android.view.View, int, long)
		 * @param parent
		 *            - the AdapterView for this listener
		 * @param v
		 *            - the View for this listener
		 * @param pos
		 *            - the 0-based position of the selection in the
		 *            mLocalAdapter
		 * @param row
		 *            - the 0-based row number of the selection in the View
		 */
		@Override
		public void onItemSelected(AdapterView<?> parent, View v, int pos,
				long row) {

			CustSpinner.this.mPos = pos;
			CustSpinner.this.mSelection = parent.getItemAtPosition(pos)
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

	public void onResume() {

		/*
		 * Try to read the preferences file. If not found, set the state to the
		 * desired initial values.
		 */

		if (!readInstanceState(context))
			setInitialState();
		setSelection(getSpinnerPosition());

	}

	public void onPause() {

		/*
		 * Save the state to the preferences file. If it fails, display a Toast,
		 * noting the failure.
		 */

		if (!writeInstanceState(context)) {
			Toast.makeText(context, "Failed to write state!", Toast.LENGTH_LONG)
					.show();
		}
	}

	/**
	 * Sets the initial state of the spinner when the application is first run.
	 */
	public void setInitialState() {

		this.mPos = DEFAULT_POSITION;

	}

	/**
	 * Read the previous state of the spinner from the preferences file
	 * 
	 * @param c
	 *            - The Activity's Context
	 */
	public boolean readInstanceState(Context c) {

		/*
		 * The preferences are stored in a SharedPreferences file. The abstract
		 * implementation of SharedPreferences is a "file" containing a hashmap.
		 * All instances of an application share the same instance of this file,
		 * which means that all instances of an application share the same
		 * preference settings.
		 */

		/*
		 * Get the SharedPreferences object for this application
		 */

		SharedPreferences p = c.getSharedPreferences(PREFERENCES_FILE,
				Context.MODE_WORLD_READABLE);
		/*
		 * Get the position and value of the spinner from the file, or a default
		 * value if the key-value pair does not exist.
		 */
		this.mPos = p.getInt(POSITION_KEY, CustSpinner.DEFAULT_POSITION);
		this.mSelection = p.getString(SELECTION_KEY, "");

		/*
		 * SharedPreferences doesn't fail if the code tries to get a
		 * non-existent key. The most straightforward way to indicate success is
		 * to return the results of a test that SharedPreferences contained the
		 * position key.
		 */

		return (p.contains(POSITION_KEY));

	}

	/**
	 * Write the application's current state to a properties repository.
	 * 
	 * @param c
	 *            - The Activity's Context
	 * 
	 */
	public boolean writeInstanceState(Context c) {

		/*
		 * Get the SharedPreferences object for this application
		 */

		SharedPreferences p = c.getSharedPreferences(
				CustSpinner.PREFERENCES_FILE, Context.MODE_WORLD_READABLE);

		/*
		 * Get the editor for this object. The editor interface abstracts the
		 * implementation of updating the SharedPreferences object.
		 */

		SharedPreferences.Editor e = p.edit();

		/*
		 * Write the keys and values to the Editor
		 */

		e.putInt(POSITION_KEY, this.mPos);
		e.putString(SELECTION_KEY, this.mSelection);

		/*
		 * Commit the changes. Return the result of the commit. The commit fails
		 * if Android failed to commit the changes to persistent storage.
		 */

		return (e.commit());

	}

	public int getSpinnerPosition() {
		return this.mPos;
	}

	public void setSpinnerPosition(int pos) {
		this.mPos = pos;
	}

	public String getSpinnerSelection() {
		return this.mSelection;
	}

	public void setSpinnerSelection(String selection) {
		this.mSelection = selection;
	}
}
