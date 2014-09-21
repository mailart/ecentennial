package my.netstudio.mycentennial;

import android.os.Handler;
import android.view.View;

public class DownloadTaskItem {
	 public static int connecterror=1;
	 public static int filenotfound=2;
	 public static int httperror=3;
	 public static int diskfull=4;
	 public static int formaterror=5;
	
	 public static int newsattachments=6;
	 public static int contentfile=7;
	
	public String m_taskname="";
	public String m_url="";
	public String m_cookie="";
	public String m_postparam="";
	public String m_savepath="";
	public String m_filename="";
	public Handler m_callback=null;
	public Object m_obj;
	
	public long m_objid=0;
	public int m_totlebytes=0;
	public int m_lastnetworkstatus=0;
	public int m_receivebytes=0;
	public int m_filefrom=0;
	public boolean iscompelte=false;
	public boolean ispause=true;

	private View p_view=null;
	
	public void SetView(View vi){
		
		p_view=vi;
	}
	public View GetView(){
		
		return p_view;
	}
	DownloadTaskItem(String taskname,String url,String cookie,String postparam,String rootpath,String filename,long objid,Object obj,Handler callback,int from){
		m_url=url;
		m_cookie=cookie;
		m_postparam=postparam;
		m_callback=callback;
		m_savepath=rootpath;
		m_filename=filename;
		m_obj=obj;
		m_objid=objid;
		m_taskname=taskname;
		m_filefrom=from;
	}
	DownloadTaskItem(String storestr){
		m_taskname=(String) storestr.substring(0,storestr.indexOf("^**$"));
		storestr=storestr.substring(storestr.indexOf("^**$")+4);
		
		m_url=(String) storestr.substring(0,storestr.indexOf("^**$"));
		storestr=storestr.substring(storestr.indexOf("^**$")+4);
		
		m_postparam=(String) storestr.substring(0,storestr.indexOf("^**$"));
		storestr=storestr.substring(storestr.indexOf("^**$")+4);
		
		m_savepath=(String) storestr.substring(0,storestr.indexOf("^**$"));
		storestr=storestr.substring(storestr.indexOf("^**$")+4);
		
		m_filename=(String) storestr.substring(0,storestr.indexOf("^**$"));
		storestr=storestr.substring(storestr.indexOf("^**$")+4);
		
		m_filefrom=Integer.parseInt((String) storestr.substring(0,storestr.indexOf("^**$")));
		storestr=storestr.substring(storestr.indexOf("^**$")+4);
		
		m_totlebytes=Integer.parseInt((String) storestr.substring(0,storestr.indexOf("^**$")));
		storestr=storestr.substring(storestr.indexOf("^**$")+4);
		
		m_lastnetworkstatus=Integer.parseInt((String) storestr.substring(0,storestr.indexOf("^**$")));
		storestr=storestr.substring(storestr.indexOf("^**$")+4);
		
		m_receivebytes=Integer.parseInt((String) storestr.substring(0,storestr.indexOf("^**$")));
		storestr=storestr.substring(storestr.indexOf("^**$")+4);
		
		m_objid=Long.parseLong((String) storestr.substring(0,storestr.indexOf("^**$")));
		storestr=storestr.substring(storestr.indexOf("^**$")+4);
		
		iscompelte=Boolean.parseBoolean((String) storestr);
	
		m_receivebytes=0;
	}
	public String toString(){
		return m_taskname+"^**$"+m_url+"^**$"+m_postparam+"^**$"+m_savepath+"^**$"+m_filename+"^**$"+String.valueOf(m_filefrom)+"^**$"+String.valueOf(m_totlebytes)
				+"^**$"+String.valueOf(m_lastnetworkstatus)
				+"^**$"+String.valueOf(m_receivebytes)+"^**$"+String.valueOf(m_objid)+"^**$"+String.valueOf(iscompelte);
	}
}
