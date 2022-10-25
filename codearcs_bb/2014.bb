; ID: 2014
; Author: bingman
; Date: 2007-05-16 00:45:23
; Title: Batch Height to Normal Converter
; Description: Batch Converter for making Normal Maps

AppTitle "BATCH HEIGHTMAP TO NORMAL MAP CODE"

Graphics 800,600,32,0


;IMPORTANT!
;1. Files should be 8 bit greyscale .bmps
;1. The images you want To convert must be in a folder called 'BMP' which is subfolder of where you run the program.
;2. A Second folder named 'Normals' must be in the 'Bmp' folder.

Global Dir$ = CurrentDir$()+"Bmp\"
Print Dir$
Global DirSave$ = Dir$ + "\Normals"
MyDir = ReadDir(Dir$)
Repeat
Map$ = NextFile$(MyDir)
If Map$ = "" Then Exit
If FileType(Dir$+"\"+Map$) = 2 Then Goto NoFile
Gosub MAKEMAP
.NoFile
Forever
CloseDir MyDir
End

.MAKEMAP
Global MapLength = (Len(Map$) -4)
Global Map2$ = LSet$(Map$,MapLength)
Global heightmap = LoadImage(Map$)
Global img_w = ImageWidth(heightmap)-1
Global  img_h = ImageHeight(heightmap)-1
Global lightmap = CreateImage(img_w+1,img_h+1)

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
ChangeDir(DirSave$)
SaveImage lightmap,Map2$ + "_N.bmp"
ChangeDir(Dir$)
FreeImage heightmap
FreeImage lightmap
Return
