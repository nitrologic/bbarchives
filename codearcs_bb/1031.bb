; ID: 1031
; Author: AbbaRue
; Date: 2004-05-16 17:11:00
; Title: Random Terrain Tile
; Description: Creates Random Terrain Tile

;Written by Harold W. Lehmann of Sarnia, On. Canada. 
;Submitted to archeives on May 16, 2004. 
;You may use this code as you please
;Just give me some Credit in your program if you do. 
; ------------------ 
;E key to create a 3D Mesh Tile.
;CRSR Del. Ins. keys to Turn Mesh for viewing. 
;Number keys for manual develop stages.
;Try the following order: 0 9 5 4 8 7 1 3. To see stages.
;Leave comment of what you think, after testing.
; ------------------ 
; Vert. Tester
; ------------------ 

;Graphics3D 640,480
Graphics3D 1024,768,32,1

Global xx=0
Global YY#=0
Global zz=0
Global nodes=360	;number of nodes x or z -1

Dim H(nodes*nodes)
Dim V(nodes,nodes); set up array to store vertices
Dim T(Nodes*nodes); set up array to store Triangles

 
SetBuffer BackBuffer() 

camera=CreateCamera() 

light=CreateLight() 
RotateEntity light,45,0,0 

ts=512 ;Texture creation using WritePixel 
tex57=CreateTexture (ts,ts,1)
SetBuffer TextureBuffer (tex57,0) 
For cdy= 0 To ts-1
For cdx= 0 To ts-1
rca=255 
rcg=Rnd(100,255)
;If rcg<70 Then rcg=0
rcr=rcg
rcb=rcg
argb=0 ;clear color
argb=(rca Shl 24) Or (rcr Shl 16) Or (rcg Shl 8) Or (rcb) 
;If rcg<74 Then argb=0 ;just to be sure lots of black
WritePixel cdx,cdy,argb 

Next 
Next 

SetBuffer BackBuffer() 

; Create blank mesh 
Land=CreateMesh() 

; Create blank Surface which is attached to mesh (Surfaces must always be attached to a mesh) 
SF=CreateSurface(Land) 
;EntityTexture land,tex57 ;a texture if I need it

;mm must be 2 less then desired verts. mm=15 gives 17 verts.
mm=17 ;Number of units-1 (start with 17)

For nx=0 To mm+1
For nz=0 To mm+1
	V(nx,nz)=AddVertex(SF,nx,0,nz,(nx*0.0625),(nz*0.0625),0 )	;create all vertices
Next 
Next

;v0=AddVertex(SF,1,0,1) ; Node corner

tt=0
For x=0 To mm Step 2 ;0-6 has 16 triangles (16x16=256)
For z=0 To mm Step 2

t(tt)=AddTriangle( SF,V(x,z),V(x,z+1),V(x+1,z+1) ) 		;1
tt=tt+1
t(tt)=AddTriangle( SF,V(x+1,z+1),V(x+1,z),V(x,z) ) 	;2
tt=tt+1
t(tt)=AddTriangle( SF,V(x,z+1),V(x,z+2),V(x+1,z+1) ) 		;3
tt=tt+1
t(tt)=AddTriangle( SF,V(x+1,z+1),V(x,z+2),V(x+1,z+2) ) 		;4
tt=tt+1
t(tt)=AddTriangle( SF,V(x+1,z),V(x+1,z+1),V(x+2,z) ) 		;5
tt=tt+1
t(tt)=AddTriangle( SF,V(x+2,z),V(x+1,z+1),V(x+2,z+1) ) 		;6
tt=tt+1
t(tt)=AddTriangle( SF,V(x+1,z+1),V(x+1,z+2),V(x+2,z+2) ) 	;7
tt=tt+1
t(tt)=AddTriangle( SF,V(x+2,z+2),V(x+2,z+1),V(x+1,z+1) ) 	;8
tt=tt+1

.ctc

Next 
Next 

; Now we will position our Mesh in front of the camera so we can see it! 
PositionEntity Land,-9,-7,12 

; Enable wireframe mode so we can see structure of model more clearly 
WireFrame True  

; And a quick loop that renders the scene and displays the contents on the screen until we press esc 
While Not KeyDown(1) 

; Constantly turn our Mesh to show it off a bit 
; TurnEntity Land,0,1,0 
If KeyDown( 205 )=True Then TurnEntity Land,0,0,-1	;Right
If KeyDown( 203 )=True Then TurnEntity Land,0,0,1	;Left
If KeyDown( 208 )=True Then TurnEntity Land,-1,0,0	;Down
If KeyDown( 200 )=True Then TurnEntity Land,1,0,0	;up
If KeyDown( 210 )=True Then TurnEntity Land,0,-1,0	;ins
If KeyDown( 211 )=True Then TurnEntity Land,0,1,0	;del
If KeyDown( 199 )=True Then RotateEntity Land,0,0,0	;hom
If KeyDown( 199 )=True Then PositionEntity Land,-9,-7,12	;hom
If KeyDown( 201 )=True Then RotateEntity Land,-90,0,0 	;pgup
If KeyDown( 201 )=True Then PositionEntity Land,-9,-9,13	;pgup

