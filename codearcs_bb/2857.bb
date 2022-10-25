; ID: 2857
; Author: Kenshin Yurihara
; Date: 2011-06-05 09:04:46
; Title: 2D Tile Mapper
; Description: Public Domain tile mapper for your use.

Type Tile
Field X#
Field Y#
Field Frame#
Field Layer#
End Type 

Type BGTile 
Field X#
Field Y#
Field Frame#
End Type 

Type SpawnPoint  
Field X#
Field Y#
Field SPN ; Spawn Point Number :D
End Type 

Type Entity 
Field X#
Field Y#
Field Name$
Field Frame
Field Layer
End Type 

Graphics 800,608,32,2
SetBuffer BackBuffer()



TileSheet = LoadAnimImage("TileSheet.bmp",16,16,0,2)
CamImg = LoadImage("CamImg.bmp")
MaskImage(CamImg,255,0,220)
MaskImage(TileSheet,255,0,220) 


CurLayer = 1

CX# = 0
CY# = 0

CurX = 0
CurY = 0
IsTyping = False

SelTile = 0
EditMode = 0
MovX = 0
MovY = 0
MaxTiles = 0
MaxBGTiles = 0
MaxSpawnPoints = 0
MaxEnts = 0

FileName = "MAPITH!.Map"
While Not KeyHit(1) ; Program loop 

;SAVING AND OMFG LOADING!;
If KeyHit(26) ; Saving 
;IsTyping = True 

file = WriteFile(FileName)
WriteInt (file, MaxTiles)

For T.Tile = Each tile 
WriteInt (file, T\X)
WriteInt (file, T\Y)
WriteInt (file, T\Frame)
WriteInt (file, T\Layer)
Next 

WriteInt (file, MaxBGTiles) 

For B.BGTile = Each BGTile
WriteInt (file, B\X)
WriteInt (file, B\Y)
WriteInt (file, B\Frame) 
Next  

WriteInt (file, MaxSpawnPoints)

For S.SpawnPoint = Each SpawnPoint
WriteInt (file, S\X)
WriteInt (file, S\Y)
WriteInt (file, S\SPN)
Next 

WriteInt (file, MaxEnts)

For E.Entity = Each Entity 
WriteInt (file, E\X)
WriteInt (file, E\Y)
WriteString (file, E\Name)
WriteInt (file, Frame)
WriteInt (file, Layer) 
Next 

CloseFile (file)
EndIf 


If KeyHit(27) ; Loading 
file = ReadFile(FileName)
Ts = ReadInt(file)

For i = 0 To Ts - 1
T.Tile = New Tile 
T\X = ReadInt(file)
T\Y = ReadInt(file)
T\Frame = ReadInt(file)
T\Layer = ReadInt(file)
Next 

BTS = ReadInt (file)

For i2 = 0 To BTS - 1
B.BGTile = New BGTile
B\X = ReadInt(file)
B\Y = ReadInt(file)
B\Frame = ReadInt(file)
Next  

SPs = ReadInt(file)

For i3 = 0 To SPs - 1
S.SpawnPoint = New SpawnPoint 
S\X = ReadInt(file) 
S\Y = ReadInt(file)
S\SPN = ReadInt(file)
Next 

ETs = ReadInt(file)

For i4 = 0 To ETs - 1
E.Entity = New Entity 
E\X = ReadInt(file)
E\Y = ReadInt(file)
E\Name = ReadString(file)
E\Frame = ReadInt(file)
E\Layer = ReadInt(file)
Next 

CloseFile (file)
EndIf 


;Render
ClsColor 0,0,100
Cls 

If IsTyping = True 

If KeyHit(28)
IsTyping = False
EndIf 

EndIf 

If IsTyping = False 
;Mov(e)X and Mov(e)Y
If KeyHit(2)
MovX = CurX 
MovY = CurY 
EndIf 


;Layer 
If KeyHit(23)
CurLayer = CurLayer - 1
EndIf 

