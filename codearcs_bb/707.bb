; ID: 707
; Author: DareDevil
; Date: 2003-05-29 00:46:09
; Title: 3dsMax Ase -&gt; B3D
; Description: convert 3ds Max Ase export to B3D file - Support Lightmap and Reflect map not animation

Open source
all the modifications must be public 
and pleace comment this ;)

;===================================================================== 
;---------------------------------------------------------------- 
; Vincenzo Caldarulo 
; nickname : VinsentX 
; e-mail: enzo_light@libero.it 
; main Programmer V&D Software Multimedia 
;---------------------------------------------------------------- 
;===================================================================== 
;**************************** 
;------------------------- 
; 3DMax Ase export to B3D 
; Support Converter: 
; 
; - Default Map 
; - Light Map 
; - Reflect Map 
;------------------------- 
;**************************** 
; 
Include "b3dfile.bb" 

Graphics3D 640,480,16,2 
SetBuffer BackBuffer() 

;===> 
Const Simb1 = 32 ; 32 = ASC(" ") 
const Simb2 = 125; 125 = ASC("}") 

const ArrDim = 200000 
const ArrMat = 99 
;===> 
global Obj_name$ 
global XYZ_Count_vertex 
global XYZ_Count_face 
global XYZ_px#, XYZ_py#, XYZ_pz# 
global XYZ_ax#, XYZ_ay#, XYZ_az#, XYZ_aw# 
global XYZ_sx#, XYZ_sy#, XYZ_sz# 
global UVW_Count_vertex 
global UVW_Count_face 
global UVW_Count_vertex1 
global UVW_Count_face1 
global UVW_OffU0# = 1 
Global UVW_OffV0# = 1 
Global UVW_OffU1# = 1 
Global UVW_OffV1# = 1 
Global UVW_OffU2# = 1 
Global UVW_OffV2# = 1 
Global UVW_TileU0# = 1 
global UVW_TileV0# = 1 
global UVW_TileU1# = 1 
global UVW_TileV1# = 1 
global UVW_TileU2# = 1 
global UVW_TileV2# = 1 
global UVW_for_XYZ 
;===> 
dim XYZ_vertex#(ArrDim,3) 
dim XYZ_normal#(ArrDim,3) 
dim XYZ_face(ArrDim,3) 
dim UVW_vertex#(ArrDim,3) 
dim UVW_face(ArrDim,3) 
dim UVW_vertex1#(ArrDim,3) 
dim UVW_face1(ArrDim,3) 
;===> 
global MAT_Count; 
;===> 
dim MatName$(ArrMat) ; nome materiale 
dim MatTex0$(ArrMat) ; nome texture default 
dim MatTex1$(ArrMat) ; nome texture lightmap 
dim MatTex2$(ArrMat) ; nome texture EnvMap 
dim MatR#(ArrMat) ; color R 
dim MatG#(ArrMat) ; color G 
dim MatB#(ArrMat) ; color B 
dim MatA#(ArrMat) ; Opacity 
dim MatShine#(ArrMat) ; Shininess 
;===> 


;===> 
;Convert_Ase_B3d("obj.ASE","Viewer V&D\gfx\Obj.B3D") 
Text 0,00,"Inizio esportazione" 
Flip 

global time1 = millisecs() 
Convert_Ase_B3d("obj.ASE","Obj.B3D") 
global time2 = millisecs() 

Global ObjExp = loadmesh("obj.b3d"); 

Text 0,10,"Esportazione avvenuta con successo" 
;===================================================================== 
;===================================================================== 
; ------------------------------- 
; Creation de la caméra 
; ------------------------------- 
Global Mouse_X_Speed# 
Global Mouse_Y_Speed# 
Global Camera 
Global Camera_VelX# 
Global Camera_VelZ# 
Global Camera_Pitch# 
Global Camera_Yaw# 

