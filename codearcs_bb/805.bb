; ID: 805
; Author: ford escort
; Date: 2003-10-05 12:57:17
; Title: image to icon
; Description: convert a picture in ico format

Graphics 800,600,32,2
bmp$="image1.bmp"
filename$="icon.ico"
Global offset=0
Global icon
saveicon(bmp$,filename$)
Function saveicon(bmp$,filename$)
	buffer=LoadImage(bmp$)
	icon=CreateBank(100000)
	If buffer
		;-------------------------------ICONDIR structure
		poke(0,1)						;reserved must be 0 					(WORD)0
		poke(1,1)						;ressource type icon=1					(WORD)2
		poke(1,1)						;how many images 1						(WORD)4
		;-------------------------------ICONDIR ENTRY
		poke(ImageWidth(buffer),0)      ;width in pixel of the picture			(BYTE)6
		poke(ImageHeight(buffer),0)		;height in pixel of the picture			(BYTE)7
		poke(0,0)						;#color in image 0 if >8bpp 			(BYTE)8
		poke(0,0)						;reserved must be 0 					(BYTE)9
		poke(1,1)						;color planes 							(WORD)10
		poke(32,1)						;bit per pixel							(WORD)12
		bytecount=((ImageWidth(buffer)*ImageHeight(buffer))*4);x,y*4 bytes
		poke(bytecount+40+(ImageWidth(buffer)*ImageHeight(buffer))/8,2)	;how many bytes in this ressource		(DWORD)14
		poke(offset+4,2)				;where in the file is the bitmap data	(DWORD)18
		;--------------------------------DIB header
		poke(40,2)						;lenght of the header					(DWORD)22
		poke(ImageWidth(buffer),2)		;width in pixel of the picture			(DWORD)26
		poke(ImageHeight(buffer)*2,2)		;width in pixel of the picture		(DWORD)30
		poke(1,1)						;color planes 1 in most case			(WORD)34
		poke(32,1)						;bit per pixel							(WORD)36
		poke(0,2)						;compression value						(DWORD)38
		poke(bytecount,2)				;pixeldata size							(DWORD)42
		poke(0,2)						;ppm									(DWORD)46
		poke(0,2)						;ppm									(DWORD)50
		poke(0,2)						;number of color 0=max                  (DWORD)54
		poke(0,2)						;number of color indice importants 0=max(DWORD)58
		SetBuffer ImageBuffer(buffer)
	For y=ImageHeight(buffer)-1 To 0 Step-1
		For x=0To ImageWidth(buffer)-1;To  Step-1
			GetColor x,y
			poke(ColorBlue(),0)
			poke(ColorGreen(),0)
			poke(ColorRed(),0)
			poke(255,0)
		Next 
	Next
	For a=1 To bytecount/8
		poke(0,0)
	Next
	
	EndIf
fil=WriteFile(filename$)
WriteBytes(icon,fil,0,offset)
CloseFile fil
End Function
Function poke(value,typ)
Select typ
	Case 0;byte
		PokeByte icon,offset,value:offset=offset+1
	Case 1;word
		PokeShort icon,offset,value:offset=offset+2
	Case 2;long
		PokeInt icon,offset,value:offset=offset+4
End Select
End Function
