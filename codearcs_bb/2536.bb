; ID: 2536
; Author: Malice
; Date: 2009-07-18 15:20:26
; Title: 3D Font /Text comands
; Description: To replace slow 2D Text commands

;;; Example (Remove from include)

Graphics3D 800,600
SetBuffer BackBuffer()

;SpriteControl Initialisation
Global spritepivot,spritecamera

;BitmapFont Initialisation
Global CurrentFont
	spritecamera=CreateCamera()
	spritepivot=CreateSpritePivot(spritecamera)

InitialiseBitmapFonts()

;Times New Roman, Size 24 and Bold
TimesNewRoman=LoadFont3D("Times new Roman",24,True)

;Arial, Size 20 and Italic
Arial=LoadFont3D("Arial",20,False,True)

;Verdana, Size 36, Bold and Underline
Verdana=LoadFont3D("Verdana",36,True,False,True)

SetFont3D(TimesNewRoman)
TimesNewRomanSprite=Text3D(400,150,"Times New Roman")
SetFont3D(Arial)
ArialSprite=Text3D(400,300,"Arial")
SetFont3D(Verdana)
VerdanaSprite=Text3D(400,450,"Verdana")
			
FreeAll3DFonts
			
While Not KeyDown(1)
	UpdateWorld
	RenderWorld
	Flip
Wend

	






















































; This code owes much to Jim Brown for SpriteControl.

; ************************ SPECIAL NOTICE FOR SPRITECONTROL USERS!!!! ************************

; The following functions overwrite those of SpriteControl in case of duplicates, please keep these ones and do not
; 		include the duplicate. Provided any 3D Text routines use the functions from here, there should be no conflicts
;		with any other functionality of SpriteControl.

; Text3D
; ModifyText3D

;The following are identical to their original sources:
; DrawImage3D
; ImageToSprite
; CreateSpritePivot
; CreateImage3D

;		SpriteControl users may also require to remove the declaration of "Global spritepivot,spritecamera" from this library.










; Notes on Use:

; Include this file into your program.

; It is important to call the "InitialiseBitmapFonts()" function before using any of the 3D Text commandset.
; Once this has been done, the full functionality can be explored.

