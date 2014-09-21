package my.netstudio.mycentennial;


import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

public class MsgDispatcher extends BroadcastReceiver {

	private boolean isServiceRunning(Context context) {
	    ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	    	String aaa=service.service.getClassName();
	        if ("my.netstudio.mycentennial.EService".equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
			context.startService(new Intent(context,EService.class));
		else if(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){//||intent.getAction().equalsIgnoreCase("android.net.wifi.WIFI_STATE_CHANGED")){
			NetworkInfo info = (NetworkInfo)intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
	
			if(isServiceRunning(context)){
				if (info.getState().equals(NetworkInfo.State.CONNECTED)&&EService.isupdatecircle)
				{
					synchronized(EService.t){
						EService.t.notify();
						EService.t.notify();
					}
				}
			}
			else
				context.startService(new Intent(context,EService.class));
		}
		else if(intent.getAction().equals("UPDATECLOCK")){
			if(isServiceRunning(context)){
				synchronized(EService.t){
					EService.t.notify();
				}
			}
			else
				context.startService(new Intent(context,EService.class));
		}
	}
	
}