If KeyHit(24)
CurLayer = CurLayer + 1
EndIf 

;Edit Mode
If KeyHit(37)
EditMode  = EditMode - 1
EndIf 

If KeyHit(38)
EditMode = EditMode + 1
EndIf 

If EditMode < 0
EditMode = 0 
EndIf 

If EditMode > 3
EditMode = 3
EndIf 

;End Edit Mode 

If KeyDown(35) ;Help Button
Text 0,0, "Edit Mode:" + EditMode
Text 0,12, "Tile Frame:" + SelTile 
Text 0,24, "Current Layer:" + CurLayer
Text 0,36, "Camera X:" + CX#
Text 0,48, "Camera Y:" + CY#
Text 0,60, "Controls:"
Text 0,72, "W - Scroll UP"
Text 0,84, "S - Scroll Down"
Text 0,96, "A - Scroll Left"
Text 0,108, "D - Scroll Right"
Text 0,120, "UP Key - Move Tile Up"
Text 0,132, "Down Key - Move Tile Down"
Text 0,144, "Left Key - Move Tile Left"
Text 0,156, "Right Key - Move Tile Right"
Text 0,168, "< - Decrease SelTile"
Text 0,180, "> - Increase SelTile" 
Text 0,192, "K - Decrease Edit Mode"
Text 0,204, "L - Increase Edit Mode"
Text 0,216, "Entier - Place Tile/Entity/BG Tile/SpawnPoint"
Text 0,228, "Shift - Delete Tile/Entity/BG Tile/SpawnPoint"
Text 0,240, "Move X:" + MovX
Text 0,256, "Move Y:" + MovY
Text 0,268, "Cur X:" + CurX
Text 0,280, "Cur Y:" + CurY 
Text 0,292, "Tiles:" + MaxTiles 
Text 0,304, "BGTiles:" + MaxBGTiles 
Text 0,316, "SpawnPoints:" + MaxSpawnPoints 
Text 0,328, "Entites:" + MaxEnts 
EndIf 

;Seletected tile
If KeyHit(51)
SelTile = SelTile - 1
EndIf 

If KeyHit(52)
SelTile = SelTile + 1
EndIf 

If SelTile > 5 Then 
SelTile = 0
EndIf 

If SelTile < 0 Then
SelTile = 5
EndIf 
;End Selected Tile

;Program Clean UP
If KeyHit(1)

For T.Tile = Each Tile
Delete T
Next 

For B.BGTile = Each BGTile
Delete B
Next 

For S.SpawnPoint = Each SpawnPoint
Delete S
Next 

For E.Entity = Each Entity
Delete E
Next 

FreeImage(CamImg)
FreeImage(TileSheet)

EndIf 
;End Program Clean Up

;Creation
If KeyHit(28)

If EditMode = 0

For T.Tile = Each Tile 

If T\X = CurX + CX# And T\Y = CurY + CY#
Delete T
MaxTiles = MaxTiles - 1
EndIf 
Next 

T.Tile = New Tile
T\X = CurX + CX#
T\Y = CurY + CY#
T\Frame = SelTile
T\Layer = CurLayer 

MaxTiles = MaxTiles + 1
EndIf 

If EditMode = 1

For B.BGTile = Each BGTile 

If B\X = CurX And B\Y = CurY
Delete B
MaxBGTiles = MaxBGTiles - 1
EndIf 
Next 

B.BGTile = New BGTile
B\X = CurX + CX#
B\Y = CurY + CY#
B\Frame = SelTile


MaxBGTiles = MaxBGTiles + 1
EndIf 

If EditMode = 2

For S.SpawnPoint = Each SpawnPoint
If S\X = CurX And S\Y = CurY
Delete S
MaxSpawnPoints = MaxSpawnPoints - 1
EndIf 
Next 

S.SpawnPoint = New SpawnPoint
S\X = CurX + CX#
S\Y = CurY + CY#
S\SPN = CurLayer 

