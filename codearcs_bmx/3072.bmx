; ID: 3072
; Author: Pineapple
; Date: 2013-08-27 18:11:36
; Title: Mouse handling
; Description: More flexible than polledinput

' 	--+-----------------------------------------------------------------------------------------+--
'	  |   This code was originally written by Sophie Kirschner (sophiek@pineapplemachine.com)   |  
' 	  | It is released as public domain. Please don't interpret that as liberty to claim credit |  
' 	  |   that isn't yours, or to sell this code when it could otherwise be obtained for free   |  
'	  |                because that would be a really shitty thing of you to do.                |
' 	--+-----------------------------------------------------------------------------------------+--


SuperStrict

Import brl.polledinput
Import brl.system

Rem
	
	Useful globals:
	
	mx : mouse X position
	my : mouse Y position
	
	mxv : mouse X velocity (change since last update)
	myv : mouse Y velocity
	
	lmb : left mouse button CLICKED (has just been pressed down)
	rmb : right mouse button CLICKED
	mmb : middle mouse button CLICKED
	
	lmbx : mouse X position at last left mouse button CLICKED
	lmby : mouse Y position at last left mouse button CLICKED
	rmbx : mouse X position at last right mouse button CLICKED
	rmby : mouse Y position at last right mouse button CLICKED
	mmbx : mouse X position at last middle mouse button CLICKED
	mmby : mouse Y position at last middle mouse button CLICKED
		
	lmbd : left mouse button DOWN (is currently held down)
	rmbd : right mouse button DOWN
	mmbd : middle mouse button DOWN
	
	lmbu : left mouse button UP (has just been released)
	rmbu : right mouse button UP
	mmbu : middle mouse button UP
	
	lmbc : left mouse button DOUBLE-CLICK (has just been double-clicked)
	rmbc : right mouse button DOUBLE-CLICK
	mmbc : middle mouse button DOUBLE-CLICK
	
	lmbtime : the last time that the left mouse button was CLICKED (on the system millisecs clock)
	rmbtime : the last time that the right mouse button was CLICKED
	mmbtime : the last time that the middle mouse button was CLICKED
	
	doubleclickdelay : maximum number of milliseconds between two clicks in order to register as a double-click
	
	mz : change in mouse z position (since last update)
	nowmz : current mouse z position
	
EndRem 

Global mx%,my%
Global mxv%=0,myv%=0
Global lmb%,rmb%,mmb%	' clicked
Global lmbd%,rmbd%,mmbd%	' down
Global lmbu%,rmbu%,mmbu%	' up
Global lmbc%,rmbc%,mmbc%	' double-click
Global lmbx%,lmby%,rmbx%,rmby%,mmbx%,mmby%
Global l_lmb%,l_rmb%,l_mmb%
Global lmbtime%,rmbtime%,mmbtime%
Global mz%,nowmz%,lastmz%
Global doubleclickdelay%=320

Function UpdateMouse()
	Local now%=MilliSecs()
	Local nmx%=MouseX(),nmy%=MouseY()
	mxv=nmx-mx;myv=nmy-my
	mx=nmx;my=nmy
	Local lastl%=lmbd,lastr%=rmbd,lastm%=mmbd
	lmbd=MouseDown(1)
	rmbd=MouseDown(2)
	mmbd=MouseDown(3)
	nowmz=MouseZ()
	mz=lastmz-nowmz
	lastmz=nowmz
	lmb=0;rmb=0;mmb=0
	If lastl>lmbd Then lmbu=1 Else lmbu=0
	If lastr>rmbd Then rmbu=1 Else rmbu=0
	If lastm>mmbd Then mmbu=1 Else mmbu=0
	lmbc=0;rmbc=0;mmbc=0
	If lastl<lmbd Then 
		If now-l_lmb<=doubleclickdelay Then
			lmbc=1
		Else
			l_lmb=now
		EndIf
		lmb=1
		lmbx=mx;lmby=my
		lmbtime=MilliSecs()
	EndIf
	If lastr<rmbd Then 
		If now-l_rmb<=doubleclickdelay Then
			rmbc=1
		Else
			l_rmb=now
		EndIf
		rmb=1
		rmbx=mx;rmby=my
		rmbtime=MilliSecs()
	EndIf
	If lastm<mmbd Then 
		If now-l_mmb<=doubleclickdelay Then 
			mmbc=1
		Else
			l_mmb=now
		EndIf
		mmb=1
		mmbx=mx;mmby=my
		mmbtime=MilliSecs()
	EndIf
End Function

' Convenience function, returns boolean whether the mouse is inside a rectangular space
Function MouseIn%(x%,y%,w%,h%)
	If mx=>x And mx<x+w And my=>y And my<y+h Then Return 1
	Return 0
End Function



' Example code

Rem

Graphics 320,320

Global vx%,vy%

Repeat
	Cls
	updatemouse()
	
	vx=12;vy=12
	
	val "mx",mx
	val "my",my
	val "mxv",mxv
	val "myv",myv
	val "lmb",lmb,1
	val "rmb",rmb,1
	val "mmb",mmb,1
	val "lmbx",lmbx
	val "lmby",lmby
	val "rmbx",rmbx
	val "rmby",rmby
	val "mmbx",mmbx
	val "mmby",mmby
	val "lmbd",lmbd
	val "rmbd",rmbd
	val "mmbd",mmbd
	val "lmbu",lmbu,1
	val "rmbu",rmbu,1
	val "mmbu",mmbu,1
	val "lmbc",lmbc,1
	val "rmbc",rmbc,1
	val "mmbc",mmbc,1
	val "lmbtime",lmbtime
	val "rmbtime",rmbtime
	val "mmbtime",mmbtime
	val "mz",mz,1
	val "nowmz",nowmz
	
	Flip
	Delay 15
Forever

Function val(t$,v%,highlight%=0)
	If highlight And v SetColor 0,255,0 Else SetColor 255,255,255
	DrawText t+": "+v,vx,vy
	vy:+12
	If vy>GraphicsHeight()-24 Then vy=12;vx:+128
End Function

EndRem
