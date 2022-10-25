; ID: 1838
; Author: Fuller
; Date: 2006-10-12 20:15:41
; Title: 3d Parralaxing
; Description: Nice Parallaxing in 3d tutorial/example

Function Make_Parallaxing_Stars_and_Sky() 

plane=CreatePlane()
MoveEntity plane,0,-1,0

p_tex=LoadTexture("media\stars.bmp",2)
ScaleTexture p_tex,20,20
EntityTexture plane,p_tex  

plane2=CreatePlane()
MoveEntity plane2,0,-10,0


p_tex2=LoadTexture("media\stars.bmp",2)
ScaleTexture p_tex2,50,50
EntityTexture plane2,p_tex2  

plane3=CreatePlane()
MoveEntity plane3,0,-20,0

p_tex3=LoadTexture("media\stars.bmp")
ScaleTexture p_tex3,50,50
EntityTexture plane3,p_tex3 

;top planes
plane1=CreatePlane()
MoveEntity plane1,0,20,0
TurnEntity plane1,180,0,0


p_tex1=LoadTexture("media\stars.bmp",2)
ScaleTexture p_tex1,20,20
EntityTexture plane1,p_tex1 

plane21=CreatePlane()
MoveEntity plane21,0,30,0
TurnEntity plane21,180,0,0

p_tex21=LoadTexture("media\stars.bmp",2)
ScaleTexture p_tex21,50,50
EntityTexture plane21,p_tex21  

plane31=CreatePlane()
MoveEntity plane31,0,40,0
TurnEntity plane31,180,0,0

p_tex31=LoadTexture("media\stars.bmp")
ScaleTexture p_tex31,50,50
EntityTexture plane31,p_tex31 

;sky
sky=CreateSphere(30)
ScaleEntity sky,200,200,200
ScaleTexture p_tex31,.1,.1
EntityTexture sky,p_tex31
FlipMesh sky


end function
