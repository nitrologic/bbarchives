; ID: 1903
; Author: Jesse
; Date: 2007-01-22 11:54:22
; Title: Spherical Text
; Description: Text scroller

SuperStrict
Global twidth%,Theight%
Type Tnode
	Field x#
	Field y#
	Field z#
End Type
 
Graphics 1024,768,32
Global format:tnode[][] = getformat()
Global textbox%[,]=gettextbox()
Local st$ = "Hello world This is a code I had been having problems with and finally I figured it out."
st :+ " I wanted To design this For a game I am working on. But now I don't feel like adding this"
st :+ " code To it. I hope you all enjoy it."
Local n% = TextWidth(st)
Local ang% = 0
Repeat 
	For Local q% = 0 To st.length-1
		put((st[q]),ang-8*q,GraphicsWidth()/2,GraphicsHeight()/2)
	Next
	Flip()
	Cls
	ang = (ang+1) Mod (180+n)
Until KeyDown(key_escape)



Function put(c%,ang%,offsetx%,offsety#)
	c = (c-32)*8
	ang = 180 -ang
	If ang >188 Return
	For Local x% =  0 To 7
		For Local y% = 0 To 15
			If (x+ang) < 180 And (x+ang)>0
				If textbox[x+c,y] = 0 
					SetColor 0,255,0
					DrawRect format[x+ang][15-y].x+offsetx,format[x+ang][15-y].y+offsety,1,8
				Else
					SetColor 255,0,0				
					DrawOval format[x+ang][15-y].x+offsetx,format[x+ang][15-y].y+offsety,1,8
				EndIf
			EndIf
		Next
	Next
End Function

Function getformat:tnode[][]()	
	Local nodeList:TList=CreateList()
	Local d# = 600
	Local mz# = -100
	Local AngleAxisz# = 0 'spin along y axis; angle of z
	Local AngleAxisx# = 20 'spin along z axiz; angle of x
	Local AngleAxisy# = 0 'spin along x axis; angle of y
	Local gtext:tnode[][]
	gtext = gtext[..180]
	For Local c% = 0 To 179
		gtext[c]=gtext[c][..16]
	Next
	Local sinAngleAxisx# = Sin(AngleAxisx)
	Local cosAngleAxisx# = Cos(AngleAxisx)
	Local sinAngleAxisy# = Sin(AngleAxisy)
	Local cosAngleAxisy# = Cos(AngleAxisy)
	Local node:tnode
	For Local i# = -8 To 7  
		node= New tnode
		nodelist.addlast(node)
		node.x = (Cos(i*2)*25)
		node.y = (Sin(i*2)*25)
		node.z = 0
	Next

	For angleaxisz = -179 To 0 Step 1
		Local sinAngleAxisz# = Sin(AngleAxisz)
		Local cosAngleAxisz# = Cos(AngleAxisz)
		Local n% = 0
		For node  = EachIn nodeList
			Local X# = -node.x; 
			Local Xa# = cosAngleAxisz * X - sinAngleAxisz * node.z; 
			Local Za# = sinAngleAxisz * X + cosAngleAxisz * node.z
			Local Ya# = cosAngleAxisx * node.y - sinAngleAxisx * Xa;
			X = cosAngleAxisx * Xa + sinAngleAxisx * node.y; 
			Local Z# = cosAngleAxisy * Za - sinAngleAxisy * Ya
			Local Y# = sinAngleAxisy * Za + cosAngleAxisy * Ya;
			Z = Z + mZ 
			Local sx# = D * X / Z
			Local sy# = D * Y / Z
			gtext[179+angleaxisz][n] = New tnode
			gtext[179+angleaxisz][n].x = sx
			gtext[179+angleaxisz][n].y = sy
			n = n + 1
		Next
	Next
	Return gtext
End Function
Function gettextbox%[,]()
		Local text$
		For Local c% = 32 To 127
			text :+ Chr(c)
		Next
		twidth% = TextWidth(text$)
		theight% = TextHeight(text$)
		Local array%[twidth,Theight]
		Local image:timage=CreateImage(Twidth,theight,1,DYNAMICIMAGE)
		Cls
		DrawText text,0,0
		GrabImage image,0,0
		Local map:TPixmap = LockImage(image)
		Cls
		Local c%=0
		For Local y# = 0 To PixmapHeight(map)-1
			For Local x#=0 To PixmapWidth(map)-1
				Local color% = $fff & ReadPixel(map,x,y)
				If  color array[x,y]=1 Else array[x,y] = 0
			Next
		Next
		Return array
End Function
