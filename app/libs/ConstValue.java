package com.bomeans.IRKit;

public interface ConstValue {
	// BIRError
	public final int BIRNoError=0;   // 沒有錯誤
	public final int BIROK=0;
    
	public final int BIRTransmitFail=1;               // 送出IR wave 失敗
	public final int BIRTransmitFailWifiToIR=2;       // 送出IR wave 失敗, 因wifi To IR 不正常
	public final int BIRNoImplement=3;                // 此功能沒有設計
    
	public final int BIRNotConnectToNetWork=4;       // iDevice 沒有連上網路
	public final int BIRNotConnectToAP=5;            // iDevice 沒有連上 wifi AP
	public final int BIRCantGetFileFromServer=6;     // 無法從server 取得資料
	public final int BIRXMLFormatError=7;            // xml format error
	public final int BIRJsonFormatError=8;           // json 檔案格式錯誤
	public final int BIRWebAPIFail=9;                // web api fail
    
	
	public final int BIRBackgroudProcessFail=10;       // 建立backgroud 程序失敗
 	
	public final int BIRNotFindWifiToIR=11;            // 找不到小火山
	public final int BIRFileReadError=12;              // 無法讀取到檔案
	public final int BIRFileSaveError=13;
	public final int BIRErrorInputParams=15;		   // 參數錯誤

	public final int BIRKeyIDNotExist = 16;            // 預送出的的key id 不存在
	
	public final int BIRLibImplementError=500;         // library 內部設計有問題

	public final int BIRUnKnowError=1000;
	
	public final int BIR_CustomerErrorBegin=0x40000000;  // 第三方開發者. .開發  interface BIRIrHW  時.. 回傳的的錯誤值請大於 0x40000000 
	
	
	// enum BIRGuiDisplayType
	public final int BIRGuiDisplayType_NO=0;      // 實體Remote 沒有顯示任何資訊
	public final int BIRGuiDisplayType_YES=1;     // 實體Remote 只有在AC power on 時會顯示資訊
	public final int BIRGuiDisplayType_ALWAYS=2;  // 實體Remote 總是顯示資訊

	
	//enum BIRWifiToIRResult 
	public final int BIRResultFind = 0;        //
	public final int BIRResultSetOk = 0;       // wifiToIR 設定成功(note 目前版本.不會有此結果)
	public final int BIRResultTimeOut=1+100;       // 時間用光結束
	public final int BIRResultUserCancal=2+100;    // 使用者中斷後結束
	
	
	// for Smart Picker  key result
	public final int BIR_PFind=0;    // 找到remote
	public final int BIR_PNext=1;    // 必須再測試下一個key 
	public final int BIR_PFail=2;    // 找不到remote
	public final int BIR_PUnknow=3;  // 不確定的狀態 可能是根本沒有呼叫begin
}


