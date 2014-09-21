package my.netstudio.mycentennial;




import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;

import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.Toast;

/*
邮件
新闻
课程
课表
*/



public class ecentennial {
	//https://banner.centennialcollege.ca/prod/bwskfshd.P_CrseSchd
	//init function
	public static String sdrootpath="";
	public boolean isInitFinished(){
		
		return initfinished;
	}
	public int InitSiteSnapShot(){

	//	WriteSysFile("download","");;
		try{
			/*String accountinfo=ecentennial.ReadsDBFile("Account");
			if(accountinfo.length()==0){//no account info
				EService.t.suspend();
			}
			else{
				this.Logintest(accountinfo.substring(0, accountinfo.indexOf("%^&*")), accountinfo.substring(accountinfo.indexOf("%^&*")+4));
				if(logteststatus>0){
					EService.t.suspend();
				}
			}*/
			
			if(IsFirstInstall()){
				
				int ret=0;
				isinprocess=true;
				
				if(homepagestr.length()==0){
					String accountinfo=ReadsSysFile(EService.gContent,"Account");
					ret=Logintest(accountinfo.substring(0, accountinfo.indexOf("%^&*")), accountinfo.substring(accountinfo.indexOf("%^&*")+4));
					if(ret!=0)
						return ret;
				}
				
				
				if((ret=InitUpdateSiteURLS())!=0)
					return ret;//user pwd error;
				
				CheckUpdateNews(true);
				initfinished=true;
				
				CheckUpdateCourse(true);
				SaveNodesToDisk(true); 
				isinprocess=false;
			}
			else{

				LoadLocalFile();
				initfinished=true;
				
			}
		}
		catch(Exception e){
			
			return -2;//sdcard error
		}
		
		return 0;
	}
	//operation function
	public int CheckUpdate() throws Exception{
		int ret=0;
		
		isinprocess=true;
		String accountinfo=ReadsSysFile(EService.gContent,"Account");
		ret=Logintest(accountinfo.substring(0, accountinfo.indexOf("%^&*")), accountinfo.substring(accountinfo.indexOf("%^&*")+4));
		if(ret!=0){
			isinprocess=false;
			return ret;
		}
		
		if(newsurl.length()==0||courlist.size()==0||MainActivity.httpsmgr.GetCookie().length()==0)
			InitUpdateSiteURLS();
		
		if(isD2LUpdate()){
			if(CheckUpdateNews(false))
				ret=10;
			
			initfinished=true;
			
			if(CheckUpdateCourse(false))
				ret=10;
			
			this.removeD2LUnreadMsg();
		}
		SaveNodesToDisk(true);
		isinprocess=false;
		return ret;
	}
	public boolean StartDownloadTask(DownloadTaskItem dtask,String taskname,String url,String cookie,String postparam,String filename,long objid,Object obj,Handler callback,int filefrom){
		if(sdrootpath.compareToIgnoreCase("error")==0){
			Toast.makeText(EService.gContent,"Error,Can not find SD Card!", Toast.LENGTH_LONG).show();
			return false;
		}

		boolean ret=false;
		if(!EService.isdownloadin3g&&!EService.isWifiConnected()){
			MainActivity.dtask= dtask;
			MainActivity.taskname=taskname;
			MainActivity.url=  url;
			MainActivity.cookie=  cookie;
			MainActivity.postparam=  postparam;
			MainActivity.filename=  filename;
			MainActivity.objid=  objid;
			MainActivity.obj=  obj;
			MainActivity.callback=  callback;
			MainActivity.filefrom=  filefrom;
			
			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.pmain);  
	        builder.setMessage("Do you want to download in 2G/3G network？It may take large flow!")  
	               .setCancelable(false)  
	               .setPositiveButton("Continue", new DialogInterface.OnClickListener() {  
	                   public void onClick(DialogInterface dialog, int id) {  
	                	   EService.isdownloadin3g=true;
	                	   
	                	   Message handlemsg = MainActivity.myHandler.obtainMessage();  
		           			handlemsg.obj = "8:";  
		           	        MainActivity.myHandler.sendMessage(handlemsg);  
	           	        
	                	   
	                	   dialog.dismiss();
	                   }  
	               })  
	               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {  
	                   public void onClick(DialogInterface dialog, int id) {  
	                	   dialog.dismiss();
	                   }  
	               });  
	        
			
			builder.create().show();
			
