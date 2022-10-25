; ID: 3290
; Author: RemiD
; Date: 2016-10-08 06:04:10
; Title: write/read color, alpha, of each texel of a texture, with different commands
; Description: also an example to write/read color, maskcolor, of each pixel of an image, with different commands

;write/read color, alpha, of each texel of a texture, with different commands
;these are the functions that i use to write/read texels properties
;
;variables, lists, functions

Global PixAlpha% = 0
Global PixRed% = 0
Global PixGreen% = 0
Global PixBlue% = 0

Function SetPixColor(PX%,PY%,R%,G%,B%)
 Color(R,G,B)
 Plot(PX,PY)
End Function

Function GetPixColor(PX%,PY%)
 GetColor(PX,PY)
 PixRed = ColorRed()
 PixGreen = ColorGreen()
 PixBlue = ColorBlue()
End Function

Function WritePix(PX%,PY%,R%,G%,B%,A%=255)
 HexARGB = RGBAToHexARGB(R,G,B,A)
 WritePixel(PX,PY,HexARGB)
End Function

Function ReadPix(PX%,PY%)
 HexARGB% = ReadPixel(PX,PY)
 HexARGBToRGBA(HexARGB%)
End Function

Function WritePixFast(PX%,PY%,R%,G%,B%,A%=255)
 HexARGB = RGBAToHexARGB(R,G,B,A)
 WritePixelFast(PX,PY,HexARGB)
End Function

Function ReadPixFast(PX%,PY%)
 HexARGB% = ReadPixelFast(PX,PY)
 HexARGBToRGBA(HexARGB%)
End Function

Function RGBAToHexARGB%(R%,G%,B%,A%)
 HexARGB% = A Shl(24) + R Shl(16) + G Shl(8) + B Shl(0)
 Return HexARGB
End Function

Function HexARGBToRGBA(HexARGB%)
 PixAlpha = HexARGB Shr(24) And 255
 PixRed = HexARGB Shr(16) And 255
 PixGreen = HexARGB Shr(8) And 255
 PixBlue = HexARGB Shl(0) And 255
End Function



;demo
GPWidth% = 640
GPHeight% = 480
Graphics3D(GPWidth,GPHeight,32,2)

SeedRnd(MilliSecs())

Global Camera = CreateCamera()
CameraRange(Camera,0.001,100)
CameraClsColor(Camera,000,000,000)

WH% = GPWidth

;create a quad
Test_Mesh = CreateMesh()
Surface = CreateSurface(Test_Mesh)
AddVertex(Surface,-(8.0)/WH,(8.0)/WH,0.0,Float(0)/8,Float(0)/8)
AddVertex(Surface,(8.0)/WH,(8.0)/WH,0.0,Float(8)/8,Float(0)/8)
AddVertex(Surface,-(8.0)/WH,-(8.0)/WH,0.0,Float(0)/8,Float(8)/8)
AddVertex(Surface,(8.0)/WH,-(8.0)/WH,0.0,Float(8)/8,Float(8)/8)
AddTriangle(Surface,0,1,2)
AddTriangle(Surface,2,1,3)
UpdateNormals(Test_Mesh)
EntityColor(Test_Mesh,255,255,255)
EntityFX(Test_Mesh,1)
EntityBlend(Test_Mesh,1)
;MoveEntity(Test_Mesh,0,0,1.0)
MoveEntity(Test_Mesh,0,0,0.1)

;create a texture
Test_Texture = CreateTexture(8,8,1+2+256)
TextureBlend(Test_Texture,1)
SetBuffer(TextureBuffer(Test_Texture))
ClsColor(128,128,128)
Cls()
Color(255,000,000) : Plot(0,0)
Color(000,255,000) : Plot(8-1,0)
Color(000,000,255) : Plot(0,8-1)
Color(255,255,000) : Plot(8-1,8-1)

EntityTexture(Test_Mesh,Test_Texture)

