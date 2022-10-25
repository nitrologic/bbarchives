; ID: 2333
; Author: Yasha
; Date: 2008-10-11 21:21:13
; Title: HSL and RGB conversion
; Description: Convert from RGB to HSL and back

Function HSLtoRGB(H,S,L)
	Local hk#=H/255.0
	Local sk#=S/255.0
	Local lk#=L/255.0
	Local p#,q#
	
	If lk<0.5 Then q=lk*(1+sk):Else q=lk+sk-(lk*sk)
	p=2*lk-q
	
	Local tR#=hk+0.3333:If tR>1 Then tR=tR-1
	Local tG#=hk
	Local tB#=hk-0.3333:If tB<0 Then tB=tB+1
	
	Local cR#=p,cG#=p,cB#=p
	
	If tR<0.1666
		cR=p+((q-p)*6*tR)
	ElseIf tR<0.5 And tR>=0.1666
		cR=q
	ElseIf tR<0.6666 And tR>=0.5
		cR=p+((q-p)*6*(0.6666-tR))
	EndIf
	
	If tG<0.1666
		cG=p+((q-p)*6*tG)
	ElseIf tG<0.5 And tG>=0.1666
		cG=q
	ElseIf tG<0.6666 And tG>=0.5
		cG=p+((q-p)*6*(0.6666-tG))
	EndIf
	
	If tB<0.1666
		cB=p+((q-p)*6*tB)
	ElseIf tB<0.5 And tB>=0.1666
		cB=q
	ElseIf tB<0.6666 And tB>=0.5
		cB=p+((q-p)*6*(0.6666-tB))
	EndIf
	
	Return (255*cR Shl 16) Or (255*cG Shl 8) Or 255*cB; Or $FF000000			;If you want an alpha value as well, add it here
End Function

Function RGBtoHSL(R,G,B)
	Local rk#=R/255.0,gk#=G/255.0,bk#=B/255.0
	Local max#=rk,min#=gk,h#,s#,l#
	
	If gk>max Then max=gk
	If bk>max Then max=bk
	If rk<min Then min=rk
	If bk<min Then min=bk
	
	If max=min
		h=0:s=0:l=max
	Else
		If max=rk
			h#=(60*(gk-bk)/(max-min)) Mod 360
		ElseIf max=gk
			h#=(60*(bk-rk)/(max-min)) + 120
		ElseIf max=bk
			h#=(60*(rk-gk)/(max-min)) + 240
		EndIf
		
		l#=(max+min)/2
		If l#<=0.5
			s#=(max-min)/(2*l)
		ElseIf l#>0.5
			s#=(max-min)/(2-2*l)
		EndIf
	EndIf
	
	Return ((h/360.0)*255 Shl 16) Or (s*255 Shl 8) Or l*255; Or $FF000000			;If you want an alpha value as well, add it here
End Function
