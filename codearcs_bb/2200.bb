; ID: 2200
; Author: Nebula
; Date: 2008-01-20 13:57:59
; Title: Lens bal
; Description: Lens bal

Graphics 640,480,16,2
SetBuffer BackBuffer()
Dim coppermap#(12000)
makecoppermap

w = 300
h = 300
While KeyDown(1) = False
	;Cls
	If n1 < 600 Then 
		n1=n1+1
		;
		drawoval 320 , 240 , 600 - n1 , 600 - n1 , n1 * 2,False
		;
	End If
	w = w - 1
	h = h - 1
	Flip
Wend
End

Function bound(in,min,max)
	If in>max Then Return max
	If in<min Then Return min
End Function

Function drawoval(x,y,w,h,n,f = 0)
	;
	If w < 1 And h < 1 Then Return
	;
	Local bmap = CreateImage(w,h)	
	;	
	;
	SetBuffer ImageBuffer(bmap)
	n = coppermap( n )
	;DebugLog n
	Color n,n,n
	;
	Select f
		Case 0
			Oval 0,0,w,h,True
		Case 1
			Oval 0,0,w,h,False
	End Select
	;
	SetBuffer BackBuffer()
	MidHandle bmap
	DrawImage bmap,x,y
	FreeImage bmap
End Function

Function makecoppermap()
	Local a# = 255
	Local n# = a# / 1200
	Local n1# = 0
	For i=0 To 1200
		;
		n1 = n1 + n
		coppermap(i) = n1
		;
	Next
End Function
