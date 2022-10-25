; ID: 2755
; Author: Streaksy
; Date: 2010-08-18 06:55:23
; Title: Load TGA (Targa) With Alpha Map
; Description: Load a TGA file to texture, retaining alpha channel.

Function LoadTGA(fn$)
fil=ReadFile(fn$)
idlength=ReadByte(fil)
colourmaptype=ReadByte(fil) 
imagetype=ReadByte(fil)
colourmapindex=ReadShort(fil)
colourmapentries=ReadShort(fil)
colourmapsize=ReadByte(fil)
xorigin=ReadShort(fil)
yOrigin=ReadShort(fil)
width=ReadShort(fil)
height=ReadShort(fil)
bitsperpixel=ReadByte(fil)
attributes=ReadByte(fil)
tex=CreateTexture(width,height,1+2+16+32)
b=TextureBuffer(tex)
LockBuffer(b)
For y=height-1 To 0 Step -1
For x=0 To width-1
col=ReadInt(fil)
WritePixelFast(x,y,col,b)
Next
Next
UnlockBuffer(b)
CloseFile fil
Return tex
End Function
