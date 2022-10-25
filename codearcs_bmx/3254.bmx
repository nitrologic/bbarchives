; ID: 3254
; Author: Bobysait
; Date: 2016-02-06 09:51:59
; Title: Search Text in files
; Description: A standalone tool to search text in files

Framework maxgui.drivers
Import mdt.uisdk

Global _window:TGadget = CreateWindow("search", 10,10,800,600,,WINDOW_TITLEBAR | WINDOW_RESIZABLE | WINDOW_CLIENTCOORDS | WINDOW_CENTER | WINDOW_STATUS );
Local txtDir:TGadget = CreateTextField(5,5,ClientWidth(_window)-10-40, 20, _window )
	txtDir.SetLayout(EDGE_ALIGNED, EDGE_ALIGNED, EDGE_ALIGNED, 0);
Local BtnDir:TGadget = CreateButton("...", 5+ClientWidth(_window)-10-40, 5, 40, 20, _window);
	btnDir.SetLayout(0, EDGE_ALIGNED, EDGE_ALIGNED, 0);
Local txtSearch:TGadget = CreateTextField(5,30,ClientWidth(_window)-10-40-80, 20, _window )
	txtSearch.SetLayout(EDGE_ALIGNED, EDGE_ALIGNED, EDGE_ALIGNED, 0);
Local sldLevel:TGadget = CreateTextFieldK( 5+ClientWidth(_window)-10-40-80, 30, 70, 20, _window, TEXTFIELDK_INT | TEXTFIELDK_CLAMP )
	sldLevel.SetLayout(0, EDGE_ALIGNED, EDGE_ALIGNED, 0);
	TTextFieldK(sldLevel).setClamp(-1,100, 1);
	sldLevel.SetValue(-1);
Local btnSearch:TGadget = CreateButton("GO !", 5+ClientWidth(_window)-10-40, 30, 40, 20, _window);
	btnSearch.SetLayout(0, EDGE_ALIGNED, EDGE_ALIGNED, 0);
	
Global txtOut:TGadget = CreateTextArea(5, 55, ClientWidth(_window), ClientHeight(_window)-60, _window );
	txtOut.SetLayout (EDGE_ALIGNED,EDGE_ALIGNED, EDGE_ALIGNED, EDGE_ALIGNED);
	
Global _g_CurDir:String;

Repeat
	WaitEvent()
	Select EventID()
		Case EVENT_APPTERMINATE
			End
		Case EVENT_WINDOWCLOSE
			End
		Case EVENT_GADGETACTION
			Select EventSource()
				Case BtnDir
					Local dir:String = RequestDir("directory to search", CurrentDir())
					If FileType(dir)=2
						ChangeDir(dir)
						txtDir.SetText(dir);
					EndIf;
				Case btnSearch
					txtOut.SetText("");
					_g_CurDir = txtDir.GetText()
					SearchTxt(_g_CurDir, txtSearch.GetText(), TTextFieldK(sldLevel).GetValueI())
					SetStatusText(_window, "done");
			End Select
	End Select
Until AppTerminate()
End;

Function SearchTxt(d:String, keywords:String, pRecursive:Int=20)
	
	d = d.Replace("\","/");
	If Right(d,1) = "/" Then d = Left(d,d.length-1);
	
	Local dir:Int = ReadDir(d)
	If Not(dir) Then Return;
	
	d :+ "/"
	SetStatusText(_window, "DIR : '"+RSet(d,30)+"'")
	Repeat
		
		Local f:String = NextFile(dir)
		Select f
			Case "" ; Exit;
			Case ".",".."
			Default
				Select FileType(d+f)
					Case 1
						searchTxtFile(d+f, keywords)
					Case 2
						If pRecursive<>0 Then SearchTxt(d+f, keywords, pRecursive-1)
				End Select
		End Select
		
	Forever
	
	CloseDir(dir)
	
End Function


Function searchTxtFile(url:String, keywords:String)
	
	Local bank:TBank = LoadBank(url)
	If bank=Null Then Return;
	
	Local kw:Byte ptr = keywords.ToCString();
	Local kl:Int = keywords.length;
	
	Local offset:Int = 0
	Local Size:Int = bank.Size();
	Local Buf:Byte ptr = bank.Buf();
	
	For offset = 0 Until Size-kl
		
		Local found:Byte = True;
		For Local a:Int = 0 Until kl
			If Buf[offset+a] <> kw[a] Then found = False; Exit;
		Next
		If found Then AddTextAreaText(txtOut, "* url="+Right(url,url.length-_g_CurDir.length).Replace("/","\")+"' pos='"+offset+"'"+Chr(13)+Chr(10) );
	Next;
	
	bank.Resize(0);
	bank = Null
	
	Delay 10;
	
End Function
