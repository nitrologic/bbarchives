; ID: 812
; Author: Snarty
; Date: 2003-10-23 02:43:25
; Title: Unique Colour Counter
; Description: Counts the amount of colours in a 24bit Image

Dim RG(255,255)

Function CountColours(Parent,Image)

	UpWin=CreateWindow("Analysing Image...",GadgetX(Parent)+((GadgetWidth(Parent)-240)/2),GadgetY(Parent)+((GadgetHeight(Parent)-60)/2),240,60,Parent,17)
	Progbar=CreateProgBar(4,4,ClientWidth(UpWin)-8,ClientHeight(UpWin)-8,UpWin,1)
	Buffer=ImageBuffer(Image)
	LockBuffer Buffer
	BBank=LockedPixels(Buffer)
	BPitch=LockedPitch(Buffer)
	SH#=ImageBuffer(Image)
	For y=0 To ImageHeight(Image)-1
		yoff=y*BPitch
		For x=0 To ImageWidth(Image)-1
			pyxoff=yoff+(x*3)
			Rd=PeekByte(BBank,pyxoff+2)
			Gr=PeekByte(BBank,pyxoff+1)
			Bl=PeekByte(BBank,pyxoff)
			If RG(Rd,Gr)
				If Not PeekByte(RG(Rd,Gr),Bl)
					PokeByte RG(Rd,Gr),Bl,1
					NumCols=NumCols+1
				EndIf
			Else
				RG(Rd,Gr)=CreateBank(256)
				PokeByte RG(Rd,Gr),Bl,1
				NumCols=NumCols+1
			EndIf
		Next
		UpdateProgBar ProgBar,y/SH
	Next
	UnlockBuffer Buffer
	For r=0 To 255
		For g=0 To 255
			If RG(r,g)
				FreeBank RG(r,g)
				RG(r,g)=0
			EndIf
		Next
	Next
	FreeGadget UpWin
	Return NumCols

End Function
