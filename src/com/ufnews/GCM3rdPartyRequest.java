package com.ufnews;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GCM3rdPartyRequest extends Thread {
	String mAddr;
	String id;
	String mResult;
	String pw;
	String reg_id;
	String msg;
	
	public GCM3rdPartyRequest()	{
		
	}
	
	public void Setting(String addr, String id, String pw, String reg_id, String msg){
		mAddr=addr;
		this.id=id;
		this.pw=pw;
		mResult="";
		this.reg_id=reg_id;
		this.msg = msg;
	}
	
	@Override
	public void run() {
	
		super.run();
		StringBuilder html=new StringBuilder();
		try {
				URL url = new URL(mAddr);
				HttpURLConnection conn=(HttpURLConnection)url.openConnection();
	
				conn.setDefaultUseCaches(false); 
				conn.setDoInput(true); // 
				conn.setDoOutput(true); // 
				conn.setRequestMethod("POST");
	
				conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");
	
				StringBuffer buffer = new StringBuffer();
				buffer.append("id").append("=").append(id).append("&");
				buffer.append("pw").append("=").append(pw).append("&");
				buffer.append("reg_id").append("=").append(reg_id).append("&");
				buffer.append("msg").append("=").append(msg);
	
				PrintWriter pw = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
				pw.write(buffer.toString());
				pw.flush();
	
				BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
	
				while(true){
						String line=br.readLine();
						if(line==null) break;
						html.append(line);
				}
				br.close();
				mResult=html.toString();
	
		
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//run
	
	public String getPort(){
	return mResult;
	
	}
 
}