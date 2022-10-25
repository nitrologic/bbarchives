; ID: 2533
; Author: Malice
; Date: 2009-07-15 17:50:43
; Title: Bitmap Font auto-template (requires Blitzsys.dll)
; Description: Generates a bitmap image from a selection of Windows' installed fonts

;Blitzsys Initialisation;				########################################################################################

Global sBlitzSysDLLNameA101B6$ = "blitzsys"
Global hWnd = DLLFindBlitzRuntimeHwnd("MakeFont")

; Blitzsys Constants;					########################################################################################
Const SW_MINIMIZE        = 6

Const FONT_ATTRIBUTES_WIDTH          = 1
Const FONT_ATTRIBUTES_HEIGHT         = 2
Const FONT_ATTRIBUTES_ESCAPEMENT     = 3
Const FONT_ATTRIBUTES_ORIENTATION    = 4
Const FONT_ATTRIBUTES_WEIGHT         = 5
Const FONT_ATTRIBUTES_ITALIC         = 6
Const FONT_ATTRIBUTES_UNDERLINE      = 7
Const FONT_ATTRIBUTES_STRIKEOUT      = 8
Const FONT_ATTRIBUTES_CHARACTERSET   = 9

Const FONT_ATTRIBUTES_QUALITY        = 12
Const FONT_ATTRIBUTES_PITCHANDFAMILY = 13
Const FONT_ATTRIBUTES_COLOR          = 14
Const FONT_ATTRIBUTES_FACE           = 15

Const ANSI_CHARSET        = 0
Const DEFAULT_CHARSET     = 1
Const SYMBOL_CHARSET      = 2
Const SHIFTJIS_CHARSET    = 128
Const HANGEUL_CHARSET     = 129
Const GB2312_CHARSET      = 134
Const CHINESEBIG5_CHARSET = 136
Const GREEK_CHARSET       = 161
Const TURKISH_CHARSET     = 162
Const HEBREW_CHARSET      = 177
Const ARABIC_CHARSET      = 178
Const BALTIC_CHARSET      = 186
Const RUSSIAN_CHARSET     = 204
Const THAI_CHARSET        = 222
Const EASTEUROPE_CHARSET  = 238

Const OEM_CHARSET = 255

Const OUT_DEFAULT_PRECIS   = 0
Const OUT_STRING_PRECIS    = 1
Const OUT_CHARACTER_PRECIS = 2
Const OUT_STROKE_PRECIS    = 3
Const OUT_TT_PRECIS        = 4
Const OUT_DEVICE_PRECIS    = 5
Const OUT_RASTER_PRECIS    = 6
Const OUT_TT_ONLY_PRECIS   = 7
Const OUT_OUTLINE_PRECIS   = 8

Const CLIP_DEFAULT_PRECIS   = 0
Const CLIP_CHARACTER_PRECIS = 1
Const CLIP_STROKE_PRECIS    = 2
Const CLIP_MASK             = 15
Const CLIP_LH_ANGLES        = 16
Const CLIP_TT_ALWAYS        = 32
Const CLIP_EMBEDDED         = 128

Const DEFAULT_QUALITY        = 0
Const DRAFT_QUALITY          = 1
Const PROOF_QUALITY          = 2
Const NONANTIALIASED_QUALITY = 3
Const ANTIALIASED_QUALITY    = 4
Const DEFAULT_PITCH          = 0
Const FIXED_PITCH            = 1
Const VARIABLE_PITCH         = 2

Const FF_DECORATIVE = 80
Const FF_DONTCARE   = 0
Const FF_MODERN     = 48
Const FF_ROMAN      = 16
Const FF_SCRIPT     = 64
Const FF_SWISS      = 32

Const FONT_COLOR_BLACK   = $00000000
Const FONT_COLOR_MAROON  = $00000080
Const FONT_COLOR_GREEN   = $00008000
Const FONT_COLOR_OLIVE   = $00008080
Const FONT_COLOR_NAVY    = $00800000
Const FONT_COLOR_PURPLE  = $00800080
Const FONT_COLOR_TEAL    = $00808000
Const FONT_COLOR_GRAY    = $00808080
Const FONT_COLOR_SILVER  = $00C0C0C0
Const FONT_COLOR_RED     = $000000FF
Const FONT_COLOR_LIME    = $0000FF00
Const FONT_COLOR_YELLOW  = $0000FFFF
Const FONT_COLOR_BLUE    = $00FF0000
Const FONT_COLOR_FUCHSIA = $00FF00FF
Const FONT_COLOR_AQUA    = $00FFFF00
Const FONT_COLOR_WHITE   = $00FFFFFF


