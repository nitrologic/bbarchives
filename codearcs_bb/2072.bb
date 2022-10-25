; ID: 2072
; Author: Ryan Burnside
; Date: 2007-07-20 17:06:25
; Title: A nice HSV and RGB color object
; Description: This will let you create a color object by defining HSV or RGB. :)

[code]
' functions to deal with color

Function find_max#(r#,g#,b#)
	' return the biggest value of rgb colors
	Return Max(r,Max(g,b))
End Function
Function find_min#(r#,g#,b#)
	Return Min(r,Min(g,b))
End Function
Type color_data
	Field r#=0,g#=0,b#=0,h#=0,s#=0,v#=0
	' R=0-255
	'G=0-255
	'B=0-255
	'H=0-360 degrees
	'S=0-1
	'V=0-1
	Method set_rgb(R#,G#,B#)
	'set the r g b values directly then adjust the hsv
	Self.r=R
	Self.g=G
	Self.b=B
	
	' *now update hsv values*
	
	' now we need to convert our HSV values to base 1
	R=R/255
	G=G/255
	B=B/255
	' find the largest and smallest of the rgb colors
	Local Max_#=find_max(R,G,B)
	Local Min_#=find_min(R,G,B)
	
	' solve the hue value
	' if max=min (no hue)  hue is irrevelant here so we will just go with 0
	If max_=min_
		Self.h=0
	End If
	
	' if red is the highest with green greater or equal to blue
	If max_= R And G >= B
		Self.h=60*( (G-B)/(max_-Min_))+0
	End If
	' if red is the highest with green less than blue
	If max_= R And G < B
		Self.h=60*( (G-B)/(max_-Min_))+360
	End If
	' if the max is green
	If max_=G
		Self.h=60*( (B-R)/(max_-Min_))+120
	End If
	' if the max is blue
	If max_=B
		Self.h=60*( (R-G)/(max_-Min_))+240
	End If
	
	' now for the saturation value
	If max_ = 0
		Self.s=0
		Else
		Self.s= 1-(min_/max_)
	End If
	' finally the value
	Self.v=max_
	End Method
	
	Method set_hsv(H#,S#,V#)
	'set the h s v values directly then adjust the r g b values
	Self.h=H
	Self.s=S
	Self.v=V
	
	' now start adjusting the RGB values
	
	Local Hi%=(H/60.0) Mod 6.0
	Local f#=(H/60.0)-Hi
	Local p#=V*(1.0-S)
	Local q#=V*(1.0-(f*S))
	Local t#=V*(1.0-(1.0-f)*S)
	
	' now preform the checks
	If Hi = 0
		R=V
		G=t
		B=p
	End If
	
	If Hi = 1
		R=q
		G=V
		B=p
	End If
	
	If Hi = 2
		R=p
		G=V
		B=t
	End If
	
	If Hi = 3
		R=p
		G=q
		B=V
	End If
	
	If Hi = 4
		R=t
		G=p
		B=V
	End If
	
	If Hi = 5
		R=V
		G=p
		B=q
	End If
	
	'convert to base 255
	Self.r=R*255.0
	Self.g=G*255.0
	Self.b=B*255.0
	
	
	EndMethod
End Type


Global m:color_data = New color_data

' create a color object ahd set hue 
m.set_hsv(60,1,1)


' make a simple gradient shift display for fun 
Graphics 640,480
Local addon#=0
While Not KeyDown(key_escape)
	Cls
	For Local y=0 To 29			
		For Local x=0 To 29

			m.set_hsv((y*12),1,.90)
			SetColor(m.r,m.g,m.b)
			DrawRect(x*5,y*5,5,5)
	 	Next
	Next
	Flip
Wend



[/code]

Please tell me what you think. It's much easier to store and pass an entire color rather than 3 extra variables in your parameters.
