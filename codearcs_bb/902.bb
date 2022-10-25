; ID: 902
; Author: Rogue Vector
; Date: 2004-02-03 07:56:24
; Title: Loading Screen with Progress Bar
; Description: A Loadscreen module and test program

.CLASS_LoadScreen 


.INFO
;<Graphics mode and buffer must be set before using this class>
;<Open Source Code>
;<By Rogue Vector 2004>


.CONSTANTS
Const LOADING_SCR=0
Const SHUTDOWN_SCR=1
Const FAILURE=0
Const SUCCESS=1
  

.PUBLIC
;<NONE>


.PROTECTED
Type TLoadScreen
	
	Field Protected_mImg%
	Field Protected_mImgPosX#
	Field Protected_mImgPosY#
	Field Protected_mImgWidth#
	Field Protected_mImgHeight#
	Field Protected_mSnd%
	Field Protected_mSndVol#
	Field Protected_mSndChn%
	Field Protected_mScrMidX#
	Field Protected_mScrMidY#
	Field Protected_mBarPosX#
	Field Protected_mBarPosY#
	Field Protected_mBarWidth#
	Field Protected_mBarHeight#
	Field Protected_mFrameWidth#
	Field Protected_mFrameHeight#
	Field Protected_mCurrentProgress%
	Field Protected_mPercentage%
	Field Protected_mFont%
	Field Protected_mMaxObjects%
	Field Protected_mBarMaxWidth#
	Field Protected_mBarMaxHeight#
	Field Protected_mBarVertOffset#
	Field Protected_mTextVertOffset#
	Field Protected_mTextHorizOffset#
	
End Type


