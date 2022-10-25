; ID: 2100
; Author: Techlord
; Date: 2007-09-05 19:03:20
; Title: Big Bad Dragon
; Description: A simple text-based RPG

Graphics 800,200,32,2
Print "*** Big Bad Dragon ***"
Print
Print "The Big Bad Dragon has kidnapped The Little Princess."
Print "Being the Brave Knight you are, you have tracked" 
Print "down the beast to its Lair. Here the beast snores "
Print "loudly, flames jet out from its nostrils. You use the "
Print "element of suprize to plan your attack... CHARGE!" 
Print

;The Dragon
Type character
	Field name$
	Field health%
	Field weapon%
	Field special%
End Type

player.character = New character
player\name$= Input("Name your Warrior:") 
player\health = 100
player\weapon = 10
player\special = 3

monster.character = New character
monster\name$ = "Dragon"
monster\health="200"
monster\weapon = 5
monster\special = 10

SeedRnd MilliSecs()
;game loop
Repeat

attack=Input("You attack with [1]Sword [2]Magic("+player\special+"): ")

If attack=1 
	playerpower = Rnd(player\weapon)
	success = Rnd(6)
	failed = Rnd(6)
	If success > failed Then 
		Print "Sword does "+playerpower+" points of damage."
		monster\health=monster\health-playerpower
		
		success = Rnd(10)
		failed = Rnd(10) 
		If success>failed Then 
			playerpower = Rnd(25)*2
			Print "You inflicted a Critical Wound for "+playerpower+" points of damage."
			monster\health=monster\health-playerpower
		EndIf
		
	Else
		Print "Your attack failed:("	
	EndIf
	
	If Rnd(12)>Rnd(15)
		Print 
		Print "The Tooth Fairy appears before you."
		Print "She holds a of small pouch of Jellybeans."
		Print "Guess the number of Jellybeans in the bag"
		magicnumber = Input("to be granted a wish: ") 
		If magicnumber>Rnd(38)
			player\special=player\special+1
			Print
			Print "POW!!!! You recieve a Magic Snowball."
		Else 
			Print "You received Didly Squat!"
		EndIf	
	EndIf
	
ElseIf attack=2

	If player\special=0 
		Print "Sorry no Magic"
	Else
		player\special = player\special -1
		playerpower = Rnd(50)
		success = Rnd(6)
		failed = Rnd(6)
		If success > failed Then 
			Print "Snowball does "+playerpower+" points of damage."
			monster\health=monster\health-playerpower
		Else
			Print "Your attack failed:("	
		EndIf 
	EndIf
End If

;Dragon attack
Dragonattack = Rnd(30) 
If Dragonattack > 15

	Dragonweapon = Rnd(1)
	
	If Dragonweapon = 1 
		If monster\special > 0
		
			monsterpower = Rnd(100)
			success = Rnd(6)
			failed = Rnd(6)
			If success > failed Then 
				Print "The Dragon's Bad Breath does "+monsterpower +" points of damage"
				player\health=player\health-monsterpower
			Else
				Print "The Dragon blew steam."	
			EndIf
		
		Else
			Print "The Dragon is out of Breath."
		EndIf
		
	Else 
		monsterpower = Rnd(monster\weapon)
		success = Rnd(6)
		failed = Rnd(6)
		If success > failed Then 
			Print "Dragon Claws does "+monsterpower +" points of damage."
			player\health=player\health-monsterpower
		Else
			Print "The Dragon tried to bite, but, has no teeth."	
		EndIf
	EndIf 
Else
	Print "The Dragon is too tired to attack."	
EndIf

FlushKeys()
Print
Print player\name+":"+player\health+" vs Dragon:"+monster\health
Until monster\health<0 Or player\health<0 

If monster\health < 0 
	Print "You slayed the Bad Breath Dragon."
	Else
	Print "The Dragon is having smoked Hero for dinner."		
End If 

If player\health < 0
	Print "You are Toast."
Else
	Print "You live up to your name Great Knight."	
End If
WaitKey()
