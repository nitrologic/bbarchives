; ID: 2920
; Author: AntonyWells
; Date: 2012-02-13 09:55:23
; Title: AnnLib Example Code #1
; Description: Example 'chase' program

#DEMO_DotChaser

Type NeuralNetwork_Chaser Extends NeuralNetworkBackProp
	
	Field XPos:Neuron, YPos:Neuron, TXPos:Neuron, TYPos:Neuron
	Field XDir:Neuron, YDir:Neuron
	
	Method New()
		
		InitBackProp(8)
		XPos = Layer[0].Add("X")
		YPos = Layer[0].Add("Y")
		TXPos = Layer[0].Add("DX")
		TYPos = Layer[0].Add("DY")
		XDir = Layer[2].Add("MX")
		YDir = Layer[2].Add("MY")
		InitNetwork()
		
									
	End Method

	
	
	Method TrainNetwork:Double()
		
		For i = 0 Until 300
			
			sx:Double = Rnd(0, 1)
			sy:Double = Rnd(0, 1)
			dx:Double = Rnd(0, 1)
			dy:Double = Rnd(0, 1)
			
			mx:Double = 0.5 + (dx - sx) * 0.5
			my:Double = 0.5 + (dy - sy) * 0.5
			
			XPos.Output = sx
			YPos.Output = sy
			TXPos.Output = dx
			TYPos.Output = dy
			
			XDir.Target = 1.0 - mx
			YDir.Target = 1.0 - my
			
			For j = 0 Until 250
				
				Run()
				LearnBP()
			
			Next
		
			
		Next
	
	End Method
	
	Method DebugLayer(i:Int, dx:Float, dy:Float)
	
		DrawText("Layer:" + i, dx, dy)
		
		dy:+20
	
	 	For n = 0 Until Layer[i].NC
	 		
			DrawText "N.Output=" + Layer[i].N[n].Output + " Err:" + Layer[i].N[n].Err,dx,dy
			dy:+20
			If Layer[i + 1] <> Null
			For n2 = 0 Until Layer[i + 1].NC
				
				DrawText "W" + n2 + ":" + Layer[i].N[n].Weight[n2], dx, dy
			
				dy:+20
			Next
			EndIf
		
			DrawText ("N.F=" + F(Layer[i].N[n].Output), dx, dy)
			
			dy:+20
						
		Next
	 
	End Method
	
End Type


SeedRnd MilliSecs()
Local dots:Int = 1000

Global mDot:Dot[dots]

Local ldot:Dot = Null

For i = 0 Until dots

	mDot[i] = New Dot
	If ldot <> Null
	
		mDot[i].Target = ldot
	
	End If
	ldot = mDot[i]

Next

Local mouseDot:Dot = New Dot

Local pd:Dot = mDot[dots - 1]
pd.Target = mouseDot


Type Dot
	
	Field ChaserBrain:NeuralNetwork_Chaser = New NeuralNetwork_Chaser
	Field X:Double, Y:Double
	
	Field Target:Dot
		
	Method New()
		
		x = Rnd(0, GraphicsWidth())
		y = Rnd(0, GraphicsHeight())
	
	End Method
	
	Method Update()
		
		If Target = Null Return
	
		ChaserBrain.XPos.Output = X / Double(GraphicsWidth())
		ChaserBrain.YPos.Output = Y / Double(GraphicsHeight())
		
		ChaserBrain.TXPos.Output = Target.X / Double(GraphicsWidth())
		ChaserBrain.TYPos.Output = Target.Y / Double(GraphicsHeight())
		
		ChaserBrain.Run()
		
		Print "Err:" + ChaserBrain.Error(2)
		
		X:+(-1.0 + ChaserBrain.XDir.Output * 2.0) * 4
		Y:+(-1.0 + ChaserBrain.YDir.Output * 2.0) * 4
		
	End Method

End Type



While Not KeyDown(KEY_ESCAPE)

	Cls
	
	mouseDot.x = MouseX()
	mouseDot.y = MouseY()

	SeedRnd 234
		
	For i = 0 Until dots
	
	
		If MouseDown(1)
			
			mDot[i].Update()
		
		End If
	
		SetColor Rnd(0, 255), Rnd(0, 255), Rnd(0, 255)
	
		DrawRect mDot[i].X - 8, mDot[i].Y - 8, 16, 16
	
	Next
	
	SetColor 255, 255, 255
	
	DrawRect mouseDot.x - 8, mouseDot.y - 8, 16, 16
	
	Flip

Wend
