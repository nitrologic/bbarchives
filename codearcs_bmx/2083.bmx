; ID: 2083
; Author: maverick69
; Date: 2007-07-31 17:04:59
; Title: Amiga-like copper background
; Description: Generates typical amiga-like copper backgrounds

SuperStrict
Graphics 640,480

SetBlend(ALPHABLEND)

SeedRnd(MilliSecs())

Function GenerateCopperList:TImage(Steps:Float)

	Local R:Int[480]
	Local G:Int[480]
	Local B:Int[480]
	
	steps = 480.0/steps
	
	Local SR:Int, SG:Int, SB:Int, DR:Int, DG:Int, DB:Int
	
	Local i:Int = 0
	
	SR = Rand(255)
	SG = Rand(255)
	SB = Rand(255)

	DR = Rand(255)
	DG = Rand(255)
	DB = Rand(255)
	
	
	While (i<479)
		
		If i Mod steps = 0
			SR = DR
			SG = DG
			SB = DB
			DR = Rand(255)
			DG = Rand(255)
			DB = Rand(255)			
			
			R[i] = SR
			G[i] = SG
			B[i] = SB
		Else
			Local s:Int = i Mod steps
			Local prozent:Float = s/steps			
			
			If SR>DR
				R[i] = SR - ((s/steps)*((SR-DR)))
			Else
				R[i] = SR + ((s/steps)*((DR-SR)))
			End If

			If SG>DG
				G[i] = SG - ((s/steps)*((SG-DG)))
			Else
				G[i] = SG + ((s/steps)*((DG-SG)))
			End If

			If SB>DB
				B[i] = SB - ((s/steps)*((SB-DB)))
			Else
				B[i] = SB + ((s/steps)*((DB-SB)))
			End If
			
			
						
		End If
		
		i:+1
	Wend

	For Local y:Int = 0 To 479
		SetColor (R[y],G[y],B[y])
		DrawLine (0,y,639,y)
	Next
	Local img:TImage = CreateImage(640,480,1,DYNAMICIMAGE)
	GrabImage(img,0,0)
	SetColor (255,255,255)
	Return img
End Function

Local CopperBG:TImage = GenerateCopperList(8)

While (Not KeyDown(KEY_ESCAPE))
	Cls
	If KeyHit(KEY_Q) Then CopperBG = GenerateCopperList(Rand(2,16))
	DrawImage CopperBG,0,0	
	Flip
Wend

End
