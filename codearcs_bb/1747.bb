; ID: 1747
; Author: namar777
; Date: 2006-07-06 11:31:44
; Title: NA_ODE
; Description: Mapping ODE with B3d Commands

;Title:NA_Ode(NassimAmar Ode) >>>>Ode 0.5b<<<<
;Description:Mapping Functions to B3d Standards
;
;
;Tutorial:
;	Creating Entities:
;				1. NaOde_CreateCylinder(name,space,enable) 
;				2. Cylinder=NaOde_CreateCylinder(name,space,enable)
;				 Naming Entities Differently is Most useful when you as a programmer
;				 would want to change the position or rotation of the latter.
;						-NaOde_CreateCylinder(name,space,enable)
;						-NaOde_CreateCylinder(name,space,enable)
;						-NAOde_PositionEntity(name,180,0,0)--> This Will Move Both objects Named "x"
;----------------------------------------------------------------------------------------------
;	Arrays:
;							Dim Cylinders(20)
;							For x=1 To 20
;							NaOde_CreateCylinder(name,space,enable)
;							NAOde_PositionEntity(name,Rnd(10),Rnd(10),Rnd(10))
;							Next
;								>>Put the x in the Name Place as NAOde. Doing So 
;								  will logically give the Various Entities Created
;								  different numbers, Hence allowing the use of specific
;								  functions. But since probably you'll be using numerous 
;								  arrays. Might as well as a string before the "x"
;										-NAOde_PositionEntity(name,rnd(10),rnd(10),rnd(10))
;

Const dParamLoStop 			= 0
Const dParamHiStop			= 1
Const dParamVel				= 2
Const dParamVel2			= 258
Const dParamFMax			= 3
Const dParamFMax2			= 259
Const dParamFudgeFactor		= 4
Const dParamBounce			= 5
Const dParamCFM				= 6
Const dParamStopERP			= 7
Const dParamStopCFM			= 8
Const dParamSuspensionERP	= 9
Const dParamSuspensionCFM	= 10

Const dContactMu2		= $001
Const dContactFDir1		= $002
Const dContactBounce	= $004
Const dContactSoftERP	= $008
Const dContactSoftCFM	= $010
Const dContactMotion1	= $020
Const dContactMotion2	= $040
Const dContactSlip1		= $080
Const dContactSlip2		= $100
Const dContactApprox0	= $0000
Const dContactApprox1_1	= $1000
Const dContactApprox1_2	= $2000
Const dContactApprox1	= $3000

Const NAOde_PrimitiveMeshes=1
Const NAOde_Meshes=2
Const NAOde_Joints=3

Const NAODE_Cube=1
Const NAODE_Sphere=2
Const NAODE_Cylinder=3

Const Hinge=1
Const Slider=2
Const Universal=3
Const Hinge2=4
Const Fixed=5
Const Motor=6
Const Ball=7
SeedRnd MilliSecs()

Type TODEGeom
	Field body%,joint%,typjoint
	Field geom%,tdata,enable,geo
	Field mesh%,mesh2%,name$,cnt,id,flag
End Type

