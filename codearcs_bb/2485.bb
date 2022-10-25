; ID: 2485
; Author: Yasha
; Date: 2009-05-23 08:15:28
; Title: Create bones in code
; Description: Add mesh-deforming bones to a static mesh without using LoadAnimMesh

Type BoneEnt		;The type for manually boned entities
	Field entity
	Field bones
	Field bone_tforms,bone_tforms_orig[2]
	Field objectlist,objectlist_orig[2]
	Field matrix101;,matrix101_orig[2]		;I DON'T KNOW what this actually does. Added in Blitz3D v1.101, comment it out for earlier versions
End Type


;Basic setup
Global appheight=768
Global appwidth=1024
Global appdepth=32

AppTitle "";,"Are you sure you want to quit?"

SC_FPS=60	;Desired framerate
rtime=Floor(1000.0/SC_FPS)
limited=True

Graphics3D appwidth,appheight,appdepth,6
SetBuffer BackBuffer()

centrecam=CreatePivot()
PositionEntity centrecam,0,15,0
camera=CreateCamera(centrecam)
PositionEntity camera,0,20,-50,1

;Setup lighting
sun=CreateLight()
PositionEntity sun,-100,400,0
PointEntity sun,centrecam

;Setup a simple scene
ground=CreateMesh()
parquet=CreateSurface(ground)
v1=AddVertex(parquet,-125,0,150):v2=AddVertex(parquet,125,0,150):v3=AddVertex(parquet,125,0,-100)
AddTriangle(parquet,v1,v2,v3):v2=AddVertex(parquet,-125,0,-100):AddTriangle(parquet,v1,v3,v2)
EntityColor ground,0,0,255
block=CreateCube():ScaleMesh block,20,5,20:EntityColor block,255,0,0


;Our entity
box=CreateCube()
PositionEntity box,-10,15,0
ScaleMesh box,7,7,7
bone=CreatePivot(box):marker=CreateSphere(8,bone)

ID=AddBone(box,bone)
surface=GetSurface(box,1)
SetVertexBone(surface,0,ID)		;Add the top half of the cube to a bone
SetVertexBone(surface,1,ID)
SetVertexBone(surface,4,ID)
SetVertexBone(surface,5,ID)
SetVertexBone(surface,8,ID)
SetVertexBone(surface,9,ID)
SetVertexBone(surface,12,ID)
SetVertexBone(surface,13,ID)
SetVertexBone(surface,16,ID)
SetVertexBone(surface,17,ID)
SetVertexBone(surface,18,ID)
SetVertexBone(surface,19,ID)



While Not KeyDown(1)
	ctime=MilliSecs()	;For the delay later, nothing to do with bones
	
	;Camera movement
	MoveEntity camera,0,KeyDown(200)-KeyDown(208),KeyDown(30)-KeyDown(44)
	TurnEntity centrecam,0,KeyDown(203)-KeyDown(205),0
	PointEntity camera,centrecam
	
	PositionEntity bone,5*Cos(MilliSecs()/10),2.5+5*Sin(MilliSecs()/10),0	;Move the bone around so we can see it
	
	;System from here down
	RenderWorld
	
	If MilliSecs()-render_time=>1000 Then fps=frames:frames=0:render_time=MilliSecs():Else frames=frames+1
	Text 0,30,"FPS: "+fps
	
	Text 0,90,MemoryFastPeekFloat(bone+$D4)
	
	n=rtime-(MilliSecs()-ctime)		;Free spare CPU time - nothing to do with bones, just good practice
	Delay n-(limited+1)
	Flip limited
Wend

FreeBoneEnt(box)	;Using this command instead of FreeEntity is essential to prevent memory leaks

End


