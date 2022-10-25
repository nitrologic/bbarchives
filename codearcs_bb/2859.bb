; ID: 2859
; Author: Andy_A
; Date: 2011-06-13 20:35:27
; Title: Texture Filled Triangle Function
; Description: textured triangles

;     Title: Textured Triangle Fill
;Programmer: Andy Amaya
;      Date: 2011.06.12

AppTitle "Texture Filled Triangle Function"

Global sw%, sh%
sw = 640 : sh = 480
Graphics sw, sh, 32, 2
SeedRnd MilliSecs()

ClsColor 32, 192, 255

; these are procedurally generated textures but,
; you can use any texture you have on your hard drive
img1% = makeTexture1()
img2% = makeTexture2()
img3% = makeTexture3()
img4% = makeTexture4()

;to display the textures being used change to 1
showTextures = 0

While MouseHit(1) = 0
	Cls
	If showTextures = 1 Then
		DrawBlock img1, 10,10
		DrawBlock img2, 40,10
		DrawBlock img3, 70,10
		DrawBlock img4, 100,10
		Color 0,0,0
		Text 144,16,"<=== These are the textures used"
	End If
    st = MilliSecs()
    For i = 1 To 4
        x1 = Rand(50,sw-50) : y1 = Rand(50,sh-50)
        x2 = Rand(50,sw-50) : y2 = Rand(50,sh-50)
        x3 = Rand(50,sw-50) : y3 = Rand(50,sh-50)
        Select i
            Case 1
            	textureFill(x1,y1,x2,y2,x3,y3,img1)
            Case 2
            	textureFill(x1,y1,x2,y2,x3,y3,img2)
            Case 3
            	textureFill(x1,y1,x2,y2,x3,y3,img3)
            Case 4
            	textureFill(x1,y1,x2,y2,x3,y3,img4)
        End Select
    Next
    et = MilliSecs()-st
	Color 0,0,0
    Text   5, sh-20,"et: "+et
    Text sw/2, sh-20,"R-click to repeat          L-click to exit",True
	Flip
    WaitMouse() ;L-click to exit ..... R-click to repeat
Wend


FreeImage img1
FreeImage img2
FreeImage img3
FreeImage img4
End


