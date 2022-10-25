; ID: 917
; Author: Matty
; Date: 2004-02-05 04:39:28
; Title: Quad Functions
; Description: Creating Quads for use as billboard/sprite models

;		How To Use These Quad Functions:
;
;First Step: Create Your Quad For Use
;
;Run the functions "NextQuad" and "NextQuadWithMySurface" to get the first available free Quad
;Choose the lowest of the two return values, not including any values below 0.

;Check if the Quad already has your surface "DoesQuadHaveSurface(QuadID)"
;If it does not then run CreateQuad, if it does then run ResetQuad
;

;
;Once the Quad is created or reset the following functions are used to manipulate the Quad
;
;InitialQuadPosition, MoveQuad, LiftQuad, ChangeQuadTexture (coordinates), AnimateQuad, ResizeQuad
;ScaleQuad, ConformQuadToTerrain,
;
;
;Prior to calling renderworld you will need to call:  PointQuadToCamera, Position Quad
;
;When removing a Quad the following is called:
;
;RemoveQuadCheat, RemoveUnusedSurface.  
;
;RemoveQuadSlow is yet to be implemented.
;
;Feel free to change the value of MaxQuad according to your PCs capabilities.  
;
;
;
;
;11th February - Fixed Scale, Resize and Added 2 new Functions.
;
;
;
;
;
;
;
;
;
;
;

;
;Feb 8th Further Quad functions:
;
;Perhaps I need a remove surface function, which is only called if there are no references to that
;surface anywhere at that point in time....
;
;
;
;





