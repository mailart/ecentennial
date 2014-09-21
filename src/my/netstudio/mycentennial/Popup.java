package my.netstudio.mycentennial;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Popup extends Dialog {
	public Popup(Context context,int x,int y) {  
  	  
        super(context);  
        requestWindowFeature(Window.FEATURE_NO_TITLE); // ����Ի�����⣬Ҫ����setContentViewǰ�����ᱨ��   
 
        setContentView(R.layout.popupmenu);

        ListView list = (ListView) findViewById(R.id.course);
        String[] adapterData = new String[]{};
        ArrayList<String> lst =new ArrayList<String>(); 
        lst.add("All courses");

        int aaaa=EService.e.courlist.size();
        for(int index=0;index<EService.e.courlist.size();index++){
        	CourseInfo info=EService.e.courlist.get(index);
        	lst.add(info.GetName());
        }
        
        
        
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.pmain,android.R.layout.simple_list_item_1,lst);
 //       arrayAdapter.remove("abc");
        
        
        list.setAdapter(arrayAdapter);
        		
        setCanceledOnTouchOutside(true);// ����Ի����ⲿȡ���Ի�����ʾ

        LayoutParams lp = getWindow().getAttributes();
       // lp.flags=WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
       
        lp.x=x;
        lp.y=y;
        lp.dimAmount=0;
      //  lp.gravity=Gravity.TOP|Gravity.LEFT;
        getWindow().setAttributes(lp);  
          
    //    getWindow().addFlags(LayoutParams.FLAG_BLUR_BEHIND);// ���ģ��Ч��   
          
        // ����͸���ȣ��Ի���͸��(�����Ի����е�����)alpha��0.0f��1.0f֮�䡣1.0��ȫ��͸����0.0f��ȫ͸��   
        // lp.alpha = 0.5f;   
  
        lp.dimAmount = 0.1f;// ���öԻ�����ʾʱ�ĺڰ��ȣ�0.0f��1.0f֮�䣬�����������ó�0.0f����ֺ���״̬����⡣   
   
    }  
  
    /** 
     * ���¼���ָ����Activity�� 
     *  
     * @param activity 
     */  
    public void bindEvent(Activity activity) {  
  
        setOwnerActivity(activity);// )�ѶԻ����ŵ�һ��Activity��   
  
       
    }  
}
