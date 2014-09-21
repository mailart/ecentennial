package my.netstudio.mycentennial;

import java.io.File;

public class CourseInfo{
	private String coursename="";
	private String courseurl="";
	private String sddir="";
	
	private int courseid=0;
	private int courseyear=0;
	private int courseterm=0;

	CourseInfo(String name,String url){
		if(name.indexOf("--")!=-1)		
			coursename=name.substring(name.indexOf("--")+2);
		else if(name.indexOf("- ")!=-1)		
			coursename=name.substring(name.indexOf("- ")+2);
		else if(name.indexOf("-")!=-1)		
			coursename=name.substring(name.indexOf("-")+1);
		else
			coursename=name;
			
		courseurl=url;
		courseid=Integer.parseInt(courseurl.substring(courseurl.indexOf("=")+1));
		//ACCT401101_2012M - 12M --Acct. Manager Decision Making (SEC. 101)
//		courseyear=Integer.parseInt(name.substring(name.indexOf("_")+1,name.indexOf("_")+5));
		
		/*
		if(name.substring(name.indexOf("_")+5,name.indexOf("_")+6).compareToIgnoreCase("F")==0){
	//		courseterm=2;//fall
		}
		else if(name.substring(name.indexOf("_")+5,name.indexOf("_")+6).compareToIgnoreCase("m")==0){
	//		courseterm=1;//summer
		}
		else{
//			courseterm=0;//spring
		}*/
		
//		sddir=ecentennial.sdpath+name.substring(0,name.indexOf("-"))+"/";
//		File file = new File(sddir);
//		if (!file.exists()) {
//			file.mkdir();
//		}

	}
	public int GetCourseTimeStamps(){
		String str=String.valueOf(courseyear)+String.valueOf(courseterm);
		return Integer.parseInt(str);
		
	}
	public int GetCourseID(){
		return courseid;
	}
	public String GetDir(){	
		return sddir;
	}
	public String GetName(){	
		return coursename;
	}
	public String GetGradeUrl(){
		return "https://e.centennialcollege.ca/d2l/lms/grades/my_grades/main.d2l?ou="+String.valueOf(courseid);
	}
	public String GetContentUrl(){
		return "https://e.centennialcollege.ca/d2l/lms/content/print/print_download.d2l?ou="+String.valueOf(courseid);
	}
	public String GetCourseHome(){
		return courseurl;
	}
}