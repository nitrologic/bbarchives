; ID: 618
; Author: jfk EO-11110
; Date: 2003-03-13 15:43:15
; Title: Bit 256 Check
; Description: Check if the Hardware supports Texture Flag 256

Graphics3D 640,480,16,2
SetBuffer BackBuffer()

; This App will check if the Hardware supports the Texture Flag Bit 256
; (used for faster Texture Manipulations)

; you only need these Globals when you want to display the results...
Global tdif1,tdif2,checksum1,checksum2
If SupportsBit256("space.jpg")
 Color 255,255,255
 Print "Your Machine seems to support VRam resident Texture Operations properly"
 Print "if CopyRect from Backbuffer to that Texturebuffer is used as well"
Else
 Color 255,255,255
 Print "Your Machine don't support VRam resident Texture Ops properly"
 Print "if CopyRect from Backbuffer to that Texturebuffer is used as well"
EndIf
Print
Print "Time without Bit256: "+tdif1+" ms"
Print "Time with Bit256: "+tdif2+" ms"
Print "Checksum without: "+checksum1
Print "Checksum with: "+checksum2
Print "Press any key to continue"
WaitKey()

;__________________________________________________________________________________
Cls
Flip
Cls

If SupportsBit256noBackBuffer("space.jpg")
 Color 255,255,255
 Print "Your Machine seems to support VRam resident Texture Operations properly"
 Print "if CopyRect from Backbuffer to that Texturebuffer is NOT used!"
Else
 Color 255,255,255
 Print "Your Machine don't support VRam resident Texture Ops properly"
 Print "if CopyRect from Backbuffer to that Texturebuffer is NOT used!"
EndIf
Print
Print "Time without Bit256: "+tdif1+" ms"
Print "Time with Bit256: "+tdif2+" ms"
Print "Checksum without: "+checksum1
Print "Checksum with: "+checksum2
Print "Press any key to exit"
WaitKey()

End

;__________________________________________________________________________________

Function SupportsBit256(test$)
 Color 25,25,0
 Text 0,0,"Testing Vram"
 tex1=LoadTexture(test$)
 If tex1=0 Then RuntimeError "Bit256 Test-Texture "+test$+" not found!"
 w=TextureWidth(tex1)
 h=TextureHeight(tex1)
 tex2=CreateTexture(w,h)
 CopyRect 0,0,w,h,0,0,TextureBuffer(tex1),TextureBuffer(tex2)
 FreeTexture tex1
 tex3=LoadTexture(test$,256)
 Color 255,0,255
 SetBuffer TextureBuffer(tex2)
   Text 0,0,"Howdy"
   tt1=MilliSecs()
   For i=0 To 100
    CopyRect 0,0,w,h,0,0,BackBuffer(),TextureBuffer(tex2)
    Text 0,0,"Howdy"
   Next
   tt2=MilliSecs()
   LockBuffer()
   checksum1=0
   For j=0 To h-1
    For i=0 To w-1
     checksum1=(checksum1+(ReadPixelFast(i,j)And $FFFFFF))And $FFFFFFF
    Next
   Next
   UnlockBuffer()
 SetBuffer BackBuffer()
 tdif1=tt2-tt1
 SetBuffer TextureBuffer(tex3)
   tt1=MilliSecs()
   For i=0 To 100
    CopyRect 0,0,w,h,0,0,BackBuffer(),TextureBuffer(tex3)
    Text 0,0,"Howdy"
   Next
   tt2=MilliSecs()
   LockBuffer()
   checksum2=0
   For j=0 To h-1
    For i=0 To w-1
     checksum2=(checksum2+(ReadPixelFast(i,j)And $FFFFFF))And $FFFFFFF
    Next
   Next
   UnlockBuffer()
 SetBuffer BackBuffer()
 tdif2=tt2-tt1
 FreeTexture tex2
 FreeTexture tex3
 If (tdif2>tdif1) Or (checksum2<>checksum1)
  Return False
 Else
  Return True
 EndIf
End Function


;__________________________________________________________________________________


Function SupportsBit256noBackBuffer(test$)
 Color 25,25,0
 Text 0,0,"Testing Vram"
 tex1=LoadTexture(test$)
 If tex1=0 Then RuntimeError "Bit256 Test-Texture "+test$+" not found!"
 w=TextureWidth(tex1)
 h=TextureHeight(tex1)
 tex2=CreateTexture(w,h)
 CopyRect 0,0,w,h,0,0,TextureBuffer(tex1),TextureBuffer(tex2)
 FreeTexture tex1
 tex3=LoadTexture(test$,256)
 Color 255,0,255
 SetBuffer TextureBuffer(tex2)
   Text 0,0,"Howdy"
   tt1=MilliSecs()
   For i=0 To 1000
    Text 0,0,"Howdy"
   Next
   tt2=MilliSecs()
   LockBuffer()
   checksum1=0
   For j=0 To h-1
    For i=0 To w-1
     checksum1=(checksum1+(ReadPixelFast(i,j)And $FFFFFF))And $FFFFFFF
    Next
   Next
   UnlockBuffer()
 SetBuffer BackBuffer()
 tdif1=tt2-tt1
 SetBuffer TextureBuffer(tex3)
   tt1=MilliSecs()
   For i=0 To 1000
    Text 0,0,"Howdy"
   Next
   tt2=MilliSecs()
   LockBuffer()
   checksum2=0
   For j=0 To h-1
    For i=0 To w-1
     checksum2=(checksum2+(ReadPixelFast(i,j)And $FFFFFF))And $FFFFFFF
    Next
   Next
   UnlockBuffer()
 SetBuffer BackBuffer()
 tdif2=tt2-tt1
 FreeTexture tex2
 FreeTexture tex3
 If (tdif2>tdif1) Or (checksum2<>checksum1)
  Return False
 Else
  Return True
 EndIf
End Function
