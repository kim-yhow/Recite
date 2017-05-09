package cn.edu.njnu.android.recite.View;

import cn.edu.njnu.android.recite.R;
import cn.edu.njnu.android.recite.Activity.MainActivity;
import cn.edu.njnu.android.recite.Class.Phonetic;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class SelectAlphaView extends View implements View.OnTouchListener{

	public String[] alpha= { "A", "B", "C", "D", "E", "F", "G", "H", "I",  
        "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",  
        "W", "X", "Y", "Z", "#" };
	private Paint paint;
	private int y=0;
	public int position=-1;
	private Context mContext;
	
	//
	public SelectAlphaView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext=context;
		paint=new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.WHITE);
		paint.setTextAlign(Align.CENTER);
		paint.setTextSize(35);		
		this.setOnTouchListener(this);
	}
	
	@Override	
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if(y==0) y=this.getHeight()/28;
		for(int i=0;i<alpha.length;i++){
			if(position!=-1&&i==position){
				paint.setColor(Color.RED);
				canvas.drawText(alpha[i], 30, 35+y*i, paint);
				paint.setColor(Color.WHITE);
			}
			else canvas.drawText(alpha[i], 30, 35+y*i, paint);
		}
		
	}

	//
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		setBackgroundResource(R.drawable.sidebar_background);
		float py=event.getY();
		position=(((int)py)-((int)py)%y)/y;
		
		MainActivity mav=(MainActivity)mContext;
		ListView lv=(ListView)mav.findViewById(R.id.menu_left_lv);
		TextView tv=(TextView)mav.findViewById(R.id.menu_left_toast);
		
		
		lv.setSelection(getMatch(lv,position));
		tv.setText(alpha[position]);
		tv.setVisibility(View.VISIBLE);
		lv.setClickable(false);
		
		if(event.getAction()==MotionEvent.ACTION_UP){
			setBackground(new ColorDrawable(0x00000000));
			lv.setClickable(true);
			tv.setVisibility(View.GONE);
		}
	
		invalidate();
		return true;
	}


	private int getMatch(ListView lv,int pos){
		String str="";
		if(pos==lv.getAdapter().getCount()) return 0;
		for(int i=0;i<lv.getAdapter().getCount();i++){
			str=Phonetic.ToPhonetic(String.valueOf(lv.getAdapter().getItem(i)).substring(0,1));
			if(str.equals(alpha[pos])) return i;
		}
		return getMatch(lv,pos+1);
	}
	
	
}
