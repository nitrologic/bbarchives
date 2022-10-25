; ID: 1207
; Author: TetraHC
; Date: 2004-11-26 16:07:27
; Title: Normal Map
; Description: Generates a simple normal map from a height map for use with Dot3

AppTitle "NormalMap"

Graphics 800,600,32,2

; this is a grayscale image. Each pixel is height information.
; White being the heighest and black being the lowest.
; you can use a color image but only the blue channel is used
heightmap = LoadImage("heightmap.bmp")

Global img_w = ImageWidth(heightmap)-1
Global  img_h = ImageHeight(heightmap)-1
lightmap = CreateImage(img_w+1,img_h+1)


LockBuffer ImageBuffer(heightmap)
LockBuffer ImageBuffer(lightmap)

For y = 0 To img_h
  If y>0 : ym1=y-1 : Else : ym1=0 : EndIf
  If y<img_h : yp1=y+1 : Else : yp1=img_h : EndIf

  For x = 0 To img_w
    If x>0 : xm1=x-1 : Else : xm1=0 : EndIf
    If x<img_w : xp1=x+1 : Else : xp1=img_w : EndIf  
    
    ; get the pixel value for height info - up, down, 
    ;  left and right of current pixel
    ;  And $0000FF restricts to blue channel
    y_u = ReadPixelFast(x,yp1,ImageBuffer(heightmap)) And $0000FF
    y_d = ReadPixelFast(x,ym1,ImageBuffer(heightmap)) And $0000FF
    x_r = ReadPixelFast(xp1,y,ImageBuffer(heightmap)) And $0000FF
    x_l = ReadPixelFast(xm1,y,ImageBuffer(heightmap)) And $0000FF

    ;calculate x and y gradient exactly the same as bumpmapping
    nx = (x_l - x_r)
    ny = (y_u - y_d)
    
    ; nx and ny can potentially fall into the range of -255 to 255, 
    ;  so we offest it To get rid of the negative value, 
    ;   Then half it To keep the value in the range of 255
    red = (nx + 255)/2 
    green = (ny + 255)/2
    blue = 255
   
    ; red is horizontal (x axis)
    ; red = 0 normal points left, red = 255 normal points right 
    ;  red = 128 normal points out of screen at you

    ;green is vertical (y axis)
    ; green = 0 normal points down, green = 255 normal points up 
    ;  green = 128 normal points out of screen at you
   
   
    ; shift the colors into the right place
    cr = (red Shl 16) + (green Shl 8) + (blue)
    WritePixelFast(x,y,cr,ImageBuffer(lightmap))
  Next
Next
UnlockBuffer ImageBuffer(lightmap)
UnlockBuffer ImageBuffer(heightmap)

While Not KeyDown(1)
  DrawImage heightmap,0,0
  DrawImage lightmap,0,ImageHeight(heightmap)
  Flip
Wend

SaveImage lightmap,"normal_map.bmp" 
FreeImage heightmap
FreeImage lightmap
End
