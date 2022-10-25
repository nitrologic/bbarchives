; ID: 534
; Author: Markus Rauch
; Date: 2002-12-28 07:52:11
; Title: Entity SelectBox as Collection
; Description: SelectBox

;Example Code for SelectBoxes from Markus Rauch , GERMANY

;If you free a Entity then free the Box too or free all Boxes

;MR 06.01.2003 waitmessage@yahoo.de use in subject BlitzBasic !

;----------------------------------------------------------------------------

.Top

Graphics3D 800,600,16,2

AppTitle "SelectBoxes Example"

SetBuffer BackBuffer()

;---------------------------------------------------------------------------- Camera Light

.CamAndLight

AmbientLight 32,32,32

Global CameraMoveSpeed#=0.1

Global Camera =CreateCamera()

Type CameraType
 Field CamPivot  
 Field Cam  
 Field Light  
End Type

Global Cams.CameraType = New CameraType
Cams\Cam=CreatePivot() ;Moving in World Coords
Cams\CamPivot=CreatePivot(Cams\Cam) ;Look around
MoveEntity Cams\Cam,0,0,-100

Cams\Light=CreateLight(1,Cams\CamPivot)  
CameraRange Camera,1,1000
LightColor Cams\Light ,255,255,255
LightRange Cams\Light,1000

;---------------------------------------------------------------------------- Sample Entitys

.SampleEntitys

Global Brush1=CreateBrush(255,0  ,0)
Global Brush2=CreateBrush(  0,255,0)
Global Brush3=CreateBrush(  0,255,255)

Global e1=CreateSphere(4)
ScaleMesh e1,25,25,25
MoveEntity e1,-75,0,0
NameEntity e1,"Sphere E1" 
EntityPickMode e1,2   ;<- !!!!!!!!!!!!!!! PickMode ON !!!
PaintEntity e1,Brush1

Global e2=CreateCylinder(8)
ScaleMesh e2,15,25,15
MoveEntity e2,0,0,0
NameEntity e2,"Cylinder E2" 
EntityPickMode e2,2
PaintEntity e2,Brush2

Global e3=CreateCone(8)
ScaleMesh e3,15,25,15
MoveEntity e3,75,0,0
NameEntity e3,"Cone E3" 
EntityPickMode e3,2
PaintEntity e3,Brush3

;---------------------------------------------------------------------------- SelectBox

.SelectBox

 Type mySelectBoxTYPE
  Field EntityFor
  Field EntityBox1
  Field EntityBox2
 End Type

 Global mySelectBox.mySelectBoxTYPE

 ;-----------------------------------

 Global Fittex=CreateTexture(64,64,1+4+8)        
 SetBuffer TextureBuffer(Fittex)
 Color 40,40,40
 Rect 0,0,64,64
 Color 255,255,0
 For x=0 To 63 Step 16
  Line x,0,x,63
 Next
 For y=0 To 63 Step 16
  Line 0,y,63,y
 Next
 Rect 0,0,64,64,False

 SetBuffer BackBuffer()

;####################################################################################### MainLoop

Global MausHitL
Global MausHitR

.Main

While KeyDown(1)=0 
 
 RenderWorld

 MausHitL=MouseHit(1)
 MausHitR=MouseHit(2)

 MouseTest 

 ;MoveSelectBoxes 0.025,0,0

 TurnEntity e1, 1,1,0 
 TurnEntity e2, 1,1,0 
 TurnEntity e3,-1,1,0 
 FitAllSelectBoxes  ;If Turned or moved then call it  !

 CameraSet Cams
 SetectBoxShowNames Camera

 Flip 

Wend
End

;####################################################################################### Camera

Function CameraSet(c.CameraType)

  PositionEntity Camera,EntityX    (c\CamPivot,True),EntityY  (c\CamPivot,True),EntityZ   (c\CamPivot,True)
  RotateEntity   Camera,EntityPitch(c\CamPivot,True),EntityYaw(c\CamPivot,True),EntityRoll(c\CamPivot,True) 

End Function

;####################################################################################### MouseTest Example

Function MouseTest()
 
 Local mx,my
 
 mx=MouseX()
 my=MouseY() 
   
 ;---------------------------------------------------------------------------------------------------------------
  
 Color 0,255,0 
 
 Local pick 
 Local t1$,t2$,t3$

  pick=CameraPick (Camera,mx,my)
  If pick<>0 Then
    If MausHitR Then
     FreeSelectBoxes
    EndIf
    ;If PickedEntity()<>EntityWorld Then
     t2$=" '" + EntityName(PickedEntity())+"'"
     If MausHitL Then
      SelectBox PickedEntity()
     EndIf
    ;EndIf

    t1$="PICK A Entity (Mesh) -> Left Mouse = Select"
    t3$=" , Right Mouse = Free Select Boxes"
    MouseHelp t1$+t2$+t3$
   
  EndIf ;Pick

 ;---------------------------------------------------------------------------------------------------------------
 
End Function

;############################################################################################################

Function MouseHelp(t$)

 Color 0,255,0
 Text GraphicsWidth()/2,20,t$,True,False

End Function

;############################################################################################################
;############################################################################################################
;############################################################################################################
;############################################################################################################

.SelectBoxFunctions

