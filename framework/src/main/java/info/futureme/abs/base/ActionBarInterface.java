package info.futureme.abs.base;

/**
 * Created by hippo on 12/18/15.
 * actionbar interface used in single fragment activity
 */
public interface ActionBarInterface {

    //get actionbar right resourceid
    int getActionBarRightResourceId();

    //on actionbar right click
    int getActionBarRight2ResourceId();

    //on right2 button click
    void onActionBarRight2Click();

    //on right click
    void onActionBarRightClick();

    void onActionBarTitleRightClick();
    void onActionBarTitleLeftClick();
    //onback pressed
    boolean onBackPressed();
}
