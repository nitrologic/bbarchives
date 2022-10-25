; ID: 2478
; Author: Jim Brown
; Date: 2009-05-11 13:57:46
; Title: Parameter Editor
; Description: Parameter Editor

Rem
	Title: Parameter Editor
	Author: J Brown
	
	About
	--------
	This editor allows you to easily adjust code variables during program
	execution via an on-screen editor. Simply assign an existing variables
	(FLOAT types only) then set the lowest and highest values in which
	the variable may be adjusted to
	
	You can add as many variables as you wish and they will be displayed
	on-screen for editing simultaneously (subject to screen resolution)
	
	The editor is mouse controlled. You can slide the bar(s) via
	dragging with the LEFT mouse button. Alternatively, roll the
	mouse wheel whilst over a bar to fine-adjust the value
	
	Example usage
	------------------
	To use this in your own code copy this file ("Param.bmx") to the
	same directory then enter this line in your code:
	
		Import "Param.bmx"
		
	This example shows how two existing varables are added for editing
	The variables are 'mysprriteX' and 'myspriteY'
	They need to be declared before adding to the editor since the editor
	mearly points to the variables and adjusts them directly
	
	' create two variables
	myspriteX#=200 ; myspriteY#=225
	
	' add them to the editor for user-controlled adjusting during execution
	Param.Add(Varptr myspriteX , "X-Pos",20,300)
	Param.Add(Varptr myspriteY , "Y-Pos",150,250)
		
	The two variable are added to the editor
	Each is given a unique label for clarity during on-screen editing
	Minimum and maximum values are also set so the user can only change
	values within these limits. Note that Minimum can be negative
	
	During execution you can switch the editor on and off as follows:
	
	Param.ShowEditor    ' turn the editor ON
	Param.HideEditor    ' turn the editor OFF
	Param.ToggleEditor  ' toggles the editor to ON or OFF
	
	You need to call Param.Update during your main code loop so that
	all mouse editing and parameter adjustments take place
	
	See below for other functions available
	Refer to "Param Example.bmx" for a demo of the editor in use
EndRem

SuperStrict