If KeyDown( 52 )=True Then MoveEntity camera,0,0,+1 ;. key
If KeyDown( 51 )=True Then MoveEntity camera,0,0,-1 ;, key

; keys for testing
If KeyDown( 20 )=True Then Gosub Test1	;t key
If KeyDown( 45 )=True Then Gosub Test2	;x key
If KeyDown( 44 )=True Then Gosub Test3	;z key
If KeyDown( 46 )=True Then Gosub Test4	;c key
If KeyDown( 47 )=True Then Gosub Test5	;v key
If KeyDown( 48 )=True Then Gosub Test6	;b key
If KeyDown( 17 )=True Then Gosub Test7	;w key
If KeyDown( 18 )=True Then Gosub Test8	;e key


; subroutine calls
.keys
If KeyDown( 2 )=True Then Gosub Octave01	;1 key
If KeyDown( 3 )=True Then Gosub Octave02	;2 key
If KeyDown( 4 )=True Then Gosub Octave03	;3 key
If KeyDown( 5 )=True Then Gosub Octave04	;4 key
If KeyDown( 6 )=True Then Gosub Octave05	;5 key
If KeyDown( 7 )=True Then Gosub Octave06	;6 key
If KeyDown( 8 )=True Then Gosub Octave07	;7 key
If KeyDown( 9 )=True Then Gosub Octave08	;8 key
If KeyDown( 10 )=True Then Gosub Octave09	;9 key
If KeyDown( 11 )=True Then Gosub Octave10	;0 key




RenderWorld 
	Text 10,12," Triangles: " + TrisRendered() 
	Text 10,24," XX: " + XX	
	Text 10,36," YY#: " + VertexY# ( SF,V(xx,zz) )
	Text 10,48," ZZ: " + ZZ 
	Text 10,60," Index: " + V(xx,zz) 
	Text 10,72," Test#: " + kk#
Flip 

Wend 


End

.Test1 ;T key
;VertexCoords SFace,index,x#,y#,z#
YY#=VertexY# ( SF,V(xx,zz) )+0.1
VertexCoords SF,V(xx,zz),xx,YY#,zz

.k20
If KeyDown(20) Goto K20 ;loop until key released
Return 

.Test2 ;X key
xx=xx+1

.k45
If KeyDown(45) Goto K45 ;loop until key released
Return 


.Test3 ;Z key

zz=zz+1
.k44
If KeyDown(44) Goto K44 ;loop until key released
Return 

.Test4 ;C key

xx=0
zz=0
.k46
If KeyDown(46) Goto K46 

Return 

.Test5 ;V key

YY#=VertexY# ( SF,V(xx,zz) )-0.1
VertexCoords SF,V(xx,zz),xx,YY#,zz

.k47
If KeyDown(47) Goto K47
Return 


.Test6 ;B key

YY#=0
VertexCoords SF,V(xx,zz),xx,YY#,zz

.k48
If KeyDown(48) Goto K48
Return 


.Test7 ;W key

If YY#=Abs(YY#) Then WireFrame True Else WireFrame False 
.k17
If KeyDown(17) Goto K17
Return 



.Test8 ;E key

Gosub Octave10 
Gosub Octave09 
Gosub Octave05 
Gosub Octave04 
Gosub Octave08 
;Gosub Octave02 
Gosub Octave07 
;Gosub Octave06 
Gosub Octave01 
Gosub Octave03 
;Gosub Octave06 

.k18
;If KeyDown(18) Goto K18

Return


.Octave01	;1 key 

For xx= 0 To 15 Step 3
For zz= 0 To 15 Step 3 

A1#=VertexY#(SF,v(xx,zz))
A2#=VertexY#(SF,v(xx,zz+3))
A3#=VertexY#(SF,v(xx+3,zz+3))
A4#=VertexY#(SF,v(xx+3,zz))

YY#=(A1+A2+A4)/3
VertexCoords SF,V(xx+1,zz+1),xx+1,YY#,zz+1

YY#=(A1+A2+A3)/3
VertexCoords SF,V(xx+1,zz+2),xx+1,YY#,zz+2

YY#=(A2+A3+A4)/3
VertexCoords SF,V(xx+2,zz+2),xx+2,YY#,zz+2

YY#=(A1+A3+A4)/3
VertexCoords SF,V(xx+2,zz+1),xx+2,YY#,zz+1

Next 
Next 


.k2
If KeyDown(2) Goto K2

Return 


.Octave02	;2 key 

