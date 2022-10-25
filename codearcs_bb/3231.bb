; ID: 3231
; Author: Andy_A
; Date: 2015-11-18 13:55:53
; Title: Guts by Jan Vibe
; Description: Guts

;GUTS
	;Original ARM BBC BASIC version by Jan Vibe
	;BlitzPlus version by Andy_A
	
	AppTitle "Guts"
	scrW%  =  800: scrH%  =  600
	Graphics scrW%, scrH%, 32, 2
	SetBuffer BackBuffer()
	SeedRnd MilliSecs()
	For i% = 0 To 1777
		A% = Rand(0,1777)
	Next

	Dim bX%(15),bY%(15),bZ#(15),clr%(15,3)
	For i% = 0 To 15
		bX%(i%) = -100
	Next

	A% = 0
	For N% = 1 To 15
		idx% = 16-N%
		clr(idx,1)= 7*N%+150
		clr(idx,2)=14*N%+45
		clr(idx,3)=14*N%+45
	Next

	X1%  = Rand(256,scrW%-256)
	Y1%  = Rand(256,scrH%-256)
	DX1% = Rand(16)*Sgn(Rnd(1.)-.5)
	DY1% = Rand(16)*Sgn(Rnd(1.)-.5)
	X2%  = Rand(256,scrW%-256)
	Y2%  = Rand(256,scrH%-256)
	
	DX2% = Rand(16)*Sgn(Rnd(1.)-.5)
	DY2% = Rand(16)*Sgn(Rnd(1.)-.5)

	While MouseHit(1) = 0	;Terminate program with Left-Click
		H% = X1%+DX1%
		If H%<256 Or H%>scrW%-256 Then DX1% = Rand(16)*Sgn(-DX1%)
		H% = Y1%+DY1%
		If H%<256 Or H%>scrH%-256 Then DY1% = Rand(16)*Sgn(-DY1%)
		X1% = X1% + DX1% 
		Y1% = Y1% + DY1%
		If X2%<X1% And DX2%< 24 DX2% = DX2% + 1
		If X2%>X1% And DX2%>-24 DX2% = DX2% - 1
		If Y2%<Y1% And DY2%< 24 DY2% = DY2% + 1
		If Y2%>Y1% And DY2%>-24 DY2% = DY2% - 1
		X2% = X2% + DX2%
		Y2% = Y2% + DY2%
		A% = (A%+10) Mod 360
		Z# = (Sin(A%)+1.)+2.
		For N% = 2 To 15
			bX%(N%-1) = bX%(N%)
			bY%(N%-1) = bY%(N%)
			bZ#(N%-1) = bZ#(N%)
 		Next
		bX%(15) = X2%
		bY%(15) = Y2%
		bZ#(15) = Z#
		For N% = 1 To 15
			Color clr(N%,1),clr(N%,2),clr(N%,3)
			rad% = N%*bZ#(N%)
			dia% = rad% + rad%
			cx% = bX%(N%)
			cy% = bY%(N%)
			Oval cx%-rad%, cy%-rad%, dia%, dia%, True
			If KeyHit(1) Then End	;Terminate program with [ESC] key
		Next
		Flip
	Wend
	End
