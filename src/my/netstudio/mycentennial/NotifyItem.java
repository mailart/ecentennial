package my.netstudio.mycentennial;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.view.View;

public class NotifyItem{
	public static final int NEWS =0;
	public static final int MAIL =1;
	public static final int COURSE =2;
	public static final int GRADE =3;
	public static final int DOWNLOAD =4;
	
	public static final int pdf =0;
	public static final int word =1;
	public static final int ppt =2;
	public static final int webpage =3;
	public static final int quicklink =4;
	public static final int xls =5;
	public static final int others =6;
	
	public static final int newsattachmentpdf =7;
	public static final int newsattachmentword =8;
	public static final int newsattachmentppt =9;
	public static final int newsattachmentxls =10;
	
	NotifyItem(int type,String couse_name,String url,String from,String title,String body,String date,boolean readed,boolean isnew,boolean attachment){
		
		notifyid=System.currentTimeMillis();
		notifytype=type;
		coursename=couse_name;
		courseurl=url;	
		
		notifyfrom=from;
		notifytitle=title;
		notifybody=body;
		
		notifydate=date;
		
		isreaded=readed;
		isnewinsert=isnew;
		
		isattachmented=attachment;
		if(notifydate.length()==0){
			
			notifydate=GetInsertDate();
		}
	}
	public String GetTypestr(){
		
		return String.valueOf(notifytype);
	}
	public String toString(){
		return String.valueOf(notifyid)+"^**$"+coursename+"^**$"+courseurl+"^**$"+notifyfrom
				+"^**$"+notifytitle+"^**$"+notifybody+"^**$"+notifydate
				+"^**$"+attachmentidlist+"^**$"+String.valueOf(isreaded)
				+"^**$"+String.valueOf(isnewinsert)
				+"^**$"+String.valueOf(isattachmented)
				+"^**$"+String.valueOf(notifytype)+"^**$"+String.valueOf(subicontype);
	}
	NotifyItem(String storestr){
		
		
		notifyid=Long.parseLong((String) storestr.substring(0,storestr.indexOf("^**$")));
		storestr=storestr.substring(storestr.indexOf("^**$")+4);
		
		coursename=(String) storestr.substring(0,storestr.indexOf("^**$"));
		storestr=storestr.substring(storestr.indexOf("^**$")+4);
		
		courseurl=(String) storestr.substring(0,storestr.indexOf("^**$"));
		storestr=storestr.substring(storestr.indexOf("^**$")+4);
		
		notifyfrom=(String) storestr.substring(0,storestr.indexOf("^**$"));
		storestr=storestr.substring(storestr.indexOf("^**$")+4);
		
		notifytitle=(String) storestr.substring(0,storestr.indexOf("^**$"));
		storestr=storestr.substring(storestr.indexOf("^**$")+4);
		
		notifybody=(String) storestr.substring(0,storestr.indexOf("^**$"));
		storestr=storestr.substring(storestr.indexOf("^**$")+4);

		
		notifydate=(String) storestr.substring(0,storestr.indexOf("^**$"));
		storestr=storestr.substring(storestr.indexOf("^**$")+4);
		
		attachmentidlist=(String) storestr.substring(0,storestr.indexOf("^**$"));
		storestr=storestr.substring(storestr.indexOf("^**$")+4);
		
		isreaded=Boolean.parseBoolean((String) storestr.substring(0,storestr.indexOf("^**$")));
		storestr=storestr.substring(storestr.indexOf("^**$")+4);;
		
		isnewinsert=Boolean.parseBoolean((String) storestr.substring(0,storestr.indexOf("^**$")));
		storestr=storestr.substring(storestr.indexOf("^**$")+4);;
		
		isattachmented=Boolean.parseBoolean((String) storestr.substring(0,storestr.indexOf("^**$")));
		storestr=storestr.substring(storestr.indexOf("^**$")+4);;
		
		notifytype=Integer.parseInt((String) storestr.substring(0, storestr.indexOf("^**$")));
		storestr=storestr.substring(storestr.indexOf("^**$")+4);
		
		subicontype=Integer.parseInt((String) storestr);
		
//		isnewinsert=Boolean.parseBoolean((String) "true");;
	}
	public void setRead(boolean isread){
		isreaded=isread;
		
		//update local file io
	}
	public void setOld(){
		isnewinsert=false;
		
		//update local file io
	}
	public boolean getRead(){
		return isreaded;
		
		//update local file io
	}
	public long getID(){
		return notifyid;
		//update local file io
	}
	public void setID(long id){
		notifyid=id;
		//update local file io
	}
	public int getType(){
		return notifytype;
		
		//update local file io
	}
	public boolean isNew(){
		return isnewinsert;
		
		//update local file io
	}
	public boolean isAttachment(){
		return isattachmented;
		
		//update local file io
	}
	public String getAttachmentNames(){
		return attachmentidlist;
		
		//update local file io
	}
	public void setAttachmentNames(String names){
		attachmentidlist=names;
		
		//update local file io
	}
	
	public void Renew(){
		isnewinsert=true;
		isreaded=false;
		notifydate=GetDate();
	}
	public String GetTitle(){
		
		return notifytitle;
	}
	public String GetDate(){
		
		return notifydate;
	}
	public void SetDate(String str){
		
		notifydate=str;;
	}
	public String GetMsg(){
		
		return notifybody;
	}
	public String GetSubject(){
		
		return coursename;
	}
	private String GetInsertDate(){
		Date now=new Date();
		 SimpleDateFormat f=new SimpleDateFormat("MM-dd-yyyy HH:mm");
		return  f.format(now);
		
	}
	public void SetView(View vi){
		
		p_view=vi;
	}
	public View GetView(){
		
		return p_view;
	}
	public void SetSubType(int type){
		
		subicontype=type;
	}
	public String GetUrl(){
		return courseurl;
	}
	public int GetSubType(){
		
		return subicontype;
	}
	public void Setdownload(DownloadTaskItem pitem){
		
		pdownload=pitem;
	}
	public DownloadTaskItem Getdownload(){
		
		return pdownload;
	}
	
	private DownloadTaskItem pdownload=null;
	private long notifyid=0;
	public int notifytype=0;
	private String attachmentidlist="";
	private boolean isattachmented=false;
	private String coursename="";
	private String courseurl="";	
	private String notifyfrom="";
	private String notifytitle="";
	private String notifybody="";
	private String notifydate="";
	private boolean isreaded=false;
	private boolean isnewinsert=false;
	private View p_view=null;
	private int subicontype=0;
};
