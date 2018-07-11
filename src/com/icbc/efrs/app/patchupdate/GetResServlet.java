package com.icbc.efrs.app.patchupdate;
//package com.icbc.patchupdate;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.OutputStream;
//import java.text.DateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.Map;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import com.icbc.ebdp.util.TestProperties;
//
//import com.icbc.ebdp.updatepatch.GetResMessage;
//
//public class GetResServlet extends HttpServlet {
//private static final long serialVersionUID = -6337789063068723181L;
//private Object String;
//	
//	/*
//	 * (non-Java-doc)
//	 * 
//	 * @see javax.servlet.http.HttpServlet#HttpServlet() 
//	 */
//	public GetResServlet() {
//		super();
//	}
//
//	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		
//		try{
//			
//			
//			
//			/** 获取请求信息 */
//			String resId = request.getParameter("resId");
//			String resVer = request.getParameter("resVer");
//			String resType = request.getParameter("resType");
//			
//			String initDate=request.getParameter("initDate");
//			Date d = DateFormat.getDateInstance(DateFormat.SHORT).parse(initDate);
//			System.out.println(d.toString());
//			
//			
//			BufferedReader br = null;
//			br = new BufferedReader(new InputStreamReader(new FileInputStream(TestProperties.getPropertyValue("currentVersion.txt"))));
//			String newVersion = br.readLine();
//			br.close();
//			
//			/** 返回请求信息 */
//			String filePath = "";
//			if(resType.equals("0"))
//				filePath = TestProperties.getPropertyValue(resId+"$"+newVersion+".zip");
//			else if(resType.equals("1"))
//				filePath = TestProperties.getPropertyValue(resId+"$"+newVersion+"-"+resVer+".patch");
//			// 不同的文件类型设置不同的MIME
//			if (filePath.endsWith(".zip")) {
//				response.setContentType("application/zip");
//			} else if (filePath.endsWith(".patch")) {
//				response.setContentType("text/plain");
//			} 			
////			response.setHeader("Content-disposition", "attachment; filename="+filePath); 			
//			FileInputStream in = new FileInputStream(getFile(filePath));
//			OutputStream o = response.getOutputStream();
//			int l = 0;
//			byte[] buffer = new byte[4096];
//			while ((l = in.read(buffer)) != -1) {
//				o.write(buffer, 0, l);
//			}
//			o.flush();
//			in.close();
//			o.close();
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
//	}
//	
//	/**
//	 * 从/configfiles路径下读取文件
//	 * @return File 
//	 */
//	private static File getFile(String fileName) {
//		try {
//			/** 从/configfiles/components路径下读取文件 */
////			File file = new File(new Object(){}.getClass().getResource("/"+fileName).getFile());
//			File file = new File(fileName);
//			return file;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//	
//}