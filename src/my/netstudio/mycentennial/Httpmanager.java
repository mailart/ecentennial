package my.netstudio.mycentennial;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.CookieStore;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.*;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceActivity.Header;
import android.util.Log;
import android.webkit.CookieSyncManager;
import android.widget.Toast;


class DownloadThread extends Thread{
	private DownloadTaskItem m_item=null;	
	private static String boundary="name=\"frameContentFile\" src=\"";
	DownloadThread(DownloadTaskItem pitem){
		m_item=pitem;
	}
	private String ReplaceURL(String url){
		url=url.replace("'","%27");
		url=url.replace("<","%3C");
		url=url.replace(">","%3E");
		url=url.replace("&","%26");
		url=url.replace("(","%28");
		url=url.replace(")","%29");
		url=url.replace(";","%3B");
		url=url.replace("+","%2B");
		url=url.replace("-","%2D");
		url=url.replace("[","%5B");
		url=url.replace("]","%5D");
		url=url.replace("{","%7B");
		url=url.replace("}","%7D");
		return url;
		
	}
	private String EncodeParam(String url){
		String url1=url.substring(url.indexOf("?")+1);
		
		while(url1.length()>0){
			String param="";
			if(url1.indexOf("&")!=-1){
				param=url1.substring(0,url1.indexOf("&"));
				url1=url1.substring(url1.indexOf("&")+1);
			}
			else
			{
				param=url1;
				url1="";
			}
			
			String value=param.substring(param.indexOf("=")+1);
			try{
			url=url.replace(value, URLEncoder.encode(value,"UTF-8"));
			}catch(Exception e){}
		}
		
		return url;
		
	}
	private String GetPages(){

		 String returns="";
		 BufferedReader in=null;
		 //Step One  register scheme of https 
		 HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;         
		 SchemeRegistry registry = new SchemeRegistry();   
		 SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();   
		 socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);   
		 registry.register(new Scheme("https", socketFactory, 443)); 
		 registry.register(new Scheme("http",PlainSocketFactory.getSocketFactory (), 80)); 

		 BasicHttpParams httpParams = new BasicHttpParams();  
		    HttpConnectionParams.setConnectionTimeout(httpParams, 60000);  
		    HttpConnectionParams.setSoTimeout(httpParams, 60000);  
		    
