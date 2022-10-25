; ID: 1
; Author: skidracer
; Date: 2001-08-16 16:24:30
; Title: SaveTGA
; Description: save texture in TGA format

Function SaveTGA(name$,texture)
    Local f,width,height,tbuffer,x,y
    width=TextureWidth(texture)
    height=TextureHeight(texture)
    f=WriteFile(name$)
    WriteByte(f,0) ;idlength
    WriteByte(f,0) ;colormaptype
    WriteByte(f,2) ;imagetype 2=rgb
    WriteShort(f,0) ;colormapindex
    WriteShort(f,0) ;colormapnumentries
    WriteByte(f,0) ;colormapsize 
    WriteShort(f,0) ;xorigin
    WriteShort(f,0) ;yorigin
    WriteShort(f,width) ;width
    WriteShort(f,height) ;height
    WriteByte(f,32) ;pixsize
    WriteByte(f,8) ;attributes
    tbuffer=TextureBuffer(texture)
    For y=height-1 To 0 Step -1
        For x=0 To width-1
            WriteInt f,ReadPixel(x,y,tbuffer)
        Next
    Next
    CloseFile f
End Function

