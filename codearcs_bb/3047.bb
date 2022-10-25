; ID: 3047
; Author: _PJ_
; Date: 2013-03-30 14:42:11
; Title: Primitives By PolyCount
; Description: Create Primitives By Number Of Triangles

Function CreatePolySphere(MaxPolys, Parent=False)
	MaxPolys=(MaxPolys*(MaxPolys<=39600 And MaxPolys >=8 ))+(39600*(MaxPolys>39600))+(8*(MaxPolys<8))
	Local Iterate
	Local Segments
	For Iterate=3 To 100
		Segments=4*(Iterate*Iterate)
		Segments=Segments * (Iterate - 1)
		If (Segments>MaxPolys)
			Iterate=Iterate-1
			Exit
		End If
	Next
	Return CreateSphere(Iterate,Parent)
End Function

Function CreatePolyCylinder(MaxPolys, Closed=True, Parent=False)
	MaxPolys=(MaxPolys*(MaxPolys<=200 And MaxPolys >=8 ))+(200*(MaxPolys>200))+(8*(MaxPolys<8))
	MaxPolys=Int(Floor(MaxPolys*0.5))
	
	Return  CreateCylinder(MaxPolys,Closed,Parent)
End Function

Function CreatePolyCone(MaxPolys, Closed=True, Parent=False)
	MaxPolys=(MaxPolys*(MaxPolys<=100 And MaxPolys >=8 ))+(100*(MaxPolys>100))+(8*(MaxPolys<8))
	Return  CreateCone(MaxPolys,Closed,Parent)
End Function
