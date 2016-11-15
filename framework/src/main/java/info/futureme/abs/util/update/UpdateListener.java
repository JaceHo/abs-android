package info.futureme.abs.util.update;

import info.futureme.abs.entity.UpdateResponse;

/**
 * Created by Jeffrey on 9/5/16.
 */
public interface UpdateListener {
    void onUpdateReturned(UpdateResponse result);
}
