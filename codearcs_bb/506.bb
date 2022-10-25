; ID: 506
; Author: Malice
; Date: 2002-11-24 13:38:38
; Title: .X Viewer
; Description: View .x files

ClearWorld

Graphics3D 1024,768,32,2
SetBuffer BackBuffer()
.go
Cls
a$=" "
dir=ReadDir(CurrentDir$())
Locate 0,0
While a$>""
Color 255,255,255
a$=NextFile$(dir)
If Len (a$)>3
If (Right$(a$,2))=".X" Or (Right$(a$,2))=".x" Then a$=(Left$(a$,(Len(a$)-2))) Print a$
End If
Wend

Locate 0,700
Color 255,0,0
file$=Input$("INPUT MESHNAME To VIEW: ")

Cls
a$=" "
dir=ReadDir(CurrentDir$())
Locate 0,0

While a$>""
Color 255,255,255
a$=NextFile$(dir)
If Len (a$)>4
If (Right$(a$,4))=".bmp" Or (Right$(a$,4))=".BMP" Then a$=(Left$(a$,(Len(a$)-4))) Print a$ 
EndIf

Wend

Locate 0,720
Color 255,0,0
txt$=Input$ ("INPUT TEXTURE: ")

Cls
AmbientLight 255,255,255

mesh=LoadMesh(file$+".X")
texture=LoadTexture(txt$+".BMP",49)
TextureBlend texture,2

EntityTexture mesh,texture

cam=CreateCamera()
CameraViewport cam,0,0,1024,768
MoveEntity cam, 120,120,120
MoveEntity mesh, 40,120,40
PointEntity cam,mesh

MoveMouse 512,360

While Not KeyDown(1)

If MouseX()<512 Then MoveMouse 512,MouseY() TurnEntity mesh,0,10,0
If MouseX()>512 Then MoveMouse 512,MouseY() TurnEntity mesh,0,-10,0

If MouseY()<360 Then MoveMouse MouseX(),360 TurnEntity mesh,-10,0,0
If MouseY()>360 Then MoveMouse MouseX(),360 TurnEntity mesh,10,0,0

If MouseDown(1) Then MoveEntity cam,0,0,1
If MouseDown(2) Then MoveEntity cam,0,0,-1

RenderWorld
Flip

Wend
ClearWorld
Goto go

End
