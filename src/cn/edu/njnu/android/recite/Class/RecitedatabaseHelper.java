package cn.edu.njnu.android.recite.Class;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cn.edu.njnu.android.recite.R;

public class RecitedatabaseHelper {

		//生成数据库表
//		private String Create_table="create table article("
//		+"id integer primary key autoincrement,"
//		+"title text,"
//		+"content text)"; 
	
		//数据库保存路径
		private static String DATABASE_PATH="/data/data/cn.edu.njnu.android.recite/databases";		
		private static final String DATABASE_NAME = "test.db";
		private static final int DATABASE_VERSION = 0;
		private static String outFileName = DATABASE_PATH + "/" + DATABASE_NAME;
		
		private Context mContext;		
		
		private SQLiteDatabase db;
		//构造函数
		public RecitedatabaseHelper(Context context) {
		
			mContext=context;
			File file=new File(outFileName);
			if(file.exists())
			{
			db=SQLiteDatabase.openOrCreateDatabase(outFileName, null);
				if(db.getVersion()!=DATABASE_VERSION)
				{
					db.close();
					file.delete();
				}			
			}
			try{
				buildDatabase();
			}catch (Exception e) {
				e.printStackTrace();
			}
	
		}

		
		private void buildDatabase() throws Exception {
			InputStream input=mContext.getResources().openRawResource(R.raw.article);
			File file=new File(outFileName);
			
			File dir=new File(DATABASE_PATH);
			if(!dir.exists())
			{
				if(!dir.mkdir())
				{
					throw new Exception("创建失败");
				}
			}
			if(!file.exists())
			{
				try{
				OutputStream out=new FileOutputStream(outFileName);
				byte[] buffer=new byte[100];
				int length;
				while((length=input.read(buffer))>0)
				{
					out.write(buffer,0,length);
				}
				out.close();
				input.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		}
public Cursor Loaddata()
{
	db=SQLiteDatabase.openOrCreateDatabase(outFileName, null);
	Cursor cursor=db.query("article", null, null, null, null, null, null);
	return cursor;
}


}
