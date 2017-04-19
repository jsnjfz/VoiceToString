package service;


import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.iflytek.voicecloud.client.LfasrClient;
import com.iflytek.voicecloud.exception.LfasrException;
import com.iflytek.voicecloud.model.LfasrType;
import com.iflytek.voicecloud.model.Message;

/**
 *
 *
 */
public class Convert extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        res.setCharacterEncoding("utf-8"); 
		String taskId = "";
        PrintWriter out = res.getWriter();  
		String appid = req.getParameter("appid");//"58ddb416";       //您的appid，如"5848d773"
		String secret_key = req.getParameter("secretkey");//"02d739f406931fd499a5266ffa129b3a";  //您的secret_key，如"b849c87a8bc2c7ww68b6dfbddee6dc35"
		//84bf7f5859fd40cd8c015b14217010fb
		String filename = req.getParameter("filename");
		System.out.println("*****XXX***" + new Date() + "*****");
		if(filename==null||filename.equals("")){
	        out.print("error!");  
	        out.flush();
	        out.close();
	        return;
		}
		if(appid!=null&&secret_key!=null&&!appid.equals("")&&!secret_key.equals("")){
			LfasrType type = LfasrType.LFASR_STANDARD_RECORDED_AUDIO;
			LfasrClient client = null;
			try {
				client = LfasrClient.InitClient(appid, secret_key, type);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Message message = null;
			try {
				System.out.println("*****000***" + new Date() + "*****");
				String savePath = this.getServletContext().getRealPath("/WEB-INF/upload");
				System.out.println("*****111***" + new Date() + "*****");
				message = client.lfasr_upload(savePath+"/"+filename);
				System.out.println("*****222***" + new Date() + "*****");
			} catch (LfasrException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  //需要转写的音频文件的路径，如"E:\\20160716am-as-shenzhen-Alex.mp3"
			catch (Exception e1) {
				System.out.println("*****Error*****");
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
			
			System.out.println("*****3333*****");
			if(message.getErr_no()==0 && message.getOk()==0){
				taskId = message.getData();
		        //将数据拼接成JSON格式  
		        out.print("{\"taskId\":\"" + taskId + "\"}");
		        out.flush();  
		        out.close();  
			}
			else{
				taskId = message.getData();
		        out.print("{\"taskId\":\"" + taskId + "\"}");
		        out.flush();
		        out.close();
			}
		}
		  
    }

}