For xx=0 To 18 ;all verts
For zz=0 To 18 ;all verts


YY#=VertexY#(SF,v(xx,zz))
YY#=YY#+(Rnd(-YY#,YY#))

VertexCoords SF,V(xx,zz),xx,YY#,zz

Next ;zz
Next ;xx

.k3
If KeyDown(3) Goto K3

Return 

.Octave03	;3 key 

For xx= 3 To 12 Step 3
For zz= 3 To 12 Step 3 


YY#=(VertexY#(SF,v(xx,zz))+VertexY#(SF,v(xx-1,zz-1))+VertexY#(SF,v(xx-1,zz+1)))/3
VertexCoords SF,V(xx-1,zz),xx-1,YY#,zz

YY#=(VertexY#(SF,v(xx-3,zz))+VertexY#(SF,v(xx-2,zz-1))+VertexY#(SF,v(xx-2,zz+1)))/3
VertexCoords SF,V(xx-2,zz),xx-2,YY#,zz

YY#=(VertexY#(SF,v(xx,zz))+VertexY#(SF,v(xx-1,zz-1))+VertexY#(SF,v(xx+1,zz-1)))/3
VertexCoords SF,V(xx,zz-1),xx,YY#,zz-1

YY#=(VertexY#(SF,v(xx,zz-3))+VertexY#(SF,v(xx-1,zz-2))+VertexY#(SF,v(xx+1,zz-2)))/3
VertexCoords SF,V(xx,zz-2),xx,YY#,zz-2



Next 
Next 


.k4
If KeyDown(4) Goto K4

Return 


.Octave04	;4 key 


For xx=0 To 18 ;all verts
For zz=0 To 18 ;all verts


YY#=VertexY#(SF,v(xx,zz))
YY#=YY#+Rnd(-1,1)

VertexCoords SF,V(xx,zz),xx,YY#,zz

Next 
Next 



.k5
If KeyDown(5) Goto K5

Return 

.Octave05	;4 key 
;smoother with random added 
;Here I am only dividing by 2 for 3 verts
;this gives me an increase

For xx= 0 To 15 Step 3
For zz= 0 To 15 Step 3 

A1#=VertexY#(SF,v(xx,zz))
A2#=VertexY#(SF,v(xx,zz+3))
A3#=VertexY#(SF,v(xx+3,zz+3))
A4#=VertexY#(SF,v(xx+3,zz))


;YY#=VertexY#(SF,v(xx,zz))
;YY#=YY#+(Rnd(-YY#,YY#))

;Using /2 here increases many places 
YY#=(A1+A2+A4)/2
YY#=YY#+(Rnd(-YY#,YY#))

VertexCoords SF,V(xx+1,zz+1),xx+1,YY#,zz+1

YY#=(A1+A2+A3)/2
YY#=YY#+(Rnd(-YY#,YY#))

VertexCoords SF,V(xx+1,zz+2),xx+1,YY#,zz+2

YY#=(A2+A3+A4)/2
YY#=YY#+(Rnd(-YY#,YY#))

VertexCoords SF,V(xx+2,zz+2),xx+2,YY#,zz+2

YY#=(A1+A3+A4)/2
YY#=YY#+(Rnd(-YY#,YY#))

VertexCoords SF,V(xx+2,zz+1),xx+2,YY#,zz+1

Next 
Next 

For xx= 3 To 12 Step 3
For zz= 3 To 12 Step 3 

;tried using /2 here also but it didn't look right
YY#=(VertexY#(SF,v(xx,zz))+VertexY#(SF,v(xx-1,zz-1))+VertexY#(SF,v(xx-1,zz+1)))/3
YY#=YY#+(Rnd(-YY#,YY#))

VertexCoords SF,V(xx-1,zz),xx-1,YY#,zz

YY#=(VertexY#(SF,v(xx-3,zz))+VertexY#(SF,v(xx-2,zz-1))+VertexY#(SF,v(xx-2,zz+1)))/3
YY#=YY#+(Rnd(-YY#,YY#))

VertexCoords SF,V(xx-2,zz),xx-2,YY#,zz

YY#=(VertexY#(SF,v(xx,zz))+VertexY#(SF,v(xx-1,zz-1))+VertexY#(SF,v(xx+1,zz-1)))/3
YY#=YY#+(Rnd(-YY#,YY#))

VertexCoords SF,V(xx,zz-1),xx,YY#,zz-1

YY#=(VertexY#(SF,v(xx,zz-3))+VertexY#(SF,v(xx-1,zz-2))+VertexY#(SF,v(xx+1,zz-2)))/3
YY#=YY#+(Rnd(-YY#,YY#))

VertexCoords SF,V(xx,zz-2),xx,YY#,zz-2

Next 
Next 


.k6
If KeyDown(6) Goto K6

Return 

