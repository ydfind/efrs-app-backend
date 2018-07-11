package com.icbc.efrs.app.patchupdate;

import java.io.BufferedReader;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


import com.icbc.efrs.app.aspect.LoggerAspect;
import com.icbc.efrs.app.prop.ServerProp;
import com.icbc.ms.component.CommonDecryptUtils;

public class GetResMessageDemo extends GetResMessage {
	public GetResMessageDemo(String resId, String resVer,String platform) {
		super(resId, resVer,platform);
		// TODO Auto-generated constructor stub
	}
	
//	public static final String  ROOT_PATH="D:/workspace/PatchUpdate/WebContent/IFTidePatch/";
	private static final String  ROOT_BASIC_PATH = "IFTidePatch/";
	
	private static final String  ROOT_SETTING_FLODER="updateRes/";//全量
	private static final String  ROOT_PATCH_FLODER="/patch/";//全量
	
	
	@Override
	public String getNewVersion() {
		// TODO Auto-generated method stub
		// DRD: resId应该为"wapBank"
		String newVersion=GetResMessageDemo.readerParam(
				getRootPath() + 
				resId + File.separator + ROOT_SETTING_FLODER + "currentInfo.txt");
		String [] newVersionArray = newVersion.split("\\|");
		for(int i=0;i<newVersionArray.length;i++){
			if(((newVersionArray[i].split(","))[0]).equals(resId+"_"+platform)){
				newVersion=newVersionArray[i].split(",")[1];
				break;
			}
		}
		
		return newVersion;
	}
	private String getRootPath(){
		return ServerProp.getAppServerPath() + ROOT_BASIC_PATH;
	}

	@Override
	public String getCurVerSha256() {
		// TODO Auto-generated method stub
		return GetResMessageDemo.readerParam(getRootPath() +
				resId+ File.separator +ROOT_SETTING_FLODER+resId+"_"+platform+"$"+resVer+".txt");
		
	}

	@Override
	public boolean isExistSha256File() {
		// TODO Auto-generated method stub
		
		File sha256File = new File(getRootPath() +
				resId+ File.separator+ROOT_SETTING_FLODER+resId+"_"+platform+"$"+resVer+".txt"); 
		
		return sha256File.exists();
	}

	@Override
	public boolean isExistSignFile() {
		// TODO Auto-generated method stub
		
		
		File signFile = new File(getRootPath()+
				resId+ File.separator+ROOT_SETTING_FLODER+resId+"_"+platform+"$"+resVer+".sign");
		
		return signFile.exists();
	}

	@Override
	public boolean isExistPatchFile(String newVer) {
		// TODO Auto-generated method stub
		File patchFile = new File(getRootPath() +
				resId+ROOT_PATCH_FLODER+resId+"_"+platform+"$"+newVer+"-"+resVer+".patch");
		
		return patchFile.exists();
	}

	@Override
	public ArrayList<String> getURIAndSign( String newVer, boolean isPatch) {
		// TODO Auto-generated method stub
		ArrayList<String> returnArrayList=new ArrayList<String>();
		
		if(isPatch){
			returnArrayList.add(readerParam(getRootPath() +
					resId+ File.separator +ROOT_SETTING_FLODER+resId+"_"+platform+"$"+newVer+"-"+resVer+".sign"));//RES_SIGN_KEY
			returnArrayList.add(resId+ROOT_PATCH_FLODER+(resId+"_"+platform+"$"+newVer+"-"+resVer+".patch"));//RES_URI_KEY
		}else{
			returnArrayList.add(readerParam(getRootPath() + 
					resId+ File.separator+ROOT_SETTING_FLODER+resId+"_"+platform+"$"+newVer+".sign"));//RES_SIGN_KEY
			returnArrayList.add(resId+"/"+newVer+"/"+(resId+"_"+platform+"$"+newVer+".zip"));//RES_URI_KEY
		}
		
		return returnArrayList;
	}

	@Override
	public boolean hasLocalRes() {
		// TODO Auto-generated method stub
		File resFolder=new File(getRootPath() + resId);
		
		return resFolder.isDirectory();
	}

