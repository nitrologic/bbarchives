; ID: 3083
; Author: Pineapple
; Date: 2013-09-25 16:36:41
; Title: Key input handling
; Description: More flexible than polledinput

' 	--+-----------------------------------------------------------------------------------------+--
'	  |   This code was originally written by Sophie Kirschner (sophiek@pineapplemachine.com)   |  
' 	  | It is released as public domain. Please don't interpret that as liberty to claim credit |  
' 	  |   that isn't yours, or to sell this code when it could otherwise be obtained for free   |  
'	  |                because that would be a really shitty thing of you to do.                |
' 	--+-----------------------------------------------------------------------------------------+--


SuperStrict

Import brl.polledinput
Import brl.linkedlist

' Example program

Rem

' Try pressing and holding shift and the spacebar down to see how stuff works; a pixel is added every time a press is registered.

Graphics 256,256
Local space:key=createkey(key_space,key.shift,400,0.9)
Local esc:key=createkey(key_escape)
Local x%=0,y%=0
Repeat
	updatemodkeys
	If space.get() Then
		Plot x,y
		x:+1
		If x>=GraphicsWidth() Then x=0;y:+1
	EndIf
	Flip
	If esc.get() Then End
Forever

EndRem



' Backbone of the stuff.
' n 			-		the scancode. (for example, key_space)
' m			-		the modifier. (for example, key.shift)
' d			-		initial delay between presses in ms. (-1 means only register initial keypress.)
' delaymod		-		every time it registers a press while the key is held down, the time until the next press is registered is multiplied by this amount.

Function createkey:key(n%,m%=0,d%=-1,delaymod!=1)
	Local k:key=New key
	k.addkey n,m
	k.keydelay=d
	k.delaymod=delaymod
	Return k
End Function

' Key input type
Type Key
	Field keys:TList=CreateList()
	Field keydelay%=210,currentdelay%=-1,mindelay%=8
	Field delaymod!=1
	
	Const none%=%000
	Const any%=%1000
	Const ctrl%=%100,shift%=%010,alt%=%001
	Const ctrlshift%=%110,shiftctrl%=%110,ctrlalt%=%101,altctrl%=%101,shiftalt%=%011,altshift%=%011
	Const ctrlaltshift%=%111,ctrlshiftalt%=%111,altshiftctrl%=%111,altctrlshift%=%111,shiftctrlalt%=%111,shiftaltctrl%=%111
	
	Method setmod(kind%)
		For Local k:control_key=EachIn keys
			k.modkind=kind
		Next
	End Method
	Function Create:Key(n0%=-1,mod0%=0,n1%=-1,mod1%=0,n2%=-1,mod2%=0,n3%=-1,mod3%=0)
		Local k:Key=New Key
		If n0>=0 k.AddKey n0,mod0
		If n1>=0 k.AddKey n1,mod1
		If n2>=0 k.AddKey n2,mod2
		If n3>=0 k.AddKey n3,mod3
		Return k
	End Function
	Method AddKey:control_key(v@,modkind%=0)
		Local n:control_key=control_key.Create(v,modkind)
		keys.addlast n
		Return n
	End Method
	Method RemoveKey%(n@,modkind%=-1)
		Local ret%=0
		For Local c:control_key=EachIn keys
			If c.num=n And (c.modkind=modkind Or modkind=-1) Then keys.remove c;ret=1
		Next
		Return ret
	End Method
	Method get%()
		Local resetdelay%=1
		Local isget%=0
		For Local k:control_key=EachIn keys
			If (Not isget) And k.get(currentdelay)
				isget=1
			EndIf
			If resetdelay And k.down() Then
				resetdelay=0
			EndIf
			If isget And (Not resetdelay) Then Exit
		Next
		If resetdelay
			currentdelay=keydelay
		ElseIf keydelay<>-1
			currentdelay=Max(mindelay,currentdelay*delaymod)
		EndIf
		Return isget
	End Method
	Method down%()
		For Local k:control_key=EachIn keys
			If k.down() Return 1
		Next
		Return 0
	End Method
	Method downtime%()
		Local ret%=0,now%=MilliSecs()
		For Local k:control_key=EachIn keys
			If k.down() Then ret=Max(ret,now-k.lastms)
		Next
		Return ret
	End Method
	Method flush()
		For Local k:control_key=EachIn keys
			k.flush
		Next
	End Method
End Type

' A key object can actually have several literal keys attached to it, and will register a press if any of the associated keys are pressed. This is the object that actually keeps track of presses.
Type control_key
	Field num@,lastms%,init%=1
	Field modkind%=0
	
	Method flush()
		lastms=MilliSecs()
	End Method
	Function Create:control_key(n@,modkind%=0)
		Local c:control_key=New control_key
		c.num=n;c.modkind=modkind
		Return c
	End Function
	Method get%(keydelay%)
		If KeyDown(num) And ((modkind=modkeys) Or (modkind=key.any)) 'ismod
			If MilliSecs()-lastms=>keydelay And keydelay>=0 lastms=MilliSecs();Return 1
			If init init=0;Return 1
		Else
			init=1;lastms=MilliSecs()
		EndIf
		Return 0
	End Method
	Method down%()
		Return KeyDown(num)
	End Method
End Type

' This stuff is for keeping track of modifier keys.
Global modkeys%=0
Function _keys_ctrl%()
	Return KeyDown(key_lcontrol) | KeyDown(key_rcontrol)
End Function
Function _keys_shift%()
	Return KeyDown(key_lshift) | KeyDown(key_rshift)
End Function
Function _keys_alt%()
	Return KeyDown(key_lalt) | KeyDown(key_ralt)
End Function

' You should run this function once every frame if modifier keys matter.
Function updatemodkeys()
	modkeys=(_keys_ctrl() Shl 2)|(_keys_shift() Shl 1)|(_keys_alt())
End Function
