package cn.edu.njnu.android.recite.Activity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;

import cn.edu.njnu.android.recite.R;
import cn.edu.njnu.android.recite.Adpter.IndexAdapter;
import cn.edu.njnu.android.recite.Class.*;
import cn.edu.njnu.android.recite.View.SelectAlphaView;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {
	private DrawerLayout drawerLayout;
	private ActionBarDrawerToggle toggle;
	private View right_sliding;
	private ActionBar actionBar;
	private ArrayList<String> index;
	private ListView lv;
	private TextView toast;
	private SelectAlphaView sav;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initActionBar();
		initDrawerLayout();
		
		lv=(ListView)findViewById(R.id.menu_left_lv);
		toast=(TextView)findViewById(R.id.menu_left_toast);
		sav=(SelectAlphaView)findViewById(R.id.menu_left_sav);
		
		lv.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(scrollState==SCROLL_STATE_TOUCH_SCROLL){
					int pos=lv.getFirstVisiblePosition();
					String str=Phonetic.ToPhonetic(String.valueOf(lv.getAdapter().getItem(pos)).substring(0,1));
					for(int i=0;i<sav.alpha.length;i++){
						if(sav.alpha[i].equals(str)){
							sav.position=i;
							sav.invalidate();
							break;
						}
					}
				}
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				
				
			}
		});
		
		
		index=new ArrayList<String>();
		Test();
		Sort s=new Sort();
		Collections.sort( index, s);
		lv.setAdapter(new IndexAdapter(this, index));
	}

	@SuppressLint("NewApi")
	private void initActionBar(){
		actionBar=super.getActionBar();
		actionBar.show();
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeAsUpIndicator(R.drawable.com_btn);
		
		//È¥³ýÄ¬ÈÏµÄICONÍ¼±ê
		 Drawable colorDrawable=new 
				 ColorDrawable(android.R.color.transparent);
		actionBar.setIcon(colorDrawable);
		actionBar.setDisplayShowCustomEnabled(true);
		TextView tvTitle=new TextView(this);
		tvTitle.setText("Ö÷  Ò³");
		tvTitle.setTextColor(Color.WHITE);
		tvTitle.setTextSize(18);
		tvTitle.setGravity(Gravity.CENTER);
		LayoutParams params=new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		tvTitle.setLayoutParams(params);
		actionBar.setCustomView(tvTitle);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	private void initDrawerLayout(){
		drawerLayout=(DrawerLayout)super.findViewById(R.id.drawer_layout);
		drawerLayout.setScrimColor(Color.TRANSPARENT);
		right_sliding=super.findViewById(R.id.right_sliding);
		toggle=new ActionBarDrawerToggle(this,drawerLayout,
				R.drawable.back_move_details_normal,R.string.drawer_open
				,R.string.drawer_close){

					@Override
					public void onDrawerClosed(View drawerView) {
						// TODO Auto-generated method stub
						super.onDrawerClosed(drawerView);
					}

					@Override
					public void onDrawerOpened(View drawerView) {
						// TODO Auto-generated method stub
						super.onDrawerOpened(drawerView);
					}
			
		};
		drawerLayout.setDrawerListener(toggle);
		//drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
		//drawerLayout.openDrawer(right_sliding);
		
		
	} 
	
	private void toggleLeftSliding(){
		if(drawerLayout.isDrawerOpen(Gravity.START)){
			drawerLayout.closeDrawer(Gravity.START);
		}else{
			drawerLayout.openDrawer(Gravity.START);
		}
	}
	private void toggleRightSliding(){
		if(drawerLayout.isDrawerOpen(Gravity.END)){
			drawerLayout.closeDrawer(Gravity.END);
		}else{
			drawerLayout.openDrawer(Gravity.END);
		}
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case android.R.id.home:
			toggleLeftSliding();
			break;
		case R.id.usersetting:
			toggleRightSliding();
			break;
		}
			
		return super.onOptionsItemSelected(item);
	}

	/**
	 * ÊµÏÖoverflow²Ëµ¥Ïî´øICON
	 */
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {  
	    if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {  
	        if (menu.getClass().getSimpleName().equals("MenuBuilder")) {  
	            try {  
	                Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);  
	                m.setAccessible(true);  
	                m.invoke(menu, true);  
	            } catch (Exception e) {  
	            }  
	        }  
	    }  
	    return super.onMenuOpened(featureId, menu);  
	}

	public TextView getToast() {
		return toast;
	}

	public void Test(){
		index.add("ÕÅ·É");
		index.add("¹ØÓð");
		index.add("Áõ±¸");
		index.add("ÕÅÁÉ");
		index.add("²Ü²Ù");
		index.add("ËïÈ¨");
		index.add("Áõ±í");
		index.add("¶­×¿");
		index.add("ÂÀ²¼");
		index.add("µõËÀ¹í");
		index.add("Öî¸ðÁÁ");
		index.add("Ë¾ÂíÜ²");
		index.add("ÕÔÔÆ");
		index.add("°¢¹ÈÎÝ");
		index.add("±Ï¸£½£");
		index.add("²ÜÑ©ÇÛ");
		index.add("µÒÈÊ½Ü");
		index.add("ÖÜè¤");
		index.add("¿ÂÄÏ");
		index.add("Âí³¬");
		index.add("Ëï²ß");
		index.add("Ì«Ê·´È");
		index.add("·þ²¿°ë²Ø");
		index.add("×ô×ôÄ¾Ð¡´ÎÀÉ");
		index.add("°Â°ÍÂí");
		index.add("ÒÁ¿¨ÂåË¹");
		
	}

	
	
}