	@Override
	public String getTypeSign(String resType) {
		// TODO Auto-generated method stub
		CommonDecryptUtils utils = new CommonDecryptUtils();		
//		String signStr=utils.getArsQian (resType,  "19538900300851670452433482354103639547911161135477894266921631953564244584601260434871142611206326582135833381236605427748357506477459570856057876568743169827072581812106998412376798372887207558653774376624639184107311697551719767386654829058041087917718002315541165923556913576623865079368123035273279633225826050404729050550746643340552201178480210991579469563824108566547700113540763985614704171603755253155907848537533947597984477821712241751708020806260730383345583394768003753008032075574653332990251655482125030108248905885092758517607139143147022100979836856116441870340009405622616781469079164740791667986549",  "12176148239728444587757081539038203499950253016373234657603283192937078491229383057213060338194875931779141113006152284582307229840333343505687775408456899139531429784522665397559864234662655353509907370122631321514971955379553336586025754513625220434983611281703720913132854835938405384843271936213070685270373274697994157208459036758738860929224808915004931802798544592249390036031014291350225815135251864089863591713486491254974429207539872262033309256723300931012692853221385993307251794236266506334230194689394671178644226311448229926177123631152032281931522277109588165556752935124264339108615788582060226797293");
		
//		String signStr=utils.getArsQian (resType,  "28606516409369871209303037373048210063279015853241596545964996740329651687707391695873934182856494617989631625707097134380607977351676699472706136444423624466221744294350733519122625058544503159064997465760354904106074554685862881655003043268638585318606148382256380988054524672994559092462846746871964015703682359573802616048227728225525923602499281967441057638252069203870749683961143016544108945399273680089309011472422953975472136340847965273516161752046029878052403724383459824787599933870285018811824887057264136550592405444770044927932120864186535231309458756085271300317468641369993989121666496441886427205219",  "7260643513640515093695877499172740988946444751862622899210854262151813878775725978747379666411873162879587598791703217011566490612444729222103451082846980627296527070086059803730499492253677549291044262713547209590009370930079850671366108026466488063074212615628616527386040914454300562186657808374937049868157182549328496196623515042782528146840070854772890395884983366379352805868908627203443133040482861810235957558066501368055272323593425496888546406464785067623036741475934012301647569833549435207083354822607123625780086028579369065654061148178318336568036717633786637641073137563454022019583782696545300736593");
		
		
		String signStr=utils.getArsQian (resType,  
				"17050367084159664512769525332665266525093869338341418927892271934680341594358801170713655732961401071336395190700962086583756401748995880354581450395937098331305418715136744435538954758293444543768958663244028176387238539783830447551387366284794482826442391865243386116457262589499251750427806554630897720016752005595336897665446993110469522317879890466488529223479307744972130883974584060952351030195729783778879554965575102026418266951961512931287264981907594584695841590538856303848048795332547809031172511089511112201882374891357961783513154938441637012413799286524635959443172109491617848807291891090555850213363",  
				"1442505231164558583052350735607303306148213496702135150946663578879543189405706836918670912262962668113154046940820519481701874439438524165189317635310020834151029565099824422803748852599811291596828786856628143289999279913414198078847405146751781275912728473015677840934265196053184479842257293174946602658092775963964422972501123652093997162608522468710599636193808222268653189876414529086149236062235114693062597400664655540632490394249225973095815624825956730274353738234272120971460007078969880124059005694495373073564207367552782215108677604344371622250183088797909210701847397414455758612913606285869662001227");
		
		
		return signStr;
	}
	
	public static String readerParam(String fileName){
		//读最新的版本
		File file=new File(fileName);
		BufferedReader reader=null;
		String returnStr=null;
		StringBuffer sb=new StringBuffer("");
		
		try {
			reader = new BufferedReader(new FileReader(file));
			
			while((returnStr = reader.readLine())!=null){
				sb.append("|"+returnStr);
			}
			
			
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}finally{
			if(reader!=null){
				try{
					reader.close();
				}catch(Exception e){
					
				}
			}
		}
		LoggerAspect.logInfo("读取文件" + fileName + " 内容为：" + sb.toString().substring(1));
		return sb.toString().substring(1);	// DRD: 为什么是substring(1)；	
	}
	
