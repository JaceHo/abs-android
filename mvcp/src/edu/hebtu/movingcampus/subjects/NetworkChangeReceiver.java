package edu.hebtu.movingcampus.subjects;


import java.util.ArrayList;
import java.util.List;

import edu.hebtu.movingcampus.utils.NetWorkHelper;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NetworkChangeReceiver extends BroadcastReceiver {
	private static List<NetworkchangeListener> listener=new ArrayList<NetworkChangeReceiver.NetworkchangeListener>();;

	@Override
	public void onReceive(final Context context, final Intent intent) {

		if(NetWorkHelper.getConnectivityStatus(context)==NetWorkHelper.TYPE_NOT_CONNECTED)
			for (NetworkchangeListener l: listener) {
				l.onDataDisabled();
			}
		else 
			for (NetworkchangeListener l: listener) {
				l.onDataEnabled();
			}
		Toast.makeText(context, NetWorkHelper.getConnectivityStatusString(context), Toast.LENGTH_LONG).show();
	}

	public interface NetworkchangeListener{
		public void onDataEnabled();
		public void onDataDisabled();
	}

	public static void registNetWorkListener(NetworkchangeListener lis){
		listener.add(lis);
	}
	public static void unRegistNetworkListener(NetworkchangeListener lis){
		listener.remove(lis);
	}
}
