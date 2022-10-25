; ID: 1512
; Author: Haramanai
; Date: 2005-10-30 02:57:24
; Title: Circle Circle Instersection Points
; Description: Finds and give the two points that intersects two circles

Rem 
 	Circle Intersection
	 By William Ngan (http:/www.metaphorical.net)
	 Processing BETA code (http://www.processing.org)
End Rem

Type Circle 

	Field x, y, r, r2

	Function create:Circle( px#, py#, pr# ) 
		Local Circle_:Circle = New Circle
		Circle_.x = px
		Circle_.y = py
		Circle_.r = pr
		Circle_.r2 = pr*pr
		Return Circle_
	End Function

End Type


Function intersect( cA:Circle , cB:Circle ) 

	Local dx# = cA.x - cB.x
	Local  dy# = cA.y - cB.y
	Local  d2# = dx*dx + dy*dy
	Local  d# = Sqr( d2 )

	If  d>cA.r+cB.r Or d<Abs(cA.r-cB.r) Return'; // no solution
	

	Local a# = (cA.r2 - cB.r2 + d2) / (2*d)
	Local h# = Sqr( cA.r2 - a*a )
	Local x2# = cA.x + a*(cB.x - cA.x)/d
	Local y2# = cA.y + a*(cB.y - cA.y)/d


	DrawRect( x2, y2, 5, 5 )

	Local paX% =  x2 + h*(cB.y - cA.y)/d 
	Local paY% =  y2 - h*(cB.x - cA.x)/d 
	Local pbX# = x2 - h*(cB.y - cA.y)/d
	Local pbY# = y2 + h*(cB.x - cA.x)/d
	DrawText pax , 10 , 10
	DrawRect paX , PaY , 5 , 5
	DrawRect pbX , PbY , 5 , 5

End Function

Function DrawCircle(R:Circle)
	DrawOval R.x - R.r  , R.y - R.r , R.r*2 , R.r*2
End Function

Local Cir:Circle = Circle.create(10 , 10 , 100)
Local Cir2:Circle = Circle.create(100 , 100 , 50)

Graphics 640 , 480 , 0

While Not KeyDown(Key_Escape)
	Cls
	SetColor 255 , 0 , 0
	drawCircle(Cir)
	SetColor 0 , 255 , 0
	drawCircle(Cir2)
	SetColor 0 , 0 , 255
	intersect(cir , cir2)
	Cir.X = MouseX()
	Cir.Y = MouseY()
	Flip
	FlushMem
Wend
