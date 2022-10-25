; ID: 1208
; Author: fredborg
; Date: 2004-11-29 07:57:43
; Title: Box Packing
; Description: Packs boxes extremely fast, useful for lightmap packing etc.

Type box
	Field id,x,y,w,h
End Type

Dim qSortBox.box(0)
Function QuickSortBoxes(l=-1,r=-1)

	If l = -1
		count = 0
		For box.box = Each box
			count = count + 1
		Next
		Dim qSortBox(count-1)
		box.box = First box
		For i = 0 To count-1
			qSortBox(i) = box
			box = After box
		Next
		l = 0
		r = count-1
	EndIf

	Local A, B, SwapA#, SwapB#, Middle#
	A = L
	B = R
	
	Middle = qSortBox( (L+R)/2 )\h
	
	While True
		
		While qSortBox( A )\h < Middle
			A = A + 1
			If A > R Then Exit
		Wend
		
		While  Middle < qSortBox( B )\h
			B = B - 1
			If B < 0 Then Exit
		Wend
		
		If A > B Then Exit
		
		box.box = qSortBox( A )
		qSortBox( A ) = qSortBox( B )
		qSortBox( B ) = box
		
		A = A + 1
		B = B - 1
		
		If B < 0 Then Exit
		
	Wend
	
	If L < B Then QuickSortBoxes( L, B )
	If A < R Then QuickSortBoxes( A, R )
	
	If count>0
		Insert qSortBox(0) Before First box
		box.box = First box
		For i = 1 To count-1
			Insert qSortBox(i) After box
			box = qSortBox(i)
		Next
	EndIf
	
End Function

Dim AlignMinY(0)
Function boxAlign()
	;Purpose: align boxes
	;Parameters: None
	;return: None
	
	QuickSortBoxes()
	
	maxx = GraphicsWidth()
	maxy = GraphicsHeight()
	
	Dim AlignMinY(maxx)
	
	For box.box = Each box
		box2.box = After box
		If box2<>Null
			box2\x = box\x+box\w
			If box2\x+box2\w>maxx
				box2\x = 0
			EndIf
		EndIf
	Next

	For box.box = Each box
		; Find the minimum y position for this box
		miny = 0
		For x = box\x To box\x+box\w-1
			If AlignMinY(x)>miny Then miny = AlignMinY(x)
		Next
		box\y = miny
		
		; Set the minimum y to the bottom edge of the box, for it's entire width
		miny = box\y+box\h
		For x = box\x To box\x+box\w-1
			AlignMinY(x) = miny
		Next
	Next
	
End Function

.MAIN
Graphics 800,800,16,2
SetBuffer(BackBuffer())

SeedRnd MilliSecs()

;make some random sized boxes
For loop = 1 To 10000
	box.box = New box
	box\id=loop
	box\w=Rnd(50)+10
	box\h=Rnd(50)+10		
Next

starttime=MilliSecs()
boxAlign()
stoptime=MilliSecs()-starttime

;display the boxes
boxarea# = 0
maxy = 0
maxx = 0
For box.box = Each box
	Color 63,127,255
	Rect box\x,box\y+16,box\w,box\h,False
	boxarea = boxarea + box\w*box\h
	If box\y+box\h>maxy Then maxy = box\y+box\h
	If box\x+box\w>maxx Then maxx = box\x+box\w
Next
totarea# = maxx*maxy

Color 0,0,0
Rect 0,0,GraphicsWidth(),10,True
Color 255,255,255
Text GraphicsWidth()/2,0,"Boxes - "+(loop-1)+" | Time - "+stoptime+"ms | Area usage - "+((boxarea*100)/totarea)+"%",True

Flip()
WaitKey()
End
