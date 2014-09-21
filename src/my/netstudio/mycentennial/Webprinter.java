package my.netstudio.mycentennial;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Text;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import my.netstudio.mycentennial.SetupDlg.SetupItems;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;



class PRINTERITEM{
	String printname;
	int level;
	int pointx,pointy;
	String remark;
}

class LOCATION{
	String building;
	List<PRINTERITEM> p;
}

class CAMPUSINFO{
	String campusinfo;
	List<LOCATION> p;
}




public class Webprinter extends Activity implements OnItemSelectedListener, OnClickListener,AdListener , Runnable{
	public static final int WEBPRINT_CENTENNIAL=0;
	public static String WEBPRINT_LIST="Progress Campus,Libary,A,B,C,D,E$pracad02\\A311,0,0,0,remark none,A$pracad02\\A313,0,0,0,remark none,A$pracad02\\A315,0,0,0,remark none,A$pracad02\\A317,0,0,0,remark none,A$pracad02\\A321,0,0,0,remark none,A$pracad02\\A322,0,0,0,remark none,A$pracad02\\A323,0,0,0,remark none,A$pracad02\\A324,0,0,0,remark none,A$pracad02\\A326,0,0,0,remark none,A$pracad02\\A332,0,0,0,remark none,A$pracad02\\A334,0,0,0,remark none,A$pracad02\\A361,0,0,0,remark none,A$pracad02\\A365,0,0,0,remark none,A$pracad02\\E117,0,0,0,remark none,E$pracad02\\E227,0,0,0,remark none,E$pracad02\\E322,0,0,0,remark none,E$pracad02\\E323,0,0,0,remark none,E$pracad02\\E324,0,0,0,remark none,E$pracad02\\PR-ITD-P1,0,0,0,remark none,Libary$pracad02\\PR-LIB-FLOOR2,0,0,0,remark none,Libary$pracad02\\PR-LIB-FLOOR3,0,0,0,remark none,Libary$pracad02\\PR-LIB-FLOOR1,0,0,0,remark none,Libary$pracad02\\PR-LIB-FLOOR4,0,0,0,remark none,Libary$pracad02\\PR-LRCLAB-P1,0,0,0,remark none,Libary$pracad02\\PR-LRCLAB-P2,0,0,0,remark none,Libary$pracad02\\PR-LRCLAB-P3,0,0,0,remark none,Libary$pracad02\\PR-LRCLAB-P5,0,0,0,remark none,Libary$pracad02\\C103,0,0,0,remark none,C$pracad02\\C211,0,0,0,remark none,C$pracad02\\C211e,0,0,0,remark none,C$pracad02\\C213A,0,0,0,remark none,C$pracad02\\D109,0,0,0,remark none,D$pracad02\\D110,0,0,0,remark none,D$pracad02\\D110 Autocad & Inventor,0,0,0,remark none,D$pracad02\\D111,0,0,0,remark none,D$pracad02\\D112 Autocad & Inventor,0,0,0,remark none,D$pracad02\\D112 Mastercam,0,0,0,remark none,D$pracad02\\D112-PLOTTER,0,0,0,remark none,D$pracad02\\D115,0,0,0,remark none,D$pracad02\\D122,0,0,0,remark none,D$pracad02\\D123 Autocad & Inventor,0,0,0,remark none,D$pracad02\\D208,0,0,0,remark none,D$pracad02\\D311 Autocad & Inventor,0,0,0,remark none,D$pracad02\\D314 Mastercam,0,0,0,remark none,D$pracad02\\A352,0,0,0,remark none,A$pracad02\\A363,0,0,0,remark none,A$pracad02\\A365,0,0,0,remark none,A$pracad02\\A304,0,0,0,remark none,A$pracad02\\B304-Progress,0,0,0,remark none,B$pracad02\\B305,0,0,0,remark none,B$pracad02\\B306,0,0,0,remark none,B$pracad02\\B307,0,0,0,remark none,B$pracad02\\B308,0,0,0,remark none,B$pracad02\\B309,0,0,0,remark none,B$pracad02\\B323,0,0,0,remark none,B$pracad02\\B325,0,0,0,remark none,B\r\nAshtonbee Campus,A,D,E,F$asacad01\\A105,0,0,0,remark none,A$asacad01\\A111,0,0,0,remark none,A$asacad01\\A121,0,0,0,remark none,A$asacad01\\A129,0,0,0,remark none,A$asacad01\\A142,0,0,0,remark none,A$asacad01\\A207,0,0,0,remark none,A$asacad01\\A208,0,0,0,remark none,A$asacad01\\A209,0,0,0,remark none,A$asacad01\\AS-SC,0,0,0,remark none,A$asacad01\\D141,0,0,0,remark none,D$asacad01\\D143,0,0,0,remark none,D$asacad01\\D156,0,0,0,remark none,D$asacad01\\E102,0,0,0,remark none,E$asacad01\\E111,0,0,0,remark none,E$asacad01\\E116,0,0,0,remark none,E$asacad01\\F116,0,0,0,remark none,F\r\nMorningside Campus,-$hpacad01\\150A,0,0,0,remark none,-$hpacad01\\160,0,0,0,remark none,-$hpacad01\\190,0,0,0,remark none,-$hpacad01\\236,0,0,0,remark none,-$hpacad01\\304XP,0,0,0,remark none,-$hpacad01\\306,0,0,0,remark none,-$hpacad01\\306XP,0,0,0,remark none,-$hpacad01\\308,0,0,0,remark none,-$hpacad01\\310,0,0,0,remark none,-$hpacad01\\312,0,0,0,remark none,-$hpacad01\\314,0,0,0,remark none,-$hpacad01\\322,0,0,0,remark none,-$hpacad01\\332,0,0,0,remark none,-$hpacad01\\332-Plotter,0,0,0,remark none,-$hpacad01\\404,0,0,0,remark none,-$hpacad01\\406,0,0,0,remark none,-$hpacad01\\408,0,0,0,remark none,-$hpacad01\\410,0,0,0,remark none,-$hpacad01\\412,0,0,0,remark none,-$hpacad01\\414,0,0,0,remark none,-$hpacad01\\416,0,0,0,remark none,-$hpacad01\\420,0,0,0,remark none,-$hpacad01\\422,0,0,0,remark none,-$hpacad01\\424,0,0,0,remark none,-$hpacad01\\426,0,0,0,remark none,-$hpacad01\\HPMAC406,0,0,0,remark none,-$hpacad01\\MAC304,0,0,0,remark none,-$hpacad01\\MAC306,0,0,0,remark none,-$hpacad01\\MAC306b,0,0,0,remark none,-$hpacad01\\MAC322,0,0,0,remark none,-$hpacad01\\MS-CCSAI-P1,0,0,0,remark none,-$hpacad01\\MS-ITDCSC-P1,0,0,0,remark none,-\r\nEast York Campus,-$TCMAC149,0,0,0,remark none,-$TCMAC155,0,0,0,remark none,-$TCMAC211,0,0,0,remark none,-$TCMAC213,0,0,0,remark none,-$TCMAC220,0,0,0,remark none,-$TCMAC225,0,0,0,remark none,-$TCMAC228,0,0,0,remark none,-$TCMAC243,0,0,0,remark none,-$TCMAC247,0,0,0,remark none,-$TCMAC255,0,0,0,remark none,-$TCMAC272,0,0,0,remark none,-$TCMAC284,0,0,0,remark none,-$141,0,0,0,remark none,-$226,0,0,0,remark none,-$LabLRC,0,0,0,remark none,-$TC-121,0,0,0,remark none,-$TC-MAC220,0,0,0,remark none,-$tc-mfp-pcl-149-1,0,0,0,remark none,-$tc-mfp-pcl-213-1,0,0,0,remark none,-$tc-mfp-pcl-247-1,0,0,0,remark none,-$tc-mfp-ps-149-1,0,0,0,remark none,-$tc-mfp-ps-213-1,0,0,0,remark none,-$tc-mfp-ps-247-1,0,0,0,remark none,-$tc-mfp-ps-275-1,0,0,0,remark none,-$tc-prt-141-1,0,0,0,remark none,-$tc-prt-pcl-141-1,0,0,0,remark none,-$tc-prt-pcl-143-1,0,0,0,remark none,-$tc-prt-pcl-145-1,0,0,0,remark none,-$tc-prt-pcl-155-1,0,0,0,remark none,-$tc-prt-pcl-207-1,0,0,0,remark none,-$tc-prt-pcl-211-1,0,0,0,remark none,-$tc-prt-pcl-214-1,0,0,0,remark none,-$tc-prt-pcl-220-1,0,0,0,remark none,-$tc-prt-pcl-225-1,0,0,0,remark none,-$tc-prt-pcl-226-1,0,0,0,remark none,-$tc-prt-pcl-228-1,0,0,0,remark none,-$tc-prt-pcl-240-1,0,0,0,remark none,-$tc-prt-pcl-240-2,0,0,0,remark none,-$tc-prt-pcl-241-1,0,0,0,remark none,-$tc-prt-pcl-243-1,0,0,0,remark none,-$tc-prt-pcl-255-1,0,0,0,remark none,-$tc-prt-pcl-272-1,0,0,0,remark none,-$tc-prt-pcl-284-1,0,0,0,remark none,-$tc-prt-ps-145-1,0,0,0,remark none,-$tc-prt-ps-155-1,0,0,0,remark none,-$tc-prt-ps-211-1,0,0,0,remark none,-$tc-prt-ps-220-1,0,0,0,remark none,-$tc-prt-ps-225-1,0,0,0,remark none,-$tc-prt-ps-228-1,0,0,0,remark none,-$tc-prt-pcl-240-1,0,0,0,remark none,-$tc-prt-pcl-240-2,0,0,0,remark none,-$tc-prt-pcl-241-1,0,0,0,remark none,-$tc-prt-pcl-243-1,0,0,0,remark none,-$tc-prt-pcl-255-1,0,0,0,remark none,-$tc-prt-pcl-272-1,0,0,0,remark none,-$tc-prt-pcl-284-1,0,0,0,remark none,-$tc-prt-ps-145-1,0,0,0,remark none,-$tc-prt-ps-155-1,0,0,0,remark none,-$tc-prt-ps-211-1,0,0,0,remark none,-$tc-prt-ps-220-1,0,0,0,remark none,-$tc-prt-ps-225-1,0,0,0,remark none,-$tc-prt-ps-228-1,0,0,0,remark none,-$tc-prt-ps-243-1,0,0,0,remark none,-$tc-prt-ps-255-1,0,0,0,remark none,-$TCCLRMAC,0,0,0,remark none,-$TCMAC145,0,0,0,remark none,-";
	String filepath=null;
	Button m_ok,m_cancel,m_finish;
	CheckBox m_save;
	
