; ID: 1801
; Author: Ryudin
; Date: 2006-08-31 19:59:42
; Title: War
; Description: a text-based war game.

Graphics 1024,768
SeedRnd MilliSecs()
Print CommandLine$()
AppTitle CommandLine$(),"DOYOYOYOYOYOYOYOY!"
Delay 75
AppTitle "War","Coward! A brave man would not retreat!"
Cls
Global defend
Global attack
Global guerrilla
Global mine
Global rocket
Global airbomb
Global endefend
Global enattack
Global enguerrilla
Global enmine
Global enrocket
Global enairbomb
Global minesdown = 0
Global enminesdown = 0
Global energy = 100
Global troops = 150
Global enenergy = 100
Global entroops = 150
Global vics
Global loss
Locate 0,0
Name$ = Input$("You have been promoted to commander, soldier. What name should I put on your badge? ")
If FileType("Users\" + Name$ + ".war") = 1 Then
	r = ReadFile("Users\" + Name$ + ".war")
		vics = ReadFloat(r)
		loss = ReadFloat(r)
	CloseFile(r)
	Print "Oh! My mistake, Commander " + Name$ + " ! Welcome back!"
	Goto start
EndIf
Print "Welcome to the position, Commander " + Name$ + "!" 
WaitKey
FlushKeys()
.start
Cls
Locate 0,0
Print "This game is simple. Just use strategy to choose what to do with your soldiers' energy. You"
Print "can use guerrilla warfare (-25 energy), air strike (-50 energy), head on attack (-10 energy),"
Print "rocket attack (-35 energy), mine traps (-25 energy), or defend (-0 energy). Your energy"
Print "regenerates at a rate of 10 per turn. Your enemy chooses out of these too. It is a turn"
Print "based system. You play defense, guerrilla, bombing (rock, paper, scissors) to see who goes"
Print "first. Your troop number is displayed with the enemy's up in the upper-left corner.Now get"
Print "out ther and do your duty, " + Name$ + "! Press any key to go on."
WaitKey
If KeyHit(2) Then
	Command$ = Input$("? ")
	r = ReadFile("Settings\" + Command$ + ".inf")
		troops = ReadFloat(r)
		energy = ReadFloat(r)
		enenergy = ReadFloat(r)
		entroops = ReadFloat(r)
	CloseFile(r)
EndIf
rps = Rand(0,1)
go = rps
If go = 1 Print "You go first!"
If go <> 1 Print "You go second."
Print "Press 'Space' to go on."
Repeat
Until KeyHit(57)
FlushKeys()
While Not KeyHit(1)
	If entroops =< 0 Then
		Cls
		Locate 0,0
		Print "You win, Commander " + Name$ + "! That's a real feather in your cap. Want to try some more?"
		vics = vics + 1
		WaitKey
		Exit
	EndIf
	If troops =< 0 Then
		Cls
		Locate 0,0
		Print "You lose, Commander " + Name$ + ". That is bad for your standing! You'd better try again, huh?"
		loss = loss + 1
		WaitKey
		Exit
	EndIf
	Cls
	Locate 0,0
	Print "Your troops: " + troops
	Print "Your troops' energy: " + energy
	Print ""
	Print "Enemy troops: " + entroops
	Print "Enemy troops' energy: " + enenergy
	If endofturn = 1 And endofturn2 = 1 Then	
		Cls
		b4btltroops = troops
		b4btlentroops = entroops
		Battle()
		If defend = True Then
			troops = troops + (troops - b4btltroops) / 2
		EndIf
		If endefend = True Then
			entroops = entroops + (entroops - b4btlentroops) / 2
		EndIf
		endofturn = False
		endofturn2 = False
		defend = 0
		mine = 0
		attack = 0
		guerrilla = 0
		rocket = 0
		airbomb = 0
		endefend = 0
		enmine = 0
		enattack = 0
		enguerrilla = 0
		enrocket = 0
		enairbomb = 0
	EndIf
	If go = 1 Then
		FlushKeys()
		Repeat
			Cls
			Locate 0,0
			Print "Your troops: " + troops
			Print "Your troops' energy: " + energy
			Print ""
			Print "Enemy troops: " + entroops
			Print "Enemy troops' energy: " + enenergy
			Text 512,0,"Press '1' to defend your troops. <COST> 0 Energy",True
			Text 512,FontHeight(),"Press '2' to attack the enemy head on. <COST> 10 Energy",True
			Text 512,FontHeight() * 2,"Press '3' to plant mines for future need. <COST> 20 Energy",True
			Text 512,FontHeight() * 3,"Press '4' to attack enemies using guerrilla troops. <COST> 25 Energy",True
			Text 512,FontHeight() * 4,"Press '5' to barrage the enmy with rockets. <COST> 35 Energy",True
			Text 512,FontHeight() * 5,"Press '6' to form an air strike force. <COST> 50 Energy",True
			If KeyHit(2) defend = True endofturn = True
			If KeyHit(3) And energy => 10 energy = energy - 10 attack = True endofturn = True
			If KeyHit(4) And energy => 20 energy = energy - 20 mine = True endofturn = True			
			If KeyHit(5) And energy => 25 energy = energy - 25 guerrilla = True endofturn = True			
			If KeyHit(6) And energy => 35 energy = energy - 35 rocket = True endofturn = True				
			If KeyHit(7) And energy => 50 energy = energy - 50 airbomb = True	endofturn = True
			Delay 200
			Flip		
		Until endofturn = True
		Cls
		Locate 0,0
		Print "Your troops: " + troops
		Print "Your troops' energy: " + energy
		Print ""
		Print "Enemy troops: " + entroops
		Print "Enemy troops' energy: " + enenergy
		go = 0
	EndIf
	If go = 0 Then
		endofturn = True
		Repeat
			enchoose = Rand(1,6)
			Select enchoose
				Case 1 
					endefend = True endofturn2 = True
					Text 512,0,"Enemy is defending.",True
				Case 2
					If enenergy => 10 enattack = True enenergy = enenergy - 10 endofturn2 = True
					Text 512,0,"Enemy is attacking our troops head on!",True
				Case 3
					If enenergy => 25 enattack = True enenergy = enenergy - 25 endofturn2 = True
					Text 512,0,"Enemy is planting mines!",True
				Case 4
					If enenergy => 25 enattack = True enenergy = enenergy - 25 endofturn2 = True
					Text 512,0,"Enemy is sending in guerrillas!",True		
				Case 5
					If enenergy => 35 enattack = True enenergy = enenergy - 35 endofturn2 = True
					Text 512,0,"Enemy is blowing up our troops with rockets!",True								
				Case 6
					If enenergy => 50 enattack = True enenergy = enenergy - 50 endofturn2 = True
					Text 512,0,"Enemy is bombing us from the air!",True
			End Select
		Until endofturn2 = True	
		FlushKeys()
		WaitKey
		go = 1
	EndIf
	If entroops =< 0 Then
		Cls
		Locate 0,0
		Print "You win, Commander " + Name$ + "! That's a real feather in your cap. Want to try some more?"
		vics = vics + 1
		win = True		
		WaitKey
		Exit
	EndIf
	If troops =< 0 Then
		Cls
		Locate 0,0
		Print "You lose, Commander " + Name$ + ". That is bad for your standing! You'd better try again, huh?"
		loss = loss + 1
		WaitKey
		Exit
	EndIf
	

	Flip
Wend

Function Battle()
	If minesdown > 0 minesdown = minesdown - 1 entroops = entroops - 7
	If mine = True minesdown = minesdown + 1 entroops = entroops - 5
	If attack = True entroops = entroops - 15 
	If guerrilla = True minesdown = minesdown + 2 entroops = entroops - 10
	If rocket = True entroops = entroops - 18
	If airbomb = True entroops = entroops - 25
	
	If enminesdown > 0 enminesdown = enminesdown - 1 troops = troops - 7
	If enmine = True enminesdown = enminesdown + 1 troops = troops - 5
	If enattack = True troops = troops - 15 
	If enguerrilla = True enminesdown = enminesdown + 2 troops = troops - 10
	If enrocket = True troops = troops - 18
	If enairbomb = True troops = troops - 25	
	energy = energy + 10
	enenergy = enenergy + 10
	If entroops =< 0 Then
		Cls
		Locate 0,0
		Print "You win, Commander " + Name$ + "! That's a real feather in your cap. Want to try some more?"
		vics = vics + 1
		win = True
	EndIf
	If troops =< 0 Then
		Cls
		Locate 0,0
		Print "You lose, Commander " + Name$ + ". That is bad for your standing! You'd better try again, huh?"
		loss = loss + 1
	EndIf
End Function
Locate 0,0
Cls
If win = True vics = vics - 1
Print "Your victories: " + vics
Print "Your losses: " + loss
ovr = vics - loss * 75.555555
Print "Your army's rating for you: " + ovr

If FileType("Users\" + Name$ + ".war") <> 0 DeleteFile("Users\" + Name$ + ".war")
w = WriteFile("Users\" + Name$ + ".war")
	WriteFloat(w,vics)
	WriteFloat(w,loss)
CloseFile(w)
WaitKey
