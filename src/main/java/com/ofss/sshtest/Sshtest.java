package com.ofss.sshtest;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;


@RestController


public class Sshtest {
	
	
	@Value("${ssh.user}")
	private String user;
	@Value("${ssh.password}")
	private String password;
	
	@RequestMapping("/ssh/{hostName}/{cmd}")
	public String sshTest(@PathVariable String hostName, @PathVariable String cmd) {
	    String output ="";
	    
	    try{
	    	
	    	java.util.Properties config = new java.util.Properties(); 
	    	config.put("StrictHostKeyChecking", "no");
	    	JSch jsch = new JSch();
	    	Session session=jsch.getSession(user, hostName, 22);
	    	session.setPassword(password);
	    	session.setConfig(config);
	    	session.connect();
	    	Channel channel=session.openChannel("exec");
	        ((ChannelExec)channel).setCommand(cmd);
	        channel.setInputStream(null);
	        ((ChannelExec)channel).setErrStream(System.err);
	        
	        InputStream in=channel.getInputStream();
	        channel.connect();
	        byte[] tmp=new byte[1024];
	        while(true){
	          while(in.available()>0){
	            int i=in.read(tmp, 0, 1024);
	            if(i<0)break;
	            output = output + new String(tmp, 0, i);
	          }
	          if(channel.isClosed()){
	            output = output + "\n\n----------------------------\n" + channel.getExitStatus();
	            break;
	          }
	          try{Thread.sleep(1000);}catch(Exception ee){}
	        }
	        channel.disconnect();
	        session.disconnect();
	    }catch(Exception e){
	    	e.printStackTrace();
	    	output = output + e;
	    	
	    }
		
		return output;
		
	}

}
