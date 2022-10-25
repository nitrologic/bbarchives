; ID: 2919
; Author: AntonyWells
; Date: 2012-02-13 09:52:40
; Title: Artificial Neural Network Library
; Description: Artificial Neural Network Library

Framework brl.glmax2d
Import brl.jpgloader
Import brl.pngloader
Import brl.retro
Import brl.standardio
Import brl.map
Import brl.linkedlist
Import brl.Random

Const MAX_WEIGHTS:Int = 1024 * 100
Const MAX_NEURONS:Int = 1024*100
Const MAX_LAYERS:Int = 32
Const LEARN_RATE:Double = 30

Type Neuron

	Field Value:Double 
	Field Weight:Double[]
	Field Output:Double
	Field Err:Double, Target:Double

End Type 

Type NeuronLayer

	Field N:Neuron[]
	Field NC:Int = 0 
	Field NMap:TMap = New TMap
	
	Method Add:Neuron(id:String)
	
		Local nn:Neuron = New Neuron
		
		N[NC] = nn
		
		NC:+1
		
		MapInsert(NMap,id,nn)
		
		Return nn 
	
	End Method 
	
	Function Cr:NeuronLayer(count:Int)
	
		Local r:NeuronLayer = New NeuronLayer
		
		r.N = New Neuron[count]
				
		For i = 0 Until count 
			
			r.N[i] = New Neuron
		
		Next 
		
		r.NC = count
		
		Return r 
	
	End Function 

	Method Get:Neuron(id:String)
	
		Return Neuron(MapValueForKey(nmap,id))
	
	End Method 
	
End Type

Type NeuralNetwork
	
	Field Layer:NeuronLayer[MAX_LAYERS]
	Field LC:Int

	Function CreateBackProp:NeuralNetwork(hidden, output)
		
		Local n:NeuralNetwork = New NeuralNetwork
		
		n.InitNetwork()
					
		Return n
	
	End Function

	Method InitNetwork()
	
		For i = 0 Until MAX_LAYERS
			
			If Layer[i] = Null Exit
			
			For n = 0 Until Layer[i].NC
				
				If Layer[i + 1] <> Null
			
					Layer[i].N[n].Weight = New Double[Layer[i + 1].NC]
				
					For w = 0 Until Layer[i + 1].NC
						
						Layer[i].n[n].Weight[w] = Rnd(0.0, 100.0) / 100.0
				
					Next
				
				End If
				
				Layer[i].N[n].Output = Rnd(0.0, 100.0) / 100.0
				
			Next
		
		Next
		
		TrainNetwork()
	
	End Method
	
	Method TrainNetwork:Double()
		
	
	End Method
	
	Method Run:Double()
		
		RunLayer(1)
		RunLayer(2)
	
	        Return Error(2)
		
	End Method
	
	Method RunLayer(i:Int)
		
		Local l1:NeuronLayer = Layer[i - 1]
		Local l2:NeuronLayer = Layer[i]
		
	
		If i = 1
		
		For n = 0 Until l2.nc
			
			v:Double = 0.0
			For in = 0 Until l1.nc
				
				v:+l1.N[in].Output * l1.N[in].Weight[n]
					
			Next
			
			l2.N[n].Value = v
			l2.N[n].Output = F(v)
				
		Next
		
		Else If i = 2
		
			For n = 0 Until l2.nc
				
				Local an:Neuron = l2.N[n]
			
				v:Double = 0.0
			
				For in = 0 Until l1.nc
				
					v:+l1.N[in].Output * l1.N[in].Weight[n]
						
				Next
			
			an.Value = v
			an.Output = F(v)
			an.Err = (an.Target - an.Output) * (an.Output) * (1.0 - an.Output)
						
		Next
		
		End If
	
	End Method
	
	Method Error:Double(l:Int)
		
		err:Double = 0.0
	
		For i = 0 Until Layer[l].NC
		
			err:+((Layer[l].N[i].Target - Layer[l].N[i].Output) ^ 2.0) / 2.0
			
		Next
		
		Return err
	
	End Method
	
	Method Train:Double()

	End Method
	
End Type

Function F:Double(x:Double)
	
	Return (1 / (1 + Exp(-x)))

End Function


Type NeuralNetworkBackProp Extends NeuralNetwork
	

	Method InitBackProp(hc:Int)
		
		LC = 3
		Layer[0] = NeuronLayer.Cr(0)
		Layer[1] = NeuronLayer.Cr(hc)
		Layer[2] = NeuronLayer.Cr(0)
		
	
		
	End Method
	
	Method LearnBP()
		
		Local il:NeuronLayer = layer[0]
		Local hl:NeuronLayer = layer[1]
		Local ol:NeuronLayer = layer[2]
	
		For i = 0 Until hl.NC
		
			v:Double = 0.0
			
			For j = 0 Until ol.NC
				
				v:+ hl.N[i].Weight[j] * ol.N[j].Err			
			
			Next
			
			
				
			hl.N[i].Err = v
		
		Next
		
		For i = 0 Until hl.NC
			
			For j = 0 Until il.NC
			
				il.N[j].Weight[i]:+LEARN_RATE * hl.n[i].Err * il.N[j].Output
			
			Next
		
		Next
		
		For i = 0 Until ol.NC
			
			For j = 0 Until hl.NC
			
				hl.N[j].Weight[i]:+LEARN_RATE * ol.n[i].Err * hl.N[j].Output
			
			Next
		
		Next
		
	
	End Method
		
End Type