Camera=CreateCamera() 
rotateentity camera,0,0,0 
;PositionEntity camera,290,-600,2380 ; da inc a metri x100 
PositionEntity camera,0,0,0 ; da inc a metri x100 
CameraRange camera,0.1,10000 
;formula del fov camera Zoom =Sqrt(Fov) 
;CameraZoom camera, sqr(50) ; 1 = 10 di fov // 7.5 = 50 di fov 
CameraZoom camera, 1 ; 1 = 10 di fov // 7.5 = 50 di fov 
; ------------------------------- 
; Chargement de l'objet 
; ------------------------------- 
;ObjExp=LoadMesh( "Obj.b3d" ) 
;ObjExp=LoadAnimMesh( "Obj.b3d" ) 
;Animate ObjExp, 1 
RotateEntity ObjExp,0,0,0 
PositionEntity ObjExp,0,0,0 
EntityShininess ObjExp,1 
PointEntity Camera,ObjExp 

temp = createsphere(8) 
entityalpha temp,0.5 
Dither True 

LightCA = 255 
AmbientLight LightCA, LightCA, LightCA 

font=LoadFont( "Arial",20 ) 
SetFont font 
;===> 
MoveMouse GraphicsWidth()/2,GraphicsHeight()/2 
;rotateentity camera,-2.7,178,180 
While KeyDown(1)=0 
Procedure_Freelook(1.05,0.2) 
updateworld() 
RenderWorld() 
Text 0,0,"Esportazione avvenuta con successo" 
text 0,15,"px:"+entityx(Camera)+"py:"+entityy(Camera)+"pz:"+entityz(Camera) 
text 0,30,"rx:"+entitypitch(Camera)+"ry:"+entityyaw(Camera)+"rz:"+entityroll(Camera) 
text 0,45,"Time to export" 
text 0,60,"Millisecs "+(time2-time1)+" secs "+((time2-time1)/1000) 
Flip 
Wend 
End 
; ------------------------- 
; Fonction Freelook 
; ------------------------- 
Function Procedure_Freelook(Velocity#,Speed#) 
AngMax = 89 
Mouse_X_Speed=MouseXSpeed()*0.5 
Mouse_Y_Speed=MouseYSpeed()*0.5 

MoveMouse GraphicsWidth()/2,GraphicsHeight()/2 
Camera_Pitch=Camera_Pitch+Mouse_Y_Speed 
Camera_Yaw=Camera_Yaw-Mouse_X_Speed 

If Camera_Pitch<-AngMax Then Camera_Pitch=-AngMax 
If Camera_Pitch>AngMax Then Camera_Pitch=AngMax 

RotateEntity camera,Camera_Pitch,Camera_Yaw,0 

If KeyDown(Key_ArrowPad_Left) Camera_VelX=Camera_VelX-Speed# ElseIf KeyDown(Key_ArrowPad_Right) Camera_VelX=Camera_VelX+Speed# 
; If KeyDown(Key_ArrowPad_Down) Camera_VelZ=Camera_VelZ-Speed# ElseIf KeyDown(Key_ArrowPad_Up) Camera_VelZ=Camera_VelZ+Speed# 
If MouseDown(2) Camera_VelZ=Camera_VelZ-Speed# ElseIf MouseDown(1) Camera_VelZ=Camera_VelZ+Speed# 

Camera_VelX=Camera_VelX/Velocity# 
Camera_VelZ=Camera_VelZ/Velocity# 
MoveEntity camera,Camera_VelX,0,Camera_VelZ 
End Function 
;===================================================================== 
;===================================================================== 


;=============================== 
;--------------------------- 
; Scrittura stringa 
;--------------------------- 
Function ReadStringN$(f,sep$) 
s$ = ""; 
SepVal = Asc(sep$) 

While Not Eof(f) 
let = ReadByte(f); 

if (let = SepVal and s$ <> "") 
return s 
else 
if ((let >= Simb1) and (let <= Simb2) and (let <> SepVal) and (let <> 34)); the code 34 = " from ascii table 
s$ = s$ + Chr(let) 
else 
if (s$ <> "") return s 
end if 
end if 
Wend 
End Function 

Function EstractName$(str_nome$) 
s$ = "" 
lun = len(str_nome) 
;===> 
For xc = lun To 1 Step -1 
s$ = mid(str_nome,xc,1) 
If ((s$ = "\") Or (xc=1)) 
s$ = mid(str_nome,xc+1,lun-xc); 
exit 
end if 
next 
;===> 
return s$ 
;===> 
end function 

;--------------------------- 
; Scrittura stringa 
;--------------------------- 
Function Convert_Ase_B3d(LoadFile$,SaveFile$) 
;stop 
infile = OpenFile(LoadFile$) 
outfile = Writefile(SaveFile$) 
WriteBB3D( outfile, "Init" ) 
StateMesh = 0 ; 1 = Init Root Object 
While Not Eof(infile) 
;===> 
Obj_name$ = "" 
XYZ_Count_vertex = 0 
XYZ_Count_face = 0 
UVW_Count_vertex = 0 
UVW_Count_face = 0 
UVW_Count_vertex1 = 0 
UVW_Count_face1 = 0 
;===> 
StateLoad = LoadAse(infile) 
;===> 
if ((StateLoad = 1) and (StateMesh = 0)) 
WriteBB3D( outfile, "Material" ) 
end if 
;===> 
if (StateLoad = 2) 
;===> 
if (stateMesh = 0) ; Open Root Object 
StateMesh = 1 
b3dBeginChunk( "NODE" );------------root node 
b3dWriteString( "root");name 
b3dWriteFloat( 0 ) ;x_pos 
b3dWriteFloat( 0 ) ;y_pos 
b3dWriteFloat( 0 ) ;y_pos 
b3dWriteFloat( 1 ) ;x_scale 
b3dWriteFloat( 1 ) ;y_scale 
b3dWriteFloat( 1 ) ;z_scale 
b3dWriteFloat( 1 ) ;rot_w 
b3dWriteFloat( 0 ) ;rot_x 
b3dWriteFloat( 0 ) ;rot_y 
b3dWriteFloat( 0 ) ;rot_z 
end if 
;===> 
WriteBB3D( outfile, "Mesh" ) 
end if 
;===> 
Wend 
;===> 
if (stateMesh = 1) ; Close Root Object 
b3dEndChunk() ;end of NODE chunk 
end if 
;===> 
WriteBB3D( outfile, "End" ) 

CloseFile infile 
CloseFile outfile 
end function 

;--------------------------- 
; Scrittura stringa 
;--------------------------- 
Function LoadAse(ASE) 

OpenP = 0 
While Not Eof(ASE) 
chunk$ = ReadStringN(ASE," ") 
;===> 
if (chunk$ = "*GEOMOBJECT") then 
;stop 
While Not Eof(ASE) 
cluster$ = ReadStringN(ASE," ") 
;===> 
if (cluster$ = "{") 
OpenP = OpenP+1 
end if 
if (cluster$ = "}") then 
OpenP = OpenP-1 
end if 
if (OpenP <= 0) 
if (XYZ_Count_vertex>2) 
return 2 
else 
return 0 
end if 
end if 
;===> 
if (cluster$ = "*NODE_NAME") then 
Obj_name$ = ReadStringN(ASE," ") 
end if 
;===> 
if (cluster$ = "*TM_POS") then 
XYZ_px = ReadStringN(ASE," ") 
XYZ_py = ReadStringN(ASE," ") 
XYZ_pz = ReadStringN(ASE," ") 
end if 
;===> 
if (cluster$ = "*TM_ROTAXIS") then 
XYZ_rx = ReadStringN(ASE," ") 
XYZ_ry = ReadStringN(ASE," ") 
XYZ_rz = ReadStringN(ASE," ") 
end if 
;===> 
if (cluster$ = "*TM_ROTANGLE") then 
XYZ_rw = ReadStringN(ASE," ") 
end if 
;===> 
if (cluster$ = "*TM_SCALE") then 
XYZ_sx = ReadStringN(ASE," ") 
XYZ_sy = ReadStringN(ASE," ") 
XYZ_sz = ReadStringN(ASE," ") 
end if 
;===> 
if cluster$ = "*MATERIAL_REF" then 
UVW_for_XYZ = ReadStringN(ASE," ") 
end if 

;===> 
if (cluster$ = "*MESH") then 
While Not Eof(ASE) 
cluster1$ = ReadStringN(ASE," ") 
;===> 
if (cluster1$="}") exit 
;===> 
if (cluster1$ = "*MESH_NUMVERTEX") then 
XYZ_Count_vertex = ReadStringN(ASE," ") 
end if 
;===> 
if (cluster1$ = "*MESH_NUMFACES") then 
XYZ_Count_face = ReadStringN(ASE," ") 
end if 
;===> 
if (cluster1$ = "*MESH_VERTEX_LIST") then 
MESH_VERTEX_LIST(ASE) 
end if 
;===> 
if (cluster1$ = "*MESH_FACE_LIST") then 
MESH_FACE_LIST(ASE) 
end if 
;===> 
if cluster1$ = "*MESH_NUMTVERTEX" then 
UVW_Count_vertex = ReadStringN(ASE," ") 
end if 
;===> 
if cluster1$ = "*MESH_TVERTLIST" then 
MESH_TVERTLIST(ASE) 
end if 
;===> 
if cluster1$ = "*MESH_NUMTVFACES" then 
UVW_Count_face = ReadStringN(ASE," ") 
end if 
;===> 
if cluster1$ = "*MESH_TFACELIST" then 
MESH_TFACELIST(ASE) 
end if 
;===> 
if cluster1$ = "*MESH_MAPPINGCHANNEL" then 
While Not Eof(ASE) 
chunk1$ = ReadStringN(ASE," ") 
;===> 
if (chunk1$="}") exit 
;===> 
if chunk1$ = "*MESH_NUMTVERTEX" then 
UVW_Count_vertex1 = ReadStringN(ASE,"") 
end if 
;===> 
if chunk1$ = "*MESH_TVERTLIST" then 
MESH_TVERTLIST1(ASE) 
end if 
;===> 
if chunk1$ = "*MESH_NUMTVFACES" then 
UVW_Count_face1 = ReadStringN(ASE," ") 
end if 
;===> 
if chunk1$ = "*MESH_TFACELIST" then 
MESH_TFACELIST1(ASE) 
end if 
;===> 
wend 
end if 
;===> 
if (cluster1$ = "*MESH_NORMALS") then 
MESH_NORMALS(ASE) 
end if 
;===> 
wend 
end if 
;===> 
wend 
end if 
;===> 
; Chunck *MATERIAL_LIST 
if chunk$ = "*MATERIAL_LIST" then 
OpenP = 0 
Id_mat = 0 
While Not Eof(ASE) 
cluster$ = ReadStringN(ASE," ") 
;===> 
if (cluster$ = "{") 
OpenP = OpenP+1 
end if 
if (cluster$ = "}") then 
OpenP = OpenP-1 
end if 
if (OpenP <= 0) 
if (MAT_Count=>0) 
return 1 
else 
return 0 
end if 
end if 
;===> 
if (cluster$ = "*MATERIAL_COUNT") then 
cnt = ReadStringN(ASE," ") 
MAT_Count = cnt-1 
end if 
;===> 
if (cluster$ = "*MATERIAL") then 
Id_mat = ReadStringN(ASE," ") 
end if 
;===> 
if (cluster$ = "*MATERIAL_NAME") then 
MatName$(Id_mat) = ReadStringN(ASE," ") 
end if 
;===> 
if (cluster$ = "*MATERIAL_DIFFUSE") then 
MatR(Id_mat) = ReadStringN(ASE," ") 
MatG(Id_mat) = ReadStringN(ASE," ") 
MatB(Id_mat) = ReadStringN(ASE," ") 
end if 
;===> 
if (cluster$ = "*MATERIAL_SHINE") then 
MatShine(Id_mat) = ReadStringN(ASE," ") 
end if 
;===> 
if (cluster$ = "*MATERIAL_TRANSPARENCY") then 
val# = ReadStringN(ASE," ") 
MatA(Id_mat) = 1.0-val 
end if 
;===> 
if (cluster$ = "*MAP_DIFFUSE") then 
While Not Eof(ASE) 
cluster1$ = ReadStringN(ASE," ") 
if (cluster1$="}") exit 
if (cluster1$="*BITMAP") 
name$ = ReadStringN(ASE,"") 
MatTex0$(Id_mat) = EstractName$(name$); 
end if 
if (cluster1$="*UVW_U_OFFSET") 
UVW_OffU0# = ReadStringN(ASE,""); 
end if 
if (cluster1$="*UVW_V_OFFSET") 
UVW_OffV0# = ReadStringN(ASE,""); 
end if 
if (cluster1$="*UVW_U_TILING") 
ut# = ReadStringN(ASE,"") 
UVW_TileU0# = 1.0/ut#; 
end if 
if (cluster1$="*UVW_V_TILING") 
vt# = ReadStringN(ASE,"") 
UVW_TileV0# = 1.0/vt#; 
end if 
Wend 
end if 
;===> 
if (cluster$ = "*MAP_SELFILLUM") then 
While Not Eof(ASE) 
cluster1$ = ReadStringN(ASE," ") 
if (cluster1$="}") exit 
if (cluster1$="*BITMAP") 
name$ = ReadStringN(ASE,"") 
MatTex1$(Id_mat) = EstractName$(name$); 
end if 
if (cluster1$="*UVW_U_OFFSET") 
UVW_OffU1# = ReadStringN(ASE,""); 
end if 
if (cluster1$="*UVW_V_OFFSET") 
UVW_OffV1# = ReadStringN(ASE,""); 
end if 
if (cluster1$="*UVW_U_TILING") 
ut# = ReadStringN(ASE,"") 
UVW_TileU1# = 1.0/ut#; 
end if 
if (cluster1$="*UVW_V_TILING") 
vt# = ReadStringN(ASE,"") 
UVW_TileV1# = 1.0/vt#; 
end if 
Wend 
end if 
;===> 
if (cluster$ = "*MAP_REFLECT") then 
While Not Eof(ASE) 
cluster1$ = ReadStringN(ASE," ") 
if (cluster1$="}") exit 
if (cluster1$="*BITMAP") 
name$ = ReadStringN(ASE,"") 
MatTex2$(Id_mat) = EstractName$(name$); 
end if 
if (cluster1$="*UVW_U_OFFSET") 
UVW_OffU2# = ReadStringN(ASE,""); 
end if 
if (cluster1$="*UVW_V_OFFSET") 
UVW_OffV2# = ReadStringN(ASE,""); 
end if 
if (cluster1$="*UVW_U_TILING") 
ut# = ReadStringN(ASE,"") 
UVW_TileU2# = 1.0/ut#; 
end if 
if (cluster1$="*UVW_V_TILING") 
vt# = ReadStringN(ASE,"") 
UVW_TileV2# = 1.0/vt#; 
end if 
Wend 
end if 
;===> 
if (cluster$ = "*") then 
Obj_name$ = ReadStringN(ASE," ") 
end if 
;===> 
wend 
end if 

wend 

end function 


;--------------------------- 
; 
;--------------------------- 
Function MESH_NORMALS(ASE) 
id = 0 
While Not Eof(ASE) 
cluster1$ = ReadStringN(ASE," ") 
if (cluster1$="*MESH_FACENORMAL") 
No$ = ReadStringN(ASE," ") 
XYZ_normal(id,0) = ReadStringN(ASE," ") 
XYZ_normal(id,1) = ReadStringN(ASE," ") 
XYZ_normal(id,2) = ReadStringN(ASE," ") 
id = id+1 
end if 
if (cluster1$="}") exit 
Wend 
end function 
;--------------------------- 
; 
;--------------------------- 
function MESH_VERTEX_LIST(ASE) 
id = 0 
While Not Eof(ASE) 
cluster2$ = ReadStringN(ASE," ") 
if (cluster2$="*MESH_VERTEX") 
No$ = ReadStringN(ASE," ") 
XYZ_vertex(id,0) = ReadStringN(ASE," ") 
XYZ_vertex(id,1) = ReadStringN(ASE," ") 
XYZ_vertex(id,2) = ReadStringN(ASE," ") 
id = id+1 
end if 
if (cluster2$="}") exit 
Wend 
end function 
;--------------------------- 
; 
;--------------------------- 
function MESH_FACE_LIST(ASE) 
id = 0 
While Not Eof(ASE) 
cluster2$ = ReadStringN(ASE," ") 
if (cluster2$="*MESH_FACE") 
No$ = ReadStringN(ASE," ") 
No$ = ReadStringN(ASE," ") 
;--- 
f1 = ReadStringN(ASE," ") : No$ = ReadStringN(ASE," ") 
f2 = ReadStringN(ASE," ") : No$ = ReadStringN(ASE," ") 
f3 = ReadStringN(ASE," ") 
XYZ_face(id,0) = f1 
XYZ_face(id,1) = f2 
XYZ_face(id,2) = f3 
id = id+1 
end if 
if (cluster2$="}") exit 
Wend 
end function 
;--------------------------- 
; 
;--------------------------- 
function MESH_TVERTLIST(ASE) 
id = 0 
While Not Eof(ASE) 
cluster2$ = ReadStringN(ASE," ") 
if (cluster2$="*MESH_TVERT") 
No$ = ReadStringN(ASE," ") 
UVW_vertex(id,0) = ReadStringN(ASE," ") 
UVW_vertex(id,1) = ReadStringN(ASE," ") 
UVW_vertex(id,2) = ReadStringN(ASE," ") 
id = id+1 
end if 
if (cluster2$="}") exit 
wend 
end function 
;--------------------------- 
; 
;--------------------------- 
function MESH_TFACELIST(ASE) 
id = 0 
While Not Eof(ASE) 
cluster2$ = ReadStringN(ASE," ") 
if (cluster2$="*MESH_TFACE") 
No$ = ReadStringN(ASE," ") 
UVW_face(id,0) = ReadStringN(ASE," ") 
UVW_face(id,1) = ReadStringN(ASE," ") 
UVW_face(id,2) = ReadStringN(ASE," ") 
id = id+1 
end if 
if (cluster2$="}") exit 
wend 
end function 
;--------------------------- 
; 
;--------------------------- 
function MESH_TVERTLIST1(ASE) 
id = 0 
While Not Eof(ASE) 
cluster2$ = ReadStringN(ASE," ") 
if (cluster2$="*MESH_TVERT") 
No$ = ReadStringN(ASE," ") 
UVW_vertex1(id,0) = ReadStringN(ASE," ") 
UVW_vertex1(id,1) = ReadStringN(ASE," ") 
UVW_vertex1(id,2) = ReadStringN(ASE," ") 
id = id+1 
end if 
if (cluster2$="}") exit 
wend 
end function 
;--------------------------- 
; 
;--------------------------- 
function MESH_TFACELIST1(ASE) 
id = 0 
While Not Eof(ASE) 
cluster2$ = ReadStringN(ASE," ") 
if (cluster2$="*MESH_TFACE") 
No$ = ReadStringN(ASE," ") 
UVW_face1(id,0) = ReadStringN(ASE," ") 
UVW_face1(id,1) = ReadStringN(ASE," ") 
UVW_face1(id,2) = ReadStringN(ASE," ") 
id = id+1 
end if 
if (cluster2$="}") exit 
wend 
end function 
;========================================================================= 
;========================================================================= 
;========================================================================= 
;--------------------------- 
; 
;--------------------------- 

Function WriteBB3D( file, state$ ) 
select state$ 
;------------------- 
case "Init" 
b3dSetFile( file ) 

; b3dWriteString( "Export 3DSMax Ase To B3D" ) ;name 
; b3dWriteString( "V&D Software Multimedia" ) ;name 
; b3dWriteString( "Write VinsentX Vincenzo Caldarulo" ) ;name 


b3dBeginChunk( "BB3D" ) 
b3dWriteInt( 1 ) ;version 
;------------------- 
case "Material" 
for Id_mat = 0 to MAT_Count 
b3dBeginChunk( "TEXS" ) 
;===> Diffuse map 
b3dWriteString( MatTex0$(Id_mat) ) 
b3dWriteInt( 1 ) ;textureflag 
b3dWriteInt( 2 ) ;textureblend 
b3dWriteFloat( UVW_OffU0# ) ;x_pos 
b3dWriteFloat( UVW_OffV0# ) ;y_pos 
b3dWriteFloat( UVW_TileU0# );x_scale 
b3dWriteFloat( UVW_TileV0# );y_scale 
b3dWriteFloat( 0 ) ;rotation 
;===> Light Map 
b3dWriteString( MatTex1$(Id_mat) ) 
b3dWriteInt( 65536) ;textureflag for 2nd ! uv channel [65536=normal, 65584=clamp u,v and so on ...] 
b3dWriteInt( 2 ) ;textureblend 
b3dWriteFloat( UVW_OffU1# ) ;x_pos 
b3dWriteFloat( UVW_OffV1# ) ;y_pos 
b3dWriteFloat( UVW_TileU1# );x_scale 
b3dWriteFloat( -UVW_TileV1# );y_scale 
b3dWriteFloat( 0 ) ;rotation 
;===> ReflectMap 
b3dWriteString( MatTex2$(Id_mat) ) 
b3dWriteInt( 64 ) ;textureflag 
b3dWriteInt( 2 ) ;textureblend 
b3dWriteFloat( UVW_OffU2# ) ;x_pos 
b3dWriteFloat( UVW_OffV2# ) ;y_pos 
b3dWriteFloat( UVW_TileU2# );x_scale 
b3dWriteFloat( UVW_TileV2# );y_scale 
b3dWriteFloat( 0 ) ;rotation 
;===> 
b3dEndChunk() ;end of TEXS chunk 
next 

for Id_mat = 0 to MAT_Count 
mat = Id_mat*3 
b3dBeginChunk( "BRUS" ) 
;===> 
b3dWriteInt( 3 ) ;textures per brush[1] 
b3dWriteString( MatName$(Id_mat) ) ;name 
b3dWriteFloat( MatR(Id_mat) ) ;red 
b3dWriteFloat( MatG(Id_mat) ) ;green 
b3dWriteFloat( MatB(Id_mat) ) ;blue 
b3dWriteFloat( MatA(Id_mat) ) ;alpha 
b3dWriteFloat( MatShine(Id_mat) ) ;shininess 
b3dWriteInt( 0 ) ;blend 
b3dWriteInt( 1 ) ;FX 
b3dWriteInt( mat+0 ) ;texture_id UV0 
b3dWriteInt( mat+2 ) ;texture_id UV0 
b3dWriteInt( mat+1 ) ;texture_id UV1 
;===> 
b3dEndChunk() ;end of BRUS chunk 
next 
;------------------- 
case "Mesh" 
;stop 
; if (Obj_name$="Plane01") then stop 
b3dBeginChunk( "NODE" );------------sub nodes 
b3dWriteString( Obj_name$);name 
b3dWriteFloat( 0 ) ;x_pos XYZ_px# 
b3dWriteFloat( 0 ) ;y_pos XYZ_py# 
b3dWriteFloat( 0 ) ;y_pos XYZ_pz# 
b3dWriteFloat( 1 ) ;x_scale XYZ_sx# 
b3dWriteFloat( 1 ) ;y_scale XYZ_sy# 
b3dWriteFloat( 1 ) ;z_scale XYZ_sz# 
b3dWriteFloat( 1 ) ;rot_w XYZ_aw# 
b3dWriteFloat( 0 ) ;rot_x XYZ_ax# 
b3dWriteFloat( 0 ) ;rot_y XYZ_ay# 
b3dWriteFloat( 0 ) ;rot_z XYZ_az# 
WriteMESH1( mesh1 ) 
b3dEndChunk() ;end of NODE chunk 
;------------------- 
case "End" 
b3dEndChunk() ;end of BB3D chunk 
End Select 
End Function 
;============================== 
;--------------------------- 
; 
;--------------------------- 
Function WriteMESH1(curobj ) 
;stop 
n_tris = (XYZ_Count_face-1) 

b3dBeginChunk( "MESH" ) 
b3dWriteInt( -1 ) ;no 'entity' brush-<---the brush!! 

b3dBeginChunk( "VRTS" ) 
b3dWriteInt( 0 ) ;flags - 0=no 1=normal 2=Vertex color 
b3dWriteInt( 2 ) ;0 tex_coord sets 
b3dWriteInt( 3 ) ;0 coords per set 

For j=0 To n_tris 
For k=2 To 0 step -1 
;===> Id di ricostruzione triangoli 
Id_Vert = XYZ_face(j,k) 
Id_UV0 = UVW_face(j,k) 
Id_UV1 = UVW_face1(j,k) 
;===> XYZ Vertex 
vx# = XYZ_vertex#(Id_Vert,0) 
vy# = XYZ_vertex#(Id_Vert,2) 
vz# = XYZ_vertex#(Id_Vert,1) 
b3dWriteFloat( vx# ); VX 
b3dWriteFloat( vy# ); VY 
b3dWriteFloat( vz# ); VZ 
;===> XYZ Vertex 
nx# = XYZ_normal#(Id_Vert,0) 
ny# = XYZ_normal#(Id_Vert,2) 
nz# = XYZ_normal#(Id_Vert,1) 
;b3dWriteFloat( nx# ); NX 
;b3dWriteFloat( ny# ); NY 
;b3dWriteFloat( nz# ); NZ 
;===> UVW 0 Default Map 
u0# = UVW_vertex#(Id_UV0,0) 
v0# = UVW_vertex#(Id_UV0,1) 
w0# = UVW_vertex#(Id_UV0,2) 
b3dWriteFloat( u0# ) ; U0 
b3dWriteFloat( v0# ) ; V0 
b3dWriteFloat( w0# ) ; W0 
;===> UVW 1 Light Map 
u1# = UVW_vertex1#(Id_UV1,0) 
v1# = UVW_vertex1#(Id_UV1,1) 
w1# = UVW_vertex1#(Id_UV1,2) 
b3dWriteFloat( u1# ) ; U1 
b3dWriteFloat( v1# ) ; V1 
b3dWriteFloat( w1# ) ; W1 
;===> 
Next 
Next 
b3dEndChunk() ;end of VRTS chunk 

b3dBeginChunk( "TRIS" ) 
b3dWriteInt( UVW_for_XYZ ) ;brush for these triangles 

For j=0 To n_tris 
;===> 
Tris_v = j*3 
t1 = Tris_v+0 
t2 = Tris_v+1 
t3 = Tris_v+2 
b3dWriteInt( t1 ); X 
b3dWriteInt( t2 ); Y 
b3dWriteInt( t3 ); Z 
;===> 
Next 

b3dEndChunk() ;end of TRIS chunk 

b3dEndChunk() ;end of MESH chunk 

End Function
