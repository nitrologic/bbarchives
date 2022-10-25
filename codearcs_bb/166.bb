; ID: 166
; Author: DougUK
; Date: 2001-12-20 07:26:24
; Title: Simple input function
; Description: Positionable input with custom number of characters

Function string_input$(aString$,n,x,y)
Repeat
 If value=0 Then
	length=Len (aString$)
	If length>n-1 Then 		
		Return aString$		
		Exit
	EndIf
	While Not value
		value=GetKey()
	Wend
	If value=13 Then ; enter key 
	Return aString$
	Exit
	EndIf
	If KeyDown (29) And value>0 Or KeyDown(157) And value>0 Then ; catch control keys
	value=0
	Else
		If value>0 And value<7 Or value>26 And value<32 Or value=9 Then ;catch unwanted keys
			value=0
		Else
		If value=8 Then ;backspace key
			If length>0 Then
				aString$=Left$ (aString$, length-1)
			EndIf
		Cls
		Text x,y, aString$
		value=0
		Else
			aString$=aString$+Chr$ (value)
			Cls
			Text x,y, aString$
			value=0
		EndIf
		EndIf
	EndIf
EndIf
Forever
End Function