Const CF_BOTH                 = 3
Const CF_TTONLY               = $40000
Const CF_EFFECTS              = 256
Const CF_FIXEDPITCHONLY       = $4000
Const CF_FORCEFONTEXIST       = $10000
Const CF_INITTOLOGFONTSTRUCT  = 64
Const CF_LIMITSIZE            = $2000
Const CF_NOOEMFONTS           = $800
Const CF_NOFACESEL            = $80000
Const CF_NOSCRIPTSEL          = $800000
Const CF_NOSTYLESEL           = $100000
Const CF_NOSIZESEL            = $200000
Const CF_NOSIMULATIONS        = 4096
Const CF_NOVECTORFONTS        = $800
Const CF_NOVERTFONTS          = $1000000
Const CF_PRINTERFONTS         = 2
Const CF_SCALABLEONLY         = $20000
Const CF_SCREENFONTS          = 1
Const CF_SCRIPTSONLY          = $400
Const CF_SELECTSCRIPT         = $400000
Const CF_WYSIWYG              = $8000

Const FW_DONTCARE   = 0
Const FW_THIN       = 100
Const FW_EXTRALIGHT = 200
Const FW_ULTRALIGHT = 200
Const FW_LIGHT      = 300
Const FW_NORMAL     = 400
Const FW_REGULAR    = 400
Const FW_MEDIUM     = 500
Const FW_SEMIBOLD   = 600
Const FW_DEMIBOLD   = 600
Const FW_BOLD       = 700
Const FW_EXTRABOLD  = 800
Const FW_ULTRABOLD  = 800
Const FW_HEAVY      = 900
Const FW_BLACK      = 900

; Constants for MessageBox()
Const MB_ICONWARNING          = $30
Const MB_ICONQUESTION         = 32

Const MB_TOPMOST              = $40000

Const MB_RETRYCANCEL          = $5
Const MB_YESNO                = 4
Const MB_YESNOCANCEL          = 3

; Return values for MessageBox()
Const IDCANCEL = 2
Const IDNO     = 7
Const IDOK     = 1
Const IDRETRY  = 4
Const IDYES    = 6

Const BIF_RETURNONLYFSDIRS  = 1
Const BIF_NEWDIALOGSTYLE    = $40
Const BIF_EDITBOX           = $10
Const BIF_USENEWUI          = (BIF_NEWDIALOGSTYLE Or BIF_EDITBOX)

; Main Program Constants/Globals; 		########################################################################################

Global FONT_OKAY$="To Proceed and generate bitmap, click Yes,"+Chr$(10)+"To select another Font, click No."+Chr$(10)+ "To Exit, close this message window Or click Cancel."
Global FONT_NOT_OKAY$="To Select another Font, click Retry."+Chr$(10)+" Otherwise, click Cancel to Exit this program."
Global FONT_IMAGE$="To Select another Font, click Yes."+Chr$(10)+"Otherwise, click No to Exit this program."

Const CLICKTOIMAGE%=3
Const CLICKTOEXIT%=2
Const CLICKTOFONT%=1
Global Caption$
Global IconFlags=(MB_TOPMOST Or MB_ICONWARNING Or MB_RETRYCANCEL)
Global Display$=FONT_NOT_OKAY$
Global GetClick%=2
Global SelectedFont$	
Global ClickResultYESRETRY%
Global ClickResultNO%	
Global ClickResultOTHER%

; Main Progrm Initialisation

DLLShowWindow(hwnd,SW_MINIMIZE)

;Probably shouldn't be needed, but just in case:
ClsColor 0,0,0
Cls
Color 255,255,255