Function AddBone(entity,bone)		;Add an entity to a mesh as a bone. Must be not be scaled or rotated, but do position it first
	ent.BoneEnt=GetBoneEnt(entity)
		
	If ent=Null
		AddAnimSeq(entity,0)		;Give the entity an animator structure where the necessary info will be stored
		ent=New BoneEnt
		
		ent\entity=entity
		ent\objectlist=CreateBank(4)
		PokeInt ent\objectlist,0,entity
		ent\bone_tforms=CreateBank(48)
		PokeFloat ent\bone_tforms,0,1:PokeFloat ent\bone_tforms,16,1:PokeFloat ent\bone_tforms,32,1
		
		ent\objectlist_orig[0]=MemoryFastPeekInt(MemoryFastPeekInt(entity+$284)+36)
		ent\objectlist_orig[1]=MemoryFastPeekInt(MemoryFastPeekInt(entity+$284)+40)
		ent\objectlist_orig[2]=MemoryFastPeekInt(MemoryFastPeekInt(entity+$284)+44)
		
		ent\bone_tforms_orig[0]=MemoryFastPeekInt(MemoryFastPeekInt(entity+$284)+96)
		ent\bone_tforms_orig[1]=MemoryFastPeekInt(MemoryFastPeekInt(entity+$284)+100)
		ent\bone_tforms_orig[2]=MemoryFastPeekInt(MemoryFastPeekInt(entity+$284)+104)
		
		MemoryFastPokeInt(entity+$240,1)
		
		;Blitz3D v1101+ ONLY from here...
		ent\matrix101=CreateBank(84)
		PokeFloat ent\matrix101, 0,1.0		;Seems to function without these, but since I don't know what they do...
		PokeFloat ent\matrix101,16,1.0
		PokeFloat ent\matrix101,32,1.0
		PokeFloat ent\matrix101,48,1.0
		PokeFloat ent\matrix101,64,1.0
		PokeFloat ent\matrix101,80,1.0
		;...to here. Comment out this entire section in earlier versions
	EndIf
	
	ent\bones=ent\bones+1
	
	ResizeBank(ent\objectlist,BankSize(ent\objectlist)+4)
	MemoryFastPokeInt(MemoryFastPeekInt(entity+$23C)+36,MemoryFastPeekInt(ent\objectlist+4))
	MemoryFastPokeInt(MemoryFastPeekInt(entity+$23C)+40,MemoryFastPeekInt(ent\objectlist+4)+BankSize(ent\objectlist))
	MemoryFastPokeInt(MemoryFastPeekInt(entity+$23C)+44,MemoryFastPeekInt(ent\objectlist+4)+BankSize(ent\objectlist))
	
	ResizeBank(ent\bone_tforms,BankSize(ent\bone_tforms)+48)
	MemoryFastPokeInt(MemoryFastPeekInt(entity+$284)+96,MemoryFastPeekInt(ent\bone_tforms+4))
	MemoryFastPokeInt(MemoryFastPeekInt(entity+$284)+100,MemoryFastPeekInt(ent\bone_tforms+4)+BankSize(ent\bone_tforms))
	MemoryFastPokeInt(MemoryFastPeekInt(entity+$284)+104,MemoryFastPeekInt(ent\bone_tforms+4)+BankSize(ent\bone_tforms))
	
	PokeInt ent\objectlist,4*ent\bones,bone
	
	PokeFloat ent\bone_tforms,48*ent\bones,1:PokeFloat ent\bone_tforms,48*ent\bones+16,1:PokeFloat ent\bone_tforms,48*ent\bones+32,1
	PokeFloat ent\bone_tforms,48*ent\bones+36,EntityX(bone,0)
	PokeFloat ent\bone_tforms,48*ent\bones+40,EntityY(bone,0)
	PokeFloat ent\bone_tforms,48*ent\bones+44,EntityZ(bone,0)
	
	;Blitz3D v1101+ ONLY from here...
	ResizeBank(ent\matrix101,BankSize(ent\matrix101)+84)
	MemoryFastPokeInt(entity+$2A4,MemoryFastPeekInt(ent\matrix101+4))
	MemoryFastPokeInt(entity+$2A8,MemoryFastPeekInt(ent\matrix101+4)+BankSize(ent\matrix101))
	MemoryFastPokeInt(entity+$2AC,MemoryFastPeekInt(ent\matrix101+4)+BankSize(ent\matrix101))
	
	PokeFloat ent\matrix101,84*ent\bones+ 0,1.0		;Seems to function without these, but since I don't know what they do...
	PokeFloat ent\matrix101,84*ent\bones+16,1.0
	PokeFloat ent\matrix101,84*ent\bones+32,1.0
	PokeFloat ent\matrix101,84*ent\bones+48,1.0
	PokeFloat ent\matrix101,84*ent\bones+64,1.0
	PokeFloat ent\matrix101,84*ent\bones+80,1.0
	;...to here. Comment out this entire section in earlier versions
	
	Return ent\bones
