package my.netstudio.mycentennial;

import java.util.List;

import my.netstudio.mycentennial.MainActivity.NotifyMsgItem;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class SetupDlg extends Activity implements OnItemClickListener{
	public void onCreate(Bundle savedInstanceState) {
		//	this.bindService(new Intent(this,EService.class),conn,BIND_AUTO_CREATE);
			
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.setup);
	        
	        
	        ListView setuplist=(ListView) this.findViewById(R.id.setuplist);
	        setuplist.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	        setuplist.setAdapter(new SetupItems());
	 //       setuplist.setSelector(getResources().getDrawable(R.drawable.listitem));
	   //     setuplist.setOnTouchListener(this);
	        setuplist.setOnItemClickListener(this);
	        
	        
	        
	}
	
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
	//		arg1.setBackgroundColor(getResources().getColor(R.color.selected));
			if(arg2==1)//update
			{
		//		DialogInterface.OnClickListener p=
				new AlertDialog.Builder(this).setTitle("Updating Cycle").
						setIcon(android.R.drawable.ic_dialog_info).
					     setSingleChoiceItems(new String[] { "20 mins","40 mins","60 mins" }, EService.gconfig.updatecycle/20-1,
					    		 new DialogInterface.OnClickListener() {
					    	 public void onClick(DialogInterface dialog, int which) {
					    		 EService.gconfig.updatecycle=20*(which+1);
					    		 EService.saveConfig();
					    		 dialog.dismiss();
					    	 }
					   }).setNegativeButton("Cancel", null).show();
			
			}
			else if(arg2==3){
				EService.gconfig.is3gdata=!((CheckBox)arg1.findViewById(R.id.CheckBox01)).isChecked();
				((CheckBox)arg1.findViewById(R.id.CheckBox01)).setChecked(EService.gconfig.is3gdata);
				EService.saveConfig();
			}
			else if(arg2==5){
				EService.gconfig.hidehistorydata=!((CheckBox)arg1.findViewById(R.id.CheckBox01)).isChecked();
				((CheckBox)arg1.findViewById(R.id.CheckBox01)).setChecked(EService.gconfig.hidehistorydata);
				EService.saveConfig();
			}
			else if(arg2==9){
				AlertDialog.Builder builder = new AlertDialog.Builder(this);  
		        builder.setMessage(getString(R.string.resetstr))  
		               .setCancelable(false)  
		               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {  
		                   public void onClick(DialogInterface dialog, int id) {  
		                	   EService.reset();
		                	   dialog.dismiss();
		                	   
		                	   Intent intent=new Intent(SetupDlg.this,Login.class);
		                	   startActivity(intent);   
		                	   finish();
		                   }  
		               }).setNegativeButton("No", new DialogInterface.OnClickListener() {  
		                   public void onClick(DialogInterface dialog, int id) {  
		                	   dialog.dismiss();
		                   }  
		               });  
		               
		        
				
				builder.create().show();
				
			}
			else if(arg2==7){
				AlertDialog.Builder builder = new AlertDialog.Builder(this);  
		        builder.setMessage(getString(R.string.backupstr))  
		               .setCancelable(false)  
		               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {  
		                   public void onClick(DialogInterface dialog, int id) {  
		                	   EService.backup();
		                	   dialog.dismiss();
		                	   Toast.makeText(EService.gContent,getString(R.string.backupstrfinish), Toast.LENGTH_LONG).show();
		                   }  
		               }).setNegativeButton("No", new DialogInterface.OnClickListener() {  
		                   public void onClick(DialogInterface dialog, int id) {  
		                	   dialog.dismiss();
		                   }  
		               });  
		        
				builder.create().show();
			}
			else if(arg2==8){
				AlertDialog.Builder builder = new AlertDialog.Builder(this);  
		        builder.setMessage(getString(R.string.restorestr))  
		               .setCancelable(false)  
		               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {  
		                   public void onClick(DialogInterface dialog, int id) {  
		                	   EService.restore();
		                	   dialog.dismiss();
		                	   Toast.makeText(EService.gContent,getString(R.string.restorestrfinish), Toast.LENGTH_LONG).show();

		                   }  
		               }).setNegativeButton("No", new DialogInterface.OnClickListener() {  
		                   public void onClick(DialogInterface dialog, int id) {  
		                	   dialog.dismiss();
		                   }  
		               });  
				builder.create().show();
			}
			else if(arg2==11){
				Uri uri= Uri.parse("mailto:gujg2010@gmail.com");
				Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
				this.startActivity(intent);
			}
	}
	
	class SetupItems  extends BaseAdapter{
    	LayoutInflater listitempane;
    	float lasttouchx=0;int lastsel=0;
    	SetupItems(){
    		
    		listitempane=LayoutInflater.from(SetupDlg.this);

    	}
		public int getCount() {
			// TODO Auto-generated method stub
			int retcount=12;
			
			return retcount;
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			Object Obj=null;
		
			return Obj;
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View p_view=null;
			String title="";
			switch(position){
				case 0:{
					title=getString(R.string.configtitle1);
					p_view=listitempane.inflate(R.layout.configitemtitle, null);
					((TextView)p_view.findViewById(R.id.itemtitle)).setText(title);
					break;
				}
				case 2:{
					title=getString(R.string.configtitle2);
					p_view=listitempane.inflate(R.layout.configitemtitle, null);
					((TextView)p_view.findViewById(R.id.itemtitle)).setText(title);
					break;
				}
				case 6:{
					title=getString(R.string.configtitle3);
					p_view=listitempane.inflate(R.layout.configitemtitle, null);
					((TextView)p_view.findViewById(R.id.itemtitle)).setText(title);
					break;
				}
				case 4:{
					title=getString(R.string.view);
					p_view=listitempane.inflate(R.layout.configitemtitle, null);
					((TextView)p_view.findViewById(R.id.itemtitle)).setText(title);
					break;
				}
				case 5:{
					title=getString(R.string.hidehistory);
					p_view=listitempane.inflate(R.layout.confignetwork, null);
					((CheckBox)p_view.findViewById(R.id.CheckBox01)).setFocusable(false);
					((TextView)p_view.findViewById(R.id.title)).setText(title);
					((CheckBox)p_view.findViewById(R.id.CheckBox01)).setChecked(EService.gconfig.hidehistorydata);
					break;
				}
				case 10:{
					title=getString(R.string.configtitle4);
					p_view=listitempane.inflate(R.layout.configitemtitle, null);
					((TextView)p_view.findViewById(R.id.itemtitle)).setText(title);
					break;
				}
				case 1:{
					
					p_view=listitempane.inflate(R.layout.configupdate, null);
					
					break;
				}
				case 3:{
					
					p_view=listitempane.inflate(R.layout.confignetwork, null);
					((CheckBox)p_view.findViewById(R.id.CheckBox01)).setChecked(EService.gconfig.is3gdata);
					break;
				}
				case 9:{
					
					p_view=listitempane.inflate(R.layout.configaccount, null);
					
					break;
				}
				case 7:{
					title=getString(R.string.backup);
					p_view=listitempane.inflate(R.layout.configaccount, null);
					((TextView)p_view.findViewById(R.id.title)).setText(title);
					break;
				}
				case 8:{
					title=getString(R.string.restore);
					p_view=listitempane.inflate(R.layout.configaccount, null);
					((TextView)p_view.findViewById(R.id.title)).setText(title);
					break;
				}
				case 11:{
					title=getString(R.string.author);
					p_view=listitempane.inflate(R.layout.configaccount, null);
					((TextView)p_view.findViewById(R.id.title)).setText(title);
					break;
				}
			}
			
			return p_view;
		}
    }
	public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return false;
    }
}