;New Functions as of Feb 8th, 
;AssignQuadRange = generates a range of valid values to be used in say a particle engine or otherwise
;It also assigns a name to these quad ranges...is the return value of the function.
;MyLower/MyUpperRange functions return the lower and upper range of quad array indices for a particular
;effect name.
;
;New Functions as of Feb 7th:
;
;ConformQuadToTerrain - will lay a quad flat on the terrain with textured side facing up
;LiftQuad - will raise a quad that has been conformed to the terrain an arbitrary height
;Does Quad Exist - will check to see if a quad has been created with that ID
;RemoveQuadCheat - remove quads after they have been used. Note this is not the best way to do it
;but it is the easiest.
;
;ScaleQuad - will allow a quad's size to be rescaled by a fractional amount.
;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;Functions that I will use for my Quads.  As well as types.  These types are stored in an array...
;
;
;
;NextQuad() - returns the next available quad id
;
;;Move Quad (ID,Angle,Speed#,Terrain)
;
;ID=The array index for that Quad as defined in the array below
;Angle = the Angle about the Y axis for moving the quad in the XZ plane.  
;Speed# = how far to move the Quad 
;Terrain - the handle for the Terrain entity if one exists, otherwise set to 0 for no terrain checking
;
;
;
;PointQuadToCamera (ID,CameraYAngle,CamX,Camz,Mode)  
;ID = Array index of quad
;CameraYAngle = the yaw angle (angle about the Y axis) of the Camera
;CamX,CamZ the camera position in the XZ plane
;Mode = 1 or 2, 1 = rotate quad according to camera rotation, 2 = rotate quad according to position
;of camera.  Mode 1 is faster, Mode 2 is more useful for somethings like trees...
;
;PositionQuad (ID) - ID = array id of quad, will place quad in 3d world after all 
;movement and rotations about the y axis are performed.
;
;
;ChangeQuadTexture (ID,LeftU#,UpperV#,RightU#,LowerV#) - ID, UV Coordinates of corners of Quad, useful
;for animating a Quad with frames of animation that are stored in the texture used for the surface.
;So for example you might have a 512x512 image with 64 images to be used in an animation
;
;If you are using Render Tweening then you only need to call Change Quad Texture and Position quad 
;on the same frame that "capture world" is executed.
;
;
;CreateQuad(ID,Surface,Width#,Height#) - ID of array, Surface Handle, Width and Height of Quad.
;
;InitialQuadPosition(ID,X#,Y#,Z#,Terrain) - Call this after creating the Quad, It may be called 
;
;at othertimes but it wasn't what was originally intended...


Const MaxQuad=2000   ;This value will most likely be about 2000.
;
;Adjust this value as you see fit
;


Dim Cosine#(360)
Dim Sine#(360)
For i=0 To 360
Cosine#(i)=Cos(i)
Sine#(i)=Sin(i)
Next




Type QuadObj

Field SurfaceId

Field Index1
Field V1_X#
Field V1_Y#
Field V1_Z#
Field V1_U#
Field V1_V#

Field Index2
Field V2_X#
Field V2_Y#
Field V2_Z#
Field V2_U#
Field V2_V#


Field Index3
Field V3_X#
Field V3_Y#
Field V3_Z#
Field V3_U#
Field V3_V#

Field Index4
Field V4_X#
Field V4_Y#
Field V4_Z#
Field V4_U#
Field V4_V#

Field Width#   ;size of the quad in the x direction
Field Height#  ;size of the quad in the y direction

Field Angle ;Y angle - as these quads will only rotate on a single axis.....
Field Blank
End Type


Type QuadRangeObj

Field EffectName$
Field LowerQuad
Field UpperQuad
Field SurfaceID
Field InUse
End Type


Type EmptyQuadList
Field QuadID

End Type

Type SurfacedQuad
Field QuadID
Field SurfaceID
Field Blank

End Type


Dim Quad.QuadObj(MaxQuad)
For i=0 To MaxQuad
Quad(i)=New QuadObj
Quad(i)\Blank=1
QuadList.EmptyQuadList=New EmptyQuadList
QuadList\QuadID=i
Next



Function InitialQuadPosition(Id,X#,Y#,Z#,Terrain)
Quad(ID)\V1_X=X#-Quad(ID)\Width*0.5
Quad(ID)\V1_Y=Y#
Quad(ID)\V1_Z=Z#
MoveQuad(ID,0,0,Terrain)

End Function





;For assigning ranges to the quads the following procedure will be put into effect....
;
;First Assign a Range by calling this function in the following manner:
;
;RangeName$=AssignQuadRange$(AmountRequired,RangeName$)
;
;Then Get the value of MyLowerRange(RangeName$) and MyUpperRange(RangeName$), if neither
;is -1 then these are your valid ranges for that effect...
;
;
;
;
;
;




Function MoveQuad(ID,Angle,Speed#,Terrain) 
Angle=Angle Mod 360
If angle<0 Then angle=angle+360
If angle>360 Then angle=angle-360
Quad(ID)\V1_X=Quad(ID)\V1_X+Speed*Cosine#(Angle)
Quad(ID)\V1_Z=Quad(ID)\V1_Z+Speed*Sine#(Angle)
If Terrain<>0 Then Quad(ID)\V1_Y=TerrainY(Terrain,Quad(ID)\V1_X,Quad(ID)\V1_Y,Quad(ID)\V1_Z) 

Quad(ID)\V2_X=Quad(Id)\V1_X
Quad(ID)\V2_Y=Quad(ID)\V1_Y+Quad(Id)\Height
Quad(ID)\V2_Z=Quad(ID)\V1_Z

Quad(ID)\V3_X=Quad(Id)\V1_X+Quad(Id)\Width
Quad(ID)\V3_Y=Quad(ID)\V1_Y+Quad(Id)\Height
Quad(ID)\V3_Z=Quad(ID)\V1_Z

Quad(ID)\V4_X=Quad(Id)\V1_X+Quad(Id)\Width
Quad(ID)\V4_Y=Quad(ID)\V1_Y
Quad(ID)\V4_Z=Quad(ID)\V1_Z


End Function

Function LiftQuad(ID,Y#,Terrain)
;This is used when a quad which has conformed to the terrain is to be lifted off the terrain...
;Y# is relative to the terrain here,...
MoveQuad(ID,0,0,Terrain)
Offset#=Y#
ConformQuadToTerrain(ID,Terrain,Offset#)

End Function


Function PointQuadToCamera(ID,CameraYAngle,CamX#,CamZ#,Mode)
If Mode=1 Then MyYAngle=(CameraYAngle) 
If Mode=2 Then MyYAngle=ATan2(Quad(ID)\V1_Z-CamZ,Quad(ID)\V1_X-CamX)-90
MyYAngle=MyYAngle Mod 360
If MyYAngle<0 Then MyYAngle=MyYAngle+360
If myYangle>360 Then MyYangle=MyYangle-360
Quad(ID)\V3_X=Quad(ID)\V1_X+Quad(ID)\Width*Cosine#(MyYAngle)
Quad(ID)\V3_Z=Quad(ID)\V1_Z+Quad(ID)\Width*Sine#(MyYAngle)

Quad(ID)\V4_X=Quad(ID)\V1_X+Quad(ID)\Width*Cosine#(MyYAngle)
Quad(ID)\V4_Z=Quad(ID)\V1_Z+Quad(ID)\Width*Sine#(MyYAngle)
Quad(Id)\Angle=MyYAngle
End Function


Function PointAndCentre(ID,CameraYAngle)
MyYAngle=(CameraYAngle) 
MyYAngle=MyYAngle Mod 360
If MyYAngle<0 Then MyYAngle=MyYAngle+360
If myYangle>360 Then MyYangle=MyYangle-360
Quad(Id)\Angle=MyYAngle
TotalX#=(Quad(ID)\V1_X+Quad(ID)\V4_X)*0.5
TotalZ#=(Quad(ID)\V1_z+Quad(ID)\V4_z)*0.5
TotalY#=(Quad(ID)\V1_y+Quad(ID)\V2_y)*0.5

Quad(ID)\V1_X#=TotalX#-(Quad(Id)\Width*0.5)*Cosine#(Quad(ID)\Angle)
Quad(ID)\V2_X#=TotalX#-(Quad(Id)\Width*0.5)*Cosine#(Quad(ID)\Angle)
Quad(ID)\V3_X#=TotalX#+(Quad(Id)\Width*0.5)*Cosine#(Quad(ID)\Angle)
Quad(ID)\V4_X#=TotalX#+(Quad(Id)\Width*0.5)*Cosine#(Quad(ID)\Angle)

Quad(ID)\V1_Z#=TotalZ#-(Quad(Id)\Width*0.5)*Sine#(Quad(ID)\Angle)
Quad(ID)\V2_Z#=TotalZ#-(Quad(Id)\Width*0.5)*Sine#(Quad(ID)\Angle)
Quad(ID)\V3_Z#=TotalZ#+(Quad(Id)\Width*0.5)*Sine#(Quad(ID)\Angle)
Quad(ID)\V4_Z#=TotalZ#+(Quad(Id)\Width*0.5)*Sine#(Quad(ID)\Angle)

Quad(id)\V1_Y#=TotalY#-(Quad(Id)\Height*0.5)
Quad(id)\V2_y#=TotalY#+(Quad(Id)\Height*0.5)
Quad(id)\V3_Y#=TotalY#+(Quad(Id)\Height*0.5)
Quad(id)\V4_Y#=TotalY#-(Quad(Id)\Height*0.5)



End Function


Function PositionQuad(ID)

VertexCoords(Quad(ID)\SurfaceID,Quad(ID)\Index1,Quad(ID)\V1_X,Quad(ID)\V1_Y,Quad(ID)\V1_Z)
VertexCoords(Quad(ID)\SurfaceID,Quad(ID)\Index2,Quad(ID)\V2_X,Quad(ID)\V2_Y,Quad(ID)\V2_Z)
VertexCoords(Quad(ID)\SurfaceID,Quad(ID)\Index3,Quad(ID)\V3_X,Quad(ID)\V3_Y,Quad(ID)\V3_Z)
VertexCoords(Quad(ID)\SurfaceID,Quad(ID)\Index4,Quad(ID)\V4_X,Quad(ID)\V4_Y,Quad(ID)\V4_Z)

End Function
Function PositionQuadCentred(id)

TotalX#=(Quad(ID)\V1_X+Quad(ID)\V4_X)*0.5
TotalZ#=(Quad(ID)\V1_z+Quad(ID)\V4_z)*0.5
TotalY#=(Quad(ID)\V1_Y+Quad(ID)\V2_Y)*0.5

V1_X#=TotalX#-(Quad(Id)\Width*0.5)*Cosine#(Quad(ID)\Angle)
V2_X#=TotalX#-(Quad(Id)\Width*0.5)*Cosine#(Quad(ID)\Angle)
V3_X#=TotalX#+(Quad(Id)\Width*0.5)*Cosine#(Quad(ID)\Angle)
V4_X#=TotalX#+(Quad(Id)\Width*0.5)*Cosine#(Quad(ID)\Angle)

V1_Y#=TotalY#-(Quad(Id)\Height*0.5)
V2_y#=TotalY#+(Quad(Id)\Height*0.5)
V3_Y#=TotalY#+(Quad(Id)\Height*0.5)
V4_Y#=TotalY#-(Quad(Id)\Height*0.5)

V1_Z#=TotalZ#-(Quad(Id)\Width*0.5)*Sine#(Quad(ID)\Angle)
V2_Z#=TotalZ#-(Quad(Id)\Width*0.5)*Sine#(Quad(ID)\Angle)
V3_Z#=TotalZ#+(Quad(Id)\Width*0.5)*Sine#(Quad(ID)\Angle)
V4_Z#=TotalZ#+(Quad(Id)\Width*0.5)*Sine#(Quad(ID)\Angle)



VertexCoords(Quad(ID)\SurfaceID,Quad(ID)\Index1,V1_X,V1_Y,V1_Z)
VertexCoords(Quad(ID)\SurfaceID,Quad(ID)\Index2,V2_X,V2_Y,V2_Z)
VertexCoords(Quad(ID)\SurfaceID,Quad(ID)\Index3,V3_X,V3_Y,V3_Z)
VertexCoords(Quad(ID)\SurfaceID,Quad(ID)\Index4,V4_X,V4_Y,V4_Z)

End Function


Function ChangeQuadTexture(ID,LeftU#,UpperV#,RightU#,LowerV#)

Quad(id)\V1_U=LeftU
Quad(id)\V1_V=LowerV
Quad(id)\V2_U=LeftU
Quad(id)\V2_V=UpperV
Quad(id)\V3_U=RightU
Quad(id)\V3_V=UpperV
Quad(id)\V4_U=RightU
Quad(id)\V4_V=LowerV

VertexTexCoords(Quad(ID)\SurfaceID,Quad(ID)\Index1,LeftU#,LowerV#)
VertexTexCoords(Quad(ID)\SurfaceID,Quad(ID)\Index2,LeftU#,UpperV#)
VertexTexCoords(Quad(ID)\SurfaceID,Quad(ID)\Index3,RightU#,UpperV#)
VertexTexCoords(Quad(ID)\SurfaceID,Quad(ID)\Index4,RightU#,LowerV#)


End Function

Function ConformQuadToTerrain(ID,Terrain,Offset#)
Quad(ID)\V2_Z=Quad(ID)\V1_Z+Quad(ID)\Height
Quad(ID)\V3_Z=Quad(ID)\V1_Z+Quad(ID)\Height
Quad(Id)\V4_Z=Quad(Id)\V1_Z
Quad(ID)\V1_Y=0
Quad(Id)\V2_Y=0
Quad(ID)\V3_Y=0
Quad(Id)\V4_Y=0
Quad(ID)\V2_X=Quad(iD)\V1_X
Quad(Id)\V3_X=Quad(ID)\V1_X+Quad(Id)\Width
Quad(ID)\V4_X=Quad(ID)\V1_X+Quad(Id)\Width
If Terrain<>0 Then
Quad(ID)\V1_Y=TerrainY(Terrain,Quad(Id)\V1_X,Quad(Id)\V1_Y,Quad(Id)\V1_Z)+Offset
Quad(ID)\V2_Y=TerrainY(Terrain,Quad(Id)\V2_X,Quad(Id)\V2_Y,Quad(Id)\V2_Z)+Offset
Quad(ID)\V3_Y=TerrainY(Terrain,Quad(ID)\V3_X,Quad(Id)\V3_Y,Quad(Id)\V3_Z)+Offset
Quad(ID)\V4_Y=TerrainY(Terrain,Quad(Id)\V4_X,Quad(Id)\V4_Y,Quad(Id)\V4_Z)+Offset

EndIf 

End Function

Function DoesQuadHaveSurface(ID)

If Quad(Id)\SurfaceID<>0 Then Return Quad(Id)\SurfaceID Else Return 0
End Function

Function RemoveQuadSlow(ID)
;This will need some work....

;currently not implemented, it does not do anything yet...
Quad(id)\Blank=1
For i=0 To MaxQuad
If quad(id)\Blank=0 Then 


EndIf
Next

End Function

Function RemoveQuadCheat(ID)

ChangeQuadTexture(ID,0,0,0,0)
Quad(id)\Width=0
Quad(id)\Height=0
MoveQuad(id,0,0,0)
PositionQuad(id)
Quad(Id)\Blank=1
For MyQuad.SurfacedQuad=Each surfacedQuad
If MyQuad\QuadID=ID Then 
MyQuad\Blank=1
Exit
EndIf
Next


End Function
Function QuadIsBlank(Id)

If Quad(Id)\Blank=1 Then Return 1 Else Return 0


End Function
Function ScaleQuad(ID,XScale#,YScale#)
Quad(iD)\Width=Quad(id)\Width*XScale
Quad(Id)\Height=quad(id)\height*YScale
Quad(id)\V2_Y=Quad(id)\V1_Y+Quad(id)\Height
Quad(id)\V3_Y=Quad(iD)\v1_y+Quad(id)\Height
End Function
Function ResetQuad(ID,Width#,Height#)
Quad(iD)\Width=Width#
Quad(Id)\Height=Height#
ChangeQuadTexture(ID,0,0,1,1)
Quad(Id)\Blank=0
For MyQuad.SurfacedQuad = Each SurfacedQuad
If MyQuad\QuadID=ID Then MyQuad\Blank=0:Exit 

Next
End Function

Function CreateQuad(ID,Surface,Width#,Height#)

Quad(id)\Blank=0
Quad(ID)\SurfaceID=Surface
Quad(ID)\Width=Width
Quad(ID)\Height=Height
Quad(ID)\Angle=0

Quad(ID)\Index1=AddVertex(Surface,0,0,0,0,1)
Quad(ID)\V1_X=-Width*0.5
Quad(ID)\V1_Y=-Height*0.5
Quad(ID)\V1_Z=0
Quad(ID)\V1_U=0
Quad(ID)\V1_V=1

Quad(ID)\Index2=AddVertex(Surface,0,Height,0,0,0)
Quad(ID)\V2_X=-Width*0.5
Quad(ID)\V2_Y=Height*0.5
Quad(ID)\V2_Z=0
Quad(ID)\V2_U=0
Quad(ID)\V2_V=0

Quad(ID)\Index3=AddVertex(Surface,Width,Height,0,1,0)
Quad(ID)\V3_X=Width*0.5
Quad(ID)\V3_Y=Height*0.5
Quad(ID)\V3_Z=0
Quad(ID)\V3_U=1
Quad(ID)\V3_V=0

Quad(ID)\Index4=AddVertex(Surface,Width,0,0,1,1)
Quad(ID)\V4_X=Width*0.5
Quad(ID)\V4_Y=-Height*0.5
Quad(ID)\V4_Z=0
Quad(ID)\V4_U=1
Quad(ID)\V4_V=1

AddTriangle(Surface,Quad(ID)\Index1,Quad(ID)\Index2,Quad(ID)\Index3)
AddTriangle(Surface,Quad(ID)\Index1,Quad(ID)\Index3,Quad(ID)\Index4)


For QuadList.EmptyQuadList = Each EmptyQuadList
If ID=QuadList\QuadID Then Delete QuadList:Exit 
Next

NewQuad.SurfacedQuad = New SurfacedQuad
NewQuad\QuadID=ID
NewQuad\SurfaceID=Surface
NewQuad\Blank=0



End Function

Function NextQuad()
For QuadList.EmptyQuadList=Each EmptyQuadList
Return QuadList\QuadID
Next
Return -1
;For i=0 To MaxQuad
;If Quad(i)\SurfaceID=0 Then Return i
;Next
Return -1
End Function

Function NextQuadWithMySurface(Surface)

For MyQuad.SurfacedQuad=Each SurfacedQuad
If MyQuad\SurfaceID=Surface And MyQuad\Blank=1 Then Return MyQuad\QuadID
Next
Return -1
End Function


Function CountUsedQuads()
count=0
For i=0 To MaxQuad
If Quad(i)\SurfaceID<>0 Then count=count+1
Next
Return count
End Function


Function RemoveUnusedSurface(Surface)
Remove=1

For MyQuad.SurfacedQuad =Each SurfacedQuad
If MYQuad\SurfaceID=Surface And MyQuad\Blank=0 Then Remove=0:Exit 
Next


;For j=0 To MaxQuad
;If Quad(j)\SurfaceID=Surface And Quad(j)\Blank=0 Then Remove=0:Exit
;Next
If Remove=1 Then 

;this should be empty, no reason to call this function any more...
For Range.QuadRangeObj=Each QuadRangeObj
If Range\SurfaceID=Surface Then Delete Range
Next


For MyQuad.SurfacedQuad= Each SurfacedQuad
If MyQuad\SurfaceID=Surface Then
Quad(MyQuad\QuadID)\SurfaceID=0
Quad(MyQuad\QuadID)\Blank=1
QuadList.EmptyQuadList=New EmptyQuadList
QuadList\QuadID=MYQuad\QuadID
 Delete MyQuad
EndIf 
Next
ClearSurface Surface
;For j=0 To MaxQuad
;If Quad(j)\SurfaceID=Surface Then 
;Quad(j)\SurfaceID=0
;Quad(j)\Blank=1
;QuadList.EmptyQuadList=New EmptyQuadList
;Quadlist\QuadID=j
;EndIf 
;Next
EndIf 

End Function


Function AnimateQuad(QuadID,SpriteWidth,SpriteHeight,TexWidth,TexHeight,Frame)
;If TexWidth<=0 Or TexHeight<=0 Or Spritewidth<=0 Or SpriteHeight<=0 Or QuadID<0 Or QuadID>MaxQuad Then Return -1

UInc#=Float(SpriteWidth)/TexWidth
VInc#=Float(SpriteHeight)/TexHeight
XWidth=TexWidth/SpriteWidth
YHeight=TexHeight/SpriteHeight

Column = (Frame Mod XWidth)
Row = 1+(Frame-Column)/YHeight
If Column=0 Then Column=XWidth
;If Column<1 Or Column>XWidth Or Row<1 Or Row>YHeight Then Return -1
If Row>YHeight Then Return -1
;otherwise go ahead and change the texture of this sprite...
ChangeQuadTexture(QuadID,(Column-1)*UInc#,(Row-1)*VInc#,Column*UInc#,Row*VInc#)


End Function

Function ResizeQuad(QuadId,Width#,Height#)

Quad(QuadID)\Width=Width
Quad(QuadID)\Height=Height
Quad(QuadId)\V2_Y=Quad(quadiD)\v1_y+Height
Quad(QuadId)\V3_Y=Quad(quadiD)\v1_y+Height

End Function



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;						THESE FUNCTIONS BELOW ARE OBSOLETE.  THEY ARE NO LONGER NECESSARY
;						THE PROGRAM FUNCTIONS BETTER AND FASTER WITH THE ABOVE FUNCTIONS
;						
;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

Function MyLowerRange(RangeName$)
For Range.QuadRangeObj=Each QuadRangeObj
If Range\EffectName$=RangeName$ Then 
Return Range\LowerQuad
EndIf
Next
Return -1
End Function

Function MyUpperRange(RangeName$)
For Range.QuadRangeObj=Each QuadRangeObj
If Range\EffectName$=RangeName$ Then 
Return Range\UpperQuad
EndIf
Next
Return -1

End Function

;;;;;;;;;;;; THESE FUNCTIONS NO LONGER REQUIRED

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

Function UnAssignQuadRange(RangeName$)

For Range.QuadRangeObj=Each QuadRangeObj
If Range\EffectName$=RangeName$ Then 
For i=Range\LowerQuad To Range\UpperQuad

If DoesQuadHaveSurface(i)<>0 Then RemoveQuadCheat(i)
Next
Range\InUse=0

Exit
EndIf
Next
End Function



Function AssignQuadRange$(Amount,RangeName$,Surface)

If Amount=0 Or RangeName$="" Then Return 0

For Range.QuadRangeObj=Each QuadRangeObj
	If Range\EffectName$=RangeName$ Then 
		Return 0
		Exit
	EndIf

Next

LowerVal=-1
UpperVal=0


For j=0 To MaxQuad
Invalid=0

For Range.QuadRangeObj=Each QuadRangeObj

If (j>=Range\LowerQuad And j<=Range\upperQuad ) Then 

If Range\SurfaceID<>Surface Or (Range\SurfaceID=Surface And Range\InUse=1) Then Invalid=1
EndIf

;If (j>=Range\LowerQuad And j<=Range\UpperQuad And Quad(j)\SurfaceID=Range\SurfaceID And Range\InUse=0) Then Invalid=0
Next
If Invalid=0 And LowerVal=-1 Then LowerVal=j
UpperVal=j
If Invalid=1 Then LowerVal=-1
If LowerVal<>-1 And UpperVal-LowerVal=Amount Then 
	NewRange.QuadRangeObj=New QuadRangeObj
	NewRange\LowerQuad=LowerVal
	NewRange\UpperQuad=UpperVal
	NewRange\SurfaceID=Surface
	NewRange\InUse=1
	NewRange\EffectName$=RangeName$
	Return RangeName$

EndIf 
;	If Quad(j)\SurfaceID=0 Then
;		If LowerVal=-1 Then LowerVal=j
;		UpperVal=j
;
;	Else	
;		LowerVal=-1
;		UpperVal=j
;	EndIf
;
;	If LowerVal<>-1 And UpperVal-LowerVal=Amount Then 
;		NewRange.QuadRangeObj=New QuadRangeObj
;		NewRange\LowerQuad=LowerVal
;		NewRange\UpperQuad=UpperVal
;		NewRange\EffectName$=RangeName$
;		Return RangeName$
;	EndIf 
Next

End Function


;;;;;;;;;; THE ABOVE FUNCTIONS FOR QUAD RANGE ARE NO LONGER REQUIRED ;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