End Function

Function GetBone(entity,boneID)				;Get a bone's entity handle from its simple ID
	be.BoneEnt=GetBoneEnt(entity)
	Return PeekInt(be\objectlist,4*boneID)
End Function

Function SetVertexBone(surface,vertex,boneID,bonenum=0,weight#=1.0)		;Attach a bone to a vertex, by bone ID
	vertexptr=MemoryFastPeekInt(surface+28)
	MemoryFastPokeByte(vertexptr+vertex*64+44+bonenum,boneID)
	If bonenum<3 Then MemoryFastPokeByte(vertexptr+vertex*64+44+bonenum+1,255)
	MemoryFastPokeFloat(vertexptr+vertex*64+48+bonenum*4,weight)
End Function

Function GetVertexBone(surface,vertex,bonenum=0)	;Returns a boneID for a vertex (not an entity handle)
	vertexptr=MemoryFastPeekInt(surface+28)
	Return MemoryFastPeekByte(vertexptr+vertex*64+44+bonenum)
End Function

Function ClearVertexBones(surface,vertex)	;This function is really just to make things clearer
	SetVertexBone(surface,vertex,255)		;Blitz treats a boneID of 255 as "stop looking". Don't need to actually zero the rest
End Function

Function GetBoneEnt.BoneEnt(entity)			;Gets a BoneEnt definition from an entity handle
	For be.BoneEnt=Each BoneEnt
		If be\entity=entity Then Return be
	Next
	Return Null
End Function

Function FreeBoneEnt(entity)				;Free a manually boned entity and its banks - you MUST use this to avoid memory leaks
	ent.BoneEnt=GetBoneEnt(entity)
	If ent=Null Then Return			;Or you could set it to free the non-boned entity anyway, and use this command on all entities - safer
	
	MemoryFastPokeInt(MemoryFastPeekInt(entity+$284)+36,ent\objectlist_orig[0])		;Restore the original lists so they can be freed properly
	MemoryFastPokeInt(MemoryFastPeekInt(entity+$284)+40,ent\objectlist_orig[1])
	MemoryFastPokeInt(MemoryFastPeekInt(entity+$284)+44,ent\objectlist_orig[2])
	
	MemoryFastPokeInt(MemoryFastPeekInt(entity+$284)+96,ent\bone_tforms_orig[0])
	MemoryFastPokeInt(MemoryFastPeekInt(entity+$284)+100,ent\bone_tforms_orig[1])
	MemoryFastPokeInt(MemoryFastPeekInt(entity+$284)+104,ent\bone_tforms_orig[2])
	
	;Blitz3D v1101+ ONLY from here...
	MemoryFastPokeInt(entity+$2A4,0)
	MemoryFastPokeInt(entity+$2A8,0)
	MemoryFastPokeInt(entity+$2AC,0)
	FreeBank ent\matrix101
	;...to here. Comment out this entire section in earlier versions
	
	FreeBank ent\objectlist
	FreeBank ent\bone_tforms
	FreeEntity entity
End Function
