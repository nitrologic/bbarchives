; ID: 3008
; Author: EsseEmmeErre
; Date: 2012-12-01 03:30:56
; Title: Pattern Image
; Description: draws dots to create a pattern!

;-> PatternImage by Stefano Maria Regattin
;i> 1 Dec 2012
;--------------
Const BorderSize=8
Const EmptyShape=0
Const EscKey=27
Const ImageSize=64
Const LeftMouseButton=1
Const RightMouseButton=2
Global WindowSize=BorderSize*2+ImageSize*3
AppTitle("PatternImage public by Stefano Maria Regattin","Press RMB or Esc to leave.")
Graphics(WindowSize,WindowSize,0,2)
For Fading=0 To BorderSize-1
 Color(63+Fading*4,63+Fading*4,63+Fading*4)
 Rect(Fading,Fading,GraphicsWidth()-Fading*2,GraphicsHeight()-Fading*2,EmptyShape)
Next
Color(255,255,255)
EndOfTheProgram=False
Repeat
 KeyPressed=GetKey()
 If KeyPressed=EscKey Then EndOfTheProgram=True
 If MouseDown(RightMouseButton)=True Then EndOfTheProgram=True
 If MouseDown(LeftMouseButton)=True Then
  MouseXPos=MouseX():MouseXPos=MouseY()
  If MouseXPos>=BorderSize And MouseXPos<WindowSize-BorderSize Then
   If MouseYPos>=BorderSize And MouseYPos<WindowSize-BorderSize Then
    PointX=(MouseXPos-BorderSize) Mod ImageSize
    PointY=(MouseYPos-BorderSize) Mod ImageSize
    For ImageCopyY=0 To 2
     For ImageCopyX=0 To 2
      ImageCopyXPos=ImageSize*ImageCopyX
      ImageCopyYPos=ImageSize*ImageCopyY
      Plot(PointX+ImageCopyXPos+BorderSize,PointY+ImageCopyY+BorderSize)
     Next
    Next
   EndIf
  EndIf
 EndIf
Until EndOfTheProgram=True
EndGraphics()
End
