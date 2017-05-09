package cn.edu.njnu.android.recite.Adpter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.edu.njnu.android.recite.R;
import cn.edu.njnu.android.recite.Class.Phonetic;

public class IndexAdapter extends BaseAdapter {

	private ArrayList<String> index;
	private Context context;
	
	final class ViewHolder{
		public TextView title;
		public TextView content;
	}
	

	public IndexAdapter(Context context,ArrayList<String> index) {
		this.index=index;
		this.context=context;
	}
	
	//����list
	public void updateListView(ArrayList<String> list){  
        this.index =  list;  
        notifyDataSetChanged();  
    }  
	
	
	@Override
	public int getCount() {
		return this.index.size();
	}

	@Override
	public Object getItem(int arg0) {
		return this.index.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	   /*
	    * ���������һЩ��Ҫ�õ�Adapter�Զ��ؼ���ʾ��ʽ��ʱ��ǳ�����
	    *Adapter �и�getView����������ʹ��setTag�Ѳ��ҵ�view������������������
	    *View�е�setTag��Onbect����ʾ��View���һ����������ݣ��Ժ������getTag()���������ȡ������
	    *
	    */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if(convertView==null){
			//��context�л��һ������������������Ϳ��԰�xml�����ļ�����view������
			convertView=LayoutInflater.from(context).inflate(R.layout.menu_left_listitem, null);
			//�൱���ǻ��棬���������е�һЩview���뵽��vh�У���֮��ʹ��ʱ����ֱ����vh
			vh=new ViewHolder();			
			vh.title=(TextView)convertView.findViewById(R.id.item_title);
			vh.content=(TextView)convertView.findViewById(R.id.item_content);
			convertView.setTag(vh);	
		}
		else vh=(ViewHolder)convertView.getTag();
				
		vh.content.setText(index.get(position));
		
		//��������ǵ�һ��ƴ�������������һ�����ֵ�ƴ������ĸһ��������ʾtitle���������ʾ
		if(position!=0&&
		Phonetic.ToPhonetic(index.get(position).substring(0,1))
											.equals(Phonetic.ToPhonetic(index.get(position-1).substring(0,1))))
			vh.title.setVisibility(View.GONE);
		else{
			vh.title.setText(Phonetic.ToPhonetic(index.get(position).substring(0,1)));
			vh.title.setVisibility(View.VISIBLE);
		}
		return convertView;
	}
	
}
