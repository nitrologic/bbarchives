; ID: 1660
; Author: zoom*
; Date: 2006-04-07 14:19:12
; Title: NetGraph
; Description: A small netgraph

;NetGraph by zoom*, use it as you wish.
;Just put my name in the credits.
;before create a netgraph.ngf file with this code inside
;NGXDispPosition == 5
;NGYDispPosition == 360

Graphics3D 640,480,16,1

If Not FileType("netgraph.ngf") Then 
	RuntimeError	"NetGraph.ngf does not seem to exist. Please download again."
	End
EndIf

NGFContent	=	ReadFile("netgraph.ngf")
linecontent$	=	ReadLine(NGFContent)
NGXDispPosition#	=	Right(linecontent,Len(linecontent)-19)
linecontent$	=	ReadLine(NGFContent)
NGYDispPosition#	=	Right(linecontent,Len(linecontent)-19)

Global	x#,y#,a#,b#

Dim buffer(63,2)
SeedRnd	MilliSecs()

font	=	LoadFont(arial)
SetFont font

While Not KeyDown(1)
	NG_PutInBuffer(Rand(0,1000),Rand(0,100))
	NG_UpdateNetGraph(1000,100,NGXDispPosition,NGYDispPosition)
	Flip
Wend
End

Function	NG_DrawGraphBG(a#,b#)

	Color	0,0,64
	Rect	a#+1,b#+1,200,115,1
	Color	255,255,255
	Rect	a#,b#,201,101,0
	Rect	a#,b#,201,116,0	
	Color	128,128,255
	Line	a#+5,b#+5,a#+5,b#+45
	Line	a#+5,b#+45,a#+195,b#+45
	Line	a#+5,b#+55,a#+5,b#+95
	Line	a#+5,b#+95,a#+195,b#+95
	
End Function

Function 	NG_UpdateNetGraph(bandwidthin#,bandwidthout#,x#,y#)
	
	NG_DrawGraphBG(x#,y#)
	For	n=1 To 63 Step 1
	
		Color	0,255,0
		Line	x#+4+3*n,y#+44,x#+4+3*n,y#+44-(buffer(n,1)*40)/bandwidthin#
		Line	x#+5+3*n,y#+44,x#+5+3*n,y#+44-(buffer(n,1)*40)/bandwidthin#
		Color	255,0,0
		Line	x#+4+3*n,y#+94,x#+4+3*n,y#+94-(buffer(n,2)*40)/bandwidthout#
		Line	x#+5+3*n,y#+94,x#+5+3*n,y#+94-(buffer(n,2)*40)/bandwidthout#		
		Color	0,0,110
		Line	x#+4+3*n,y#+44-(buffer(n,1)*40)/bandwidthin#-1,x#+4+3*n,y#+5
		Line	x#+5+3*n,y#+44-(buffer(n,1)*40)/bandwidthin#-1,x#+5+3*n,y#+5
		Line	x#+4+3*n,y#+94-(buffer(n,2)*40)/bandwidthout#-1,x#+4+3*n,y#+55
		Line	x#+5+3*n,y#+94-(buffer(n,2)*40)/bandwidthout#-1,x#+5+3*n,y#+55
		If n<63 Then NG_UpdateBuffer(n)

	Next
	Color	0,128,255
	Text	x#+4,y#+102,"NetGraph>> in::" + Int(buffer(63,1)) + "Ko/s | out::" + Int(buffer(63,2)) + "Ko/s"

	Delay 1000
	
End Function

Function 	NG_PutInBuffer(invalue#,outvalue#)

	buffer(63,1) = invalue#
	buffer(63,2) = outvalue#
	
End Function

Function NG_UpdateBuffer(i)

	buffer(i,1) = buffer(i+1,1)
	buffer(i,2) = buffer(i+1,2)
	
End Function