Function SelectBox(Entity)

 If Entity=0 Then Return 0

 DebugLog "SelectBox"

 Local twice=0

 For mySelectBox.mySelectBoxTYPE = Each mySelectBoxTYPE
  If mySelectBox\EntityFor=Entity Then
   FreeSelectBox
   twice=True
  EndIf
 Next

 If twice=True Then Return 0

 mySelectBox.mySelectBoxTYPE=New mySelectBoxTYPE

 mySelectBox\EntityFor=Entity
 mySelectBox\EntityBox1=CreateCube()
 mySelectBox\EntityBox2=CreateCube()

 EntityAlpha mySelectBox\EntityBox1,.5
 EntityAlpha mySelectBox\EntityBox2,.5
 EntityTexture mySelectBox\EntityBox1,fittex
 EntityTexture mySelectBox\EntityBox2,fittex

 FlipMesh mySelectBox\EntityBox1

 FitSelectBox mySelectBox\EntityFor

 Return True

End Function

;############################################################################################################

Function SelectBoxRemoveEntity(Entity)

 ;When delete an Entity you must remove the Box !!!

 If Entity=0 Then Return 0

 For mySelectBox.mySelectBoxTYPE = Each mySelectBoxTYPE
  If mySelectBox\EntityFor=Entity Then
   FreeSelectBox
   Return True
  EndIf
 Next

End Function

;############################################################################################################

Function FitSelectBox(Entity)

 ;06.01.2003

 ;----------------------------------- Find Mesh Min Max XYZ

 Local mx1#= 3.4*10^38,my1#= 3.4*10^38,mz1#= 3.4*10^38
 Local mx2#=-3.4*10^38,my2#=-3.4*10^38,mz2#=-3.4*10^38
 Local i,vi,s
 Local check=False

 If CountSurfaces(Entity)>0 Then
  For i=1 To CountSurfaces(Entity)
   s=GetSurface(Entity,i)
   If CountVertices(s)=>1 Then
    For vi = 0 To CountVertices(s)-1
     If VertexX(s,vi)<mx1 Then mx1=VertexX(s,vi)
     If VertexY(s,vi)<my1 Then my1=VertexY(s,vi)
     If VertexZ(s,vi)<mz1 Then mz1=VertexZ(s,vi)
     If VertexX(s,vi)>mx2 Then mx2=VertexX(s,vi)
     If VertexY(s,vi)>my2 Then my2=VertexY(s,vi)
     If VertexZ(s,vi)>mz2 Then mz2=VertexZ(s,vi)
     check=True
    Next
   EndIf ;Count Vert
  Next
 EndIf ;Count Surf

 If check=False Then mx1=0:mx2=0:my1=0:my2=0:mz1=0:mz2=0

 ;-----------------------------------

 Local mw#,mh#,md#,xp#,yp#,zp#

 mw# = Abs(mx2-mx1)
 mh# = Abs(my2-my1)
 md# = Abs(mz2-mz1)

 xp# = EntityX(Entity)
 yp# = EntityY(Entity)
 zp# = EntityZ(Entity)

 PositionEntity mySelectBox\EntityBox1,xp,yp,zp
 PositionEntity mySelectBox\EntityBox2,xp,yp,zp

 RotateEntity mySelectBox\EntityBox1,EntityPitch(Entity,True),EntityYaw(Entity,True),EntityRoll(Entity,True)
 RotateEntity mySelectBox\EntityBox2,EntityPitch(Entity,True),EntityYaw(Entity,True),EntityRoll(Entity,True)

 FitMesh mySelectBox\EntityBox1,mx1-0.1,my1-0.1,mz1-0.1,mw#+0.2,mh#+0.2,md#+0.2
 FitMesh mySelectBox\EntityBox2,mx1-0.1,my1-0.1,mz1-0.1,mw#+0.2,mh#+0.2,md#+0.2

End Function

;############################################################################################################

Function FitAllSelectBoxes()

 ;04.01.2003

 ;Refresh all when one of it had it size changed or had rotate

 For mySelectBox.mySelectBoxTYPE = Each mySelectBoxTYPE

  FitSelectBox mySelectBox\EntityFor

 Next

End Function

;############################################################################################################

Function FreeSelectBoxes()

 ;Remove All

 For mySelectBox.mySelectBoxTYPE = Each mySelectBoxTYPE
  FreeSelectBox
 Next

End Function

;############################################################################################################

Function FreeSelectBox()

 ;Remove this

  FreeEntity mySelectBox\EntityBox1
  FreeEntity mySelectBox\EntityBox2
  Delete mySelectBox

End Function

;############################################################################################################

Function MoveSelectBoxes(x#,y#,z#)

 ;Move whole collection :-)

 For mySelectBox.mySelectBoxTYPE = Each mySelectBoxTYPE
  TranslateEntity mySelectBox\EntityFor ,x,y,z,True
  TranslateEntity mySelectBox\EntityBox1,x,y,z,True
  TranslateEntity mySelectBox\EntityBox2,x,y,z,True
 Next

End Function

;############################################################################################################

Function SetectBoxShowNames(CameraX)

 ;Call it after RenderWorld and before Flip :-)

 Local x#,y#

 For mySelectBox.mySelectBoxTYPE = Each mySelectBoxTYPE

  If EntityInView(mySelectBox\EntityFor,CameraX)=True Then 

   CameraProject CameraX,EntityX(mySelectBox\EntityFor,True),EntityY(mySelectBox\EntityFor,True),EntityZ(mySelectBox\EntityFor,True)

   x#=ProjectedX()
   y#=ProjectedY()

   Color 255,128,64
   Text x#,y#,EntityName(mySelectBox\EntityFor),True,True

  EndIf

 Next
 
End Function

;############################################################################################################
