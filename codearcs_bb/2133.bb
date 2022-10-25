; ID: 2133
; Author: Nebula
; Date: 2007-11-01 08:23:16
; Title: Binary rhythms (32)
; Description: Binary music/rhythms/speech. (bin = 0 or 1)

;root music
;note lines (chords) not inserted yet.

Graphics 640,48,16,2
SetBuffer BackBuffer()

Global snd = LoadSound("standard.wav")

	cnt = 1

Global abc


Global timer = CreateTimer(5)

a$ = Bin(123)

While KeyDown( 1 ) = False
	Cls
	If KeyDown(200) = True Then q2 = q2 + 1 : aa$ = Bin(q2)
	If KeyDown(205) = True Then q = q + 1 : a$ = Bin(q)
	If KeyDown(203) = True Then q = q - 1 : a$ = Bin(q)
	If KeyDown(200) = True Then q = Rand(19826422221) : a$ = Bin(q)
	;
	Text 0,0,a$
	Text 0,15,"Cursor up random"
	Text 0,25,"Curs left and curs right"
	;
	b$ = Mid(a$,cnt,1)
	DebugLog b$
	;
	If b$="1" Then 
		If ( Int ( Mid( aa$,cnt,1 ) ) * 100 ) = 1 Then ozz = ozz + 123 Else ozz = ozz - 123
		ChannelVolume abc,.4				
		ChannelPitch abc,zz
		abc = PlaySound( snd ) : ClsColor Rand( 200 ),0,0 : Cls
	End If
	WaitTimer(timer)
	StopChannel abc
	cnt = cnt + 1
	If cnt>31 Then cnt= 1
	Flip
Wend
End
