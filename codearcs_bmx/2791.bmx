; ID: 2791
; Author: ErikT
; Date: 2010-12-03 07:52:19
; Title: ScaleEX: Scaling filter for 2D pixel art
; Description: Applies smoothing to pixelled sprites and backgrounds

SuperStrict

Function ScaleEX(PM:TPixmap)
	Local PMPixels: Int Ptr = Int Ptr(pm.pixels)
	Local Pix_W:Int = PixmapWidth(PM)
	Local Pix_H:Int = PixmapHeight(PM)
	Local x:Int
	Local y:Int
	Local yt:Int
	Local yb:Int
	Local xl:Int
	Local xr:Int
	Local yt2:Int
	Local yb2:Int
	Local xl2:Int
	Local xr2:Int
	
	Local ScaledPM:TPixmap = CreatePixmap(Pix_W*2,Pix_H*2,PF_RGBA8888)
	Local x2PMPixels:Int Ptr = Int Ptr (ScaledPM.pixels)
	
	Local x2Pix_W:Int = PixmapWidth(ScaledPM)
	Local x2Pix_H:Int = PixmapHeight(ScaledPM)
	
	Local P:Int
	Local A:Int
	Local B:Int
	Local C:Int
	Local D:Int
	Local FL:Int	' far left pixel (beside C)
	Local TR:Int	' top right pixel
	Local FR:Int	' far right pixel (beside B)
	Local TL:Int	' top left pixel
	Local FT:Int	' far top pixel
	Local FB:Int	' far bottom pixel
	Local BL:Int	' bottom left pixel
	Local BR:Int	' bottom right pixel
	
	Local P_1:Int	' output pixel top-left
	Local P_2:Int	' output pixel top-right
	Local P_3:Int	' output pixel bottom-left
	Local P_4:Int	' output pixel bottom-right

	' RotSprite vars
	Local P_Clr:Int[4]	' 1 = R, 2 = G, 3 = B
	Local A_Clr:Int[4]	' 1 = R, 2 = G, 3 = B
	Local B_Clr:Int[4]	' 1 = R, 2 = G, 3 = B
	Local C_Clr:Int[4]	' 1 = R, 2 = G, 3 = B
	Local D_Clr:Int[4]	' 1 = R, 2 = G, 3 = B
	Local P_Value:Int		' holds average of RGB values
	Local A_Value:Int
	Local B_Value:Int
	Local C_Value:Int
	Local D_Value:Int
	
	' go through all pixels in the image
	For x = 0 To Pix_W - 1
		For y = 0 To Pix_H - 1
			yt = (y-1)*Pix_W
			yb = (y+1)*Pix_W
			yt2 = (y-2)*Pix_W
			yb2 = (y+2)*Pix_W
			xl2 = (x-2)
			xr2 = (x+2)
			xl = x-1
			xr = x+1
			Local yc:Int = y*pix_W
			' read the source pixel and its surrounding pixels
			P = PMPixels[yc+x]
			If x > 0 And x < Pix_W - 1 And y > 0 And y < Pix_H - 1
				A = PMPixels[yt+x]
				B = PMPixels[yc+xr]
				C = PMPixels[yc+xl]
				D = PMPixels[yb+x]
			Else
				A = P
				B = P
				C = P
				D = P
			EndIf

			P_1 = P
			P_2 = P
			P_3 = P
			P_4 = P

			' get colors
			P_Clr[1] = (P & $FF0000) Shr 16	' red
			P_Clr[2] = (P & $FF00) Shr 8		' green
			P_Clr[3] = (P & $FF)			' blue
			A_Clr[1] = (A & $FF0000) Shr 16	' red
			A_Clr[2] = (A & $FF00) Shr 8		' green
			A_Clr[3] = (A & $FF)			' blue
			B_Clr[1] = (B & $FF0000) Shr 16	' red
			B_Clr[2] = (B & $FF00) Shr 8		' green
			B_Clr[3] = (B & $FF)			' blue
			C_Clr[1] = (C & $FF0000) Shr 16	' red
			C_Clr[2] = (C & $FF00) Shr 8		' green
			C_Clr[3] = (C & $FF)			' blue
			D_Clr[1] = (D & $FF0000) Shr 16	' red
			D_Clr[2] = (D & $FF00) Shr 8		' green
			D_Clr[3] = (D & $FF)			' blue

			' set average value from RGB
			P_Value = (P_Clr[1] + P_Clr[2] + P_Clr[3]) / 3
			A_Value = (A_Clr[1] + A_Clr[2] + A_Clr[3]) / 3
			B_Value = (B_Clr[1] + B_Clr[2] + B_Clr[3]) / 3
			C_Value = (C_Clr[1] + C_Clr[2] + C_Clr[3]) / 3
			D_Value = (D_Clr[1] + D_Clr[2] + D_Clr[3]) / 3

			' check for equal pixels around P and modify output pixels accordingly
			If B <> C And A <> D And (A = C Or A = B Or C = D Or B = D)
				' if A = C then check far left and top right pixel to see if it's necessary 
				' to modify P_2 as well
				If A = C
					P_1 = A
					' check pixel at far left and top right to see if stepping needs easing
					If x > 1 Then FL = PMPixels[yc+xl2]
					' if FL = C then read another pixel: TR
					If FL = C
						If x < Pix_W - 1 And y > 0 Then TR = PMPixels[yt+xr]
						' If both = A && C then set P_2 to be A as well.
						If FL = C And TR = A Then P_2 = A
					EndIf

					' If FL is C and TR is A then we already know what to smooth here so
					' we can skip checking for vertical stepping and save CPU load. But if not
					' then do another check for P_3 to see if the bottom left and far top 
					' pixels are the same.
					If FL <> C Or TR <> A
						' check pixel at far top and bottom left to see if stepping needs easing
						If x > 0 And y < Pix_H - 1 Then BL = PMPixels[yb+xl]
						If BL = C
							If y > 1 Then FT = PMPixels[yt2+x]
							' If both = A && C then set P_3 to be C as well
							If BL = C And FT = A Then P_3 = C
						EndIf
					EndIf

				' if A = B then check far right and top left pixel to see if it's necessary 
				' to modify P_1 as well
				ElseIf A = B
					P_2 = B
					If x < Pix_W - 2 Then FR = PMPixels[yc+xr2]
					If FR = B
						If x > 0 And y > 0 Then TL = PMPixels[yt+xl]
						If FR = B And TL = A Then P_1 = B
					EndIf

					If FR <> B Or TL <> A
						If x < Pix_W - 1 And y < Pix_H - 1 Then BR = PMPixels[yb+xr]
						If BR = B
							If y > 1 Then FT = PMPixels[yt2+x]
							If BR = B And FT = A Then P_4 = B
						EndIf
					EndIf

				' if C = D then check far left and bottom right pixel to see if it's necessary 
				' to modify P_4 as well
				ElseIf C = D
					P_3 = C
					If x > 1 Then FL = PMPixels[yc+xl2]
					If FL = C
						If x > 0 And y < Pix_H - 1 Then BR = PMPixels[yb+xr]
						If FL = C And BR = D Then P_4 = C
					EndIf

					If FL <> C Or BR <> D
						If x > 0 And y > 0 Then TL = PMPixels[yt+xl]
						If TL = C
							If y < Pix_H - 2 Then FB = PMPixels[yb2+x]
							If TL = C And FB = D Then P_1 = C
						EndIf
					EndIf

				' if B = D then check far right and bottom left pixel to see if it's necessary 
				' to modify P_3 as well
				ElseIf B = D
					P_4 = D
					If x < Pix_W - 2 Then FR = PMPixels[yc+xr2]
					If FR = B
						If x > 0 And y < Pix_H - 1 Then BL = PMPixels[yb+xl]
						If FR = B And BL = D Then P_3 = D
					EndIf

					If FR <> B Or BL <> D
						If x < Pix_W - 1 And y > 0 Then TR = PMPixels[yt+xr]
						If TR = B
							If y < Pix_H - 2 Then FB = PMPixels[yb2+x]
							If TR = B And FB = D Then P_2 = B
						EndIf
					EndIf
				EndIf
			EndIf


			' if equal pixel conditions aren't met then check for similar pixels instead:
			If P_1 = P And A <> C	' P_1 is unmodified and A & C are similar but not equal
				' if A is similar to C and very different from P then turn P_1 into C
				If A_Value < C_Value + 40 And A_Value > C_Value - 40 And (P_Value >= A_Value + 40 Or P_Value <= A_Value - 40) And B <> D And A <> B
					If A <> D And B <> C Then P_1 = C
				EndIf
			EndIf
			If P_2 = P And A <> B	' P_2 is unmodified and A & B are similar but not equal
				' if B is similar to A and very different from P then turn P_2 into A
				If A_Value < B_Value + 40 And A_Value > B_Value - 40 And (P_Value >= B_Value + 40 Or P_Value <= B_Value - 40) And A <> C And B <> D
					If B <> C And A <> D Then P_2 = A
				EndIf
			EndIf
			If P_3 = P And C <> D	' P_3 is unmodified and C & D are similar but not equal
				' if C is similar to D and very different from P then turn P_3 into D
				If D_Value < C_Value + 40 And D_Value > C_Value - 40 And (P_Value >= C_Value + 40 Or P_Value <= C_Value - 40) And B <> D And C <> A
					If A <> D And B <> C Then P_3 = D
				EndIf
			EndIf
			If P_4 = P And B <> D	' P_4 is unmodified and B & D are similar but not equal
				' if D is similar to B and very different from P then turn P_4 into B
				If B_Value < D_Value + 40 And B_Value > D_Value - 40 And (P_Value >= D_Value + 40 Or P_Value <= D_Value - 40) And B <> A And D <> C
					If B <> C And A <> D Then P_4 = B
				EndIf
			EndIf

			x2PMPixels[(y*2  )*ScaledPM.Width+(x*2  )] = P_1
			x2PMPixels[(y*2  )*ScaledPM.width+(x*2+1)] = P_2
			x2PMPixels[(y*2+1)*ScaledPM.width+(x*2  )] = P_3
			x2PMPixels[(y*2+1)*ScaledPM.Width+(x*2+1)] = P_4
		Next
	Next

	OutputPixmap = ScaledPM

