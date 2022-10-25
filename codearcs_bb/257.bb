; ID: 257
; Author: jfk EO-11110
; Date: 2002-03-02 03:51:46
; Title: Animated Flag
; Description: Flag with Screensaver Initialisation

; Realtime-Plasma-based Mesdeformation FLAG-SCREENSAVER Demo
; Compile to .EXE and rename to .SCR. Then copy to windows\system\
; Can be used with _any_ Flag-Picture. It will also run without a Picture.
; 2002 by CSP

;.................................... Screensaver-specific Initialisation:
; prevent to be started more than one instance when used as a Screensaver
cmd$=CommandLine$()
If Len(cmd$)>1
 For i=1 To Len(cmd$)-1
  mi$=Upper$(Mid$(cmd$,i,2))
  If mi$="/P" Or mi$="/C"
   ; is a Call For Screensaver settings. nothing to set tough...
   End
  EndIf
 Next
EndIf
cspfile$="csp_mutex.txt"
If FileType(cspfile$)=1
 csp=ReadFile(cspfile$)
 timestamp=ReadLine(csp) ; read last legal Instance Timestamp
 CloseFile csp
 timenow=MilliSecs()
 If Abs(timestamp-timenow)<1000 ; more than n secs ago?
  ; no, seems To be only a Mutant
  End
 Else
  ; yes, seems To be the first Instance, a new legal instance
  csp=WriteFile(cspfile$)
  WriteLine csp,Str$(MilliSecs()) ; update timestamp
  CloseFile csp
 EndIf
Else
 ; seems it runs first from this Computer/Folder
  csp=WriteFile(cspfile$)
  WriteLine csp,Str$(MilliSecs()) ; update timestamp
  CloseFile csp
EndIf
;...................eo Screensaver init

Graphics3D 800,600,16,1
SetBuffer BackBuffer()

; init plasma lookup table
Dim cosinus#(640)
For c = 0 To 640
   cosinus#(c) = Cos((115*3.14159265358 * c) / 320) * 32 + 32
Next 

;size of map
Const Gridx=32
Const Gridz=32

x#=0:y#=0:z#=0

SetBuffer BackBuffer()

lit=CreateLight()
AmbientLight 25,25,25
cam=CreateCamera()

CameraRange cam,.1,1000
PositionEntity cam,16,32,-10 
TurnEntity cam,30,0,0

; used for surface
Const Texture_Grid=4

; create Mesh
Dim map#(gridx,gridz)
mesh=Create_Map(gridx,gridz,b1,3)

; try to load a Flag Texture
flagname$="my_lovely_flag.bmp"
If FileType(flagname$)=1
 tex1=LoadTexture(flagname$,9) ; any texture for flag 
 tex2=LoadTexture(flagname$,9) ; any texture for Background
Else
 ; else create a placebo-flag on the fly
 tex1=CreateTexture(256,256,9)
 tex2=CreateTexture(256,256,9)
 TextureCoords tex1,1
 SetBuffer TextureBuffer(tex1)
 For i=0 To 255 Step 32 ; smileys'n'stripes :)
  Color 255,255,255
  Rect i,0,16,256,1
  Color 0,0,255
  Rect i+16,0,16,256,1
 Next
 Color 255,0,0
 Rect 0,0,127,127
 Color 255,255,255
 For j=0 To 127 Step 16
  For i=0 To 127 Step 16
   Color 255,255,255
   Oval i+5,j+4,8,8,1
   Color 255,0,0
   Oval i+2,j+4,8,8,1
   Color 255,255,255
   Oval i+4,j+5,2,2,1
   Oval i+4,j+9,2,2,1
  Next
 Next
 CopyRect 0,0,256,256,0,0,TextureBuffer(tex1),TextureBuffer(tex2)
 SetBuffer BackBuffer()
EndIf

EntityTexture mesh,tex1,0,1

; Background
in=CreateSphere() 
ScaleEntity in,100,100,100
FlipMesh in
EntityTexture in,tex2


; try to load a Sprite BMP
sparkfile$="bluespark.bmp"
If FileType(sparkfile$)=1
 spark=LoadTexture(sparkfile$,2)
Else
 ; else create it on the fly
 spark=CreateTexture(64,64,2)
 SetBuffer TextureBuffer(spark)
 For i=0 To 32
  r=i*8
  g=i*10
  b=i*i
  If r>255 Then r=255
  If g>255 Then g=255
  If b>255 Then b=255
  Color r,g,b
  Oval i,  i,  64-(i*2),64-(i*2),0
  Oval i+1,i,  64-(i*2),64-(i*2),0
 Next 
 LockBuffer
 For j=0 To 63
  For i=0 To 63
   argb=ReadPixelFast(i,j)
   r=(argb Shr 16) And $ff
   g=(argb Shr 8) And $ff
   b=argb And $ff
   a=((r+g+b)/3) Shl 24
   WritePixelFast i,j,(argb And $ffffff) Or a
  Next
 Next
 UnlockBuffer
 SetBuffer BackBuffer()
