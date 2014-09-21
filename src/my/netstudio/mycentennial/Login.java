package my.netstudio.mycentennial;

import java.io.File;

import my.netstudio.mycentennial.MainActivity.NotifyMsgItem;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends Activity implements OnClickListener , Runnable{
	public  static EditText usr=null;
	public  static EditText pwd=null;
	public  static CheckBox mibiledata=null;
	public  static TextView retstr=null,errorret=null;
	public  static Button b=null;
	public static ProgressBar bar;
	private int logintype=0;
	private String accountstr="";
	String updatefilepath="";
	
	private Handler myHandler = null;
	
	private ServiceConnection conn = new ServiceConnection() { 

			public void onServiceConnected(ComponentName name, IBinder service) {
				// TODO Auto-generated method stub
			}



			public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub
				
			} 

	    }; 
	    
	public void run() {
		
		if(logintype==0){//user input
			int ret=LogginProcess();
        	if(ret==0){//alread logging
        		//close it and open main
        		Message msg = myHandler.obtainMessage();  
				msg.obj = "5:";  
		        myHandler.sendMessage(msg); 
		
        	}
        	else{//error
        		//show inputs
        		Message msg = myHandler.obtainMessage();  
				msg.obj = "3:";  
		        myHandler.sendMessage(msg); 
        	}
		}
		else if(logintype==1){//auto mode
		
			Message msg = myHandler.obtainMessage();  
	        msg.obj = "0:Logging...it may take few minutes";  
	        myHandler.sendMessage(msg); 

	        
	        int countd=0;
    		int ret=-1;
        	do{
        		try{
					
					Thread.sleep(1000);
				}
				catch(Exception e){
									
				}
        		ret=EService.e.GetLogintest();
        		countd++;
        		if(ret==2)
        			ret=LogginProcess();
        		if(ret==0){//ok
        			
        			msg = myHandler.obtainMessage();  
        	        msg.obj = "0:Logging successful...Processing data";  
        	        myHandler.sendMessage(msg); 

        			while(!EService.e.isInitFinished()){
        				
        				try{
        					
        					Thread.sleep(1000);
        				}
        				catch(Exception e){
        					
        				}
        			}
        			//close it and open main
        			msg = myHandler.obtainMessage();  
    				msg.obj = "5:";  
    		        myHandler.sendMessage(msg); 
        			return;
        		}
				

        	}while(ret<0);
        	
        	msg = myHandler.obtainMessage();  
			msg.obj = "3:";  
	        myHandler.sendMessage(msg); 
        	
        	if(ret==2){
        		msg = myHandler.obtainMessage();  
				msg.obj = "1:Network error";  
		        myHandler.sendMessage(msg); 
			}
        	if(ret==3){
        		msg = myHandler.obtainMessage();  
				msg.obj = "1:Format error,the site has been updated you may need to update software!";  
		        myHandler.sendMessage(msg); 
			}
			else if(ret==1){
				msg = myHandler.obtainMessage();  
				msg.obj = "1:Invald Username or Password";  
		        myHandler.sendMessage(msg); 
				
			}
        	
		}
	}
	private void Msgbox(String msg){
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);  
        builder.setMessage(msg)  
               .setCancelable(false)  
               .setPositiveButton("Yes", null);  
		
		builder.create().show();
    }
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String msg;
		if(v==b){
			if(usr.getText().toString().length()==0)
			{
				msg=getString(R.string.usenameerror);
				Msgbox(msg);
				return;
			}
			else if(pwd.getText().toString().length()==0)
			{
				msg=getString(R.string.passworderror);
				Msgbox(msg);
				return;
			}
			else if(!isWifiConnected()&&!mibiledata.isChecked()){
				msg=getString(R.string.wifierror);
				Msgbox(msg);
				return;
			}
				
			EService.gconfig.is3gdata=mibiledata.isChecked();
			EService.saveConfig();
			
			logintype=0;
			ShowInputs(false);
			new Thread(this).start();
		}
		if(v==usr){
			if(usr.getText().toString().compareToIgnoreCase("username")==0)
				usr.setText("");
		}
	}
	public boolean isWifiConnected()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(wifiNetworkInfo.isConnected())
        {
            return true ;
        }
     
        return false ;
    }
	void UpdateConfirm(){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);  
        builder.setMessage(getString(R.string.updateconfirm))  
               .setCancelable(false)  
               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {  
                   public void onClick(DialogInterface dialog, int id) {  
                	   Intent intent = new Intent();  
           	        	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
           	        	intent.setAction(android.content.Intent.ACTION_VIEW);  
           	        	intent.setDataAndType(Uri.fromFile(new File(Login.this.updatefilepath)),"application/vnd.android.package-archive");  
           	        	Login.this.startActivity(intent);
           	        	
           	        	dialog.dismiss();
           	        	Login.this.finish();
                	   
                   }  
               }).setNegativeButton("No", new DialogInterface.OnClickListener() {  
                   public void onClick(DialogInterface dialog, int id) {  
                	   dialog.dismiss();
                   }  
               });  
               
        
		builder.create().show();
	}
	void CheckUpdateApk(){
		
		boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); //ÅÐ¶Ïsd¿¨ÊÇ·ñ´æÔÚ
		if (sdCardExist)
		{
			updatefilepath=Environment.getExternalStorageDirectory().toString()+"/ecentennial/update.apk";
		}		
					
		int vercounter=1000;
		try{
			vercounter=this.getApplicationContext().getPackageManager().getPackageInfo("my.netstudio.mycentennial", 0).versionCode;
		}
		catch(Exception e){}
					
	    PackageManager pmv = this.getApplicationContext().getPackageManager();
	    PackageInfo packageInfo1 = pmv.getPackageArchiveInfo(updatefilepath, PackageManager.GET_ACTIVITIES);
	    				
	    if(packageInfo1!=null&&packageInfo1.versionCode>vercounter)
	    	UpdateConfirm();		

	    
	}
	public void onCreate(Bundle savedInstanceState) {
	//	this.bindService(new Intent(this,EService.class),conn,BIND_AUTO_CREATE);
		this.startService(new Intent(this,EService.class));
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logging);
        
        
        usr=(EditText) this.findViewById(R.id.user);
		pwd=(EditText) this.findViewById(R.id.password);
		b=(Button) this.findViewById(R.id.button1);
		retstr=(TextView)this.findViewById(R.id.retstr);
		errorret=(TextView)this.findViewById(R.id.error);
		bar=(ProgressBar)this.findViewById(R.id.progress);
		mibiledata=(CheckBox) this.findViewById(R.id.mibiledata);
	//	mibiledata.setChecked(EService.gconfig.is3gdata);
		
		
		TextPaint tp = retstr .getPaint();
		tp.setFakeBoldText(true);
		tp = errorret .getPaint();
		tp.setFakeBoldText(true);
		
		errorret.setText("");
		usr.setOnClickListener(this);
		b.setOnClickListener(this);
		
		myHandler=new Handler(Looper.myLooper ()) {  
			  
		    public void handleMessage(Message msg) {  
		    	switch(Integer.parseInt(((String)msg.obj).substring(0,1))){
		    		case 0:{//update ret
		    			retstr.setText(((String)msg.obj).substring(2));
		    			break;
		    		}
		    		case 1:{//update error
		    			errorret.setText(((String)msg.obj).substring(2));
		    			break;
		    		}
		    		case 2:{
		    			
		    			break;
		    		}
					case 3:{//show inputs
						ShowInputs(true);
						break;
					}
					case 4:{
						
						break;
					}
					case 5:{//open details
						
						Intent intent=new Intent(Login.this,MainActivity.class);
				        startActivity(intent);   
						finish();
						break;
					}
		    	}
		      //  updateUI((String)msg.obj);  
		    }  
		};  
		
		
        accountstr=ecentennial.ReadsSysFile(this.getApplicationContext(),"Account");
        if(accountstr.length()>0){
        	
     //   	while(!EService.e.isInitFinished()){
				
				
	//		}
        	Intent intent=new Intent(Login.this,MainActivity.class);
	        startActivity(intent);   
			finish();
			
			/*
        	usr.setText(accountstr.substring(0, accountstr.indexOf("%^&*")));
        	pwd.setText(accountstr.substring(accountstr.indexOf("%^&*")+4));
        	ShowInputs(false);
        	logintype=1;
        	
        	new Thread(this).start();
        	*/
        }
        else{
        	CheckUpdateApk();
        	ShowInputs(true);
        }
    }
	private int LogginProcess(){
		
		Message msg = myHandler.obtainMessage();  
        msg.obj = "1:";  
        myHandler.sendMessage(msg);  
        
        msg = myHandler.obtainMessage();  
        msg.obj = getString(R.string.login) ;
        myHandler.sendMessage(msg); 
       
		int ret=EService.e.Logintest(usr.getText().toString(), pwd.getText().toString());
		if(ret==0){
			msg = myHandler.obtainMessage();  
			msg.obj = getString(R.string.loginfinish) ;//"0:Logging successful...Processing data...";  
	        myHandler.sendMessage(msg); 
	        
	        if(pwd.getText().toString().length()>0)
				EService.e.WriteSysFile("Account", usr.getText().toString()+"%^&*"+pwd.getText().toString());
			
			synchronized(EService.t){
				EService.t.notify();
			}
			while(!EService.e.isInitFinished()){
				
				try{
					
					Thread.sleep(1000);
				}
				catch(Exception e){
					
				}
			}
			return 0;
		}
		else{
			
			if(ret==2){
				msg = myHandler.obtainMessage();  
				msg.obj = "1:Network error";  
		        myHandler.sendMessage(msg); 
			}
			if(ret==3){
        		msg = myHandler.obtainMessage();  
				msg.obj = "1:Format error,the site has been updated you may need to update software!";  
		        myHandler.sendMessage(msg); 
			}
			else if(ret==1){
				msg = myHandler.obtainMessage();  
				msg.obj = "1:Invald Username or Password";  
		        myHandler.sendMessage(msg); 
			}
				 
			return  ret;
		}
	}
	private void ShowInputs(boolean show){
		
		bar.setVisibility(show==false?View.VISIBLE:View.INVISIBLE);
		retstr.setVisibility(show==false?View.VISIBLE:View.INVISIBLE);
		usr.setVisibility(show?View.VISIBLE:View.INVISIBLE);
		pwd.setVisibility(show?View.VISIBLE:View.INVISIBLE);
		b.setVisibility(show?View.VISIBLE:View.INVISIBLE);
		mibiledata.setVisibility(show?View.VISIBLE:View.INVISIBLE);
		
	}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

}
