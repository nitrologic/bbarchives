; ID: 1266
; Author: pexe
; Date: 2005-01-24 15:09:37
; Title: Grayscale
; Description: Turn your graphics to grayscale mode

;Grayscale
;By pexe

;WARNING: This is my first script using readpixel and writepixel, and its VERY SLOW!

;Function grayscale(Buffer, X Starting Point, Y Starting Point, Width, Height)

Function grayscale(GS_buffer,GS_vx,GS_vy,GS_w,GS_h)
LockBuffer(GS_buffer)
For GS_y = GS_vy To GS_vy+GS_h-1
For GS_x = GS_vx To GS_vx+GS_w-1
GS_pix = ReadPixelFast(GS_x,GS_y,GS_buffer)

GS_r% = (GS_pix Shr 16) And $ff ;\
GS_g% = (GS_pix Shr 8) And $ff  ;  Transform values
GS_b% = GS_pix And $ff          ;/

GS_v% = 0.3*GS_r+0.59*GS_g+0.11*GS_b

GS_pix=(GS_v Or (GS_v Shl 8) Or (GS_v Shl 16) Or ($ff000000)) ;Put values back
WritePixelFast GS_x,GS_y,GS_pix,GS_buffer

Next
Next
UnlockBuffer(GS_buffer)
End Function
