; ID: 2269
; Author: Warpy
; Date: 2008-06-11 20:23:31
; Title: Draw with 2D pan and zoom
; Description: Some helper functions for panning and zooming around a 2d area

Global gwidth#,gheight#

Global panx#=0,pany#=0,zoom#=1,tzoom#=zoom





Function transformpoly#[](poly#[] , px# , py# , an# , scale# = 1) 

	'px, py: translate

	'an: rotate

	'scale: duhhh thicko

	

	Local opoly#[Len(poly)]

	

	can# = Cos(an)

	san#=Sin(an)

	For n=0 To Len(poly)-1 Step 2

		x#=poly[n]*scale

		y#=poly[n+1]*scale

		opoly[n]=x*Can-y*San+px

		opoly[n+1]=x*San+y*Can+py

	Next

	Return opoly

End Function



Function zoompoly#[](poly#[])

	Local opoly#[Len(poly)]

	For n=0 To Len(poly)-1 Step 2

		opoly[n]=zoomx(poly[n])

		opoly[n+1]=zoomy(poly[n+1])

	Next

	Return opoly

End Function



Function drawrotatedline#(x1# , y1# , x2# , y2# , px# , py# , an# , scale# = 1)

	'px,py: translate

	'an: rotate

	'scale: duhhh thicko

	

	can# = Cos(an)

	san# = Sin(an)

	

	nx1#=(x1*can-y1*san)*scale+px

	ny1#=(x1*san+y1*can)*scale+py

	nx2#=(x2*can-y2*san)*scale+px

	ny2#=(x2*san+y2*can)*scale+py

	DrawLine nx1,ny1,nx2,ny2

End Function



Function drawoutline(poly#[],thickness=1)

	'DrawPoly poly

	SetLineWidth thickness

	l = Len(poly)

	For n=0 To l-3 Step 2

		x1#=poly[n]

		y1#=poly[n+1]

		x2#=poly[(n+2) Mod l]

		y2#=poly[(n+3) Mod l]

		DrawZoomLine x1,y1,x2,y2

	Next

	SetLineWidth 1

End Function





Function ZoomX#(x#)

	Return (x - panx) * zoom + gwidth / 2

End Function

Function ZoomY#(y#)

	Return (y - pany) * zoom + gheight / 2

End Function



Function UnzoomX#(x#)

	Return (x - gwidth / 2) / zoom + panx

End Function

Function UnzoomY#(y#)

	Return (y - gheight / 2) / zoom + pany

End Function



Function DrawZoomPoly(poly#[],outline=False)

	poly=poly[..]

	While i < Len(poly)

		poly[i] = zoomx(poly[i])

		poly[i + 1] = zoomy(poly[i + 1]) 

		i:+ 2

	Wend

	If outline

		ox# = poly[0]

		oy# = poly[1]

		i = 2

		While i < Len(poly)

			DrawLine ox , oy , poly[i] , poly[i + 1]

			ox = poly[i]

			oy=poly[i+1]

			i:+ 2

			DrawLine poly[0],poly[1],ox,oy

		Wend

	Else

		DrawPoly poly

	EndIf

End Function



Function DrawZoomLine(ax# , ay# , bx# , by#)

	ax = zoomx(ax)

	ay = zoomy(ay)

	bx = zoomx(bx)

	by = zoomy(by)

	DrawLine ax,ay,bx,by

End Function



Function DrawZoomRect(x# , y# , width# , height#,zoomdimensions=1,filled=1)

	x = zoomx(x)

	y = zoomy(y)

	If zoomdimensions

		width:* zoom

		height:* zoom

	EndIf

	If filled

		DrawRect x , y , width , height

	Else

		DrawLine x , y , x + width , y

		DrawLine x + width , y , x + width , y + height

		DrawLine x , y , x , y + height

		DrawLine x , y + height , x + width , y + height

	EndIf

End Function



Function DrawZoomCircle(x# , y# , radius#)

	x = zoomx(x) 

	y = zoomy(y)

	radius:* zoom

	DrawOval x - radius , y - radius , 2 * radius , 2 * radius

End Function



Function DrawZoomText(txt$ , x# , y#)

	x = ZoomX(x)

	y = ZoomY(y)

	DrawText txt , x , y

End Function



Function DrawZoomImage(image:TImage , x# , y#,width#,heighto=0)

	If heighto

		w# = width / ImageHeight(image)

	Else

		w# = width / ImageWidth(image)

	EndIf

	SetScale w*zoom , w*zoom

	DrawImage image , zoomx(x) , zoomy(y)

	SetScale 1,1

End Function





'graphics init

Function initgfx()

	Graphics gwidth , gheight

End Function
