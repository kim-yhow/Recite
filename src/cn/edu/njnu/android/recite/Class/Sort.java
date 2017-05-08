package cn.edu.njnu.android.recite.Class;

import java.util.Comparator;

public class Sort implements Comparator<String>{

	@Override
	public int compare(String lhs, String rhs) {
		for(int i=0;i<Math.min(lhs.length()-1, rhs.length());i++){
			char x=Phonetic.ToPhonetic(lhs.substring(i, i+1)).charAt(0);
			char y=Phonetic.ToPhonetic(rhs.substring(i, i+1)).charAt(0);
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