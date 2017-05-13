package cn.edu.njnu.android.recite.Adpter;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.edu.njnu.android.recite.R;
import cn.edu.njnu.android.recite.Class.Article;
import cn.edu.njnu.android.recite.Class.Phonetic;

public class IndexAdapter extends BaseAdapter {

	private ArrayList<Article> index;
	private Context context;
	
	final class ViewHolder{
		public TextView Letter;
		public TextView ArticleTitle;
	}
	

	public IndexAdapter(Context context,ArrayList<Article> index) {
		this.index=index;
		this.context=context;
	}
	
	//更新list
	public void updateListView(ArrayList<Article> list){  
        this.index =  list;  
        notifyDataSetChanged();  
    }  
	
	
	@Override
	public int getCount() {
		return this.index.size();
	}

	@Override
	public Article getItem(int arg0) {
		return this.index.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	   /*
	    * 这个东西在一些需要用到Adapter自定控件显示方式的时候非常有用
	    *Adapter 有个getView方法，可以使用setTag把查找的view缓存起来方便多次重用
	    *View中的setTag（Onbect）表示给View添加一个格外的数据，以后可以用getTag()将这个数据取出来。
	    *
	    */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if(convertView==null){
			//从context中获得一个布局填充器，这样就可以把xml布局文件换成view对象了
			convertView=LayoutInflater.from(context).inflate(R.layout.menu_left_listitem, null);
			//相当于是缓存，将适配器中的一些view加入到了vh中，在之后使用时可以直接用vh
			vh=new ViewHolder();			
			vh.Letter=(TextView)convertView.findViewById(R.id.item_title);
			vh.ArticleTitle=(TextView)convertView.findViewById(R.id.item_content);
			convertView.setTag(vh);	
		}
		else vh=(ViewHolder)convertView.getTag();
			
		vh.ArticleTitle.setText(index.get(position).getTitile());
		//如果不是是第一个拼音，且这个和上一个汉字的拼音首字母一样，则不显示title，否则就显示
		if(position!=0&&
		Phonetic.ToPhonetic(index.get(position).getTitile().substring(0,1))
											.equals(Phonetic.ToPhonetic(index.get(position-1).getTitile().substring(0,1))))
			vh.Letter.setVisibility(View.GONE);
		else{
			Log.d("article",index.get(position).getTitile().substring(0,1));
			vh.Letter.setText(Phonetic.ToPhonetic(index.get(position).getTitile().substring(0,1)));
			vh.Letter.setVisibility(View.VISIBLE);
		}
		return convertView;
	}
	
}