; Main Loop;							########################################################################################
		
	While Not KeyDown(1)
		SelectedFont$=SelectFont$()
		
		If (SelectedFont$<>"")
			Caption$=SelectedFont$+" Font Selected."
			Display$=FONT_OKAY$
			IconFlags=(MB_TOPMOST Or MB_ICONQUESTION Or MB_YESNOCANCEL)
			ClickResultYESRETRY=ClickToImage
			ClickResultNO=ClickToFont
			ClickResultOTHER=ClicktoExit
		Else
			Caption$="No Valid Font Selected"
			Display$=FONT_NOT_OKAY$
			IconFlags=(MB_TOPMOST Or MB_ICONWARNING Or MB_RETRYCANCEL)
			ClickResultYESRETRY=ClickToFont
			ClickResultNO=ClickToExit
			ClickResultOTHER=ClicktoExit						
		End If
		
		GetClick=WindowMessage(Caption$,Display$,IconFlags)
		
		If (GetClick=CLICKTOEXIT)
			AppTitle "Program Ended","Thank you for using Makefont"
			RuntimeError "Thank you for using Makefont"
		End If
		
		If (GetClick=CLICKTOIMAGE)
			CreateFontImage(SelectedFont$)
			
			Caption$="Bitmap Font Image Created."
			Display$=FONT_IMAGE$
			IconFlags=(MB_TOPMOST Or MB_ICONQUESTION Or MB_YESNO)
			ClickResultYESRETRY=ClickToFont
			ClickResultNO=ClickToExit
			ClickResultOTHER=ClicktoExit
			If (WindowMessage(Caption$,Display$,IconFlags)=CLICKTOEXIT)
				AppTitle "Program Ended","Thank you for using Makefont"
				RuntimeError "Thank you for using Makefont"
			End If
		End If
	Wend	
		
; Program Functions;					########################################################################################

Function WindowMessage(MessageTitle$,MessageText$,FlagVals%)
	Local Click=DLLMessageBox(MessageTitle$,MessageText$,FlagVals%)
		If ((Click=IDYES) Or (Click=IDRETRY) Or (Click=IDOK))
			Return ClickResultYesRetry
		End If
		If (Click=IDNO) 
			Return ClickResultNo
		End If
		
		Return ClickResultOther			
End Function

Function SelectFont$()
	Local MyFont
	Local Bank = CreateBank(65)
	Local Typeface$
	Local MyFontHeight=14
	Local MyFontColour=FONT_COLOR_BLACK
	Local MyFontBold=False
	Local MyFontItalic=False
	Local MyFontUline=False	

	DLLSetFontAttribute(Bank,FONT_ATTRIBUTES_COLOR,MyFontColour)
	DLLSetFontAttribute(Bank,FONT_ATTRIBUTES_BOLD,MyFontBold)
	DLLSetFontAttribute(Bank,FONT_ATTRIBUTES_ITALIC,MyFontItalic)
	DLLSetFontAttribute(Bank,FONT_ATTRIBUTES_UNDERLINE,MyFontUline)
	DLLSetFontAttribute(Bank,FONT_ATTRIBUTES_FACE,"Arial")
	DLLSetFontAttribute(Bank,FONT_ATTRIBUTES_HEIGHT,14)
	If DLLChooseFont(Bank,CF_SCREENFONTS Or CF_NOSCRIPTSEL Or CF_FORCEFONTEXIST Or CF_INITTOLOGFONTSTRUCT,10,16)
		TypeFace$ = DLLGetFontAttribute(Bank,FONT_ATTRIBUTES_FACE)
		If (TypeFace$ <> "")
			MyFontColour = DLLGetFontAttribute(Bank,FONT_ATTRIBUTES_COLOR)
			MyFontBold = DLLGetFontAttribute(Bank,FONT_ATTRIBUTES_BOLD)
			MyFontItalic = DLLGetFontAttribute(Bank,FONT_ATTRIBUTES_ITALIC)
			MyFontUline = DLLGetFontAttribute(Bank,FONT_ATTRIBUTES_UNDERLINE)
			MyFontHeight = DLLGetFontAttribute(Bank,FONT_ATTRIBUTES_HEIGHT)
			MyFont = LoadFont(TypeFace$,MyFontHeight,MyFontBold,MyFontItalic,MyFontULine)
			If (myFont)
				SetFont MyFont
			Else
				TypeFace$=""
			EndIf
		EndIf	
	EndIf
	Return TypeFace$
	FreeBank Bank
End Function

