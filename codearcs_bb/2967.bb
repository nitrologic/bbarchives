; ID: 2967
; Author: RemiD
; Date: 2012-08-02 05:38:17
; Title: deletes an entry in an array list + reorganize the array list
; Description: 0.15ms for a list of 100 entities

;This code shows you how to delete an entry from an array and then to reorganize the array list.
;Notice how the meshes are not deleted but rather their references are copied in the Swap_Array and then copied again in the Array

Graphics3D(640,480,32,2)  
 
SeedRnd(MilliSecs())  

Global Camera = CreateCamera()
CameraRange(Camera,0.1,1000)

Global CubicalXMesh = CreateCube()
ScaleMesh(CubicalXMesh,1.0/2,1.0/2,1.0/2)
HideEntity(CubicalXMesh)

Global CylindricalXMesh = CreateCylinder(8)
ScaleMesh(CylindricalXMesh,1.0/2,1.0/2,1.0/2)
HideEntity(CylindricalXMesh)

Global SphericalXMesh = CreateSphere(8)
ScaleMesh(SphericalXMesh,1.0/2,1.0/2,1.0/2)
HideEntity(SphericalXMesh)

Global PyramidalXMesh = CreateCone(4)
ScaleMesh(PyramidalXMesh,1.0/2,1.0/2,1.0/2)
HideEntity(PyramidalXMesh)

;Shapes :
Const CCubical% = 1
Const CCylindrical% = 2
Const CSpherical% = 3
Const CPyramidal% = 4

;Materials :
Const CTin% = 1
Const CCopper% = 2
Const CSilver% = 3
Const CGold% = 4

;Lists to store the properties and the components of the Things
Global ThingsCount% = 0
Dim ThingShape%(100)
Dim ThingMaterial%(100)
Dim ThingName$(100)
Dim ThingMesh(100)

;Lists to temporarily store the properties and the components of the Things
Global Swap_ThingsCount% = 0
Dim Swap_ThingShape%(100)
Dim Swap_ThingMaterial%(100)
Dim Swap_ThingName$(100)
Dim Swap_ThingMesh(100)

BuildThings()

PositionEntity(Camera,5,10,-5)
RotateEntity(Camera,45,0,0)

DLight = CreateLight(1)
LightColor(DLight,250,250,250)
PositionEntity(DLight,-100,100,-100)
RotateEntity(DLight,45,-45,0)

AmbientLight(050,050,050)

PositionEntity(Camera,50,50,-25)
RotateEntity(Camera,45,0,0)

Global RoutineTime%

While( KeyDown(1)<>1 )
 
 If(KeyHit(57)>0)
  If(ThingsCount > 0)
   Id% = Rand(1,ThingsCount)
   DeleteThing(Id)
   RoutineStart% = MilliSecs()
   ReorganizeThingsList()
   RoutineTime = MilliSecs() - RoutineStart
  EndIf
 EndIf
 
 UpdateThings()

 SetBuffer(BackBuffer())
 RenderWorld()

 Text(0,0,"Triangles = "+TrisRendered())
 Text(0,20,"ThingsCount = "+ThingsCount)
 Text(0,40,"RoutineTime = "+RoutineTime)
 Text(0,60,"Press Space to delete a random Thing in the list.")

 Flip(1)

Wend

End()

