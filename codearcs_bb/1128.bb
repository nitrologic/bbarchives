; ID: 1128
; Author: Chris C
; Date: 2004-08-06 06:21:32
; Title: interactive entities
; Description: clickable switches open doors

maxdoor=10
Dim door_ent(maxdoor),door_name$(maxdoor),door_state$(maxdoor),door_time(maxdoor)
; really must get round to doin' a door type!

Global door
Const type_character=1,type_scenery=2 ; collision types
Const sx=640,sy=480 ; size of screen
Const camhigh=160

Graphics3D sx,sy,16
SetBuffer BackBuffer()

cam = CreateCamera() 

EntityType cam,type_character
EntityRadius cam,30
CameraRange cam,1,10000
PositionEntity cam,-650,40,120
UpdateWorld

CameraZoom cam,1.5

;CreateLight()

map=LoadCSM("test.csm","textures\")
EntityType map,type_scenery

Collisions type_character,type_scenery,2,2



While Not KeyDown(1) ; if ESCAPE pressed then exit 

	mys = MouseYSpeed() 

	If Abs(EntityPitch(cam)+mys) < 75 TurnEntity cam, mys,0,0  
	TurnEntity cam,0,-MouseXSpeed(),0 
	RotateEntity cam,EntityPitch(cam),EntityYaw(cam),0


	LinePick(EntityX(cam),EntityY(cam),EntityZ(cam),0,-1000,0)
	th=PickedY()+camhigh
	If th>EntityY(cam) Then
		yvel=0
		PositionEntity cam,EntityX(cam),th,EntityZ(cam)	
	EndIf
	If th<EntityY(cam) Then yvel=yvel-1
	If th=EntityY(cam) And MouseDown(3) Then yvel=10

	MoveEntity cam,0,yvel,0

	If MouseDown(1) Then MoveEntity cam,0,0,3

	MoveMouse sx/2,sy/2 ; centre mouse cursor 


	UpdateWorld

	RenderWorld
	
	Text 0,0,"pos x="+EntityX(cam)
	Text 200,0,"pos y="+EntityY(cam)
	Text 400,0,"pos z="+EntityZ(cam)

	Text 200,10,"th="+th

	Text 0,20,"cam colls="+CountCollisions(cam)

	CameraPick(cam,sx/2,sy/2)

	cp=PickedEntity() 
	Text sx/2,sy/2,cp,True,True
	If cp Then 
		Text sx/2,10+sy/2,"classname="+KeyValue(cp,"classname")
		Text sx/2,20+sy/2,"target="+KeyValue(cp,"target")
		For n=0 To door-1
			If door_name(n)=KeyValue(cp,"target") And Keyvalue(cp,"classname")="switch" Then 
				Text sx/2,-30+sy/2,"**** right mouse to trigger ****",True
				Text sx/2,30+sy/2,"door ent="+door_ent(n)
				If MouseDown(2) Then
					If door_state(n)="" Then 
						door_time(n)=50
						door_state(n)="opening"
					End If
				End If
			EndIf
		Next
	EndIf



	For n=0 To door-1
		If door_time(n)=0 Then
			If door_state(n)="closing" Then	door_state(n)=""
			If door_state(n)="waiting" Then
				door_state(n)="closing" 
				door_time(n)=50
			EndIf
			If door_state(n)="opening" Then	
				door_state(n)="waiting" 
				door_time(n)=200
			EndIf
		EndIf

		If door_state(n)="opening" Then MoveEntity door_ent(n),0,4,0
		If door_state(n)="closing" Then MoveEntity door_ent(n),0,-4,0
		If door_time(n) Then door_time(n)=door_time(n)-1
	
		Text 0,100+(n*10),n+"="+door_state(n)+","+door_time(n)	

	Next 


	Flip

Wend 


;=================================================
;=================================================
Function LoadCSM(file$,texturepath$=".\")

f=ReadFile(file)
If Not f Return

ChangeDir FileDir(file)

lightmapbank=CreateBank()
texturebank=CreateBank()

;Version - this will load CShop 4.0 and CShop 4.1 maps
version=ReadInt(f)
If version<>4 And version<>5
CloseFile f
Return
EndIf

map=CreatePivot()

;Groups
;DebugLog groupcount+" groups"
groupcount=ReadInt(f)
For n=1 To groupcount
flags=ReadInt(f)
group=ReadInt(f)
Properties$=readstringn(f)
r=ReadInt(f)
g=ReadInt(f)
b=ReadInt(f)
Next

;Visgroups (new in 4.1)
If version=5
visgroupcount=ReadInt(f)
For n=1 To visgroupcount
name$=readstringn(f)
flags=ReadInt(f)
r=ReadInt(f)
g=ReadInt(f)
b=ReadInt(f)
Next
EndIf

;Lightmaps
lightmapcount=ReadInt(f)
;DebugLog lightmapcount+" lightmaps"
For n=1 To lightmapcount
w=ReadInt(f)
h=ReadInt(f)
texture=CreateTexture(w,h)
TextureCoords texture,1
ResizeBank lightmapbank,BankSize(lightmapbank)+4
PokeInt lightmapbank,BankSize(lightmapbank)-4,texture
LockBuffer TextureBuffer(texture)
For ty=0 To h-1
For tx=0 To w-1
hue=ReadInt(f)
WritePixelFast tx,ty,hue,TextureBuffer(texture)
Next
Next
UnlockBuffer TextureBuffer(texture)
Next

;Meshes
meshcount=ReadInt(f)

;DebugLog meshcount+" meshes"
For n=1 To meshcount
	flags=ReadInt(f)
	group=ReadInt(f)
	properties$=readstringn(f)
	r=ReadInt(f)
	g=ReadInt(f)
	b=ReadInt(f)
	x#=ReadFloat(f)
	y#=ReadFloat(f)
	z#=ReadFloat(f)
	

	If version=5 visgroup=ReadInt(f)

	facecount=ReadInt(f)
	;DebugLog facecount+" surfaces."

	mesh=CreateMesh(map)
	PositionEntity mesh,x,y,z
;	If properties<>"" Then
		EntityPickMode(mesh,2)
;	End If
	
	EntityType mesh,type_scenery
	NameEntity mesh,properties
	
	If keyvalue(mesh,"classname")="door" Then
		door_ent(door)=mesh
		door_name$(door)=keyvalue(mesh,"name")
		door=door+1
	EndIf
	
	;Surfaces
	For s=1 To facecount
		flags=ReadInt(f)
		texturefile$=readstringn(f)
		lightmapindex=ReadInt(f)
		offsetu#=ReadFloat(f)
		offsetv#=ReadFloat(f)
		scaleu#=ReadFloat(f)
		scalev#=ReadFloat(f)
		rotation#=ReadFloat(f)
		vertexcount=ReadInt(f)
		;DebugLog vertexcount+" vertices"
		trianglecount=ReadInt(f)
		;DebugLog trianglecount+" triangles"
		linecount=ReadInt(f)

		surf=CreateSurface(mesh)
		brush=CreateBrush()
		texturefile=Lower(texturefile)
		texture=retrievetexture(texturepath+texturefile,texturebank)
		If texture BrushTexture brush,texture
		If lightmapindex>0 And lightmapindex*4<=BankSize(lightmapbank)
			lightmap=PeekInt(lightmapbank,(lightmapindex-1)*4)
			If lightmap
				BrushTexture brush,lightmap,0,1
				BrushFX brush,1
			EndIf
		EndIf
		PaintSurface surf,brush
		FreeBrush brush

;Vertices
		For v=0 To vertexcount-1
			x#=ReadFloat(f)
			y#=ReadFloat(f)
			z#=ReadFloat(f)
			nx#=ReadFloat(f)
			ny#=ReadFloat(f)
			nz#=ReadFloat(f)
			r=ReadInt(f)
			g=ReadInt(f)
			b=ReadInt(f)
			u0#=ReadFloat(f)
			v0#=ReadFloat(f)
			w0#=ReadFloat(f)
			u1#=ReadFloat(f)
			v1#=ReadFloat(f)
			w1#=ReadFloat(f)

			TFormPoint x,y,z,0,mesh
			AddVertex surf,TFormedX(),TFormedY(),TFormedZ(),u0,-v0
			VertexColor surf,v,r,g,b
			VertexTexCoords surf,v,u1,-v1,0,1
			VertexNormal surf,v,nx,ny,nz
		Next

;Triangles
		For t=0 To trianglecount-1
			a=ReadInt(f)
			b=ReadInt(f)
			c=ReadInt(f)
			AddTriangle surf,a,c,b
		Next

		For l=0 To linecount-1
			a=ReadInt(f)
			b=ReadInt(f)
		Next
	Next



Next

;Point Entities
entitycount=ReadInt(f)
;DebugLog entitycount+" entities"
For n=1 To entitycount
visgroup=ReadInt(f); used to be flags, but wasn't really used
group=ReadInt(f)
properties$=readstringn(f)
x#=ReadFloat(f)
y#=ReadFloat(f)
z#=ReadFloat(f)
entity=CreatePivot(map)
NameEntity entity,properties
PositionEntity entity,x,y,z
Next

;Free textures
For n=0 To BankSize(lightmapbank)-1 Step 4
FreeTexture PeekInt(lightmapbank,n)
Next
FreeBank lightmapbank
For n=0 To BankSize(texturebank)-1 Step 8
FreeBank PeekInt(texturebank,n)
FreeTexture PeekInt(texturebank,n+4)
Next
FreeBank texturebank

CloseFile f
Return map
End Function

;Read a null-terminated string
Function ReadStringN$(f,maxlength=0)
Repeat
ch=ReadByte(f)
If ch=0 Return t$
If maxlength
If Len(t$)=maxlength Return t$+Chr(ch)
EndIf
t$=t$+Chr$(ch)
Forever
End Function

;Return a loaded texture
Function RetrieveTexture(file$,bank)
For n=0 To BankSize(bank)-1 Step 8
namebank=PeekInt(bank,n)
s$=""
For b=0 To BankSize(namebank)-1
s=s+Chr(PeekByte(namebank,b))
Next
If s=file Return PeekInt(bank,n+4)
Next
ResizeBank bank,BankSize(bank)+8
namebank=CreateBank(Len(file))
For b=0 To BankSize(namebank)-1
PokeByte namebank,b,Asc(Mid(file,b+1))
Next
;DebugLog "Loading texture "+Chr(34)+CurrentDir()+file+Chr(34)
PokeInt bank,BankSize(bank)-8,namebank
texture=LoadTexture(file)
If Not texture DebugLog "Failed to load texture "+Chr(34)+CurrentDir()+file+Chr(34)
PokeInt bank,BankSize(bank)-4,texture
Return texture
End Function

;Get the file part of a file path
Function FileName$(file$,ext=1)
file=Replace(file,"/","\")
Repeat
p=Instr(file,"\")
If p
file=Right(file,Len(file)-p)
Else
Exit
EndIf
Forever
If Not ext
p=Instr(file,".")
If p file=Left(file,p-1)
EndIf
Return file
End Function

;Get the directory of a file path
Function FileDir$(file$)
file=Replace(file,"/","\")
oldp=1
Repeat
p=Instr(file,"\",oldp)
If p
oldp=p+1
Else
file=Left(file,oldp-1)
Exit
EndIf
Forever
Return file
End Function

;Parsing function
Function Piece$(s$,entry,char$=" ")
While Instr(s,char+char)
s=Replace(s,char+char,char)
Wend
For n=1 To entry-1
p=Instr(s,char)
s=Right(s,Len(s)-p)
Next
p=Instr(s,char)
If p<1
a$=s
Else
a=Left(s,p-1)
EndIf
Return a
End Function

;Function for retrieving entity properties
;[ "light"=KeyValue(entity,"classname") ]
Function KeyValue$(entity,key$)
properties$=EntityName(entity)
key$=Lower(key)
Repeat
p=Instr(properties,Chr(10))
If p test$=(Left(properties,p-1)) Else test=properties
testkey$=Piece(test,1,"=")
testkey=Trim(testkey)
testkey=Replace(testkey,Chr(34),"")
testkey=Lower(testkey)
If testkey=key
value$=Piece(test,2,"=")
value$=Trim(value$)
value$=Replace(value$,Chr(34),"")
Return value
EndIf
If Not p Return
properties=Right(properties,Len(properties)-p)
Forever
End Function
