; ID: 896
; Author: jfk EO-11110
; Date: 2004-01-26 19:33:43
; Title: Textured voxel demo
; Description: A landscape rendered with voxel-like raycasting

; Voxel/Raycasting with heightmap, texture and "fog" - flight over landscape
; by CSP just4fun
Graphics 320,240,16,2
SetBuffer BackBuffer()

; can also use heightmap and texture files...
Global hm
hm=CreateImage(256,256)

Dim mount(1000,3)
For i=0 To 10
 mount(i,0)=Rand(0,256)
 mount(i,1)=Rand(0,256)
 mount(i,2)=Rand(30,127)
Next

Cls
Print "Creating Heightmap - please stand by"
Flip
SetBuffer ImageBuffer(hm)
For i=0 To 255
 For i2=0 To 10
  r=mount(i2,2)-i
  If r>0
   Color i+i,i+i,i+i
   Oval mount(i2,0)-r/2,mount(i2,1)-r/2,r,r,0
   Oval 1+mount(i2,0)-r/2,mount(i2,1)-r/2,r,r,0
   Oval mount(i2,0)-r/2,1+mount(i2,1)-r/2,r,r,0
  EndIf
 Next
Next
SetBuffer BackBuffer()

Cls
Print "Creating Terrain Texture - wait a second..."
Flip
Global mossy
mossy=CreateImage(256,256)

For i=0 To 1000
 mount(i,0)=Rand(0,256)
 mount(i,1)=Rand(0,256)
 mount(i,2)=Rand(0,20)
Next
SetBuffer ImageBuffer(mossy)

For i=0 To 50
 For i2=0 To 1000
  r=mount(i2,2)-i
  If r>0
   Color Rand(50)+i*4,Rand(50)+i*10,0
   Oval mount(i2,0)-r/2,mount(i2,1)-r/2,r,r,0
   Oval 1+mount(i2,0)-r/2,mount(i2,1)-r/2,r,r,0
   Oval mount(i2,0)-r/2,1+mount(i2,1)-r/2,r,r,0
  EndIf
 Next
Next

SetBuffer BackBuffer()

Global grw=GraphicsWidth()
Global grh=GraphicsHeight()
Global grwh=grw/2
Global grhh=grh/2
Global my
Global px#=8.0
Global pz#=8.0
Global a#

Color 127,127,127
MoveMouse grwh,0
;__________________________MAINLOOP_________________________
While KeyDown(1)=0
 Cls
 If KeyDown(200) Then ; up
  px=px+Sin(a)
  pz=pz+Cos(a)
 EndIf
 If KeyDown(208) Then ; down
  px=px-Sin(a)
  pz=pz-Cos(a)
 EndIf
 a=(a-mxs#) Mod 360 ; use mouse to steer
 raycast()
 ; Text 0,0, "x:"+px+ " z:"+pz
 Text 0,0,"Use Mouse + Arrows"
 Flip
 my=MouseY()
 If my>130 Then my=130
 mxs#=MouseXSpeed()/3.0 ; used by steer
 MoveMouse GraphicsWidth()/2,my
Wend
End
;________________________eo mainloop__________________________


Function raycast()
 For i=-grwh To grwh-1 Step 4
  row=grh+1
;  igrwh=i+grwh
  grwh_mi=grwh-i
  rayx#=px
  rayz#=pz
  stepx#=Sin(a+0.125*i)
  stepz#=Cos(a+0.125*i)
  count#=0

  LockBuffer BackBuffer()
  LockBuffer ImageBuffer(hm)
  LockBuffer ImageBuffer(mossy)
  While count<200 ; camerarange
   rayx#=rayx+stepx
   rayz#=rayz+stepz
   If rayx>-127 And rayx<127
    If rayz>-127 And rayz<127
     c#=ReadPixelFast(rayx+127,rayz+127,ImageBuffer(hm)) And $ff
   h#=((15000.0-my*100)/count)-((.1*my+30)*c)/count
   If h<row
    If c=0 Then 
     co=$9dd0 ; water
    Else
     ;co=((c Xor $ff)Shl 7)And $ff00 Or (c Shl 16)
     co=ReadPixelFast(rayx+127,rayz+127,ImageBuffer(mossy)) And $ffff Or (c Xor $FF)
    EndIf
    For ii= h To row-1
     x=grwh_mi
     y=ii
     If x>=0 And x<=grw And y>=0 And y<=grh
      WritePixelFast x,y,co,BackBuffer()
     EndIf
    Next
    row=h
   EndIf
   EndIf
   EndIf
   count=count+1
  Wend
  UnlockBuffer BackBuffer()
  UnlockBuffer ImageBuffer(hm)
  UnlockBuffer ImageBuffer(mossy)
 Next
End Function