			/*
			LayoutInflater mLayoutInflater = (LayoutInflater)MainActivity.pmain.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        View view = mLayoutInflater.inflate(R.layout.listitem, null);

	        PopupWindow mPopupWindow = new PopupWindow(view, 400, LayoutParams.WRAP_CONTENT);
	        mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
	        mPopupWindow.update();
	        mPopupWindow.setTouchable(true);
	        mPopupWindow.setFocusable(true);
	        
	        if (!mPopupWindow.isShowing())
	        {
	            // mPopupWindow.showAsDropDown(view,0,0);
//	            mPopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
	            mPopupWindow.showAsDropDown(view);
	        }*/
		}	
		
		if(EService.isdownloadin3g||EService.isWifiConnected()){
			DownloadTaskItem pold=downloadtaskhash.get(taskname);
			if(pold==null||(dtask!=null&&dtask.ispause==true)){
				ret=true;
				DownloadTaskItem pitem= MainActivity.httpsmgr.StartDownloadTask(dtask,taskname,url,cookie,postparam,sdrootpath,filename,objid,obj,callback, filefrom); 
				((NotifyItem)pitem.m_obj).Setdownload(pitem);
				if(pold==null){
					downloadtaskhash.put(taskname, pitem);
					alldownloadtask.add(0, pitem);
				}
			}
		}
		return ret;
	}
	public void SaveNodesToDisk(boolean type){
		String bk="";
		if(type){
			synchronized(allnotifyitems){
				for(int i=0;i<allnotifyitems.size();i++){
					
					bk+=allnotifyitems.get(i).toString()+"@#@@#@";
					
				}
				WriteSysFile("data",bk);
			}
		}
		else{
			synchronized(alldownloadtask){
				for(int i=0;i<alldownloadtask.size();i++){
					
					bk+=alldownloadtask.get(i).toString()+"@#@@#@";
					
				}
				WriteSysFile("download",bk);
			}
		}
		//WriteDBFile(sdpath+"data",bk);
	}
	public void ClearTNewItemMark(int type){
		for(int i=0;i<allnotifyitems.size();i++){
			if(allnotifyitems.get(i).getType()==type){
				allnotifyitems.get(i).setOld();
			}
		}
    	
	}
	public void ClearSNewItemMark(long id){
		for(int i=0;i<allnotifyitems.size();i++){
			if(allnotifyitems.get(i).getID()==id){
				allnotifyitems.get(i).setOld();
			}
		}
    	
	}
	public List<NotifyItem> GetNotifyItems(){
		return allnotifyitems;
	}
	public List<DownloadTaskItem> GetDownloadTasks(){
		return alldownloadtask;
	}
	public void setAccount(String user,String password){
		usr=user;
		pwd=password;
	}

	private static final String boundarystr="!@#$%^&*()";
	public List<NotifyItem> allnotifyitems=new ArrayList<NotifyItem>();
	public Map<String ,NotifyItem> notifyhash=new HashMap<String ,NotifyItem>();
	public List<DownloadTaskItem> alldownloadtask=new ArrayList<DownloadTaskItem>();
	public Map<String ,DownloadTaskItem> downloadtaskhash=new HashMap<String ,DownloadTaskItem>();
	////
	public String username="";
	public int orgid=0;
	public  boolean isinprocess=false;
	public List<String> coursetitle =new ArrayList<String>();
	
	private int logteststatus=-1;
	boolean  initfinished=false;
	private String homepagestr="";
	private String usr="",pwd="";
	
	//colspan="2" class="d_gn" 
		//	class="D2LLink"
