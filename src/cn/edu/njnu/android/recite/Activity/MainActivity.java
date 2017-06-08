package cn.edu.njnu.android.recite.Activity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.Layout.Alignment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.edu.njnu.android.recite.R;
import cn.edu.njnu.android.recite.Adpter.IndexAdapter;
import cn.edu.njnu.android.recite.Class.Article;
import cn.edu.njnu.android.recite.Class.Phonetic;
import cn.edu.njnu.android.recite.Class.RecitedatabaseHelper;
import cn.edu.njnu.android.recite.Class.Sort;
import cn.edu.njnu.android.recite.View.SearchTextView;
import cn.edu.njnu.android.recite.View.SelectAlphaView;

import com.iflytek.cloud.EvaluatorListener;
import com.iflytek.cloud.EvaluatorResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvaluator;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.ise.result.Result;
import com.iflytek.ise.result.xml.XmlResultParser;
import com.iflytek.sunflower.FlowerCollector;

public class MainActivity extends Activity implements View.OnClickListener{
	//数据库接口
	private RecitedatabaseHelper dbHelper;	
	private DrawerLayout drawerLayout;
	private ActionBarDrawerToggle toggle;
	private View right_sliding;
	private ActionBar actionBar;
	private ArrayList<Article> mDataList;
	private ListView lv;
	private TextView toast,mRes;
	private SelectAlphaView sav;
	private IndexAdapter mAdapter;
	private Sort mSortOfPhonetic=new Sort();
	private TextView main_context,mHint;
	private SearchTextView mSearch;
	private Button bt_start,bt_end;
	
	//提示条
	private Toast mToast;
	
	//检测评测是否结束
	private String mTip;	
	private static String TAG = MainActivity.class.getSimpleName();
	private final static int REQUEST_CODE_SETTINGS = 1;
	private boolean mFirstClick=false;
	private Result mResult=new Result();
	private String mLastResult;
	private SpeechEvaluator mIse;
	// 评测语种
	private String language;
	// 评测题型
	private String category;
	// 结果等级
	private String result_level;
	private final static String PREFER_NAME = "ise_settings";
	// 评测监听接口
	private EvaluatorListener mEvaluatorListener = new EvaluatorListener() {
		
		@Override
		public void onResult(EvaluatorResult result, boolean isLast) {
			Log.d(TAG, "evaluator result :" + isLast);
			if (isLast) {
				StringBuilder builder = new StringBuilder();
				builder.append(result.getResultString());
				
				if(!TextUtils.isEmpty(builder)) {
					
				}
				mLastResult = builder.toString();
				
				showTip("评测结束");
			}
		}

		@Override
		public void onError(SpeechError error) {
			if(error != null) {	
				showTip("error:"+ error.getErrorCode() + "," + error.getErrorDescription());
				mHint.setText("");
				mHint.setHint("请点击“开始评测”按钮");
			} else {
				Log.d(TAG, "evaluator over");
			}
		}

		@Override
		public void onBeginOfSpeech() {
			// 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
			Log.d(TAG, "evaluator begin");
		}

		@Override
		public void onEndOfSpeech() {
			// 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
			Log.d(TAG, "evaluator stoped");
		}

		@Override
		public void onVolumeChanged(int volume, byte[] data) {
			showTip("当前音量：" + volume);
			Log.d(TAG, "返回音频数据："+data.length);
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
			// 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
			//	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
			//		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
			//		Log.d(TAG, "session id =" + sid);
			//	}
		}
	
	};
	
	//主线程更改UI
	private Handler hd=new Handler(){
		@Override
		public void dispatchMessage(Message msg) {
			if(msg.what==0){
				hd.removeMessages(0);
				bt_end.setEnabled(true);
				mFirstClick=true;
				bt_end.setText("结果分析");
				mHint.setHint("分析结束，点击结果分析查看结果~");
			}
			else if(msg.what==1){
				hd.removeMessages(0);
				bt_end.setEnabled(true);
				mFirstClick=false;
				bt_end.setText("停止评测");
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		//创建数据库
		initDB();
		DisplayMetrics DM=new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(DM);
		setContentView(R.layout.activity_main);	
		//初始化标题栏
		initActionBar();
		initDrawerLayout();
		//加载所有View
		LoadView();		
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
				
				view.setVisibility(View.VISIBLE);
				//标题
				String titmp=mAdapter.getItem(position).getTitile().replace("\\n", "\n");
				//内容
				String contmp=mAdapter.getItem(position).getContent().replace("\\n", "\n");
				showTip(mAdapter.getItem(position).getTitile());								
			
				main_context.setText(titmp+"\n"+contmp);
				bt_start.setEnabled(true);
				drawerLayout.closeDrawer(Gravity.START);
				mHint.setHint("点击开始评测，开始进行背诵~");
			}
		});
				
