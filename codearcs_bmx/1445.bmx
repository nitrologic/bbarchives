; ID: 1445
; Author: Filax
; Date: 2005-08-18 05:25:33
; Title: Fade In/Out and CrossFade library
; Description: Fade In/Out and CrossFade library

Type Object_Fade
	Field Px:Int
	Field Py:Int
	Field Tx:Int
	Field Ty:Int
	
	Field Red:Byte
	Field Green:Byte
	Field Blue:Byte
	
	Field ImageScr:Timage
	Field ImageDes:Timage
	
	Field FadeType:Int
	Field FadeStart:Int=False
	
	Field AlphaValue:Float

	Field AlphaRate:Int
	Field AlphaTimer:Fade_Timer
	
	' ----------------------------------
	' Permet de créer un fade de couleur
	' ----------------------------------
	Function CreateColorFade:Object_Fade(Red:Byte=10,Green:Byte=10,Blue:Byte=10)
		Local F:Object_Fade = New OBject_Fade
		F.Px=0
		F.Py=0
		F.Tx=GraphicsWidth()
		F.Ty=GraphicsHeight()
		
		F.Red=Red
		F.Green=Green
		F.Blue=Blue
		
		F.AlphaValue=1
		F.FadeType=1
		
		F.AlphaRate=15
		F.AlphaTimer=Fade_Timer.Create(F.AlphaRate)
		
		Return F
	End Function
	
	' -------------------------------------
	' Permet de créer un cross fade d'image
	' -------------------------------------
	Function CreateCrossFade:Object_Fade(Source:Timage,Destination:Timage)
		Local F:Object_Fade = New OBject_Fade
		F.Px=0
		F.Py=0
		F.Tx=GraphicsWidth()
		F.Ty=GraphicsHeight()

		F.Red=255
		F.Green=255
		F.Blue=255
	
		F.ImageScr=Source
		F.ImageDes=Destination
		
		F.AlphaValue=0
		F.FadeType=3
		
		F.AlphaRate=15
		F.AlphaTimer=Fade_Timer.Create(F.AlphaRate)
		
		Return F
	End Function
	
	' --------------------------
	' Permet de démarrer un fade
	' --------------------------
	Method Start()
		FadeStart=True
	End Method
	
	' ------------------------
	' Permet de stoper un fade
	' ------------------------
	Method Stop()
		FadeStart=False
	End Method
	
	' ---------------------------------------
	' Permet de tester si un fade est terminé
	' ---------------------------------------
	Method Test()
		Return FadeStart
	End Method

	' ------------------------------------------
	' Permet de convertir un fade out en fade in
	' ------------------------------------------
	Method ConvertToFadeIn()
		FadeType=1
	End Method
		
	' ------------------------------------------
	' Permet de convertir un fade in en fade out
	' ------------------------------------------
	Method ConvertToFadeOut()
		FadeType=2
	End Method
	
	' -----------------------------------------------------------------
	' Permet de changer les image source et destination d'un cross fade
	' -----------------------------------------------------------------
	Method CrossFadeSwitch(Source:Timage,Destination:Timage)
		ImageScr=Source
		ImageDes=Destination
		AlphaValue=0
	End Method
	
	' ------------------------------
	' Permet de rafraichir les fades
	' ------------------------------
	Method Redraw()
		Select FadeType
		' -------
		' Fade In
		' -------
		Case 1
			If FadeStart=True Then
				If AlphaTimer.TestEnd()=True Then
					AlphaValue=AlphaValue-0.005
					AlphaTimer=Fade_Timer.Create(AlphaRate)
				EndIf
				
				If AlphaValue<=0 Then 
					AlphaValue=0
					FadeStart=False
				EndIf
			EndIf
			
			SetBlend AlphaBlend
			SetAlpha AlphaValue
			SetColor Red,Green,Blue
			
			DrawRect Px,Py,Tx,Ty
		
		' --------
		' Fade Out
		' --------
		Case 2
			If FadeStart=True Then
				If AlphaTimer.TestEnd()=True Then
					AlphaValue=AlphaValue+0.005
					AlphaTimer=Fade_Timer.Create(AlphaRate)
				EndIf
				
				If AlphaValue>=1 Then 
					AlphaValue=1
					FadeStart=False
				EndIf
			EndIf
			
			SetBlend AlphaBlend
			SetAlpha AlphaValue
			SetColor Red,Green,Blue
			
			DrawRect Px,Py,Tx,Ty
			
		' ----------
		' Cross Fade 
		' ----------
		Case 3
			If FadeStart=True Then
				If AlphaTimer.TestEnd()=True Then
					AlphaValue=AlphaValue+0.005
					AlphaTimer=Fade_Timer.Create(AlphaRate)
				EndIf
				
				If AlphaValue>=1 Then 
					AlphaValue=1
					FadeStart=False
				EndIf
			EndIf
			
			SetBlend AlphaBlend
			SetColor Red,Green,Blue
			
			DrawImage ImageScr,Px,Py	
			
			SetAlpha AlphaValue		
			DrawImage ImageDes,Px,Py				
		End Select
		
		' -----------------------------
		' On reset les param par defaut
		' -----------------------------
		SetColor 255,255,255		
		SetBlend SolidBlend
		SetAlpha 1
	End Method
