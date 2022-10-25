; ID: 3121
; Author: xlsior
; Date: 2014-04-15 20:09:36
; Title: Get External IP Address
; Description: External IP Address

' External IP detector
' by Marc van den dikkenberg / http://www.xlsior.org
'
' The following snippet uses a free IP lookup service provided by www.dyndns.org
' Please use sparingly to conserve their server resources

SuperStrict
Framework BRL.StandardIO
Import BRL.System
Import BRL.Bank
Import BRL.HTTPStream
Import BRL.Retro

AppTitle$="NAT IP Finder 1.0"
Local t:Int

Local MyIP:String

MyIP$=GetExternalIP()

If MyIP$="-1" Then
	Notify "Error obtaining IP address",True
Else If MyIP$="0" Then 
	Notify "Error parsing IP data",True
Else
'	IP Address found, let's copy to clipboard:
	TextToClipboard(MyIP$)
	Notify "External IP Address: "+myip$+"  ~n~n(Copied to Clipboard)"

End If

End


Function GetExternalIP$()

	Local ipstart:Int=0
	Local ipend:Int=0
	Local temp:Int=0
	Local ResultBank:TBank
	Local MyParsedIP:String
	MyParsedIP$=""
	ResultBank:TBank=LoadBank("http::checkip.dyndns.org")
	
	If Not ResultBank Then 
		' Error - No Network Connection Detected 
		Return -1
	Else
		For temp=0 To BankSize(ResultBank:TBank)-1
			MyParsedIP$=MyParsedIP$+Chr$(PeekByte(ResultBank:TBank,temp))
		Next
		
		ipstart=Instr(MyParsedIP$,":",1)+2
		ipEnd= Instr(MyParsedIP$,"</body>",1)-1
		MyParsedIP$=Mid$(MyParsedIP$,ipstart,ipend-ipstart)
		If Len(MyParsedIP$)<7 Then
			' Shortest possible IP = x.x.x.x
			' Error - Could not detect IP address"
			Return 0
		Else 
			' External IP address successfully parsed
			Return MyParsedIP$
		End If
	End If 
End Function


Extern "Win32"
	Function OpenClipboard%(hwnd%)
	Function CloseClipboard%()
	Function EmptyClipboard%()
	Function SetClipboardData(format%, hMem:Byte Ptr)
	Function GlobalAlloc(Flags:Int, Bytes:Int)
	Function GlobalFree(Mem:Int)
	Function GlobalLock:Byte Ptr(Mem:Int)
	Function GlobalUnlock(Mem:Int)
End Extern 
	
Function TextToClipboard(txt:String)
	Const CF_TEXT%=$1
	Const GMEM_MOVEABLE%=$2
	Const GMEM_DDESHARE%=$2000
	If txt$="" Return
	Local TextBuf:Byte Ptr = Txt.ToCString()
	Local Memblock:Int = GlobalAlloc(GMEM_MOVEABLE|GMEM_DDESHARE, txt.Length+1)
	Local DataBuf:Byte Ptr = GlobalLock(Memblock)
	MemCopy DataBuf, TextBuf, Txt.length
	If OpenClipboard(0)
		EmptyClipboard
		SetClipboardData CF_TEXT, DataBuf
		CloseClipboard
	EndIf
	GlobalUnlock Memblock
	GlobalFree Memblock
End Function
