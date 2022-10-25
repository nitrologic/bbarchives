; ID: 821
; Author: Stevie G
; Date: 2003-11-04 18:20:04
; Title: Retro style patchwork landscape generator - ala Zarch
; Description: ditto

Graphics3D 320,240,16,1 

Const size#=256 
Const divs#=7 
Const divg#=divs*4+1 
Const scale#=16 
Const speed#=.25 

Global divd#=Sqr( 2*(divs*divs) )
Global camera=CreateCamera() 
Global light=CreateLight() 
Global target=CreatePivot() 
Global map,map_tex 
Global grid=create_grid() 
Global surf=GetSurface(grid,1) 
Global x_pos#= size *.5 
Global z_pos#= size *.5 
Global last_x# = x_pos 
Global last_z# = z_pos 
Global frames 

Type point 
	Field height# 
	Field index 
	Field r,g,b 
End Type 

Dim terrain.point(size-1,size-1) 

random_terrain(0) ;10031972 

While Not KeyDown(1) 

	x_pos=qwrap( x_pos + (KeyDown(205)-KeyDown(203)) * speed,size) 
	z_pos=qwrap( z_pos + (KeyDown(208)-KeyDown(200)) * speed,size) 
	
	update_terrain() 
	update_camera() 
	
	frames=qwrap(frames+1,10) 
	If frames = 0 update_map() 


UpdateWorld() 
RenderWorld() 

Flip 
Wend 

;===================================================================================================== 
;UPDATE TERRAIN ===================================================================================================== 
;===================================================================================================== 

Function update_terrain() 

ax#=Floor(x_pos):az#=Floor(z_pos) 
fx#=(x_pos) - ax:fz#=(z_pos) - az 

For z=-divs To divs+1 
	For x=-divs To divs+1 

		nx#=qwrap(ax+x,size) 
		nz#=qwrap(az+z,size) 
		
		vy#=terrain(nx,nz)\height 
		vx#=qlimit( x-fx,-divs,divs) 
		vz#=qlimit( z-fz,-divs,divs) 
		
		;brightness 
		c#=.25 + ( divd - Sqr(vx*vx+vz*vz) ) / (divd )  
		r=terrain(nx,nz)\r * c 
		g=terrain(nx,nz)\g * c 
		b=terrain(nx,nz)\b * c 
		
		If Abs(vx)=divs Or Abs(vz)=divs 
		vy#=get_height( x_pos+vx , z_pos+vz) 
		EndIf 

		;vertex positions 
		For iz = 0 To ( z > -divs And z < divs+1 ) 
			For ix = 0 To ( x > -divs And x < divs+1 ) 
				x1 = ( x + divs ) * 2 - ( x > -divs ) + ix 
				z1 = ( z + divs ) * 2 - ( z > -divs ) + iz 
				v=x1+z1*(divg+1) 
				VertexCoords surf,v, vx * scale , vy , -vz * scale 
			Next 
		Next 

		;colours 
		For iz = 0 To (z < divs+1) 
			For ix = 0 To (x < divs+1) 
				x1 = ( x + divs ) * 2 + ix 
				z1 = ( z + divs ) * 2 + iz 
				v=x1+z1*(divg+1) 
				VertexColor surf,v,r,g,b 
			Next 
		Next 

	Next 
Next 

End Function 

;===================================================================================================== 
;GET HEIGHT OF POINT ON WORLD CO-ORDS ===================================================================================================== 
;===================================================================================================== 