Function textureFill(x1#, y1#, x2#, y2#, x3#, y3#, imageHandle%)
	Local slope1#, slope2#, slope3#, x#, y#
	Local imgW%, imgH%, tx%, sy%, ey%, ix%, iy%, vLen%, tLen%
	
	imgW = ImageWidth(imageHandle)
	imgH = ImageHeight(imageHandle)
	
    ;triangle coordinates must be ordered: where x1 < x2 < x3
    If x2 < x1 Then x  = x2 : y  = y2 : x2 = x1 : y2 = y1 : x1 = x  : y1 = y
    ;swap x1, y1, with x3, y3
    If x3 < x1 Then x  = x3 : y  = y3 : x3 = x1 : y3 = y1 : x1 = x  : y1 = y
    ;swap x2, y2 with x3, y3
    If x3 < x2 Then x  = x3 : y  = y3 : x3 = x2 : y3 = y2 : x2 = x  : y2 = y
    If x1 <> x3 Then slope1 = (y3-y1)/(x3-x1)
    ;draw the first half of the triangle
    length = x2 - x1
    If length <> 0 Then
        slope2 = (y2-y1)/(x2-x1)
        For x = 0 To length
            ;add x to coord x1
            tx = x+x1
            ;find modulus of x coord on texture
            ix = tx Mod imgW
            ;calc the starting y pos
            sy = Int(x*slope1+y1)
            ;calc the ending y pos
            ey = Int(x*slope2+y1)
            ;make sure starting y pos is less than ending y pos
            If ey < sy Then tmp=sy: sy = ey: ey = tmp
            ;vLen (vertical length) is delta of starting y pos and ending y pos
            vLen = ey-sy
            ;while vertical length is non-zero add slices of texture (vLen = zero at vertex)
            While vLen > 0
                ;find the modulus of starting y pos to grab a slice of texture image at correct position
                iy = sy Mod imgH
                ;tLen represents length of texture slice to grab
                tLen = imgH - iy
                ;if starting y pos plus texture length >= ending y pos of triangle then truncate tLen
				If (sy + tLen) >= ey Then tLen = ey-sy
				;grab a 1 by tLen slice of the texture at ix, iy
				CopyRect ix, iy,1,tLen, tx, sy, ImageBuffer(imageHandle),BackBuffer()
                If tLen > 0 Then
                    ;increment the starting y pos by adding texture slice length
                    sy = sy + tLen
                    ;subtract texture slice length from vertical length triangle section
                    ;(this is how we eventually exit the While-Wend loop)
                    vLen = vLen - tLen
                End If
            Wend
        Next
    End If
    ;draw the second half of the triangle
    y = length*slope1+y1 : length = x3-x2
    If length <> 0 Then
        slope3 = (y3-y2)/(x3-x2)
        For x = 0 To length
            ;add x to coord x2
            tx = x+x2
            ;find modulus of x coord on texture
            ix = tx Mod imgW
            ;calc the starting y pos
            sy = Int(x*slope1+y)
            ;calc the ending y pos
            ey = Int(x*slope3+y2)
            ;make sure start y pos is less than ending y pos
            If ey < sy Then tmp=sy: sy = ey: ey = tmp
            ;vLen (vertical length) is delta of starting y pos and ending y pos
            vLen = ey-sy
            ;while vertical length is non-zero add slices of texture (vLen = zero at vertex)
            While vLen > 0
                ;find the modulus of starting y pos to grab a slice of texture image at correct position
                iy = sy Mod imgH
                ;tLen represents length of texture slice to grab
                tLen = imgH - iy
                ;if starting y pos plus texture length >= ending y pos of triangle then truncate tLen
				If (sy + tLen) >= ey Then tLen = ey-sy
				;grab a 1 by tLen slice of the texture at ix, iy
				CopyRect ix, iy,1,tLen, tx, sy, ImageBuffer(imageHandle),BackBuffer()
                If tLen > 0 Then
                    ;increment the starting y pos by adding texture slice length
                    sy = sy + tLen
                    ;subtract texture slice length from vertical length triangle section
                    ;(this is how we eventually exit the While-Wend loop)
                    vLen = vLen - tLen
                End If
            Wend
        Next
    End If
End Function

Function makeTexture1()
	Local dh#, diam%, j#, i%
	
	texture% = CreateImage(24,24)
	SetBuffer(ImageBuffer(texture))
	Color 0, 0, 128
	Rect 0, 0, 24, 24, True
	dh = 256./23.
	diam = 24
	j = 91.0
	offset = 0
	While j <= 224.0
		i = Floor(j)
		Color i,i,i
		Oval 0+offset, 0+offset, diam, diam, True
		diam = diam - 2
		offset = offset + 1
		If diam < 1 Then diam = 1: offset = diam/2
		j = j + dh
	Wend
	SetBuffer(BackBuffer())
	Return texture
End Function

Function makeTexture2()
	texture% = CreateImage(21,21)
	SetBuffer(ImageBuffer(texture))
	Color 128, 0, 0
	Rect 0, 0, 21, 3, True
	Color 255,160,0
	Rect 0, 3, 21, 3, True
	Color 255,255,0
	Rect 0, 6, 21, 3, True
	Color 0,255,0
	Rect 0, 9, 21, 3, True
	Color 0,0,255
	Rect 0,12, 21, 3, True
	Color 128,0,255
	Rect 0,15, 21, 3, True
	Color 0,0,128
	Rect 0,18, 21, 3, True
	SetBuffer(BackBuffer())
	Return texture
End Function

Function makeTexture3()
	Local dh#, diam%, j#, i%
	
	texture% = CreateImage(24,24)
	SetBuffer(ImageBuffer(texture))
	Color 128, 0, 0
	Rect 0, 0, 24, 24, True
	dh = 256./23.
	diam = 24
	j = 91.0
	offset = 0
	While j <= 224.0
		i = Floor(j)
		Color i,i,0
		Oval 0+offset, 0+offset, diam, diam, True
		diam = diam - 2
		offset = offset + 1
		If diam < 1 Then diam = 1: offset = diam/2
		j = j + dh
	Wend
	SetBuffer(BackBuffer())
	Return texture
End Function

Function makeTexture4()
	texture% = CreateImage(42,24)
	SetBuffer(ImageBuffer(texture))
	Color 0,80,112
	Rect 0,0,42,28,True
	Color 255,255,32
	Text 21, 0,"Blitz",True
	Text 21,11,"Plus",True
	SetBuffer(BackBuffer())
	Return texture
End Function
