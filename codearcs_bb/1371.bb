; ID: 1371
; Author: WarpZone
; Date: 2005-05-11 23:38:21
; Title: 5 simple overlay effects
; Description: Additive, Multiply, Difference, Lighten, Darken.

Graphics3D 800,600,16,0
camera=CreateCamera()

Global Target= CreateImage(64,64)
Global Source1= LoadImage("tex3.bmp")
Global Source2= LoadImage("tex4.bmp")

LightenBlend(Source1,Source2,Target)

Repeat
Mil1=MilliSecs() 
UpdateWorld
RenderWorld
SetBuffer BackBuffer()
CopyRect 0,0,64,64,0,0,ImageBuffer(Target),BackBuffer() 
Mil2= MilliSecs() 
Color 255,255,0
Text 4,500,"Render time: "+(Mil2-Mil1)
Flip
Until KeyDown(1)

Function AddBlend(SourceTexture1,SourceTexture2,TargetTexture)
  For x = 0 To 64
    For y = 0 To 64
      SetBuffer ImageBuffer(SourceTexture1)
      GetColor x,y
      r1#=ColorRed()
      g1#=ColorGreen()
      b1#=ColorBlue()
      SetBuffer ImageBuffer(SourceTexture2)
      GetColor x,y
      r2#=ColorRed()
      g2#=ColorGreen()
      b2#=ColorBlue()
      SetBuffer ImageBuffer(TargetTexture)
      sumr=r1+r2
      sumg=g1+g2
      sumb=b1+b2
      If sumr>255 Then sumr=255
      If sumg>255 Then sumg=255
      If sumb>255 Then sumb=255
      Color sumr,sumg,sumb
      Plot x,y
    Next
  Next
End Function

Function MultiplyBlend(SourceTexture1,SourceTexture2,TargetTexture)
  For x = 0 To 64
    For y = 0 To 64
      SetBuffer ImageBuffer(SourceTexture1)
      GetColor x,y
      r1#=ColorRed()
      g1#=ColorGreen()
      b1#=ColorBlue()
      SetBuffer ImageBuffer(SourceTexture2)
      GetColor x,y
      r2#=ColorRed()
      g2#=ColorGreen()
      b2#=ColorBlue()
      SetBuffer ImageBuffer(TargetTexture)
      sumr=r1*r2/256
      sumg=g1*g2/256
      sumb=b1*b2/256
      Color sumr,sumg,sumb
      Plot x,y
    Next
  Next
End Function

Function DifferenceBlend(SourceTexture1,SourceTexture2,TargetTexture)
  For x = 0 To 64
    For y = 0 To 64
      SetBuffer ImageBuffer(SourceTexture1)
      GetColor x,y
      r1#=ColorRed()
      g1#=ColorGreen()
      b1#=ColorBlue()
      SetBuffer ImageBuffer(SourceTexture2)
      GetColor x,y
      r2#=ColorRed()
      g2#=ColorGreen()
      b2#=ColorBlue()
      SetBuffer ImageBuffer(TargetTexture)
      If r1>r2 Then sumr=r1-r2 Else sumr=r2-r1
      If g1>g2 Then sumg=g1-g2 Else sumg=g2-g1
      If b1>b2 Then sumb=b1-b2 Else sumb=b2-b1
      Color sumr,sumg,sumb
      Plot x,y
    Next
  Next
End Function

Function LightenBlend(SourceTexture1,SourceTexture2,TargetTexture)
  For x = 0 To 64
    For y = 0 To 64
      SetBuffer ImageBuffer(SourceTexture1)
      GetColor x,y
      r1#=ColorRed()
      g1#=ColorGreen()
      b1#=ColorBlue()
      SetBuffer ImageBuffer(SourceTexture2)
      GetColor x,y
      r2#=ColorRed()
      g2#=ColorGreen()
      b2#=ColorBlue()
      SetBuffer ImageBuffer(TargetTexture)
      If r1>r2 Then sumr=r1 Else sumr=r2
      If g1>g2 Then sumg=g1 Else sumg=g2
      If b1>b2 Then sumb=b1 Else sumb=b2
      Color sumr,sumg,sumb
      Plot x,y
    Next
  Next
End Function

Function DarkenBlend(SourceTexture1,SourceTexture2,TargetTexture)
  For x = 0 To 64
    For y = 0 To 64
      SetBuffer ImageBuffer(SourceTexture1)
      GetColor x,y
      r1#=ColorRed()
      g1#=ColorGreen()
      b1#=ColorBlue()
      SetBuffer ImageBuffer(SourceTexture2)
      GetColor x,y
      r2#=ColorRed()
      g2#=ColorGreen()
      b2#=ColorBlue()
      SetBuffer ImageBuffer(TargetTexture)
      If r1<r2 Then sumr=r1 Else sumr=r2
      If g1<g2 Then sumg=g1 Else sumg=g2
      If b1<b2 Then sumb=b1 Else sumb=b2
      Color sumr,sumg,sumb
      Plot x,y
    Next
  Next
End Function
