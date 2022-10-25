; ID: 843
; Author: Jeppe Nielsen
; Date: 2003-11-29 09:20:00
; Title: Continuous Bezier Spline
; Description: Create a continuous bezier spline using multiple splines

;**************************************************
;* Continuous Bezier Spline by Jeppe Nielsen 2003 *
;**************************************************
;*       Email: nielsen_jeppe@hotmail.com         *
;**************************************************
;*       Based on Wedoe´s bezier function         *
;**************************************************

Global beziercontrolpoint

Graphics 800,600,16,2
SetBuffer BackBuffer()


beziernew(300,400,325,400,400,400,375,400)


bezier.bezier=Null
event=0
smooth#=0.01


Repeat
	Cls
	
	Text 400,0,"Continuous Bezier Spline by Jeppe Nielsen",1
	Text 400,20,"Left mouse button to drag control points",1
	Text 400,40,"Right mouse button to create a new bezier piece",1
	Text 400,60,"+/- to control curve smoothness : " +smooth#,1,0
	
	If MouseDown(1)
	
		Select event
			
			Case 0
				
				bezier=bezierselect.bezier(MouseX(),MouseY())
				If bezier<>Null
				event=1
				EndIf
							
			Case 1
			
				beziermovepoint(bezier,beziercontrolpoint,MouseX(),MouseY())
			
		End Select
	
	Else
	
		event=0
	
	EndIf
	
	;new bezier
	If MouseHit(2)
	
		lb.bezier=Last bezier
		
		;last bezier´s end point
		x=lb\x[1]
		y=lb\z[1]
		
		beziernew(x,y,x+25,y,x+100,y,x+75,y)
	
	EndIf
	
	If KeyDown(74)
		smooth#=smooth#+0.01
		If smooth#>1
			smooth#=1
		EndIf
	EndIf
	
	If KeyDown(78)
		smooth#=smooth#-0.01
		If smooth#<0.001
			smooth#=0.001
		EndIf
	EndIf

	
	
	bezierdraw(smooth#)
	
	Flip
	
Until KeyDown(1)
End








;x,y,z[0..1] = End points
;x,y,z[2..3] = Control points

Type bezier

Field x#[3],z#[3]

Field b.bezier ;before
Field a.bezier ;after

Field length#

End Type


Function beziernew.bezier(x1#,z1#,vx1#,vz1#,x2#,z2#,vx2#,vz2#)

b.bezier=New bezier

b\x[0]=x1
b\z[0]=z1

b\x[1]=x2
b\z[1]=z2

b\x[2]=vx1
b\z[2]=vz1

b\x[3]=vx2
b\z[3]=vz2

bezierconnect()

Return b

End Function

Function bezierdelete(b.bezier)

	Delete b
	
End Function

Function bezierdeleteall()

	For b.bezier=Each bezier
		bezierdelete(b)
	Next
	
End Function

Function beziermovepoint(b.bezier,point,x#,z#)

b\x#[point]=x#
b\z#[point]=z#

Select point

Case 0

If b\b<>Null
dx#=(b\x[0]-b\x[2])
dz#=(b\z[0]-b\z[2])

b\b\x#[3]=b\x[0]+dx#
b\b\z#[3]=b\z[0]+dz#

EndIf


End Select

If b\b<>Null
Select point
	Case 0
		b\b\x#[1]=x#
		b\b\z#[1]=z#
		
		dx#=(b\x[0]-b\x[2])
		dz#=(b\z[0]-b\z[2])
	
		b\b\x#[3]=b\x[0]+dx#
		b\b\z#[3]=b\z[0]+dz#

		
				
	Case 2
	
		dx#=(b\x[0]-b\x[2])
		dz#=(b\z[0]-b\z[2])
	
		b\b\x#[3]=b\x[0]+dx#
		b\b\z#[3]=b\z[0]+dz#
	End Select
EndIf
If b\a<>Null
Select point
	Case 1
		b\a\x#[0]=x#
		b\a\z#[0]=z#
		
		dx#=(b\x[1]-b\x[3])
		dz#=(b\z[1]-b\z[3])
	
		b\a\x#[2]=b\x[1]+dx#
		b\a\z#[2]=b\z[1]+dz#

	Case 3
		dx#=(b\x[1]-b\x[3])
		dz#=(b\z[1]-b\z[3])
	
		b\a\x#[2]=b\x[1]+dx#
		b\a\z#[2]=b\z[1]+dz#
	End Select
	
EndIf



End Function


Function bezierconnect()

For b.bezier=Each bezier
	
	b\a=Null
	b\b=Null
		
	For bb.bezier=Each bezier
		If b<>bb
		
			dx#=(bb\x[0]-b\x[1])
			dz#=(bb\z[0]-b\z[1])
			
			dist#=dx*dx+dz*dz
			
			If dist<16
			
				b\a=bb
			
			EndIf
			
			dx#=(bb\x[1]-b\x[0])
			dz#=(bb\z[1]-b\z[0])
			
			dist#=dx*dx+dz*dz
			
			If dist<16
			
				b\b=bb
						
			EndIf
				
		EndIf
	Next
	
Next

End Function

Function bezierselect.bezier(x,y)

For b.bezier=Each bezier

	For n=0 To 3
		Oval b\x[n]-4,b\z[n]-4,8,8
		If x>b\x[n]-4
			If y>b\z[n]-4
				If x<b\x[n]+4
					If y<b\z[n]+4
											
						beziercontrolpoint=n
						
						Return b
						
					EndIf 
				EndIf
			EndIf
		EndIf
		
	Next

Next

End Function

Function bezierdraw(inc#=0.01)

	b.bezier=First bezier

	t#=0
	pointx# = b\x[0]*(1-t)^3 + 3*b\x[2]*(1-t)^2*t + 3*b\x[3]*(1-t)*t^2 + b\x[1]*t^3
	pointz# = b\z[0]*(1-t)^3 + 3*b\z[2]*(1-t)^2*t + 3*b\z[3]*(1-t)*t^2 + b\z[1]*t^3

	lpointx#=pointx#
	lpointz#=pointz#
		

	While b<>Null
	
		t#=0
		
		For n=0 To 3
			Oval b\x[n]-4,b\z[n]-4,8,8
		Next
	
		While t#<=1
		
			pointx# = b\x[0]*(1-t)^3 + 3*b\x[2]*(1-t)^2*t + 3*b\x[3]*(1-t)*t^2 + b\x[1]*t^3
			pointz# = b\z[0]*(1-t)^3 + 3*b\z[2]*(1-t)^2*t + 3*b\z[3]*(1-t)*t^2 + b\z[1]*t^3
			
			Line pointx,pointz,lpointx,lpointz
			
			lpointx#=pointx#
			lpointz#=pointz#
			
			t#=t#+inc#
		
		Wend	
		
		t#=1
	
		pointx# = b\x[0]*(1-t)^3 + 3*b\x[2]*(1-t)^2*t + 3*b\x[3]*(1-t)*t^2 + b\x[1]*t^3
		pointz# = b\z[0]*(1-t)^3 + 3*b\z[2]*(1-t)^2*t + 3*b\z[3]*(1-t)*t^2 + b\z[1]*t^3
		
		Line pointx,pointz,lpointx,lpointz
		
		b=After b
		
	Wend

End Function