End Type

' ---------------------------------
' Multitask timer for gadget events
' ---------------------------------
Type Fade_Timer
	Field Start:Int
	Field TimeOut:Int
	
	' ----------------
	' Define the timer
	' ----------------
	Function Create:Fade_Timer(Out:Int) 
		Local NewTimer:Fade_Timer
	
		NewTimer = New Fade_Timer
		NewTimer.Start = MilliSecs() 
		NewTimer.TimeOut = NewTimer.Start + Out
	
		Return NewTimer
	End Function

	' --------------
	' Free the timer
	' --------------	
	Function Freetimer(Timer:Fade_Timer)
		Timer=Null	
	End Function

	' ------------------
	' Test the timer end
	' ------------------
	Method TestEnd()
		If TimeOut < MilliSecs()
			Freetimer(Self)
			Return True
		Else
			Return False
		EndIf
	End Method
End Type


How to use ? :


Framework brl.d3d7max2d
Import brl.jpgloader

' modules which may be required:
' Import brl.pngloader
' Import brl.bmploader
' Import brl.tgaloader
SetGraphicsDriver D3D7Max2DDriver()



Include "Inc_Fade.bmx"

Graphics 800,600,32,60

pic1=LoadImage("Background01.jpg")
pic2=LoadImage("Background02.jpg")
pic3=LoadImage("Background03.jpg")


' ----------------------------
' Fondu du noir vers l'image 1
' ----------------------------
MyFade01:Object_Fade=Object_Fade.CreateColorFade()
MyFade01.Start()

While MyFade01.Test()=True
	Cls
	DrawImage Pic1,0,0
	MyFade01.Redraw()	
	Flip
Wend


' ---------------------------------
' Fondu de l'image 1 vers l'image 2
' ---------------------------------
MyFade02:Object_Fade=Object_Fade.CreateCrossFade(Pic1,Pic2)
MyFade02.Start()

While MyFade02.Test()=True
	Cls
	MyFade02.Redraw()	
	Flip
Wend


' ---------------------------------
' Fondu de l'image 2 vers l'image 3
' ---------------------------------
MyFade02.CrossFadeSwitch(Pic2,Pic3)
MyFade02.Start()

While MyFade02.Test()=True
	Cls
	MyFade02.Redraw()	
	Flip
Wend


' ---------------------------------
' Fondu de l'image 3 vers l'image 2
' ---------------------------------
MyFade02.CrossFadeSwitch(Pic3,Pic2)
MyFade02.Start()

While MyFade02.Test()=True
	Cls
	MyFade02.Redraw()	
	Flip
Wend

' ---------------------------------
' Fondu de l'image 2 vers l'image 1
' ---------------------------------
MyFade02.CrossFadeSwitch(Pic2,Pic1)
MyFade02.Start()

While MyFade02.Test()=True
	Cls
	MyFade02.Redraw()	
	Flip
Wend

' -----------------------------
' Fondu de l'image vers le noir
' -----------------------------
MyFade01.ConvertToFadeOut()
MyFade01.Start()

While MyFade01.Test()=True
	Cls
	DrawImage Pic1,0,0
	MyFade01.Redraw()	
	Flip
Wend
	
WaitKey