.Octave06	;6 key 

For xx=2 To 15 Step 3 
For zz=2 To 15 Step 3 

YY#=VertexY#(SF,v(xx+1,zz+1))+VertexY#(SF,v(xx+1,zz-1))
YY#=YY#+VertexY#(SF,v(xx-1,zz-1))+VertexY#(SF,v(xx-1,zz+1))
YY#=YY#/4
YY#=YY#+(Rnd(-YY#,YY#))
kk#=YY#
VertexCoords SF,V(xx,zz),xx,YY#,zz
Next 
Next 

.k7
If KeyDown(7) Goto K7

Return 

.Octave07	;7 key 

;smoother with random added

For xx= 0 To 15 Step 3
For zz= 0 To 15 Step 3 

A1#=VertexY#(SF,v(xx,zz))
A2#=VertexY#(SF,v(xx,zz+3))
A3#=VertexY#(SF,v(xx+3,zz+3))
A4#=VertexY#(SF,v(xx+3,zz))


;YY#=VertexY#(SF,v(xx,zz))
;YY#=YY#+(Rnd(-YY#,YY#))


YY#=(A1+A2+A4)/3
YY#=YY#+(Rnd(-YY#,YY#))

VertexCoords SF,V(xx+1,zz+1),xx+1,YY#,zz+1

YY#=(A1+A2+A3)/3
YY#=YY#+(Rnd(-YY#,YY#))

VertexCoords SF,V(xx+1,zz+2),xx+1,YY#,zz+2

YY#=(A2+A3+A4)/3
YY#=YY#+(Rnd(-YY#,YY#))

VertexCoords SF,V(xx+2,zz+2),xx+2,YY#,zz+2

YY#=(A1+A3+A4)/3
YY#=YY#+(Rnd(-YY#,YY#))

VertexCoords SF,V(xx+2,zz+1),xx+2,YY#,zz+1

Next 
Next 

For xx= 3 To 12 Step 3
For zz= 3 To 12 Step 3 


YY#=(VertexY#(SF,v(xx,zz))+VertexY#(SF,v(xx-1,zz-1))+VertexY#(SF,v(xx-1,zz+1)))/3
YY#=YY#+(Rnd(-YY#,YY#))

VertexCoords SF,V(xx-1,zz),xx-1,YY#,zz

YY#=(VertexY#(SF,v(xx-3,zz))+VertexY#(SF,v(xx-2,zz-1))+VertexY#(SF,v(xx-2,zz+1)))/3
YY#=YY#+(Rnd(-YY#,YY#))

VertexCoords SF,V(xx-2,zz),xx-2,YY#,zz

YY#=(VertexY#(SF,v(xx,zz))+VertexY#(SF,v(xx-1,zz-1))+VertexY#(SF,v(xx+1,zz-1)))/3
YY#=YY#+(Rnd(-YY#,YY#))

VertexCoords SF,V(xx,zz-1),xx,YY#,zz-1

YY#=(VertexY#(SF,v(xx,zz-3))+VertexY#(SF,v(xx-1,zz-2))+VertexY#(SF,v(xx+1,zz-2)))/3
YY#=YY#+(Rnd(-YY#,YY#))

VertexCoords SF,V(xx,zz-2),xx,YY#,zz-2

Next 
Next 



.k8
If KeyDown(8) Goto K8

Return 

.Octave08	;8 key 

For xx=3 To 15 Step 3 
For zz=3 To 15 Step 3 

YY#=VertexY#(SF,v(xx+1,zz+1))+VertexY#(SF,v(xx+1,zz-1))
YY#=YY#+VertexY#(SF,v(xx-1,zz-1))+VertexY#(SF,v(xx-1,zz+1))
YY#=YY#/4
YY#=YY#+(Rnd(-YY#,YY#))
kk#=YY#
VertexCoords SF,V(xx,zz),xx,YY#,zz
Next 
Next 

.k9
If KeyDown(9) Goto K9

Return 


.Octave09	;9 key 

For dd=0 To 33
xx=Abs(Rnd(6))
xx=xx*3
YY#=Rnd(-5,5)
zz=Abs(Rnd(6))
zz=zz*3
VertexCoords SF,V(xx,zz),xx,YY#,zz

Next ;dd

.k10
If KeyDown(10) Goto K10

Return 


.Octave10	;0 key 


For xx=0 To 18 
For zz=0 To 18 

VertexCoords SF,V(xx,zz),xx,0,zz
Next 
Next 


.k11
If KeyDown(11) Goto K11

Return 

Function Vxy(ss,ee,ff)
YY#=(VertexY#(ss,v(ee+2,ff-1))+VertexY#(ss,v(ee-1,ff-1))+VertexY#(ss,v(ee-1,ff+2)))/3
Return YY# 
End Function ;End Vxy function 


;  The end!
