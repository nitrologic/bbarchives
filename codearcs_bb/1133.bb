; ID: 1133
; Author: BODYPRINT
; Date: 2004-08-13 11:42:09
; Title: CreateTorus
; Description: Create a torus mesh

;Create Torus Function
;Written by Philip Merwarth
;Friday the 13th August, 2004 (oooooh)
;
;CreateTorus(radius#,width#,segments,sides[,parent])
;
;radius# = torus radius
;width# = radius of tube in the torus
;segments = the number of segments around the torus
;sides = the number of segments, or sides of the tube in the torus
;parent = parent entity handle

Function CreateTorus(torrad#,torwidth#,segments,sides,parent=0)

	torusmesh=CreateMesh(parent)
	surf=CreateSurface(torusmesh)
	
	FATSTEP#=360.0/sides
	DEGSTEP#=360.0/segments

	radius#=0
	x#=0
	y#=0
	z#=0
	
	fat#=0
	Repeat
		radius = torrad + (torwidth)*Sin(fat)
		deg#=0
		z=torwidth*Cos(fat)
		Repeat
			x=radius*Cos(deg)
			y=radius*Sin(deg)
			AddVertex surf,x,y,z,x,y,z			
			deg=deg+DEGSTEP	
		Until deg>=360
		fat=fat+FATSTEP
	Until fat>=360
	
	For vert=0 To segments*sides-1
		v0=vert
		v1=vert+segments
		v2=vert+1
		v3=vert+1+segments
		
		If v1>=(segments*sides) Then v1=v1-(segments*sides)
		If v2>=(segments*sides) Then v2=v2-(segments*sides)
		If v3>=(segments*sides) Then v3=v3-(segments*sides)
		
		AddTriangle surf,v0,v1,v2
		AddTriangle surf,v1,v3,v2	
	Next
	
	UpdateNormals torusmesh

	Return torusmesh
End Function
