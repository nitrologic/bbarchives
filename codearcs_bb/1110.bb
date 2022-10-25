; ID: 1110
; Author: jfk EO-11110
; Date: 2004-07-17 17:25:56
; Title: Underwater-ish Screen Distortion
; Description: A simple sinus curved screen distortion FX

;Underwater FX by jfk of CSP, please mention this in the credits.
Graphics3D 640,480,32,1
SetBuffer BackBuffer()


camera=CreateCamera()
TranslateEntity camera,0,0,-12
; probably use some fog...
CameraFogMode camera,1
CameraFogColor camera,50,100,150
CameraFogRange camera,-100,200
CameraClsColor camera,50,100,150



; used by screen sinus-distortion fx:
Global screenbk=CreateImage(GraphicsWidth(),GraphicsHeight())
Global underw_a


; some dummy content...
n=100
Dim o(n)
For i=0 To n
 o(i)=CreateCube()
 PositionEntity o(i),Rnd(-10,10),Rnd(-10,10),Rnd(-10,10)
 RotateEntity o(i),Rand(360),Rand(360),Rand(360)
 EntityColor o(i),Rand(255),Rand(255),Rand(255)
Next


While Not KeyDown(1)
 RenderWorld()
 CaptureScreen()
 WobbleView()
 VWait
 Flip 0
Wend
End


Function capturescreen()
 CopyRect 0,0,GraphicsWidth(),GraphicsHeight(),0,0,BackBuffer(),ImageBuffer(screenbk)
End Function

Function WobbleView()
 gw#=GraphicsWidth()
 gh#=GraphicsHeight()
 underw_a=(underw_a+4)
 steph#=gh/32
 mu8#=gh/60
 If underw_a>359 Then underw_a=0
  For iif#=0 To gh-4  Step .001
   wsin#=(Sin((underw_a+iif)Mod 360.0)*mu8#)
   CopyRect 0,  iif,         gw,steph+4, 0,iif+wsin#, ImageBuffer(screenbk),BackBuffer()
   iif=iif+steph
  Next
End Function
