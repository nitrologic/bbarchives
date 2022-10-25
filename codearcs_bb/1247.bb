; ID: 1247
; Author: Hulk
; Date: 2004-12-29 05:05:40
; Title: Save TGA 32
; Description: Save alpha from rgb sum

; ID: 1
; Author: skidracer
; Date: 2001-08-16 16:24:30
; Title: SaveTGA
; Description: save texture in TGA format
;
; Addition coding HULK on 12/2004
; Description: save buffer to TGA format
; flag: pix_depth=24 no alpha save
;       pix_depth=32 save alpha from rgb sum ;nice!


Graphics3D 640,480,32,2
SetBuffer FrontBuffer()

;**************************************************************
; MISC STUFF
camera%=CreateCamera()

PositionEntity camera,0,0,-10

Dim p(100)
For a=1 To 100
	p(a)=CreateSprite()
	EntityAlpha p(a),Rnd(.1,.5)
	EntityColor p(a),Rnd(255),Rnd(255),Rnd(255)
	PositionEntity p(a),Rnd(-10,10),Rnd(-10,10),Rnd(0,10)
Next

RenderWorld()
Color 255,0,0
Rect 0,0,640,480,0
WaitKey()
;**************************************************************

Save_buffer_tga("c:\test.tga",FrontBuffer(),640,480,32)
End





Function Save_buffer_TGA(name$,buffer%,sx%,sy%,pix_depth%=24)
    Local f,width%,height%,x%,y%
    width=sx
    height=sy
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

    LockBuffer buffer
	For y=height-1 To 0 Step -1
        For x=0 To width-1

			If pix_depth=24
				pix%=ReadPixelFast(x,y,buffer)
				WriteInt f,pix
			Else
				pix%=(ReadPixelFast(x,y,buffer) Shl 8) Shr 8

				ir%=((pix Shr 16) And $00ff)
				ig%=((pix Shr 8) And $00ff)	
				ib%=((pix And $00ff))
				
				alpha#=(ir + ig +ib) * 1.5		;1.5 alpha power based on rgb sum :) 1.5, 1.7, 1.9 increase visibility but decrease alpha
				
				If alpha>255 Then alpha=255
				
				WriteInt f,( alpha Shl 24 ) Or pix
			EndIf      
			
        Next
    Next
    CloseFile f
	UnlockBuffer buffer
End Function
