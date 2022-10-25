; ID: 1696
; Author: Booticus
; Date: 2006-05-05 17:44:54
; Title: zoomable clickable scrollable starfield!
; Description: zoomable clickable scrollable starfield!

SuperStrict

Global objList:TList=CreateList()

Graphics 640,480

Global theZoomFactor:Float=1.0
Global oldZoomFactor:Float=0.0

For Local i:Int=1 To 5
	Local o:obj=New obj
	o.x=Rand(-GraphicsWidth(),GraphicsWidth())
	o.y=Rand(-GraphicsHeight(),GraphicsHeight())
Next

Global youX:Float,youY:Float

Global line:Int
While KeyHit(KEY_ESCAPE)=0
Cls	
line=50
	If KeyDown(KEY_NUMADD) Then theZoomFactor:+0.01
	If KeyDown(KEY_NUMSUBTRACT) Then theZoomFactor:-0.01
'	If theZoomFactor<0.1 Then theZoomFactor=0.1
'	If theZoomFactor>4 Then theZoomFactor=4	
	
	
	If KeyDown(KEY_UP) Then youY:-1.0*theZoomFactor
	If KeyDown(KEY_DOWN) Then youY:+1.0*theZoomFactor
	If KeyDown(KEY_LEFT) Then youX:-1.0*theZoomFactor
	If KeyDown(KEY_RIGHT) Then youX:+1.0*theZoomFactor

	If MouseZ() And oldZoomFactor<>MouseZ()
		If oldZoomFactor<MouseZ()
			theZoomFactor:+0.1	
		Else
			theZoomFactor:-0.1			
		EndIf
			If theZoomFactor>4.0
				theZoomFactor=4.0
			End If
			If theZoomFactor<0.2
				theZoomFactor=0.2
			End If
		oldZoomFactor=MouseZ()
	EndIf
	SetColor 255,255,255;DrawText theZoomFactor,0,0


		
	
	updateObjs(MouseDown(1))

	SetColor 0,255,0
	DrawRect (GraphicsWidth()/2)-3,(GraphicsHeight()/2)-3,7*theZoomFactor,7*theZoomFactor
	SetColor 255,255,255;DrawText theZoomFactor,0,0
	DrawText "Press +/- on the numpad to zoom. Cursors to move",0,10
	
Flip
Wend
End


Type obj
	Field x:Int
	Field y:Int
	Field dist:Float
	
	Method New() ListAddLast(objList,Self); End Method
	Method Update()
		Local dx:Int=(youX*theZoomFactor)-GraphicsWidth()/2  'here you have forgotten the scalefactor
		Local dy:Int=(youY*theZoomFactor)-GraphicsHeight()/2 'here you have forgotten the scalefactor
		SetScale 1,1
		SetColor 255,0,0
		If dist<50*thezoomfactor Then SetColor 255,255,0
		DrawRect Int((x*theZoomFactor-3)-dx),Int((y*theZoomFactor-3)-dy),Int(7*theZoomFactor),Int(7*theZoomFactor)
		DrawText dist,10,line
		line:+20
	End Method
End Type	

Function updateObjs(checkmouse:Int)
	Local mx:Float=MouseX()
	Local my:Float=MouseY()
	Local dx:Int=(youX*theZoomFactor)-GraphicsWidth()/2
	Local dy:Int=(youY*theZoomFactor)-GraphicsHeight()/2
	For Local i:obj=EachIn objList
		i.Update()
		If checkmouse Then
			Local xdiff:Float,ydiff:Float
			xdiff=(mx)-(i.x*thezoomfactor-dx)
			ydiff=(my)-(i.y*thezoomfactor-dy)
			i.dist=Sqr((xdiff*xdiff)+(ydiff*ydiff))
	
		Else
			i.dist=1000
		endif
	Next

End Function
