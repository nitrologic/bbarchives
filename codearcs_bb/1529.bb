; ID: 1529
; Author: markcw
; Date: 2005-11-10 15:12:22
; Title: Switch Graphics3d modes
; Description: Switch resolution, colour and window modes

;Switch Graphics3d modes example, on 10/11/05

;Globals
Global width=640,height=480,depth=16,window=2
Global camera,light,cube,tex

InitWindow(width,height,depth,window) ;First init

;Main Loop
While Not KeyDown(1)

 p#=p+0.25 : RotateEntity cube,p,p,p ;update cube

 SwitchWindow()
 SwitchResolution()
 SwitchColours()

RenderWorld ;set backbuffer

 Text 8,8,"Press W for window: "+window
 Text 8,24,"Press R for resolution: "+width+"x"+height
 Text 8,40,"Press C for colours: "+depth+" bit"

Flip ;switch to frontbuffer
Wend
End

;Functions
Function InitWindow(wd,ht,dp,win) ;Create 3d window, entities, etc.

Graphics3D wd,ht,dp,win ;Set 3d graphics mode
SetBuffer BackBuffer()

camera=CreateCamera() ;Create camera and light
light=CreateLight()
RotateEntity light,90,0,0

cube=CreateCube() ;Create cube
PositionEntity cube,0,0,5

tex=CreateTexture(256,256) ;Create texture
SetBuffer TextureBuffer(tex) ;Set buffer - texture buffer
 ClsColor 255,255,255 : Cls ;Clear buffer to grey
 Color 0,0,0 : Text 8,16,"This texture"
 Text 8,32,"was created using"
 Color 0,0,255 : Text 8,48,"CreateTexture()"
 Color 0,0,0 : Text 8,64,"and drawn to using"
 Color 0,0,255 : Text 8,80,"SetBuffer TextureBuffer()"
 Color 255,255,255 ;reset text to white
SetBuffer BackBuffer() ;Set buffer - backbuffer
EntityTexture cube,tex ;Texture cube

End Function

Function SwitchWindow() ;Switch window states with W key

If KeyDown(17) ;if W key
 If k=0
  k=1
  window=window+1
  If window>2 Then window=1 ;set full or windowed modes
  EndGraphics ;return to non-graphics state
  InitWindow(width,height,depth,window) ;Next init
 EndIf
Else
 k=0
EndIf

End Function

Function SwitchResolution() ;Switch resolution with R key

If KeyDown(19) ;if R key
 If k=0
  k=1
  If width=640 ;set 640x480 or 800x600 modes
   width=800 : height=600
  Else
   width=640 : height=480
  EndIf
  EndGraphics ;return to non-graphics state
  InitWindow(width,height,depth,window) ;next init
 EndIf
Else
 k=0
EndIf

End Function

Function SwitchColours() ;Switch colour depth with C key

If KeyDown(46) ;if C key
 If k=0
  k=1
  If depth=16 ;set 16 or 32 bit modes
   depth=32
  Else
   depth=16
  EndIf
  EndGraphics ;return to non-graphics state
  InitWindow(width,height,depth,window) ;next init
 EndIf
Else
 k=0
EndIf

End Function
