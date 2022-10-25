; ID: 383
; Author: col
; Date: 2002-08-04 15:54:38
; Title: Light Mapping example
; Description: A demonstration of using lightmaps within simple geometry

Graphics3D 800,600,32,2

AppTitle "Bouncing Lights - LightMapping Demonstration - NRG Studio"

ClearTextureFilters

Global test.particle,texLight

Type particle
	Field entMain
	Field entposy
	Field entnegy
	Field entposx
	Field entnegx
	Field entposz
	Field entnegz
	Field r,g,b
	Field x#,y#,z#,dx#,dy#,dz#
End Type

cam=CreateCamera()

wall=CreateMesh()
surf=CreateSurface(wall)
AddVertex(surf,-6,1,6,  0,0)	;0 : -z top left
AddVertex(surf,6,1,6,   6,0)	;1 : -z top right
AddVertex(surf,6,-1,6,  6,1)	;2 : -z bottom right
AddVertex(surf,-6,-1,6, 0,1)	;3 : -z bottom left

AddVertex(surf,-6,1,-6,  0,0)	;4 : +z top left
AddVertex(surf,6,1,-6,   6,0)	;5 : +z top right
AddVertex(surf,6,-1,-6,  6,1)	;6 : +z bottom right
AddVertex(surf,-6,-1,-6, 0,1)	;7 : +z bottom left

AddVertex(surf,-6,1,-6 ,0,0)	;8 : -x
AddVertex(surf,-6,1,6  ,6,0)	;9 : -x
AddVertex(surf,-6,-1,6 ,6,1)	;10: -x
AddVertex(surf,-6,-1,-6,0,1)	;11: -x

AddVertex(surf,6,1,6,0,0)		;12: +x
AddVertex(surf,6,1,-6,6,0)		;13: +x
AddVertex(surf,6,-1,-6,6,1)		;14: +x
AddVertex(surf,6,-1,6,0,1)		;15: +x

AddTriangle(surf,0,1,2)		;-z
AddTriangle(surf,0,2,3)		;-z
AddTriangle(surf,5,7,6)		;+z
AddTriangle(surf,5,4,7)		;+z
AddTriangle(surf,8,9,10)	;-x
AddTriangle(surf,8,10,11)	;-x
AddTriangle(surf,12,14,15)	;+x
AddTriangle(surf,12,13,14)  ;+x

Floors=CreateMesh()
surf=CreateSurface(Floors)
AddVertex(surf,-6,-1,-6,0,0)
AddVertex(surf,6,-1,-6,4,0)
AddVertex(surf,6,-1,6,4,4)
AddVertex(surf,-6,-1,6,0,4)

AddTriangle(surf,2,1,0)
AddTriangle(surf,3,2,0)

roof=CreateMesh()
surf=CreateSurface(roof)
AddVertex(surf,-6,1,-6,0,0)
AddVertex(surf,6,1,-6,4,0)
AddVertex(surf,6,1,6,4,4)
AddVertex(surf,-6,1,6,0,4)

AddTriangle(surf,0,1,2)
AddTriangle(surf,0,2,3)

EntityColor floors,200,200,200 ;darken the floor a little

;load textures
texWall=LoadTexture("wall.jpg")
texFloor=LoadTexture("floor.jpg",8)
texRoof=LoadTexture("roof.jpg",8)
texLight=LoadTexture("light.bmp",2)

;set textures
EntityTexture wall,texWall
EntityTexture floors,texFloor
EntityTexture roof,texRoof

;Make things a little dark
AmbientLight 90,90,90

;Add 2 lights to show the spheres
light1=CreateLight()
light2=CreateLight()
RotateEntity light1,90,0,0
RotateEntity light2,270,0,0

;Move viewpoint back a bit to get more lights into view
PositionEntity cam,0,0,-4

;Some scratch variables
help=False
lights=0
maxtris=0
maxtriframe=0

;initialize 10 light sources
For n=0 To 9
	AddLight:lights=lights+1
Next