		 DefaultHttpClient client = new DefaultHttpClient(httpParams);  
		 SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry); 
		
		 DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());      
		 //------- Set verifier    
		 HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);     
		 // -------Example send http request      
		 // final String url = "https://martinreichart.com/_tmpdata/login_valid.json";  
		 
		 HttpPost httpPost = new HttpPost(EncodeParam(m_item.m_url)); ;
		 HttpGet httpget=new HttpGet(EncodeParam(m_item.m_url)); ;
		 
		 
		 //set cookie
		 if(m_item.m_cookie.length()>0){
			 httpget.setHeader("Cookie", m_item.m_cookie);
			 httpPost.setHeader("Cookie", m_item.m_cookie);
		 }
		 
		 
		 List<NameValuePair> params = new ArrayList<NameValuePair>();
		 boolean ispost=false;
		 while(m_item.m_postparam.length()>0){
			 ispost=true;
			 String half="";
			 if(m_item.m_postparam.indexOf("&")!=-1){
				 half=m_item.m_postparam.substring(0, m_item.m_postparam.indexOf("&"));
				 m_item.m_postparam=m_item.m_postparam.substring(m_item.m_postparam.indexOf("&")+1);
			 }
			 else{
				 half=m_item.m_postparam;
				 m_item.m_postparam="";
			 } 
		     params.add(new BasicNameValuePair(half.substring(0, half.indexOf("=")),half.substring(half.indexOf("=")+1)));
		 }
		 
	
		 //Step Three Get Data
		 try {
			 	if(ispost)
			 		httpPost.setEntity(new UrlEncodedFormEntity(params,"GB2312"));
		 
			 	HttpResponse response = httpClient.execute(ispost?httpPost:httpget); 
			 	
				
			 	int retcode=response.getStatusLine().getStatusCode();
			 	if (retcode== HttpStatus.SC_OK) {  
			 		
			 		String encodestr="";
			 		if(response.getEntity().getContentEncoding()!=null)
			 			encodestr=response.getEntity().getContentEncoding().toString();
			 		if(encodestr.toLowerCase().indexOf("gzip")!=-1){
			 			
			 			InputStream is = response.getEntity().getContent();
			 		      GZIPInputStream gzin = new GZIPInputStream(is); 
			 		      InputStreamReader isr = new InputStreamReader(gzin); // 设置读取流的编码格式，自定义编码
			 		      java.io.BufferedReader br = new java.io.BufferedReader(isr);
			 		      String tempbf; 
			 		      while((tempbf=br.readLine())!=null){ 
			 		    	 returns+=tempbf;
				        	 try{
				        		 Thread.sleep(0);
				        	 }
				        	 catch(Exception e){}
			 		      }
			 		      isr.close();
			 		      gzin.close();
			 		}
			 		else{
			 		
		                // 解析返回的内容   
				 		//response.getEntity()
		                /// returns = EntityUtils.toString(response.getEntity());   
				 	
				 		in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				         
				         String subline="";
				         while ((subline = in.readLine()) != null) {
				        	
				        	 returns+=subline;
				         }
			 		}
			
	             } 
			 	else if(retcode==404)
			 		m_item.m_lastnetworkstatus=m_item.filenotfound;
			 	else
			 		m_item.m_lastnetworkstatus=m_item.httperror;
	     }
		 catch(Exception ex)
		 {
			 m_item.m_lastnetworkstatus=m_item.connecterror;
			 String aa =ex.toString();
			 aa="";
		 }
		 try{
			 if(in!=null)
				 in.close();
		 }
		 catch(Exception ex)
		 {
			 String aa =ex.toString();
		 }
		 httpClient.getConnectionManager().shutdown();  

		 return returns;
	}
	private void GetFile(){
		try {
		String returns="";
		 BufferedReader in=null;
		 //Step One  register scheme of https 
		 HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;         
		 SchemeRegistry registry = new SchemeRegistry();   
		 SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();   
		 socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);   
		 registry.register(new Scheme("https", socketFactory, 443)); 
		 registry.register(new Scheme("http",PlainSocketFactory.getSocketFactory (), 80)); 

		 BasicHttpParams httpParams = new BasicHttpParams();  
		    HttpConnectionParams.setConnectionTimeout(httpParams, 60000);  
		    HttpConnectionParams.setSoTimeout(httpParams, 60000);  
		    
		 DefaultHttpClient client = new DefaultHttpClient(httpParams);  
		 SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry); 
		
		 DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());      
		 //------- Set verifier    
		 HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);     
		 // -------Example send http request      
		 // final String url = "https://martinreichart.com/_tmpdata/login_valid.json";      
		 HttpPost httpPost = new HttpPost(ReplaceURL(m_item.m_url)); 
		 HttpGet httpget=new HttpGet(ReplaceURL(m_item.m_url)); 
		 //set cookie
		 if(m_item.m_cookie.length()>0){
			 httpget.setHeader("Cookie", m_item.m_cookie);
			 httpPost.setHeader("Cookie", m_item.m_cookie);
		 }
		 httpPost.setHeader("Accept-Encoding", "gzip,deflate");
		 httpget.setHeader("Accept-Encoding", "gzip,deflate");
		 
		 List<NameValuePair> params = new ArrayList<NameValuePair>();
		 boolean ispost=false;
		 while(m_item.m_postparam.length()>0){
			 ispost=true;
			 String half="";
			 if(m_item.m_postparam.indexOf("&")!=-1){
				 half=m_item.m_postparam.substring(0, m_item.m_postparam.indexOf("&"));
				 m_item.m_postparam=m_item.m_postparam.substring(m_item.m_postparam.indexOf("&")+1);
			 }
			 else{
				 half=m_item.m_postparam;
				 m_item.m_postparam="";
			 } 
		     params.add(new BasicNameValuePair(half.substring(0, half.indexOf("=")),half.substring(half.indexOf("=")+1)));
		 }
		 
	
		 //Step Three Get Data
		 
			 	if(ispost)
			 		httpPost.setEntity(new UrlEncodedFormEntity(params,"GB2312"));
		 
			 	HttpResponse response = httpClient.execute(ispost?httpPost:httpget); 
			 	
				
			 	int retcode=response.getStatusLine().getStatusCode();
			 	if (retcode== HttpStatus.SC_OK) {  
			 		org.apache.http.Header[] headers = response.getHeaders("Content-length");
			 		if(headers!=null)
			 		{
			 			String val=headers[0].getValue();
			 			m_item.m_totlebytes=Integer.parseInt(val);
			 		}
			 	//	String rethdr=response.getHeaders(name)
			 	//	m_item.m_totlebytes=httpconn.getContentLength()
			 		
			 		String encodestr="";
			 		if(response.getEntity().getContentEncoding()!=null)
			 			encodestr=response.getEntity().getContentEncoding().toString();
			        BufferedInputStream bis = new BufferedInputStream(response.getEntity().getContent());
			        File newFile = new File(m_item.m_savepath+m_item.m_filename);
			        FileOutputStream fos = new FileOutputStream(newFile);
			        BufferedOutputStream bos = new BufferedOutputStream(fos);
			             
			        byte[] bytes = new byte[4096];
			        int len = 0;//最后一次的长度可能不足4096
			        while((len = bis.read(bytes)) > 0) {
			        	m_item.m_receivebytes+=len;
			        	
			        	if(m_item.m_callback!=null){
			        		Message handlemsg = m_item.m_callback.obtainMessage();  
							handlemsg.obj = "3:";  
							m_item.m_callback.sendMessage(handlemsg);  
						}
			        	
			            bos.write(bytes,0,len);
			        }
			        bos.flush();
			        
			        m_item.iscompelte=true;
		        	
	             }
			 	else if(retcode==404)
			 		m_item.m_lastnetworkstatus=m_item.filenotfound;
			 	else
			 		m_item.m_lastnetworkstatus=m_item.httperror;
			 	
			 	httpClient.getConnectionManager().shutdown(); 
			 	if(in!=null)
					 in.close();
	     }
		 catch(Exception ex)
		 {
			
			 String aa =ex.toString();
			 m_item.m_lastnetworkstatus=m_item.connecterror;
		 }
		
	}
	public void run(){
		m_item.ispause=false;
		if(m_item.m_callback!=null){
    		Message handlemsg = m_item.m_callback.obtainMessage();  
			handlemsg.obj = "3:";  
			m_item.m_callback.sendMessage(handlemsg);  
		}
//		GetFile();
		
		if(m_item.m_filefrom==DownloadTaskItem.contentfile&&m_item.m_filename.compareTo(m_item.m_taskname)==0){
			String str=GetPages();
			if(str.indexOf(boundary)!=-1){
				str=str.substring(str.indexOf(boundary)+boundary.length());
				str=str.substring(0,str.indexOf("\""));
				str=Httpmanager.FilterEntities(str);
				boolean isoutsideurl=false;
				if(str.indexOf("http")==0){
					m_item.m_url=str;
					isoutsideurl=true;
				}
				else
					m_item.m_url="https://e.centennialcollege.ca/"+str.substring(0,str.indexOf("?"));
				
				
				
				if(isoutsideurl==false){
					m_item.m_filename="";
					m_item.m_filename=m_item.m_url;
					//m_item.m_filename=m_item.m_filename.substring(0, m_item.m_filename.indexOf("?"));
					while(m_item.m_filename.indexOf("/")!=-1){
						
						m_item.m_filename=m_item.m_filename.substring(m_item.m_filename.indexOf("/")+1);
					}
					m_item.m_url=m_item.m_url.replace(" ","%20");
					GetFile();
				}
				else{
					m_item.m_url=m_item.m_url.replace(" ","%20");
					m_item.iscompelte=true;
					
				}
			}
			else if(str.length()>0)
				m_item.m_lastnetworkstatus=m_item.formaterror;
			else
				m_item.m_lastnetworkstatus=m_item.connecterror;
		}
		else{
			
			m_item.m_url=m_item.m_url.replace(" ","%20");
			GetFile();
		}
			
		
		m_item.ispause=true;
		if(m_item.m_callback!=null){
    		Message handlemsg = m_item.m_callback.obtainMessage();  
			handlemsg.obj = "4:";  
			m_item.m_callback.sendMessage(handlemsg);  
		}
	}
	
}
class MyX509TrustManager implements X509TrustManager  //编写证书过滤器 
{

