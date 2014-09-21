package my.netstudio.mycentennial;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnTouchListener;
import android.widget.ViewFlipper;

public class Guidance extends Activity  implements OnTouchListener{

	private ViewFlipper mViewFlipper;  
    private GestureDetector mGestureDetector;  
    Guidance pactivity=null;
    private PageControlView mIndicateView = null;
    class MyGestureListener extends SimpleOnGestureListener {


        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                float velocityY) {
            // TODO Auto-generated method stub
        	if (e2.getX() - e1.getX() > 100) {  
        		
                mViewFlipper.showPrevious();  
                mIndicateView.setIndication(mViewFlipper.getChildCount(), mViewFlipper.getDisplayedChild());
            } else if(e2.getX() - e1.getX() < -100){  
            	if(mViewFlipper.getDisplayedChild()==mViewFlipper.getChildCount()-1){
            		Intent intent=new Intent(Guidance.this,Login.class);
            		Guidance.this.startActivity(intent);
            		pactivity.finish();
            	}
            	else{
            		
            		mViewFlipper.showNext();  
            		mIndicateView.setIndication(mViewFlipper.getChildCount(), mViewFlipper.getDisplayedChild());
            	}
            }  
        	return true;
        }

       
    }

    
	public void onCreate(Bundle savedInstanceState) {
		//	this.bindService(new Intent(this,EService.class),conn,BIND_AUTO_CREATE);
        
		
        if(ecentennial.ReadsSysFile(this.getApplicationContext(),"Account").length()>0){
        	Intent intent=new Intent(Guidance.this,Login.class);
        	this.startActivity(intent);
			finish();
			
			
        }
        
        
		pactivity=this;
		super.onCreate(savedInstanceState);  
		requestWindowFeature(Window.FEATURE_NO_TITLE);   
		getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);  
		
        setContentView(R.layout.guidance);  
  
        mGestureDetector =  new GestureDetector(new MyGestureListener());
        mViewFlipper = (ViewFlipper) findViewById(R.id.viewflipper);  
        mViewFlipper.setOnTouchListener(this);  
        
        
        mIndicateView = (PageControlView) findViewById(R.id.what_news_page_control);
        mIndicateView.setIndication(mViewFlipper.getChildCount(), 0);
        
 //       mViewFlipper.startFlipping();  
	}
	
	

	public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        return mGestureDetector.onTouchEvent(event);
    }



	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
}