	boolean isrun=false;
	View processview=null;
	AdView adView = null;//(AdView) this.findViewById(R.id.adView1);//new AdView(this, AdSize.BANNER, "a1511b1f983d53c");		    
    AdRequest request = null;//new AdRequest();
	ImageView favor,twitter,googleplus;
	boolean issucessful;
	EditText m_usr,m_pwd;
	Spinner campussel=null;
    Spinner blocksel=null;
    Httpmanager mgr=null;
    WebPrinterMgr webprintmgr= null;
    Spinner printsel=null;
    boolean istxt=false;
    
    public static Handler myHandler = null;
    public static Webprinter pthis=null;
    
    TextView printmsg1,printmsg2,printmsg3;
    
    int blocklastestsel=-1;
    int printlastsel=-1;
    List<CAMPUSINFO> p=new ArrayList<CAMPUSINFO>();
    PopupWindow mPopupWindow =null;
    
    
    public void WriteSysFile(String filename,String str){
    	try{
    		  
    		   File saveFile=new File(filename);
    		   
    		   FileOutputStream fout=this.getApplicationContext().openFileOutput(filename,this.MODE_PRIVATE);
    		   fout.write(str.getBytes());
    		   fout.close();
    	}
    	catch(Exception e){

    	}
    	
    }
    public String ReadsSysFile(String filename){
    	String ret="";
    	try{
    		  
    		  // File saveFile=new File("/sdcard/"+filename);

    				
    		   FileInputStream fin=this.getApplicationContext().openFileInput(filename);
    		   int len=fin.available();
    		   if(len>0){
    			   byte []buffer=new byte[len];
    			   fin.read(buffer);
    			   ret=new String(buffer,0,len);
    			   fin.close();
    		   }
    	
    	}
    	catch(Exception e){}
    	return ret;
    }
    private void LoadConfig(){
    	String str=ReadsSysFile("paccount");
    	if(str.length()>0){
    		String usr=str.substring(0, str.indexOf(","));
    		String pwd=str.substring(str.indexOf(",")+1);
    		m_usr.setText(usr);
    		m_pwd.setText(pwd);
    		m_save.setChecked(true);
    	}
    	else{
    		m_save.setChecked(false);
    	}
    	
    	str=ReadsSysFile("printsel");
    	if(str.length()>0){
    	
    		int campuslastestsel=Integer.valueOf(str.substring(0, str.indexOf(",")));
    		str=str.substring(str.indexOf(",")+1);
    		
    		blocklastestsel=Integer.valueOf(str.substring(0, str.indexOf(",")));
    		str=str.substring(str.indexOf(",")+1);
    		printlastsel=(Integer.valueOf(str));
    		
    		
    	}
    }
    private String LoadPrintList(String filename){
    	String ret="";
    	try{
    		File saveFile=new File(filename);
    		  // File saveFile=new File("/sdcard/"+filename);
    		   FileInputStream fin=new FileInputStream(saveFile);
    		   int len=fin.available();
    		   if(len>0){
    			   byte []buffer=new byte[len];
    			   fin.read(buffer);
    			   ret=new String(buffer,0,len);
    			   fin.close();
    		   }
    	
    	}
    	catch(Exception e){
    		BuildPrintList(filename);
    		ret=LoadPrintList(filename);
    	}
    	return ret;
    }
    private void BuildPrintList(String filename){
    	
    	try{
 		   File saveFile=new File(filename);
 		   if(saveFile.exists()==false)
 			   saveFile.createNewFile();
 		   
 		   FileOutputStream fout=new FileOutputStream(saveFile);//.o.pmain.openFileOutput(filename,0);
 		   byte[] bytes=WEBPRINT_LIST.getBytes();
 		   fout.write(bytes);
 		   fout.close();
 		   
	 	}
	 	catch(Exception e){
	 		String aa=e.toString();
	 		Log.e("",aa);
	 	}
    	return ;
    }
    private void SaveConfig(String filename,String str){
    	
    	WriteSysFile(filename,str);
    }
	public static int ScreenOrient(Activity activity){  
        //取得当前屏幕的方向，如果此值为-1表示androidManifest.xml没有设置Android:screanOrentation属性所以这样无法判断屏幕方向。  
        //可以使用另一种思路，即长度大于高度的为横屏，否则为竖屏。  
        int orientation = activity.getRequestedOrientation();//得到屏幕方向  
        int landscape = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;//横屏静态常量  
        int portrait = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;//竖屏常量  
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();//得到系统显示属性后得到屏幕宽度  
        int height = activity.getWindowManager().getDefaultDisplay().getHeight();//得到屏幕高度  
        return width>height?portrait:landscape;//判断  
    }  
	public void onCreate(Bundle savedInstanceState) {
		pthis=this;
		//	this.bindService(new Intent(this,EService.class),conn,BIND_AUTO_CREATE);
		//this.startService(new Intent(this,EService.class));	
        super.onCreate(savedInstanceState);
       // if(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT==ScreenOrient(this))
       // 	setContentView(R.layout.webprinter1);
        //else
        	setContentView(R.layout.webprinter);
        /*
         * Intent install = new Intent();
           install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
           install.setAction(android.content.Intent.ACTION_VIEW);
           install.setDataAndType(Uri.fromFile(mFile),"application/vnd.android.package-archive");
          startActivity(install);//安装
         * */
        String sdrootpath="";
        File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState()
		 .equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
		if (sdCardExist)
		{
			sdDir = Environment.getExternalStorageDirectory();//获取跟目录
			sdrootpath=sdDir.toString()+"/wifi printer/";
			File file = new File(sdrootpath);
			if (!file.exists()) {
				file.mkdir();
				BuildPrintList(sdrootpath+"printerlist");
			}
		}
		else{
			AlertDialog.Builder normalDia=new AlertDialog.Builder(Webprinter.this);
	        normalDia.setIcon(R.drawable.ic_launcher);
	        normalDia.setTitle("Error");
	        normalDia.setMessage("A SD card can not be found.");
	        
	        normalDia.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	                // TODO Auto-generated method stub
	            	dialog.dismiss();
	            }
	        });
	        
	        normalDia.create().show();
	        finish();
			
		}
		
		WEBPRINT_LIST=LoadPrintList(sdrootpath+"printerlist");
		
        Intent intent = getIntent();
        String action = intent.getAction();
        
        m_usr=(EditText)this.findViewById(R.id.username);
        m_pwd=(EditText)this.findViewById(R.id.password);
        m_ok=(Button)this.findViewById(R.id.Print);
        m_cancel=(Button)this.findViewById(R.id.Cancel);
        m_ok.setOnClickListener(this);
        m_cancel.setOnClickListener(this);
        m_save=(CheckBox)this.findViewById(R.id.remembercheckbox);
        m_ok.setFocusable(true);
        m_finish=(Button)this.findViewById(R.id.OK);
        m_finish.setVisibility(View.INVISIBLE);
        m_finish.setOnClickListener(this);
        
        
        
        
        
        
        campussel=(Spinner)this.findViewById(R.id.campus);
        blocksel=(Spinner)this.findViewById(R.id.block);
        printsel=(Spinner)this.findViewById(R.id.printer);
        
