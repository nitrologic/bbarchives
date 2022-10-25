; ID: 502
; Author: Olive
; Date: 2002-11-22 06:24:46
; Title: Portable Lightmapper optimisation
; Description: A little optimisation

Search : 

;Check for visibility
distx# = xi-l\x
disty# = yi-l\y
distz# = zi-l\z
If LinePick(l\x,l\y,l\z,distx,disty,distz,0)=imagepivot
	endist# = EntityDistance(imagepivot,lightpivot)
	If endist <= lrange
	;If endist < 1 Then endist = 1
	normx# = xi - l\x : normy# = yi - l\y : normz# = zi - l\z
	normx = normx/endist : normy = normy/endist : normz = normz/endist
	cosangle# = (nx*normx)+(ny*normy)+(nz*normz)
	intense# = Abs(cosangle)
	If intense > 1 Then intense = 1
	If intense < 0 Then intense = 0
	minusr# = falloffr*endist
	minusg# = falloffg*endist
	minusb# = falloffb*endist
	colr# = l\r - minusr+falloffr
	colg# = l\g - minusg+falloffg
	colb# = l\b - minusb+falloffb
	argb = ReadPixelFast(x,y) And $FFFFFF
	colr = colr + (argb Shr 16 And %11111111)
	colg = colg + (argb Shr 8 And %11111111)
	colb = colb + (argb And %11111111)
	colr = colr*intense*lum : colg = colg*intense*lum
	colb = colb*intense*lum
	If colr < 0 Then colr = 0
	If colr > 255 Then colr = 255
	If colg < 0 Then colg = 0
	If colg > 255 Then colg = 255
	If colb < 0 Then colb = 0
	If colb > 255 Then colb = 255
	rgb = colb Or (colg Shl 8) Or (colr Shl 16)
	;Color colr,colg,colb
	;Rect x,y,1,1
	WritePixelFast x,y,rgb
	EndIf
EndIf
....
..
..
.

just replace with

;Check for visibility
distx# = xi-l\x
disty# = yi-l\y
distz# = zi-l\z
endist# = EntityDistance(imagepivot,lightpivot)
If endist <= lrange
	If LinePick(l\x,l\y,l\z,distx,disty,distz,0)=imagepivot
		normx# = xi - l\x : normy# = yi - l\y : normz# = zi - l\z
		normx = normx/endist : normy = normy/endist : normz = normz/endist
		cosangle# = (nx*normx)+(ny*normy)+(nz*normz)
		if (cosangle > 0)		;Backface culling
			intense# = Abs(cosangle)
			If intense > 1 Then intense = 1
			minusr# = falloffr*endist
			minusg# = falloffg*endist
			minusb# = falloffb*endist
			colr# = (l\r - minusr+falloffr)*intense
			colg# = (l\g - minusg+falloffg)*intense
			colb# = (l\b - minusb+falloffb)*intense
			argb = ReadPixelFast(x,y) And $FFFFFF
			colr = colr + (argb Shr 16 And %11111111)
			colg = colg + (argb Shr 8 And %11111111)
			colb = colb + (argb And %11111111)
			colr = colr*intense*lum : colg = colg*intense*lum
			colb = colb*lum
			If colr > 255 Then colr = 255
			If colg > 255 Then colg = 255
			If colb > 255 Then colb = 255
			rgb = colb Or (colg Shl 8) Or (colr Shl 16)
			WritePixelFast x,y,rgb
		endif
	EndIf
EndIf

....
..
...

THAT'S ALL
Bye :)
