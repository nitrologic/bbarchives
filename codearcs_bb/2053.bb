; ID: 2053
; Author: Nebula
; Date: 2007-07-05 17:37:01
; Title: Patrolling
; Description: Draw patroling paths.

;
; Patrolling Units.. (editor)
;

Graphics 800,600,16,2
SetBuffer BackBuffer()

Global myim = CreateImage(GraphicsWidth(),GraphicsHeight())

Type ml
	Field x,y
End Type
Type tape
	Field lokatie

End Type
;
Global tape.tape = New tape
tape\lokatie = 0
;
While KeyDown(1) = False
	;
	Cls

	If Rand(40) = 1 Then tape.tape = New tape	;

	;
	;
	If MouseDown(2) = True Then 
		oldx = MouseX()
		oldy = MouseY()
	End If
	;
	If MouseDown(1) = True Then
		;
		If Abs(dist(MouseX(),MouseY(),oldx,oldy)) > 10 Then MoveMouse oldx,oldy
		newx = MouseX()
		newy = MouseY()
		;
			If Abs(dist(newx,newy,oldx,oldy)) > 6 Then
			;
			this.ml = New ml
			this\x  = newx
			this\y  = newy
			oldx = newx
			oldy = newy
			;
			End If
		;
	EndIf
	;
	For this.ml = Each ml
		;
		Plot this\x,this\y
		;
	Next
	;
	For i=0 To 10
	play
	Next
	;
	Text GraphicsWidth()/2,GraphicsHeight()-40,"Move the mouse slowly while holding down the left mouse button",1,1
	;
	Flip	
Wend
End
;
Function play()
	;
	For t.tape = Each tape
	cnt=0
	For this.ml = Each ml
		;
		;		
		If cnt = t\lokatie
			Oval this\x,this\y,5,5,True						
			If Rand(50) = 1 Then t\lokatie = t\lokatie + 1

			Exit
		End If
		cnt=cnt+1
		;
	Next
	Next
	;
End Function
;
Function neemrouteop()
	;
	
	;
End Function
;

Function dist#(x1#,y1#,x2#,y2#)
	Return Sqr((x1-x2)^2+(y1-y2)^2)
End Function
