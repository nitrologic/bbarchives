; ID: 1793
; Author: jfk EO-11110
; Date: 2006-08-22 16:38:17
; Title: Motion Blur
; Description: Dedicated for racing games (Updated)

Graphics3D 1024,768,32,1
SetBuffer BackBuffer()

light=CreateLight()
RotateEntity light,45,45,0

cube=CreateCube()
TranslateEntity cube,0,0,3.0
ScaleEntity cube,.1,2,.1

camera=CreateCamera()
CameraRange camera,0.01,100

CameraClsColor camera,255,0,0




; init motionblur
motion_blur_on=1
motion_blur_quad=create_blurquad(camera) ; create a special 4-tris quad with a zero Alpha center vertex.
EntityFX motion_blur_quad,2 Or 1 Or 16
motion_blur_tex=CreateTexture(1024,1024,256)
EntityAlpha motion_blur_quad,0.333
; use the following remarked lines if you need to visually control texture alignement (skip copyrect render to tex!)
;SetBuffer TextureBuffer(motion_blur_tex)
;Color 0,255,0  
;Rect 0,128,1024,768,0
;SetBuffer BackBuffer()
EntityTexture motion_blur_quad,motion_blur_tex
TranslateEntity motion_blur_quad,-(1.0/2048.0),0-(1.0/2048.0),  0.995 ;1.0 would be exact screen matching (pixelperfect)
EntityOrder motion_blur_quad,-1

; eo init motion blur






While KeyDown(1)=0
    ; call motion blur in mainloop--------
	If KeyHit(57) ; space= toggle motion blur
	 motion_blur_on=motion_blur_on Xor 1
	 If motion_blur_on=0 Then 
	  HideEntity motion_blur_quad
	 Else
	  ShowEntity motion_blur_quad
	 EndIf
	EndIf
    ; update motion blur texture
    If motion_blur_on<>0
     CopyRect 0,0,1024,768,0,128,BackBuffer(),TextureBuffer(motion_blur_tex)
    EndIf
    ;-----
    TurnEntity cube,.2,.4,.6
    UpdateWorld()
    RenderWorld()
    VWait:Flip 0
Wend




Function create_blurquad(par=0)
 Local al1#,al2#,m,s,v0,v1,v2,tr
 al1#=1.0
 al2#=0.2
 m=CreateMesh()
 s=CreateSurface(m)

 v0=AddVertex(s,-1,-1,0,   0,1)
 v1=AddVertex(s,+1,-1,0,   1,1)
 v2=AddVertex(s, 0,0 ,0,   .5,.5)
 VertexColor s,v0,255,255,255,al1#
 VertexColor s,v1,255,255,255,al1#
 VertexColor s,v2,255,255,255,al2#
 tr=AddTriangle(s,v0,v1,v2)  

 v0=AddVertex(s,+1,-1,0,   1,1)
 v1=AddVertex(s,+1,+1,0,   1,0)
 v2=AddVertex(s, 0,0 ,0,   .5,.5)
 VertexColor s,v0,255,255,255,al1#
 VertexColor s,v1,255,255,255,al1#
 VertexColor s,v2,255,255,255,al2#
 tr=AddTriangle(s,v0,v1,v2)

 v0=AddVertex(s,+1,+1,0,   1,0)
 v1=AddVertex(s,-1,+1,0,   0,0)
 v2=AddVertex(s, 0,0 ,0,   .5,.5)
 VertexColor s,v0,255,255,255,al1#
 VertexColor s,v1,255,255,255,al1#
 VertexColor s,v2,255,255,255,al2#
 tr=AddTriangle(s,v0,v1,v2)

 v0=AddVertex(s,-1,+1,0,   0,0)
 v1=AddVertex(s,-1,-1,0,   0,1)
 v2=AddVertex(s, 0,0 ,0,   .5,.5)
 VertexColor s,v0,255,255,255,al1#
 VertexColor s,v1,255,255,255,al1#
 VertexColor s,v2,255,255,255,al2#
 tr=AddTriangle(s,v0,v1,v2)

 FlipMesh m
 UpdateNormals m
 If par <>0 Then EntityParent m,par
 Return m
End Function
