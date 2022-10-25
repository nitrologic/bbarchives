; ID: 209
; Author: Giano
; Date: 2002-01-30 08:43:43
; Title: Drawing 3d
; Description: Some 3d drawing functions for points, lines and segments

;*********************************************************************************
;*** Drawing 3d with Blitz
;*** Remember to use this functions after Renderworld
;*** Sometimes becouse extremes points of lines are out of the camera's
;*** field of view, the lines aren't drawed...Use segments that draws more
;*** then a line from 2 points.
;*** Hold mouse to move  Happyness ;) 
;*********************************************************************************
;*** 2002 By Gianluca SCLANO (BitmaniaK)
;*** sclano@hotmail.com
;*** gianluca.sclano@tiscalinet.it
;*********************************************************************************


;*********************************************************************************
;*** Plot a 3d Pixel
;*********************************************************************************
Function plot3d(x#,y#,z#, camera)
	CameraProject camera,x#,y#,z# 
	If ProjectedZ() Then Plot ProjectedX# ( ) ,ProjectedY# ( )
End Function	

;*********************************************************************************
;*** Draw a fast single 3d line 
;*********************************************************************************
Function Line3d(x0#,y0#,z0#,x1#,y1#,z1#, camera)
	CameraProject (camera,x0,y0,z0)
	px0#=ProjectedX# ( ) 
	py0#=ProjectedY# ( ) 
	pz0#=ProjectedZ# ( ) 

	CameraProject (camera,x1,y1,z1)
	If ProjectedZ#( ) >0 And pz0>0 Then Line px0,py0,ProjectedX# ( ) ,ProjectedY# ( ) 
End Function

;*********************************************************************************
;*** Draw a multi segments 3d line 
;*********************************************************************************
Function segments3d(x0#,y0#,z0#,x1#,y1#,z1#, camera, segments=1)

	stepx#=(x1-x0)/segments
	stepy#=(y1-y0)/segments
	stepz#=(z1-z0)/segments
	x1 = x0+stepx#
	y1 = y0+stepy#
	z1 = z0+stepz#

	For t= 1 To segments
		CameraProject (camera,x0,y0,z0)
		px0#=ProjectedX( ) 
		py0#=ProjectedY( ) 
		pz0#=ProjectedZ( ) 

		CameraProject (camera,x1,y1,z1)
		If ProjectedZ#( )>0 And pz0>0 Then Line px0,py0,ProjectedX#( ) ,ProjectedY#( ) 
		x0 = x1 : y0 = y1 : z0 = z1
		x1 = x0+stepx# : y1 = y0+stepy# : z1 = z0+stepz#		
	Next
End Function

;*********************************************************************************
;*** THE TEST :)
;*********************************************************************************
Type entity3D
	Field x#,y#,z#
	Field dx#,dy#,dz#
	Field counter
End Type

Type point3D
	Field x#,y#,z#
	Field dy#
End Type


Function testDraw3d(points=100)
	Graphics3D 800,600,16
	SetBuffer BackBuffer()
	
	camera=CreateCamera()
	light=CreateLight()
	PositionEntity camera,0,50,-100
	RotateEntity camera,30,0,0   
	cube=CreateCube()
	For t=1 To 15
		m=CopyEntity(cube)
		PositionEntity m,Rnd(-50,50),Rnd(-30,30),Rnd(-50,50)
		EntityColor m,Rand(0,255),Rand(0,255),Rand(0,255)
	Next
;*** Create some types for lines extremes	
	For t=1 To points
		e.entity3D = New entity3D
		e\x=Rnd(-10,10)
		e\y=Rnd(-10,10)
		e\z=Rnd(-10,10)
		e\dx=Rnd(-3,3)
		e\dy=Rnd(-3,3)
		e\dz=Rnd(-3,3)
		e\counter = 50 + Rand(20)
	Next
;*** Create some types for points
	For t=1 To points
		p.point3D = New point3D 
		p\x=Rnd(-50,50)
		p\y=Rnd(-10,10)
		p\z=Rnd(-50,50)
		p\dy = Rnd(.1,1)
	Next
	colorCycle=0

;*** The loop begins
   While Not KeyHit(1)
   
   	mx#=mx#+MouseXSpeed()*.3
   	mz#=mz#-MouseYSpeed()*.3
   	my#=my#+MouseZSpeed()*5
   	MoveMouse 400,300
   	
   	If MouseDown(1)
		PositionEntity camera,EntityX(camera)+mx#,EntityY(camera)+my#,EntityZ(camera)+mz#
	Else
		PositionEntity cube,EntityX(cube)+mx#,EntityY(cube)+my#,EntityZ(cube)+mz#
   	End If
   	
   	;reset mouse vars
   	mx#=0 : mz#=0 : my#=0
   	
   	UpdateWorld
;**** Update lines
   	For e.entity3D  = Each entity3D 
		e\x=e\x+e\dx
		e\y=e\y+e\dy
		e\z=e\z+e\dz
		e\counter = e\counter -1
		If e\counter <0 
			e\dx=Rnd(-3,3)
			e\dy=Rnd(-3,3)
			e\dz=Rnd(-3,3)
			
			e\counter = 50 + Rand(20)		
		End If
	Next
;*** Update Points	
	For p.point3D = Each point3D 
		p\y = p\y+p\dy
		If p\y>50 
			p\dy=-Abs(p\dy)
		Else If p\y<-50
			p\dy=Abs(p\dy)
		End If
	Next
;*** RENDER	
   	RenderWorld
;*** NOW USE THE FUNCTIONS

;*** Drawing functions
	colorCycle=(colorCycle+1) Mod 256
;*** 3d lines
   	For e.entity3D  = Each entity3D 
		If e<>Last entity3D
			ne.entity3D = After e
		Else
			ne.entity3D = First entity3D
		End If
		Color 255-colorCycle,colorCycle,255-colorCycle
		line3d(e\x,e\y,e\z,ne\x,ne\y,ne\z, camera)
	Next
	
;*** 3d Points	
	For p.point3D = Each point3D 
		Color Rand(255),255,255
		plot3d(p\x,p\y,p\z, camera)
	Next
;*** 3d segments	
	Color 255-colorCycle,255-colorCycle,255
	segments3d(0,0,0,EntityX(cube),EntityY(cube),EntityZ(cube),camera,10)
   	Flip
   Wend
;*** end loop
End Function

;*********************************************************************************
testDraw3d(50)
End
;*********************************************************************************
