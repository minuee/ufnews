package com.ufnews;

public class check_existence_reg_id {
	String red_id;
	
	public check_existence_reg_id(String red_id) {		
		this.red_id = red_id;
	}
	
	public boolean check(){
		System.out.println("length = " + red_id.length());
		if(red_id.length() < 6){
			return false;
		}else{
			return true;
		}
	}
}
