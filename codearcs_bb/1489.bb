; ID: 1489
; Author: Jeppe Nielsen
; Date: 2005-10-15 22:35:39
; Title: Smooth bezier curves between points
; Description: Draw smooth bezier curves between points

;Smooth bezier curves between points
;by Jeppe Nielsen 2005

Graphics 800,600,16,2
SetBuffer BackBuffer()


For a#=0 To 359 Step 30
r=120-r
PointNew 400+Sin(a)*(r+60),300+Cos(a)*(r+60)
Next
;PointNew 200,150
;PointNew 300,200
;PointNew 400,250
;PointNew 500,100

selectedpoint.point=Null

show1=True
show2=False
show3=True

smooth#=0.1

Repeat

Cls

If KeyDown(78)

	smooth#=smooth#-0.01

ElseIf KeyDown(74)

	smooth#=smooth#+0.01

EndIf

If KeyHit(2)
	show1=1-show1
EndIf
If KeyHit(3)
	show2=1-show2
EndIf
If KeyHit(4)
	show3=1-show3
EndIf
If KeyHit(5)
	showcontrol=1-showcontrol
EndIf

pointhit.point=PointHit(MouseX(),MouseY())
If pointhit<>Null

	If Sin(MilliSecs()*4)>0
		Oval pointhit\x-8,pointhit\y-8,16,16,0
	EndIf
		
EndIf

		
Select state

	Case 0
	

		
				
			If MouseDown(1)
			
				If pointhit<>Null
				
					selectedpoint=pointhit
					If selectedpoint<>Null
						state=1
					EndIf
					
				Else
				
					selectedpoint=PointNew(MouseX(),MouseY())
				
				EndIf
				
			EndIf
		
		
	Case 1
		If MouseDown(1)
			
			If selectedpoint<>Null
			
	
				PointMove selectedpoint,MouseX(),MouseY()
							
			EndIf
			
		Else
		
			state=0
		
		EndIf	

End Select

If selectedpoint<>Null
	
	If show1
		Oval selectedpoint\x-6,selectedpoint\y-6,12,12,True
	EndIf

	If KeyHit(211)
		
		PointDelete selectedpoint
	
	EndIf

EndIf


If show1
PointDraw point_plot
EndIf
If show2
PointDraw point_line
EndIf
If show3
PointDraw point_bezier,smooth#,showcontrol
EndIf

Text GraphicsWidth()/2,1,"Smooth bezier curves between points",1
Text GraphicsWidth()/2,1+13*1,"Keys 1, 2, 3 toggle different curves",1
Text GraphicsWidth()/2,1+13*2,"Key 4 toggle control points",1
Text GraphicsWidth()/2,1+13*3,"+/- adjust curve smoothness : "+smooth,1
Text GraphicsWidth()/2,1+13*4,"Click on point to move it",1
Text GraphicsWidth()/2,1+13*5,"Click anywhere to add point",1
Text GraphicsWidth()/2,1+13*6,"Delete to delete selected point",1





Flip

Until KeyDown(1)

End





Type point

	Field x#,y# ;coords of point
	
	Field cx1#,cy1#;control point 1 for bezier interpolation
	Field cx2#,cy2#;control point 2 for bezier interpolation
	
End Type

Function PointNew.point(x#,y#)

	p.point=New point
	p\x=x
	p\y=y
	
	PointUpdate
	
	Return p
End Function

Function PointDelete(p.point)

	Delete p
	
	PointUpdate
	
End Function

Function PointUpdate()

For p.point=Each point
		
		;store next point
		pp.point=After p
		If pp=Null
			pp=First point
		EndIf
				
		;vector from current point to next point
		dx#=(pp\x-p\x)
		dy#=(pp\y-p\y)
		
		;length of vector
		l#=Sqr(dx*dx+dy*dy)
		
		;normal of vector
		nx1#=dx/l
		ny1#=dy/l
		
		;store point before current point
		ppp.point=Before p
		If ppp=Null
			ppp=Last point
		EndIf
		
		;vector
		dx#=(p\x-ppp\x)
		dy#=(p\y-ppp\y)

		;length of vector
		ll#=Sqr(dx*dx+dy*dy)
		
		nx2#=dx/ll
		ny2#=dy/ll
		
		nx#=(nx1+nx2)/2.0
		ny#=(ny1+ny2)/2.0	
		
		ll#=Sqr(nx*nx+ny*ny)
		nx=nx/ll
		ny=ny/ll
			
		
		;place first control point
		p\cx1#=p\x+nx*l*0.33333
		p\cy1#=p\y+ny*l*0.33333		
		
		
		ppp.point=After pp
		If ppp=Null
			ppp=First point
		EndIf
		
		;vector
		dx#=(pp\x-ppp\x)
		dy#=(pp\y-ppp\y)

		;length of vector
		ll#=Sqr(dx*dx+dy*dy)
		
		nx2#=dx/ll
		ny2#=dy/ll
		
		nx#=(-nx1+nx2)/2.0
		ny#=(-ny1+ny2)/2.0
		
		ll#=Sqr(nx*nx+ny*ny)
		nx=nx/ll
		ny=ny/ll		
		
		;place first control point
		p\cx2#=pp\x+nx*l*0.33333
		p\cy2#=pp\y+ny*l*0.33333	
	
		
		
		
		
Next

End Function

Const point_plot=0
Const point_line=1
Const point_bezier=2


Function PointDraw(mode=point_bezier,st#=0.05,showcontrol=False)

Select mode

	Case point_plot;only points
		For p.point=Each point
			Plot p\x,p\y
			
			Oval p\x-4,p\y-4,8,8
		Next
		
	Case point_line;only lines
		For p.point=Each point
			
			pp.point=After p
			If pp=Null
				pp=First point
			EndIf
			
			Line p\x,p\y,pp\x,pp\y
		Next
		
	Case point_bezier;only bezier curves
		
		For p.point=Each point

			pp.point=After p
			If pp=Null
				pp=First point
			EndIf
			
			t#=0
			While t#<1.0
			
				x1#=p\x*(1-t)^3+3*p\cx1*(1-t)^2*t+3*p\cx2*(1-t)*t*t+pp\x*t^3
				y1#=p\y*(1-t)^3+3*p\cy1*(1-t)^2*t+3*p\cy2*(1-t)*t*t+pp\y*t^3
				
				tt#=t#+st#
				If tt>1
					tt=1
				EndIf
								
				x2#=p\x*(1-tt)^3+3*p\cx1*(1-tt)^2*tt+3*p\cx2*(1-tt)*tt*tt+pp\x*tt^3
				y2#=p\y*(1-tt)^3+3*p\cy1*(1-tt)^2*tt+3*p\cy2*(1-tt)*tt*tt+pp\y*tt^3			
						
				Line x1,y1,x2,y2
			
				t#=t#+st#
			Wend
		
			If showcontrol=True
			
				Oval p\cx1-2,p\cy1-2,4,4
				Oval p\cx2-2,p\cy2-2,4,4
				
			EndIf
			
			
		Next		
	


End Select



End Function

Function PointHit.point(x#,y#,size#=16)
sqsize=size*size

For p.point=Each point
	
	dx#=x-p\x
	dy#=y-p\y
	
	l#=dx*dx+dy*dy
	
	If l<sqsize
		Return p
	EndIf

Next

End Function

Function PointMove(p.point,x#,y#)

p\x=x
p\y=y

PointUpdate

End Function
