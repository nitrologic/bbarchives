; ID: 2141
; Author: Ked
; Date: 2007-11-06 15:48:23
; Title: Image To Icon (UPDATE)
; Description: Ford Escort's code updated to support a mask image.

Graphics 800,600,32,2
bmp$="test.bmp"
maskfile$="test_mask.bmp"
filename$="test.ico"
saveicon(bmp$,filename$,maskfile$)

Function SaveIcon(bmp$,filename$,maskfile$)
	Local buffer=LoadImage(bmp$)
	Local mask=LoadImage(maskfile$)
	If ImageWidth(buffer)<>ImageWidth(mask) Or ImageHeight(buffer)<>ImageHeight(mask) Then
		RuntimeError "Invalid mask image!"
	EndIf
	Local icon=CreateBank(100000)
	Local offset=0
	
	If buffer
		;-------------------------------ICONDIR structure
		offset=Poke(0,1,icon,offset)						;reserved must be 0 					(WORD)0
		offset=Poke(1,1,icon,offset)						;ressource type icon=1					(WORD)2
		offset=Poke(1,1,icon,offset)						;how many images 1						(WORD)4
		;-------------------------------ICONDIR ENTRY
		offset=Poke(ImageWidth(buffer),0,icon,offset)      ;width in pixel of the picture			(BYTE)6
		offset=Poke(ImageHeight(buffer),0,icon,offset)		;height in pixel of the picture			(BYTE)7
		offset=Poke(0,0,icon,offset)						;#color in image 0 if >8bpp 			(BYTE)8
		offset=Poke(0,0,icon,offset)						;reserved must be 0 					(BYTE)9
		offset=Poke(1,1,icon,offset)						;color planes 							(WORD)10
		offset=Poke(32,1,icon,offset)						;bit per pixel							(WORD)12
			bytecount=((ImageWidth(buffer)*ImageHeight(buffer))*4);x,y*4 bytes
		offset=Poke(bytecount+40+(ImageWidth(buffer)*ImageHeight(buffer))/8,2,icon,offset)	;how many bytes in this ressource		(DWORD)14
		offset=Poke(offset+4,2,icon,offset)				;where in the file is the bitmap data	(DWORD)18
		;--------------------------------DIB header
		offset=Poke(40,2,icon,offset)						;lenght of the header					(DWORD)22
		offset=Poke(ImageWidth(buffer),2,icon,offset)		;width in pixel of the picture			(DWORD)26
		offset=Poke(ImageHeight(buffer)*2,2,icon,offset)		;width in pixel of the picture		(DWORD)30
		offset=Poke(1,1,icon,offset)						;color planes 1 in most case			(WORD)34
		offset=Poke(32,1,icon,offset)						;bit per pixel							(WORD)36
		offset=Poke(0,2,icon,offset)						;compression value						(DWORD)38
		offset=Poke(bytecount,2,icon,offset)				;pixeldata size							(DWORD)42
		offset=Poke(0,2,icon,offset)						;ppm									(DWORD)46
		offset=Poke(0,2,icon,offset)						;ppm									(DWORD)50
		offset=Poke(0,2,icon,offset)						;number of color 0=max                  (DWORD)54
		offset=Poke(0,2,icon,offset)						;number of color indice importants 0=max(DWORD)58

		For y=ImageHeight(buffer)-1 To 0 Step-1
		For x=0 To ImageWidth(buffer)-1;To  Step-1
			SetBuffer ImageBuffer(buffer)
				GetColor x,y
				red=ColorRed()
				green=ColorGreen()
				blue=ColorBlue()
				
				offset=Poke(blue,0,icon,offset)
				offset=Poke(green,0,icon,offset)
				offset=Poke(red,0,icon,offset)
			SetBuffer ImageBuffer(mask)
				GetColor x,y
				alpha=ColorRed()
				
				offset=Poke(alpha,0,icon,offset)
		Next 
		Next
		
		For a=1 To bytecount/8
			offset=Poke(0,0,icon,offset)
		Next
	
	EndIf
	
	fil=WriteFile(filename$)
	WriteBytes(icon,fil,0,offset)
	CloseFile fil
End Function

Function Poke(value,typ,bank,offset)
	Select typ
		Case 0;byte
			PokeByte bank,offset,value:offset=offset+1
			Return offset
		Case 1;word
			PokeShort bank,offset,value:offset=offset+2
			Return offset
		Case 2;long
			PokeInt bank,offset,value:offset=offset+4
			Return offset
	End Select
End Function
