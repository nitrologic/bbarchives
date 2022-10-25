; ID: 275
; Author: Snarty
; Date: 2002-03-21 15:50:22
; Title: Snarty Line (ProPixel Code)
; Description: Improvement on the Bresham Line Drawing algo (For Blitz).

; Snarty Line
; Written By Paul Snart (Snarty)
; Oct 2001

; Stx,Sty = Start pixel posistion
; Enx,Eny = End point.
; Mode    = False = Normal, True = XOR'ed
; CurBrush= Image Memory Handle

Function SLine(stx#,sty#,enx#,eny#,mode)

	mvx#=Stx-enx:mvy#=sty-eny
	If mvx<0 mvx=-mvx
	If mvy<0 mvy=-mvy
	If mvy>mvx mv#=mvy Else mv#=mvx
	stpx#=(mvx/mv):If Stx>enx stpx=-stpx
	stpy#=(mvy/mv):If Sty>eny stpy=-stpy
	If Mode=1 LockBuffer BackBuffer()
	For nc=0 To Floor(mv)
		If mode=0
			If BrushMode<>1 Or Brush=0
				DrawImage CurBrush,stx,sty
			Else
				If BrushMode=1
					DrawBlock CurBrush,stx,sty
				EndIf
			EndIf
		Else
			rgb=ReadPixelFast(stx,sty) And $FFFFFF
			ColD=$FFFFFF Xor rgb
			WritePixelFast stx,sty,ColD
		EndIf
		stx=stx+stpx:sty=sty+stpy
	Next
	If Mode=1 UnlockBuffer BackBuffer()

End Function
