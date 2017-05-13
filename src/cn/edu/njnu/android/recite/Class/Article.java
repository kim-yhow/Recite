package cn.edu.njnu.android.recite.Class;

import android.util.Log;

public class Article {
	private final static int PEOM=1;
	private final static int ESSAY=2; 
	private String author="";
	private String titile="";
	private String content="";
	private int type;	
	
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	public String getTitile() {
		return titile;
	}
	public void setTitile(String titile) {
		this.titile = titile;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		char tmp;
		for(int i=0;i<content.length();i++)
		{
			tmp=content.charAt(i);
		
			if(tmp=='。'||tmp=='？'||tmp=='！'||tmp=='.'||tmp=='!'||tmp=='?')
			{
				this.content=this.content+String.valueOf(tmp)+"\n";
			}
			else
				this.content+=String.valueOf(tmp);
		}
		
	}
	
}