Global space%=ODE_dWorldCreate(1)
;>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>My Functions<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
Function NAODE_Init(Gravity#=-1,Bounce#=0.2,BounceVelocity#=1,Friction#=0.6,Friction2#=0.6,EntitiesSoftness#=0,GroundSoftness#=0,Slip1#=0,Slip2#=0,Motion1#=0,Motion2#=0)
ODE_dWorldSetGravity(0, Gravity#, 0)
ODE_dSetBOUNCE(Bounce#)
ODE_dSetBOUNCE_VEL(BounceVelocity#)
ODE_dSetMU(Friction#); Friction
ODE_dSetMU2(Friction2#); Friction
ODE_dSetSOFT_ERP(EntitiesSoftness#)
ODE_dSetSOFT_CFM(GroundSoftness#)
ODE_dSetSLIP1(Slip1#)
ODE_dSetSLIP1(Slip2#)
ODE_dSetMOTION1(Motion1#)
ODE_dSetMOTION2(Motion2#)
End Function 

Function NAODE_Features(all)
ODE_dSetContactMode(all)
End Function 

Function NAODE_CountEntities(id)
num=0
For g.TodeGeom=Each TodeGeom
If g\id=id Then num=num+1
Return num
Next
End Function 

Function NAODE_CreateCylinder(name$,world,gravmode=0)
g.TOdeGeom=New TOdeGeom
g\name$=Upper(name$)
g\id=NAOde_PrimitiveMeshes
g\cnt=NAOde_CountEntities(g\id)
g\enable=1
g\geo=NAODE_Cylinder
g\body=ODE_dBodyCreate()
g\geom=ODE_dCreateCCylinder(world,radius#,height#,mass#)
ODE_dGeomSetBody(g\geom,g\body)
g\mesh=CreateCylinder()
ScaleEntity g\mesh,1,1,1
ODE_dBodySetPosition g\body,0,0,0
ODE_dBodySetRotation g\body,0,0,0
ODE_dBodySetGravityMode(g\body,gravmode) 
Return g\mesh
End Function 

Function NAODE_CreateCube(name$,world,gravmode=0)
g.TOdeGeom=New TOdeGeom
g\name$=Upper(name$)
g\id=NAOde_PrimitiveMeshes
g\cnt=NAOde_CountEntities(g\id)
g\geo=NAODE_Cube
g\mesh=CreateCube()
g\enable=1
ScaleEntity g\mesh,1,1,1
g\body=ODE_dBodyCreate()
g\geom=ODE_dCreateBox(world,MeshWidth(g\mesh),MeshHeight(g\mesh),MeshDepth(g\mesh),mass#)
ODE_dGeomSetBody(g\geom,g\body)
ODE_dBodySetPosition g\body,0,0,0
ODE_dBodySetRotation g\body,0,0,0
ODE_dBodySetGravityMode(g\body,gravmode) 
Return g\mesh
End Function 
 
Function NAODE_CreateSphere(name$,world,gravmode=0)
g.TOdeGeom=New TOdeGeom
g\name$=Upper(name$)
g\id=NAOde_PrimitiveMeshes
g\cnt=NAOde_CountEntities(g\id)
g\geo=NAODE_Sphere
g\body=ODE_dBodyCreate()
g\enable=1
g\geom=ODE_dCreateSphere%(world, radius#, mass#)
ODE_dGeomSetBody(g\geom,g\body)
g\mesh=CreateSphere()
ScaleEntity g\mesh,1,1,1
ODE_dBodySetPosition g\body,0,0,0
ODE_dBodySetRotation g\body,0,0,0
ODE_dBodySetGravityMode(g\body,gravmode) 
Return g\mesh
End Function 


Function NAODE_SetMeshToBody(name$,meshpath$)
For g.TodeGeom=Each TodeGeom
If g\name$=Upper(name$) And g\id=NAODE_PrimitiveMeshes
g\mesh=LoadMesh(meshpath$)
Return g\mesh
EndIf 
Next
End Function 

Function NAODE_LoadMesh%(path$,name$,x#=0,y#=0,z#=0,scale#=1) ;rx#=0,ry#=0,rz#=0 On A later Version
g.TODEGeom=New TODEGeom
	g\tdata=t_data
	g\name$=Upper(name$)
	g\id=NAODE_Meshes
	g\cnt=NAODE_CountEntities(g\id)
	g\mesh=LoadMesh(path$)
	mesh%=CopyMesh(g\mesh)
	tris_count% = 0
	vert_count% = 0
	For i = 1 To CountSurfaces(mesh)
		tris_count = tris_count + CountTriangles(GetSurface(mesh, i))
		vert_count = vert_count + CountVertices(GetSurface(mesh, i))
	Next
	DebugLog "Tris count: " + tris_count
	DebugLog "Vertices count: " + vert_count
		DebugLog "surface count: "+CountSurfaces(mesh)
	vertices_bank% = CreateBank(vert_count * 4 * 4)
	indices_bank% = CreateBank(tris_count * 3 * 4)

	offset% = 0
	offset_tris% = 0
	baseverts=0
	For i = 1 To CountSurfaces(mesh)
		sf = GetSurface(mesh, i)
		For v = 0 To CountVertices(sf) - 1
			PokeFloat vertices_bank, offset, VertexX(sf, v)*(scale#)+x#
			offset = offset + 4
			PokeFloat vertices_bank, offset, VertexY(sf, v)*(scale#)+y#
			offset = offset + 4
			PokeFloat vertices_bank, offset, VertexZ(sf, v)*(scale#)+z#
			offset = offset + 8
		Next
		For t = 0 To CountTriangles(sf) - 1
			For j = 0 To 2
				v = TriangleVertex(sf, t, j)+baseverts
				PokeInt indices_bank, offset_tris, v
				offset_tris = offset_tris + 4
			Next
		Next
		baseverts=baseverts+CountVertices(sf)
	Next
	FreeEntity mesh
	PositionEntity g\mesh,x#,y#,z#
	RotateEntity g\mesh,rx#,ry#,rz#
	ScaleEntity g\mesh,scale#,scale#,scale#
	t_data = ODE_dGeomTriMeshDataCreate()
	ODE_dGeomTriMeshDataBuildSimple(t_data, vertices_bank, vert_count, indices_bank, tris_count * 3)
 	ODE_dCreateTriMesh(space, t_data)
	Return g\mesh
End Function

Function NAODE_PositionEntity(name$,x#,y#,z#)
For g.TOdeGeom=Each TOdeGeom
If g\name$=Upper(name$) ODE_dBodySetPosition g\body,x#,y#,z#
Next
End Function 

Function NAODE_RotateEntity(name$,rx#,ry#,rz#)
For g.TOdeGeom=Each TOdeGeom
If g\name$=Upper(name$) ODE_dBodySetRotation g\body,rx#,ry#,rz#
Next
End Function 

Function NAODE_ScaleEntity(name$,world,x#,y#,z#,mass#=1)
For g.TOdeGeom=Each TOdeGeom
If g\name$=Upper(name$) 
	If g\geo=NAODE_Cube 
		ODE_dGeomDestroy(g\geom)
		g\geom=ODE_dCreateBox(world,x#,y#,z#,mass#)
		ODE_dGeomSetBody(g\geom,g\body)
		ScaleEntity g\mesh,x#,y#,z#
	EndIf 
	If g\geo=NAODE_Sphere
		ODE_dGeomDestroy(g\geom)
		g\geom=ODE_dCreateSphere(world, x#, mass#)
		ODE_dGeomSetBody(g\geom,g\body)
		ScaleEntity g\mesh,x#,x#,x#
	EndIf 
	If g\geo=NAODE_Cylinder
		ODE_dGeomDestroy(g\geom)
		g\geom=ODE_dCreateCCylinder(world,x#,y#,mass#)
		ODE_dGeomSetBody(g\geom,g\body)
		ScaleEntity g\mesh,x#,y#,x#
	EndIf 	
EndIf
Next
End Function 

Function NAODE_PushEntity(name$,x#,y#,z#)
For g.TOdeGeom=Each TOdeGeom
If g\name$=Upper(name$) ODE_dBodyAddForce g\body,x#,y#,z#
Next
End Function 

Function NAODE_TorqueEntity(name$,x#,y#,z#)
For g.TodeGeom=Each Todegeom
If g\name$=Upper(name$) ODE_dBodyAddTorque g\body,x#,y#,z#
Next
End Function 

Function NAODE_TranslateEntity(name$,x#,y#,z#)
For g.TOdeGeom=Each TOdeGeom
If g\name$=Upper(name$) ODE_dBodyTranslateMass(g\body,x#,y#,z#)
Next
End Function 

Function NAODE_EntityX(name$) 
For g.TodeGeom=Each Todegeom
If g\name$=Upper(name$) Return ODE_dBodyGetPositionX#(g\body)
Next
End Function

Function NAODE_EntityY(name$) 
For g.TodeGeom=Each Todegeom
If g\name$=Upper(name$) Return ODE_dBodyGetPositionY#(g\body)
Next
End Function

Function NAODE_EntityZ(name$) 
For g.TodeGeom=Each Todegeom
If g\name$=Upper(name$) Return ODE_dBodyGetPositionZ#(g\body)
Next
End Function

Function NAODE_EntityPitch(name$) 
For g.TodeGeom=Each Todegeom
If g\name$=Upper(name$) Return ODE_dBodyGetPitch#(g\body)
Next
End Function

Function NAODE_EntityYaw(name$) 
For g.TodeGeom=Each Todegeom
If g\name$=Upper(name$) Return ODE_dBodyGetYaw#(g\body)
Next
End Function

Function NAODE_EntityRoll(name$) 
For g.TodeGeom=Each Todegeom
If g\name$=Upper(name$) Return ODE_dBodyGetRoll#(g\body)
Next
End Function

Function NAODE_EnableEntity(name$) 
For g.TodeGeom=Each Todegeom
If g\name$=Upper(name$) body=g\body
g\enable=1
Next
ODE_dBodyEnable(body%)
End Function

Function NAODE_DisableEntity(name$) 
For g.TodeGeom=Each Todegeom
If g\name$=Upper(name$) body=g\body
g\enable=0
Next
ODE_dBodyDisable(body%)
End Function

Function NAODE_FreeEntity(name$) 
For g.TodeGeom=Each Todegeom
If g\name$=Upper(name$)
body=g\body
FreeEntity g\mesh
Delete g
EndIf
Next
ODE_dBodyDestroy(body%)
End Function 

Function NAODE_EntityMass(name$,mass#)
For g.TodeGeom=Each Todegeom
If g\name$=Upper(name$) ODE_dBodySetMass(g\body,mass#)
Next
End Function 

Function NAODE_Gravity(x#,y#,z#)
ODE_dWorldSetGravity(x#,y#,z#)
End Function 

Function NAODE_CountCollisions(name$) 
For g.TodeGeom=Each Todegeom
If g\name$=Upper(name$) Return ODE_dBodyGetCollisionsCount%(g\body)
Next
End Function

Function NAODE_CollisionX(name$) 
For g.TodeGeom=Each Todegeom
If g\name$=Upper(name$) Return ODE_dBodyGetCollisionX#(g\body, index%)
Next

End Function

Function NAODE_CollisionY(name$,index) 
For g.TodeGeom=Each Todegeom
If g\name$=Upper(name$) Return ODE_dBodyGetCollisionY#(g\body, index%)
Next

End Function

Function NAODE_CollisionZ(name$,index) 
For g.TodeGeom=Each Todegeom
If g\name$=Upper(name$) Return ODE_dBodyGetCollisionZ#(g\body, index%)
Next
End Function

Function NAODE_CollisionNX(name$,index) 
For g.TodeGeom=Each Todegeom
If g\name$=Upper(name$) Return ODE_dBodyGetCollisionNX#(g\body, index%)
Next
End Function

Function NAODE_CollisionNY(name$,index) 
For g.TodeGeom=Each Todegeom
If g\name$=Upper(name$) Return ODE_dBodyGetCollisionNY#(g\body,index%)
Next
End Function

Function NAODE_CollisionNZ(name$,index) 
For g.TodeGeom=Each Todegeom
If g\name$=Upper(name$) Return ODE_dBodyGetCollisionNZ#(g\body,index%)
Next
End Function


Function NAODE_EntityCollided(name$,index) 
For g.TodeGeom=Each Todegeom
If g\name$=Upper(name$) body=g\body 
If g\body=ODE_dBodyGetCollisionBody%(body%, index%)
Return g\name$
EndIf 
Next
End Function

Function NAODE_EntityAngularVelX(name$) 
For g.TodeGeom=Each Todegeom
If g\name$=Upper(name$) 
Return ODE_dBodyGetAngularVelX(g\body)
End If
Next
End Function

Function NAODE_EntityAngularVelY(name$) 
For g.TodeGeom=Each Todegeom
If g\name$=Upper(name$) 
Return ODE_dBodyGetAngularVelY(g\body)
End If
Next
End Function

Function NAODE_EntityAngularVelZ(name$) 
For g.TodeGeom=Each Todegeom
If g\name$=Upper(name$) 
Return ODE_dBodyGetAngularVelZ(g\body)
End If
Next
End Function

Function NAODE_EntityAngularVel(name$,ax#,ay#,az#) 
For g.TodeGeom=Each Todegeom
If g\name$=Upper(name$) 
	ODE_dBodySetAngularVel(g\body, ax#, ay#, az#)
End If
Next
End Function


Function NAODE_CreateJoint(name$,SelectWhichOne$)
g.TodeGeom=New TodeGeom
g\name$=Upper(name$)
g\id=NAODE_Joints
g\cnt=NAODE_CountEntities(g\id)
Select Upper(SelectWhichOne$)
		Case "BALL" 
			g\joint=ODE_dJointCreateBall()
			g\typjoint=Ball
		Case "HINGE" 
			g\joint=ODE_dJointCreateHinge()
			g\typjoint=Hinge
		Case "SLIDER" 
			g\joint=ODE_dJointCreateSlider()
			g\typjoint=Slider
		Case "UNIVERSAL" 
			g\joint=ODE_dJointCreateUniversal()	
			g\typjoint=Universal
		Case "HINGE2"
			g\joint=ODE_dJointCreateHinge2()
			g\typjoint=Hinge2
		Case "FIXED"
			g\joint=ODE_dJointCreateFixed()	
			g\typjoint=Fixed
		Case "MOTOR"
			g\joint=ODE_dJointCreateAMotor()
			g\typjoint=Motor
		Default 
			g\joint=ODE_dJointCreateHinge()	
			g\typjoint=Hinge
End Select
End Function



Function NAODE_AttachJoint(joint$,name1$,name2$) 
For g.TodeGeom=Each Todegeom
If g\name$=Upper(name2$) body2=g\body
If g\name$=Upper(joint$) joint=g\joint
If g\name$=Upper(name1$) body1=g\body
Next
ODE_dJointAttach(joint,body1,body2)
End Function 

Function NAODE_DetachJoint(joint$) 
For g.TodeGeom=Each Todegeom
If g\name$=Upper(joint$) joint=g\joint
Next
ODE_dJointDestroy(joint)
End Function

Function NAODE_DeleteVolume(name$) 
For g.TodeGeom=Each Todegeom
If g\name$=Upper(name$) geom=g\geom
Next
ODE_dSpaceRemove(space%,geom)
End Function
		
Function NAODE_PositionJointAxises(jointname$,axis1x#,axis1y#,axis1z#,axis2x#,axis2y#,axis2z#)
For g.TodeGeom=Each TodeGeom
If g\name$=Upper(jointname$) And g\id=NAODE_Joints
	If g\typjoint=Hinge2
		ODE_dJointSetHinge2Axis1 (g\joint,axis1x#,axis1y#,axis1z#)
		ODE_dJointSetHinge2Axis2 (g\joint,axis2x#,axis2y#,axis2z#)
	EndIf 
	If g\typjoint=Hinge
		ODE_dJointSetHingeAxis(g\joint,axis1x#,axis1y#,axis1z#) 
	EndIf 
EndIf 
Next
End Function 

Function NAODE_SetJointParameter(name$,Parameter,Suspension#=.3)
For g.TodeGeom=Each TodeGeom
If g\name$=Upper(jointname$) And g\id=NAODE_Joints
ODE_dJointSetHinge2Param(g\joint, Parameter,Suspension#)
EndIf 
Next
End Function 

Function NAODE_JointHinge2Angle1(name$)
For g.TodeGeom=Each TodeGeom
If g\name$=Upper(jointname$) And g\id=NAODE_Joints
Return ODE_dJointGetHinge2Angle1(g\joint)
EndIf 
Next
End Function 

Function NAODE_CreateSimpleSpace(space)
Return ODE_dSimpleSpaceCreate(space)
End Function

Function NAODE_AddSpace(space,name$) 
For g.TodeGeom=Each Todegeom
If g\name$=Upper(name$) ODE_dSpaceAdd(space%,g\geom)
Next
End Function




Function NAODE_PositionJoint(name$,x#,y#,z#)
For g.TodeGeom=Each TodeGeom
If g\name$=Upper(jointname$) And g\id=NAODE_Joints
ODE_dJointSetHinge2Anchor(g\joint,x#,y#,z#)
EndIf 
Next
End Function 

Function NAODE_UpdateWorld(TypeofUpdate=1)				;Modified the Original One Just to Keep Track of Each Entity
	For g.TODEGeom = Each TODEGeom
	If g\id=NAOde_PrimitiveMeshes
	 For cnt=1 To NAOde_CountEntities(g\id)
	  If g\cnt=cnt	
		If g\enable=1
		ODE_dBodyEnable g\body
	    PositionEntity g\mesh, ODE_dGeomGetPositionX(g\geom), ODE_dGeomGetPositionY(g\geom), ODE_dGeomGetPositionZ(g\geom)
		RotateEntity g\mesh, ODE_dGeomGetPitch(g\geom), ODE_dGeomGetYaw(g\geom), ODE_dGeomGetRoll(g\geom), 1
	  EndIf 
	EndIf 	
	 Next
	EndIf
	Next
	Select TypeofUpdate
		Case 2	
			ODE_dWorldStep(0.1)
		Default
			ODE_dWorldQuickStep(0.1)
	End Select		
End Function
