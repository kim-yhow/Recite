package cn.edu.njnu.android.recite.Class;

public class Article {
	private final static int POEM=1;
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
		switch(this.type){
		case POEM:
			char tmp;
			int countmp=0;
			for(int i=0;i<content.length();i++){
				tmp=content.charAt(i);
				countmp++;
				if(tmp=='。'||tmp=='？'||tmp=='！'||tmp=='.'||tmp=='!'||tmp=='?'||countmp==8)
					{
						countmp=0;	
						this.content=this.content+String.valueOf(tmp)+"\n";						
					}				
				else this.content+=String.valueOf(tmp);
				if(tmp==','||tmp=='，') countmp=0;
			}
			break;
		case ESSAY:
			this.content=content;
			break;
		default:
			break;
		}
		
		
	}
	
}