End Function



' reads more pixels in some cases - smoother results and less artifacts at roughly the same speed
Function ScaleEX_HQ(PM:TPixmap)
	Local PMPixels: Int Ptr = Int Ptr(pm.pixels)
	Local Pix_W:Int = PixmapWidth(PM)
	Local Pix_H:Int = PixmapHeight(PM)
	Local x:Int
	Local y:Int
	Local yt:Int
	Local yb:Int
	Local xl:Int
	Local xr:Int
	Local yt2:Int
	Local yb2:Int
	Local xl2:Int
	Local xr2:Int
	
	Local ScaledPM:TPixmap = CreatePixmap(Pix_W*2,Pix_H*2,PF_RGBA8888)
	Local x2PMPixels:Int Ptr = Int Ptr (ScaledPM.pixels)
	
	Local x2Pix_W:Int = PixmapWidth(ScaledPM)
	Local x2Pix_H:Int = PixmapHeight(ScaledPM)
	
	Local P:Int
	Local A:Int
	Local B:Int
	Local C:Int
	Local D:Int
	Local FL:Int	' far left pixel (beside C)
	Local TR:Int	' top right pixel
	Local FR:Int	' far right pixel (beside B)
	Local TL:Int	' top left pixel
	Local FT:Int	' far top pixel
	Local FB:Int	' far bottom pixel
	Local BL:Int	' bottom left pixel
	Local BR:Int	' bottom right pixel
	
	Local P_1:Int	' output pixel top-left
	Local P_2:Int	' output pixel top-right
	Local P_3:Int	' output pixel bottom-left
	Local P_4:Int	' output pixel bottom-right

	' RotSprite vars
	Local P_Clr:Int[4]	' 1 = R, 2 = G, 3 = B
	Local A_Clr:Int[4]	' 1 = R, 2 = G, 3 = B
	Local B_Clr:Int[4]	' 1 = R, 2 = G, 3 = B
	Local C_Clr:Int[4]	' 1 = R, 2 = G, 3 = B
	Local D_Clr:Int[4]	' 1 = R, 2 = G, 3 = B
	Local P_Value:Int		' holds average of RGB values
	Local A_Value:Int
	Local B_Value:Int
	Local C_Value:Int
	Local D_Value:Int
	
	' go through all pixels in the image
	For x = 0 To Pix_W - 1
		For y = 0 To Pix_H - 1
			yt = (y-1)*Pix_W
			yb = (y+1)*Pix_W
			yt2 = (y-2)*Pix_W
			yb2 = (y+2)*Pix_W
			xl2 = (x-2)
			xr2 = (x+2)
			xl = x-1
			xr = x+1
			Local yc:Int = y*pix_W
			' read the source pixel and its surrounding pixels
			P = PMPixels[yc+x]
			If x > 0 And x < Pix_W - 1 And y > 0 And y < Pix_H - 1
				A = PMPixels[yt+x]
				B = PMPixels[yc+xr]
				C = PMPixels[yc+xl]
				D = PMPixels[yb+x]
			Else
				A = P
				B = P
				C = P
				D = P
			EndIf

			P_1 = P
			P_2 = P
			P_3 = P
			P_4 = P

			' get colors
			P_Clr[1] = (P & $FF0000) Shr 16	' red
			P_Clr[2] = (P & $FF00) Shr 8		' green
			P_Clr[3] = (P & $FF)			' blue
			A_Clr[1] = (A & $FF0000) Shr 16	' red
			A_Clr[2] = (A & $FF00) Shr 8		' green
			A_Clr[3] = (A & $FF)			' blue
			B_Clr[1] = (B & $FF0000) Shr 16	' red
			B_Clr[2] = (B & $FF00) Shr 8		' green
			B_Clr[3] = (B & $FF)			' blue
			C_Clr[1] = (C & $FF0000) Shr 16	' red
			C_Clr[2] = (C & $FF00) Shr 8		' green
			C_Clr[3] = (C & $FF)			' blue
			D_Clr[1] = (D & $FF0000) Shr 16	' red
			D_Clr[2] = (D & $FF00) Shr 8		' green
			D_Clr[3] = (D & $FF)			' blue

			' set average value from RGB
			P_Value = (P_Clr[1] + P_Clr[2] + P_Clr[3]) / 3
			A_Value = (A_Clr[1] + A_Clr[2] + A_Clr[3]) / 3
			B_Value = (B_Clr[1] + B_Clr[2] + B_Clr[3]) / 3
			C_Value = (C_Clr[1] + C_Clr[2] + C_Clr[3]) / 3
			D_Value = (D_Clr[1] + D_Clr[2] + D_Clr[3]) / 3

			' check for equal pixels around P and modify output pixels accordingly
			If B <> C And A <> D And (A = C Or A = B Or C = D Or B = D)
				' if A = C then check far left and top right pixel to see if it's necessary 
				' to modify P_2 as well
				If A = C
					P_1 = A
					' check pixel at far left and top right to see if stepping needs easing
					If x > 1 Then FL = PMPixels[yc+xl2]
					' if FL = C then read another pixel: TR
					If FL = C
						If x < Pix_W - 1 And y > 0 Then TR = PMPixels[yt+xr]
						' If both = A && C then set P_2 to be A as well.
						If FL = C And TR = A Then P_2 = A
					EndIf

					' If FL is C and TR is A then we already know what to smooth here so
					' we can skip checking for vertical stepping and save CPU load. But if not
					' then do another check for P_3 to see if the bottom left and far top 
					' pixels are the same.
					If FL <> C Or TR <> A
						' check pixel at far top and bottom left to see if stepping needs easing
						If x > 0 And y < Pix_H - 1 Then BL = PMPixels[yb+xl]
						If BL = C
							If y > 1 Then FT = PMPixels[yt2+x]
							' If both = A && C then set P_3 to be C as well
							If BL = C And FT = A Then P_3 = C
						EndIf
					EndIf

				' if A = B then check far right and top left pixel to see if it's necessary 
				' to modify P_1 as well
				ElseIf A = B
					P_2 = B
					If x < Pix_W - 2 Then FR = PMPixels[yc+xr2]
					If FR = B
						If x > 0 And y > 0 Then TL = PMPixels[yt+xl]
						If FR = B And TL = A Then P_1 = B
					EndIf

					If FR <> B Or TL <> A
						If x < Pix_W - 1 And y < Pix_H - 1 Then BR = PMPixels[yb+xr]
						If BR = B
							If y > 1 Then FT = PMPixels[yt2+x]
							If BR = B And FT = A Then P_4 = B
						EndIf
					EndIf

				' if C = D then check far left and bottom right pixel to see if it's necessary 
				' to modify P_4 as well
				ElseIf C = D
					P_3 = C
					If x > 1 Then FL = PMPixels[yc+xl2]
					If FL = C
						If x > 0 And y < Pix_H - 1 Then BR = PMPixels[yb+xr]
						If FL = C And BR = D Then P_4 = C
					EndIf

					If FL <> C Or BR <> D
						If x > 0 And y > 0 Then TL = PMPixels[yt+xl]
						If TL = C
							If y < Pix_H - 2 Then FB = PMPixels[yb2+x]
							If TL = C And FB = D Then P_1 = C
						EndIf
					EndIf

				' if B = D then check far right and bottom left pixel to see if it's necessary 
				' to modify P_3 as well
				ElseIf B = D
					P_4 = D
					If x < Pix_W - 2 Then FR = PMPixels[yc+xr2]
					If FR = B
						If x > 0 And y < Pix_H - 1 Then BL = PMPixels[yb+xl]
						If FR = B And BL = D Then P_3 = D
					EndIf

					If FR <> B Or BL <> D
						If x < Pix_W - 1 And y > 0 Then TR = PMPixels[yt+xr]
						If TR = B
							If y < Pix_H - 2 Then FB = PMPixels[yb2+x]
							If TR = B And FB = D Then P_2 = B
						EndIf
					EndIf
				EndIf
			EndIf


			' if equal pixel conditions aren't met then check for similar pixels instead:
			If P_1 = P And A <> C	' P_1 is unmodified and A & C are similar but not equal
				' if A is similar to C and very different from P then turn P_1 into C
				If A_Value < C_Value + 40 And A_Value > C_Value - 40 And (P_Value >= A_Value + 40 Or P_Value <= A_Value - 40) And B <> D And A <> B
					If A <> D And B <> C
						' check top-left pixel; only modify P_1 if TL <> P (no diagonal line from TL to P)
						If x > 0 And y > 0 Then TL = PMPixels[yt+xl]
						If TL <> P Then P_1 = C
					EndIf
				EndIf
			EndIf
			If P_2 = P And A <> B	' P_2 is unmodified and A & B are similar but not equal
				' if B is similar to A and very different from P then turn P_2 into A
				If A_Value < B_Value + 40 And A_Value > B_Value - 40 And (P_Value >= B_Value + 40 Or P_Value <= B_Value - 40) And A <> C And B <> D
					If B <> C And A <> D
						' check top-right pixel; only modify P_2 if TR <> P (no diagonal line from TR to P)
						If x < Pix_W - 1 And y > 0 Then TR = PMPixels[yt+xr]
						If TR <> P Then P_2 = A
					EndIf
				EndIf
			EndIf
			If P_3 = P And C <> D	' P_3 is unmodified and C & D are similar but not equal
				' if C is similar to D and very different from P then turn P_3 into D
				If D_Value < C_Value + 40 And D_Value > C_Value - 40 And (P_Value >= C_Value + 40 Or P_Value <= C_Value - 40) And B <> D And C <> A
					If A <> D And B <> C
						' check bottom-left pixel; only modify P_3 if BL <> P (no diagonal line from BL to P)
						If x > 0 And y < Pix_H - 1 Then BL = PMPixels[yb+xl]
						If BL <> P Then P_3 = D
					EndIf
				EndIf
			EndIf
			If P_4 = P And B <> D	' P_4 is unmodified and B & D are similar but not equal
				' if D is similar to B and very different from P then turn P_4 into B
				If B_Value < D_Value + 40 And B_Value > D_Value - 40 And (P_Value >= D_Value + 40 Or P_Value <= D_Value - 40) And B <> A And D <> C
					If B <> C And A <> D
						' check bottom-right pixel; only modify P_4 if BR <> P (no diagonal line from BR to P)
						If x < Pix_W - 1 And y < Pix_H - 1 Then BR = PMPixels[yb+xr]
						If BR <> P Then P_4 = B
					EndIf
				EndIf
			EndIf

			x2PMPixels[(y*2  )*ScaledPM.Width+(x*2  )] = P_1
			x2PMPixels[(y*2  )*ScaledPM.width+(x*2+1)] = P_2
			x2PMPixels[(y*2+1)*ScaledPM.width+(x*2  )] = P_3
			x2PMPixels[(y*2+1)*ScaledPM.Width+(x*2+1)] = P_4
		Next
	Next

	OutputPixmap = ScaledPM