MaxSpawnPoints = MaxSpawnPoints + 1
EndIf 

If EditMode = 3

For E.Entity = Each Entity 

If E\X = CurX And E\Y = CurY
Delete B
MaxEnts = MaxEnts - 1
EndIf 
Next 

E.Entity = New Entity 
E\X = CurX + CX#
E\Y = CurY + CY#
E\Layer = CurLayer
E\Frame = SelTile
IsTyping = True 
E\Name = Input("Enter Entity Name:")


MaxEnts = MaxEnts + 1
EndIf 



EndIf ;Total End Creation


;Destruction 
If KeyHit(54)

;For Loop
For T.Tile = Each Tile 

If T\X = CurX And T\Y = CurY
Delete T
MaxTiles = MaxTiles - 1
EndIf 

Next 
;End Foor Loop 

For B.BGTile = Each BGTile 

If B\X = CurX And B\Y = CurY
Delete B
MaxBGTiles = MaxBGTiles - 1
EndIf 
Next 

For S.SpawnPoint = Each SpawnPoint
If S\X = CurX And S\Y = CurY
Delete S
MaxSpawnPoints = MaxSpawnPoints - 1
EndIf 
Next 


For E.Entity = Each Entity 

If E\X = CurX And E\Y = CurY
Delete B
MaxEnts = MaxEnts - 1
EndIf 
Next 

EndIf 

;End Destruction 

If KeyHit(200) ; Move Up
CurY = CurY - 16
EndIf 

If KeyHit(208) ; Move Down
CurY = CurY + 16
EndIf 

If KeyHit(203) ; Move Left
CurX = CurX - 16
EndIf 

If KeyHit(205) ; Move Right
CurX = CurX + 16
EndIf 


If KeyHit(17) ; Scroll UP :D
CY# = CY# - 16
EndIf 

If KeyHit(31) ;Scroll Down :D
CY# = CY# + 16
EndIf  

If KeyHit(30) ; Scroll Left
CX# = CX# - 16
EndIf 

If KeyHit(32) ; Scroll Right
CX# = CX# + 16
EndIf 


EndIf ;End if isTyping = false 

;Tile Drawing
For T.Tile = Each Tile

If ImagesOverlap(TileSheet,T\X, T\Y,CamImg,CX#,CY#)
DrawImage TileSheet, Ceil((Abs(T\X - CX#) / 16) * 16), Ceil((Abs(T\Y - CY#) / 16) * 16), T\Frame#
EndIf 

Next 

For B.BGTile = Each BGTile

If ImagesOverlap(TileSheet,B\X, B\Y,CamImg,CX#,CY#)
DrawImage TileSheet, Ceil((Abs(B\X - CX#) / 16) * 16), Ceil((Abs(B\Y - CY#) / 16) * 16), B\Frame#
EndIf 

Next 

For S.SpawnPoint = Each SpawnPoint 

If ImagesOverlap(TileSheet,S\X, S\Y,CamImg,CX#,CY#)
Oval Ceil((Abs(S\X - CX#) / 16) * 16), Ceil((Abs(S\Y - CY#) / 16) * 16),16,16,1
EndIf 

Next 


For E.Entity = Each Entity

If ImagesOverlap(TileSheet,E\X, E\Y,CamImg,CX#,CY#)
DrawImage TileSheet, Ceil((Abs(E\X - CX#) / 16) * 16), Ceil((Abs(E\Y - CY#) / 16) * 16), E\Frame#
Text E\X, E\Y + 5, "Name:" + E\Name 
EndIf 

Next 

;End Tile Drawing 

If EditMode <> 2
DrawImage TileSheet, CurX, CurY, SelTile

Else 
Oval CurX,CurY,16,16,1
EndIf 
DrawImage CamImg, CX#, CY# 
Flip 
;End Render 

Wend ;End Program Loop
