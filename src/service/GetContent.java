package service;


import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.iflytek.voicecloud.client.LfasrClient;
import com.iflytek.voicecloud.exception.LfasrException;
import com.iflytek.voicecloud.model.LfasrType;
import com.iflytek.voicecloud.model.Message;

public class GetContent extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        res.setCharacterEncoding("utf-8"); 
        PrintWriter out = res.getWriter();  
        String content = "";
		String appid = req.getParameter("appid");
		String secret_key = req.getParameter("secretkey");
		String taskid = req.getParameter("taskid");
		System.out.println("****appid:"+appid+",secret_key:"+secret_key+",taskid:"+taskid+"****");
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
			message = client.lfasr_get_result(taskid);
		} catch (LfasrException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (message.getErr_no() == 0 && message.getOk() == 0){
			Gson gson = new Gson();
			String json = message.getData();
			if(json != null){
				
				  List persons=new ArrayList();
				  List<JsonElement> list=new ArrayList();
				  JsonParser jsonParser=new JsonParser();
				  JsonElement jsonElement=jsonParser.parse(json);  //将json字符串转换成JsonElement
				  JsonArray jsonArray=jsonElement.getAsJsonArray();  //将JsonElement转换成JsonArray
				  Iterator it=jsonArray.iterator();  //Iterator处理
				  while(it.hasNext()){  //循环
				   jsonElement=(JsonElement) it.next(); //提取JsonElement
				   json=jsonElement.toString();
				   Map<String, String> map = gson.fromJson(json, new TypeToken<Map<String, String>>()
			        {
			        }.getType());
				   content += map.get("onebest");	   
				  }
					res.setCharacterEncoding("utf-8"); 
			}
		}
		else{
			content = message.getFailed() + "错误代码:" + message.getErr_no();
		}
        out.print("{\"content\":\"" + content + "\"}");  
        out.flush();  
        out.close(); 
		  
    }

}