//	private String contentmark="<td colspan=\"3\"";//get unit title
	
	
	/*
	private String contenttablemarkstart="<span class=\"d2l-offscreen\">Search</span></a>";
	private String contenttablemarkend="<input type=\"hidden\" name=\"d2l_controlMap\"";
	private String contenttableblank="There is no content to display";
	
	private String gradetablemarkstart="<h2 class=\"dhdg_1\">";
	private String gradetablemarkblank="No items found";
	private String gradetablemarkend="<input type=\"hidden\" name=\"d2l_controlMap\"";*/
	private String gradetielemark="<div class=\"dco_c\"><strong>";
	private String grademarks="<div style=\"text-align:center;display:inline;\">";
	private String contentitemmark="class=\"D2LLink\"";//behind this mark ,skip next <img is item name
	private String contenttablemark="<a class=\"D2LLink\" title=\"";
	private String contenttableid="PreviewTopic(";

	private String updatealertsstart="d2l-minibar-activity-buckethandle alert_bucket_Grades"; 
	private String updatealertsend="/>";
	private String updatealertisvisible="d2l-hidden"; 
	
	
	private static final String host="https://e.centennialcollege.ca/";
	private static String usernamemark="<span class=\"d2l-menuflyout-text\">";
	final private String durl="https://e.centennialcollege.ca/d2l/lms/content/preview.d2l?tId=%d&ou=%d";
	final private String resetupdatealert="https://e.centennialcollege.ca/d2l/MiniBar/%d/ActivityFeed/GetAlerts?Category=1";

	///
	private String newsurl="";
	private String newsmark="d2l/lms/news/main.d2l";
	private String newstartmark="<label><strong>";
	private String newsattachmark="<label>Attachment";
	private String newsidmark="'newsId':'";
	private Map<Integer,Integer> newsidmap= new  HashMap<Integer ,Integer>();
	private String newsitemdetailurl="https://e.centennialcollege.ca/d2l/m/le/news/%d/details/%d";
	private String newsattachmentstart="<h2>Attachments</h2>";
	private String newsattachmentsurl="<a class=\"d2l-itemlist-pad\" href=\"";
	private String newsattachmentsfilename="<span class=\"d2l-itemlist-title\">";
	private String newsattachmentend="</ul>";
	//	private String newendmark="</p></div>";
	private String newsdatemark="<label>";
	private String newscoursemark="d2l/lp/ouHome/home.d2l";
	private String newsbodystartmark="<p>";
	
	/*
	 * <label><strong>Final Course Grades</strong>


<label>Aug 14, 2012 11:55 AM</label>


<a class="d2l-outline" href="/d2l/lp/ouHome/home.d2l?ou=61958">12M --Economic Analysis for Mgrs. (SEC. 101)</a>



<p>Your final exams have been marked and course grades have been calculated. Both are available for you to view on e.centennial. Grades have been rounded upward in your favor when appropriate.</p>
<p>Grades have been OFFICIALLY submitted, and no changes can be made.</p>
<p>Should you wish to view your exam, e-mail me to arrange a time to do so. </p></div>


	 * */		
	
	
	
	
	/////
	public List <CourseInfo> courlist=new ArrayList<CourseInfo>();
	private static String coursemark="d2l/lp/ouHome/home.d2l";
	
	
	private boolean isD2LUpdate(){
		boolean ret=true;
		
		String htmlbody=homepagestr;
		if(htmlbody.length()>0&&htmlbody.indexOf(updatealertsstart)!=-1){//valid
			htmlbody=htmlbody.substring(htmlbody.indexOf(updatealertsstart));
			htmlbody=htmlbody.substring(0,htmlbody.indexOf(updatealertsend));
			int index=htmlbody.indexOf(updatealertisvisible);
			if(htmlbody.indexOf(updatealertisvisible)==-1)
				return false;
		}
		return ret;
	}
	private void removeD2LUnreadMsg(){
		String url=String.format(resetupdatealert, orgid);
		String ret=MainActivity.httpsmgr.GetData(url,"",MainActivity.httpsmgr.GetCookie(),"",0,0);
		return;
	}
	private void LoadLocalFile(){
		
		String temp=this.ReadsSysFile(EService.gContent,"data");
		while(temp.length()>0){
			String item=temp.substring(0,temp.indexOf("@#@@#@"));
			temp=temp.substring(temp.indexOf("@#@@#@")+6);	
				
			NotifyItem pnotify=new NotifyItem(item);//init from str
			allnotifyitems.add(pnotify);
//			if(pnotify.getType()!=NotifyItem.NEWS)
				notifyhash.put(pnotify.GetTitle()+pnotify.GetSubject()+pnotify.GetTypestr(), pnotify);
//			else
//				notifyhash.put(pnotify.GetTitle()+pnotify.GetSubject()+pnotify.GetTypestr(), pnotify);
		}
		
		temp=this.ReadsSysFile(EService.gContent,"download");
		while(temp.length()>0){
			String item=temp.substring(0,temp.indexOf("@#@@#@"));
			temp=temp.substring(temp.indexOf("@#@@#@")+6);
				
			DownloadTaskItem pnotify=new DownloadTaskItem(item);
			alldownloadtask.add(pnotify);
			downloadtaskhash.put(pnotify.m_taskname, pnotify);
		}
		
		
		/*fix point*/
		for(int index=0;index<alldownloadtask.size();index++){
			DownloadTaskItem ptask=alldownloadtask.get(index);
			for(int notifyindex=0;notifyindex<allnotifyitems.size();notifyindex++){
				NotifyItem pnotify=allnotifyitems.get(notifyindex);
				
				if(pnotify.getID()==ptask.m_objid){
					ptask.m_obj=pnotify;
					pnotify.Setdownload(ptask);
					break;
				}
			}
		}
		
		
		temp=this.ReadsSysFile(EService.gContent,"courses");
		this.coursetitle.clear();
		coursetitle.add("All courses");
		while(temp.length()>0){
			String item=temp.substring(0,temp.indexOf(";"));
			temp=temp.substring(temp.indexOf(";")+1);
				
			coursetitle.add(item);
		}
		
	//	InitUpdateSiteURLS();//init cookie,course list
		
	}
	private boolean IsFirstInstall() throws Exception{
		
		File sdDir = null;
		 boolean sdCardExist = Environment.getExternalStorageState()
		 .equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
		if (sdCardExist)
		{
			sdDir = Environment.getExternalStorageDirectory();//获取跟目录
			sdrootpath=sdDir.toString()+"/ecentennial/";
			File file = new File(sdrootpath);
			if (!file.exists()) {
				file.mkdir();
				
			}
		}
		else
			sdrootpath="error";
		
		String datastr=ecentennial.ReadsSysFile(EService.gContent,"data");
		if(datastr.length()>0)
			return false;
		else
			return true;
	}
	public static void WriteSysFile(String filename,String str){
    	try{
    		  
    		   File saveFile=new File(filename);
    		   
    		   FileOutputStream fout=EService.gContent.openFileOutput(filename,EService.MODE_PRIVATE);
    		   fout.write(str.getBytes());
    		   fout.close();
    	}
    	catch(Exception e){

    	}
    	
    }
    public static String ReadsSysFile(Context c,String filename){
    	String ret="";
    	try{
    		  
    		  // File saveFile=new File("/sdcard/"+filename);

    				
    		   FileInputStream fin=c.openFileInput(filename);
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
	public static void WriteDBFile(String filename,String str){
    	try{
    		   File saveFile=new File(filename);
    		   if(saveFile.exists()==false)
    			   saveFile.createNewFile();
    		   
    		   FileOutputStream fout=new FileOutputStream(saveFile);//.o.pmain.openFileOutput(filename,0);
    		   byte[] bytes=str.getBytes();
    		   fout.write(bytes);
    		   fout.close();
    		   
    	}
    	catch(Exception e){
    		String aa=e.toString();
    		Log.e("",aa);
    	}
    	
    }
    public static String ReadsDBFile(String filename){
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
    	catch(Exception e){}
    	return ret;
    }
   
    private void ResortToHead(NotifyItem p){
    	
		for(int i=0;i<allnotifyitems.size();i++){
			if(allnotifyitems.get(i)==p){
				allnotifyitems.remove(i);
				p.setRead(false);
				p.Renew();
				allnotifyitems.add(0,p);
				break;
			}
		}
    	
    }
	private boolean CheckUpdateCourse(boolean isfirstinstall){
		
		boolean ret=false;
		ret=CheckNewsAttachment();
		List<NotifyItem> newlist=new ArrayList<NotifyItem>();
		for(int i=0;i<courlist.size();i++){
			String contentpage=MainActivity.httpsmgr.GetData(courlist.get(i).GetContentUrl(),"",MainActivity.httpsmgr.GetCookie(),"",0,0);
			String gradepage=MainActivity.httpsmgr.GetData(courlist.get(i).GetGradeUrl(),"",MainActivity.httpsmgr.GetCookie(),"",0,0);
	
			while(contentpage.length()>0){
				if(contentpage.indexOf(contenttablemark)!=-1){
					try{
						contentpage=contentpage.substring(contentpage.indexOf(contenttablemark)+contenttablemark.length());
						
						String contentbodystart=contentpage;
						if(contentbodystart.indexOf(contenttablemark)!=-1){
							contentbodystart=contentbodystart.substring(0,contentbodystart.indexOf(contenttablemark));
						}
						
						int subtype=0;
						if(contentbodystart.indexOf("Adobe Acrobat Document")!=-1){
							
							subtype=NotifyItem.pdf;
						}
						else if(contentbodystart.indexOf("Word Document")!=-1){
							
							subtype=NotifyItem.word;
						}
						else if(contentbodystart.indexOf("PowerPoint Presentation")!=-1){
							
							subtype=NotifyItem.ppt;
						}
						else if(contentbodystart.indexOf("Web Page")!=-1){
							
							subtype=NotifyItem.webpage;
						}
						else if(contentbodystart.indexOf("Quicklink")!=-1){
							
							subtype=NotifyItem.quicklink;
						}
						else if(contentbodystart.indexOf("Excel")!=-1){
							
							subtype=NotifyItem.xls;
						}
						else
							subtype=NotifyItem.others;
						
						contentbodystart=contentbodystart.substring(0, contentbodystart.indexOf(">"));
						String title=contentbodystart.substring(0,contentbodystart.indexOf("\""));
						String cid=contentbodystart.substring(contentbodystart.indexOf(contenttableid)+contenttableid.length());
						cid=cid.substring(0,cid.indexOf(","));
						String url=String.format(durl, Integer.parseInt(cid),courlist.get(i).GetCourseID());
						
						title=title.replace("Preview ","");
						title=title.replace(" in a new window","");
						if(title.substring(0,1).compareToIgnoreCase(" ")==0)
							title=title.substring(1);
						
						
						title=Httpmanager.FilterEntities(title);
						
						
						if(isfirstinstall){
							
							NotifyItem pnotify=new NotifyItem(NotifyItem.COURSE,courlist.get(i).GetName(),url,courlist.get(i).GetName(),title,"","",isfirstinstall,false,false);
							pnotify.SetSubType(subtype);
							notifyhash.put(title+courlist.get(i).GetName()+pnotify.GetTypestr(), pnotify);
							allnotifyitems.add(allnotifyitems.size(),pnotify);
							
						
						}
						else if(notifyhash.get(title+courlist.get(i).GetName()+String.valueOf(NotifyItem.COURSE))==null){//new and in updatemode
							ret=true;
							NotifyItem pnotify=new NotifyItem(NotifyItem.COURSE,courlist.get(i).GetName(),url,courlist.get(i).GetName(),title,"","",false,true,false);
							pnotify.SetSubType(subtype);
							notifyhash.put(title+courlist.get(i).GetName()+pnotify.GetTypestr(), pnotify);
							newlist.add(newlist.size(),pnotify);
						}
						
						Thread.sleep(10);
					}
					catch(Exception e){}
				}
				else
					contentpage="";
			}
			
			
			while(gradepage.length()>0){
				if(gradepage.indexOf(gradetielemark)!=-1){
					try{
						gradepage=gradepage.substring(gradepage.indexOf(gradetielemark)+gradetielemark.length());
						
						String bodystart=gradepage;
						String title=bodystart.substring(0, bodystart.indexOf("<"));
						
						if(bodystart.indexOf(gradetielemark)!=-1){
							bodystart=bodystart.substring(0,bodystart.indexOf(gradetielemark));
						}
						
						float num1=0xffffff,num2=0xffffff;
						while(bodystart.length()>0){
							if(bodystart.indexOf(grademarks)!=-1){
								bodystart=bodystart.substring(bodystart.indexOf(grademarks)+grademarks.length());
								String marks=bodystart.substring(bodystart.indexOf(">")+1);
								marks=marks.substring(0,marks.indexOf("<"));
								marks=marks.replace(" ","");
								marks=marks.replace("-","0");
								
								if(marks.indexOf("/")!=-1&&num2>Float.parseFloat(marks.substring(marks.indexOf("/")+1))){
									num1=Float.parseFloat(marks.substring(0,marks.indexOf("/")));
									num2=Float.parseFloat(marks.substring(marks.indexOf("/")+1));
								}
							}
							else
								bodystart="";
						}
					
						if(num2>0&&num2!=0xffffff){//num2 is valid
							if(isfirstinstall){	
								NotifyItem pnotify=new NotifyItem(NotifyItem.GRADE,courlist.get(i).GetName(),courlist.get(i).GetContentUrl(),courlist.get(i).GetName(),title,"",String.format("%.1f/%.1f",num1,num2),isfirstinstall,false,false);
								notifyhash.put(title+courlist.get(i).GetName()+pnotify.GetTypestr(), pnotify);
								allnotifyitems.add(allnotifyitems.size(),pnotify);					
							}
							NotifyItem pold=notifyhash.get(title+courlist.get(i).GetName()+String.valueOf(NotifyItem.GRADE));
							if(pold==null){//new and in updatemode
								ret=true;
								NotifyItem pnotify=new NotifyItem(NotifyItem.GRADE,courlist.get(i).GetName(),courlist.get(i).GetContentUrl(),courlist.get(i).GetName(),title,"",String.format("%.1f/%.1f",num1,num2),false,true,false);
								notifyhash.put(title+courlist.get(i).GetName()+pnotify.GetTypestr(), pnotify);
								newlist.add(newlist.size(),pnotify);
							}
							else if(pold.GetDate().compareToIgnoreCase(String.format("%.1f/%.1f",num1,num2))!=0){
								ret=true;
								pold.SetDate(String.format("%.1f/%.1f",num1,num2));
								ResortToHead(pold);
							}
						}
						
						Thread.sleep(10);
			
					}
					catch(Exception e){}
					
		/*			if(isfirstinstall){
						
						NotifyItem pnotify=new NotifyItem(NotifyItem.GRADE,courlist.get(i).GetName(),url,courlist.get(i).GetName(),title,"","",isfirstinstall,false);
						notifyhash.put(title+courlist.get(i).GetName(), pnotify);
						allnotifyitems.add(allnotifyitems.size(),pnotify);
						
					
					}
					else if(notifyhash.get(title+courlist.get(i).GetName())==null){//new and in updatemode
						ret=true;
						NotifyItem pnotify=new NotifyItem(NotifyItem.GRADE,courlist.get(i).GetName(),url,courlist.get(i).GetName(),title,"","",false,true);
						notifyhash.put(title+courlist.get(i).GetName(), pnotify);
						newlist.add(newlist.size(),pnotify);
					}*/
				}
				else
					gradepage="";
			}
			
			
			/*
			contentpage=contentpage.substring(contentpage.indexOf(contenttablemarkstart)+contenttablemarkstart.length());
			contentpage=contentpage.substring(0,contentpage.indexOf(contenttablemarkend));
			
			gradepage=gradepage.substring(gradepage.indexOf(gradetablemarkstart)+gradetablemarkstart.length());
			gradepage=gradepage.substring(0,gradepage.indexOf(gradetablemarkend));
			
			if(!isfirstinstall){//update mode
				//Read old
				String savestr=ReadsDBFile(courlist.get(i).GetDir()+"data");
				if(savestr.length()>0){
					String cachecontentpage=savestr.substring(0, savestr.indexOf(boundarystr));
					String cachegradepage=savestr.substring(savestr.indexOf(boundarystr)+boundarystr.length());
				
					//String name,String url,String from,String title,String body,String date)
					if(contentpage.indexOf(contenttableblank)==-1&&cachecontentpage.compareToIgnoreCase(contentpage)!=0){
						ret=true;
						NotifyItem pnotify=notifyhash.get(courlist.get(i).GetName()+String.valueOf(NotifyItem.COURSE));
					//	
						if(pnotify!=null){
							
							ResortToHead(pnotify);
						}
						else{
				
							pnotify=new NotifyItem(NotifyItem.COURSE,courlist.get(i).GetName(),courlist.get(i).GetContentUrl(),"","Content Updated","","",false,true);
							allnotifyitems.add(0,pnotify);
							notifyhash.put(courlist.get(i).GetName()+"0", pnotify);
	
						}
					}
					if(gradepage.indexOf(gradetablemarkblank)==-1&&cachegradepage.compareToIgnoreCase(gradepage)!=0){
						ret=true;
						NotifyItem pnotify=notifyhash.get(courlist.get(i).GetName()+String.valueOf(NotifyItem.GRADE));
						if(pnotify!=null){
							
							ResortToHead(pnotify);
						}
						else{
							pnotify=new NotifyItem(NotifyItem.GRADE,courlist.get(i).GetName(),courlist.get(i).GetGradeUrl(),"","Grade Updated","","",false,true);
							allnotifyitems.add(0,pnotify);
							notifyhash.put(courlist.get(i).GetName()+"1", pnotify);
						}
					}
				}
			}
			String savestr=contentpage+boundarystr+gradepage;
			WriteDBFile(courlist.get(i).GetDir()+"data",savestr);*/
			if(MainActivity.myHandler!=null){
				
				Message handlemsg = MainActivity.myHandler.obtainMessage();  
				handlemsg.obj = "7:";  
		        MainActivity.myHandler.sendMessage(handlemsg);  
			}
			
		}
		
		if(ret)
			allnotifyitems.addAll(0, newlist);
		return ret;
	}
	
	private void BuildNewsIDTable(String html){
		try{
			newsidmap.clear();
			int index=0;
			while(html.length()>0){
				if(html.indexOf(newsidmark)!=-1){
					html=html.substring(html.indexOf(newsidmark)+newsidmark.length());
					String id=html;
					
					if(id.indexOf(newsidmark)!=-1){
						id=id.substring(0,id.indexOf(newsidmark));	
					}
					id=id.substring(0,id.indexOf("'"));
					newsidmap.put(index, Integer.parseInt(id));
					
					index++;
				}
				else
					html="";
			}
		}
		catch(Exception e){}
	}
	private long GetNewsID(int index){
		if(newsidmap.size()==0)
			return System.currentTimeMillis();
		
		return newsidmap.get(index);
	}
	private boolean CheckNewsAttachment(){
		/*
		 * 	private String newsitemdetailurl="https://e.centennialcollege.ca/d2l/m/le/news/%d/details/%d";
	private String newsattachmentstart="<h2>Attachments</h2>";
	private String newsattachmentsurl="<a class=\"d2l-itemlist-pad\" href=\"";
	private String newsattachmentsfilename="<span class=\"d2l-itemlist-title\">";
	private String newsattachmentend="</ul>";
		 * */
		List<NotifyItem> newlist=new ArrayList<NotifyItem>();
		boolean ret=false;
		
		for(int notifyindex=0;notifyindex<allnotifyitems.size();notifyindex++){
			NotifyItem pnotify=allnotifyitems.get(notifyindex);
			
			if(pnotify.isAttachment()&&pnotify.getAttachmentNames().length()==0){
				try{
					String newsdetailsurl=String.format(newsitemdetailurl,orgid,pnotify.getID());
					String html=MainActivity.httpsmgr.GetData(newsdetailsurl,"",MainActivity.httpsmgr.GetCookie(),"",0,0);
					html=html.substring(html.indexOf(newsattachmentstart)+newsattachmentstart.length());
					html=html.substring(0,html.indexOf(newsattachmentend));
					
					String attachmentnames="";
					while(html.length()>0){
						if(html.indexOf(newsattachmentsurl)!=-1){
							try{
								html=html.substring(html.indexOf(newsattachmentsurl)+newsattachmentsurl.length());
								String url=html;
								
								if(url.indexOf(newsattachmentsurl)!=-1){
									url=url.substring(0,url.indexOf(newsattachmentsurl));	
								}
								String attachmenturl=url.substring(0,url.indexOf("\""));
								String attachmentname=url.substring(url.indexOf(newsattachmentsfilename)+newsattachmentsfilename.length());
								attachmentname=attachmentname.substring(0,attachmentname.indexOf("<"));
								
								if(pnotify.isNew())
									ret=true;
								
								if(attachmenturl.indexOf("http")!=0){
									attachmenturl=host+attachmenturl;
								}
								
								int subtype=NotifyItem.others;
								if(attachmentname.indexOf(".pdf")!=-1){
									subtype=NotifyItem.newsattachmentpdf;
									
								}
								else if(attachmentname.indexOf(".ppt")!=-1){
									subtype=NotifyItem.newsattachmentppt;
										
								}
								else if(attachmentname.indexOf(".doc")!=-1){
									subtype=NotifyItem.newsattachmentword;
								}
								else if(attachmentname.indexOf(".xls")!=-1){
									subtype=NotifyItem.newsattachmentxls;
								}
								
								NotifyItem pnotifyitem=new NotifyItem(NotifyItem.COURSE,pnotify.GetSubject(),attachmenturl,pnotify.GetSubject(),attachmentname,"","",!pnotify.isNew(),pnotify.isNew(),false);
								pnotifyitem.SetSubType(subtype);
								notifyhash.put(attachmentname+pnotify.GetSubject()+pnotify.GetTypestr(), pnotifyitem);
								
								
								newlist.add(newlist.size(),pnotifyitem);
						//		allnotifyitems.add(pnotify.isNew()?0:allnotifyitems.size(),pnotifyitem);
											
								attachmentnames+=attachmentname+";";
								
								
								
								Thread.sleep(10);
							}
							catch(Exception e){}
						}
						else
							html="";
					}
					
					pnotify.setAttachmentNames(attachmentnames);
				}
				catch(Exception e){}
			}
			if(MainActivity.myHandler!=null){
				
				Message handlemsg = MainActivity.myHandler.obtainMessage();  
				handlemsg.obj = "7:";  
		        MainActivity.myHandler.sendMessage(handlemsg);  
			}
		}
		allnotifyitems.addAll(0, newlist);
		return ret;
	}
	private boolean CheckUpdateNews(boolean isfirstinstall){
		boolean ret=false;
		List<NotifyItem> newlist=new ArrayList<NotifyItem>();
		String temp=MainActivity.httpsmgr.GetData(newsurl,"",MainActivity.httpsmgr.GetCookie(),"",0,0);
		temp=Httpmanager.FilterEntities(temp);
		BuildNewsIDTable(temp);
		int newsindex=0;
		while(temp.length()>0){
			if(temp.indexOf(newstartmark)!=-1){
				try{
					temp=temp.substring(temp.indexOf(newstartmark)+newstartmark.length());
					String newbody=temp;
					
					
					if(newbody.indexOf(newstartmark)!=-1){
						newbody=newbody.substring(0,newbody.indexOf(newstartmark));
					}
	
					boolean isattachment=false;
					if(newbody.indexOf(newsattachmark)!=-1)
						isattachment=true;
					
					String newstitle="",newssubject="",newssubjecturl="",newsdate="",newstxt="";
					
					newstitle=newbody.substring(0,newbody.indexOf("<"));
					newsdate=newbody.substring(newbody.indexOf(newsdatemark)+newsdatemark.length());
					newssubjecturl=newbody.substring(newbody.indexOf(newscoursemark));
					newstxt=Httpmanager.FilterHtmlMark(newbody.substring(newbody.indexOf(newsbodystartmark)));
					
					newsdate=newsdate.substring(0,newsdate.indexOf("<"));
					newssubject=newssubjecturl.substring(newssubjecturl.indexOf(">")+1 );
					newssubject=newssubject.substring(0,newssubject.indexOf("<"));
					newssubjecturl=host+newssubjecturl.substring(0,newssubjecturl.indexOf("\""));
	
				//	newssubject=Httpmanager.FilterEntities(newssubject);
				//	newstxt=Httpmanager.FilterEntities(newstxt);
					
	//				String name,String url,String from,String title,String body,String date)
					if(isfirstinstall){
						NotifyItem pnotify=new NotifyItem(NotifyItem.NEWS,newssubject,newssubjecturl,newssubject,newstitle,newstxt,newsdate,isfirstinstall,false,isattachment);
						pnotify.setID(GetNewsID(newsindex));
						notifyhash.put(newstitle+pnotify.GetSubject()+pnotify.GetTypestr(), pnotify);
						allnotifyitems.add(allnotifyitems.size(),pnotify);
						
					}
					else if(notifyhash.get(newstitle+newssubject+String.valueOf(NotifyItem.NEWS))==null){//new and in updatemode
						ret=true;
						NotifyItem pnotify=new NotifyItem(NotifyItem.NEWS,newssubject,newssubjecturl,newssubject,newstitle,newstxt,newsdate,false,true,isattachment);
						pnotify.setID(GetNewsID(newsindex));
						notifyhash.put(newstitle+pnotify.GetSubject()+pnotify.GetTypestr(), pnotify);
						newlist.add(newlist.size(),pnotify);
						
						
					}
					
					
					newsindex++;
					Thread.sleep(10);
				}
				catch(Exception e){}
				}
			else{
				temp="";
			}	
		}
		if(ret)
			allnotifyitems.addAll(0, newlist);
		
		if(MainActivity.myHandler!=null){
			
			Message handlemsg = MainActivity.myHandler.obtainMessage();  
			handlemsg.obj = "5:";  
	        MainActivity.myHandler.sendMessage(handlemsg);  
		}
		
		return ret;
	}
	private String Connect(){
		if(usr.length()==0||pwd.length()==0)
			return "";
		
		if(MainActivity.httpsmgr==null)
			MainActivity.httpsmgr= new Httpmanager();
		// TODO Auto-generated method stub
		MainActivity.httpsmgr.ClearCookie();
		return MainActivity.httpsmgr.GetData("https://e.centennialcollege.ca/d2l/lp/auth/login/login.d2l","","","Username="+usr+"&Password="+pwd,0,0);
	}
	public int Logintest(String usrname,String password){//0 ok,1 account error,2	network error
		setAccount(usrname,password);
		homepagestr=Connect();
		if(homepagestr.length()==0)
		{
			logteststatus=2;
			return logteststatus;
		}
		else if(homepagestr.indexOf("</html>")==-1){
			logteststatus=2;
			return logteststatus;
		}
		if(homepagestr.indexOf("Loggin")!=-1)
		{
			logteststatus=1;
		}
		else if(homepagestr.indexOf(coursemark)!=-1)
			logteststatus=0;
		else
			logteststatus=3;

		return logteststatus;
	}
	public int GetLogintest(){
		
		return logteststatus;
	}
	private int InitUpdateSiteURLS(){
		String htmlbody=homepagestr;
		if(htmlbody.length()>0&&htmlbody.indexOf(usernamemark)!=-1){//valid
			
			//get profile name
			username=htmlbody.substring(htmlbody.indexOf(usernamemark)+usernamemark.length());
			username=username.substring(0,username.indexOf("<"));
			
			
			if(MainActivity.myHandler!=null){
				
				Message handlemsg = MainActivity.myHandler.obtainMessage();  
				handlemsg.obj = "10:";  
		        MainActivity.myHandler.sendMessage(handlemsg);  
			}
			
			//get news address
			newsurl=htmlbody.substring(htmlbody.indexOf(newsmark));
			newsurl=newsurl.substring(0,newsurl.indexOf("\""));
			if(newsurl.indexOf("http")==-1)
				newsurl=host+newsurl;
			
			orgid=Integer.parseInt(newsurl.substring(newsurl.indexOf("ou=")+3));
//			String newsinfo=MainActivity.httpsmgr.GetData(newsurl,MainActivity.httpsmgr.GetCookie(),"");
					
			//get coruse info
			String temp=htmlbody.substring(htmlbody.indexOf("My Courses")+1);
			String cousenamebk="";
			int maxcoursetimestamp=0;
			this.coursetitle.clear();
			coursetitle.add("All courses");
			while(temp.length()>0){
				if(temp.indexOf(coursemark)!=-1){
					String url=temp.substring(temp.indexOf(coursemark)).substring(0, temp.substring(temp.indexOf(coursemark)).indexOf("\""));
					String name=temp.substring(temp.indexOf(coursemark)).substring(temp.substring(temp.indexOf(coursemark)).indexOf(">")+1);
					name=name.substring(0, name.indexOf("<"));
					temp=temp.substring(temp.indexOf(coursemark)+1);
					name=Httpmanager.FilterEntities(name);
					
					
					if(url.length()>0){
						CourseInfo pitem=new CourseInfo(name,"https://e.centennialcollege.ca/"+url);
						courlist.add(pitem);
						coursetitle.add(pitem.GetName());
						cousenamebk+=pitem.GetName()+";";
					}
			//		if(pitem.GetCourseTimeStamp()>maxcoursetimestamp)
			//			maxcoursetimestamp=pitem.GetCourseTimeStamp();
				}
				else{
					temp="";
				}	
			}
			
			WriteSysFile("courses",cousenamebk);
		//	for(int i=courlist.size()-1;i>=0;i--){
		//		if(courlist.get(i).GetCourseTimeStamp()<maxcoursetimestamp)
		//			courlist.remove(i);
		//	}
			
			
			return 0;
		}
		return htmlbody.length()>0?-1:-2;//-2 networkerror,-1 pwd usr error
	}
}
