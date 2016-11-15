package info.futureme.abs.base;

import android.os.Bundle;

import butterknife.ButterKnife;

/**
 * InjectableActivity will be extended by every activity in the app, and it hides
 * common logic for concrete activities, like initial view injections
 */
public abstract class InjectableActivity extends FBaseActivity {
	/**
	 * activity oncreate to bind butterknife
	 * 
	 * @param savedInstanceState
	 */
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(provideContentViewId());
        ButterKnife.bind(this);
    }

    /**
	 * provide contentviewId to be injected for butterknife, it is also known as
	 * contentviewid here.
	 */
    protected abstract int provideContentViewId();
}
