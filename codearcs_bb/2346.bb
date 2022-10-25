; ID: 2346
; Author: TAS
; Date: 2008-10-28 02:26:46
; Title: Fractual Line
; Description: Draws a rough line between xy points

;Draws a bumpy line between two xy points
;BlitzPlus! 1.44
;Thomas A. Stevenson (TAS)
;10-27-2008

Graphics GadgetWidth(Desktop()),GadgetHeight(Desktop())

seed=MilliSecs()
SeedRnd(seed)  
Color 255,0,0
BrokenLine(100,100,400,300,9,100,0.70)
Text 440,300,"0.70 Not enough damping"

SeedRnd(seed)  
Color 0,255,0
BrokenLine(100,200,400,400,9,100,0.60)
Text 440,400,"0.60"

SeedRnd(seed)  
Color 0,0,255
BrokenLine(100,300,400,500,9,100,0.50)
Text 440,500,"0.50"

SeedRnd(seed)  
Color 255,0,255
BrokenLine(100,400,400,600,9,100,0.40)
Text 440,600,"0.40"

Text GadgetWidth(Desktop())/2,GadgetHeight(Desktop())-100,"Press Any key",1,1
Flip
While GetKey()=0: Wend


Function BrokenLine(x0%,y0%,x2%,y2%,d%,r%,damp#)
	;d parameter determines how many sub-segments are create, i.e. 2^d segments
	;r is a roughness parameter in pixels
	;damp# decrease r for sublines
	
	If d>0 Then 
		;calculate new mid point x1,y1 with random offset 
		;and break line into two lines
		;x1,y1 is perpendicular to orginal line and at 
		;random distance of r1 where: -r>=r1=<r from line 
		;Note if the dy/dx is the slope of a line a
		;perpendicular line has a  slope of -dx/d 
		d=d-1
		dx=x2-x0
		dy=y2-y0
		r0=Sqr(dx*dx+dy*dy)
		r1=Sqr(Rand(r,r*r))
		;r1/r0 is a scale factor so that x2,y2 is r distance from the line
		If r0>1 Then dx1=dx*r1/r0 Else dx1=1
		If r0>1 Then dy1=dy*r1/r0 Else dy1=1	
		;dir is used to make x2,y2 randomly above or below line	
		dir=1-2*(Rand(10)>5)
		;new mid point halfway on line + random perpendicular offset r
		x1=(x0+x2)/2+dy1*dir
		y1=(y0+y2)/2-(dx1*dir)
		;require to get a bumpy line rather than a mess 
		;values between 0.30 and 0.65 probably work best
		r=r*damp#  
		BrokenLine(x0%,y0%,x1%,y1%,d%,r,damp#)
		BrokenLine(x1%,y1%,x2%,y2%,d%,r,damp#)
	Else
		Line x0%,y0%,x2%,y2%
	EndIf
End Function