	public void checkClientTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
		// TODO Auto-generated method stub
		
	}

	public void checkServerTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
		// TODO Auto-generated method stub
		
	}

	public X509Certificate[] getAcceptedIssuers() {
		// TODO Auto-generated method stub
		return null;
	}   
};      
 class MyHostnameVerifier implements HostnameVerifier{

	public boolean verify(String hostname, SSLSession session) {
		// TODO Auto-generated method stub
		return true;
	}
	
	
}
public class Httpmanager {
	 private String m_usr;
	 private String m_pwd;
	 private String m_logurl;
	 private String m_host;
	 
	 int retcode=0;
	 private String cookie="";
	 private String redirect="";
	 
	 private String error="";
	 
	 public String GetError(){
		 
		 return error;
	 }
	 public String GetCookie(){
		 
		 return cookie;
	 }
	 public void ClearCookie(){
		 
		 cookie="";
	 }
	 /*
	 private static class SavingTrustManager implements X509TrustManager {

		 private final X509TrustManager tm;
		 private X509Certificate[] chain;

		 SavingTrustManager(X509TrustManager tm) {
			 this.tm = tm;
		 }

		 public X509Certificate[] getAcceptedIssuers() {
			 throw new UnsupportedOperationException();
		 }

		 public void checkClientTrusted(X509Certificate[] chain, String authType)
		throws CertificateException {
			 	throw new UnsupportedOperationException();
		 }

		 public void checkServerTrusted(X509Certificate[] chain, String authType)
		 throws CertificateException {
			 this.chain = chain;
			 tm.checkServerTrusted(chain, authType);
		 }
	}
	 
	 private static void installCert(String host, int port, String passwd,String sslProtocol) 
	 {
		 try {
			 String trustStorePath = System.getProperty("javax.net.ssl.trustStore");
			 // File keystoreFile = new File(trustStorePath);
			 // 由于android权限原因，无法读取trustStorePath="//system/etc/security/cacerts.bks"文件，此处由sdcard代替
			 File keystoreFile = new File("/sdcard/cacerts.bks");
			 InputStream in = new FileInputStream(keystoreFile);
			 KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
			 ks.load(in, passwd.toCharArray());
			 in.close();

			 SSLContext context = SSLContext.getInstance(sslProtocol);
			 TrustManagerFactory tmf = TrustManagerFactory
			 .getInstance(TrustManagerFactory.getDefaultAlgorithm());
			 tmf.init(ks);

			 X509TrustManager defaultTrustManager = (X509TrustManager) tmf
			 .getTrustManagers()[0];
			 SavingTrustManager tm = new SavingTrustManager(defaultTrustManager);
			 context.init(null, new TrustManager[] { tm }, null);
			 javax.net.ssl.SSLSocketFactory factory = context.getSocketFactory();
			 boolean istrusted = false;
			 try {
				 SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
				 socket.setSoTimeout(50000);
				 socket.startHandshake();
				 socket.close();
				 istrusted = true;
			 } catch (SSLException e) {
				 Log.i("xx", e.getMessage());
				 istrusted = false;
			 }
			 
			 if (!istrusted) {
				 X509Certificate[] chain = tm.chain;
				 if (chain == null) {
					 return;
				 }
				 ks.setCertificateEntry(host + "_" + 0, chain[0]);
				 // 如果想更改新密码，这个passwd替换成新密码即可
				 ks.store(new FileOutputStream(new File("/sdcard/cacerts.bks")),
						 passwd.toCharArray());
			 }

		 } catch (FileNotFoundException e) {
		 Log.e("xx", e.getMessage());
		 } catch (NoSuchAlgorithmException e) {
		 Log.e("xx", e.getMessage());
		 } catch (CertificateException e) {
		 Log.e("xx", e.getMessage());
		 } catch (IOException e) {
		 Log.e("xx", e.getMessage());
		 } catch (KeyStoreException e) {
		 Log.e("xx", e.getMessage());
		 } catch (KeyManagementException e) {
		 Log.e("xx", e.getMessage());
		 }
	}
	 private HttpClient makeHttpsClient(String keyStorePasswd, int port) {
		 try {
			 KeyStore trustStore = KeyStore.getInstance(KeyStore
			 .getDefaultType());
			 String trustStorePath = System
			 .getProperty("javax.net.ssl.trustStore");
			 // File keystoreFile = new File(trustStorePath);
			 // 由于android权限原因，无法读取trustStorePath="//system/etc/security/cacerts.bks"文件，此处由sdcard代替
			 File keystoreFile = new File("/sdcard/cacerts.bks");
			 trustStore.load(new FileInputStream(keystoreFile), keyStorePasswd
			 .toCharArray());

			 TrustManager[] tm = { new MyX509TrustManager() }; 
			 
			 
			 SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
		     sslContext.init(null, tm, new java.security.SecureRandom()); 
		     javax.net.ssl.SSLSocketFactory socketFactory = sslContext.getSocketFactory(); 
	//		 SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
	//		 socketFactory.setHostnameVerifier(tm);
			 
			 Scheme sch = new Scheme("https", socketFactory, port);
			 HttpClient httpClient = new DefaultHttpClient();
			 httpClient.getConnectionManager().getSchemeRegistry().register(sch);
			 
			 return httpClient;
			 
		 } catch (KeyStoreException e) {
		 Log.e("xx", e.getMessage());
		 } catch (NoSuchAlgorithmException e) {
		 Log.e("xx", e.getMessage());
		 } catch (CertificateException e) {
		 Log.e("xx", e.getMessage());
		 } catch (KeyManagementException e) {
		 Log.e("xx", e.getMessage());
		 } catch (UnrecoverableKeyException e) {
		 Log.e("xx", e.getMessage());
		 } catch (IOException e) {
		 Log.e("xx", e.getMessage());
		 }
		 return null;
	 }
	 private HttpPost makeHttpPost(String url) {
		 HttpPost httpPost = new HttpPost(url);
		 HttpParams timeParams = new BasicHttpParams();
		 HttpConnectionParams.setConnectionTimeout(timeParams, 30 * 1000);
		 HttpConnectionParams.setSoTimeout(timeParams, 30 * 1000);
		 httpPost.setParams(timeParams);
		 return httpPost;
	 }*/
	 public void setuserName(String logurl,String username,String password,String host){
		 m_usr=username;
		 m_pwd=password;
		 m_logurl=logurl;
		 m_host=host;
	 }
	 public static String FilterHtmlMark(String htmlstr){
			String tempstr=htmlstr;
			htmlstr="";
			while(tempstr.length()!=0){
				if(tempstr.indexOf("<")!=-1){
					htmlstr+=tempstr.substring(0,tempstr.indexOf("<"));
					tempstr=tempstr.substring(tempstr.indexOf("<")+1);
					if(tempstr.indexOf(">")!=-1){
						tempstr=tempstr.substring(tempstr.indexOf(">")+1);
					}
					else
						htmlstr+="<";
				}
				else{
					htmlstr+=tempstr;
					tempstr="";
				}
				
			}
			return htmlstr;
		}
	 public static String FilterEntities(String htmlstr){
			htmlstr=htmlstr.replace("&quot;", "\"");
			htmlstr=htmlstr.replace("&#34;", "\"");
			htmlstr=htmlstr.replace("&#x22;", "\"");
			htmlstr=htmlstr.replace("&amp;", "&");
			htmlstr=htmlstr.replace("&#38;", "&");
			htmlstr=htmlstr.replace("&#39;", "'");
			htmlstr=htmlstr.replace("&#x26;", "&");
			htmlstr=htmlstr.replace("&lt;", "<");
			htmlstr=htmlstr.replace("&#60;", "<");
			htmlstr=htmlstr.replace("&#x3C;", "<");
			htmlstr=htmlstr.replace("&gt;", ">");
			htmlstr=htmlstr.replace("&#62;", ">");
			htmlstr=htmlstr.replace("&#x3E;", ">");
			htmlstr=htmlstr.replace("&#160;", "");
		 
			htmlstr=htmlstr.replace("&circ;", "^");
			htmlstr=htmlstr.replace("&#710;", "^");
			htmlstr=htmlstr.replace("&#x2C6;", "^");
			htmlstr=htmlstr.replace("&tilde;", "~");
			htmlstr=htmlstr.replace("&#732;", "~");
			htmlstr=htmlstr.replace("&#x2DC;", "~");
			
			
			return htmlstr;
		}
	 private HttpsURLConnection GetHttpsConnect(String url){
		 HttpsURLConnection urlCon=null;
		 try{
			 urlCon =(HttpsURLConnection) (new URL(url)).openConnection();
	    	 TrustManager[] managers ={new MyX509TrustManager()};  
	    	 SSLContext sslContext=SSLContext.getInstance("TLS");  
	         sslContext.init(null, managers, new SecureRandom());  
	         urlCon.setSSLSocketFactory(sslContext.getSocketFactory());  
	         urlCon.setHostnameVerifier(new MyHostnameVerifier());  
		 }
         catch(IOException e){
        	 error=e.toString();
        	 Log.e("xx", error);
         }
         catch(Exception e){
        	 error=e.toString();
        	 Log.e("xx", error);
         }
         return urlCon;
	 }
	 private HttpURLConnection GetHttpConnect(String url){
		 HttpURLConnection urlCon=null;
		 try{
			 urlCon =(HttpURLConnection) (new URL(url)).openConnection();
	    	 
		 }
         catch(IOException e){
        	 error=e.toString();
        	 Log.e("xx", error);
         }
         catch(Exception e){
        	 error=e.toString();
        	 Log.e("xx", error);
         }
         return urlCon;
	 }
	 private String ProcessCookie(String curcookie){
		 String retcook="";
		 curcookie=curcookie.replaceAll(",",";");
		 String lastval="";
		 while(curcookie.length()>0){
			 String half="";
			 if(curcookie.indexOf(";")!=-1){
				 half=curcookie.substring(0, curcookie.indexOf(";")+1);
				 curcookie=curcookie.substring(curcookie.indexOf(";")+1);
			 }
			 else{
				 half=curcookie;
				 curcookie="";
			 }
			 half=half.replaceAll(" ","");
			 if(half.indexOf("=")!=-1){//是等式
				 
				 String vname=half.substring(0, half.indexOf("="));
				 String vvalue=half.substring(half.indexOf("=")+1);
				 if(vname.compareToIgnoreCase("Comment")!=0&&
						 vname.compareToIgnoreCase("Domain")!=0&&
						 vname.compareToIgnoreCase("Max-Age")!=0&&
						 vname.compareToIgnoreCase("Path")!=0&&
						 vname.compareToIgnoreCase("expires")!=0&&
						 vname.compareToIgnoreCase("Versions")!=0&&
						 vvalue.length()>1
						 )
				 {
					 retcook+=half+" ";
					 lastval=half;
				 }
				
			 }
			 else if(half.indexOf("HttpOnly")!=-1){
		//		 retcook=retcook.substring(0, retcook.indexOf(lastval));
			 }
			 
		 }
		 
		 return retcook;
	 }
	 public DownloadTaskItem StartDownloadTask(DownloadTaskItem dtask,String taskname,String url,String cookie,String postparam,String rootpath,String filename,long objid,Object obj,Handler callback,int filefrom){
		 DownloadTaskItem pret=dtask;
		 if(pret==null)
			 pret=new DownloadTaskItem(taskname,url,cookie,postparam,rootpath,filename,objid,obj,callback,filefrom);
		 new DownloadThread(pret ).start(); 
		 return pret;
	 }
	 
