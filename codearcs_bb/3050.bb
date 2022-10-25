; ID: 3050
; Author: -Rick-
; Date: 2013-04-15 17:19:16
; Title: Quick Color Function
; Description: Quickly Set Color

Graphics 800,600,16,2
 
 Global key$ = "0"
 
 While Not KeyHit(1)
 	SetCol("yellow",key)
 	Text GraphicsWidth()/2,GraphicsHeight()/2,"This is a test",1,1
 	If KeyHit(2) Then key = "1"
 	If KeyHit(3) Then key = "2"
 	If KeyHit(4) Then key = "3"
 	If KeyHit(5) Then key = "4"
 	If KeyHit(11) Then key = "0"
 Wend
 End


;XXXXXXXXXXXXXXXXXXXXXX
;	SET COLOR
;XXXXXXXXXXXXXXXXXXXXXX
Function SetCol(Kolor$="white",Effect$ = "0")

	FTime$ = Str(MilliSecs())
	Time$ = Mid(Ftime,Len(ftime)-2,2)
	Fade# = Float(Time) * .01
	Effect$ = Lower(Effect)

	Select Effect
		Case 0,"none"
			fade = 1.0
		Case 1,"blink"
			If fade < .5 Then 
				fade = 1
			Else
				fade = 0
			EndIf
		Case 2,"pulse"
			If Fade < .5 Then 
				Fade# = 1 - Fade
			EndIf
		Case 3,"charge"
			If fade < 1 Then
				fade = fade + fade / 5
			Else
				fade = 0
			EndIf
		Case 4,"fade"
			If fade > 0 Then
				fade = 1 - fade 
			Else
				fade = 1
			EndIf
	End Select

	Kolor = Lower(Kolor)
	Select Kolor
		Case "0","blk","black"							;Black
			Color 0,0,0
		Case "1","red"									;Red
			Color 255*Fade,0,0	
		Case "2","grn","green"							;Green
			Color 0,255*Fade,0
		Case "3","blu","blue"							;Blue
			Color 0,0,255*Fade
		Case "4","yel","yellow"							;Yellow
			Color 255*Fade,255*Fade,0
		Case "5","drd","dred","darkred"					;Dark Red
			Color 150*Fade,0,0
		Case "6","dgr","dgreen","darkgreen"				;Dark Green
			Color 0,150*Fade,0
		Case "7","dbl","dblue","darkblue"				;Dark Blue
			Color 0,0,150*Fade
		Case "8","dyl","dyellow","darkyellow"			;Dark Yellow
			Color 150*Fade,150*Fade,0
		Case "9","pur","purple"							;Purple
			Color 255*Fade,0,255*Fade
		Case "10","whi","white"							;White
			Color 255*Fade,255*Fade,255*Fade
		Case "11","gry","grey","gray"					;Grey
			Color 150*Fade,150*Fade,150*Fade
		Case "12","tel","teal"							;Teal
			Color 0,255*Fade,255*Fade
		Case "13","dgry","dgrey","dark grey","dark gray";Dark Grey
			Color 50*fade,50*Fade,50*Fade	
	End Select
End Function
