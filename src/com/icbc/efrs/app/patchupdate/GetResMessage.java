package com.icbc.efrs.app.patchupdate;

import java.util.ArrayList;
import java.util.HashMap;


//resNewVer
//resType
//resTypeSign
//resSign
//resUri


public abstract class GetResMessage {
	String resId=null;     // 通常是wapBank
	String resVer=null;    
	String platform=null;  // DRD:通常是android
	public GetResMessage(String resId,String resVer,String platform){
		this.resId=resId;
		this.resVer=resVer;
		this.platform=platform;
	}
	
	abstract public boolean hasLocalRes();//是否有本地资源
	abstract public String getNewVersion();//获得当前最新版本
	abstract public String getCurVerSha256();//获得当前上送版本的sha256值
	abstract public boolean isExistSha256File();
	abstract public boolean isExistSignFile();
	abstract public boolean isExistPatchFile(String newVer);
	
	abstract public String getTypeSign (String resType);
	
	
	abstract public ArrayList<String> getURIAndSign(String newVer,boolean isPatch);//根据isPatch获得要返回的数据
}