Function GetFontCharMaxWidth%()
	Local Max%=False
	Local Char%=False
	Local Width%=0
	For Char%=31 To 126
		Width=StringWidth(Chr$(Char))
		If (Width>Max)
			
			Max=Width
		End If
	Next
	Return Max
End Function

Function GetFontCharMaxHeight%()
	Local Max%=False
	Local Char%=False
	Local Height%=0
	For Char%=31 To 126
		Height=StringHeight(Chr$(Char))
		If (Height>Max)
			
			Max=Height
		End If
	Next
	Return Max
End Function

Function CreateFontImage(FontName$)
	Local CharWidth=GetFontCharMaxWidth%()
	Local CharHeight=GetFontCharMaxHeight%()
	Local XPlus%=(CharWidth Shr True)
	Local YPlus%=(CharHeight Shr True)

	Local X%
	Local Y%

	Local Image=CreateImage(CharWidth*16,CharHeight*16)
	SetBuffer ImageBuffer(Image)
	
	Cls
	
	For Y=0  To 15
		For X=0 To 15
			Text (X*CharWidth)+XPLUS,(Y*CharHeight)+YPLUS,Chr$(X + (Y*16)),True,True
		Next
	Next
	
	If (SaveDirectory$="")
		While (FileType(SaveDirectory$)<>2)
				SaveDirectory$=DLLBrowseForFolder$("Select a location in which to save the image")
			Wend
		If ((Right$(SaveDirectory$,1)<>"/") And (Right$(SaveDirectory$,1)<>"\"))
		SaveDirectory=SaveDirectory+"/"
	End If
	End If
	
	SetBuffer BackBuffer()
	SaveBuffer(ImageBuffer(Image),SaveDirectory$+FontName$+Str(CharWidth)+" x "+Str(CharHeight)+".bmp")
	FreeImage Image
	Image=0
End Function

; Blitzsys Functions;					########################################################################################


Function DLLFindBlitzRuntimeHwnd(sAppName$,sClosePrompt$ = "")
	Local windowHandle,sAppTempName$,sRandomString$ = " "
	
	SeedRnd MilliSecs()
	
	For a = 0 To 50
		sRandomString$ = sRandomString$ + Chr(Rnd(65,90))
	Next
	sAppTempName$ = sAppName$ + sRandomString$
	AppTitle sAppTempName$
	windowHandle = DLLFindWindow(sAppTempName$)
	SeedRnd 0
	
	; If this happens, there is a REAL problem, so closing the program should be fine..
	If windowHandle = False Then RuntimeError("Blitz window could not be found! Program Terminating!")
	AppTitle sAppName$,sClosePrompt$
	Return windowHandle
End Function

Function DLLFindWindow(sCaption$)
	Local iBankSize,mBankIn,iResult,iBankOffset
	
	; Gather the size of all 4 parameters + size values.
	iBankSize = Len(sCaption$) + 1
	
	mBankIn  = CreateBank(iBankSize)

	PokeString(mBankIn,sCaption$,0)
	
	iResult = CallDLL(sBlitzSysDLLNameA101B6,"FindWindowWrapper",mBankIn)
	
	FreeBank mBankIn
	Return iResult
End Function

Function DLLSetWindowZOrder(windowHandle,windowAfterHandle)
	Local x,y,w,h
	x = DLLGetWindowX(windowHandle)
	y = DLLGetWindowY(windowHandle)
	w = DLLGetWindowWidth(windowHandle)
	h = DLLGetWindowHeight(windowHandle)
	Return DLLSetWindowPos(windowHandle,windowAfterHandle,x,y,w,h)
End Function
Function DLLSetWindowPos(hwndHandle,hWndInsertAfter,iNewX,iNewY,iNewW,iNewH,iFlags = 0)
	Local mBankIn,iResult
	
	mBankIn  = CreateBank(7 * 4)
	PokeInt mBankIn,0,hwndHandle
	PokeInt mBankIn,4,hWndInsertAfter
	PokeInt mBankIn,8,iNewX
	PokeInt mBankIn,12,iNewY
	PokeInt mBankIn,16,iNewW
	PokeInt mBankIn,20,iNewH
	PokeInt mBankIn,24,iFlags
	
	iResult = CallDLL(sBlitzSysDLLNameA101B6,"SetWindowPosWrapper",mBankIn)
	
	FreeBank mBankIn
	Return iResult
End Function

Function DLLGetWindowX(hwndHandle)
	Local mBankIn,iResult
	
	mBankIn  = CreateBank(4)
	PokeInt mBankIn,0,hwndHandle
	
	iResult = CallDLL(sBlitzSysDLLNameA101B6,"GetWindowXWrapper",mBankIn)
	
	FreeBank mBankIn
	Return iResult
End Function

Function DLLGetWindowY(hwndHandle)
	Local mBankIn,iResult
	
	mBankIn  = CreateBank(4)
	PokeInt mBankIn,0,hwndHandle
	
	iResult = CallDLL(sBlitzSysDLLNameA101B6,"GetWindowYWrapper",mBankIn)
	
	FreeBank mBankIn
	Return iResult
End Function

Function DLLGetWindowWidth(hwndHandle)
	Local mBankIn,iResult
	
	mBankIn  = CreateBank(4)
	PokeInt mBankIn,0,hwndHandle
	
	iResult = CallDLL(sBlitzSysDLLNameA101B6,"GetWindowWidthWrapper",mBankIn)
	
	FreeBank mBankIn
	Return iResult
End Function

Function DLLGetWindowHeight(hwndHandle)
	Local mBankIn,iResult
	
	mBankIn  = CreateBank(4)
	PokeInt mBankIn,0,hwndHandle
	
	iResult = CallDLL(sBlitzSysDLLNameA101B6,"GetWindowHeightWrapper",mBankIn)
	
	FreeBank mBankIn
	Return iResult
End Function

Function DLLGetFontAttribute$(iFontBank,iFontAttribute)
	If iFontBank <> 0
		Select iFontAttribute
			Case FONT_ATTRIBUTES_HEIGHT
				Return PeekInt(iFontBank,0)
			Case FONT_ATTRIBUTES_WIDTH
				Return PeekInt(iFontBank,4)
			Case FONT_ATTRIBUTES_ESCAPEMENT
				Return PeekInt(iFontBank,8)
			Case FONT_ATTRIBUTES_ORIENTATION
				Return PeekInt(iFontBank,12)
			Case FONT_ATTRIBUTES_WEIGHT
				Return PeekInt(iFontBank,16)
			Case FONT_ATTRIBUTES_ITALIC
				Return PeekByte(iFontBank,20)
			Case FONT_ATTRIBUTES_UNDERLINE
				Return PeekByte(iFontBank,21)
			Case FONT_ATTRIBUTES_STRIKEOUT
				Return PeekByte(iFontBank,22)
			Case FONT_ATTRIBUTES_CHARACTERSET
				Return PeekByte(iFontBank,23)
			Case FONT_ATTRIBUTES_QUALITY
				Return PeekByte(iFontBank,26)
			Case FONT_ATTRIBUTES_PITCHANDFAMILY
				Return PeekByte(iFontBank,27)
			Case FONT_ATTRIBUTES_COLOR
				Return PeekInt(iFontBank,61)
			Case FONT_ATTRIBUTES_FACE
				Return PeekString(iFontBank,28)
			Default
				Return 0
		End Select
	EndIf
End Function

Function DLLSetFontAttribute(iFontBank,iFontAttribute,iValue$)
	If iFontBank <> 0
		Select iFontAttribute
			Case FONT_ATTRIBUTES_HEIGHT
				PokeInt(iFontBank,0,Float(iValue) * 1.33333334)
			Case FONT_ATTRIBUTES_WIDTH
				PokeInt(iFontBank,4,iValue)
			Case FONT_ATTRIBUTES_ESCAPEMENT
				PokeInt(iFontBank,8,iValue)
			Case FONT_ATTRIBUTES_ORIENTATION
				PokeInt(iFontBank,12,iValue)
			Case FONT_ATTRIBUTES_WEIGHT
				PokeInt(iFontBank,16,iValue)
			Case FONT_ATTRIBUTES_ITALIC
				PokeByte(iFontBank,20,iValue)
			Case FONT_ATTRIBUTES_UNDERLINE
				PokeByte(iFontBank,21,iValue)
			Case FONT_ATTRIBUTES_STRIKEOUT
				PokeByte(iFontBank,22,iValue)
			Case FONT_ATTRIBUTES_CHARACTERSET
				PokeByte(iFontBank,23,iValue)
			Case FONT_ATTRIBUTES_QUALITY
				PokeByte(iFontBank,26,iValue)
			Case FONT_ATTRIBUTES_PITCHANDFAMILY
				PokeByte(iFontBank,27,iValue)
			Case FONT_ATTRIBUTES_COLOR
				PokeInt(iFontBank,61,iValue)
			Case FONT_ATTRIBUTES_FACE
				PokeString(iFontBank,Left$(iValue,32),28)
		End Select
	EndIf
End Function

Function DLLChooseFont(iFontBank,iFlags,iMinSize = 8,iMaxSize = 16)
	Local mBankIn,iResult
	
	mBankIn  = CreateBank(16)
	PokeInt mBankIn,0,iFlags
	PokeInt mBankIn,4,iMinSize
	PokeInt mBankIn,8,iMaxSize
	
	iResult = CallDLL(sBlitzSysDLLNameA101B6,"ChooseFontWrapper",mBankIn,iFontBank)

	FreeBank mBankIn
	Return iResult
End Function

Function PokeString(mBankAddr,sStringOut$,iBufferOffset = 0)
	For n = 1 To Len(sStringOut$)
		PokeByte mBankAddr,iBufferOffset,Asc(Mid$(sStringOut$,n,1))
		iBufferOffset = iBufferOffset + 1
	Next
	PokeByte mBankAddr,iBufferOffset,0
End Function

Function PeekString$(mBankAddr,iBufferOffset = 0)
	Local sOutStr$ = "",iByte
	
	For n = 0 To BankSize(mBankAddr)
		iByte = PeekByte(mBankAddr,iBufferOffset)
		If iByte <> 0 
			sOutStr$ = sOutStr$ + Chr(iByte)
		Else
			Exit
		EndIf
		iBufferOffset = iBufferOffset + 1
	Next

	Return sOutStr$
End Function

Function DLLBrowseForFolder$(sTitle$,iFlags = BIF_NEWDIALOGSTYLE Or BIF_RETURNONLYFSDIRS,iOutBufferSize = 512)
	Local iBankSize,mBankIn,mBankOut,iResult,sResult$ = ""

	iBankSize = Len(sTitle$) + 5
	
	mBankIn  = CreateBank(iBankSize)
	mBankOut = CreateBank(iOutBufferSize)

	PokeInt mBankIn,0,iFlags
	PokeString(mBankIn,sTitle$,4)
	
	iResult = CallDLL(sBlitzSysDLLNameA101B6,"BrowseForFolderWrapper",mBankIn,mBankOut)
	
	; Pull the resulting string out of the bank...
	If iResult <> 0
		sResult$ = PeekString$(mBankOut,0)
	EndIf
	
	FreeBank mBankIn
	FreeBank mBankOut
	Return sResult$
End Function
	
Function DLLMessageBox(sCaption$,sContents$,iFlags = MB_TOPMOST)
	Local iBankSize,mBankIn,iResult,iBankOffset
	
	; Gather the size of all 4 parameters + size values.
	iBankSize = Len(sCaption$) + Len(sContents$) + 6
	
	mBankIn  = CreateBank(iBankSize)

	PokeString(mBankIn,sCaption$,0)
	iBankOffset = Len(sCaption$) + 1
	PokeString(mBankIn,sContents$,iBankOffset)
	iBankOffset = iBankOffset + Len(sContents$) + 1
	PokeInt mBankIn,iBankOffset,iFlags
	
	iResult = CallDLL(sBlitzSysDLLNameA101B6,"MsgBoxWrapper",mBankIn)
	
	FreeBank mBankIn
	Return iResult
End Function	

Function DLLShowWindow(hwndHandle,iFlags)
	Local iBankSize,mBankIn,iResult
	
	mBankIn  = CreateBank(8)
	PokeInt mBankIn,0,hwndHandle
	PokeInt mBankIn,4,iFlags
		
	iResult = CallDLL(sBlitzSysDLLNameA101B6,"ShowWindowWrapper",mBankIn)
	
	FreeBank mBankIn
	Return iResult
End Function
