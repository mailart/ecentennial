package my.netstudio.mycentennial;




import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.google.ads.AdRequest;
import com.google.ads.AdView;


import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class MainActivity extends Activity implements OnItemLongClickListener,OnTabChangeListener,OnClickListener,OnItemClickListener,OnTouchListener{
	
	public static Httpmanager httpsmgr;
	public static WebView web=null;
	public static CookieManager cookieManager=null ;
	public static MainActivity pmain=null;
	public static TextView courselable=null;
	public static ProgressBar bar;
	public static TabHost th=null;
	public static ImageView sync,favor,twitter,googleplus;
	public static String coursetext="";
	private static String scookie="";
	private static String surl="";
	public static Handler myHandler = null;
	public static ListView newslist,contextlist,gradelist,downloadlist;
	View newTab,contextTab,gradeTab,downloadtab;
	public static TextView txt1,txt2,txt3,txt4;
	private  int currentseltab=0;
	
	
	/*download confirm values*/
	public static DownloadTaskItem dtask;
	public static String taskname;
	public static String url;
	public static String cookie;
	public static String postparam;
	public static String filename;
	public static long objid;
	public static Object obj;
	public static Handler callback;
	public static int filefrom;
	public String updatefilepath="";
	
	private float lastx=-1,lasty=-1;
	private boolean isswap=false;
	
    public void LoadPage(String url,String cookies) {  
    	scookie=cookies;
    	surl=url;
    	
    	CookieSyncManager.createInstance(EService.gContent);  
        cookieManager.setAcceptCookie(true);  
        cookieManager.removeSessionCookie();//移除  
    //    url=url.replace("https", "http");
       cookieManager.setCookie("e.centennialcollege.ca", cookies.substring(0,cookies.indexOf(";")));//cookies是在HttpClient中获得的cookie  
        cookieManager.setCookie("e.centennialcollege.ca", cookies.substring(cookies.indexOf(";")+1));
        CookieSyncManager.getInstance().sync(); 
        String cookife = cookieManager.getCookie(url);
   //     try{
   //     Thread.sleep(3000);}catch(Exception e){}
        
    //    Map<String,String> cookie=new HashMap<String,String>();
    //    cookie.put("Cookie", cookies);
     //  MainActivity.web.loadUrl(url);//, cookie);
        
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(surl));
        startActivity(i); 
    }  
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	pmain=this;
    	EService.isdownloadin3g=false;
    
    	
    	cookieManager= CookieManager.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        AdView adView = (AdView) this.findViewById(R.id.adView1);//new AdView(this, AdSize.BANNER, "a1511b1f983d53c");
	    AdRequest request = new AdRequest();
	    adView.loadAd(request);

        bar=(ProgressBar)this.findViewById(R.id.checkstatus);
        bar.setVisibility(View.INVISIBLE);
        
        sync=(ImageView)this.findViewById(R.id.Sync);
        sync.setOnClickListener(this);
        favor=(ImageView)this.findViewById(R.id.Fav);
        favor.setOnClickListener(this);
        twitter=(ImageView)this.findViewById(R.id.twitter);
        twitter.setOnClickListener(this);
        googleplus=(ImageView)this.findViewById(R.id.google);
        googleplus.setOnClickListener(this);
        
        th=(TabHost)findViewById(R.id.tabhost);
        th.setup();
        th.setOnTabChangedListener(this);
        
        DisplayMetrics dm = new DisplayMetrics();   
        getWindowManager().getDefaultDisplay().getMetrics(dm);   
        int screenWidth = dm.widthPixels/4+20;   
        
        newTab = (View) LayoutInflater.from(this).inflate(R.layout.tabsheet, null);  
        TextView text0 = (TextView) newTab.findViewById(R.id.tab_label); 
        text0.setText("News"); 
        newTab.setMinimumWidth(screenWidth);
          
        contextTab = (View) LayoutInflater.from(this).inflate(R.layout.tabsheet, null);  
        TextView text1 = (TextView) contextTab.findViewById(R.id.tab_label);  
        text1.setText("Content");  
        contextTab.setMinimumWidth(screenWidth);
          
        gradeTab = (View) LayoutInflater.from(this).inflate(R.layout.tabsheet, null);  
        TextView text2 = (TextView) gradeTab.findViewById(R.id.tab_label);  
        text2.setText("Grade");  
        gradeTab.setMinimumWidth(screenWidth);
        
        downloadtab = (View) LayoutInflater.from(this).inflate(R.layout.tabsheet, null);  
        TextView text3 = (TextView) downloadtab.findViewById(R.id.tab_label);  
        text3.setText("Download");
        downloadtab.setMinimumWidth(screenWidth);
        

    /*    
        th.addTab( th .newTabSpec( "news" ).setIndicator( newTab ).setContent(
        		new TabHost.TabContentFactory() {

                    public View createTabContent(String tag) {
                    	LayoutInflater listitempane=LayoutInflater.from(MainActivity.this);
                        return listitempane.inflate(R.layout.tabpage, null);
                    }
                }));*/
        		
        th.addTab( th .newTabSpec( "news" ).setIndicator( newTab ).setContent(R.id.News ));
        th.addTab( th .newTabSpec( "context" ).setIndicator( contextTab ).setContent(R.id.Content ));
        th.addTab( th .newTabSpec( "grade" ).setIndicator(gradeTab ).setContent(R.id.Grade ));
        th.addTab( th .newTabSpec( "download" ).setIndicator(downloadtab ).setContent(R.id.Download ));
        th.setCurrentTab(currentseltab);
    	
  /*      
        int aaaaa=EService.e.courlist.size();
        Spinner  sp = (Spinner) findViewById(R.id.spinner1);
        List<String> lst =new ArrayList<String>(); 
        lst.add("All courses                              ");       
        for(int index=0;index<aaaaa;index++){
        	CourseInfo info=EService.e.courlist.get(index);
        	lst.add(info.GetName());
        }
        ArrayAdapter< String> adapter =new ArrayAdapter< String>( this,R.layout.spinnernormal,lst);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);
    //    LinearLayout l=new LinearLayout(this);
    //    LinearLayout.LayoutParams ll=new LinearLayout.LayoutParams(
    //    		LinearLayout.LayoutParams.WRAP_CONTENT,
    //    		LinearLayout.LayoutParams.WRAP_CONTENT
    //    );
    //    l.addView(sp,ll);
       */
        
        /*
         * Intent intent = getIntent();
                String action = intent.getAction();
                if(intent.ACTION_VIEW.equals(action)){
                             TextView tv = (TextView)findViewById(R.id.tvText);
                            tv.setText(intent.getDataString());
                }
         * */
      
        
        courselable=(TextView)this.findViewById(R.id.course);
        coursetext=courselable.getText().toString();
        (courselable).setOnClickListener(this);
      
        newslist=(ListView) this.findViewById(R.id.News);
        newslist.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        newslist.setAdapter(new NotifyMsgItem(NotifyItem.NEWS));
        newslist.setOnTouchListener(this);
        newslist.setOnItemClickListener(this);
        
        
        
        gradelist=(ListView) this.findViewById(R.id.Grade);
        gradelist.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        gradelist.setAdapter(new NotifyMsgItem(NotifyItem.GRADE));
        gradelist.setOnTouchListener(this);
        gradelist.setOnItemClickListener(this);
        
        contextlist=(ListView) this.findViewById(R.id.Content);
        contextlist.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        contextlist.setAdapter(new NotifyMsgItem(NotifyItem.COURSE));
        contextlist.setOnTouchListener(this);
        contextlist.setOnItemClickListener(this);
        
        downloadlist=(ListView) this.findViewById(R.id.Download);
        downloadlist.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        downloadlist.setAdapter(new NotifyMsgItem(NotifyItem.DOWNLOAD));
        downloadlist.setOnTouchListener(this);
        downloadlist.setOnItemClickListener(this);
        downloadlist.setOnItemLongClickListener(this);
        
    
        txt1=(TextView) newTab.findViewById(R.id.num);;
        txt2=(TextView)  contextTab.findViewById(R.id.num);;
        txt3=(TextView)  gradeTab.findViewById(R.id.num);
        txt4=(TextView)  downloadtab.findViewById(R.id.num);
        
    //    OnFlash();
 
        if(EService.e!=null)
        	this.setTitle("eCentennial-"+((EService.e.username.length()==0)?"offline":EService.e.username));
        
    	/*
	    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
	    	.detectDiskReads()
	    	.detectDiskWrites()
	    	.detectNetwork() // 这里可以替换为detectAll() 就包括了磁盘读写和网络I/O
	    	.penaltyLog() //打印logcat，当然也可以定位到dropbox，通过文件保存相应的log
	    	.build());
	  */  	

  
       
        /* 
        web =new WebView(this);
       web.getSettings().setJavaScriptEnabled(true);  
        try{
        	
        //	web.loadUrl("http://www.google.com");
        }
        catch(NullPointerException e)
        {
        	
        	String aaa=e.toString();
        	Log.e("asdf", aaa);
        }
        
        Button but=(Button)this.findViewById(R.id.button1s);
        but.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				CookieSyncManager.createInstance(MainActivity.gContent);  
		        cookieManager.setAcceptCookie(true);  
		        cookieManager.removeSessionCookie();//移除  
		    //    url=url.replace("https", "http");
		       cookieManager.setCookie("e.centennialcollege.ca", MainActivity.scookie.substring(0,MainActivity.scookie.indexOf(";")));//cookies是在HttpClient中获得的cookie  
		        cookieManager.setCookie("e.centennialcollege.ca", MainActivity.scookie.substring(MainActivity.scookie.indexOf(";")+1));
		        CookieSyncManager.getInstance().sync(); 
		        String cookife = cookieManager.getCookie(MainActivity.surl);
		   //     try{
		   //     Thread.sleep(3000);}catch(Exception e){}
		        
		    //    Map<String,String> cookie=new HashMap<String,String>();
		    //    cookie.put("Cookie", cookies);
		        MainActivity.web.loadUrl(MainActivity.surl);//, cookie);
		        
		        
			}});
        
        
   // 	this.setScrollBarStyle(SCROLLBARS_OUTSIDE_OVERLAY);
        */
        
        
       
        
        PackageManager pm = this.getApplicationContext().getPackageManager();
        List<PackageInfo> activityList = pm.getInstalledPackages(0);
        boolean isecentennialinstalled=false;
        boolean isprinterinstalled=false;
        for (final PackageInfo app : activityList) {
		    
		    if((app.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0){
		    	
		    	String aaa=app.applicationInfo.packageName;
		    	if(aaa.toLowerCase().indexOf("mycentennial")!=-1)
		    		isecentennialinstalled=true;
		    	else if(aaa.toLowerCase().indexOf("centennial.wifi")!=-1)
		    		isprinterinstalled=true;
		    }
		}
        
        if(!isprinterinstalled&&EService.ischeckprint==false){
        	EService.ischeckprint=true;
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);  
            builder.setMessage(this.getString(R.string.installtips))
            		.setCancelable(false)  
                   .setPositiveButton("Yes", new DialogInterface.OnClickListener() {  
                       public void onClick(DialogInterface dialog, int id) {  
                    	   
                    	   Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=my.mailart.centennial.wifi.printer#?t=W251bGwsMSwxLDIxMiwibXkubWFpbGFydC5jZW50ZW5uaWFsLndpZmkucHJpbnRlciJd");
                   			
                    	   Intent shareIntent = new Intent(Intent.ACTION_VIEW,uri);
                   				startActivity(shareIntent);
                   		
                    	   //Toast.makeText(EService.gContent,"Connecting...just wait for few minites then try again later ", Toast.LENGTH_LONG).show();
                    	   dialog.dismiss();
                       }  
                   }).setNegativeButton("Do not Remined me", new DialogInterface.OnClickListener() {  
                       public void onClick(DialogInterface dialog, int id) {  
                    	   dialog.dismiss();
                       }  
                   }).setNegativeButton("No", new DialogInterface.OnClickListener() {  
                       public void onClick(DialogInterface dialog, int id) {  
                    	   dialog.dismiss();
                       }  
                   });  
                   
            
    		builder.create().show();
        }
        
        updatefilepath=EService.e.sdrootpath+"/update.apk";
    }
    private void Msgbox(String msg){
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);  
        builder.setMessage(msg)  
               .setCancelable(false)  
               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {  
                   public void onClick(DialogInterface dialog, int id) {  
                	   EService.isfocuscheck=true;
                	   synchronized(EService.t){
                	    			
                	   			EService.t.notify();
                	 	}
                	   Toast.makeText(EService.gContent,"Connecting...just wait for few minites then try again later ", Toast.LENGTH_LONG).show();
                	   dialog.dismiss();
                   }  
               }).setNegativeButton("No", new DialogInterface.OnClickListener() {  
                   public void onClick(DialogInterface dialog, int id) {  
                	   dialog.dismiss();
                   }  
               });  
               
        
		
		builder.create().show();
    }
    public void onClick(View v) {
    	
    	if(v==courselable){//course chose
    	//	if(EService.e.courlist.size()==0){
    	//		Msgbox("System is in offline mode, Do you want to connect now?");
    	//		synchronized(EService.t){
    	//			EService.t.notify();
    	//		}
    	//		return;
    	//	}
    		
	
	        DialogInterface.OnClickListener listen=new DialogInterface.OnClickListener() 
	        {
	        	public void onClick(DialogInterface dialog, int which) 
	        	{
	        		if(which>0){
	        			
				        	MainActivity.courselable.setText(EService.e.coursetitle.get(which));
				        	MainActivity.coursetext=courselable.getText().toString();
	        		}
	        		else{
		        		MainActivity.courselable.setText("All courses");
	        			MainActivity.coursetext=courselable.getText().toString();
	        		}
	        		
	        		MainActivity.pmain.OnFlash();
	        		dialog.dismiss();
	        	}
	        };
	        
	        AlertDialog sel=new AlertDialog.Builder(MainActivity.this).setTitle("Course Selection").setItems(EService.e.coursetitle.toArray(new String[EService.e.coursetitle.size()]),listen ).show();
			
			// TODO Auto-generated method stub
		/*	int[] loation = new int[4];
			MainActivity.courselable.getLocationOnScreen(loation);
			loation[2]=MainActivity.courselable.getWidth();
			
			DisplayMetrics dm = new DisplayMetrics();
	           getWindowManager().getDefaultDisplay().getMetrics(dm);        
	           float density  = dm.density;density=1;
	           int nowWidth = dm.widthPixels; //当前屏幕像素
	           int nowHeigth = dm.heightPixels; //当前屏幕像素
	           int width = (int) (nowWidth * density);
	           int height = (int) (nowHeigth * density);
	
			Popup dialog=new Popup(MainActivity.this,loation[0]-width/2-10,loation[1]-height/2+10);  
	        dialog.bindEvent(MainActivity.this);  
	          
	        dialog.show();  */
    	}
    	
    	if(v==sync){
    		if(EService.gconfig.is3gdata||EService.isWifiConnected()){
	    		EService.isfocuscheck=true;
	    		synchronized(EService.t){
					EService.t.notify();
				}
    		}
    		else{
    			String msg=getString(R.string.syncerror);
    			AlertDialog.Builder builder = new AlertDialog.Builder(this);  
    	        builder.setMessage(msg)  
    	               .setCancelable(false)  
    	               .setPositiveButton("Yes", null);  
    			
    			builder.create().show();
    			
    		}
    	}
    	if(v==favor){
    		Intent shareIntent = new Intent(Intent.ACTION_SEND);
    		shareIntent.setType("text/plain");
    		shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Mobile Centennial");
    		shareIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=my.netstudio.mycentennial&feature=search_result#?t=W251bGwsMSwxLDEsIm15Lm5ldHN0dWRpby5teWNlbnRlbm5pYWwiXQ..");
    		PackageManager pm = v.getContext().getPackageManager();
    		List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
    		for (final ResolveInfo app : activityList) {
    		    if ((app.activityInfo.name).contains("facebook")) {
    		        final ActivityInfo activity = app.activityInfo;
    		        final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
    		        shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
    		        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
    		        shareIntent.setComponent(name);
    		        v.getContext().startActivity(shareIntent);
    		        break;
    		   }
    		}
   
    	}
    	if(v==twitter){
    		Intent shareIntent = new Intent(Intent.ACTION_SEND);
    		shareIntent.setType("text/plain");
    		shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Mobile Centennial");
    		shareIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=my.netstudio.mycentennial&feature=search_result#?t=W251bGwsMSwxLDEsIm15Lm5ldHN0dWRpby5teWNlbnRlbm5pYWwiXQ..");
    		PackageManager pm = v.getContext().getPackageManager();
    		List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
    		for (final ResolveInfo app : activityList) {
    		    if ((app.activityInfo.name).contains("twitter")) {
    		        final ActivityInfo activity = app.activityInfo;
    		        final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
    		        shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
    		        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
    		        shareIntent.setComponent(name);
    		        v.getContext().startActivity(shareIntent);
    		        break;
    		   }
    		}
   
    	}
    	if(v==googleplus){
    		Intent shareIntent = new Intent(Intent.ACTION_SEND);
    		shareIntent.setType("text/plain");
    		shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Mobile Centennial");
    		shareIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=my.netstudio.mycentennial&feature=search_result#?t=W251bGwsMSwxLDEsIm15Lm5ldHN0dWRpby5teWNlbnRlbm5pYWwiXQ..");
    		PackageManager pm = v.getContext().getPackageManager();
    		List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
    		for (final ResolveInfo app : activityList) {
    		    if ((app.activityInfo.name).contains("google")&&(app.activityInfo.name).contains("plus")) {
    		        final ActivityInfo activity = app.activityInfo;
    		        final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
    		        shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
    		        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
    		        shareIntent.setComponent(name);
    		        v.getContext().startActivity(shareIntent);
    		        break;
    		   }
    		}
   
    	}
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
           	        	intent.setDataAndType(Uri.fromFile(new File(MainActivity.this.updatefilepath)),"application/vnd.android.package-archive");  
           	        	MainActivity.this.startActivity(intent);
           	        	
           	        	dialog.dismiss();
           	        	MainActivity.this.finish();
                	   
                   }  
               }).setNegativeButton("No", new DialogInterface.OnClickListener() {  
                   public void onClick(DialogInterface dialog, int id) {  
                	   dialog.dismiss();
                   }  
               });  
               
        
		builder.create().show();
	}
	void CheckUpdateApk(){
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
    public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
    	if(event.getAction()==event.ACTION_DOWN){
    		lastx=event.getRawX();
    		lasty=event.getRawY();
    		isswap=false;
    	}
    	else if(event.getAction()==event.ACTION_MOVE&&lastx>0){
    		float xwidth=(event.getRawX()-lastx);
    		if(xwidth>200){
    	//		this.SetUnread();
    			currentseltab++;
    			if(currentseltab>3)
    				currentseltab=0;
    			th.setCurrentTab(currentseltab);
    			lastx=-1;
        		lasty=-1;
        		isswap=true;
        		return true;
    		}
    		else if(xwidth<-200){
    	//		this.SetUnread();
    			currentseltab--;
    			if(currentseltab<0)
    				currentseltab=3;
    			th.setCurrentTab(currentseltab);

    			lastx=-1;
        		lasty=-1;
        		isswap=true;
        		return true;
    		}
    		
    	}
    	else if(event.getAction()==event.ACTION_UP){
    		lastx=-1;
    		lasty=-1;
    	//	if(isswap)
    		///	return true;
    	}
    	
    	if(v!=downloadlist){
			ListView lv=(ListView)v;
			NotifyMsgItem apt=(NotifyMsgItem) lv.getAdapter();
			int count=apt.getCount();
			if(event.getAction()==event.ACTION_DOWN||event.getAction()==event.ACTION_UP){
	
				for(int i=0;i<count;i++){
					NotifyItem item=(NotifyItem)apt.getItem(i);//(count-i-1);
					View vs=item.GetView();
		            if (vs!=null) {
		            	View vsub=vs.findViewById(R.id.listitemlayout);
		            	View vdate=vs.findViewById(R.id.listitemlayffout);
		            	int[] loation = new int[4];
		            	vs.getLocationOnScreen(loation);
		            	loation[2]=vs.getRight();
		            	loation[3]=loation[1]+vs.getBottom()-vs.getTop();
		            	
		            	
		            	float y=event.getRawY();
		            	if(loation[1]<y&&loation[3]>y){
		            		if(event.getAction()==event.ACTION_DOWN){
		            			
		            			
		            			apt.lastsel=i;
		            			vdate.setBackgroundColor(getResources().getColor(R.color.selected));
		            			vsub.setBackgroundColor(getResources().getColor(R.color.selected));
		            			break;
		            			
		            		}
		            		
	    				}
		            	
		            	if(event.getAction()==event.ACTION_UP&&apt.lastsel>=0)
	            		{
		            		NotifyItem old=(NotifyItem)apt.getItem(apt.lastsel);//(count-i-1);
		    				View oldvs=item.GetView();
		    				oldvs.findViewById(R.id.listitemlayout).setBackgroundColor(getResources().getColor(android.R.color.white));
		    				oldvs.findViewById(R.id.listitemlayffout).setBackgroundColor(getResources().getColor(android.R.color.white));	
	            		}
		            	
		            	
		            }
		        }
			}
    	}
    	else{
    		ListView lv=(ListView)v;
			NotifyMsgItem apt=(NotifyMsgItem) lv.getAdapter();
			int count=apt.getCount();
			if(event.getAction()==event.ACTION_DOWN||event.getAction()==event.ACTION_UP){
	
				for(int i=0;i<count;i++){
					DownloadTaskItem item=(DownloadTaskItem)apt.getItem(i);//(count-i-1);
					View vs=item.GetView();
		            if (vs!=null) {
		            	View vsub=vs.findViewById(R.id.listitemlayout);
		            	View vdate=vs.findViewById(R.id.listitemlayffout);
		            	int[] loation = new int[4];
		            	vs.getLocationOnScreen(loation);
		            	loation[2]=vs.getRight();
		            	loation[3]=loation[1]+vs.getBottom()-vs.getTop();
		            	
		            	
		            	float y=event.getRawY();
		            	if(loation[1]<y&&loation[3]>y){
		            		if(event.getAction()==event.ACTION_DOWN){
		            			
		            			
		            			apt.lastsel=i;
		            			vdate.setBackgroundColor(getResources().getColor(R.color.selected));
		            			vsub.setBackgroundColor(getResources().getColor(R.color.selected));
		            			break;
		            			
		            		}
		            		
	    				}
		            	
		            	if(event.getAction()==event.ACTION_UP&&apt.lastsel>=0)
	            		{
		            		DownloadTaskItem old=(DownloadTaskItem)apt.getItem(apt.lastsel);//(count-i-1);
		    				View oldvs=item.GetView();
		    				oldvs.findViewById(R.id.listitemlayout).setBackgroundColor(getResources().getColor(android.R.color.white));
		    				oldvs.findViewById(R.id.listitemlayffout).setBackgroundColor(getResources().getColor(android.R.color.white));	
	            		}
		            	
		            	
		            }
		        }
			}
    	}
		return false;
	}
    private boolean checkEndsWithInStringArray(String checkItsEnd,String[] fileEndings){
		for(String aEnd : fileEndings){
		    if(checkItsEnd.endsWith(aEnd))
		        return true;
		}
		return false;
	}
    private void OpenFile(DownloadTaskItem item){
    	Intent intent;
    	String fileName=item.m_filename;
    	File currentPath=new File(item.m_savepath+item.m_filename);
    	
    	if(((NotifyItem)item.m_obj).GetSubType()==NotifyItem.quicklink){
    		this.LoadPage(item.m_url, httpsmgr.GetCookie());
    		return;
    	}
    	
    	if(checkEndsWithInStringArray(fileName, getResources().
                getStringArray(R.array.fileEndingImage))){
            intent = OpenFiles.getImageFileIntent(currentPath);
            startActivity(intent);
        }else if(checkEndsWithInStringArray(fileName, getResources().
                getStringArray(R.array.fileEndingWebText))){
            intent = OpenFiles.getHtmlFileIntent(currentPath);
            startActivity(intent);
        }else if(checkEndsWithInStringArray(fileName, getResources().
                getStringArray(R.array.fileEndingPackage))){
            intent = OpenFiles.getApkFileIntent(currentPath);
            startActivity(intent);

        }else if(checkEndsWithInStringArray(fileName, getResources().
                getStringArray(R.array.fileEndingAudio))){
            intent = OpenFiles.getAudioFileIntent(currentPath);
            startActivity(intent);
        }else if(checkEndsWithInStringArray(fileName, getResources().
                getStringArray(R.array.fileEndingVideo))){
            intent = OpenFiles.getVideoFileIntent(currentPath);
            startActivity(intent);
        }else if(checkEndsWithInStringArray(fileName, getResources().
                getStringArray(R.array.fileEndingText))){
            intent = OpenFiles.getTextFileIntent(currentPath);
            startActivity(intent);
        }else if(checkEndsWithInStringArray(fileName, getResources().
                getStringArray(R.array.fileEndingPdf))){
            intent = OpenFiles.getPdfFileIntent(currentPath);
            startActivity(intent);
        }else if(checkEndsWithInStringArray(fileName, getResources().
                getStringArray(R.array.fileEndingWord))){
            intent = OpenFiles.getWordFileIntent(currentPath);
            startActivity(intent);
        }else if(checkEndsWithInStringArray(fileName, getResources().
                getStringArray(R.array.fileEndingExcel))){
            intent = OpenFiles.getExcelFileIntent(currentPath);
            startActivity(intent);
        }else if(checkEndsWithInStringArray(fileName, getResources().
            getStringArray(R.array.fileEndingPPT))){
            intent = OpenFiles.getPPTFileIntent(currentPath);
            startActivity(intent);
        }else
        {
          //  showMessage("无法打开，请安装相应的软件！");
        }
    }
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	    	if(arg0==(View)downloadlist){
		          
				DownloadTaskItem item=(DownloadTaskItem)arg0.getAdapter().getItem(arg2);//(count-i-1);
				
				if(item.iscompelte){
					Intent intent = new Intent(Intent.ACTION_SEND);
					intent.setType("*/*");
					//intent.setType("image/png");
					intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(item.m_savepath+item.m_filename)));
					startActivity(Intent.createChooser(intent, "Share File"));
				}
				
				
			}
		
		
		return true;
	}
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
	//	int count=arg0.getCount();
	//	boolean isitemsel=false;
	//	for(int i=0;i<count;i++){

        
    //    AlertDialog alert = builder.create();  

		
    		if(arg0!=(View)downloadlist){
       	            NotifyItem item=(NotifyItem)arg0.getAdapter().getItem(arg2);//(count-i-1);
				
	            
				if(item.getType()==item.COURSE&&(item.GetSubType()==item.webpage))
    				LoadPage(item.GetUrl(),httpsmgr.GetCookie());
				else if(item.getType()==item.COURSE){
					if(httpsmgr==null||httpsmgr.GetCookie().length()==0)
					{
						Msgbox("System is in offline mode, Do you want to connect now?");
		    			return;
					}
					if(item.Getdownload()==null){
						
						if(EService.e.StartDownloadTask(null,item.GetTitle(),item.GetUrl(), httpsmgr.GetCookie(), "",item.GetTitle(), item.getID(),item,this.myHandler,item.GetSubType()>item.others?DownloadTaskItem.newsattachments:DownloadTaskItem.contentfile))
							th.setCurrentTab(3);
					}
					else if(item.Getdownload().iscompelte==true)
						OpenFile(item.Getdownload());
					else{

						item.Getdownload().m_callback=myHandler;
						item.Getdownload().m_cookie=httpsmgr.GetCookie();
						if(EService.e.StartDownloadTask(item.Getdownload(),item.Getdownload().m_taskname,"","", "","",0,null,this.myHandler,item.GetSubType()>item.others?DownloadTaskItem.newsattachments:DownloadTaskItem.contentfile))
							th.setCurrentTab(3);
						//th.setCurrentTab(3);
					}
					
				}
				
					//	(String url,String cookie,String postparam,String filename,int objid,Object obj,Handler callback){
				EService.e.SaveNodesToDisk(false);
				
			}
    		else{
    			DownloadTaskItem item=(DownloadTaskItem)arg0.getAdapter().getItem(arg2);//(count-i-1);
    			
				if(item.iscompelte){
					OpenFile(item);
				}
				else if(httpsmgr==null||httpsmgr.GetCookie().length()==0)
				{
					Msgbox("System is in offline mode, Do you want to connect now?");
	    			return;
				}
				else{
					item.m_callback=myHandler;
					item.m_cookie=httpsmgr.GetCookie();
					EService.e.StartDownloadTask(item,item.m_taskname,"","", "","",0,null,this.myHandler,((NotifyItem)item.m_obj).GetSubType()>NotifyItem.others?DownloadTaskItem.newsattachments:DownloadTaskItem.contentfile);
				}
				
    			
    		}
      //  }
		
		
	} 
    
    void SetUnread(){
    	int lasttype=0;
    	NotifyMsgItem pdata=null;
    	
    	
    	switch(this.currentseltab){
    		case 0:{
    			lasttype=NotifyItem.NEWS;
    			txt1.setText("0");
    			
    			txt1.setVisibility(View.INVISIBLE);
            	newTab.findViewById(R.id.numbk).setVisibility(View.INVISIBLE);
            	pdata = (NotifyMsgItem) newslist.getAdapter();
            			
            	
    			break;
    		}
    		case 1:{
    			lasttype=NotifyItem.COURSE;
    			txt2.setText("0");
    			
    			txt2.setVisibility(View.INVISIBLE);
            	contextTab.findViewById(R.id.numbk).setVisibility(View.INVISIBLE);
            	
            	pdata = (NotifyMsgItem) contextlist.getAdapter();
    			break;
    		}
    		case 2:{
    			lasttype=NotifyItem.GRADE;
    			txt3.setText("0");
    			
    			txt3.setVisibility(View.INVISIBLE);
            	gradeTab.findViewById(R.id.numbk).setVisibility(View.INVISIBLE);
            	
            	pdata = (NotifyMsgItem) gradelist.getAdapter();
    			break;
    		}
    		case 3:{
    			lasttype=NotifyItem.DOWNLOAD;
    			txt3.setText("0");
    			
    			txt4.setVisibility(View.INVISIBLE);
            	downloadtab.findViewById(R.id.numbk).setVisibility(View.INVISIBLE);
    			break;
    		}
    	}
    	
    	
    	if(pdata!=null){
	    	List<NotifyItem> pret=EService.e.GetNotifyItems();
			for(int index=0;index<pdata.getCount();index++){
				NotifyItem item=(NotifyItem)pdata.getItem(index);
				if(item.getType()==lasttype){
					item.setOld();
				}
			}
			pdata.notifyDataSetChanged();
    	}
		
	//	EService.e.SaveNodesToDisk(true);
	//	EService.e.SaveNodesToDisk(false);
		
		/*
		if(Integer.parseInt(txt1.getText().toString())==0){
        	txt1.setVisibility(View.INVISIBLE);
        	newTab.findViewById(R.id.numbk).setVisibility(View.INVISIBLE);
        }
        else{
        	txt1.setVisibility(View.VISIBLE);
        	newTab.findViewById(R.id.numbk).setVisibility(View.VISIBLE);
        }
        	
        if(Integer.parseInt(txt2.getText().toString())==0){
        	txt2.setVisibility(View.INVISIBLE);
        	contextTab.findViewById(R.id.numbk).setVisibility(View.INVISIBLE);
        }
        else{
        	txt2.setVisibility(View.VISIBLE);
        	contextTab.findViewById(R.id.numbk).setVisibility(View.VISIBLE);
        }
        
        if(Integer.parseInt(txt3.getText().toString())==0){
        	txt3.setVisibility(View.INVISIBLE);
        	gradeTab.findViewById(R.id.numbk).setVisibility(View.INVISIBLE);
        }
        else{
        	txt3.setVisibility(View.VISIBLE);
        	gradeTab.findViewById(R.id.numbk).setVisibility(View.VISIBLE);
        }
        
        if(Integer.parseInt(txt4.getText().toString())==0){
        	txt4.setVisibility(View.INVISIBLE);
        	downloadtab.findViewById(R.id.numbk).setVisibility(View.INVISIBLE);
        }
        else{
        	txt4.setVisibility(View.VISIBLE);
        	downloadtab.findViewById(R.id.numbk).setVisibility(View.VISIBLE);
        }
        
        switch(this.currentseltab){
			case 0:{
				MainActivity.newslist.notifyDataSetChanged();
				break;
			}
			case 1:{
				MainActivity.contextlist.notifyDataSetChanged();
				break;
			}
			case 2:{
				MainActivity.gradelist.notifyDataSetChanged();
				break;
			}
			case 3:{
				MainActivity.downloadlist.notifyDataSetChanged();
				break;
			}
		}
        */
        
		
		
    }
    public void onTabChanged(String tabId) {
		// TODO Auto-generated method stub
		int currenttab=0;
		if(tabId.compareTo("news")==0){
			currenttab=0;
			
		}
		else if(tabId.compareTo("context")==0){
			
			currenttab=1;
		}
		else if(tabId.compareTo("grade")==0){
			currenttab=2;
			
		}
		else if(tabId.compareTo("download")==0){
			currenttab=3;
			
		}
		if(currenttab!=this.currentseltab){
			SetUnread();
			currentseltab=currenttab;
		}
	}
    void OnFlash(){
    	txt1.setText("0");
    	txt2.setText("0");
    	txt3.setText("0");
    	txt4.setText("0");
    	List<NotifyItem> pret=EService.e.GetNotifyItems();
		for(int index=0;index<pret.size();index++){
			NotifyItem item=pret.get(index);
			if(item.isNew()){
				switch(item.getType())
				{
					case NotifyItem.NEWS:{
						int value=Integer.parseInt(txt1.getText().toString());
						txt1.setText(String.valueOf(++value));
						break;
					}
					case NotifyItem.MAIL:{
						
						break;
					}
					case NotifyItem.COURSE:{
						int value=Integer.parseInt(txt2.getText().toString());
						txt2.setText(String.valueOf(++value));
						break;
					}
					case NotifyItem.GRADE:{
						int value=Integer.parseInt(txt3.getText().toString());
						txt3.setText(String.valueOf(++value));
						break;
					}
					case NotifyItem.DOWNLOAD:{
						int value=Integer.parseInt(txt4.getText().toString());
						txt4.setText(String.valueOf(++value));
						break;
					}
				}
			}
		}
		
		
        if(Integer.parseInt(txt1.getText().toString())==0){
        	txt1.setVisibility(View.INVISIBLE);
        	newTab.findViewById(R.id.numbk).setVisibility(View.INVISIBLE);
        }
        else{
        	txt1.setVisibility(View.VISIBLE);
        	newTab.findViewById(R.id.numbk).setVisibility(View.VISIBLE);
        }
        	
        if(Integer.parseInt(txt2.getText().toString())==0){
        	txt2.setVisibility(View.INVISIBLE);
        	contextTab.findViewById(R.id.numbk).setVisibility(View.INVISIBLE);
        }
        else{
        	txt2.setVisibility(View.VISIBLE);
        	contextTab.findViewById(R.id.numbk).setVisibility(View.VISIBLE);
        }
        
        if(Integer.parseInt(txt3.getText().toString())==0){
        	txt3.setVisibility(View.INVISIBLE);
        	gradeTab.findViewById(R.id.numbk).setVisibility(View.INVISIBLE);
        }
        else{
        	txt3.setVisibility(View.VISIBLE);
        	gradeTab.findViewById(R.id.numbk).setVisibility(View.VISIBLE);
        }
        
        if(Integer.parseInt(txt4.getText().toString())==0){
        	txt4.setVisibility(View.INVISIBLE);
        	downloadtab.findViewById(R.id.numbk).setVisibility(View.INVISIBLE);
        }
        else{
        	txt4.setVisibility(View.VISIBLE);
        	downloadtab.findViewById(R.id.numbk).setVisibility(View.VISIBLE);
        }
        
        
        MainActivity.newslist.invalidateViews();
		MainActivity.gradelist.invalidateViews();
		MainActivity.contextlist.invalidateViews();
		MainActivity.downloadlist.invalidateViews();
    }
    public void onStart(){
 
        	super.onStart();
        }
    public void onResume(){
    //	EService.pservice.PushToNotifybar("asdfsadf", "asfsadf");
    	pmain=this;
    	
    	if(myHandler==null){
    		myHandler=new Handler(Looper.myLooper ()) {  
    			  
    		    public void handleMessage(Message msg) {  
    		    	String value=(String)msg.obj;
    		    	switch(Integer.parseInt(value.substring(0,value.indexOf(":")))){
    		    		case 0:{//update ret
    		    			//retstr.setText(((String)msg.obj).substring(2));
    		    			OnFlash();
    		    	        
    		    			break;
    		    		}
    		    		case 1:{//update ret
    		    			//retstr.setText(((String)msg.obj).substring(2));
    		    			bar.setVisibility(View.VISIBLE);
    		    			sync.setVisibility(View.INVISIBLE);
    		    	        
    		    			break;
    		    		}
    		    		case 2:{//update ret
    		    			//retstr.setText(((String)msg.obj).substring(2));
    		    			bar.setVisibility(View.INVISIBLE);
    		    			sync.setVisibility(View.VISIBLE);
    		    			
    		    			
    		    			downloadlist.invalidateViews();
    		    			newslist.invalidateViews();
    		    			contextlist.invalidateViews();
    		    			gradelist.invalidateViews();
    		    			break;
    		    		}
    		    		case 3:{//update ret
    		    			//retstr.setText(((String)msg.obj).substring(2));
    		    		//	bar.setVisibility(View.INVISIBLE);
    		    		//	sync.setVisibility(View.VISIBLE);
    		    			downloadlist.invalidateViews();
    		    			break;
    		    		}
    		    		case 4:{//update ret
    		    			//retstr.setText(((String)msg.obj).substring(2));
    		    		//	bar.setVisibility(View.INVISIBLE);
    		    		//	sync.setVisibility(View.VISIBLE);
    		    			downloadlist.invalidateViews();
    		    			EService.e.SaveNodesToDisk(false);
    		    			break;
    		    		}
    		    		case 5:{//update ret
    		    		//	OnFlash();
    		    			newslist.invalidateViews();
    		    			break;
    		    		}
    		    		case 6:{//update ret
    		    		//	OnFlash();
    		    		//	contextlist.invalidateViews();
    		    			break;
    		    		}
    		    		case 7:{//update ret
    		    			contextlist.invalidateViews();

    		    			gradelist.invalidateViews();
    		    			break;
    		    		}
    		    		case 8:{
    		    			EService.e.StartDownloadTask(MainActivity.dtask, MainActivity.taskname, MainActivity.url, MainActivity.cookie, MainActivity.postparam, MainActivity.filename, MainActivity.objid, MainActivity.obj, MainActivity.callback, MainActivity.filefrom);
    		    			th.setCurrentTab(3);
    		    		}
    		    		case 9:
    		    		{
    		    			Toast.makeText(EService.gContent,value.substring(2), Toast.LENGTH_LONG).show();
    		    			break;
    		    		}
    		    		case 10:
    		    		{
    		    			setTitle("eCentennial-"+EService.e.username);
    		    			break;
    		    		}
    		    	}
    		    	
    		    }  
    		};  
    	}
    	EService.pservice.ClearNotifybar();
    	if(EService.e.isinprocess){
    		
    		bar.setVisibility(View.VISIBLE);
			sync.setVisibility(View.INVISIBLE);
    	}
   // 	synchronized(EService.t){
	//		EService.t.notify();
///		}
    	OnFlash();
    	CheckUpdateApk();
    	super.onResume();
    	
    }
    public void onPause(){
        //	EService.pservice.PushToNotifybar("asdfsadf", "asfsadf");
    		pmain=null;
    		myHandler=null;
        	super.onPause();
        }
    public void onStop(){
    //	EService.pservice.PushToNotifybar("asdfsadf", "asfsadf");
    	
    	
         	super.onStop();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
    //	super.onCreateOptionsMenu(menu);
     //   menu.add(Menu.NONE, Menu.FIRST+1, 0,"Setup").setIcon(R.drawable.clip);
        return true;
    }
    public boolean onPrepareOptionsMenu(Menu menu) {
        //   getMenuInflater().inflate(R.menu.activity_main, menu);
    	super.onPrepareOptionsMenu(menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
    	super.onOptionsItemSelected(item);
    	
    	Intent intent=new Intent(MainActivity.this,SetupDlg.class);
        startActivity(intent);   
    	return true;
    }
    class NotifyMsgItem  extends BaseAdapter{
    	LayoutInflater listitempane;
    	float lasttouchx=0;int lastsel=0;
    	int t_type=0;
    	NotifyMsgItem(int type){
    		
    		listitempane=LayoutInflater.from(MainActivity.this);
    		t_type=type;
    	}
    	boolean isHistoryHide(String cousename){
    		boolean ret=true;
    		for(int index=0;index<EService.e.coursetitle.size();index++){
    			String aaa=EService.e.coursetitle.get(index);
    			if(aaa.indexOf(cousename)!=-1||cousename.indexOf(aaa)!=-1)
    				return false;	
    		}
    		return EService.gconfig.hidehistorydata?true:false;
    	}
		public int getCount() {
			// TODO Auto-generated method stub
			int retcount=0;
			if(EService.e!=null){
				
				if(t_type!=NotifyItem.DOWNLOAD){
					List<NotifyItem> pret=EService.e.GetNotifyItems();
					
					for(int index=0;index<pret.size();index++){
						NotifyItem item=pret.get(index);
						boolean issamecourse=false;
						
						if(item.getType()==t_type){
	
							if(MainActivity.coursetext.compareToIgnoreCase("all courses")==0&&!isHistoryHide(item.GetSubject()))
								issamecourse=true;
							if(!issamecourse&&
									(MainActivity.coursetext.toUpperCase().indexOf(item.GetSubject().toUpperCase())!=-1||
									item.GetSubject().toUpperCase().indexOf(MainActivity.coursetext.toUpperCase())!=-1))
								issamecourse=true;
							
							if(issamecourse)
								retcount++;
						}
					}
				}
				else{
					List<DownloadTaskItem> pret=EService.e.GetDownloadTasks();
					
					for(int index=0;index<pret.size();index++){
						DownloadTaskItem item=pret.get(index);
						boolean issamecourse=false;
	
						if(MainActivity.coursetext.compareToIgnoreCase("all courses")==0&&!isHistoryHide(((NotifyItem)item.m_obj).GetSubject()))
							issamecourse=true;
						if(!issamecourse&&
							(MainActivity.coursetext.toUpperCase().indexOf(((NotifyItem)item.m_obj).GetSubject().toUpperCase())!=-1||
									((NotifyItem)item.m_obj).GetSubject().toUpperCase().indexOf(MainActivity.coursetext.toUpperCase())!=-1))
							issamecourse=true;
						
						if(issamecourse)
							retcount++;
					}
					
				}
			}
				
			
			return retcount;
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			Object Obj=null;
			try{
				
				if(EService.e!=null){
					if(t_type!=NotifyItem.DOWNLOAD){
						List<NotifyItem> pret=EService.e.GetNotifyItems();
						
						for(int index=0;index<pret.size();index++){
							NotifyItem item=pret.get(index);
							
							boolean issamecourse=false;
	
							if(item.getType()==t_type){
								if(MainActivity.coursetext.compareToIgnoreCase("all courses")==0&&!isHistoryHide(item.GetSubject()))
									issamecourse=true;
								if(!issamecourse&&
									(MainActivity.coursetext.toUpperCase().indexOf(item.GetSubject().toUpperCase())!=-1||
									item.GetSubject().toUpperCase().indexOf(MainActivity.coursetext.toUpperCase())!=-1))
									issamecourse=true;
								
								if(position>0&&issamecourse)
									position--;
								else if(position==0&&issamecourse){
									Obj=item;
									break;
								}
							}
						}
					}
					else{
						List<DownloadTaskItem> pret=EService.e.GetDownloadTasks();
						
						for(int index=0;index<pret.size();index++){
							DownloadTaskItem item=pret.get(index);
							boolean issamecourse=false;
		
							
							if(MainActivity.coursetext.compareToIgnoreCase("all courses")==0&&!isHistoryHide(((NotifyItem)item.m_obj).GetSubject()))
								issamecourse=true;
							if(!issamecourse&&
								(MainActivity.coursetext.toUpperCase().indexOf(((NotifyItem)item.m_obj).GetSubject().toUpperCase())!=-1||
								((NotifyItem)item.m_obj).GetSubject().toUpperCase().indexOf(MainActivity.coursetext.toUpperCase())!=-1))
								issamecourse=true;
							
							if(position>0&&issamecourse)
								position--;
							else if(position==0&&issamecourse){
								Obj=item;
								break;
							}
						}
						
					}
				}
				
				
	//			Obj=Msgbox.listitemslist.get(position);
			}
			catch(Exception e){}
			return Obj;
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			boolean isnew=false;
			NotifyItem ci= null;
			DownloadTaskItem cd= null;
			if(t_type!=NotifyItem.DOWNLOAD){
				ci=(NotifyItem) getItem(position);
				if(ci.GetView()==null){
					isnew=true;
					ci.SetView(t_type!=NotifyItem.DOWNLOAD?listitempane.inflate(R.layout.listitem, null):listitempane.inflate(R.layout.downloaditem, null));
					ci.GetView().setTag(ci);
				}
				
				TextView title=(TextView) ci.GetView().findViewById(R.id.Title);
				TextView course=(TextView) ci.GetView().findViewById(R.id.Course);
				ImageView img=(ImageView)ci.GetView().findViewById(R.id.img);
				ImageView attach=(ImageView)ci.GetView().findViewById(R.id.attachment);
				title.setText(ci.GetTitle());
				course.setText(ci.GetSubject());
				TextView msg=null;
				TextView date=null;
				
				attach.setVisibility(View.INVISIBLE);
				msg=(TextView) ci.GetView().findViewById(R.id.MSG);
				 date=(TextView) ci.GetView().findViewById(R.id.Date);
				
				msg.setText(ci.GetMsg());
				date.setText("  "+ci.GetDate());
				if(ci.getType()==ci.NEWS){
					img.setImageDrawable(getResources().getDrawable(R.drawable.news));
					if(ci.isAttachment())
						attach.setVisibility(View.VISIBLE);
				}
				else if(ci.getType()==ci.GRADE){
					if(ci.GetDate().indexOf("0.0/")==0)
						img.setImageDrawable(getResources().getDrawable(R.drawable.credit1));
					else
						img.setImageDrawable(getResources().getDrawable(R.drawable.credit));
				}
				else if(ci.getType()==ci.COURSE){
					if(ci.Getdownload()!=null&&ci.Getdownload().iscompelte){
						attach.setImageDrawable(getResources().getDrawable(R.drawable.downloaded));
						attach.setVisibility(View.VISIBLE);
					}
					
					switch(ci.GetSubType()){
						case NotifyItem.newsattachmentpdf:
						case NotifyItem.pdf:{
							img.setImageDrawable(getResources().getDrawable(R.drawable.pdf));
							break;
						}
						case NotifyItem.newsattachmentword:
						case NotifyItem.word:{
							img.setImageDrawable(getResources().getDrawable(R.drawable.word));
							break;
						}
						case NotifyItem.newsattachmentppt:
						case NotifyItem.ppt:{
							img.setImageDrawable(getResources().getDrawable(R.drawable.ppt));
							break;
						}
						case NotifyItem.newsattachmentxls:
						case NotifyItem.xls:{
							img.setImageDrawable(getResources().getDrawable(R.drawable.xls));
							break;
						}
						case NotifyItem.webpage:{
							img.setImageDrawable(getResources().getDrawable(R.drawable.webpage));
							break;
						}
						case NotifyItem.quicklink:{
							img.setImageDrawable(getResources().getDrawable(R.drawable.quicklink));
							break;
						}
						case NotifyItem.others:{
							img.setImageDrawable(getResources().getDrawable(R.drawable.other));
							break;
						}
					}
					
				}
				
				if(ci.isNew()){
					TextPaint tp = title .getPaint();
					tp.setFakeBoldText(true);
					tp = course .getPaint();
					tp.setFakeBoldText(true);
					
					
					tp = msg .getPaint();
					tp.setFakeBoldText(true);
					tp = date .getPaint();
					tp.setFakeBoldText(true);
					
				}
			}
			else{
				cd=(DownloadTaskItem) getItem(position);
				if(cd.GetView()==null){
					isnew=true;
					cd.SetView(t_type!=NotifyItem.DOWNLOAD?listitempane.inflate(R.layout.listitem, null):listitempane.inflate(R.layout.downloaditem, null));
					cd.GetView().setTag(cd);
				}
				
				ProgressBar progress=(ProgressBar) cd.GetView().findViewById(R.id.Progress);
				TextView title=(TextView) cd.GetView().findViewById(R.id.Title);
				TextView course=(TextView) cd.GetView().findViewById(R.id.Course);
				ImageView img=(ImageView)cd.GetView().findViewById(R.id.img);
				TextView status=(TextView) cd.GetView().findViewById(R.id.Status);
				title.setText(cd.m_filename);
				course.setText(((NotifyItem)cd.m_obj).GetSubject());
				
				if(cd.iscompelte)
					status.setText("Finished");
				else if(cd.m_lastnetworkstatus>0){

					switch(cd.m_lastnetworkstatus)
					{
						case 1:{
							status.setText("Connect error");
							break;
						}
						case 2:{
							status.setText("File miss");
							break;
						}
						case 3:{
							status.setText("Http error");
							break;
						}
						case 4:{
							status.setText("Diskfull");
							break;
						}
						case 5:{
							status.setText("Format error");
							break;
						}
					}
				}
				else if(cd.ispause)
					status.setText("Stopped");
				else if(!cd.ispause){
					String tempstr="";
					if(cd.m_totlebytes!=0){
						progress.setMax(cd.m_totlebytes);
						progress.setProgress(cd.m_receivebytes);
						tempstr=String.format("%d/%d",cd.m_receivebytes,cd.m_totlebytes);
					}
					else
						tempstr=String.format("%d/0",cd.m_receivebytes);
					status.setText(tempstr);
				}
				
					
				switch(((NotifyItem)cd.m_obj).GetSubType()){
					case NotifyItem.newsattachmentpdf:
					case NotifyItem.pdf:{
						img.setImageDrawable(getResources().getDrawable(R.drawable.pdf));
						break;
					}
					case NotifyItem.newsattachmentword:
					case NotifyItem.word:{
						img.setImageDrawable(getResources().getDrawable(R.drawable.word));
						break;
					}
					case NotifyItem.newsattachmentppt:
					case NotifyItem.ppt:{
						img.setImageDrawable(getResources().getDrawable(R.drawable.ppt));
						break;
					}
					case NotifyItem.newsattachmentxls:
					case NotifyItem.xls:{
						img.setImageDrawable(getResources().getDrawable(R.drawable.xls));
						break;
					}
					case NotifyItem.webpage:{
						img.setImageDrawable(getResources().getDrawable(R.drawable.webpage));
						break;
					}
					case NotifyItem.quicklink:{
						img.setImageDrawable(getResources().getDrawable(R.drawable.quicklink));
						break;
					}
					case NotifyItem.others:{
						img.setImageDrawable(getResources().getDrawable(R.drawable.other));
						break;
					}
				}
				
				if(cd.iscompelte){
					progress.setMax(1);
					progress.setProgress(1);
				}
				
				
				
			}
			
			

			
			
			
			
			
			
			
			return ci!=null?ci.GetView():cd.GetView();
		}
    }
}
