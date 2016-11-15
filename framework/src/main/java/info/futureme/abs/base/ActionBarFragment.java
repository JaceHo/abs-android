package info.futureme.abs.base;

/**
 * fragment with actionbar defined in screen
 * @author Jeffrey
 * @version 1.0
 * @updated 26-一月-2016 13:27:37
 */
public abstract class ActionBarFragment extends InjectableFragment implements ActionBarInterface {
	/**
	 * actionbar second right image icon resource id.
	 */
    public int getActionBarRight2ResourceId(){
        return 0;
    }

	/**
	 * actionbar second right image icon from right 
	 */
    public void onActionBarRight2Click(){

    }

	/**
	 * actionbar right resource for actionbar displaying
	 * @return
	 */
	public int getActionBarRightResourceId(){
		return 0;
	}

	/**
	 * on right click
	 */
	public void onActionBarRightClick(){

	}

	/**
	 * on back pressed
	 * @return
	 */
	public boolean onBackPressed(){
		return false;
	}
}