Type Param
	Global BarWidth%=400 , BarHeight%=16 , XPos%=160 , YPos%=4
	Global MX%,MY%,cY% , Current:Param
	Global list:TList=New TList
	Global EditMode% , MouseWheelValue%
	Global BackR%=$22,BackG%=$22,BackB%=$10
	Global ForeR%=$ff,ForeG%=$ff,ForeB%=$a0
	Global Alpha#=0.6
	Field name$,val:Float Ptr,lo#,hi#
	' add a parameter for editing (only FLOAT types are handled)
	Function Add(value:Float Ptr, pname$,loval#=0.0,hival#=100.0)
		Local p:Param=New Param
		Param.list.AddLast p
		p.name$=pname$ ; p.lo=loval ; p.hi=hival
		If p.lo>=p.hi p.lo=p.hi-0.01
		p.val=value
		If p.val[0]>p.hi p.val[0]=p.hi
		If p.val[0]<p.lo p.val[0]=p.lo
	End Function
	' check if mouse is over this parameter editing bar
	Method IsMouseOver%()
		If Param.MX>=Param.XPos And Param.MX<=Param.XPos+Param.BarWidth
			If Param.MY>=Param.cY And Param.MY<=Param.cY+Param.BarHeight
				Return True
			EndIf
		EndIf
	End Method
	' udpate editor and associated parameters
	Function Update()
		Global percent%
		If Param.EditMode=False Return
		SetRotation 0.0 ; SetScale 1.0,1.0 ; SetBlend ALPHABLEND
		SetAlpha Param.Alpha
		Param.cY%=Param.YPos ; 
		If Param.MX<>MouseX() Or Param.MY<>MouseY() Param.MouseWheelValue=MouseZ()
		Param.MX=MouseX() ; Param.MY=MouseY()
		For Local p:param=EachIn Param.list
			' is mouse over bar when mouse wheel is rolled?
			If MouseZ()<>Param.MouseWheelValue
				If p.IsMouseOver()
					p.val[0]:+(Float(MouseZ()-Float(Param.MouseWheelValue)))/100.0
					Param.MouseWheelValue=MouseZ()
				EndIf
			EndIf
			' control updating when mouse Left button is down
			If MouseDown(1)
				If Param.Current=p
						percent=(Param.MX-Param.XPos)/(Float(Param.BarWidth)/100.0)
						If percent<0 percent=0
						If percent>100 percent=100
						p.val[0]=((p.hi-p.lo)*(Float(percent)/100.0))+p.lo	
				Else
					If p.IsMouseOver()
						If Param.Current=Null	Param.Current=p
					EndIf
				EndIf
			EndIf
			If MouseDown(1)=0
				Param.Current=Null
			EndIf
			' render
			SetColor Param.BackR,Param.BackG,Param.BackB
			DrawRect Param.XPos-2 , Param.cY , Param.BarWidth+4 , Param.BarHeight
			SetColor Param.ForeR,Param.ForeG,Param.ForeB
			percent=Abs((p.val[0]-p.lo)/(p.hi-p.lo))*100.0
			DrawRect Param.XPos , Param.cY+1 , percent*(Float(Param.BarWidth)/100.0) , Param.BarHeight-2
			DrawText p.name,Param.XPos-TextWidth(p.name)-4,Param.cY+(Param.BarHeight/2)-TextHeight(p.name)/2
			DrawText p.val[0],Param.XPos+Param.BarWidth+4,cY+(Param.BarHeight/2)-TextHeight(p.name)/2
			Param.cY:+Param.BarHeight+2
		Next
	End Function
	' switch mode so that editor is active (drawn to screen)
	Function ShowEditor()
		Param.EditMode=True
	End Function
	' switch mode so that editor is inactive (not drawn to screen)
	Function HideEditor()
		Param.EditMode=False
	End Function
	' toggle the editors ON/OFF state
	Function ToggleEditor()
		Param.EditMode=Not Param.EditMode
	End Function
	' set the overall editor size
	' w=length of each bar and h=height of each bar
	Function SetSize(w%,h%)
		Param.BarWidth%=w ; Param.BarHeight%=h
	End Function
	' set the editors x and y offset positions
	Function SetPos(x%,y%)
		Param.XPos=x ; Param.YPos=y
	End Function
	' set the editors foregroung color
	Function SetForeColor(r%,g%,b%)
		Param.ForeR=r ; Param.ForeG=g ; Param.ForeB=b
	End Function
	' set the editors background color
	Function SetBackColor(r%,g%,b%)
		Param.BackR=r ; Param.BackG=g ; Param.BackB=b
	End Function
End Type



!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
SPLIT CODE HERE
Below this is the 'demo' code
!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!




' parameter editor demo

SuperStrict

?Win32
Framework BRL.D3D7Max2D
?Linux
Framework BRL.GLMax2D
?MacOS
Framework BRL.GLMax2D
?

Import "Param.bmx"

Const sw%=640,sh%=340
AppTitle = "Parameter Editor - Example"
Graphics sw,sh

Global SineH#=88.0 ' height of sinewave
Global SineF#=5.0 ' frequency of sinewave
Global SineW#=6.5 ' thickness of sinewave

Rem
	Add the above three variables for editing
	Each Param.Add requires the following details:
		pointer to variable for adjusting
		a label description (appears in the onscreen editor)
		the minimum value in which the variable can be adjusted to
		the maximum value in which the variable can be adjusted to
EndRem

Param.Add(Varptr SineH , "Height",12,155)
Param.Add(Varptr SineF , "Freq",1,14)
Param.Add(Varptr SineW , "Thickness",2.0,14.0)

' show the editor on screen
Param.ShowEditor
' change the 'y' position of the editor (Default=4)
Param.YPos=10
' change the default width and height of each bar
Param.SetSize 340,14

' ******************************************

Repeat
	Cls
	'
	SetClsColor 132,163,142
	SetColor 25,30,51
	Local f#=10
	' draw sinewave
	Repeat
		DrawOval f,(sh/2)+Cos(SineF*f)*SineH , SineW,SineW
		f:+1
	Until f>750
	' show text info
	SetColor $ff,$ff,$ff
	DrawText "Hold/Click left mouse button over parameter to adjust",20,sh-56
	DrawText "For fine adjustment roll mouse wheel over parameter",20,sh-40
	DrawText "Toggle parameter editing on/off with right mouse button",20,sh-20
	' use right mouse button to toggle editor on/off
	If MouseHit(2) Param.ToggleEditor
	' call this at each loop to update the variables when user changes sliders
	' Note, values are only adjusted when the editor is on-screen
	Param.Update
	Flip
Until KeyHit(KEY_ESCAPE)

' ******************************************

End
