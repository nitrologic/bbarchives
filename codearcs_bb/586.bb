; ID: 586
; Author: Snarty
; Date: 2003-02-12 22:56:15
; Title: SLine
; Description: Useing a Bank instead of Writepixelfast

Function SLineBP(stx#,sty#,enx#,eny#,r,g,b,Buffer)

	mvx#=Stx-enx:mvy#=sty-eny
	If mvx<0 mvx=-mvx
	If mvy<0 mvy=-mvy
	If mvy>mvx mv#=mvy Else mv#=mvx
	stpx#=(mvx/mv):If Stx>enx stpx=-stpx
	stpy#=(mvy/mv):If Sty>eny stpy=-stpy
	LockBuffer Buffer
	SL_Pitch=LockedPitch(Buffer)
	SL_Bank=LockedPixels(Buffer)
	SL_Mode=LockedFormat(Buffer)
	Col=ConvertRGB(r,g,b,SL_Mode)
	If SL_Mode=1 Or SL_Mode=2
		For nc=0 To Floor(mv)
			PokeShort SL_Bank,(Floor(stx)*2)+(Floor(sty)*SL_Pitch),Col
			stx=stx+stpx:sty=sty+stpy
		Next
	Else
		For nc=0 To Floor(mv)
			PokeInt SL_Bank,(Floor(sty)*SL_Pitch)+(Floor(stx)*4),Col
			stx=stx+stpx:sty=sty+stpy
		Next
	EndIf
	UnlockBuffer Buffer

End Function
