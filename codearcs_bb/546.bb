; ID: 546
; Author: elias_t
; Date: 2003-01-12 14:25:24
; Title: Text input box
; Description: Simple text input box function

;text input///////////////////////////////////////////////////////////////				
;A simple text input function with numpad support

;***
Global geted$;the returned string
;***

;example
Graphics 640,480,32,2
SetBuffer BackBuffer()


text_input(200,200,15)


If geted$<>""
Color 255,255,255
Print "You Typed : "+geted$
Delay 3000
EndIf

End



;*************************************************************************
;
;tx,ty=x,y coords
;
;tl=length of input characters
;
;*************************************************************************

Function text_input(tx,ty,tl)

	FlushKeys()
			
	Color 56,78,112:Rect tx,ty,(tl+3)*6,15,0;box
	Color 215,218,227:Rect tx+1,ty+1,((tl+3)*6)-2,13,1;box
	Color 56,78,112:Rect tx+2,ty+3,6,10;cursor
	geted$="":get=0:gg=0:ad=1:Flip
		
	While Not get
						
 	get=GetKey()

;numpad numbers
If KeyHit(82) Then get=48;0
If KeyHit(79) Then get=49;1
If KeyHit(80) Then get=50;2
If KeyHit(81) Then get=51;3
If KeyHit(75) Then get=52;4
If KeyHit(76) Then get=53;5
If KeyHit(77) Then get=54;6
If KeyHit(71) Then get=55;7
If KeyHit(72) Then get=56;8
If KeyHit(73) Then get=57;9
If KeyHit(83) Then get=46;comma

			ge$=Chr$(get)


;Exclude various keys
If KeyHit(211) Then get=8
If KeyHit(53) Then get=0
If KeyHit(43) Then get=0
If (KeyHit(197) Or KeyHit(199) Or KeyHit(200) Or KeyHit(201) ) Then get=0
If (KeyHit(203) Or KeyHit(205) Or KeyHit(207) Or KeyHit(208) ) Then get=0
If (KeyHit(209) Or KeyHit(210) Or KeyHit(181) Or KeyHit(15) ) Then get=0


			If get

			If get = 27 Then Goto cancel;esc
			
			If gg>tl And get <> 8 Then Goto skip_type
						
			If get = 8
			ad=-1
			Else ad=1
			EndIf
			
			gg=gg+ad
			
			If gg<0
			gg=0
			Goto skip_type
			EndIf
			
			If get = 13 And geted$<>"" Then Exit
			If get = 13 And geted$="" Then Return
									
			geted$=Left$(geted$,gg)
			
			If get <>8	
			Color 215,218,227:Rect (tx+3)+(gg-1)*6,ty+3,6,10	
			Color 10,10,82:Rect (tx+3)+gg*6,ty+3,6,10
			
			Else		
			
			Color 10,10,82:Rect (tx+3)+gg*6,ty+3,6,10	
			Color 215,218,227:Rect (tx+3)+(gg+1)*6,ty+3,6,10
			
			EndIf
						
			If get<>8
			Text (tx+4)+(gg-1)*6,ty+1,ge$
			geted$=geted$+ge$
			EndIf
			
			
		EndIf;if get	
			
.skip_type
If MouseDown(2)
FlushMouse():FlushKeys():Delay 150
geted$=""
Return geted$
EndIf

If MouseDown(1) And geted$<>""
FlushMouse():FlushKeys():Delay 150
Return geted$
EndIf
			
			Flip
			get=0
			Wend
			
.cancel
FlushMouse():FlushKeys():Delay 150		
If get=27 Then geted$=""
Return geted$
		
End Function			
;*************************************************************************
