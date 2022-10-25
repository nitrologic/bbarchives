; ID: 348
; Author: Chroma
; Date: 2002-07-05 22:48:07
; Title: Terrain Colormap Generator
; Description: A simple terrain color map generator.

;-Terrain Texture Generator-;
;-by Chroma-;

Graphics 800,600,16,2
SetBuffer BackBuffer()

map=LoadImage("hmap2.bmp")

mapWidth = ImageWidth(map)
mapHeight = ImageHeight(map)

DrawBlock map,0,0

;-Open a Text File-;
myfile=WriteFile("text.txt")

;-----------------------;
;-Read Map Pixel Values-;
;-----------------------;
LockBuffer
For y=0 To mapHeight
    For x=0 To mapWidth
        rgb = ReadPixelFast(x,y) And $FFFFFF
        WriteInt myfile,rgb
    Next    
Next
UnlockBuffer
;-----------------------;
CloseFile myfile


;-Open the test map-;
myfile=ReadFile("text.txt")

;-Now let's reconstruct the picture
For y=0 To mapHeight
    For x=0+300 To mapWidth+300
        rgb=ReadInt(myfile)
        r = rgb Shr 16 And %11111111
        g = rgb Shr 8 And %11111111
        b = rgb And %11111111
        ;Average out the value
        bw=(r+g+b)/3
        ;-Discern between the 4 terrain types-;
        ;Region1(Snow) : 256-192
        If bw=>192 Then r=255:g=255:b=255
        ;Region2(Rock) : 192-128
        If bw<192 And bw=>128 Then r=100:g=100:b=100
        ;Region3(Grass): 128-64
        If bw<128 And bw=>64 Then r=100:g=200:b=100
        ;Region4(Sand) : 64-0 
        If bw<64 r=100:g=50:b=50
        ;Throw out some color for testing    
        Color r,g,b 
        Plot x,y
    Next    
Next

CloseFile myfile


;MainLoops
While Not KeyHit(1)

Text 10,300,"Terrain Texture Generator"


Flip
Wend
End
