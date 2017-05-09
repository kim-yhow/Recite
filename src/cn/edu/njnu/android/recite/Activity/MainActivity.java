package cn.edu.njnu.android.recite.Activity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;

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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.edu.njnu.android.recite.R;
import cn.edu.njnu.android.recite.Adpter.IndexAdapter;
import cn.edu.njnu.android.recite.Class.Phonetic;
import cn.edu.njnu.android.recite.Class.Sort;
import cn.edu.njnu.android.recite.View.SearchTextView;
import cn.edu.njnu.android.recite.View.SelectAlphaView;

public class MainActivity extends Activity {
	private DrawerLayout drawerLayout;
	private ActionBarDrawerToggle toggle;
	private View right_sliding;
	private ActionBar actionBar;
	private ArrayList<String> mDataList;
	private ListView lv;
	private TextView toast;
	private SelectAlphaView sav;
	private SearchTextView mSearch;
	private IndexAdapter mAdapter;
	private Sort mSortOfPhonetic=new Sort();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initActionBar();
		initDrawerLayout();
		
		lv=(ListView)findViewById(R.id.menu_left_lv);
		toast=(TextView)findViewById(R.id.menu_left_toast);
		sav=(SelectAlphaView)findViewById(R.id.menu_left_sav);	
		mSearch=(SearchTextView)findViewById(R.id.menu_left_stv);
		
		//搜索框的监听函数		
		mSearch.addTextChangedListener(new TextWatcher(){

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			//搜索框变换监听
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				//删选字符
				filterSource(s.toString());
				
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		//listView的滑动监听
		lv.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				//手接触ScrollView触发一次或者进行滑动，或者停下时均进行判断
				if(scrollState==SCROLL_STATE_FLING ||scrollState==SCROLL_STATE_TOUCH_SCROLL ||scrollState==SCROLL_STATE_IDLE ){
					//listView第一个可见项
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
		
        //listView的项目点击监听，一旦点击 便会调用重载的OnItemClik函数，具体操作可以在此处填写
		lv.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			Toast.makeText(getApplication(),(String)mAdapter.getItem(position) , Toast.LENGTH_SHORT).show();
				
			}
		});
				
		mDataList=new ArrayList<String>();
		Test();
		//比较规则
		
		//Collection.sort方法参数1 需要比较的内容，参数2 比较规则
		Collections.sort(mDataList,mSortOfPhonetic);
		mAdapter=new IndexAdapter(this, mDataList);
		lv.setAdapter(mAdapter);
	}

	//用于过滤函数
	protected void filterSource(String str) {
		ArrayList<String> fiterData=new ArrayList<String>();
		if(TextUtils.isEmpty(str))
		{
			fiterData=mDataList;
		}
		else  {
			fiterData.clear();
			//java list遍历 for(数据类型 自定义参数  需要遍历的list)
			for(String data:mDataList)
			{
				if(data.toUpperCase().indexOf(str.toUpperCase())!=-1 ||
						(Phonetic.ToPhonetic(data.substring(0,1))).toUpperCase().equals(str.toUpperCase())  )
				{
					fiterData.add(data);
				}
			}
			}
		Collections.sort(fiterData,mSortOfPhonetic);
		mAdapter.updateListView(fiterData);;
		}
		
	

	@SuppressLint("NewApi")
	private void initActionBar(){
		actionBar=super.getActionBar();
		actionBar.show();
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeAsUpIndicator(R.drawable.com_btn);
		
		//去除默认的ICON图标
		 Drawable colorDrawable=new 
				 ColorDrawable(android.R.color.transparent);
		actionBar.setIcon(colorDrawable);
		actionBar.setDisplayShowCustomEnabled(true);
		TextView tvTitle=new TextView(this);
		tvTitle.setText("主  页");
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
	 * 实现overflow菜单项带ICON
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
		mDataList.add("张飞");
		mDataList.add("关羽");
		mDataList.add("刘备");
		mDataList.add("张辽");
		mDataList.add("曹操");
		mDataList.add("孙权");
		mDataList.add("刘表");
		mDataList.add("董卓");
		mDataList.add("吕布");
		mDataList.add("吊死鬼");
		mDataList.add("诸葛亮");
		mDataList.add("司马懿");
		mDataList.add("赵云");
		mDataList.add("阿谷屋");
		mDataList.add("毕福剑");
		mDataList.add("曹雪芹");
		mDataList.add("狄仁杰");
		mDataList.add("周瑜");
		mDataList.add("柯南");
		mDataList.add("马超");
		mDataList.add("孙策");
		mDataList.add("太史慈");
		mDataList.add("服部半藏");
		mDataList.add("佐佐木小次郎");
		mDataList.add("奥巴马");
		mDataList.add("伊卡洛斯");
		
	}

	
	
}