EndIf


; init Particles
spritemax=200 ; number of sprites
Dim sp(spritemax),spx#(spritemax),spy#(spritemax),spz#(spritemax),sps#(spritemax)
For i=0 To spritemax
 sp(i)=CreateSprite()
 EntityTexture sp(i),spark
 SpriteViewMode sp(i),1
 spx#(i)=16+Rnd(10)-5
 spy#(i)=27+Rnd(10)-5
 spz#(i)=16+Rnd(100)-50
 sps#(i)=1+Rnd(0,0.5)
 PositionEntity sp(i),spx#(i),spy#(i),spz#(i)
Next

	mx=MouseX()
	my=MouseY()
    mx2=mx
	my2=my
;___________________________________MAINLOOP__________________________________

While KeyDown(1)=0 And mx2=mx And my2=my
	mx2=MouseX()
	my2=MouseY()
	Gosub Update_Plasma
	mesh=Update_Map(mesh,gridx,gridz,b1,3)
	TurnEntity in,0,0,1
	TurnEntity cam,0,0,1.0

	;upate particles
	For i=0 To spritemax
	 spz#(i)=spz#(i)-sps#(i)
	 If spz#(i)<-30 Then
	  spz#(i)=16+100
	 EndIf
	 PositionEntity sp(i),spx#(i),spy#(i),spz#(i)
	Next
	
	UpdateWorld()
	RenderWorld
	Flip
Wend
;__________________________________eo mainloop________________________________

FreeEntity mesh
FreeEntity lit
FreeEntity cam
EndGraphics
End

Function Create_Map(tilex,tilez,brush1,tile)
.create_map
	mesh=CreateMesh()
	surf=CreateSurface(mesh,brush1)
	wid#=Float(1)/Float(Texture_grid)
	u0#=wid*Float(tile Mod texture_Grid)
	v0#=wid*Float(tile/texture_grid)
	u1#=u0+wid
	v1#=v0
	u2#=u1
	v2#=v0+wid
	u3#=u0
	v3#=v2
	u#=0
	v#=0
	stp#=1.0/Float(tilex)
	For z#=0 To tilez-1
		u=0
		For x#=0 To tilex-1
			h1#=map(x,z)
			h2#=map(x+1,z)
			h3#=map(x+1,z+1)
			h4#=map(x,z+1)
			AddVertex surf,x,h1,z,u0,v0
			VertexTexCoords surf,cnt,u,v,0,1
			
			AddVertex surf,x+1,h2,z,u1,v1
			VertexTexCoords surf,cnt+1,u+stp,v,0,1
			
			AddVertex surf,x+1,h3,z+1,u2,v2
			VertexTexCoords surf,cnt+2,u+stp,v+stp,0,1
			
			AddVertex surf,x,h4,z+1,u3,v3
			VertexTexCoords surf,cnt+3,u,v+stp,0,1
			
			AddTriangle surf,cnt,cnt+2,cnt+1
			AddTriangle surf,cnt,cnt+3,cnt+2
			cnt=cnt+4
			u=u+stp
		Next
		v=v+stp
	Next
	UpdateNormals mesh
	Return mesh
End Function


Function Update_Map(mesh,tilex,tilez,brush1,tile)
	surf=GetSurface(mesh,1)
	wid#=Float(1)/Float(Texture_grid)
	u0#=wid*Float(tile Mod texture_Grid)
	v0#=wid*Float(tile/texture_grid)
	u1#=u0+wid
	v1#=v0
	u2#=u1
	v2#=v0+wid
	u3#=u0
	v3#=v2
	u#=0
	v#=0
	stp#=1.0/Float(tilex)
	For z#=0 To tilez-1
		u=0
		For x#=0 To tilex-1
			h1#=map(x,z)
			h2#=map(x+1,z)
			h3#=map(x+1,z+1)
			h4#=map(x,z+1)
			VertexCoords surf,cnt,x,h1,z
			VertexCoords surf,cnt+1,x+1,h2,z
			VertexCoords surf,cnt+2,x+1,h3,z+1
			VertexCoords surf,cnt+3,x,h4,z+1
			cnt=cnt+4
			u=u+stp
		Next
		v=v+stp
	Next
	UpdateNormals mesh
	Return mesh
End Function

; this will animate kinda heightmap inside array 'map(,)'
.Update_Plasma
 wave1% = wave1% + 8
 If wave1% >= 320 Then wave1% = 0 
 wave2% = wave2% + 4
 If wave2% >= 320 Then wave2% = 0
  For yw = 0 To 329 Step 10
   y10=yw/10
   dw = cosinus#(yw + wave2) + cosinus#(Yw + wave2)
   For xw = 0 To 329 Step 10
    x10=xw/10
    map(x10,y10) = 1.0 + Abs(((cosinus#(xw + wave1) + cosinus#(xw + yw) + dw) / 16) )
   Next 
  Next
Return
