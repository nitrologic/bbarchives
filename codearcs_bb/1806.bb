; ID: 1806
; Author: jfk EO-11110
; Date: 2006-09-04 14:46:07
; Title: OptimizeAlphaChannel
; Description: An other solution for the black edges problem of masked textures

Graphics3D 640,480,32,2
SetBuffer BackBuffer()

;this array is used by the OptimizeAlphaChannel function!
Dim tex_memory(0,0)


; create a simple test scene:
camera=CreateCamera()
CameraClsColor camera,127,127,127
TranslateEntity camera,0,0,-2.5
light=CreateLight()
RotateEntity light,45,45,0

cube=CreateCube()
EntityFX cube,16


; create a test texture
s=128
tex=CreateTexture(s,s,4)
SetBuffer TextureBuffer(tex)
For i=0 To 25
 Color Rand(255),Rand(255),Rand(255)
 Oval Rand(-25,s+25),Rand(-25,s+25),25,25,1
Next
SetBuffer BackBuffer()
EntityTexture cube,tex



; *** compare these two ways of masking the texture: ***

;SetMaskChannel(tex)        ; standard method, also used by Loading with flag 4
OptimizeAlphaChannel(tex)   ; optimized method, paint edges with neighbour colors to 
                            ; prevent fade-To-black edges

;(you may also skip both functions to see how the texture is unmasked after painting to it: 
;although created with flag 4, after painting to it the alpha channel is set to <>zero (aka opaque))



; test loop
While KeyDown(1)=0
 TurnEntity cube,.1,.2,.3
 RenderWorld()
 Flip
Wend
End




Function SetMaskChannel(tex)
 ;this function will set the alpha channel of black pixels to be fully transparent 
 ;(as required by masked textures using flag 4).
 Local w,h,rgb,r,g,b,x,y,x2,y2,count,average_rgb
 w=TextureWidth(tex)
 h=TextureHeight(tex)
 Dim tex_memory(w,h)
 SetBuffer TextureBuffer(tex)
 LockBuffer()
 For y=0 To h-1
  For x=0 To w-1
   rgb=ReadPixelFast(x,y) And $FFFFFF
   If rgb=0 Then
    WritePixelFast x,y,0
   Else
   EndIf
  Next
 Next
 UnlockBuffer()
 SetBuffer BackBuffer()
End Function



Function OptimizeAlphaChannel(tex)
 ;this function will set the alpha channel of black pixels to be fully transparent 
 ;(as required by masked textures using flag 4). Additionally it will paint the RGB of transparent 
 ; pixels with the average neighbour color to prevent black edge bleeding artefacts.
 Local w,h,rgb,r,g,b,x,y,x2,y2,count,average_rgb
 w=TextureWidth(tex)
 h=TextureHeight(tex)
 Dim tex_memory(w,h)
 SetBuffer TextureBuffer(tex)
 LockBuffer()
 For y=0 To h-1
  For x=0 To w-1
   rgb=ReadPixelFast(x,y) And $FFFFFF
   tex_memory(x,y)=rgb
  Next
 Next
 For y=0 To h-1
  For x=0 To w-1
   If tex_memory(x,y)=0
    count=0
    r=0
    g=0
    b=0
    For y2=y-1 To y+1
     For x2=x-1 To x+1
      If (x<>x2 Or y<>y2) And ((x2>=0) And (x2<w) And (y2>=0) And (y2<h)) 
       If tex_memory(x2,y2)<>0
        r=r+ ((tex_memory(x2,y2) And $FF0000) Shr 16)
        g=g+ ((tex_memory(x2,y2) And $FF00)   Shr 8)
        b=b+ ((tex_memory(x2,y2) And $FF))
        count=count+1
       EndIf
      EndIf
     Next
    Next
    If count>0
     r=r/count
     g=g/count
     b=b/count
     average_rgb=(r Shl 16) Or (g Shl 8) Or b
    Else
     average_rgb=0
    EndIf
    WritePixelFast x,y,average_rgb
   EndIf
  Next
 Next
 UnlockBuffer()
 SetBuffer BackBuffer()
 Dim tex_memory(0,0)
End Function
