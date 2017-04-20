package service;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@MultipartConfig(fileSizeThreshold=1024*1024, // 1MB  当数据量大于该值时，内容将被写入文件
	maxFileSize=1024*1024*20,      // 10MB  允许上传的文件最大值。默认值为 -1，表示没有限制
	maxRequestSize=1024*1024*50)
public class Upload extends HttpServlet{

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("utf-8");  
        request.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter(); 
        //得到上传文件的保存目录，将上传的文件存放于WEB-INF目录下，不允许外界直接访问，保证上传文件的安全
        String savePath = this.getServletContext().getRealPath("/WEB-INF/upload");
        String message = "";
        String savePathStr = "";
        
        for (Part part : request.getParts()) {
            String fileName = extractFileName(part);
            System.out.println(fileName);
            if(fileName==null||fileName.trim().equals("")){
                message = "请选择文件进行上传！";
                out.print("{\"content\":\"" + message + "\"}");  
                out.flush();  
                out.close();  
                return;
            }
            //注意：不同的浏览器提交的文件名是不一样的，有些浏览器提交上来的文件名是带有路径的，如：  c:\a\b\1.txt，而有些只是单纯的文件名，如：1.txt
            //处理获取到的上传文件的文件名的路径部分，只保留文件名部分
            fileName = fileName.substring(fileName.lastIndexOf(File.separator)+1);
            //得到上传文件的扩展名 支持wav,flac,opus,m4a,mp3
            String fileExtName = fileName.substring(fileName.lastIndexOf(".")+1);
            if(!"wav".equals(fileExtName)&&!"mp3".equals(fileExtName)&&!"flac".equals(fileExtName)&&!"opus".equals(fileExtName)&&!"m4a".equals(fileExtName)){
                message = "上传文件的类型不符合！请上传wav,mp3,flac,opus,m4a结尾的文件！";
                out.print("{\"content\":\"" + message + "\"}");  
                out.flush();  
                out.close();  
                return;
            }
            
            fileName = mkFileName(fileName);
            savePathStr = savePath + "/" +  fileName;
            System.out.println("保存路径为:"+savePathStr);
            part.write(savePathStr);
            
            message = "文件上传成功";
            out.print("{\"content\":\"" + message  + "\"," + "\"filename\":\"" + fileName + "\"}");
            out.flush();
            out.close();
        }
        
    }


    //生成上传文件的文件名，文件名以：uuid+"_"+文件的原始名称
    public String mkFileName(String fileName){
        return UUID.randomUUID().toString()+"_"+fileName;
    }
    
    /**
     * 从 HTTP header content-disposition中获得文件名
     * content-disposition中内容：form-data; name="dataFile"; filename="PHOTO.JPG"
     */
    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] items = contentDisp.split(";");
        for (String s : items) {
            if (s.trim().startsWith("filename")) {
                return s.substring(s.indexOf("=") + 2, s.length()-1);
            }
        }
        return "";
    }
    
}