While Not KeyDown(1)
	;move view with the mouse
	mx#=MouseX()/2-400
	RotateEntity cam,0,mx,0
		
	;update each light
	For test.particle=Each particle
		UpdateLight(test)
	Next	
	
	;Render 3D to the backbuffer
	RenderWorld
	
	;calculate frames per second - Credit goes to Kostik (David Tews)
	counter=counter+1
	If time=0 time=MilliSecs()
	If time+1001 <MilliSecs()
		rate=counter
		counter=0
		time=MilliSecs()
	EndIf

	If TrisRendered()>maxtris maxtris=TrisRendered():maxtriframe=rate
	
	;do numpad + and -
	If KeyHit(78) AddLight: lights=lights+1
	If KeyHit(74) And lights>1 DeleteLight(First particle): lights=lights-1
	
	;TAB for help
	If KeyHit(15) help=Not help
	If Not help
		Text 0,0,"Tab for HELP ON/OFF"
	Else
		Text 0,0,"Move mouse to look"
		Text 0,12,"Numpad + and - to increase/decrease number of lights"
		Text 0,36,"INFO:"
		Text 0,48,"No of lights rendered       - "+lights
		Text 0,60,"No of triangles rendered    - "+TrisRendered()
		Text 0,72,"Maximum triangles rendered  - "+maxtris+" at "+maxtriframe+" FPS"
		Text 0,84,"No of rrames rendered per s - "+rate
	EndIf
	
	Flip
Wend

For test.particle=Each particle
	Deletelight(test)
Next

End

Function UpdateLight(test.particle)
	test\x=test\x+test\dx
	test\y=test\y+test\dy
	test\z=test\z+test\dz
	If test\x>5.9 Or test\x<-5.9 test\dx=-test\dx
	If test\y>.9 Or test\y<-.9 test\dy=-test\dy
	If test\z>5.9 Or test\z<-5.9 test\dz=-test\dz
		
	EntityAlpha test\entposx,test\x-5
	EntityAlpha test\entnegx,-5-test\x
	EntityAlpha test\entnegy,-test\y
	EntityAlpha test\entposy,test\y
	EntityAlpha test\entposz,test\z-5
	EntityAlpha test\entnegz,-5-test\z
				
	PositionEntity test\entMain,test\x,test\y,test\z
	PositionEntity test\entnegy,test\x,-.99,test\z
	PositionEntity test\entposy,test\x,.99,test\z
	PositionEntity test\entposx,5.99,test\y,test\z
	PositionEntity test\entnegx,-5.99,test\y,test\z
	PositionEntity test\entposz,test\x,test\y,5.99
	PositionEntity test\entnegz,test\x,test\y,-5.99
End Function

Function DeleteLight(light.particle)
	FreeEntity light\entmain
	FreeEntity light\entnegx
	FreeEntity light\entposx
	FreeEntity light\entnegy
	FreeEntity light\entposy
	FreeEntity light\entnegz
	FreeEntity light\entposz
	Delete light.particle
End Function

Function AddLight()
	test.particle=New particle
	test\entMain=CreateSphere()
	ScaleEntity test\entMain,.1,.1,.1
	test\x=Rnd(5)-2.5
	test\y=Rnd(1)-.4
	test\z=Rnd(5)-2.5
	test\dx=Rnd(.1)-.05
	test\dy=Rnd(.1)-.05
	test\dz=Rnd(.1)-.05
	test\r=Rnd(155)+100
	test\g=Rnd(155)+100
	test\b=Rnd(155)+100
	EntityColor test\entMain,test\r,test\g,test\b
	EntityAlpha test\entmain,.2
	
	;now for the lightmaps
	;first the floor
	negy=CreateMesh()
	surf=CreateSurface(negy)
	AddVertex(surf,-1,0,1,0,0)
	AddVertex(surf,1,0,1,1,0)
	AddVertex(surf,1,0,-1,1,1)
	AddVertex(surf,-1,0,-1,0,1)
	AddTriangle(surf,0,1,2)
	AddTriangle(surf,0,2,3)

	EntityColor negy,test\r,test\g,test\b
	EntityTexture negy,texLight
	EntityFX negy,1
	test\entnegy=negy
	
	;then the roof
	posy=CopyMesh(negy)
	RotateMesh posy,180,0,0
	EntityColor posy,test\r,test\g,test\b
	EntityTexture posy,texLight
	EntityFX posy,1	
	test\entposy=posy
	
	;posx
	posx=CopyMesh(posy)
	RotateMesh posx,-90,90,0
	EntityColor posx,test\r,test\g,test\b
	EntityTexture posx,texLight
	EntityFX posx,1
	test\entposx=posx

	;negx
	negx=CopyMesh(posx)
	RotateMesh negx,0,180,0
	EntityColor negx,test\r,test\g,test\b
	EntityTexture negx,texLight
	EntityFX negx,1
	test\entnegx=negx
	
	;posz
	posz=CopyMesh(posx)
	RotateMesh posz,0,90,0
	EntityColor posz,test\r,test\g,test\b
	EntityTexture posz,texLight
	EntityFX posz,1
	test\entposz=posz
	
	;negz
	negz=CopyMesh(posz)
	RotateMesh negz,0,180,0
	EntityColor negz,test\r,test\g,test\b
	EntityTexture negz,texLight
	EntityFX negz,1
	test\entnegz=negz

Return