Function BuildThings()

 For i% = 1 To 100
 
  ThingsCount = ThingsCount + 1
  Id% = ThingsCount

  RandomChoice% = Rand(1,4)
  If(RandomChoice = 1)
   ThingShape(Id) = CCubical
  Else If(RandomChoice = 2)
   ThingShape(Id) = CCylindrical
  Else If(RandomChoice = 3)
   ThingShape(Id) = CSpherical
  Else If(RandomChoice = 4)
   ThingShape(Id) = CPyramidal
  EndIf
  
  If(ThingShape(Id) = CCubical)
   ThingMesh(Id) = CopyEntity(CubicalXMesh)
   ThingName(Id) = "Cube"
  ElseIf(ThingShape(Id) = CCylindrical)
   ThingMesh(Id) = CopyEntity(CylindricalXMesh)
   ThingName(Id) = "Cylinder"
  ElseIf(ThingShape(Id) = CSpherical)
   ThingMesh(Id) = CopyEntity(SphericalXMesh)
   ThingName(Id) = "Sphere"
  ElseIf(ThingShape(Id) = CPyramidal)
   ThingMesh(Id) = CopyEntity(PyramidalXMesh)
   ThingName(Id) = "Pyramid"
  EndIf

  RandomChoice% = Rand(1,4)
  If(RandomChoice = 1)
   ThingMaterial(Id) = CTin
   ThingName(Id) = ThingName(Id)+"Of"+"Tin"
  Else If(RandomChoice = 2)
   ThingMaterial(Id) = CCopper
   ThingName(Id) = ThingName(Id)+"Of"+"Copper"
  Else If(RandomChoice = 3)
   ThingMaterial(Id) = CSilver
   ThingName(Id) = ThingName(Id)+"Of"+"Silver"
  Else If(RandomChoice = 4)
   ThingMaterial(Id) = CGold
   ThingName(Id) = ThingName(Id)+"Of"+"Gold"
  EndIf

  If(ThingMaterial(Id) = CTin)
   EntityColor(ThingMesh(Id),175,175,175)
  ElseIf(ThingMaterial(Id) = CCopper)
   EntityColor(ThingMesh(Id),160,080,000)
  ElseIf(ThingMaterial(Id) = CSilver)
   EntityColor(ThingMesh(Id),125,125,125)
  ElseIf(ThingMaterial(Id) = CGold)
   EntityColor(ThingMesh(Id),175,165,000)
  EndIf

  PositionEntity(ThingMesh(Id),Rnd(0,100),Rnd(0,10),Rnd(0,100))
  RotateEntity(ThingMesh(Id),0,Rnd(-180,180),0)

  NameEntity(ThingMesh(Id),"THI"+Str(Id))

 Next

End Function

Function UpdateThings()

 For Id% = 1 To ThingsCount

  MoveEntity(ThingMesh(Id),0,0,0.1) 

  If( EntityX(ThingMesh(Id),True) <= 0 Or EntityX(ThingMesh(Id),True) >= 100 Or EntityZ(ThingMesh(Id),True) <= 0 Or EntityZ(ThingMesh(Id),True) >= 100 )
   RotateEntity(ThingMesh(Id),0,Rnd(-180,180),0)
  EndIf

 Next

End Function

Function DeleteThing(Id%)
 
 ThingShape(Id) = 0
 ThingMaterial(Id) = 0
 FreeEntity(ThingMesh(Id))
 ThingMesh(Id) = 0
 ThingName(Id) = ""

End Function

Function ReorganizeThingsList()

 ;Copy the remaining entries in the Thing array to the Swap_Thing array
 Swap_ThingsCount = 0
 For Id% = 1 To ThingsCount
  If(ThingMesh(Id) <> 0)
   Swap_ThingsCount = Swap_ThingsCount + 1
   SId% = Swap_ThingsCount
   Swap_ThingShape(SId) = ThingShape(Id)
   Swap_ThingMaterial(SId) = ThingMaterial(Id)
   Swap_ThingName(SId) = ThingName(Id)
   Swap_ThingMesh(SId) = ThingMesh(Id)
  EndIf
 Next
 
 ;Delete all entries in the Thing array
 For Id% = 1 To ThingsCount
  ThingShape(Id) = 0
  ThingMaterial(Id) = 0
  ThingName(Id) = ""
  ThingMesh(Id) = 0
 Next
 ThingsCount = 0

 ;Copy all entries in the Swap_Thing to the Thing array
 ThingsCount = 0
 For SId% = 1 To Swap_ThingsCount
  ThingsCount = ThingsCount + 1
  Id% = ThingsCount
  ThingShape(Id) = Swap_ThingShape(SId)
  ThingMaterial(Id) = Swap_ThingMaterial(SId)
  ThingName(Id) = Swap_ThingName(SId)
  ThingMesh(Id) = Swap_ThingMesh(SId)
 Next

 ;Delete all entries in the Swap_Thing array
 For SId% = 1 To Swap_ThingsCount
  Swap_ThingShape(SId) = 0
  Swap_ThingMaterial(SId) = 0
  Swap_ThingName(SId) = ""
  Swap_ThingMesh(SId) = 0
 Next
 Swap_ThingsCount = 0

 ;Attribute a new name to each entry (pickable/collidable) in the Thing array
 For Id% = 1 To ThingsCount
  NameEntity(ThingMesh(Id),"THI"+Str(Id))
 Next

End Function
