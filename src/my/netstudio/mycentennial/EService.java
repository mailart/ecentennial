package my.netstudio.mycentennial;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

class globalconfig{
	public int updatecycle;
	public boolean is3gdata;
	public boolean hidehistorydata;
}

public class EService extends Service {
	private boolean isCreated=false;
	public static ecentennial e=null;
	public static boolean ischeckprint=false;
	public static boolean isupdatecircle=false;
	public static globalconfig gconfig;
	public static EService pservice=null;
	public static int notifyid=0x34234;
	public static Thread t=null;
	public static boolean isfocuscheck=false;
	public static Context gContent;
	public static boolean isdownloadin3g=false;
	AlarmManager am = null;
	PendingIntent pi=null;
	
	
	Runnable go=new Runnable(){
		String aaa="";
		public void run() {
			Date now=new Date();
			 SimpleDateFormat f=new SimpleDateFormat("MM-dd-yyyy HH:mm");
			  
			
			WritetoEndofFile("/sdcard/output.txt", f.format(now)+"\tsystem service start\r\n");
			
			try{
				try{
		        	String accountinfo=ecentennial.ReadsSysFile(gContent,"Account");
					if(accountinfo.length()==0){//no account info
						synchronized(EService.t){
							EService.t.wait();
						}	
					}
		        }
		        catch(Exception e){}
					
				int isfirstrun=e.InitSiteSnapShot();
				switch(isfirstrun){
					case -1:{
						PushToNotifybar(0,"eCentennial","Init error!","");
						break;
					}
					case 1:{//login failed
						e.WriteSysFile("Account", "");
						PushToNotifybar(1,"eCentennial","Login failed, Check it to input account again!","");
						synchronized(EService.t){
							EService.t.wait();
						}
						e.InitSiteSnapShot();
						break;
					}
				}
				
				if(MainActivity.myHandler!=null){//MainActivity.pmain!=null&&!MainActivity.pmain.isFinishing()){
					
					Message handlemsg = MainActivity.myHandler.obtainMessage();  
					handlemsg.obj = "2:";  
			        MainActivity.myHandler.sendMessage(handlemsg);  
				}
				
				while(true){
					
					EService.isupdatecircle=true;
					
					synchronized(EService.t){
							
						am.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+gconfig.updatecycle*60*1000,pi);  //set next wake up time
						EService.t.wait();
						
						if(!EService.gconfig.is3gdata&&!EService.isWifiConnected())
							EService.t.wait();									
					}
					
					CheckUpdate();
					
					if(MainActivity.myHandler!=null){
						
						Message handlemsg = MainActivity.myHandler.obtainMessage();  
						handlemsg.obj = "1:";  
				        MainActivity.myHandler.sendMessage(handlemsg);  
					}
					
					 now=new Date();
					  f=new SimpleDateFormat("MM-dd-yyyy HH:mm");
					  
					aaa="start:"+f.format(now)+"\r\n";
					WritetoEndofFile(e.sdrootpath+"output.txt", aaa);
					
					
					int ret=0;
					try{
						if(IsRunTime()||isfocuscheck)
							ret=e.CheckUpdate();
						isfocuscheck=false;
					}
					catch(Exception er){
						
						WritetoEndofFile(e.sdrootpath+"output.txt", "error\r\n");
					}
				
					switch(ret){
						case 10:{
							PushToNotifybar(0,"eCentennial","eCentennial has been updated!","");
							break;
						}
						case 0:{//normal
							
							break;
						}
						case 1:{//login failed
							e.WriteSysFile("Account", "");
							PushToNotifybar(1,"eCentennial","Login failed, Check it to input account again!","");
							synchronized(EService.t){
								EService.t.wait();
							}	
							break;
						}
						case 2:{//network error
							
							if(MainActivity.myHandler!=null){
								Message handlemsg = MainActivity.myHandler.obtainMessage();  
								handlemsg.obj = "9:Server timeout or Network is not avaliable, connection failed.";  
						        MainActivity.myHandler.sendMessage(handlemsg);  
							}
							
							break;
						}
						case 3:{//unknown site may be updated
							
							break;
						}
					}
					
					
					now=new Date();
					  f=new SimpleDateFormat("MM-dd-yyyy HH:mm");
					  
					aaa="finished:"+f.format(now)+"\r\n";
					WritetoEndofFile(e.sdrootpath+"output.txt", aaa);
					
					
					if(MainActivity.myHandler!=null){
						
						Message handlemsg = MainActivity.myHandler.obtainMessage();  
						handlemsg.obj = "2:";  
				        MainActivity.myHandler.sendMessage(handlemsg);  
					}
					
				}
			}
			
			catch(Exception er){
				
				String aaaa=er.toString();
				aaaa+="";
			}	
		}
    };
    public static boolean IsRunTime(){
    	boolean ret=true;
    	Date now=new Date();
    	int hours=now.getHours();
    	int day=now.getDay();
    	if(day==6)
    		return false;
    	if(hours>=0&&hours<8)
    		return false;
    	return ret;
    }
    public static void WritetoEndofFile(String filename,String str){
    	
    	try{
    		   File saveFile=new File(filename);
    		   if(saveFile.exists()==false)
    			   saveFile.createNewFile();
    		   
    		   FileOutputStream fout=new FileOutputStream(saveFile,true);//.o.pmain.openFileOutput(filename,0);
    	
    		   byte[] bytes=str.getBytes();
    		   fout.write(bytes);
    		   fout.close();
    		   
    	}
    	catch(Exception e){
    		String aa=e.toString();
    		Log.e("",aa);
    	}
    	
    }
    public static boolean isWifiConnected()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)gContent.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(wifiNetworkInfo.isConnected())
        {
            return true ;
        }
     
        return false ;
    }

	private boolean IslastestVer(String str){
		boolean ret=false;
		String logs=e.ReadsSysFile(EService.gContent,"updatelog");
		
		if(logs.indexOf(str)!=-1)
			ret=true;
		
		logs+=str+",";
		e.WriteSysFile("updatelog", logs);
		return ret;
	}
    public void CheckUpdate(){
    	//pcu:msg:url
    	try{
    	int vercounter=this.getApplicationContext().getPackageManager().getPackageInfo("my.netstudio.mycentennial", 0).versionCode;
			
    	PackageManager pm = this.getApplicationContext().getPackageManager();
		PackageInfo packageInfo = pm.getPackageArchiveInfo(e.sdrootpath+"/update.apk", PackageManager.GET_ACTIVITIES);
		
			
	    	String str=MainActivity.httpsmgr.GetData("http://www.elearntech.info/pcu","",new String( ""),new String( ""),0,0);
	    	if(str.length()>0&&str.indexOf(":")!=-1&&str.indexOf("pcu")==0){
	    		String ver,msg,file;
	    		ver=str.substring(0,str.indexOf (":"));
	    		str=str.substring(str.indexOf (":")+1);
	    		msg=str.substring(0,str.indexOf (":"));
	    		file=str.substring(str.indexOf (":")+1);
	    		
	    		if(!IslastestVer(ver)){
	    			if(file.indexOf(".apk")!=-1){//update mode
	    				
	    				for(int start=0;start<3&&!MainActivity.httpsmgr.GetFile(file, "", "", e.sdrootpath+"/update.apk");start++){
	    				}
	    				
	    				PackageManager pmv = this.getApplicationContext().getPackageManager();
	    				PackageInfo packageInfo1 = pmv.getPackageArchiveInfo(e.sdrootpath+"/update.apk", PackageManager.GET_ACTIVITIES);
	    				
	    				if(packageInfo1!=null&&packageInfo1.versionCode>vercounter)
	    					PushToNotifybar(3,"New version for Mobile centennial",msg,e.sdrootpath+"/update.apk");
	    				
	    			}
	    			else{//html
	    				PushToNotifybar(2,"System Notification",msg,file);
	    			}
	    		}
	    	}
	    	str=MainActivity.httpsmgr.GetData("http://www.elearntech.info/ppu","",new String( ""),new String( ""),0,0);
	    	if(str.length()>0&&str.indexOf(":")!=-1&&str.indexOf("ppu")==0){
	    		String ver,url;
	    		ver=str.substring(0,str.indexOf (":"));
	    		str=str.substring(str.indexOf (":")+1);
	    		url=str.substring(0,str.indexOf (":"));
	    		
	    		if(!IslastestVer(ver)){
	    			
	    				String wifipath="";
    					boolean sdCardExist = Environment.getExternalStorageState() .equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
    					if (sdCardExist)
    					{
    						File sdDir = Environment.getExternalStorageDirectory();//获取跟目录
    						wifipath=sdDir.toString()+"/wifi printer/";
    					}
	    						
	    				for(int start=0;start<3&&!MainActivity.httpsmgr.GetFile(url, "", "", wifipath+"printerlist");start++){
	    				}
	    				
	    			
	    		}
	    	}
    	}catch(Exception er){
			
			String aaaa=er.toString();
			aaaa+="";
		}	
    }
    public void ClearNotifybar(){

    	NotificationManager notificationManager = (NotificationManager) this
                .getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(notifyid);//(R.string.app_name);
    }
    public void PushToNotifybar(int type,String title,String msg,String path){
    	String ns = Context.NOTIFICATION_SERVICE;

    	NotificationManager mNotificationManager = (NotificationManager)getSystemService(ns);
		//新建状态栏通知
    	Notification baseNF = new Notification();
		 
		//设置通知在状态栏显示的图标
		baseNF.icon = R.drawable.esmall;
		
		//通知时在状态栏显示的内容
		baseNF.tickerText = msg;
		baseNF.defaults = Notification.DEFAULT_SOUND;
		baseNF.defaults |= Notification.DEFAULT_VIBRATE;
		

		
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
		PendingIntent logincontentIntent = PendingIntent.getActivity(this, 0, new Intent(this, Login.class), 0);
		if(type==1)//error
			contentIntent=logincontentIntent;
		else if(type==2)//html
		{
			Uri uri = Uri.parse(path);  
			contentIntent= PendingIntent.getActivity(this, 0, new Intent(Intent.ACTION_VIEW,uri),0);
		}
		else if(type==3){//apk
			Intent intent = new Intent();  
	        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
	        intent.setAction(android.content.Intent.ACTION_VIEW);  
	        intent.setDataAndType(Uri.fromFile(new File(path)),   "application/vnd.android.package-archive");  
	      
	        
			contentIntent=PendingIntent.getActivity(this, 0, intent,0);
		}
			
		baseNF.setLatestEventInfo(EService.this.getBaseContext(),title, msg, contentIntent);
	
		baseNF.ledARGB = 0xff00ff00;  
		baseNF.ledOnMS = 300;  
		baseNF.ledOffMS = 1000;  
		baseNF.flags |= Notification.FLAG_SHOW_LIGHTS;  
	    
		//发出状态栏通知
		//The first parameter is the unique ID for the Notification 
		// and the second is the Notification object.
		mNotificationManager.notify(notifyid, baseNF);
		
		if(MainActivity.myHandler!=null){
			
			Message handlemsg = MainActivity.myHandler.obtainMessage();  
			handlemsg.obj = "0:";  
	        MainActivity.myHandler.sendMessage(handlemsg);  
		}
		/*
		 * //2、在程序代码中使用RemoteViews的方法来定义image和text。然后把RemoteViews对象传到contentView字段

RemoteViews contentView = new RemoteViews(getPackageName(),R.layout.view);

contentView.setImageViewResource(R.id.image,R.drawable.icon);

contentView.setTextViewText(R.id.text,”Hello,this message is in a custom expanded view”);

notification.contentView = contentView;

//3、为Notification的contentIntent字段定义一个Intent(注意，使用自定义View不需要 setLatestEventInfo()方法)

Intent notificationIntent = new Intent(this,Main.class);

PendingIntent contentIntent = PendingIntent.getActivity(this,0,notificationIntent,0);

notification.contentIntent = contentIntent;

//4、发送通知

mNotificationManager.notify(2,notification);
		 * */
    }
	@Override
	public IBinder onBind(Intent intent) {
		onCreate();
		// TODO Auto-generated method stub
		return null;
	}
	static public void saveConfig(){
		String str;
		str=String.valueOf(EService.gconfig.updatecycle)+","+String.valueOf(EService.gconfig.is3gdata)+","+String.valueOf(EService.gconfig.hidehistorydata);
		EService.e.WriteSysFile("gconfig", str);
		
	}
	static public void backup(){
		String str;
		str=EService.e.ReadsSysFile(EService.gContent,"data");
		e.WriteDBFile(e.sdrootpath+"/data", str);
		str=e.ReadsSysFile(EService.gContent,"download");
		e.WriteDBFile(e.sdrootpath+"/download", str);
	}
	static public void reset(){
		e.WriteSysFile("data", "");
		e.WriteSysFile("download", "");
		e.WriteSysFile("courses", "");
		e.WriteSysFile("Account", "");
	}
	static public void restore(){
		String temp=EService.e.ReadsDBFile(e.sdrootpath+"/data");	
		String str=e.ReadsSysFile(EService.gContent,"data");
		String donotexist="";
		while(temp.length()>0){
			String item=temp.substring(0,temp.indexOf("@#@@#@"));
			temp=temp.substring(temp.indexOf("@#@@#@")+6);	
				
			if(str.indexOf(item)==-1)
			{
				donotexist+=item+"@#@@#@";
				NotifyItem pnotify=new NotifyItem(item);//init from str
				e.allnotifyitems.add(pnotify);
				e.notifyhash.put(pnotify.GetTitle()+pnotify.GetSubject()+pnotify.GetTypestr(), pnotify);
			}
		}
		if(donotexist.length()>0){
			str=donotexist+str;
			e.WriteSysFile("data", str);
		}
		
		donotexist="";
		temp=EService.e.ReadsDBFile(e.sdrootpath+"/download");	
		str=e.ReadsSysFile(EService.gContent,"download");
		while(temp.length()>0){
			String item=temp.substring(0,temp.indexOf("@#@@#@"));
			temp=temp.substring(temp.indexOf("@#@@#@")+6);
				
			if(str.indexOf(item)==-1)
			{
				donotexist+=item+"@#@@#@";
				DownloadTaskItem pnotify=new DownloadTaskItem(item);
				e.alldownloadtask.add(pnotify);
				e.downloadtaskhash.put(pnotify.m_taskname, pnotify);
			}
		}
		if(donotexist.length()>0){
			str=donotexist+str;
			e.WriteSysFile("download", str);
		}

	}
	private void readConfig(){
		String str=EService.e.ReadsSysFile(EService.gContent,"gconfig");
		if(str.length()>0){
			EService.gconfig.updatecycle=Integer.valueOf(str.substring(0,str.indexOf(",")));
			str=str.substring(str.indexOf(",")+1);
			EService.gconfig.is3gdata=Boolean.valueOf(str.substring(0,str.indexOf(",")));
			str=str.substring(str.indexOf(",")+1);
			EService.gconfig.hidehistorydata=Boolean.valueOf(str);
		}
	}
    public void onCreate() {
    	if(isCreated)
    		return;
    	isCreated=true;
    	super.onCreate();
    	
    	pservice=this;
    	gContent=this.getApplicationContext();
		EService.e= new ecentennial();
        //default 
    	gconfig=new globalconfig();
    	gconfig.updatecycle=20;
    	gconfig.is3gdata=false;
    	gconfig.hidehistorydata=true;
    	readConfig();
    	
    	am = (AlarmManager)getSystemService(ALARM_SERVICE);
    	Intent intent = new Intent("UPDATECLOCK");  
    //	intent.putExtra("","");     
    	pi = PendingIntent.getBroadcast(this,0,intent,0);
    			
    			
    			
    	
        
        t = new Thread(go);
        
        t.start();
        
    	
    	
		
        
    }
}
