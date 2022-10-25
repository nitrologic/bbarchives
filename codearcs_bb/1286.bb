; ID: 1286
; Author: jfk EO-11110
; Date: 2005-02-10 00:46:53
; Title: LoadAlphaChannel
; Description: Load a Textures Alpha-Channel from a 8 Bit PNG

; example , texture flags have to be used individually, but
; of course the Flag 2 (alpha) must be set.
; Both textures need to have the same size.

; loading the RGB channels from a lossy compressed JPG:
hudtex=LoadTexture("data\hud.jpg",256 Or 16 Or 32 Or 2)
; Assigning the Alpha Channel form a 8- Bit PNG:
LoadAlphaChannel(hudtex,"data\hud_mask.png")


Function LoadAlphaChannel(id,file$)
 tex4=LoadTexture(file$,2)
 If tex4<>0
  If TextureWidth(id)=TextureWidth(tex4)
   If TextureHeight(id)=TextureHeight(tex4)
    SetBuffer TextureBuffer(id)
    LockBuffer(TextureBuffer(tex4))
    LockBuffer(TextureBuffer(id))
    For j=0 To TextureHeight(tex4)-1
     For i=0 To TextureWidth(tex4)-1
      argb=ReadPixelFast(i,j,TextureBuffer(tex4)) And $ffffff
      r=(argb Shr 16)And $FF
      g=(argb Shr 8)And $FF
      b=argb And $FF
      grey= ((r+g+b)/3)
      If grey > 255 Then grey=255
      rgb=(ReadPixelFast(i,j,TextureBuffer(id))) And $FFFFFF
      WritePixelFast i,j,rgb Or (grey Shl 24),TextureBuffer(id)
     Next
    Next
    UnlockBuffer(TextureBuffer(tex4))
    UnlockBuffer(TextureBuffer(id))
    SetBuffer BackBuffer()
   EndIf
  EndIf
  FreeTexture tex4
 EndIf
End Function