		mDataList=new ArrayList<Article>();
		
		//数据导入方法
		LoadData();
		
		//Collection.sort方法参数1 需要比较的内容，参数2 比较规则
		Collections.sort(mDataList,mSortOfPhonetic);
		mAdapter=new IndexAdapter(this, mDataList);
		
		lv.setAdapter(mAdapter);
		
		//更改TextView高度
		android.view.ViewGroup.LayoutParams lp=main_context.getLayoutParams();
		lp.height=(int) (0.4*DM.heightPixels);
		main_context.setLayoutParams(lp);
	
		//测试加入文字，设置垂直滚动条
		mHint.setHint("点击左上方的图标选择文章~");
		main_context.setMovementMethod(ScrollingMovementMethod.getInstance());
		mHint.setMovementMethod(ScrollingMovementMethod.getInstance());
		mRes.setMovementMethod(ScrollingMovementMethod.getInstance());
		//SDK初始化
		SpeechUtility.createUtility(this, "appid=58db4195");
		mIse=SpeechEvaluator.createEvaluator(MainActivity.this, null);
		bt_end.setOnClickListener(this);
		bt_start.setOnClickListener(this);

	}
	
	//加载view
	private void LoadView() {
		lv=(ListView)findViewById(R.id.menu_left_lv);
		toast=(TextView)findViewById(R.id.menu_left_toast);
		sav=(SelectAlphaView)findViewById(R.id.menu_left_sav);	
		mSearch=(SearchTextView)findViewById(R.id.menu_left_stv);
		main_context=(TextView)findViewById(R.id.ac_main_context);
		//将字体文件保存在assets/fonts/目录下，创建Typeface对象
		Typeface typeFace =Typeface.createFromAsset(getAssets(),"fonts/HWXW.ttf");
		//使用字体
		main_context.setTypeface(typeFace);
		
		
		bt_start=(Button)findViewById(R.id.ac_main_start);		
		bt_end=(Button)findViewById(R.id.ac_main_end);
		mHint=(TextView)findViewById(R.id.ac_main_result);
		mRes=(TextView)findViewById(R.id.ac_main_res);
		mToast = Toast.makeText(MainActivity.this, "", Toast.LENGTH_LONG);
		//设置按钮初始不可按
		bt_start.setEnabled(false);
		bt_end.setEnabled(false);
	}

	//用于初始化数据
	private void initDB() {
		dbHelper=new RecitedatabaseHelper(this);			
	}

	//用于过滤函数
	protected void filterSource(String str) {
		ArrayList<Article> fiterData=new ArrayList<Article>();
		if(TextUtils.isEmpty(str))
		{
			fiterData=mDataList;
		}
		else  {
			fiterData.clear();
			//java list遍历 for(数据类型 自定义参数  需要遍历的list)
			for(Article data:mDataList)
			{
				if(data.getTitile().toUpperCase().indexOf(str.toUpperCase())!=-1 ||
						(Phonetic.ToPhonetic(data.getTitile().substring(0,1))).toUpperCase().equals(str.toUpperCase())  )
				{
					fiterData.add(data);
				}
			}
			}
		Collections.sort(fiterData,mSortOfPhonetic);
		mAdapter.updateListView(fiterData);
		}
		

	@SuppressLint("NewApi")
	private void initActionBar(){
		actionBar=super.getActionBar();
		//设置左上角的按钮可以点击
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeAsUpIndicator(R.drawable.com_btn);

		actionBar.show();
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
	
		
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

	public Result getResult() {
		return mResult;
	}

	//加载数据库中的数据
	public void LoadData(){
	
		Cursor cursor=dbHelper.Loaddata();
		if(cursor.moveToFirst())
		{
			do				
			{
				Article article=new Article();
				article.setTitile(cursor.getString(cursor.getColumnIndex("title")));
				article.setType(cursor.getInt(cursor.getColumnIndex("type")));
				article.setContent(cursor.getString(cursor.getColumnIndex("content")));		
				article.setAuthor(cursor.getString(cursor.getColumnIndex("author")));
				Log.d("article", article.getTitile()+"   "+article.getContent());
				mDataList.add(article);
			}while(cursor.moveToNext());
		}
	}


	//other method
	private void showTip(String str) {
		if(!TextUtils.isEmpty(str)) {
			mToast.setText(str);
			mTip=str;
			mToast.show();
		}
	}
	
	private void setParams() {
		SharedPreferences pref = getSharedPreferences(PREFER_NAME, MODE_PRIVATE);
		// 设置评测语言
		language = pref.getString(SpeechConstant.LANGUAGE, "zh_cn");
		// 设置需要评测的类型
		category = pref.getString(SpeechConstant.ISE_CATEGORY, "read_sentence");
		// 设置结果等级（中文仅支持complete）
		result_level = pref.getString(SpeechConstant.RESULT_LEVEL, "complete");
		// 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
		String vad_bos = pref.getString(SpeechConstant.VAD_BOS, "5000");
		// 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
		String vad_eos = pref.getString(SpeechConstant.VAD_EOS, "1800");
		// 语音输入超时时间，即用户最多可以连续说多长时间；
		String speech_timeout = pref.getString(SpeechConstant.KEY_SPEECH_TIMEOUT, "-1");
		
		mIse.setParameter(SpeechConstant.LANGUAGE, language);
		mIse.setParameter(SpeechConstant.ISE_CATEGORY, category);
		mIse.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
		mIse.setParameter(SpeechConstant.VAD_BOS, vad_bos);
		mIse.setParameter(SpeechConstant.VAD_EOS, vad_eos);
		mIse.setParameter(SpeechConstant.KEY_SPEECH_TIMEOUT, speech_timeout);
		mIse.setParameter(SpeechConstant.RESULT_LEVEL, result_level);
		
		// 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
		// 注：AUDIO_FORMAT参数语记需要更新版本才能生效
		mIse.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
		mIse.setParameter(SpeechConstant.ISE_AUDIO_PATH, Environment.getExternalStorageDirectory().getAbsolutePath() + "/msc/ise.wav");
	}
	
	@Override
	protected void onResume() {
		// 开放统计 移动数据统计分析
		FlowerCollector.onResume(MainActivity.this);
		FlowerCollector.onPageStart(TAG);
		super.onResume();
	}
	
	// 设置评测试题
	private void setEvaText() {
		SharedPreferences pref = getSharedPreferences(PREFER_NAME, MODE_PRIVATE);
		language = pref.getString(SpeechConstant.LANGUAGE, "zh_cn");
		category = pref.getString(SpeechConstant.ISE_CATEGORY, "read_sentence");
		
		String text = "";
		if ("en_us".equals(language)) {
			
		} else {
			text="";
		}
		
		main_context.setText(text);
		mLastResult = null;
	}
	
	@Override
	protected void onPause() {
		// 开放统计 移动数据统计分析
		FlowerCollector.onPageEnd(TAG);
		FlowerCollector.onPause(MainActivity.this);
		super.onPause();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (REQUEST_CODE_SETTINGS == requestCode) {
			setEvaText();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if (null != mIse) {
			mIse.destroy();
			mIse = null;
		}
	}

	@Override
	public void onClick(View view) {
	
		if( null == mIse ){
			// 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
			this.showTip( "创建对象失败，请确认 libmsc.so 放置正确，且有调用 createUtility 进行初始化" );
			return;
		}
		
		switch (view.getId()) {
			case R.id.ac_main_start:
				main_context.setVisibility(View.INVISIBLE);
				if (mIse == null) {
					return;
				}
				if(mIse.isEvaluating()) break;
				
				String evaText = main_context.getText().toString();
				mLastResult = null;
				bt_end.setEnabled(true);;
				
				mHint.setText("");
				mHint.setHint("记住了么？开始背诵吧~");
				
				setParams();
				mIse.startEvaluating(evaText, null, mEvaluatorListener);
				break;
			case R.id.ac_main_end:
				main_context.setVisibility(View.VISIBLE);
				if(!mFirstClick){
					if(mIse.isEvaluating()) mIse.stopEvaluating();
					
					mHint.setHint("还在分析哦~");
					bt_end.setText("分析中……");
					bt_end.setEnabled(false);
					//计时器检查评测是否结束
					Timer t=new Timer();
					t.schedule(new TimerTask() {
						Message msg=new Message();
						@Override
						public void run() {
							if(mTip.equals("评测结束")){
								msg.what=0;
							}
							else if(mTip.startsWith("error")){
								msg.what=1;
							}
							hd.sendMessage(msg);
							this.cancel();
						}
					}, 500, 500);	
				}
				else{
					if(!TextUtils.isEmpty(mLastResult)) {
						XmlResultParser resultParser = new XmlResultParser();
						mResult = resultParser.parse(mLastResult);
						
						if(null==mResult) showTip("解析结果为空");
						else{
							mResult.content=main_context.getText().toString();
							mRes.setText(mResult.toString());
						}
					}
					//显示渐变的动画效果
					AlphaAnimation aa=new AlphaAnimation(0, 1);
					aa.setDuration(800);
					mRes.setVisibility(View.VISIBLE);
					mRes.setAnimation(aa);			
					mFirstClick=false;
					bt_end.setText("停止评测");
					mHint.setHint("点击开始评测，开始进行背诵~");
				}
				break;
		}
		
	}	
}