//       ArrayList<String> allcountries=new ArrayList<String>();  
//       for(int i=0;i<countries.length;i++){  
//           allcountries.add(countries[i]);  
//       }  
        ArrayList<String> campus=new ArrayList<String>();
         
        
        
    		myHandler=new Handler(Looper.myLooper ()) {  
    			  
    		    public void handleMessage(Message msg) {  
    		    	String value=(String)msg.obj;
    		    	switch(Integer.parseInt(value.substring(0,value.indexOf(":")))){
    		    		case 0:{//update ret
    		    			printmsg1.setText(((String)msg.obj).substring(2));
    		    			
    		    	        
    		    			break;
    		    		}
    		    		case 1:{//update ret
    		    			//retstr.setText(((String)msg.obj).substring(2));
    		    			printmsg2.setText(((String)msg.obj).substring(2));
    		    	        
    		    			break;
    		    		}
    		    		case 2:{//update ret
    		    			//retstr.setText(((String)msg.obj).substring(2));
    		    			printmsg3.setText(((String)msg.obj).substring(2));
    		    			break;
    		    		}
    		    		case 3:{
    		    			m_finish.setVisibility(View.VISIBLE);
    		    			if(issucessful){
	    		    			favor.setVisibility(View.VISIBLE);//=(ImageView)this.findViewById(R.id.Fav);
	    				        twitter.setVisibility(View.VISIBLE);
	    				        googleplus.setVisibility(View.VISIBLE);
    		    			}
    		    			break;
    		    			
    		    		}
    		    		case 4:{

    		    			mPopupWindow = new PopupWindow(processview,LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);          
    		    			mPopupWindow.showAsDropDown(adView);
    		    			break;
    		    			
    		    		}
    		    		
    		    	}
    		    	
    		    }  
    		};  
    	
        
        
        String prtlist=WEBPRINT_LIST;
        while(prtlist.length()>0){
        	String line=null;
        	int ccc=prtlist.indexOf("\r\n");
        	if(prtlist.indexOf("\r\n")!=-1){
        		line=prtlist.substring(0, prtlist.indexOf("\r\n"));
        		prtlist=prtlist.substring( prtlist.indexOf("\r\n")+2);
        	}
        	else{
        		line=prtlist;
        		prtlist="";
        	}
        	
        	String campusname=line.substring(0,line.indexOf(("$")));
			String block=campusname.substring(campusname.indexOf((","))+1);
			campusname=campusname.substring (0,campusname.indexOf ((",")));
			line=line.substring(line.indexOf(("$"))+1);
			
			if(campusname.length()>0){
				CAMPUSINFO pcampus=new CAMPUSINFO();
				pcampus.campusinfo=campusname;
				pcampus.p=new ArrayList<LOCATION>();
				p.add(pcampus);
				campus.add(campusname);

				while(block.length ()>0){
					LOCATION ploc=new LOCATION();
					ploc.p=new ArrayList<PRINTERITEM>();
					String blockitem;
					if(block.indexOf ((","))==-1){
						blockitem=block;
						block=("");
					}
					else{
						blockitem=block.substring(0,block.indexOf((",")));
						block=block.substring(block.indexOf((","))+1);
					}

					ploc.building=blockitem;
					pcampus.p.add(ploc);
					

				}
				
				
				while(line.length ()>0){
					String printitem;
					if(line.indexOf (("$"))==-1){
						printitem=line;
						line=("");
					}
					else{
						printitem=line.substring(0,line.indexOf(("$")));
						line=line.substring(line.indexOf(("$"))+1);
					}

					PRINTERITEM p=new PRINTERITEM();
					p.printname=printitem.substring(0,printitem.indexOf((",")));
					printitem=printitem.substring (printitem.indexOf(",")+1);
					p.level=Integer.valueOf(printitem.substring(0,printitem.indexOf((","))));
					printitem=printitem.substring (printitem.indexOf(",")+1);
					p.pointx=Integer.valueOf(printitem.substring(0,printitem.indexOf((","))));
					printitem=printitem.substring (printitem.indexOf(",")+1);
					p.pointy=Integer.valueOf(printitem.substring(0,printitem.indexOf((","))));
					printitem=printitem.substring (printitem.indexOf(",")+1);
					p.remark=printitem.substring (0,printitem.indexOf(","));
					String blackname=printitem.substring (printitem.indexOf(",")+1);
					
					
					for(int index=0;index<pcampus.p.size();index++){
						LOCATION ploc=pcampus.p.get(index);
						if(ploc.building .compareToIgnoreCase(blackname)==0){
							ploc.p.add(p);
							break;
						}
					}
				}
			}
        }


        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,campus);  
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
        campussel.setAdapter(adapter);  
               
        campussel.setOnItemSelectedListener(this);  
        blocksel.setOnItemSelectedListener(this);  
        printsel.setOnItemSelectedListener(this); 
        campussel.setSelection(0);  
        
        LoadConfig();
        
        
        if(intent.ACTION_VIEW.equals(action)){
        	TextView tv = (TextView)findViewById(R.id.progress);
        	boolean iscontent=true;
        	String extname="";
        	String uripath=intent.getDataString();
        	if(uripath.indexOf("file:")==0)
        		iscontent=false;
        	
        	if(!iscontent){
        		extname=intent.getDataString();
        		while(extname.indexOf(".")!=-1)
        			extname=extname.substring(extname.indexOf(".")+1);
        	}
        	else{
        		
        		String contenttype=intent.getType().toLowerCase();
            	if(contenttype.indexOf("text/plain")!=-1)
        			extname="txt";
        		else if(contenttype.indexOf("application/msword")!=-1)
        			extname="doc";
        		else if(contenttype.indexOf("application/ms-word")!=-1)
        			extname="doc";
        		else if(contenttype.indexOf("application/vnd.ms-word")!=-1)
        			extname="doc";
        		else if(contenttype.indexOf("application/vnd.ms-works")!=-1)
        			extname="wcm";
        		else if(contenttype.indexOf("application/pdf")!=-1)
        			extname="pdf";
            	
        		else if(contenttype.indexOf("application/vnd.openxmlformats-officedocument.wordprocessingml.document")!=-1)
        			extname="docx";
        		else if(contenttype.indexOf("application/vnd.openxmlformats-officedocument.wordprocessingml.template")!=-1)
        			extname="dotx";
        		else if(contenttype.indexOf("application/vnd.ms-powerpoint.template.macroEnabled.12")!=-1)
        			extname="potm";
        		else if(contenttype.indexOf("application/vnd.ms-powerpoint.slideshow.macroEnabled.12")!=-1)
        			extname="ppsm";
        		else if(contenttype.indexOf("application/vnd.ms-powerpoint.presentation.macroEnabled.12")!=-1)
        			extname="pptm";
        		else if(contenttype.indexOf("application/vnd.ms-powerpoint.addin.macroEnabled.12")!=-1)
        			extname="ppam";
        		else if(contenttype.indexOf("application/vnd.openxmlformats-officedocument.presentationml.slideshow")!=-1)
        			extname="ppsx";
        		else if(contenttype.indexOf("application/vnd.openxmlformats-officedocument.presentationml.template")!=-1)
        			extname="potx";
        		else if(contenttype.indexOf("application/vnd.openxmlformats-officedocument.presentationml.presentation")!=-1)
        			extname="pptx";
        		else if(contenttype.indexOf("application/vnd.ms-powerpoint")!=-1)
        			extname="ppt";
        		else if(contenttype.indexOf("application/vnd.ms-excel.sheet.binary.macroEnabled.12")!=-1)
        			extname="xlsb";
        		else if(contenttype.indexOf("application/vnd.ms-excel.addin.macroEnabled.12")!=-1)
        			extname="xlam";
        		else if(contenttype.indexOf("application/vnd.ms-excel.template.macroEnabled.12")!=-1)
        			extname="xltm";
        		else if(contenttype.indexOf("application/vnd.ms-excel.sheet.macroEnabled.12")!=-1)
        			extname="xlsm";
        		else if(contenttype.indexOf("application/vnd.openxmlformats-officedocument.spreadsheetml.template")!=-1)
        			extname="xltx";
        		else if(contenttype.indexOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")!=-1)
        			extname="xlsx";
        		else if(contenttype.indexOf("application/vnd.ms-excel")!=-1)
        			extname="xls";
        		else if(contenttype.indexOf("application/vnd.ms-word.template.macroEnabled.12")!=-1)
        			extname="dotm";
        		else if(contenttype.indexOf("application/vnd.ms-word.document.macroEnabled.12")!=-1)
        			extname="docm";

        	}
        	
        	if(extname.length()==0){
        		AlertDialog.Builder builder = new AlertDialog.Builder(this);  
		        builder.setMessage("Error: Unknown File Type")  
		               .setCancelable(false)  
		               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {  
		                   public void onClick(DialogInterface dialog, int id) {  
		                	   
		                	    dialog.dismiss();
		                   }  
		               });  
		        builder.create().show();
				this.finish();
        	}
        	if(intent.getType().toLowerCase().indexOf("text")!=-1){
        		istxt=true;}
        	
        	
        	
        	//text/plain
        	
        	Uri uri = null;  
            uri = Uri.parse(uripath);
            List<String> segments = uri.getPathSegments();  
            String dbName = segments.get(0);  
     //       String id = segments.get(1);  
      //      String format = segments.get(2);  
            //application/pdf
            //if(filepath==null){
        	 try {  
                 InputStream in = getContentResolver().openInputStream(uri);//Uri.parse(intent.getDataString()));  
                 // 输出文件同样，需要增加人机交互  
                 filepath=sdrootpath+Long.toString(System.currentTimeMillis())+"."+extname;
                 FileOutputStream  fos = new FileOutputStream(filepath,true);  
                 int ch = 0;  
                 byte []buf=new byte[1024];
                 while((ch = in.read(buf)) != -1){  
                     fos.write(buf,0,ch);  
                 }  
                 fos.close();  
 
             } catch (MalformedURLException e) {  
                 // TODO Auto-generated catch block  
                 e.printStackTrace();  
             } catch (IOException e) {  
                 // TODO Auto-generated catch block  
                 e.printStackTrace();  
             }  
            
        	 
         //   tv.setText(filepath);
        	
            
            
           
        }
      
        mgr=new Httpmanager();
        this.webprintmgr=new WebPrinterMgr();
        
        
        
        adView = (AdView) this.findViewById(R.id.adView1);//new AdView(this, AdSize.BANNER, "a1511b1f983d53c");		    
	    request = new AdRequest();
	    adView.setVisibility(View.INVISIBLE);
	    adView.setAdListener(this);
	    
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
   
        InputMethodManager m=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
    //    m.hideSoftInputFromWindow(myEditText.getWindowToken(), 0); 
        if(!isecentennialinstalled){
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);  
            builder.setMessage(this.getString(R.string.installtips))
            		.setCancelable(false)  
                   .setPositiveButton("Yes", new DialogInterface.OnClickListener() {  
                       public void onClick(DialogInterface dialog, int id) {  
                    	   
                    	   Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=my.netstudio.mycentennial&feature=search_result#?t=W251bGwsMSwxLDEsIm15Lm5ldHN0dWRpby5teWNlbnRlbm5pYWwiXQ..");
                   			
                    	   Intent shareIntent = new Intent(Intent.ACTION_VIEW,uri);
                   				startActivity(shareIntent);
                   		
                    	   //Toast.makeText(EService.gContent,"Connecting...just wait for few minites then try again later ", Toast.LENGTH_LONG).show();
                    	   dialog.dismiss();
                       }  
                   }).setNegativeButton("No", new DialogInterface.OnClickListener() {  
                       public void onClick(DialogInterface dialog, int id) {  
                    	   dialog.dismiss();
                       }  
                   });  
                   
            
    		builder.create().show();
        }
        
	}
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		if(arg0==campussel){
			String selitemtxt=campussel.getSelectedItem().toString();
			for(int index=0;index<p.size();index++){
				CAMPUSINFO ploc=p.get(index);
				if(ploc.campusinfo.compareToIgnoreCase(selitemtxt)==0){
					ArrayList<String> block=new ArrayList<String>();   
					for(int index1=0;index1<ploc.p.size();index1++){
						LOCATION pblock=ploc.p.get(index1);
						block.add(pblock.building);
					}
					ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,block);  
		            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
		            this.blocksel.setAdapter(adapter);  
		            if(blocklastestsel!=-1)
		            	blocksel.setSelection(blocklastestsel); 
		            else
		            	blocksel.setSelection(0); 
					break;
				}
			}
			
		}
		else if(arg0==blocksel){
			String selitemtxt=blocksel.getSelectedItem().toString();
			String campusname=campussel.getSelectedItem().toString();
			CAMPUSINFO psel=null;
			for(int index=0;index<p.size();index++){
				CAMPUSINFO ploc=p.get(index);
				if(ploc.campusinfo.compareToIgnoreCase(campusname)==0){
					psel=ploc;
					break;
				}
			}
			
			
			for(int index=0;index<psel.p.size();index++){
				LOCATION ploc=psel.p.get(index);
				if(ploc.building.compareToIgnoreCase(selitemtxt)==0){
					ArrayList<String> block=new ArrayList<String>();   
					for(int index1=0;index1<ploc.p.size();index1++){
						PRINTERITEM pprint=ploc.p.get(index1);
						block.add(pprint.printname);
					}
					ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,block);  
		            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
		            this.printsel.setAdapter(adapter); 
		            
		            if(this.printlastsel!=-1)
			            printsel.setSelection(printlastsel); 
		            else
		            	printsel.setSelection(0); 
					break;
				}
			}
			
		}
	}
	public void onNothingSelected(AdapterView<?> arg0) {
		
	}
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v==m_ok){
			if(m_save.isChecked()){
				String str=m_usr.getText().toString()+","+m_pwd.getText().toString();
				SaveConfig("paccount",str);
				
				str=String.valueOf(this.campussel.getSelectedItemId())+","+String.valueOf(this.blocksel.getSelectedItemId())+","+String.valueOf(this.printsel.getSelectedItemId());
				SaveConfig("printsel",str);
			}
			else
				SaveConfig("paccount","");
			
		
			if(!isWifiConnected()){
				AlertDialog.Builder builder = new AlertDialog.Builder(this);  
		        builder.setMessage("Wifi is not connected")  
		               .setCancelable(false)  
		               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {  
		                   public void onClick(DialogInterface dialog, int id) {  
		                	   
		                	    dialog.dismiss();
		                   }  
		               });  
		           
		        
				builder.create().show();
				
				return;
			}
			//mPopupWindow.showAtLocation(this.findViewById(R.id.WebPrintMain), Gravity.CENTER, 20, 20);//在屏幕居中，无偏移 
			
		//	try{
			
				LayoutInflater mLayoutInflater = (LayoutInflater) this.getApplicationContext()  
		                .getSystemService(LAYOUT_INFLATER_SERVICE);  
		        processview = mLayoutInflater.inflate( R.layout.printprocess, null);
		        processview.setBackgroundColor(Color.GRAY);//(getResources().getDrawable(R.drawable.fantasy_pictuer_06));
		    /*    processview.setOnClickListener(new View.OnClickListener() {
		                
		                public void onClick(View v) {
		                        // TODO Auto-generated method stub
		                	mPopupWindow.dismiss();
		                	//this.findViewById(R.id.scrollView1).setVisibility(true);
		                }
		        });*/
		        
		        favor=(ImageView)processview.findViewById(R.id.Fav);
		        twitter=(ImageView)processview.findViewById(R.id.twitter);
		        googleplus=(ImageView)processview.findViewById(R.id.google);
		        favor.setOnClickListener(this);
		        googleplus.setOnClickListener(this);
		        twitter.setOnClickListener(this);
		        printmsg1=(TextView) processview.findViewById(R.id.textView1);
		        printmsg2=(TextView)processview.findViewById(R.id.textView2);
		        printmsg3=(TextView)processview.findViewById(R.id.textView3);
		        
		        this.findViewById(R.id.lad1).setVisibility(View.VISIBLE);
		        adView.setVisibility(View.VISIBLE);
		        //request.setTesting(true);
				adView.loadAd(request);
		        
				this.findViewById(R.id.scrollView1).setVisibility(View.INVISIBLE);
				findViewById(R.id.progress).setVisibility(View.INVISIBLE);
				findViewById(R.id.imageView1).setVisibility(View.INVISIBLE);
				
				
		        
		        
		        

		        
				
		        
		        
		        
				favor.setVisibility(View.INVISIBLE);//=(ImageView)this.findViewById(R.id.Fav);
		        twitter.setVisibility(View.INVISIBLE);
		        googleplus.setVisibility(View.INVISIBLE);
		//	}
		//	catch(Exception e){
		//		String aaa=e.toString();
		//		aaa="";
		//	}
			
		}
		else if(v==this.m_cancel){
			File file = new File(filepath);
			file.delete();
			this.finish();
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
		else if(v==m_finish){
			if(!issucessful){
				mPopupWindow.dismiss();
				m_finish.setVisibility(View.INVISIBLE);
				//adView.setVisibility(View.INVISIBLE);
				findViewById(R.id.lad1).setVisibility(View.INVISIBLE);
				this.findViewById(R.id.scrollView1).setVisibility(View.VISIBLE);
				findViewById(R.id.progress).setVisibility(View.VISIBLE);
				findViewById(R.id.imageView1).setVisibility(View.VISIBLE);
			}
			else
				this.finish();
			
		}
	}
	private boolean isWifiConnected()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(wifiNetworkInfo.isConnected())
        {
            return true ;
        }
     
        return false ;
    }
	private void Msgbox(String msg){
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);  
        builder.setMessage(msg)  
               .setCancelable(false)  
               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {  
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
	public void run() {
		// TODO Auto-generated method stub
		try{
			Thread.sleep(500);
		}
		catch(Exception e){}
		
		issucessful=false;
		
		Message msg = myHandler.obtainMessage();  
        msg.obj = "4:" ;
        myHandler.sendMessage(msg); 
        
		if(istxt){//转换 pdf
			
			msg = myHandler.obtainMessage();  
	        msg.obj = getString(R.string.doc2pdf1) ;
	        myHandler.sendMessage(msg); 
	        
	        msg = myHandler.obtainMessage();  
	        msg.obj = getString(R.string.doc2pdf2) ;
	        myHandler.sendMessage(msg); 
	        
	        msg = myHandler.obtainMessage();  
	        msg.obj = "2:";
	        myHandler.sendMessage(msg); 

			ArrayList<BasicNameValuePair> postParams=new ArrayList<BasicNameValuePair>();
			postParams.add(new BasicNameValuePair("outputFormat","pdf"));
			if(mgr.Upload("http://www.doc2pdf.net/convert/document.pdf","inputDocument",filepath,filepath+".pdf",postParams,0,0)){
				//convert finished
				
				msg = myHandler.obtainMessage();  
				msg.obj = getString(R.string.doc2pdf3) ;
		        myHandler.sendMessage(msg); 
		        
		        
				File file = new File(filepath);
				file.delete();
				filepath+=".pdf";
				istxt=false;
				//log in webprinter
			}
			else{
				msg = myHandler.obtainMessage();  
				msg.obj = getString(R.string.doc2pdf4) ;
		        myHandler.sendMessage(msg); 
				return;
			}
			
			
			
			
		}
		
		webprintmgr.SetAccountInfo(this.m_usr.getText().toString(), m_pwd.getText().toString());
		if(this.webprintmgr.Login(WEBPRINT_CENTENNIAL)){
			if(webprintmgr.Print(this.printsel.getSelectedItem().toString(), filepath, 1, WEBPRINT_CENTENNIAL)==true)
			{
				issucessful=true;
				
				File file = new File(filepath);
				file.delete();
				
				int pages=0;
				float cost=0;
				
				String m_balance=String.format("0:Balance: $%.2f (%.2f-%.2f)",webprintmgr.m_balance-webprintmgr.m_cost,webprintmgr.m_balance,webprintmgr.m_cost);
				msg = myHandler.obtainMessage();  
				msg.obj = m_balance;
		        myHandler.sendMessage(msg); 
		        
		        
				
//				::ShowWindow(::GetDlgItem(phandle,IDC_BUTTON1),SW_SHOW);
//				::SendMessage (phandle,WM_PROGRESSERROR,(DWORD)(LPCTSTR)theApp.web.GetErrorStr(),theApp.web .isOutofBalance ()?NULL:(DWORD)"");
//				return 0;
			}
		}
		else{
			
			
		}
		
		
		
		
		msg = myHandler.obtainMessage();  
        msg.obj = "3:";
        myHandler.sendMessage(msg); 
        isrun=false;
		
		
		//this.finish();
	}
	public void onDismissScreen(Ad arg0) {
		// TODO Auto-generated method stub
		
	}
	public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
		// TODO Auto-generated method stub
		
	}
	public void onLeaveApplication(Ad arg0) {
		// TODO Auto-generated method stub
		
	}
	public void onPresentScreen(Ad arg0) {
		// TODO Auto-generated method stub
		
	}
	public void onReceiveAd(Ad arg0) {
		// TODO Auto-generated method stub
		if(!isrun){
			new Thread(this).start();
			isrun=true;
		}
	}
}