SetBuffer(BackBuffer())
ClsColor(000,000,000)
Cls()
CameraClsColor(Camera,255,000,255)
RenderWorld()
Flip()
WaitKey()



;write/read the colors/alpha of the texels on the texture :



;using setpixcolor / getpixcolor ( color+plot / getcolor+colorred+colorgreen+colorblue )
DebugLog("")
SetBuffer(TextureBuffer(Test_Texture))
For PX% = 0 To 8-1 Step 1
 For PY% = 0 To 8-1 Step 1
  R% = Rand(025,255)
  G% = 000
  B% = 000
  ;DebugLog("Texel at "+PX+","+PY+" is colored in "+R+","+G+","+B)
  SetPixColor(PX,PY,R,G,B)
 Next
Next

DebugLog("")
SetBuffer(TextureBuffer(Test_Texture))
For PX% = 0 To 8-1 Step 1
 For PY% = 0 To 8-1 Step 1
  GetPixColor(PX,PY)
  R% = PixRed
  G% = PixGreen
  B% = PixBlue
  ;DebugLog("Texel at "+PX+","+PY+" is colored in "+R+","+G+","+B)
 Next
Next

SetBuffer(BackBuffer())
ClsColor(000,000,000)
Cls()
CameraClsColor(Camera,255,000,255)
RenderWorld()
Flip()
WaitKey()



;using writepix / readpix ( writepixel / readpixel )
DebugLog("")
SetBuffer(TextureBuffer(Test_Texture))
For PX% = 0 To 8-1 Step 1
 For PY% = 0 To 8-1 Step 1
  R% = 000
  G% = Rand(025,255)
  B% = 000
  A% = Rand(000,255)
  ;DebugLog("Texel at "+PX+","+PY+" is colored in "+R+","+G+","+B+" and has an alpha of "+A)
  WritePix(PX,PY,R,G,B,A)
 Next
Next

DebugLog("")
SetBuffer(TextureBuffer(Test_Texture))
For PX% = 0 To 8-1 Step 1
 For PY% = 0 To 8-1 Step 1
  ReadPix(PX,PY)
  R% = PixRed
  G% = PixGreen
  B% = PixBlue
  A% = PixAlpha
  ;DebugLog("Texel at "+PX+","+PY+" is colored in "+R+","+G+","+B+" and has an alpha of "+A)
 Next
Next

SetBuffer(BackBuffer())
ClsColor(000,000,000)
Cls()
CameraClsColor(Camera,255,000,255)
RenderWorld()
Flip()
WaitKey()



;using writepixfast / readpixfast ( writepixelfast / readpixelfast )
DebugLog("")
SetBuffer(TextureBuffer(Test_Texture))
LockBuffer(TextureBuffer(Test_Texture))
For PX% = 0 To 8-1 Step 1
 For PY% = 0 To 8-1 Step 1
  R% = 000
  G% = 000
  B% = Rand(025,255)
  A% = Rand(000,255)
  ;DebugLog("Texel at "+PX+","+PY+" is colored in "+R+","+G+","+B+" and has an alpha of "+A)
  WritePix(PX,PY,R,G,B,A)
 Next
Next
UnlockBuffer(TextureBuffer(Test_Texture))

DebugLog("")
SetBuffer(TextureBuffer(Test_Texture))
LockBuffer(TextureBuffer(Test_Texture))
For PX% = 0 To 8-1 Step 1
 For PY% = 0 To 8-1 Step 1
  ReadPixFast(PX,PY)
  R% = PixRed
  G% = PixGreen
  B% = PixBlue
  A% = PixAlpha
  ;DebugLog("Texel at "+PX+","+PY+" is colored in "+R+","+G+","+B+" and has an alpha of "+A)
 Next
Next
UnlockBuffer(TextureBuffer(Test_Texture))

SetBuffer(BackBuffer())
ClsColor(000,000,000)
Cls()
CameraClsColor(Camera,255,000,255)
RenderWorld()
Flip()
WaitKey()



End()
