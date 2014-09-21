package my.netstudio.mycentennial;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Message;
import android.widget.Toast;

public class WebPrinterMgr {

	public String m_usr,m_pwd;
	public String m_cookie="", m_error="";
	public float m_cost=0;
	public int m_sheets;
	public float m_balance=0;
	public boolean isremember=false,ismoneyrunout=false;
	public static final int  WEBPRINT_CENTENNIAL=	0;
	Httpmanager mgr=null;
	WebPrinterMgr(){
		m_balance=0;
		mgr=new Httpmanager();
		isremember=false;
		ismoneyrunout=false;
	}

	
	private void FireMsg(String msg){
	
	}
	public boolean Print(String printname,String filepath,int copies,int type){
		//PR-LIB-FLOOR2
		//PR-ITD-P1
		ismoneyrunout=false;
		Message msgpackage = Webprinter.myHandler.obtainMessage();  
		msgpackage.obj = Webprinter.pthis.getString(R.string.print5) ;
        Webprinter.myHandler.sendMessage(msgpackage); 
        
        String banlancestr=String.format("0:Balance: $%.2f", this.m_balance);
        msgpackage = Webprinter.myHandler.obtainMessage();  
		msgpackage.obj = banlancestr;
        Webprinter.myHandler.sendMessage(msgpackage); 
        
        
		switch(type){
			case WEBPRINT_CENTENNIAL:{
				String cookie=mgr.GetCookie();
				
				String poststr=(""),ret=("");


				/*find print*/
				ret=mgr.GetData("http://pracad01.cencol.ca/app?service=action/1/UserWebPrint/0/$ActionLink","http://http://pracad01.cencol.ca/:9191/app?service=page/UserWebPrint",cookie,poststr,0,9191);//,
				poststr="service=direct/1/UserWebPrintSelectPrinter/$Form&sp=S0&Form0=$Hidden,$Hidden$0,$TextField,$Submit,$RadioGroup,$Submit$0,$Submit$1&$Hidden=&$Hidden$0=&$TextField=%%PRINT%%&$Submit=Find+Printer";
				poststr=poststr.replace(("%%PRINT%%"),printname);
				cookie=m_cookie;
				ret=mgr.GetData("http://pracad01.cencol.ca/app","http://pracad01.cencol.ca/:9191/app?service=action/1/UserWebPrint/0/$ActionLink",cookie,poststr,0,9191);//(),

				if(ret.length()==0){
					msgpackage = Webprinter.myHandler.obtainMessage();  
					msgpackage.obj = "1:Error:"+mgr.GetError();
			        Webprinter.myHandler.sendMessage(msgpackage); 
			        return false;
				}
				
				if(ret.indexOf(("Location/Department"))==-1){
					msgpackage = Webprinter.myHandler.obtainMessage();  
					msgpackage.obj = Webprinter.pthis.getString(R.string.print6) ;
			        Webprinter.myHandler.sendMessage(msgpackage); 
			        
					m_error=("Format error, Update please");
					return false;
				}

				ret=ret.substring(ret.indexOf(("Location/Department"))+1);

				if(ret.indexOf(printname)==-1){
					msgpackage = Webprinter.myHandler.obtainMessage();  
					msgpackage.obj = Webprinter.pthis.getString(R.string.print7) ;
			        Webprinter.myHandler.sendMessage(msgpackage); 
			        
					m_error=("Can not find print on webprinter's list, please select another one and try again");
					return false;
				}

				
				/*choose print*/
				//if(m_hwnd)
				//	::SendMessage (m_hwnd,WM_PROGRESS,(DWORD)"Selecting Printer...",NULL);


				cookie=m_cookie;
				poststr=("service=direct/1/UserWebPrintSelectPrinter/$Form&sp=S0&Form0=$Hidden,$Hidden$0,$TextField,$Submit,$RadioGroup,$Submit$0,$Submit$1&$Hidden=&$Hidden$0=&$TextField=%%PRINTNAME%%&$RadioGroup=0&$Submit$1=2.+%E6%89%93%E5%8D%B0%E9%80%89%E9%A1%B9%E5%8F%8A%E5%B8%90%E6%88%B7%E9%80%89%E6%8B%A9+%3E%3E");
				poststr=poststr.replace(("%%PRINTNAME%%"),printname);
				ret=mgr.GetData(("http://pracad01.cencol.ca/app"),"http://pracad01.cencol.ca/:9191/app?service=action/1/UserWebPrint/0/$ActionLink",cookie,poststr,0,9191);//,()


				/*confirm copies*/
			//	if(m_hwnd)
			//		::SendMessage (m_hwnd,WM_PROGRESS,(DWORD)"Setting Printer...",NULL);

				if(ret.length()==0){
					msgpackage = Webprinter.myHandler.obtainMessage();  
					msgpackage.obj = "1:Error:"+mgr.GetError();
			        Webprinter.myHandler.sendMessage(msgpackage); 
			        return false;
				}


				poststr=("service=direct/1/UserWebPrintOptionsAndAccountSelection/$Form&sp=S0&Form0=copies,$Submit,$Submit$0&copies=%%NUMOFCOPY%%&$Submit=3.+%E4%B8%8A%E4%BC%A0%E6%89%93%E5%8D%B0%E6%96%87%E4%BB%B6+%3E%3E");
				String temp=String.valueOf(copies).toString();
				poststr=poststr.replace(("%%NUMOFCOPY%%"),temp);
				cookie=m_cookie;
				ret=mgr.GetData(("http://pracad01.cencol.ca/app"),"http://pracad01.cencol.ca/:9191/app",cookie,poststr,0,9191);//,()
				
				if(ret.length()==0){
					msgpackage = Webprinter.myHandler.obtainMessage();  
					msgpackage.obj = "1:Error:"+mgr.GetError();
			        Webprinter.myHandler.sendMessage(msgpackage); 
			        return false;
				}
				
				if(ret.indexOf(("uploadFormSubmitURL = '"))==-1){
					msgpackage = Webprinter.myHandler.obtainMessage();  
					msgpackage.obj = Webprinter.pthis.getString(R.string.print6) ;
			        Webprinter.myHandler.sendMessage(msgpackage); 
					m_error=("Can not find Cmd, Update please");
					return false;
				}

				msgpackage = Webprinter.myHandler.obtainMessage();  
				msgpackage.obj = Webprinter.pthis.getString(R.string.print8) ;
		        Webprinter.myHandler.sendMessage(msgpackage); 
		        
				/*upload*/
		//		if(m_hwnd)
		//			::SendMessage (m_hwnd,WM_PROGRESS,(DWORD)"Uploading Data...",NULL);

				String tempstr="uploadFormSubmitURL = '";
				String posturl=ret.substring(ret.indexOf(tempstr)+tempstr.length());
				posturl=posturl.substring(0,posturl.indexOf("'"));
				
				ArrayList<BasicNameValuePair> postParams=new ArrayList<BasicNameValuePair>();
				postParams.add(new BasicNameValuePair("service","direct/1/UserWebPrintUpload/$Form"));
				postParams.add(new BasicNameValuePair("sp","S0"));
				postParams.add(new BasicNameValuePair("Form0","$Submit,$Submit$0"));
				postParams.add(new BasicNameValuePair("$Submit$0","Upload & Complete ..."));
				cookie=m_cookie;
				ret=mgr.Upload("http://pracad01.cencol.ca"+posturl,"file", filepath, postParams, 0, 9191);//,("http://http://pracad01.cencol.ca/:9191/app")
		
				cookie=m_cookie;
				poststr="service=direct/1/UserWebPrintUpload/$Form$0&sp=S1&Form1=";
				ret=mgr.GetData("http://pracad01.cencol.ca/app","http://pracad01.cencol.ca/:9191/app",cookie,poststr,0,9191);//(),"",
				
				if(ret.length()==0){
					msgpackage = Webprinter.myHandler.obtainMessage();  
					msgpackage.obj = "1:Error:"+mgr.GetError();
			        Webprinter.myHandler.sendMessage(msgpackage); 
			        return false;
				}
				
				/*get status*/

				msgpackage = Webprinter.myHandler.obtainMessage();  
				msgpackage.obj = Webprinter.pthis.getString(R.string.print9) ;
		        Webprinter.myHandler.sendMessage(msgpackage); 
			//	if(m_hwnd)
			//		::SendMessage (m_hwnd,WM_PROGRESS,(DWORD)("Checking Status..."),NULL);

				if(ret.indexOf("var webPrintUID")==-1)
				{
					msgpackage = Webprinter.myHandler.obtainMessage();  
					msgpackage.obj = Webprinter.pthis.getString(R.string.print10) ;
			        Webprinter.myHandler.sendMessage(msgpackage); 
					m_error=("Can not find Status Check URL, Update please");
					return false;
				}
				ret=ret.substring(ret.indexOf("var webPrintUID")+1);
				ret=ret.substring(ret.indexOf("'")+1);
				ret=ret.substring(0,ret.indexOf("'"));
				String jsoncheck=ret;

				/*
				{"status":{"code":"rendering","complete":false,"text":"Rendering","messages":[{"info":"Queued in position 1."},{"info":"Starting rendering process."},{"info":"Preparing job for rendering."},{"info":"Rendering job ..."}],"formatted":"<span class=\"info\">Rendering job ...</span>"},"documentName":"2323322.pdf","printer":"pracad01\\PR-LIB-FLOOR2"}

				{"status":{"code":"rendering","complete":false,"text":"Rendering","messages":[{"info":"Queued in position 1."},{"info":"Starting rendering process."},{"info":"Preparing job for rendering."},{"info":"Rendering job ..."},{"info":"Rendering successful.  Preparing job for processing."}],"formatted":"<span class=\"info\">Rendering successful.  Preparing job for processing.</span>"},"documentName":"2323322.pdf","printer":"pracad01\\PR-LIB-FLOOR2"}

				{"status":{"code":"queued","complete":true,"text":"Finished: Queued for printing","formatted":"Finished: Queued for printing"},"documentName":"2323322.pdf","printer":"pracad01\\PR-LIB-FLOOR2","pages":1,"cost":"$0.06"}


				*/

				int count=0;
				while(count<90){
					
					ret=mgr.GetData("http://pracad01.cencol.ca/rpc/web-print/job-status/"+jsoncheck+".json","http://pracad01.cencol.ca/:9191/app",cookie,"",0,9191);//(),"",

					if(ret.length()==0){
						msgpackage = Webprinter.myHandler.obtainMessage();  
						msgpackage.obj = "1:Error:"+mgr.GetError();
				        Webprinter.myHandler.sendMessage(msgpackage); 
				        return false;
					}
					

					if(ret.indexOf (("\"formatted\":\""))!=-1){
						tempstr="\"formatted\":\"";
						String param=ret.substring (ret.indexOf (tempstr)+tempstr.length());
						
						boolean isfinish=false;
						//"pages":1,"cost":"$0.06"
						if(param.indexOf (("\"pages\":"))!=-1&&param.indexOf (("\"cost\":"))!=-1){
							m_sheets=0;
							m_cost=0;
							String text=param.substring (param.indexOf (("\"pages\":")));
		//					sscanf(text,("\"pages\":%d,\"cost\":\"$%f\""),&m_sheets,&m_cost);
						//"pages":1,"cost":"$0.06"}
							
							text=text.replace("\"","");
							String dividetext=text.substring(text.indexOf(":")+1);
							dividetext=dividetext.substring(0,dividetext.indexOf(","));
							m_sheets=Integer.valueOf(dividetext);
							text=text.substring(text.indexOf("$")+1);
							text=text.substring(0,text.indexOf("}"));
							m_cost=Float.valueOf(text);
							
							isfinish=true;
							float eachpagescost=((float)m_cost/(float)m_sheets);
							if(eachpagescost>0.07){
								String msg="";
								msg.format (("%s%.2f%s"),("The standard cost of each page is $0.06, you were charged $"),eachpagescost,(" each, it usually casue by an error of college's webprint system. Please contact helpdesk of College, check your account or return your money"));
								FireMsg(msg);
							}
							else if(m_cost>m_balance){
								ismoneyrunout=true;
								m_error.format (("This document needs $%.2f(%d pages), Your balance is $%.2f"),m_cost,m_sheets,m_balance);;
								msgpackage = Webprinter.myHandler.obtainMessage();  
								msgpackage.obj = "2:"+m_error;
								Webprinter.myHandler.sendMessage(msgpackage); 
								return false;
							}
						}
						
						
						if(param.indexOf ((">"))!=-1){
							param=param.substring (param.indexOf ((">"))+1);
							param=param.substring (0,param.indexOf (("<")));
						}

						if(param.indexOf (("\""))!=-1)
							param=param.substring (0,param.indexOf (("\"")));

						
						if(param.length()>0){
							msgpackage = Webprinter.myHandler.obtainMessage();  
							msgpackage.obj ="2:"+param ;
							Webprinter.myHandler.sendMessage(msgpackage); 
						}

					//	if(m_hwnd&&param.GetLength ())
					//		::SendMessage (m_hwnd,WM_PROGRESS,(DWORD)"Checking Status...",(DWORD)(LPCTSTR)param);
					
						if(isfinish)//finish
							break;

					}
					try{
						Thread.sleep(1000);
					}
					catch(Exception e){
						
					}
					count++;
				}

				if(count>=90){
					msgpackage = Webprinter.myHandler.obtainMessage();  
					msgpackage.obj ="2:Error: Time out" ;
					Webprinter.myHandler.sendMessage(msgpackage); 
					
				}
				
				break;
			}
		}

//		m_hwnd=false;
		if(m_cost==0)
			return false;
		return true;
	}
	
