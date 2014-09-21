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
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 灭掉对话框标题，要放在setContentView前面否则会报错   
 
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
        		
        setCanceledOnTouchOutside(true);// 点击对话框外部取消对话框显示

        LayoutParams lp = getWindow().getAttributes();
       // lp.flags=WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
       
        lp.x=x;
        lp.y=y;
        lp.dimAmount=0;
      //  lp.gravity=Gravity.TOP|Gravity.LEFT;
        getWindow().setAttributes(lp);  
          
    //    getWindow().addFlags(LayoutParams.FLAG_BLUR_BEHIND);// 添加模糊效果   
          
        // 设置透明度，对话框透明(包括对话框中的内容)alpha在0.0f到1.0f之间。1.0完全不透明，0.0f完全透明   
        // lp.alpha = 0.5f;   
  
        lp.dimAmount = 0.1f;// 设置对话框显示时的黑暗度，0.0f和1.0f之间，在我这里设置成0.0f会出现黑屏状态，求解。   
   
    }  
  
    /** 
     * 绑定事件到指定的Activity上 
     *  
     * @param activity 
     */  
    public void bindEvent(Activity activity) {  
  
        setOwnerActivity(activity);// )把对话框附着到一个Activity上   
  
       
    }  
}
