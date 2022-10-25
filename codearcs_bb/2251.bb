; ID: 2251
; Author: Baystep Productions
; Date: 2008-05-04 11:18:43
; Title: Transparent TGA (Load/Draw)
; Description: Load and draw TGA images with alpha channels.

;CREATED BY CHRIS PIKUL
;----------------------

Type TGAImage
	Field file$,width%,height%,depth%,IMGBank,RGBImg,blend%
End Type
Global tga.TGAImage

Function LoadTGAImage.TGAImage(file$,blend%=0)	;Blend: -1=Sub 0=Multiply 1=Additive
	img = ReadFile(file$)
	If Not img Then RuntimeError("Could not load specified image!")
	Local idlen%=ReadByte(img)
	Local clrmap%=ReadByte(img)
	Local imgtyp%=ReadByte(img)
	Local cm_index%=ReadShort(img)
	Local cm_length%=ReadShort(img)
	Local cm_entsize%=ReadByte(img)
	Local img_xorg%=ReadShort(img)
	Local img_yorg%=ReadShort(img)
	Local img_width%=ReadShort(img)
	Local img_height%=ReadShort(img)
	Local img_depth%=ReadByte(img)
	Local img_desc%=ReadByte(img)
	Local img_info$=""
	If idlen%<>0 Then
		For i=1 To idlen%
			img_info$=img_info$+Chr$(ReadByte(img))
		Next
	EndIf
	Local cm_data$=""
	If clrmap%<>0 Then
		For i=1 To cm_entsize%*cm_length%
			cm_data$=cm_data$+Chr$(ReadByte(img))
		Next
	EndIf
	tga.TGAImage = New TGAImage
	tga\IMGBank = CreateBank((4*img_width%)*(4*img_height))
	tga\RGBImg = CreateImage(img_width%,img_height%)
	tga\width%=img_width%
	tga\height%=img_height%
	tga\depth%=img_depth%
	tga\blend%=blend%
	intwrite=0
	SetBuffer ImageBuffer(tga\RGBImg)
	LockBuffer
	For y=img_height%-1 To 0 Step -1
		For x=0 To img_width-1
			If img_depth%=32
				pix%=ReadInt(img)
				PokeInt(tga\IMGBank,intwrite,pix%)
				r%=(pix Shr 16) And $FF
				g%=(pix Shr 8) And $FF
				b%=pix And $FF
				out_pix% = b% Or (g% Shl 8) Or (r% Shl 16)
				intwrite=intwrite+4
				WritePixelFast(x,y,out_pix%)
			Else
				
			EndIf
		Next
	Next
	UnlockBuffer
	SetBuffer BackBuffer()
	
	Return tga.TGAImage
End Function

Function DrawTGAStatic(hnd.TGAImage,sx%,sy%)
	DrawImage(hnd\RGBImg,sx%,sy%)
End Function

Function DrawTGAImage(hnd.TGAImage,sx%,sy%,a#=1,aopt#=True) ;Handle,XCord,YCord,Alpha,ARGB(FALSE=RGB)
	intread=0
	Local iA#=0.0
	If aopt#=False Or hnd\depth%<>32
		DrawImage(hnd\RGBImg,sx%,sy%)
	Else
		LockBuffer()
		For y=hnd\height%-1 To 0 Step -1
			For x=0 To hnd\width%-1
				pix% = PeekInt(hnd\IMGBank,intread)
				opix% = ReadPixelFast(sx%+x,sy%+y)
				
				iA#=((pix Shr 24) And $FF)/255.0
				Select hnd\blend%
					Case 1
						iR%=((pix Shr 16) And $FF)*(a#*iA#)
						iG%=((pix Shr 8) And $FF)*(a#*iA#)
						iB=(pix And $FF)*(a#*iA#)
						gR%=((opix Shr 16) And $FF)+iR%
						gG%=((opix Shr 8) And $FF)+iG%
						gB%=(opix And $FF)+iB%
					Case 0
						iR%=((pix Shr 16) And $FF)
						iG%=((pix Shr 8) And $FF)
						iB=(pix And $FF)
						gR%=((opix Shr 16) And $FF)
						gG%=((opix Shr 8) And $FF)
						gB%=(opix And $FF)
						gR%=gR%+(iR%-gR%)*iA#:gG%=gG%+(iG%-gG%)*iA#:gB%=gB%+(iB%-gB%)*iA#
					Case -1
						iR%=((pix Shr 16) And $FF)*(a#*iA#)
						iG%=((pix Shr 8) And $FF)*(a#*iA#)
						iB=(pix And $FF)*(a#*iA#)
						gR%=((opix Shr 16) And $FF)-iR%
						gG%=((opix Shr 8) And $FF)-iG%
						gB%=(opix And $FF)-iB%
				End Select
				
				gR%=gR% And 255:gG%=gG% And 255:gB%=gB% And 255
				rpix% = gB% Or (gG% Shl 8) Or (gR% Shl 16)
				WritePixelFast(sx%+x,sy%+y,rpix%)
				intread=intread+4
			Next
		Next
		UnlockBuffer()
	EndIf
End Function

Function FreeTGAImage(hnd.TGAImage)
	FreeImage(hnd\RGBImg)
	FreeBank(hnd\IMGBank)
	Delete hnd
End Function