; Either LoadExisting bitmap fonts created with the "MAKEFONT.bb" code ( http://www.blitzbasic.com/codearcs/codearcs.php?code=2533#comments )
; 		with the LoadBitmapFont() command, ehich will return a valid Handle for use with SetFont3D()
;		Alternatively To create and utilise fonts during runtime, use the LoadFont3D() command. Again, this will return
;		a handle for use.

; Just as with Blitz3D's standard 2D Font/Text commands, once a Font is loaded, it can be:
;	Free'd					-		FreeFont3D( FontHandle )
;	Set as current Font		-		SetFont3D( FontHandle )

;	To display text in the currently set Font, use the Text3D() command.























;SpriteControl Initialisation - ONLY COMMENTED OUT DUE TO EXAMPLE!
;Global spritepivot,spritecamera

;Bitmap Font Initialisation - ONLY COMMENTED OUT DUE TO EXAMPLE!
;Global CurrentFont

Type BitmapFont
	Field Image%
	Field CharWidth%
	Field CharHeight%
End Type


; Bitmap Font Functions;					########################################################################################

; Similar to how Blitz3D handles Fonts normally, as a safety precaution, the initialisation function
; Ensures we have a valid font set as "CurrentFont". This same font is used if SetFont is used on an invalid font too.
Function InitialiseBitmapFonts()
	FreeAll3DFonts()
	CurrentFont=LoadFont3D("Blitz",12)
End Function

Function FreeAll3DFonts()
	Local AllFonts.BitmapFont
	For AllFonts = Each BitmapFont
		If (AllFonts.BitmapFont <> Null)
			If (AllFonts\Image)
				FreeImage AllFonts\Image
				AllFonts\Image = False
				AllFonts\CharWidth=False
				AllFonts\CharHeight=False
			End If
		End If
		Delete AllFonts.BitmapFont
	Next
End Function

Function SetFont3D(FontImageHandle%)
	If (FontImageHandle)
		CurrentFont=FontImageHandle
	Else
		SetFontDefault3D()
	End If
End Function

Function LoadFont3D(RealFontName$,MyFontHeight=14,MyFontBold=False,MyFontItalic=False,MyFontULine=False)
	If (RealFontName$="")
		RealFontName$="Blitz"
	End If
	Local FontPath$=SystemProperty ("windowsdir") +"Fonts\"+RealFontName$+".ttf"
	Local MyFont = LoadFont(FontPath$,MyFontHeight,MyFontBold,MyFontItalic,MyFontULine)
	
		If (Not(MyFont))
			Return False
		End If	
	
	SetFont MyFont	
	MyFontImage=CreateFontImage()
	FreeFont MyFont
	MyFont=False
	Return MyFontImage	
End Function

Function LoadBitmapFont(FilePath$)
	Local TempImage=LoadImage(FilePath$)
	Local IterCharsX,IterCharsY
	If (TempImage)
		BMF.BitmapFont= New BitmapFont
		BMF\CharWidth=ImageWidth(TempImage) Shr 4
		BMF\CharHeight=ImageHeight(TempImage) Shr 4
		BMF\Image=CreateImage(CharWidth,CharHeight,256)
		For IterCharsY=0 To 15
			For IterCharsX=0 To 15		
				SetBuffer ImageBuffer(BMF\Image,(IterCharsY*16)+IterCharsX)
				DrawImageRect TempImage,0,0,IterCharsX,IterCharsY,BMF\CharWidth,BMF\CharHeight
			Next
		Next
		SetBuffer BackBuffer
		FreeImage tempImage
		TempImage=False
		CurrentFont=BMF\Image
		Return BMF\Image
	End If
	Return False
End Function

Function GetBitmapFontByImageHandle.BitmapFont(ImageHandle)
	If (ImageHandle)
		For BMF.BitmapFont = Each BitmapFont
			If BMF\Image=ImageHandle
				Return BMF.BitmapFont
				Exit
			End If
		Next
	Else
		Return Null
	End If
End Function

Function FreeFont3D(ImageHandle)	
	If (ImageHandle)
		Free.BitmapFont=GetBitmapFontByImageHandle.BitmapFont(ImageHandle)
		If Free.BitmapFont <> Null
			If (Free\Image)
				FreeImage Free\Image
				Free\Image=False
				Delete Free.BitmapFont
			End If
		End If
	End If

	;Safety check, let's keep the default font in memory.
	Local FirstFont.BitmapFont=First BitmapFont
	If (FirstFont.BitmapFont = Null)
		CurrentFont=LoadFont3D("Arial",12)		
	End If
	If (CurrentFont=ImageHandle)
		SetFontDefault3D()
	End If
End Function

Function BitmapFontText(BitmapFontImageHandle,TextString$)
	TextString$=TextString$+" "
	Local IterChars
	Local Frame
	TextImage.BitmapFont=GetBitmapFontByImageHandle.BitmapFont(BitmapFontImageHandle)
	Local CWidth=TextImage\CharWidth
	For IterChars=1 To Len(TextString$)
		Frame=Asc(Mid$(TextString$,IterChars,1))
		DebugLog Str(Frame)
		DrawImage TextImage\Image,IterChars*CWidth,0,Frame
	Next
End Function

Function SetFontDefault3D()
	Local FirstFont.BitmapFont=First BitmapFont
	
	If FirstFont.BitmapFont = Null
		CurrentFont=LoadFont3D("Blitz",12)
	Else
		CurrentFont=FirstFont\Image
	End If	
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

Function CreateFontImage()

	BMF.BitmapFont= New BitmapFont

	BMF\CharWidth=GetFontCharMaxWidth%()
	BMF\CharHeight=GetFontCharMaxHeight%()
	Local XPlus%=(BMF\CharWidth Shr True)
	Local YPlus%=(BMF\CharHeight Shr True)

	Local Frame%

	BMF\Image=CreateImage(BMF\CharWidth,BMF\CharHeight,256)
		
	For Frame=0 To 255
			
		SetBuffer ImageBuffer(BMF\Image,Frame)
		Text XPlus,YPlus,Chr$(Frame),True,True
	Next
	
	SetBuffer BackBuffer()
	; Returns the Font Image handle to be used as a Handle for the Font itself. useful for checking.
	Return BMF\Image

End Function



























































; Create a single 'sprite pivot' and scale to screen resolution
Function CreateSpritePivot(parentcam=0,dist#=1.0)
	Local gw=GraphicsWidth()
	Local gh=GraphicsHeight()
	spritepivot=CreatePivot(parentcam)
	Local aspect#=Float(gh)/Float(gw)
	Local scale#=2.0/gw
	PositionEntity spritepivot,-1,aspect,dist
	ScaleEntity spritepivot,scale,scale,scale
	NameEntity spritepivot,"SpriteControl Pivot"
	Return spritepivot
End Function

Function Text3D(xpos,ypos,txt$,flags=5,par=-1)
	If par=-1 par=spritepivot
	If txt$="" Then txt$=" "
	If (Not(CurrentFont))
		SetFontDefault3D()
	End If
	CFont.BitmapFont=GetBitmapFontByImageHandle(CurrentFont)
	Local txtwidth=CFont\CharWidth * (Len(Txt$)+1)
	Local txtheight=CFont\CharHeight

	Local gbuffer=GraphicsBuffer()
	Local tmpimage=CreateImage(txtwidth,txtheight)
	SetBuffer ImageBuffer(tmpimage)
	Cls 
	BitmapFontText(CFont\Image,txt$)
	SetBuffer gbuffer
	textsprite=ImageToSprite(tmpimage,flags)
	FreeImage tmpimage
	DrawImage3D textsprite,xpos,ypos
	Return textsprite
End Function

; Modify existing text in quad sprite
Function ModifyText3D(sprite,xpos,ypos,t$,flags=5)
	If t$="" Then t$=" "
	Local par=GetParent(sprite)
	FreeImage3D sprite
	Local newstextsprite=Text3D(xpos,ypos,t$,flags,par)
	Return newsprite
End Function

Function CreateImage3D(w=1,h=1,par=-1)
	If par=-1 par=spritepivot
	Local sprite=CreateMesh(par)
	Local s=CreateSurface(sprite)
	AddVertex s,0,0,0 ,0,0 : AddVertex s,2,0,0 , 1,0
	AddVertex s,0,-2,0 ,0,1 : AddVertex s,2,-2,0 , 1,1
	AddTriangle s,0,1,2 : AddTriangle s,3,2,1
	ScaleEntity sprite,Float(w)/2,Float(h)/2,1
	EntityFX sprite,1+16
	EntityOrder sprite,-100
	PositionEntity sprite,-10000,-10000,0
	Return sprite
End Function

; Position quad sprite at 2D screen coordinates
Function DrawImage3D(sprite,x,y,frame=-99999,z#=0)
	PositionEntity sprite,x+0.5,-y+0.5,z
	If frame<>-99999
		Local tex=GetSpriteTexture(sprite)
		Local en$=EntityName(sprite)
		Local fw#=Float(Mid$(en$,Instr(en$,"W")+1,Instr(en$,"H")-Instr(en$,"W")))
		Local fh#=Float(Mid$(en$,Instr(en$,"H")+1,Instr(en$,"X")-Instr(en$,"H")))
		Local framesH=Int(Mid$(en$,Instr(en$,"X")+1,Instr(en$,"Y")-Instr(en$,"X")))
		Local framesV=Int(Mid$(en$,Instr(en$,"Y")+1,Instr(en$,"E")-Instr(en$,"Y")))
		Local numframes=framesH*framesV
		If numframes>0
			If frame>numframes frame=(frame Mod numframes)
			If frame<1 frame=numframes-(Abs(frame) Mod numframes)
			frame=frame-1
			Local oy#=frame/framesH
			Local ox#=frame-(oy*framesH)
			PositionTexture tex,-ox/fw,-oy/fh
			FreeTexture tex
		EndIf
	EndIf
End Function

; Convert an existing 2D image to quad sprite
Function ImageToSprite(img,texflags=5,numframesX=1,numframesY=1,par=-1)
	If par=1 par=spritepivot
	Local iw=ImageWidth(img) , ih=ImageHeight(img)
	Local tw=2 Shl (Len(Int(Bin(iw-1)))-1)
	Local th=2 Shl (Len(Int(Bin(ih-1)))-1)
	Local tex=CreateTexture(tw,th,texflags)
	Local ib=ImageBuffer(img) : LockBuffer ib
	Local tb=TextureBuffer(tex) : LockBuffer tb
	Local x,y
	For x=0 To iw-1
		For y=0 To ih-1
			rgbc=ReadPixelFast(x,y,ib) And $00ffffff
			If rgbc=((r Shl 16)+(g Shl 8)+b)
				WritePixelFast x,y,($00000000),tb
			Else
				WritePixelFast x,y,(rgbc Or $ff000000),tb
			EndIf
		Next
	Next
	UnlockBuffer ib : UnlockBuffer tb
	ScaleTexture tex,Float(tw)/Float(iw/numframesX),Float(th)/Float(ih/numframesY)
	Local sprite=CreateImage3D(iw,ih,par)
	EntityTexture sprite,tex
	EntityFX sprite,16+1 : EntityOrder sprite,-100
	ScaleEntity sprite,Float(iw/numframesX)/2,Float(ih/numframesY)/2,1
	Return sprite
End Function