	 public boolean GetFile(String URL,String curcookie,String postparam,String filepath){

		 boolean ret=false;
		 BufferedReader in=null;
		 //Step One  register scheme of https 
		 HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;         
		 SchemeRegistry registry = new SchemeRegistry();   
		 SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();   
		 socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);   
		 registry.register(new Scheme("https", socketFactory, 443)); 
		 registry.register(new Scheme("http",PlainSocketFactory.getSocketFactory (), 80)); 

		 BasicHttpParams httpParams = new BasicHttpParams();  
		    HttpConnectionParams.setConnectionTimeout(httpParams, 60000);  
		    HttpConnectionParams.setSoTimeout(httpParams, 60000);  
		    
		 DefaultHttpClient client = new DefaultHttpClient(httpParams);  
		 SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry); 
		
		 DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());      
		 //------- Set verifier    
		 HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);     
		 // -------Example send http request      
		 // final String url = "https://martinreichart.com/_tmpdata/login_valid.json";      
		 HttpPost httpPost = new HttpPost(URL); 
		 HttpGet httpget=new HttpGet(URL);
		 //set cookie
		 if(curcookie.length()>0){
			 httpget.setHeader("Cookie", curcookie);
			 httpPost.setHeader("Cookie", curcookie);
			 
		 }
		 
		 
		 
		 List<NameValuePair> params = new ArrayList<NameValuePair>();
		 boolean ispost=false;
		 while(postparam.length()>0){
			 ispost=true;
			 String half="";
			 if(postparam.indexOf("&")!=-1){
				 half=postparam.substring(0, postparam.indexOf("&"));
				 postparam=postparam.substring(postparam.indexOf("&")+1);
			 }
			 else{
				 half=postparam;
				 postparam="";
			 } 
		     params.add(new BasicNameValuePair(half.substring(0, half.indexOf("=")),half.substring(half.indexOf("=")+1)));
		 }
		 
	
		 //Step Three Get Data
		 try {
			 	if(ispost)
			 		httpPost.setEntity(new UrlEncodedFormEntity(params,"GB2312"));
		 
			 	HttpResponse response = httpClient.execute(ispost?httpPost:httpget); 
			 	
				
			 	retcode=response.getStatusLine().getStatusCode();
			 	if (retcode== HttpStatus.SC_OK) {  
			 		if(cookie.length()==0){
			 			//MainActivity.cookieManager.setAcceptCookie(true);  
				 		//MainActivity. cookieManager.removeSessionCookie();//移除  
				 		for(int index=0;index<httpClient.getCookieStore().getCookies().size();index++){
				 			Cookie a=httpClient.getCookieStore().getCookies().get(index);
				 			cookie+=a.getName()+"=";
				 			cookie+=a.getValue()+"; ";
				 		//	MainActivity.cookieManager.setCookie("e.centennialcollege.ca", a.getName()+"="+a.getValue());
				 		}	
				 	
			 		}
			 		
			 		
	                // 解析返回的内容   
			 		//response.getEntity()
	                /// returns = EntityUtils.toString(response.getEntity());   
			 	
			 		BufferedInputStream bis = new BufferedInputStream(response.getEntity().getContent());
			        File newFile = new File(filepath);
			        FileOutputStream fos = new FileOutputStream(newFile);
			        BufferedOutputStream bos = new BufferedOutputStream(fos);
			             
			        byte[] bytes = new byte[4096];
			        int len = 0;//最后一次的长度可能不足4096
			        while((len = bis.read(bytes)) > 0) {
			     
			        	
		
			            bos.write(bytes,0,len);
			        }
			        bos.flush();
			        ret=true;
	             } 
	     }
		 catch(Exception ex)
		 {
			 error =ex.toString();
			 
		 }
		 try{
			 if(in!=null)
				 in.close();
		 }
		 catch(Exception ex)
		 {
			 error =ex.toString();
		 }
		 httpClient.getConnectionManager().shutdown();  

		 return ret;
	}
	 public String GetData(String URL,String reference,String curcookie,String postparam,int httpsport,int httpport){

		 String returns="";
		 BufferedReader in=null;
		 //Step One  register scheme of https 
		 HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;         
		 SchemeRegistry registry = new SchemeRegistry();   
		 SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();   
		 socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);   
		 registry.register(new Scheme("https", socketFactory, httpsport>0?httpsport:443)); 
		 registry.register(new Scheme("http",PlainSocketFactory.getSocketFactory (), httpport>0?httpport:80)); 

		 BasicHttpParams httpParams = new BasicHttpParams();  
		    HttpConnectionParams.setConnectionTimeout(httpParams, 60000);  
		    HttpConnectionParams.setSoTimeout(httpParams, 60000);  
		    
		 DefaultHttpClient client = new DefaultHttpClient(httpParams);  
		 SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry); 
		
		 DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());      
		 //------- Set verifier    
		 HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);     
		 // -------Example send http request      
		 // final String url = "https://martinreichart.com/_tmpdata/login_valid.json";      
		 HttpPost httpPost = new HttpPost(URL); 
		 HttpGet httpget=new HttpGet(URL);
		 //set cookie
		 if(curcookie.length()>0){
			 httpget.setHeader("Cookie", curcookie);
			 httpPost.setHeader("Cookie", curcookie);
			 
		 }
		 
		 httpget.setHeader("Accept-Encoding", "gzip,deflate");
		 httpPost.setHeader("Accept-Encoding", "gzip,deflate");
		 httpget.setHeader("Accept", "text/html, application/xhtml+xml, */*");
		 httpPost.setHeader("Accept", "text/html, application/xhtml+xml, */*");
		 
		 
		 if(reference.length()>0){
			 httpget.setHeader("Referer", reference);
			 httpPost.setHeader("Referer", reference);
		 }
		 
		 List<NameValuePair> params = new ArrayList<NameValuePair>();
		 boolean ispost=false;
		 while(postparam.length()>0){
			 ispost=true;
			 String half="";
			 if(postparam.indexOf("&")!=-1){
				 half=postparam.substring(0, postparam.indexOf("&"));
				 postparam=postparam.substring(postparam.indexOf("&")+1);
			 }
			 else{
				 half=postparam;
				 postparam="";
			 } 
		     params.add(new BasicNameValuePair(half.substring(0, half.indexOf("=")),half.substring(half.indexOf("=")+1)));
		 }
		 
	
		 //Step Three Get Data
		 try {
			 	if(ispost)
			 		httpPost.setEntity(new UrlEncodedFormEntity(params,"GB2312"));
		 
			 	HttpResponse response = httpClient.execute(ispost?httpPost:httpget); 
			 	
				
			 	retcode=response.getStatusLine().getStatusCode();
			 	if (retcode== HttpStatus.SC_OK) {  
			 		if(cookie.length()==0){
			 			//MainActivity.cookieManager.setAcceptCookie(true);  
				 		//MainActivity. cookieManager.removeSessionCookie();//移除  
				 		for(int index=0;index<httpClient.getCookieStore().getCookies().size();index++){
				 			Cookie a=httpClient.getCookieStore().getCookies().get(index);
				 			cookie+=a.getName()+"=";
				 			cookie+=a.getValue()+"; ";
				 		//	MainActivity.cookieManager.setCookie("e.centennialcollege.ca", a.getName()+"="+a.getValue());
				 		}	
				 	
			 		}
			 		String encodestr="";
			 		if(response.getEntity().getContentEncoding()!=null)
			 			encodestr=response.getEntity().getContentEncoding().toString();
			 		if(encodestr.toLowerCase().indexOf("gzip")!=-1){
			 			
			 			InputStream is = response.getEntity().getContent();
			 		      GZIPInputStream gzin = new GZIPInputStream(is); 
			 		      InputStreamReader isr = new InputStreamReader(gzin); // 设置读取流的编码格式，自定义编码
			 		      java.io.BufferedReader br = new java.io.BufferedReader(isr);
			 		      String tempbf; 
			 		      while((tempbf=br.readLine())!=null){ 
			 		    	 returns+=tempbf;
				        	 try{
				        		 Thread.sleep(0);
				        	 }
				        	 catch(Exception e){}
			 		      }
			 		      isr.close();
			 		      gzin.close();
			 		}
			 		else{
	                // 解析返回的内容   
				 		//response.getEntity()
		                /// returns = EntityUtils.toString(response.getEntity());   
				 	
				 		in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				         
				         String subline="";
				         while ((subline = in.readLine()) != null) {
				        	
				        	 returns+=subline;
				        	 try{
				        		 Thread.sleep(0);
				        	 }
				        	 catch(Exception e){error=e.toString();}
				         }
			 		}
	             } 
	     }
		 catch(Exception ex)
		 {
			 error =ex.toString();
			 returns="";
		 }
		 try{
			 if(in!=null)
				 in.close();
		 }
		 catch(Exception ex)
		 {
			 error =ex.toString();
		 }
		 httpClient.getConnectionManager().shutdown();  

		 return returns;
	}
	
	 public String Upload(String URL,String filename,String filepath,ArrayList<BasicNameValuePair> postParams,int httpsport,int httpport){
		 String returns="";
		 boolean ret=false;
		 BufferedReader in=null;
		 //Step One  register scheme of https 
		 HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;         
		 SchemeRegistry registry = new SchemeRegistry();   
		 SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();   
		 socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);   
		 registry.register(new Scheme("https", socketFactory, httpsport>0?httpsport:443)); 
		 registry.register(new Scheme("http",PlainSocketFactory.getSocketFactory (), httpport>0?httpport:80)); 

		 BasicHttpParams httpParams = new BasicHttpParams();  
		    HttpConnectionParams.setConnectionTimeout(httpParams, 60000);  
		    HttpConnectionParams.setSoTimeout(httpParams, 60000);  
		    
		 DefaultHttpClient client = new DefaultHttpClient(httpParams);  
		 SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry); 
		
		 DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());      
		 //------- Set verifier    
		 HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);     
		 // -------Example send http request      
		 // final String url = "https://martinreichart.com/_tmpdata/login_valid.json";      
		 HttpPost httpPost = new HttpPost(URL); 
		 //set cookie

		 
		 try{
			 MultipartEntity params = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
				
		 	params.addPart(filename, new FileBody(new File(filepath)));
		  	for (BasicNameValuePair param : postParams) {  
			  params.addPart(param.getName(), new StringBody(param.getValue()));  
	        }
		  	httpPost.setEntity(params);
		 }
		 
		 catch(Exception ev){
			 error=ev.toString();
		 }
	
		 //Step Three Get Data
		 try {
			 	
			 	HttpResponse response = httpClient.execute(httpPost); 
			 	
				
			 	retcode=response.getStatusLine().getStatusCode();
			 	if (retcode== HttpStatus.SC_OK) {  
			 		if(cookie.length()==0){
			 			//MainActivity.cookieManager.setAcceptCookie(true);  
				 		//MainActivity. cookieManager.removeSessionCookie();//移除  
				 		for(int index=0;index<httpClient.getCookieStore().getCookies().size();index++){
				 			Cookie a=httpClient.getCookieStore().getCookies().get(index);
				 			cookie+=a.getName()+"=";
				 			cookie+=a.getValue()+"; ";
				 		//	MainActivity.cookieManager.setCookie("e.centennialcollege.ca", a.getName()+"="+a.getValue());
				 		}	
				 	
			 		}
			 		
			 		
	                // 解析返回的内容   
			 		//response.getEntity()
	                /// returns = EntityUtils.toString(response.getEntity());   
			 	
			 		in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			         
			         String subline="";
			         while ((subline = in.readLine()) != null) {
			        	
			        	 returns+=subline;
			        	 try{
			        		 Thread.sleep(0);
			        	 }
			        	 catch(Exception e){}
			         }
			
	             } 
			 	ret= true;
	     }
		 catch(Exception ex)
		 {
			 error =ex.toString();
			 
		 }
		 try{
			 if(in!=null)
				 in.close();
		 }
		 catch(Exception ex)
		 {
			 error =ex.toString();
		 }
		 httpClient.getConnectionManager().shutdown();  

		 return returns;
	}
	 public boolean Upload(String URL,String filename,String filepath,String savepath,ArrayList<BasicNameValuePair> postParams,int httpsport,int httpport){

		 boolean ret=false;
		 BufferedReader in=null;
		 //Step One  register scheme of https 
		 HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;         
		 SchemeRegistry registry = new SchemeRegistry();   
		 SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();   
		 socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);   
		 registry.register(new Scheme("https", socketFactory, httpsport>0?httpsport:443)); 
		 registry.register(new Scheme("http",PlainSocketFactory.getSocketFactory (), httpport>0?httpport:80)); 

		 BasicHttpParams httpParams = new BasicHttpParams();  
		    HttpConnectionParams.setConnectionTimeout(httpParams, 60000);  
		    HttpConnectionParams.setSoTimeout(httpParams, 60000);  
		    
		 DefaultHttpClient client = new DefaultHttpClient(httpParams);  
		 SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry); 
		
		 DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());      
		 //------- Set verifier    
		 HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);     
		 // -------Example send http request      
		 // final String url = "https://martinreichart.com/_tmpdata/login_valid.json";      
		 HttpPost httpPost = new HttpPost(URL); 
		 //set cookie

		 
		 try{
			 MultipartEntity params = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
				
		 	params.addPart(filename, new FileBody(new File(filepath)));
		  	for (BasicNameValuePair param : postParams) {  
			  params.addPart(param.getName(), new StringBody(param.getValue()));  
	        }
		  	httpPost.setEntity(params);
		 }
		 
		 catch(Exception ev){
			 error=ev.toString();
		 }
	
		 //Step Three Get Data
		 try {
			 	
			 	HttpResponse response = httpClient.execute(httpPost); 
			 	
				
			 	retcode=response.getStatusLine().getStatusCode();
			 	if (retcode== HttpStatus.SC_OK) {  
			 		if(cookie.length()==0){
			 			//MainActivity.cookieManager.setAcceptCookie(true);  
				 		//MainActivity. cookieManager.removeSessionCookie();//移除  
				 		for(int index=0;index<httpClient.getCookieStore().getCookies().size();index++){
				 			Cookie a=httpClient.getCookieStore().getCookies().get(index);
				 			cookie+=a.getName()+"=";
				 			cookie+=a.getValue()+"; ";
				 		//	MainActivity.cookieManager.setCookie("e.centennialcollege.ca", a.getName()+"="+a.getValue());
				 		}	
				 	
			 		}
			 		
			 		
	                // 解析返回的内容   
			 		//response.getEntity()
	                /// returns = EntityUtils.toString(response.getEntity());   
			 	
			 		BufferedInputStream bis = new BufferedInputStream(response.getEntity().getContent());
			        File newFile = new File(savepath);
			        FileOutputStream fos = new FileOutputStream(newFile);
			        BufferedOutputStream bos = new BufferedOutputStream(fos);
			        
			        byte[] bytes = new byte[4096];
			        int len = 0;//最后一次的长度可能不足4096
			        while((len = bis.read(bytes)) > 0) {
			        	
			            bos.write(bytes,0,len);
			        }
			        bos.flush();
			
	             } 
			 	ret= true;
	     }
		 catch(Exception ex)
		 {
			 error =ex.toString();
			 
		 }
		 try{
			 if(in!=null)
				 in.close();
		 }
		 catch(Exception ex)
		 {
			 error =ex.toString();
		 }
		 httpClient.getConnectionManager().shutdown();  

		 return ret;
	}
	 private String GetData(HttpsURLConnection urlsCon,String host,String url,String reference,boolean type,String curcookie,String postparam){
		 String query = postparam; //请求参数
         byte[] entitydata = query.getBytes();//得到实体数据
         String line="";
         HttpURLConnection urlCon=urlsCon;
         try{
        	 String domain="";
        	 String fullurl=host+url;
        	 if(urlCon==null){
        		 if(host.indexOf("https")!=-1){
        			 urlCon =GetHttpsConnect(fullurl);
        		 		domain=host.substring(8);    
        	 	}
        	 	else{
        			 urlCon =GetHttpConnect(fullurl);
        		 	domain=host.substring(7);
        	 	}
        	 }
        	 urlCon.setConnectTimeout(30000);  
        	 urlCon.setReadTimeout(30000); 
        	  	 

        	  if(reference!=null)
        		  urlCon.setRequestProperty("Referer", reference);
        	  
        	  urlCon.setRequestProperty("Cache-Control", "no-cache"); 
        	  urlCon.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.5; Windows 98)");  
        	  urlCon.setRequestProperty("Accept-Encoding", "identity");  
        	urlCon.setRequestProperty("Accept", "image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, */*");  
        	urlCon.setRequestProperty("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");  
             urlCon.setRequestProperty("connection", "Keep-Alive");  
             urlCon.setRequestProperty("Host", domain);  
	   //      
	         
	         if(curcookie.length()>0)
	        	 urlCon.setRequestProperty("Cookie", curcookie);
	         
	         if(type){
	        	 urlCon.setDoOutput(true);
		         urlCon.setDoInput(true);
	        	 urlCon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	        	 urlCon.setRequestProperty("Content-Length",String.valueOf(entitydata.length));
	        	 urlCon.setRequestMethod("POST");
	         }
	         else{
	        	 
	        	 
	        	urlCon.setDoOutput(false);
	         	urlCon.setDoInput(true);
	         	urlCon.setRequestMethod("GET");
	         	
	         }
	         
	         urlCon.connect();
         
	         if(type){
		         //把封装好的实体数据发送到输出流
		         OutputStream outStream = urlCon.getOutputStream();
		         outStream.write(entitydata, 0, entitydata.length);
		   //      outStream.write(entitydata);
		         outStream.flush();
		         outStream.close();
	         }
	
	         
	         retcode=urlCon.getResponseCode();
	         if(urlCon.getHeaderField("Location")!=null)
	        	 redirect=urlCon.getHeaderField("Location");

	         Map   m=urlCon.getHeaderFields();
             Set   set=m.entrySet();
             Iterator   it=set.iterator();
             while(it.hasNext())
             {
               Map.Entry   me=(Map.Entry)it.next();
               String skey=me.getKey()!=null?me.getKey().toString():"";
               String svalue=me.getValue()!=null?me.getValue().toString():"";
               if(skey.compareToIgnoreCase("Set-Cookie")==0){
            	   String tempcookie=svalue;
            	   tempcookie=tempcookie.substring(1, tempcookie.length()-1);
            	   cookie+=ProcessCookie(tempcookie);
            	   //cookie=cookie.substring(0, cookie.length()-2);
               }
             } 
	         
	         
	 //        if(urlCon.getHeaderField("Set-Cookie")!=null)
	   //     	 cookie=urlCon.getHeaderField("Set-Cookie");
             
	       //服务器返回输入流并读写
             BufferedReader in = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
	         
	         String subline="";
	         while ((subline = in.readLine()) != null) {
	        	
	            line+=subline;
	         }
	         in.close();
	         
         }
         catch(IOException e){
        	 error=e.toString();
        	 Log.e("xx", error);
         }
         catch(Exception e){
        	 error=e.toString();
        	 Log.e("xx", error);
         }
         
         if(urlCon!=null)
        	 urlCon.disconnect();
		 return line;
		 
	 }
	 
	 public String Login(String post){
		 /*
		 String query = post;; //请求参数
//		 String aaa=GetPage("https://e.centennialcollege.ca/","",false,cookie,query);
//		 HttpsURLConnection urlCon=GetHttpConnect(m_host+m_logurl);
//		 String paddgetxt=GetHttpPage(null,"https://e.centennialcollege.ca","",null,false,cookie,query);
		 String oldhost=m_host;
		 String oldurl=m_logurl;
		 String pagetxt=GetHttpPage(null,m_host,m_logurl,null,true,cookie,post);
	//	 Toast.makeText(MainActivity.gContent, cookie+"sadf",Toast.LENGTH_SHORT).show();
	     while(retcode>300&&retcode<400){//redirection
	 
	    	 String newhost=m_host;
	    	 
	    	 if(redirect.indexOf(".ca")!=-1){
	    		 newhost=redirect.substring(0, redirect.indexOf(".ca")+3);
	    		 redirect=redirect.substring(redirect.indexOf(".ca")+3);
	    	 }
		      pagetxt=GetHttpPage(null,newhost,redirect,oldhost+oldurl,false,cookie,query);
		      
		      oldurl=redirect;
		      oldhost=newhost;
         }
         */
		 return "";
	 }

	 
	 private Object getApplicationContext() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
