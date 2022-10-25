; ID: 1436
; Author: Cruis.In
; Date: 2005-08-05 16:17:44
; Title: Zooming In/Out
; Description: Zoom with the Mouse Wheel

Strict
Graphics 800,600,0

Global newscale:Float


While Not KeyHit(KEY_ESCAPE)
Cls
		
	SetColor(244,122,134)
	SetScale(1.0 + newscale,1.0 + newscale)
	
	'handle cube by the centre
	SetHandle(50,50)
	DrawRect(400,300,100,100)
	
	'call function
	zoomcamera()
	
Flip

Wend

Function ZoomCamera()
	Local MaxZoom:Float  =  3.0
	Local minzoom:Float  = -0.8
	Local speed:Float    = 0.03
	Local set_Zoom:Float
	Local zoom			
	
	'mouse wheel value = to variable zoom
	zoom = MouseZ()
	 
    'so that it doesn't zoom in whole numbers( which wouldn't be smooth)

	set_Zoom = 0.2 * zoom 
	

	'limit max zoom in		
	If set_Zoom > maxZoom
		Set_Zoom = maxZoom
	End If 
	
	'zooms to the set zoom value(determined by how much you scroll)		
	If KeyDown(KEY_Z) = 0 And set_Zoom > newScale
		newscale :+ speed 
	End If
	
	'same as above 	
	If KeyDown(KEY_X) = 0 And set_Zoom < newScale
		newscale :- speed 		
	End If
	
	'limit zoom out		
	If newscale < minZoom
		newscale = minZoom
	End If
	
	SetColor(100,100,100)
	SetScale(1.0,1.0)
	DrawText("Newscale: "+newscale,100,100)
End Function
