package cn.edu.njnu.android.recite.Class;

import java.util.Comparator;

public class Sort implements Comparator<Article>{

	@Override
	public int compare(Article lhs, Article rhs) {
		for(int i=0;i<Math.min(lhs.getTitile().length()-1, rhs.getTitile().length());i++){
			char x=Phonetic.ToPhonetic(lhs.getTitile().substring(i, i+1)).charAt(0);
			char y=Phonetic.ToPhonetic(rhs.getTitile().substring(i, i+1)).charAt(0);
			if(x==y) continue;
			else if(x>y) return 1;
			else return -1;
		}
		return 0;
	}
	
	public Sort() {
		// TODO Auto-generated constructor stub
	}
	
}