.CONSTRUCTORS
Function LoadScreen_Create.TLoadScreen(v_maxobjects%, v_maxbarheight#, v_maxbarwidth#, v_barvertoffset#, v_textvertoffset#)

	Local l_object.TLoadScreen = New TLoadScreen
	
	l_object\Protected_mImg=0
	l_object\Protected_mImgPosX=0.0
	l_object\Protected_mImgPosY=0.0
	l_object\Protected_mImgWidth#=0.0
	l_object\Protected_mImgHeight#=0.0
	l_object\Protected_mSnd=0
	l_object\Protected_mSndVol=0.0
	l_object\Protected_mSndChn=0
	l_object\Protected_mScrMidX = Float(GraphicsWidth())  / 2
	l_object\Protected_mScrMidY = Float(GraphicsHeight()) / 2
	l_object\Protected_mBarPosX = l_object\Protected_mScrMidX - Float(v_maxbarwidth) / 2
	l_object\Protected_mBarPosY = GraphicsHeight() - Float(GraphicsHeight() * v_barvertoffset)
	l_object\Protected_mBarWidth   = v_maxbarwidth
	l_object\Protected_mBarHeight  = v_maxbarheight
	l_object\Protected_mFrameWidth = v_maxbarwidth  + 4
	l_object\Protected_mFrameHeight= v_maxbarheight + 4
	l_object\Protected_mCurrentProgress=0
	l_object\Protected_mPercentage=0
	l_object\Protected_mFont=0
	l_object\Protected_mMaxObjects      = v_maxobjects
	l_object\Protected_mBarMaxWidth     = v_maxbarwidth
	l_object\Protected_mBarMaxHeight    = v_maxbarheight
	l_object\Protected_mBarVertOffset   = Float( GraphicsHeight() * v_barvertoffset)
	l_object\Protected_mTextVertOffset  = GraphicsHeight() - Float(GraphicsHeight() * v_textvertoffset)
	l_object\Protected_mTextHorizOffset = Float(GraphicsWidth()/2)

	
	Return l_object

End Function


.DESTRUCTORS
Function LoadScreen_Destroy(v_object.TLoadScreen)
	
	If (Handle v_object)
	
		If (v_object\Protected_mImg)  Then FreeImage v_object\Protected_mImg
		If (v_object\Protected_mSnd)  Then FreeSound v_object\Protected_mSnd
		If (v_object\Protected_mFont) Then FreeFont  v_object\Protected_mFont
		
		Delete v_object
	
		Return SUCCESS
			
	EndIf
	
	Return FAILURE

End Function


Function LoadScreen_DestroyAll()
	
	Local l_tmp.TLoadScreen
	
	For l_tmp = Each TLoadScreen
	
		If (l_tmp\Protected_mImg)  Then FreeImage l_tmp\Protected_mImg
		If (l_tmp\Protected_mSnd)  Then FreeSound l_tmp\Protected_mSnd
		If (l_tmp\Protected_mFont) Then FreeFont  l_tmp\Protected_mFont

		Delete l_tmp

	Next

	Return SUCCESS

End Function


.METHODS
Function LoadScreen_SetImage(v_object.TLoadScreen, v_file$)

	If (Handle v_object)
			
		v_object\Protected_mImg       = LoadImage(v_file)
		v_object\Protected_mImgWidth  = ImageWidth(v_object\Protected_mImg)
		v_object\Protected_mImgHeight = ImageHeight(v_object\Protected_mImg)
		v_object\Protected_mImgPosX   = v_object\Protected_mScrMidX - v_object\Protected_mImgWidth  / 2
		v_object\Protected_mImgPosY   = v_object\Protected_mScrMidY - v_object\Protected_mImgHeight / 2
				
		Return SUCCESS
	
	EndIf
	
	Return FAILURE

End Function


Function LoadScreen_Update(v_object.TLoadScreen, v_showPercentage%=False, v_red%=255, v_green%=255, v_blue%=255)

	If (Handle v_object)
		
		Cls		
		DrawImage v_object\Protected_mImg, v_object\Protected_mImgPosX, v_object\Protected_mImgPosY
		v_object\Protected_mCurrentProgress = v_object\Protected_mCurrentProgress + 1
		v_object\Protected_mPercentage      = Int(v_object\Protected_mCurrentProgress*100 / v_object\Protected_mMaxObjects)
		v_object\Protected_mBarWidth        = Float(v_object\Protected_mBarMaxWidth * v_object\Protected_mPercentage) / 100  
		Color v_red, v_green, v_blue
		Rect v_object\Protected_mBarPosX-2, v_object\Protected_mBarPosY-2, v_object\Protected_mFrameWidth, v_object\Protected_mFrameHeight, 0
		Rect v_object\Protected_mBarPosX,   v_object\Protected_mBarPosY,   v_object\Protected_mBarWidth,   v_object\Protected_mBarHeight,   1

		If (v_showPercentage) Text v_object\Protected_mTextHorizOffset, v_object\Protected_mTextVertOffset, Str(v_object\Protected_mPercentage) + "%", True

		Flip

		Return SUCCESS
		
	End If
	
	Return FAILURE

End Function
	

Function LoadScreen_SetFont(v_object.TLoadScreen, v_TrueTypeFont$, v_height%, v_bold%=False, v_italic%=False, v_underline%=False)

	If (Handle v_object)

		v_object\Protected_mFont = LoadFont(v_TrueTypeFont, v_height, v_bold, v_italic, v_underline)
		SetFont v_object\Protected_mFont

		Return SUCCESS

	EndIf

	Return FAILURE

End Function


Function LoadScreen_SetText(v_object.TLoadScreen, v_textvertoffset#, v_texthorizoffset#, v_default%=True)

	If (Handle v_object)
	
		If (v_default)
		
			v_object\Protected_mTextHorizOffset = Float(GraphicsWidth()/2)
		
		Else
					
			v_object\Protected_mTextVertOffset  = GraphicsHeight() - Float(GraphicsHeight() * v_textvertoffset)
			v_object\Protected_mTextHorizOffset = GraphicsWidth()  - Float(GraphicsWidth()  * v_texthorizoffset)
		
		EndIf
			
		Return SUCCESS
		
	EndIf 

	Return FAILURE
	
End Function


Function LoadScreen_SetSound(v_object.TLoadScreen, v_file$="", v_vol#=0.3)

	If (Handle v_object) And (v_file<>"")

		v_object\Protected_mSnd    = LoadSound(v_file) 
		v_object\Protected_mSndVol = v_vol
		v_object\Protected_mSndChn = PlaySound(v_object\Protected_mSnd)
		ChannelVolume v_object\Protected_mSndChn, 0.0
		PauseChannel  v_object\Protected_mSndChn

		Return SUCCESS

	EndIf

	Return FAILURE

End Function


Function LoadScreen_PlaySound(v_object.TLoadScreen)

	If (Handle v_object) And (v_object\Protected_mSnd<>0) 
	
		ChannelVolume v_object\Protected_mSndChn, v_object\Protected_mSndVol
		ResumeChannel v_object\Protected_mSndChn
				
		Return SUCCESS
	
	EndIf
	
	Return FAILURE

End Function




.ENDCLASS_LoadScreen






;Test program for the LoadScreen Class
;By Rogue Vector 2004

Graphics3D 800, 600, 16, 2
SetBuffer BackBuffer()

Include "LoadScreen.bb"

;Create a new load screen
Global myLoadScreen.TLoadScreen = LoadScreen_Create(10, 8, 150, 0.38, 0.35 )
If (LoadScreen_SetImage(myLoadScreen, "loading.jpg"))    Then DebugLog "LoadScreen_SetImage was a SUCCESS" Else DebugLog "LoadScreen_SetImage was a FAILURE"
If (LoadScreen_SetFont(myLoadScreen, "Arial", 20, True)) Then DebugLog "LoadScreen_SetFont was a SUCCESS"  Else DebugLog "LoadScreen_SetFont was a FAILURE"
If (LoadScreen_SetSound(myLoadScreen, "freewaresnd.mp3" )) Then DebugLog "LoadScreen_SetSound was a SUCCESS"  Else DebugLog "LoadScreen_SetSound was a FAILURE"
If (LoadScreen_PlaySound(myLoadScreen)) Then DebugLog "LoadScreen_PlaySound was a SUCCESS"  Else DebugLog "LoadScreen_PlaySound was a FAILURE"

;Simulate a loading sequence - Maximum of 10 objects to load
LoadScreen_Update(myLoadScreen, True, 0, 98, 39) ;1
Delay 1000 : LoadScreen_Update(myLoadScreen, True, 0, 98, 39) ;2
Delay 1000 : LoadScreen_Update(myLoadScreen, True, 0, 98, 39) ;3
Delay 1000 : LoadScreen_Update(myLoadScreen, True, 0, 98, 39) ;4
Delay 1000 : LoadScreen_Update(myLoadScreen, True, 0, 98, 39) ;5
Delay 1000 : LoadScreen_Update(myLoadScreen, True, 0, 98, 39) ;6
Delay 1000 : LoadScreen_Update(myLoadScreen, True, 0, 98, 39) ;7
Delay 1000 : LoadScreen_Update(myLoadScreen, True, 0, 98, 39) ;8
Delay 1000 : LoadScreen_Update(myLoadScreen, True, 0, 98, 39) ;9
Delay 1000 : LoadScreen_Update(myLoadScreen, True, 0, 98, 39) ;10	
	
Delay 1000
Color 155,155,155
Text GraphicsWidth()/2, GraphicsHeight() - 160, "Hit the <space> bar to continue",True : Flip
While Not KeyHit(57) : Wend

If (LoadScreen_Destroy(myLoadScreen)) Then DebugLog "Load screen object was destroyed..."
;Delay 1000

EndGraphics

End
