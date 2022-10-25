; ID: 2118
; Author: Nebula
; Date: 2007-10-12 13:16:02
; Title: Line point statistics editor
; Description: Statistics screen with drag feature

;
; 
; Statistics ( Crom Design ) Freeware : : :
; 
;
Graphics 640,480,16,2
SetBuffer BackBuffer()
Global numnode = 0

Type node
	Field x#,y#,w#,h#,active
End Type

Dim tnx(2)
Dim tny(2)
Global tnactive
Global tnum

; the amount of lines and point (To / x is the total amount)
For x=-20 To 200 Step 5
	newnode(x,Rand(200),5,5)
	numnode = numnode + 1
Next

While KeyDown(1) = False
	Cls
	Text 0,0,"100%"
	Line 10,0,10,200
	Line 10,200,200,200
	Text 0,200,"0%"
	Text 0,220,"Month"
	Text 200,220,"End of Month"
	Text 10,250,"Press mouse on a white block to move it to another place"
	drawalllines
	selectnode MouseX(),MouseY()
	If tnactive = True Then
		cnt=0
		If MouseDown(1) = True Then
				tnx(1) = MouseX()
				tny(1) = MouseY()
				Rect tnx(1),tny(1),5,5,True
				Line tnx(0),tny(0),tnx(1),tny(1)
				Line tnx(1),tny(1),tnx(2),tny(2)
			Else
			tnactive = False
			For this.node = Each node
				cnt = cnt + 1
				If tnum = cnt
					this\x = MouseX()
					this\y = MouseY()
					Exit
				End If
			Next
		End If
	End If
	Flip
Wend

Function newnode(x#,y#,w#,h#)
	;
	this.node = New node
	this\x = x
	this\y = y
	this\w = w
	this\h = h	
	this\active = False
	;
End Function

Function drawalllines()
	;
	For aa.node = Each node
		cnt=cnt+1
		If cnt>2 Then Exit
	Next
	If cnt<1 Then Return
	;
	that.node = First node
	that = After that
	this.node = First node
	While cnt<numnode
		;
		cnt = cnt + 1
		drawline(this,that)
		that = After that
		this = After this
		;
	Wend
	;
End Function

Function drawline(this.node,that.node)
	Color 200,200,200
	Line this\x,this\y,that\x,that\y
	;
	If this\active = True Then Color 255,0,0 Else Color 255,255,255
	Rect this\x,this\y,this\w,this\h,True
	Rect that\x,that\y,that\w,that\h,True
End Function

Function selectnode(x#,y#)
	If MouseDown(1) = False Then Return
	For this.node = Each node
	cnt=cnt+1
	If RectsOverlap(x,y,2,2,this\x,this\y,this\w,this\h) = True Then
;		this\active = True
		tnx(1) = this\x
		tny(1) = this\y
		tnum = cnt
		this = Before this
		tnx(0) = this\x
		tny(0) = this\y
		this = After this
		this = After this
		tnx(2) = this\x
		tny(2) = this\y
		this = Before this
		tnactive = True		
		Else
;		this\active = False
	End If
	Next
End Function