Function get_height(bx#,bz#) 

bx=qwrap(bx,size):bz=qwrap(bz,size) 
tx#=Floor(bx):tz#=Floor(bz) 
jx#=bx - tx:jz#=bz - tz 
cx#=qwrap(tx+1.0,size) 
cz#=qwrap(tz+1.0,size) 
v1#=terrain(tx,tz)\height+(terrain(cx,tz)\height-terrain(tx,tz)\height)*jx 
v2#=terrain(tx,cz)\height+(terrain(cx,cz)\height-terrain(tx,cz)\height)*jx 

Return v1+(v2-v1)*jz 

End Function 

;===================================================================================================== 
;UPDATE CAMERA ===================================================================================================== 
;===================================================================================================== 

Function update_camera() 

PositionEntity target,0,divs*10+get_height(x_pos,z_pos)* 1.0,-divs*25 
dx#=(EntityX(target,1)-EntityX(camera,1))*.1 
dy#=(EntityY(target,1)-EntityY(camera,1))*.1 
dz#=(EntityZ(target,1)-EntityZ(camera,1))*.1 
TranslateEntity camera,dx,dy,dz 

End Function 

;===================================================================================================== 
;UPDATE MAP ===================================================================================================== 
;===================================================================================================== 

Function update_map() 

SetBuffer TextureBuffer(map_tex) 

For z=-2 To 2 
	For x=-2 To 2 
		xp=qwrap(last_x+x,size) 
		zp=qwrap(last_z+z,size) 
		Color terrain(xp,zp)\r,terrain(xp,zp)\g,terrain(xp,zp)\b 
		Plot xp,zp 
	Next 
Next 

For z=-2 To 2 
	For x=-2 To 2 
		xp=qwrap(Floor(x_pos)+x,size) 
		zp=qwrap(Floor(z_pos)+z,size) 
		Color 255,255,255 
		Plot xp,zp 
	Next 
Next 

SetBuffer BackBuffer() 

last_x=Floor(x_pos) 
last_z=Floor(z_pos) 

End Function 


;===================================================================================================== 
;GENERATE RANDOM TERRAIN FOR TESTING ===================================================================================================== 
;===================================================================================================== 

Function random_terrain(seed) 

SeedRnd seed 

;initialise 
For z=0 To size-1 
	For x=0 To size-1 
		terrain(x,z) = New point 
	Next 
Next 

passes=50+Rand(150) 

;do heights 
For parts=0 To passes 
	start_x=Rand(0,size) 
	start_z=Rand(0,size) 
	rad#=Rand(1,32) 
	start_y#=Rand(8,32) 
	
	For z=-rad To rad 
		For x=-rad To rad 
			d#=Sqr(x*x+z*z) 
			If d < rad 
				py#=Cos(d/rad * 90) * start_y 
				px=qwrap(start_x+x,size) 
				pz=qwrap(start_z+z,size) 
				terrain(px,pz)\height = ( terrain(px,pz)\height + py ) 
			EndIf 
		Next 
	Next 
Next 

;do colours - based on code from BIG& 

For z=size-1 To 0 Step -1 
	For x=0 To size-1 
		th#=get_height(x+.5,z+.5) 
		terrain(x,z)\r = th+Rnd(80) 
		terrain(x,z)\g = th+120 
		terrain(x,z)\b = th+Rnd(80) 
		If th=0 ;*BODGE*Sea 
			terrain(x,z)\r=60+Rnd(40) 
			terrain(x,z)\g=60+Rnd(40) 
			terrain(x,z)\b=200+Rnd(40)
		Else 
			If th < 12;*BODGE*Beach 
				terrain(x,z)\r=th *10+90 
				terrain(x,z)\g=th *10+90 
				terrain(x,z)\b=240-th *20 
			End If 
		End If 
	Next 
Next 
;*BODGE* Color Landing Pad 
For z=-4 To 4 
	For x=-4 To 4 
		col=Rnd(32)+168 
		xp=qwrap(size*.5+x,size):zp=qwrap(size*.5+z,size) 
		If x >-4 And z >-4 terrain(xp,zp)\height=30 
		terrain(xp,zp)\r=col 
		terrain(xp,zp)\g=col 
		terrain(xp,zp)\b=col 
	Next 
Next 

;create map 
map_tex=CreateTexture(256,256) 
SetBuffer TextureBuffer(map_tex) 
For z=0 To size-1 
	For x=0 To size-1 
		Color terrain(x,z)\r,terrain(x,z)\g,terrain(x,z)\b 
		Plot x,z 
	Next 
Next 

SetBuffer BackBuffer() 
map=create_quad(camera) 
PositionEntity map,-5,3,6 
EntityTexture map,map_tex,0 

End Function 

;===================================================================================================== 
;CREATE DISPLAY GRID ===================================================================================================== 
;===================================================================================================== 

Function create_grid() 

mesh=CreateMesh():s=CreateSurface(mesh) 

For z=0 To divg 
	For x=0 To divg 
		v=AddVertex(s,0,0,0) 
	Next 
Next 

For z=0 To divg-1 
	For x=0 To divg-1 
		v0=x+z*(divg+1):v1=v0+1 
		v2=v1+divg+1:v3=v0+divg+1 
		AddTriangle s,v0,v1,v2 
		AddTriangle s,v2,v3,v0 
	Next 
Next 

EntityFX mesh,6 
Return mesh 

End Function 

;===================================================================================================== 
;===================================================================================================== 
;===================================================================================================== 

Function qlimit#(q#,a#,b#) 

If q < a q=a 
If q > b q=b 
Return q 

End Function 

;===================================================================================================== 
;===================================================================================================== 
;===================================================================================================== 

Function qwrap#(q#,a#) 

If q >=a q=q-a 
If q < 0 q=a+q 
Return q 

End Function 

;===================================================================================================== 
;===================================================================================================== 
;===================================================================================================== 

Function create_quad(parent=0) 

mesh=CreateMesh(parent) 
surface=CreateSurface(mesh) 
AddVertex surface,-1,1,0,0,0 
AddVertex surface,1,1,0,1,0 
AddVertex surface,1,-1,0,1,1 
AddVertex surface,-1,-1,0,0,1 
AddTriangle surface,0,1,2 
AddTriangle surface,2,3,0 
Return mesh 

End Function