	public static void main(String[] args){
		CommonDecryptUtils utils = new CommonDecryptUtils();
		String signStr=utils.getArsQian ("3",  "17050367084159664512769525332665266525093869338341418927892271934680341594358801170713655732961401071336395190700962086583756401748995880354581450395937098331305418715136744435538954758293444543768958663244028176387238539783830447551387366284794482826442391865243386116457262589499251750427806554630897720016752005595336897665446993110469522317879890466488529223479307744972130883974584060952351030195729783778879554965575102026418266951961512931287264981907594584695841590538856303848048795332547809031172511089511112201882374891357961783513154938441637012413799286524635959443172109491617848807291891090555850213363",  "1442505231164558583052350735607303306148213496702135150946663578879543189405706836918670912262962668113154046940820519481701874439438524165189317635310020834151029565099824422803748852599811291596828786856628143289999279913414198078847405146751781275912728473015677840934265196053184479842257293174946602658092775963964422972501123652093997162608522468710599636193808222268653189876414529086149236062235114693062597400664655540632490394249225973095815624825956730274353738234272120971460007078969880124059005694495373073564207367552782215108677604344371622250183088797909210701847397414455758612913606285869662001227");
		System.out.println(signStr);
	}
	
}




///*
// * 判断签名是否相等
// */
//public static boolean isEqual(String str1,String str2){
//	boolean res = false;		
//	try {
//		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(str1)));
//		String str3;
//		str3 = br.readLine();
//		res = str3.equals(str2);
//		br.close();
//	} catch (IOException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//	return res;
//}
//
//



//
//String rootPath=rootBundlePath+"/"+resId;
//
//
////判断是否存在resId对应的差量包文件夹
//
//File resFolder=new File(resId);
//
//if(resFolder.isDirectory()){
//
//	//读最新的版本
//	String newVersion = readerParam("currentVersion.txt");
//	//读签名文件
//	
//	File signFile = new File(resId+"$"+resVer+".sign");
//	
//	//读sha1值
//	File sha1File = new File(resId+"$"+resVer+".txt");
//	String sha1Value = readerParam(resId+"$"+resVer+".txt");
//	
//	
//	HashMap<String,String> hmap = new HashMap<String,String>();
//	
//	
//	if(!signFile.exists() || (sha1File.exists() && !sha1Value.equals(resSha1))){
//		//签名文件不存在，或sha1校验失败时全量更新
//		
//		hmap.put("resId", resId);
//		hmap.put("resVer", newVersion);
//		hmap.put("resType", "0");
//		hmap.put("resSign", readerParam(resId+"$"+newVersion+".sign"));
//		
//	}else if(!resVer.equals( newVersion)){
//		//走差量
//		
//		File checkFilePath = new File(resId+"$"+newVersion+"-"+resVer+".patch");
//		if(checkFilePath.exists()){
//			//增量更新
//			hmap.put("resId", resId);
//			hmap.put("resVer", newVersion);
//			hmap.put("resType", "1");
//			hmap.put("resSign", readerParam(resId+"$"+newVersion+"-"+resVer+".sign"));
//		}else{
//			//全量更新
//			hmap.put("resId", resId);
//			hmap.put("resVer", newVersion);
//			hmap.put("resType", "0");
//			hmap.put("resSign", readerParam(resId+"$"+newVersion+".sign"));
//		}
//		
//		
//	}
	/** 返回请求信息 */
//	response.setContentType("text/plain;charset=utf-8");
//	String str = JSON.toJSONString(hmap);			
//	response.getWriter().write(str);
//}else{
//	response.setContentType("text/plain;charset=utf-8");
//	HashMap<String,String> hmap = new HashMap<String,String>();
//	hmap.put("resType", "error");
//	String str = JSON.toJSONString(hmap);			
//	response.getWriter().write(str);
//}	