End Function



' smooths pixels by color groups instead of just RGB averages,
' generally smoother and less artifacty than ScaleEX_HQ but MUCH slower.
Function ScaleEX_SQ(PM:TPixmap)

	Local PMPixels: Int Ptr = Int Ptr(pm.pixels)
	Local Pix_W:Int = PixmapWidth(PM)
	Local Pix_H:Int = PixmapHeight(PM)
	Local x:Int
	Local y:Int
	Local yt:Int
	Local yb:Int
	Local xl:Int
	Local xr:Int
	Local yt2:Int
	Local yb2:Int
	Local xl2:Int
	Local xr2:Int

	Local ScaledPM:TPixmap = CreatePixmap(Pix_W*2,Pix_H*2,PF_RGBA8888)
	Local x2PMPixels:Int Ptr = Int Ptr (ScaledPM.pixels)
	
	Local x2Pix_W:Int = PixmapWidth(ScaledPM)
	Local x2Pix_H:Int = PixmapHeight(ScaledPM)
	
	Local P:Int
	Local A:Int
	Local B:Int
	Local C:Int
	Local D:Int
	Local FL:Int	' far left pixel (beside C)
	Local TR:Int	' top right pixel
	Local FR:Int	' far right pixel (beside B)
	Local TL:Int	' top left pixel
	Local FT:Int	' far top pixel
	Local FB:Int	' far bottom pixel
	Local BL:Int	' bottom left pixel
	Local BR:Int	' bottom right pixel

	Local P_1:Int	' output pixel top-left
	Local P_2:Int	' output pixel top-right
	Local P_3:Int	' output pixel bottom-left
	Local P_4:Int	' output pixel bottom-right

	' RotSprite vars
	Local P_Clr:Int[4]	' 1 = R, 2 = G, 3 = B
	Local A_Clr:Int[4]	' 1 = R, 2 = G, 3 = B
	Local B_Clr:Int[4]	' 1 = R, 2 = G, 3 = B
	Local C_Clr:Int[4]	' 1 = R, 2 = G, 3 = B
	Local D_Clr:Int[4]	' 1 = R, 2 = G, 3 = B
	Local P_Value:Int		' holds average of RGB values
	Local A_Value:Int
	Local B_Value:Int
	Local C_Value:Int
	Local D_Value:Int

	' Sort by color
	Local Red:Int = 1
	Local Orange:Int = 2
	Local Yellow:Int = 3
	Local Army:Int = 4
	Local Green:Int = 5
	Local Jade:Int = 6
	Local Cyan:Int = 7
	Local Azure:Int = 8
	Local Blue:Int = 9
	Local Indigo:Int = 10
	Local Purple:Int = 11
	Local Magenta:Int = 12
	Local Grey:Int = 20
	Local P_Class:Int
	Local A_Class:Int
	Local B_Class:Int
	Local C_Class:Int
	Local D_Class:Int


	' go through all pixels in the image
	For x = 0 To Pix_W - 1
		For y = 0 To Pix_H - 1
			yt = (y-1)*Pix_W
			yb = (y+1)*Pix_W
			yt2 = (y-2)*Pix_W
			yb2 = (y+2)*Pix_W
			xl2 = (x-2)
			xr2 = (x+2)
			xl = x-1
			xr = x+1
			Local yc:Int = y*pix_W
			' read the source pixel and its surrounding pixels
			P = PMPixels[yc+x]
			If x > 0 And x < Pix_W - 1 And y > 0 And y < Pix_H - 1
				A = PMPixels[yt+x]
				B = PMPixels[yc+xr]
				C = PMPixels[yc+xl]
				D = PMPixels[yb+x]
			Else
				A = P
				B = P
				C = P
				D = P
			EndIf

			P_1 = P
			P_2 = P
			P_3 = P
			P_4 = P

			' get colors
			P_Clr[1] = (P & $FF0000) Shr 16	' red
			P_Clr[2] = (P & $FF00) Shr 8		' green
			P_Clr[3] = (P & $FF)			' blue
			A_Clr[1] = (A & $FF0000) Shr 16	' red
			A_Clr[2] = (A & $FF00) Shr 8		' green
			A_Clr[3] = (A & $FF)			' blue
			B_Clr[1] = (B & $FF0000) Shr 16	' red
			B_Clr[2] = (B & $FF00) Shr 8		' green
			B_Clr[3] = (B & $FF)			' blue
			C_Clr[1] = (C & $FF0000) Shr 16	' red
			C_Clr[2] = (C & $FF00) Shr 8		' green
			C_Clr[3] = (C & $FF)			' blue
			D_Clr[1] = (D & $FF0000) Shr 16	' red
			D_Clr[2] = (D & $FF00) Shr 8		' green
			D_Clr[3] = (D & $FF)			' blue

			' set average value from RGB
			P_Value = (P_Clr[1] + P_Clr[2] + P_Clr[3]) / 3
			A_Value = (A_Clr[1] + A_Clr[2] + A_Clr[3]) / 3
			B_Value = (B_Clr[1] + B_Clr[2] + B_Clr[3]) / 3
			C_Value = (C_Clr[1] + C_Clr[2] + C_Clr[3]) / 3
			D_Value = (D_Clr[1] + D_Clr[2] + D_Clr[3]) / 3

			' get color class
			' red
			If A_Clr[1] > A_Clr[2] + 21 And A_Clr[1] > A_Clr[3] + 21 And (A_Clr[2] < A_Clr[3] + 21 And A_Clr[2] > A_Clr[3] - 21) Then A_Class = Red
			' orange
			If A_Clr[1] > A_Clr[3] + 21 And A_Clr[2] <= A_Clr[1] - 21 And A_Clr[2] >= A_Clr[3] + 21 Then A_Class = Orange
			' yellow
			If A_Clr[1] <= A_Clr[2] + 21 And A_Clr[1] >= A_Clr[2] - 21 And A_Clr[1] >= A_Clr[3] + 21 Then A_Class = Yellow
			' army green
			If A_Clr[2] > A_Clr[3] + 21 And A_Clr[1] <= A_Clr[2] - 21 And A_Clr[1] >= A_Clr[3] + 21 Then A_Class = Army
			' green
			If A_Clr[2] > A_Clr[1] + 21 And A_Clr[2] > A_Clr[3] + 21 And (A_Clr[1] < A_Clr[3] + 21 And A_Clr[1] > A_Clr[3] - 21) Then A_Class = Green
			' jade green
			If A_Clr[2] > A_Clr[1] + 21 And A_Clr[3] <= A_Clr[2] - 21 And A_Clr[3] >= A_Clr[1] + 21 Then A_Class = Jade
			' Cyan
			If A_Clr[2] <= A_Clr[3] + 21 And A_Clr[2] >= A_Clr[3] - 21 And A_Clr[3] >= A_Clr[1] + 21 Then A_Class = Cyan
			' Azure blue
			If A_Clr[3] > A_Clr[1] + 21 And A_Clr[2] <= A_Clr[3] - 21 And A_Clr[2] >= A_Clr[1] + 21 Then A_Class = Azure
			' blue
			If A_Clr[3] > A_Clr[2] + 21 And A_Clr[3] > A_Clr[1] + 21 And (A_Clr[1] < A_Clr[2] + 21 And A_Clr[1] > A_Clr[2] - 21) Then A_Class = Blue
			' indigo
			If A_Clr[3] > A_Clr[2] + 21 And A_Clr[1] <= A_Clr[3] - 21 And A_Clr[1] >= A_Clr[2] + 21 Then A_Class = Indigo
			' purple
			If A_Clr[1] <= A_Clr[3] + 21 And A_Clr[1] >= A_Clr[3] - 21 And A_Clr[1] >= A_Clr[2] + 21 Then A_Class = Purple
			' magenta
			If A_Clr[1] > A_Clr[2] + 21 And A_Clr[3] <= A_Clr[1] - 21 And A_Clr[3] >= A_Clr[2] + 21 Then A_Class = Magenta
			' greyscales (includes desaturated colors)
			If A_Clr[1] <= A_Clr[2] + 21 And A_Clr[1] >= A_Clr[2] - 21 And A_Clr[1] <= A_Clr[3] + 21 And A_Clr[1] >= A_Clr[3] - 21 Then A_Class = Grey

			' red
			If B_Clr[1] > B_Clr[2] + 21 And B_Clr[1] > B_Clr[3] + 21 And (B_Clr[2] < B_Clr[3] + 21 And B_Clr[2] > B_Clr[3] - 21) Then B_Class = Red
			' orange
			If B_Clr[1] > B_Clr[3] + 21 And B_Clr[2] <= B_Clr[1] - 21 And B_Clr[2] >= B_Clr[3] + 21 Then B_Class = Orange
			' yellow
			If B_Clr[1] <= B_Clr[2] + 21 And B_Clr[1] >= B_Clr[2] - 21 And B_Clr[1] >= B_Clr[3] + 21 Then B_Class = Yellow
			' army green
			If B_Clr[2] > B_Clr[3] + 21 And B_Clr[1] <= B_Clr[2] - 21 And B_Clr[1] >= B_Clr[3] + 21 Then B_Class = Army
			' green
			If B_Clr[2] > B_Clr[1] + 21 And B_Clr[2] > B_Clr[3] + 21 And (B_Clr[1] < B_Clr[3] + 21 And B_Clr[1] > B_Clr[3] - 21) Then B_Class = Green
			' jade green
			If B_Clr[2] > B_Clr[1] + 21 And B_Clr[3] <= B_Clr[2] - 21 And B_Clr[3] >= B_Clr[1] + 21 Then B_Class = Jade
			' Cyan
			If B_Clr[2] <= B_Clr[3] + 21 And B_Clr[2] >= B_Clr[3] - 21 And B_Clr[3] >= B_Clr[1] + 21 Then B_Class = Cyan
			' Azure blue
			If B_Clr[3] > B_Clr[1] + 21 And B_Clr[2] <= B_Clr[3] - 21 And B_Clr[2] >= B_Clr[1] + 21 Then B_Class = Azure
			' blue
			If B_Clr[3] > B_Clr[2] + 21 And B_Clr[3] > B_Clr[1] + 21 And (B_Clr[1] < B_Clr[2] + 21 And B_Clr[1] > B_Clr[2] - 21) Then B_Class = Blue
			' indigo
			If B_Clr[3] > B_Clr[2] + 21 And B_Clr[1] <= B_Clr[3] - 21 And B_Clr[1] >= B_Clr[2] + 21 Then B_Class = Indigo
			' purple
			If B_Clr[1] <= B_Clr[3] + 21 And B_Clr[1] >= B_Clr[3] - 21 And B_Clr[1] >= B_Clr[2] + 21 Then B_Class = Purple
			' magenta
			If B_Clr[1] > B_Clr[2] + 21 And B_Clr[3] <= B_Clr[1] - 21 And B_Clr[3] >= B_Clr[2] + 21 Then B_Class = Magenta
			' greyscales (includes desaturated colors)
			If B_Clr[1] <= B_Clr[2] + 21 And B_Clr[1] >= B_Clr[2] - 21 And B_Clr[1] <= B_Clr[3] + 21 And B_Clr[1] >= B_Clr[3] - 21 Then B_Class = Grey

			' red
			If C_Clr[1] > C_Clr[2] + 21 And C_Clr[1] > C_Clr[3] + 21 And (C_Clr[2] < C_Clr[3] + 21 And C_Clr[2] > C_Clr[3] - 21) Then C_Class = Red
			' orange
			If C_Clr[1] > C_Clr[3] + 21 And C_Clr[2] <= C_Clr[1] - 21 And C_Clr[2] >= C_Clr[3] + 21 Then C_Class = Orange
			' yellow
			If C_Clr[1] <= C_Clr[2] + 21 And C_Clr[1] >= C_Clr[2] - 21 And C_Clr[1] >= C_Clr[3] + 21 Then C_Class = Yellow
			' army green
			If C_Clr[2] > C_Clr[3] + 21 And C_Clr[1] <= C_Clr[2] - 21 And C_Clr[1] >= C_Clr[3] + 21 Then C_Class = Army
			' green
			If C_Clr[2] > C_Clr[1] + 21 And C_Clr[2] > C_Clr[3] + 21 And (C_Clr[1] < C_Clr[3] + 21 And C_Clr[1] > C_Clr[3] - 21) Then C_Class = Green
			' jade green
			If C_Clr[2] > C_Clr[1] + 21 And C_Clr[3] <= C_Clr[2] - 21 And C_Clr[3] >= C_Clr[1] + 21 Then C_Class = Jade
			' Cyan
			If C_Clr[2] <= C_Clr[3] + 21 And C_Clr[2] >= C_Clr[3] - 21 And C_Clr[3] >= C_Clr[1] + 21 Then C_Class = Cyan
			' Azure blue
			If C_Clr[3] > C_Clr[1] + 21 And C_Clr[2] <= C_Clr[3] - 21 And C_Clr[2] >= C_Clr[1] + 21 Then C_Class = Azure
			' blue
			If C_Clr[3] > C_Clr[2] + 21 And C_Clr[3] > C_Clr[1] + 21 And (C_Clr[1] < C_Clr[2] + 21 And C_Clr[1] > C_Clr[2] - 21) Then C_Class = Blue
			' indigo
			If C_Clr[3] > C_Clr[2] + 21 And C_Clr[1] <= C_Clr[3] - 21 And C_Clr[1] >= C_Clr[2] + 21 Then C_Class = Indigo
			' purple
			If C_Clr[1] <= C_Clr[3] + 21 And C_Clr[1] >= C_Clr[3] - 21 And C_Clr[1] >= C_Clr[2] + 21 Then C_Class = Purple
			' magenta
			If C_Clr[1] > C_Clr[2] + 21 And C_Clr[3] <= C_Clr[1] - 21 And C_Clr[3] >= C_Clr[2] + 21 Then C_Class = Magenta
			' greyscales (includes desaturated colors)
			If C_Clr[1] <= C_Clr[2] + 21 And C_Clr[1] >= C_Clr[2] - 21 And C_Clr[1] <= C_Clr[3] + 21 And C_Clr[1] >= C_Clr[3] - 21 Then C_Class = Grey

			' red
			If D_Clr[1] > D_Clr[2] + 21 And D_Clr[1] > D_Clr[3] + 21 And (D_Clr[2] < D_Clr[3] + 21 And D_Clr[2] > D_Clr[3] - 21) Then D_Class = Red
			' orange
			If D_Clr[1] > D_Clr[3] + 21 And D_Clr[2] <= D_Clr[1] - 21 And D_Clr[2] >= D_Clr[3] + 21 Then D_Class = Orange
			' yellow
			If D_Clr[1] <= D_Clr[2] + 21 And D_Clr[1] >= D_Clr[2] - 21 And D_Clr[1] >= D_Clr[3] + 21 Then D_Class = Yellow
			' army green
			If D_Clr[2] > D_Clr[3] + 21 And D_Clr[1] <= D_Clr[2] - 21 And D_Clr[1] >= D_Clr[3] + 21 Then D_Class = Army
			' green
			If D_Clr[2] > D_Clr[1] + 21 And D_Clr[2] > D_Clr[3] + 21 And (D_Clr[1] < D_Clr[3] + 21 And D_Clr[1] > D_Clr[3] - 21) Then D_Class = Green
			' jade green
			If D_Clr[2] > D_Clr[1] + 21 And D_Clr[3] <= D_Clr[2] - 21 And D_Clr[3] >= D_Clr[1] + 21 Then D_Class = Jade
			' Cyan
			If D_Clr[2] <= D_Clr[3] + 21 And D_Clr[2] >= D_Clr[3] - 21 And D_Clr[3] >= D_Clr[1] + 21 Then D_Class = Cyan
			' Azure blue
			If D_Clr[3] > D_Clr[1] + 21 And D_Clr[2] <= D_Clr[3] - 21 And D_Clr[2] >= D_Clr[1] + 21 Then D_Class = Azure
			' blue
			If D_Clr[3] > D_Clr[2] + 21 And D_Clr[3] > D_Clr[1] + 21 And (D_Clr[1] < D_Clr[2] + 21 And D_Clr[1] > D_Clr[2] - 21) Then D_Class = Blue
			' indigo
			If D_Clr[3] > D_Clr[2] + 21 And D_Clr[1] <= D_Clr[3] - 21 And D_Clr[1] >= D_Clr[2] + 21 Then D_Class = Indigo
			' purple
			If D_Clr[1] <= D_Clr[3] + 21 And D_Clr[1] >= D_Clr[3] - 21 And D_Clr[1] >= D_Clr[2] + 21 Then D_Class = Purple
			' magenta
			If D_Clr[1] > D_Clr[2] + 21 And D_Clr[3] <= D_Clr[1] - 21 And D_Clr[3] >= D_Clr[2] + 21 Then D_Class = Magenta
			' greyscales (includes desaturated colors)
			If D_Clr[1] <= D_Clr[2] + 21 And D_Clr[1] >= D_Clr[2] - 21 And D_Clr[1] <= D_Clr[3] + 21 And D_Clr[1] >= D_Clr[3] - 21 Then D_Class = Grey



			' check for equal pixels around P and modify output pixels accordingly
			If B <> C And A <> D And (A = C Or A = B Or C = D Or B = D)
				' if A = C then check far left and top right pixel to see if it's necessary 
				' to modify P_2 as well
				If A = C
					P_1 = A
					' check pixel at far left and top right to see if stepping needs easing
					If x > 1 Then FL = PMPixels[yc+xl2]
					' if FL = C then read another pixel: TR
					If FL = C
						If x < Pix_W - 1 And y > 0 Then TR = PMPixels[yt+xr]
						' If both = A && C then set P_2 to be A as well.
						If FL = C And TR = A Then P_2 = A
					EndIf

					' If FL is C and TR is A then we already know what to smooth here so
					' we can skip checking for vertical stepping and save CPU load. But if not
					' then do another check for P_3 to see if the bottom left and far top 
					' pixels are the same.
					If FL <> C Or TR <> A
						' check pixel at far top and bottom left to see if stepping needs easing
						If x > 0 And y < Pix_H - 1 Then BL = PMPixels[yb+xl]
						If BL = C
							If y > 1 Then FT = PMPixels[yt2+x]
							' If both = A && C then set P_3 to be C as well
							If BL = C And FT = A Then P_3 = C
						EndIf
					EndIf

				' if A = B then check far right and top left pixel to see if it's necessary 
				' to modify P_1 as well
				ElseIf A = B
					P_2 = B
					If x < Pix_W - 2 Then FR = PMPixels[yc+xr2]
					If FR = B
						If x > 0 And y > 0 Then TL = PMPixels[yt+xl]
						If FR = B And TL = A Then P_1 = B
					EndIf

					If FR <> B Or TL <> A
						If x < Pix_W - 1 And y < Pix_H - 1 Then BR = PMPixels[yb+xr]
						If BR = B
							If y > 1 Then FT = PMPixels[yt2+x]
							If BR = B And FT = A Then P_4 = B
						EndIf
					EndIf

				' if C = D then check far left and bottom right pixel to see if it's necessary 
				' to modify P_4 as well
				ElseIf C = D
					P_3 = C
					If x > 1 Then FL = PMPixels[yc+xl2]
					If FL = C
						If x > 0 And y < Pix_H - 1 Then BR = PMPixels[yb+xr]
						If FL = C And BR = D Then P_4 = C
					EndIf

					If FL <> C Or BR <> D
						If x > 0 And y > 0 Then TL = PMPixels[yt+xl]
						If TL = C
							If y < Pix_H - 2 Then FB = PMPixels[yb2+x]
							If TL = C And FB = D Then P_1 = C
						EndIf
					EndIf

				' if B = D then check far right and bottom left pixel to see if it's necessary 
				' to modify P_3 as well
				ElseIf B = D
					P_4 = D
					If x < Pix_W - 2 Then FR = PMPixels[yc+xr2]
					If FR = B
						If x > 0 And y < Pix_H - 1 Then BL = PMPixels[yb+xl]
						If FR = B And BL = D Then P_3 = D
					EndIf

					If FR <> B Or BL <> D
						If x < Pix_W - 1 And y > 0 Then TR = PMPixels[yt+xr]
						If TR = B
							If y < Pix_H - 2 Then FB = PMPixels[yb2+x]
							If TR = B And FB = D Then P_2 = B
						EndIf
					EndIf
				EndIf
			EndIf




			' if equal pixel conditions aren't met then check for similar pixels instead:
			If P_1 = P And A <> C	' P_1 is unmodified and A & C are similar but not equal
				' if A is similar to C and very different from P then turn P_1 into C
				If A_Value < C_Value + 70 And A_Value > C_Value - 70 And ((P_Value >= A_Value + 70 Or P_Value <= A_Value - 70) Or (P_Value >= C_Value + 70 Or P_Value <= C_Value - 70)) And B <> D And A <> B
					If A <> D And B <> C And (A_Class <= C_Class + 1 And (A_Class >= C_Class - 1 And C_Class >= 1))	' same color?
						' check top-left pixel; only modify P_1 if TL <> P (no diagonal line from TL to P)
						If x > 0 And y > 0 Then TL = PMPixels[yt+xl]
						If TL <> P Then P_1 = C
					EndIf
				EndIf
			EndIf
			If P_2 = P And A <> B	' P_2 is unmodified and A & B are similar but not equal
				' if B is similar to A and very different from P then turn P_2 into A
				If A_Value < B_Value + 70 And A_Value > B_Value - 70 And ((P_Value >= B_Value + 70 Or P_Value <= B_Value - 70) Or (P_Value >= B_Value + 70 Or P_Value <= B_Value - 70)) And A <> C And B <> D
					If B <> C And A <> D And (A_Class <= B_Class + 1 And (A_Class >= B_Class - 1 And B_Class >= 1))	' same color?
						' check top-right pixel; only modify P_2 if TR <> P (no diagonal line from TR to P)
						If x < Pix_W - 1 And y > 0 Then TR = PMPixels[yt+xr]
						If TR <> P Then P_2 = A
					EndIf
				EndIf
			EndIf
			If P_3 = P And C <> D	' P_3 is unmodified and C & D are similar but not equal
				' if C is similar to D and very different from P then turn P_3 into D
				If D_Value < C_Value + 70 And D_Value > C_Value - 70 And ((P_Value >= C_Value + 70 Or P_Value <= C_Value - 70) Or (P_Value >= D_Value + 70 Or P_Value <= D_Value - 70)) And B <> D And C <> A
					If A <> D And B <> C And P_1 <> C And P_2 <> A And (C_Class <= D_Class + 1 And (C_Class >= D_Class - 1 And D_Class >= 1))	' same color?
						' check bottom-left pixel; only modify P_3 if BL <> P (no diagonal line from BL to P)
						If x > 0 And y < Pix_H - 1 Then BL = PMPixels[yb+xl]
						If BL <> P Then P_3 = D
					EndIf
				EndIf
			EndIf

			If P_4 = P And B <> D	' P_4 is unmodified and B & D are similar but not equal
				' if D is similar to B and very different from P then turn P_4 into B
				If B_Value < D_Value + 70 And B_Value > D_Value - 70 And ((P_Value >= D_Value + 70 Or P_Value <= D_Value - 70) Or (P_Value >= B_Value + 70 Or P_Value <= B_Value - 70)) And B <> A And D <> C
					If B <> C And A <> D And P_1 <> C And P_2 <> A And (B_Class <= D_Class + 1 And (B_Class >= D_Class - 1 And D_Class >= 1))	' same color?
						' check bottom-right pixel; only modify P_4 if BR <> P (no diagonal line from BR to P)
						If x < Pix_W - 1 And y < Pix_H - 1 Then BR = PMPixels[yb+xr]
						If BR <> P Then P_4 = B
					EndIf
				EndIf
			EndIf


			x2PMPixels[(y*2  )*ScaledPM.Width+(x*2  )] = P_1
			x2PMPixels[(y*2  )*ScaledPM.width+(x*2+1)] = P_2
			x2PMPixels[(y*2+1)*ScaledPM.width+(x*2  )] = P_3
			x2PMPixels[(y*2+1)*ScaledPM.Width+(x*2+1)] = P_4
		Next
	Next

	OutputPixmap = ScaledPM

End Function




Global InputPixmap:TPixmap = LoadPixmap("input.bmp")	' alpha'ed pngs also work
inputpixmap = inputPixmap.convert(PF_RGBA8888)
Global OutputPixmap:TPixmap

Global TestImg:TImage



Graphics 800,600,32

Local secs:Int= MilliSecs()
ScaleEX(InputPixmap)
Print MilliSecs() - secs
TestImg = LoadImage(OutputPixmap)


While Not KeyDown(KEY_ESCAPE)

	Cls
		DrawImage(TestImg,0,0)
	Flip

Wend

End
