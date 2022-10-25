; ID: 1158
; Author: AntMan - Banned in the line of duty.
; Date: 2004-09-05 13:18:45
; Title: Neural Net Engine
; Description: Feed fordward neural net engine, b+/3d.

Global debug ; Blitz will not compile any constant expressions if they're false. Or so I'v heard...


;---Constants/Globals

Const C_maxInputs = 5000,C_maxNPL = 500,C_maxLayers = 5
Const C_maxNet = 50000 ;Max number of neurons any single neruon can be linked to(On a per neuron basis, so 2 neurons is limit*2 and so on, up to n(Inifinity)
Const nBias# = - 1 ;Do not change!(cue everyone changing it, crashing their machines..;->
Const C_sypAct# = 1 ;Activation value. Change to suit your needs.
Const V_maxVec = 500 
Global vTmp.vector = New vector;IO vector used by neural nets
Const c_maxHis = 500
Dim nFire.neuron(c_maxHis)
Dim nNull.neuron(c_maxHis) ;For the learning, history of a cycles, null and fired neurons.


Type vector
    Field size
    Field v#[V_maxVec]  
End Type

Type neuron
    Field numNet
    Field weight#[C_maxNet+1]
    Field net.neuron[C_maxNet]
End Type

Type nLayer
    Field neuron.neuron[C_maxNPL]
    Field numNeurons,numIn
End Type

Type neuralNet 
	Field numInputs,numOutputs
	Field numLayers,numNPL
	Field nLayer.nLayer[C_maxLayers]
End Type

;----Funcs

Function initFFnet() 
    vTmp = New vector
End Function

Function nLayer.nLayer(numNeurons,numIn)
	nLayer.nLayer = New nLayer
	nLayer\numNeurons = numNeurons
	nLayer\numIn = numIn
	For n=1 To numNeurons
		nLayer\neuron[n] = neuron(numIn)
	Next
	Return nLayer
End Function

Function neuron.neuron(numNet)
	neuron.neuron = New neuron 
	For i = 1 To numNet + 1
		neuron\weight[i] = Rnd(0.4)
	Next
	neuron\weight[C_maxNet+1] = Rnd(0.6)
	Return neuron
End Function



Function neuralNet.neuralNet(numInputs,numHidden,numOutputs,populate = True,initVecTmp = True)
	out.neuralNet = New neuralNet
	out\numInputs = numInputs
	out\numOutputs = numOutputs
	out\numLayers = numHidden
	If populate 
		out\nLayer[1] = nLayer(numInputs,numInputs)
		out\nLayer[2] = nLayer(numHidden,numInputs)
		out\nLayer[3] = nLayer(numOutputs,numHidden)
		linkLayers(out\nLayer[1],out\nLayer[2],True)
		linkLayers(out\nLayer[2],out\nLayer[3],True,True)
	End If
	Return out
End Function

Function linkLayers(l1.nLayer,l2.nLayer,chain = False,preserve = True) ;1> 2<> 3<
	For n = 1 To l1\numNeurons
		If Not preserve
			l1\neuron[n]\numNet=0
		EndIf
	For t = 1 To l2\numNeurons
	l1\neuron[n]\net[t+l1\neuron[n]\numNet] = l2\neuron[t]
	Next
	l1\neuron[n]\numNet = l1\neuron[n]\numNet+l2\numNeurons
	Next
	If chain linkLayers(l2,l1,False,preserve)
End Function ;To double chain two layers, set chain to true
	


;--Force feedforward net cycle. If you add differant cycles please share them!


;Learning modules (Use history look ups, don't fuck with them))

Function punishNet()
	If Not c_maxHis End
	For j = 1 To c_maxHis
		If Not nFire(j) = Null
			For n = 1 To c_maxNet
				nFire(j)\weight[n] = nFire(j)\weight[n] - 0.05
			Next
		EndIf
	Next
End Function

Function rewardNet()
	If Not c_maxHis End
	For j = 1 To c_maxHis
		If Not nFire(j) = Null
			;nFire(j)\weight[c_maxNet+1] = nFire(j)\weight[c_maxNet+1] - 0.2
			For n = 1 To c_maxNet
				nFire(j)\weight[n] = nFire(j)\weight[n] + 0.05
			Next
		EndIf
	Next
End Function

;fin learn


Function FFnetCycle(in.neuralNet) ; input->[?> hidden ?> output >]->user/GA
Local tWeight#
	clearHistory()
	For layer = 1 To 3
		For i = 1 To in\nLayer[layer]\numNeurons
		tWeight = 0
		For n = 1 To in\nLayer[layer]\numIn
			;tWeight = tWeight + (in\nLayer[layer]\neuron[i]\weight[n] * vTmp\v[n])
			tWeight = tWeight + (vTmp\v[n] * in\nLayer[layer]\neuron[i]\weight[n])
			If debug
				If n = 1
					DebugLog "-------------------"
					DebugLog "Layer >"+layer 
					DebugLog "Input Neuron>"+i
					DebugLog "Threashold>"+in\nLayer[layer]\neuron[i]\weight[C_maxNet+1]
				EndIf
				DebugLog "Weight "+n+">"+in\nLayer[layer]\neuron[i]\weight[n]
			EndIf

		Next
		tWeight = tWeight + (in\nLayer[layer]\neuron[i]\weight[c_maxNet+1]) * nBias
		pushVector(vTmp,sigmoid(tWeight,C_sypAct))
		;-
		If vTmp\v[1] > in\nLayer[layer]\neuron[i]\weight[C_maxNet+1]
			For j = 1 To c_maxHis
				If nFire(j) = Null
					nFire(j) = in\nLayer[layer]\neuron[i]
					;in\nLayer[layer]\neuron[i]\weight[c_maxNet+1] = 0
					Exit
				EndIf
			Next
			If debug
				DebugLog "Neuron "+i+" on layer "+layer+" fired"
			EndIf
		Else
			For j = 1 To c_maxHis
				If nNull(j) = Null
					nNull(j) = in\nLayer[layer]\neuron[i]
					Exit
				EndIf
			Next
		EndIf
		If debug
			DebugLog "Activation > " + vTmp\v[1]
		EndIf
		Next
	Next
End Function

Function clearHistory()
	For j = 1 To c_maxHis
		nFire(j) = Null
		nNull(j) = Null
	Next
End Function

Function debugHistory()
	If debug
		For j = 1 To c_maxHis
			If Not nFire(j) = Null fCount = fCount + 1
			If Not nNull(j) = Null nCount = nCount + 1 
		Next
		DebugLog "History Debug_____"
		DebugLog "1 Cycle"
		DebugLog fCount+" Neurons fired"
		DebugLog nCount+" Inhibited neurons"
	EndIf
End Function


Function sigmoid#(in#,round#)
	Return ( 1. / (1. + Exp(-in / round)))
End Function

Function setInput(i,v#) 
	If i>vTmp\size vTmp\size = i
	vTmp\v[i] = v
End Function


Function getInput#(i)
	Return vTmp\v[i] 
End Function

Function clearNetIO() ;needed after every cycle's results/inputs are not needed.
	vTmp\size = 0
	vTmp\v[1] = 0
End Function

;Input related (to do with the input layer. (In plain english, manually set the input layers neuron's)

;Note, this isn't used any longer. But would be useful for things like IR

Function injectI(net.neuralNet,l,o1,val#=0,o2 = 1,dW=1)
	aN = ((dW * o2) - dW) + o1
	net\nLayer[l]\neuron[aN]\weight[C_maxNet+1] = val
End Function 


;--

Function getOutput(net.neuralNet,n,sum = False)
	If Not sum Return net\nLayer[3]\neuron[n]\weight[C_maxNet+1]
End Function


;--vector lib(if you can call a couple of functions a lib...)

Function vector.vector()
	out.vector = New vector
	out\size = 1
    Return out
End Function 

Function setVector(in.vector,i,v#)
	in\v[i]=v
End Function

Function getVector#(in.vector,i)
	Return in\v[i]
End Function

Function scaleVector(in.vector,sf#)
	For v = 1 To in\size
		in\v[v]=in\v[v] * sf#
	Next
End Function

Function projectVector(in.vector,offSet = 1)
	If offset+2 > in\size Return 
	For v = offSet To offSet + 1
		in\v[v]=in\v[v] / in\v[offSet+2]
	Next
End Function

Function pushVector(in.vector,v#) 
	If in\size < V_maxVec in\size=in\size+1 Else Return 
	If in\Size > 1
		For i=in\size-1 To 1 Step - 1
			in\v[i+1]=in\v[i]
		Next
	EndIf
	in\v[1] = v
End Function


;Havn't tried this function yet, should work but isn't to do with anything above. yet.

Function vectorDistance#(v1.vector,v2.vector,dimensions = -1)
Local sV#,sT# 
	If dimensions = -1 dimensions = v1\size 
	For dimensions = dimensions To 1 Step -1
		sT = (v2\v[d] - v1\v[d]) 
		sV=Sv + (sT * sT)
	Next
Return Sqr(sv)
End Function