	void GetAccountInfo(String usr,String pwd){
		usr=m_usr;
		pwd=m_pwd;
	}
	/*
	boolean TopUp(String num,int type){
		m_balance=0;
		switch(type){
			case WEBPRINT_CENTENNIAL:{

				
					if(m_hwnd)
						::SendMessage (m_hwnd,WM_PROGRESS,(DWORD)"Starting Top-up...",NULL);

					Httpmanager mgr;
					String poststr="service=direct/1/Home/$Form&sp=S0&Form0=$Hidden,inputUsername,inputPassword,$PropertySelection,$Submit&$Hidden=true&inputUsername=%%USR%%&inputPassword=%%PWD%%&$PropertySelection=en&$Submit=Log+in";//"Username=300679900&Password=etstudio";
					//String ret=mgr.GetData(1,("e.centennialcollege.ca"),("d2l/lp/auth/login/login.d2l"),(""),(""),poststr,poststr.GetLength(),error,cookie);
					//;jsessionid=za6tsp1p7p98
					poststr.replace(("%%USR%%"),this->m_usr);
					poststr.replace(("%%PWD%%"),m_pwd);

					m_cookie="";
					String m_str=m_cookie;
					String ret=mgr.GetData(0,("http://pracad01.cencol.ca/"),("user"),("http://http://pracad01.cencol.ca/:9191/"),"",m_cookie,NULL,0,m_error);
					m_str=m_cookie;
					ret=mgr.GetData(1,("http://pracad01.cencol.ca/"),("app;")+m_cookie.substring(0,m_cookie.GetLength()-1),("http://http://pracad01.cencol.ca/:9191/user"),"",m_str,poststr,0,9191);

					if(ret.GetLength ()==0){
						m_error=("Network error, Checking your connection try it again");
						return false;
					}
					if(ret.indexOf(("inputUsername"))!=-1||ret.indexOf(("Balance"))==-1){
						m_error=("Format error Update please");
						return false;
					}


					poststr="service=direct/1/UserTopUpCards/$TopUpCardUse.$Form&sp=S0&Form0=$TextField,$Submit&$TextField=%%REMNO%%&$Submit=Redeem+Card";
					//String ret=mgr.GetData(1,("e.centennialcollege.ca"),("d2l/lp/auth/login/login.d2l"),(""),(""),poststr,poststr.GetLength(),error,cookie);
					//;jsessionid=za6tsp1p7p98
					poststr.replace(("%%REMNO%%"),num);

					m_str=m_cookie;
					ret=mgr.GetData(1,("http://pracad01.cencol.ca/"),("app"),("http://http://pracad01.cencol.ca/:9191/"),"",m_cookie,poststr,0,9191);
					
					if(ret.GetLength ()==0){
						m_error=("Network error, Checking your connection try it again");
						return false;
					}//
					if(ret.indexOf(("The supplied card number is invalid"))!=-1){
						m_error=("Card number is invalid...");
						return false;
					}
					else if(ret.indexOf(("The value associated with this card has been successfully applied to your account."))==-1){
						m_error=("Top-up failed please try to do it on Website...");
						return false;
					}
					else if(m_hwnd)
						::SendMessage (m_hwnd,WM_PROGRESS,(DWORD)"Top-up successful, print will be started soon...",NULL);
					else
						AfxMessageBox("Unknown error,if it failed you may need to top-up on website. Print will be started soon...",NULL);
				break;						 
			}
			default:
				break;
		}

		return true;
	}*/
	void SetAccountInfo(String usr,String pwd){
		m_usr=usr;
		m_pwd=pwd;
		
	}
	boolean Login(int type){
		m_balance=0;
	
		
		
		switch(type){
			case WEBPRINT_CENTENNIAL:{

				Message msg = Webprinter.myHandler.obtainMessage();  
		        msg.obj = Webprinter.pthis.getString(R.string.print1) ;
		        Webprinter.myHandler.sendMessage(msg); 
		        
		        msg = Webprinter.myHandler.obtainMessage();  
		        msg.obj = ("1:") ;
		        Webprinter.myHandler.sendMessage(msg); 
		        
		        msg = Webprinter.myHandler.obtainMessage();  
		        msg.obj = ("2:") ;
		        Webprinter.myHandler.sendMessage(msg);
		        
			//	if(m_hwnd)
			//		::SendMessage (m_hwnd,WM_PROGRESS,(DWORD)"Logging to Webprinter...",NULL);

				
				String poststr="service=direct/1/Home/$Form&sp=S0&Form0=$Hidden,inputUsername,inputPassword,$PropertySelection,$Submit&$Hidden=true&inputUsername=%%USR%%&inputPassword=%%PWD%%&$PropertySelection=en&$Submit=Log+in";//"Username=300679900&Password=etstudio";
				//String ret=mgr.GetData(1,("e.centennialcollege.ca"),("d2l/lp/auth/login/login.d2l"),(""),(""),poststr,poststr.GetLength(),error,cookie);
				//;jsessionid=za6tsp1p7p98
				poststr=poststr.replace(("%%USR%%"),m_usr);
				poststr=poststr.replace(("%%PWD%%"),m_pwd);

				m_cookie="";
				String m_str=m_cookie;
				String ret=mgr.GetData("http://pracad01.cencol.ca/user","http://http://pracad01.cencol.ca/:9191/",m_cookie,"",0,9191);//(),"",
				m_cookie=mgr.GetCookie();
				
				if(ret.length()==0){
					msg = Webprinter.myHandler.obtainMessage();  
					msg.obj = "1:Error:"+mgr.GetError();
			        Webprinter.myHandler.sendMessage(msg); 
			        return false;
				}
				
				
				ret=mgr.GetData("http://pracad01.cencol.ca/app;"+m_cookie.substring(0,m_cookie.length()-1),"http://http://pracad01.cencol.ca/:9191/user",m_cookie,poststr,0,9191);//,(),"",

				if(ret.length()==0){
					msg = Webprinter.myHandler.obtainMessage();  
					msg.obj = "1:Error:"+mgr.GetError();
			        Webprinter.myHandler.sendMessage(msg); 
			        return false;
				}
				
				if(ret.indexOf(("inputUsername"))!=-1){
					msg = Webprinter.myHandler.obtainMessage();  
			        msg.obj = Webprinter.pthis.getString(R.string.print11) ;
			        Webprinter.myHandler.sendMessage(msg); 
					m_error=("invalid username or password");
					return false;
				}
				else if(ret.indexOf("Balance")==-1){
					
					msg = Webprinter.myHandler.obtainMessage();  
			        msg.obj = Webprinter.pthis.getString(R.string.print3) ;
			        Webprinter.myHandler.sendMessage(msg); 
					m_error=("Format error Update please");
					return false;
				}

				ret=ret.substring(ret.indexOf(("Balance"))+1);
				ret=ret.substring(ret.indexOf(("$"))+1);
				ret=ret.substring(0,ret.indexOf((" ")));
				m_balance=Float.valueOf(ret);

			//	int value=m_balance*100;
			//	m_balance=(float)value/(float)100;

				break;						 
			}
			default:
				break;
		}

		return true;
	}
}
