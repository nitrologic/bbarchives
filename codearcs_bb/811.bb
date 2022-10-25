; ID: 811
; Author: Uhfgood
; Date: 2003-10-17 04:33:44
; Title: Neptune's Caverns, Updated and modernized.
; Description: Took the original port I had made, and then removed goto's and gosubs.

; Neptune's Cavern's -- A text adventure listed in
; the book "Write your own program : A database adventure game"
; by Steve Rodgers and Marcus Milton orginally published
; in 1985.  Ported to BlitzBasic in 2003 by Keith Weatherby II
; Version 02 - Time to update it, with functions, removal of
; line numbers and eventually gosubs and goto's

Const ScreenWidth  = 640 ; x res
Const ScreenHeight = 480 ; y res
Const ScreenBpp    = 32  ; bits per pixel
Const ScreenMode   = 2   ; mode 2 is windowed

; room constants
Const WALL_ROOM_01    = 1
Const KNIFE_ROOM      = 2
Const DOOR_ROOM       = 3
Const WALL_ROOM_02    = 4
Const SEAWEED_ROOM    = 5
Const FLIPPER_ROOM    = 6
Const BONEFISH_ROOM   = 7
Const BRIGHT_ROOM     = 8
Const GREEN_CUFF_ROOM = 9
Const SEAHORSE_ROOM   = 10
Const BLUE_CUFF_ROOM  = 11
Const SQUARE_ROOM_01  = 12
Const DJ_CHEST_ROOM   = 13
Const MURKY_ROOM      = 14
Const BONE_ROOM       = 15
Const URCHIN_ROOM     = 16
Const RED_CUFF_ROOM   = 17
Const SQUARE_ROOM_02  = 18
Const HOLE_ROOM       = 19
Const OCTOPUS_ROOM    = 20
Const CABIN_ROOM      = 21
Const SHARK_ROOM      = 22
Const SQUARE_ROOM_03  = 23
Const SQUARE_ROOM_04  = 24

; object constants
Const KNIFE        = 1
Const FLIPPERS     = 2
Const KEY          = 3
Const SEAWEED      = 4
Const BONE         = 5
Const PLUG         = 6
Const YELLOW_CUFFS = 7 
Const GREEN_CUFFS  = 8
Const RED_CUFFS    = 9
Const BLUE_CUFFS   = 10

; misc object constants 
Const OBJECT_GONE = 0
Const CARRYING_OBJECT = 99

; noun index constants
Const N_NONE = 0
Const N_NORTH = 1
Const N_SOUTH = 2
Const N_EAST = 3
Const N_WEST = 4
Const N_DUMMY = 5
Const N_CHEST = 6
Const N_WINDOW = 7
Const N_KNIFE = 8
Const N_FLIPPERS = 9
Const N_KEY = 10
Const N_SEAWEED = 11
Const N_BONE = 12
Const N_PLUG = 13
Const N_HANDCUFFS = 14

; Verb Index constants
Const V_NONE      = 0
Const V_GO        = 1
Const V_GET       = 2
Const V_DROP      = 3
Const V_CUT       = 4
Const V_WEAR      = 5
Const V_GIVE      = 6
Const V_UNLOCK    = 7
Const V_USE       = 8 
Const V_INVENTORY = 9
Const V_HELP      = 10
Const V_LOOK      = 11
Const V_QUIT      = 12

Global NumNouns     = 14        ; number of nouns
Global NumVerbs     = 12        ; number of verbs
Global CharPosition = DOOR_ROOM ; character position (room)
Global NumInvItems  = 1         ; number of inventory items
Global NumCuffs     = 0         ; number of handcuffs
Global WinFlag      = False     ; Win flag
Global LostFlag     = False     ; Lost Flag
Global UnlockFlag   = False     ; Unlock Flag
Global FlippersFlag = False     ; Flipper Flag
Global CutFlag      = False     ; Cut Flag
Global VerbIndex    = V_NONE     ; Verb index
Global NounIndex    = N_NONE     ; Noun Index
Global NewGameFlag  = False     ; start a new game?
Global ExitFlag     = False     ; end current game?

Dim DataOne$( 24 )          ; Room description part 1
Dim DataTwo$( 24 )          ; room description part 2
Dim Exits( 24, 4 )          ; Exits
Dim NounName$( NumNouns )   ; Noun descriptions
Dim VerbName$( NumVerbs )   ; Verb Descriptions
Dim ObjectName$( 10 )       ; Object Descriptions
Dim ObjectList( 10 )        ; Object index

; Init graphics mode
Graphics ScreenWidth, ScreenHeight, ScreenBpp, ScreenMode

	; main loop, keep executing, until the
	; exit flag is encountered.
	While( ExitFlag = False )

		; If we're not in a new game
		; Then initialize it, and execute
		If NewGameFlag = False
			NewGameFlag = True
			Initialize()
			Look()
		End If

		; accept text
		GameInput()
		
		; sort responses into verb/noun and execute
		Sort()
		
		; Win or lose the game will end
		If WinFlag  = True Then EndGame() 
		If LostFlag = True Then EndGame()
		
	Wend ; end while exit flag is false

End ; end main program

; ************* INPUT      *************
Function GameInput()

	; we use element 0 as a temp
	; make sure to clear everything
	; out for this particular time.
	VerbName$( V_NONE ) = "" 
	NounName$( N_NONE ) = ""
	Response$ = ""
	
	; prompt user, and make sure the response is
	; in all capital letters, as I check against
	; caps
	Response$ = Input( "What do you do next? " ) 
	Response$ = Upper$( Response$ )
	
	; clear the screen to 
	; make things a bit more tidy
	; then reset cursor to the first
	; position
	Cls 
	Locate( 0, 0 )
	
	; run through each character of response
	For StringPos = 1 To Len( Response$ )
		
		; Check for space, then extract.
		If Mid$( Response$, StringPos, 1 ) = " " Then 
		
			; extract the verb from first 3 characters
			; and then put it into temporary string
			VerbName$( V_NONE ) = Left$( Response$, 3 )
			
			; Start from after the space and grab the next
			; 3 characters and put it into temp noun string 
			NounName$( N_NONE ) = Mid$( Response$, StringPos + 1, 3 ) 
			
			; jump to the end of the loop
			; to disreguard any other characters
			StringPos = Len( Response$ )
		
		End If ; space
		
	Next ; for string pos
	
	; jump ship if there is a noun
	If NounName$( N_NONE ) <> "" Then Return
	
	; get the first 3 letters
	Response$ = Left$( Response$, 3 ) 
	
	; check for exit
	If Response$ = "QUI" Then 
	
		ExitFlag = True
		Return
	
	End If
	
	; If any of these are directions, then
	; automatically make the verb "go" and
	; make the noun the direction 
	;
	;;;;;;;;;;;;;;;
	;
	; Since the "If Response" line was too
	; long I decided to split it up for each
	; part, that's why each piece has pretty
	; much the same code.	
	
	If Response$ = "NOR" Then 
	
		VerbName$( V_NONE ) = "GO " 
		NounName$( N_NONE ) = Response$ 
		Return
	
	End If

	If Response$ = "SOU" Then 
	
		VerbName$( V_NONE ) = "GO " 
		NounName$( N_NONE ) = Response$ 
		Return
	
	End If

	If Response$ = "EAS" Then 
	
		VerbName$( V_NONE ) = "GO " 
		NounName$( N_NONE ) = Response$ 
		Return
	
	End If

	If Response$ = "WES" Then 
	
		VerbName$( V_NONE ) = "GO " 
		NounName$( N_NONE ) = Response$ 
		Return
	
	End If
	
	; if any of these are general actions then
	; put a dummy noun in temp noun string and
	; put the action in the verb temp string
	;
	;;;;;;;
	;
	; Did the same here as with the direction verbs
	
	If Response$ = "HEL" Then 
	
		NounName$( N_NONE ) = "DOO" 
		VerbName$( V_NONE ) = Response$ 
		Return
	
	End If

	If Response$ = "INV" Then 
	
		NounName$( N_NONE ) = "DOO" 
		VerbName$( V_NONE ) = Response$ 
		Return
	
	End If

	If Response$ = "LOO" Then 
	
		NounName$( N_NONE ) = "DOO" 
		VerbName$( V_NONE ) = Response$ 
		Return
	
	End If

	If Response$ = "QUI" Then 
	
		NounName$( N_NONE ) = "DOO" 
		VerbName$( V_NONE ) = Response$ 
		Return
	
	End If
	
	; otherwise the parser can't understand it.
	Print "I don't understand your words. "

End Function ; input

; ************* SORT         *************
Function Sort()

	; make sure the indices are reset
	VerbIndex = V_NONE
	NounIndex = N_NONE


	; run through each of the verbs in the list
	For VerbCount = 1 To NumVerbs
	
		; if the temp verb string is equal to any of the
		; verbs in the list (via first 3 letters) then...
		VStr$ = Left$( VerbName$( VerbCount ), 3 )
		If VerbName$( V_NONE ) = VStr$ Then 
			
			; set the verb index to the current count
			VerbIndex = VerbCount
			
			; jump to end of loop by making
			; count the equal to the number of verbs 
			VerbCount = NumVerbs
		
		End If ; verb is in list
		
	Next ; verb count
	
	; run through each of the nouns in the list
	For NounCount = 1 To NumNouns

		; if the temp noun string is equal to any of the
		; nouns in the list then...
		If NounName$( N_NONE ) = NounName$( NounCount ) Then 
		
			; set the noun index to the current count
			NounIndex = NounCount 

			; jump to end of loop by making noun
			; count the equal to the number of nouns 
			NounCount = NumNouns
		
		End If ; noun in list
	
	Next ; noun count
	
	; neither noun or verb is in list
	If NounIndex = N_NONE Or VerbIndex = V_NONE Then 
		
		; state words are unknown
		Print "Say what?" 
		Return
		
	End If ; noun or verb isn't in list
	
	; So if everything is a go
	; then based on the current
	; verb, execute one of the
	; game functions.
	Select VerbIndex 
	
		Case 1  Go()         ; make a move to one of the exits         
		Case 2  GameGet()    ; get all items    
		Case 3  GameDrop()   ; drop the plug   
		Case 4  GameCut()    ; cut the seaweed   
		Case 5  GameWear()   ; wear the flippers   
		Case 6  GameGive()   ; allows you to give bone and seaweed   
		Case 7  GameUnlock() ; This unlocks the chest in the game
		Case 8  GameUse()    ; use an item in general   
		Case 9  Inventory()  ; display the items you have   
		Case 10 GameHelp()   ; display key words   
		Case 11 Look()       ; show room description
	
	End Select ; verb index

End Function ; sort

; ************* GO           *************
; move to different rooms
Function Go() 

	; If one of the nouns but
	; not one of the four directions
	If NounIndex > N_WEST Then 
	
		; print a message
		Print "Go where?" 
		Return
		
	End If ; noun index > 4
	
	; If there's not an exit in the room
	If Exits( CharPosition, NounIndex ) = 0 Then 
	
		; print as such
		Print "No Exit That Way" 
		Return
	
	End If ; no exit

	; if we're in the sea urchin room and you're not
	; wearing any flippers, and then you manage to move
	; in two of the directions, then you lose (ie die)	
	If CharPosition = URCHIN_ROOM 
	
		; trying to move in urchin room
		If( NounIndex = N_NORTH Or NounIndex = N_WEST ) 
		
			If FlippersFlag = False Then 

				; means we lost 
				LostFlag = True 
				Return

			End If ; flippers not on
			
		End If ; if we're going north or west
		
	End If ; in urchin room
	
	; update the new character position (room) 
	; based on what direction we just went to.
	CharPosition = Exits( CharPosition, NounIndex ) 
	
	; let's see the description now
	Look()
	
	Return

End Function ; Go

; ************* GET          *************
; grabs any of the 10 items you can get
Function GameGet()

	; If we're in the room with the chest
	; and someone tries to pick it up, then
	If NounIndex = N_CHEST 
	
		; Davy Jones' Locker
		If CharPosition = DJ_CHEST_ROOM Then 
	
			; tell them it's heavy, or print
			; something equally witty.
			Print "It's too heavy!" 
			Return
		
		End If ; Chest room
		
	End If ; D.J.'s Locker
	
	; If they try to pick up items that
	; can't be picked up...
	If NounIndex < N_KNIFE Then 
	
		; print a message about their
		; appearent stupidity.
		Print "Don't be Silly" 
		Return
	
	End If ; item isn't grabable
	
	; If you already have the item, and the item
	; isn't handcuffs, then
	If ObjectList( NounIndex - 7 ) = CARRYING_OBJECT 
	
		; hand cuffs
		If NounName$( N_NONE ) <> "HAN" Then 
	
			; state a message that you already have it
			Print "You've already got it!" 
			Return
		
		End If ; handcuffs
		
	End If ; if you already have the item
	
	; run through the cuffs	
	For ObjectIndex = YELLOW_CUFFS To BLUE_CUFFS
		
		; If player is trying to pick up handcuffs
		; while in the room with handcuffs then
		If ObjectList( ObjectIndex ) = CharPosition 
		
			; if the word is handcuffs
			If NounIndex = N_HANDCUFFS Then 
			
				; bump up the noun index to the next
				; position (next pair of handcuffs)
				NounIndex = ObjectIndex + 7 
				
				; increase amount of cuffs
				NumCuffs = NumCuffs + 1  
				
				; jump to the end of the loop
				ObjectIndex = 10
			
			End If ; cuffs
			
		End If ; trying to pick up hand cuffs
		
	Next ; run through cuffs
	
	; If any objects other than cuffs are being picked up
	If ObjectList( NounIndex - 7 ) = CharPosition Then 
	
		; confirmation message
		Print "O.K." 
		
		; make sure the object is set to carried
		ObjectList( NounIndex - 7 ) = CARRYING_OBJECT 
		
		; increase number of inventory items
		NumInvItems = NumInvItems + 1 
		Return
		
	End If ; objects being picked up
	
	; else you can't pick it up
	Print "It isn't here!" 

	Return

End Function ; GameGet

; ************* DROP         *************
Function GameDrop()

	; if you try to drop an item
	; that can't be dropped, then
	If NounIndex < N_KNIFE Then 
	
		; print a message about
		; the mental faculties of
		; the player
		Print "Don't be silly" 
		Return
	End If
	
	; if the item is a cuff
	For ObjectIndex = YELLOW_CUFFS To BLUE_CUFFS
	
		; and you have the item in your possesion
		If ObjectList( ObjectIndex ) = CARRYING_OBJECT 
		
			; what's the word?
			If NounIndex = N_HANDCUFFS Then 
		
				; take it out of your inventory
				NounIndex = ObjectIndex + 7 
				
				; remove number of cuffs owned
				NumCuffs = NumCuffs - 1 
				
				; drop to end of the loop
				ObjectIndex = 10
			
			End If ; noun is handcuffs
			
		End If ; have cuffs
		
	Next ; cuff items
	
	; If you don't have the item to drop then
	If ObjectList( NounIndex - 7 ) <> CARRYING_OBJECT Then
	
		; remind the player that they
		; need some therapy for dropping
		; objects they don't have 
		Print "You haven't got it!" 
		Return
		
	End If ; don't have an item to drop
	
	; otherwise acknowledge item drop
	Print "O.K." 
	
	; update item position (room )
	ObjectList( NounIndex - 7 ) = CharPosition 
	
	; decrease number of inventory items
	NumInvItems = NumInvItems - 1
	
	; If we drop the plug in the room with the drain
	; then yay! we win!
	If CharPosition = HOLE_ROOM 
		
		; the word is plugged
		If NounIndex = N_PLUG WinFlag = True
	
	End If ; hole room
	
	Return

End Function ; GameDrop

; ************* CUT          *************
Function GameCut()

	; If you don't have the knife
	If ObjectList( KNIFE ) <> CARRYING_OBJECT Then
	
		; remind the player an invisible knife
		; is as sharp as their wit 
		Print "You've nothing sharp enough!" 
		Return
		
	End If ; don't have knife
	
	; If you're trying to cut anything
	; other than the sea weed
	If NounIndex <> N_SEAWEED Then 
	
		; tell the player they can't cut it
		; and should give up right now.
		Print "You can't cut that!" 
		Return
	
	End If ; cutting anything other than seaweed
	
	; If you're in any other room
	; than the sea weed room..
	If CharPosition <> SEAWEED_ROOM Then 
	
		; you can't really do it
		Print "You can't do that" 
		Return
		
	End If ; not sea weed room
	
	; If you've cut it already	
	If CutFlag = True Then 
	
		; tell them no use in cutting the
		; cheese err, uhh, seaweed again
		Print "You've already done that!" 
		Return
		
	End If ; already cut
	
	; then all tests have passed, you may proceed
	; mighty adventurer and warrior!
	Print "The seaweed falls away to reveal an open window."
	
	; sea weed is now in the floor
	ObjectList( SEAWEED ) = SEAWEED_ROOM 
	
	; just made a new exit
	Exits( 5, 2 ) = BLUE_CUFF_ROOM
	
	; make sure they know they can't cut it again 
	CutFlag = True
	
	; change the description to reflect an open window.
	TempData$ = Left$( DataTwo$( SEAWEED_ROOM ), 9 )
	DataTwo$( SEAWEED_ROOM ) = TempData$ + "an open window in it" 
	Return 

End Function ; GameCut

; ************* WEAR         *************
; wear the flippers
Function GameWear()

	; If the player is wearing them and
	; tries to put them on again then
	If FlippersFlag = True 
	
		; is the word flippers?
		If NounName$( N_NONE ) = "FLI" Then 
	
			; again point to their mental stability
			Print "You've already got them on!" 
			Return
	
		End If ; the word is flippers
		
	End If ; flippers on
	
	; If you have the flippers and you wear them
	If ObjectList( FLIPPERS ) = CARRYING_OBJECT 
	
		; The word is?
		If NounName$( N_NONE ) = "FLI" Then 
		
			; yay, we have them on
			FlippersFlag = True 
			
			; now we're no longer carrying them
			ObjectList( FLIPPERS ) = OBJECT_GONE 
			
			; congratulate them on their cunning
			; use of manual dexterity
			Print "They fit nicely!" 
			Return
		
		End If ; flippers yay!
		
	End If ; have flippers, put them on
	
	; If they're trying to put on some cuffs
	If NounIndex = N_HANDCUFFS 
	
		; we have more than - cuffs?
		If NumCuffs > 0 Then
	
			; Then smack them upside the head
			; for wanting to do such a thing. 
			Print "That's really silly!" 
			Return
		
		End If ; yes, but you tried to put them on
		
	End If ; noun is handcuffs
	
	; if all else fails, then tell
	; them they can't fit into that size 6
	Print "You can't wear that!" 
	Return

End Function ; wear

; ************* GIVE         *************
; well gee i wonder what this could be...
; this is for the seaweed and the bone
Function GameGive()

	; if we're in the sea horsey room and we
	; try to give them some seaweed
	If CharPosition = SEAHORSE_ROOM 
	
		; is the word? seaweed this time
		If NounIndex = N_SEAWEED Then
		
			; if we haven't got the seaweed
			If ObjectList( SEAWEED ) <> CARRYING_OBJECT Then 
			
				; sic the seahorses on them!
				Print "You haven't got it" 
				Return
			
			End If ; haven't got seaweed
		
			; otherwise all is well and good
	        
			; no longer have the seaweed
			ObjectList( SEAWEED ) = OBJECT_GONE
			
			; now there's a key
			ObjectList( KEY ) = SEAHORSE_ROOM
			
			; clue them in
			Print "Something glints in the corner" 
			Return
	
		End If ; seaweed's the word
		
	End If ; sea horse room
	
	; If we're in the bone fish room and we try to give
	If CharPosition = BONEFISH_ROOM 
	
		; I have a bone (noun) to pick with you
		If NounIndex = N_BONE Then 

			; Do we have the object?
			If ObjectList( BONE ) <> CARRYING_OBJECT Then 
			
				; no then point to the player
				; and make the "loco" motion
				Print "You haven't got it" 
				Return
			
			End If ; have the bone
			
			; okay well everything looks in order
			
			; no longer carrying the bone
			ObjectList( BONE ) = OBJECT_GONE 
			
			; a new exit emerges
			Exits( 7, 2 ) = DJ_CHEST_ROOM
			
			; let the player know he's not losing his mind
			Print "The Fish snatches the bone and retires to a corner"
			
			; reset the room description for the bonefish room
			D1$ = "You are in a cavern. In the corner"
			D2$ = "a Bonefish is chewing a thigh-bone" 
			
			; split up for readability.
			DataOne$( BONEFISH_ROOM ) = D1$
			DataTwo$( BONEFISH_ROOM ) = D2$
			
			Return
	
		End If ; them bones...
		
	End If ; have bone will travel
	
	; if we're facing the octopus
	If CharPosition = OCTOPUS_ROOM Then 
	
		; you can't give anything to an octopus
		; especially when it involves binding him
		; with rainbow colored handcuffs
		Print "That won't do any good!" 
		Return
	
	End If ; octopus room
	
	; there you are trying to give
	; objects to imaginary people again
	Print "Give it to whom?" 
	Return

End Function ; give

; ************* UNLOCK       *************
; Unlock Davy Jones' Locker
Function GameUnlock()

	; If you don't have the key
	If ObjectList( KEY ) <> CARRYING_OBJECT Then
	
		; tell them to jimmy the lock with
		; the knife, of course that won't
		; work but maybe this will convince
		; them that they are crazy. 
		Print "You haven't even got a key!" 
		Return
	
	End If ; no key
	
	; okay so you have the key, but guess what, you
	; have no place to put that key... 
	If NounIndex = N_DUMMY
	
		; are we in that door room?
		If CharPosition = DOOR_ROOM Then 
		
			; ask the player where they're trying to put that?
			Print "There isn't even a keyhole!" 
			Return
		
		End If ; door room

		; or the otherside?
		If CharPosition = GREEN_CUFF_ROOM Then 
		
			; ask the player where they're trying to put that?
			Print "This is the otherside of the keyless door." 
			Print "DUH!!"
			Return
		
		End If ; green cuff room
		
	End If ; right object, wrong key
	
	; if you're not in the room with the chest
	If CharPosition <> DJ_CHEST_ROOM Then 
	
		; tell them the should know better
		; than to try other things than
		; unlocking...
		Print "I don't see a lock anywhere, do you?" 
		Return
	
	End If ; not chest room, or not trying to unlock

	; trying to key something else?
	; maybe a car? but we're underwater
	If NounIndex <> N_CHEST Then 
	
		; tell them the should know better
		; than to try other things than
		; unlocking...
		Print "Uhh, trying to key something else?"
		Print "Can we say 'childish'?" 
		Return
	
	End If ; not chest room, or not trying to unlock
	
	; well if it is unlocked then
	If UnlockFlag = True Then 
	
		; tell them "YOU JUST DID IT YOU IDIOT!"
		Print "It's already unlocked!" 
		Return
	
	End If ; unlocked
	
	; okay well we're in the right room
	; the chest is here, and locked, and
	; we have a key to open it
	
	; unlock the chest
	UnlockFlag = True
	
	; put the handcuffs in there 
	ObjectList( YELLOW_CUFFS ) = DJ_CHEST_ROOM
	
	; tell them they done the right thing 
	Print "The key turns easily"
	
	; make sure the description reflects the fact that is just
	; an empty chest now.
	D1$ = "You are in a room with an open chest in the middle."
	D2$ = ""
	
	; split for comfort
	DataOne$( DJ_CHEST_ROOM ) = D1$
	DataTwo$( DJ_CHEST_ROOM ) = D2$
	
	Return

End Function ; unlock

; ************* USE          *************
; well we can only use the handcuffs.
Function GameUse()

	; not in the octopus room?
	If CharPosition <> OCTOPUS_ROOM Then 
	
		; anyone can plainly see you can't
		; use anything in this game except
		; rainbow colored handcuffs
		Print "You can't do that here" 
		Return
	
	End If ; not in octopus room

	; not using handcuffs?
	If NounIndex <> N_HANDCUFFS Then 
	
		; anyone can plainly see you can't
		; use anything in this game except
		; rainbow colored handcuffs
		Print "You want to do what?" 
		Return
	
	End If ; or have any hand cuffs
	
	; don't have enough hand cuffs?
	If NumCuffs < 4 Then 
	
		; then the octopus will eat you
		Print "You haven't got enough pairs!" 
		Return
	
	End If ; not enough cuffs
	
	; all is well, show the octopus' discomfort and know
	; that animal rights activists will be up in arms	
	Print "Now the octopus can't move.  He doesn't amused "
	Print "at these nasty turn of events.  Handcuffs! Pfft!" 
	
	; we no longer have cuffs
	For ObjectIndex =  YELLOW_CUFFS To BLUE_CUFFS
	 
		ObjectList( ObjectIndex ) = OBJECT_GONE 
	
	Next ; no cuffs
	
	; no cuffs here
	NumCuffs = 0 
	
	; we have a new exit
	Exits( 20, 4 ) = HOLE_ROOM
	
	; reflect the current state of affairs, and realize
	; that octopus could pull you apart if he wasn't bound
	D1$ = "You are in a corridor with a current going west "
	D2$ = "A colorful, handcuffed octopus sits sulking."
	DataOne$( OCTOPUS_ROOM ) = D1$
	DataTwo$( OCTOPUS_ROOM ) = D2$
	 
	Return

End Function ; Use

; ************* INVENTORY    *************
; show inventory
Function Inventory()

	; display the contents of your knapsack
	Print 
	Print "You are carrying : "
	
	; I guess we don't have anything 
	If NumInvItems = 0 Then
	
		; what a revalation 
		Print "Nothing!" 
		Return
	
	End If ; no items
	

	; if we got 'em flaunt 'em	
	For ObjectIndex = 1 To 10
		
		If ObjectList( ObjectIndex ) = CARRYING_OBJECT Then 
			Print "A " + ObjectName$( ObjectIndex )
		End If
		
	Next ; for objects
	 
	Return

End Function ; inventory

; ************* HELP         *************
; display help for those sissy's who can't
; cut it on their own.
Function GameHelp()

	Print 
	Print "These are the verbs you may use : " 
	
	; go through each verb
	For VerbCount = 1 To NumVerbs 
	
		; Display them.  Better than the neverending story!
		Print VerbName$( VerbCount ) 
	Next
	
	Print 
	Print "(You need only type the first three letters)" 
	
	Return

End Function ; help

; ************* LOOK         *************
; show the room
Function Look()

	; display room descriptions
	Print 
	Print DataOne$( CharPosition ) 
	Print DataTwo$( CharPosition ) 
	
	; If we're in the shark room then
	If CharPosition = SHARK_ROOM Then 
	
		; we're most likely dead
		LostFlag = True  
		Return
	
	End If ; shark room
	
	; run through each object
	For ObjectIndex = 1 To 10
		
		; as long as we're not in the sea weed window room
		; or the bones of long dead explorers room then...
		If ObjectIndex <> BONE Or CharPosition <> BONE_ROOM
		
			; display the object in that room
			If ObjectList( ObjectIndex ) = CharPosition Then
			
				; yep print it 
				Print "A " + ObjectName$( ObjectIndex ) + " is here"
			
			End If ; object in room
		
		End If ; not seaweed or bone room
		
	Next ; each object
	
	; Display exits if it's there.	
	Print 
	Print "Exits : "
	If Exits( CharPosition, 1 ) > 0 Then Print "North  " 
	If Exits( CharPosition, 2 ) > 0 Then Print "South  "
	If Exits( CharPosition, 3 ) > 0 Then Print "East   "
	If Exits( CharPosition, 4 ) > 0 Then Print "West   "
	Print 
	Print 
	Return

End Function ; Looky loo

; ************* INITIALIZE   *************
Function Initialize()
	
	; clear screen, make sure to point at
	; beginning of the data.
	Cls 
	Locate( 0, 0 ) 
	Restore GameData
	
	; Display title screen
	Print "Neptune's Caverns"
	Print "You have found the magic plug that"
	Print "belongs at the bottom of the sea and"
	Print "decide to replace it before the water"
	Print "drains away.  With your scuba gear you"
	Print "dive into the ocean and begin your "
	Print "adventure..."
	Print
	Print "(If you need assistance type HELP)"
		
	; init variables
	NumNouns     = 14        ; number of nouns
	NumVerbs     = 12        ; number of verbs
	CharPosition = DOOR_ROOM ; character position (room)
	NumInvItems  = 1         ; number of inventory items
	NumCuffs     = 0         ; number of handcuffs
	WinFlag      = False     ; Win flag
	LostFlag     = False     ; Lost Flag
	UnlockFlag   = False     ; Unlock Flag
	FlippersFlag = False     ; Flipper Flag
	CutFlag      = False     ; Cut Flag
	VerbIndex    = V_NONE         ; Verb index
	NounIndex    = N_NONE         ; Noun Index

	; read room descriptions into string array
	For RoomIndex = 1 To 24
	

		; description 1 in data one, 
		; and description 2 in data 2
		; using element 0 as a temp
		Read DataOne$( 0 )
		Read DataTwo$( 0 )
						
		; dump temp data into 20 rooms 
		DataOne$( RoomIndex ) = DataOne$( 0 ) 
		DataTwo$( RoomIndex ) = DataTwo$( 0 )

		; I've decided to give rooms 17, 18, 
		; 23, 24 their own data. instead of
		; sharing room 12's specifically.
	
		; run through each exit
		For ExitNum = 1 To 4 
			
			; read exit data
			Read Exits( RoomIndex, ExitNum ) 
			
		Next ; exits 
		
	Next ; rooms
	
	; run through each object
	For ObjectIndex = 1 To 10
	
		; read the names into the name array 
		Read ObjectName$( ObjectIndex )
		
		; read the actual object numbers
		; into the object list.
		Read ObjectList( ObjectIndex ) 
	
	Next ; each object
	
	; run through each noun
	For NounCount = 1 To NumNouns 
		
		; read description
		Read NounName$( NounCount ) 
		
	Next ; nouns
	
	; run through each verb
	For VerbCount = 1 To NumVerbs 
	
		; verb descriptions for help
		Read VerbName$( VerbCount ) 
		
	Next ; verbs
	
	Return 

End Function ; initialize

; ************* END GAME     *************
; guess what... this is the end of the game
; we get to tell the player how they royally
; screwed up, or successfully completed the
; task.
Function EndGame()

	; display results
	
	If CharPosition = SHARK_ROOM Then ; Shark bait buddy
		
		Print "With a snap, the shark bites off your head " 
		Print
		Print
		
	Else If CharPosition = URCHIN_ROOM Then ; urchin's of doom
		
		Print "You have stepped on a poisonous sea-urchin"
		Print "You die a horrible death." 
		Print
		Print
		
	Else
		
		; I guess we plugged up that drain
		Print "With a 'THUNK' the plug drops into the "
		Print "hole and the swirling waters grow still"
		Print "Congratulations! You saved the seas! " 
		Print
		Print
		
	End If ; win or lost
	
	; Want to try this again?
	Response$ = Upper( Input$( "Do you wish to play again? " ) )
	
	; yes?
	If Left$( Response$, 1 ) = "Y" Then 
	
		; reset everything
		NewGameFlag = False
	
	Else ; no
	
		; let's get out of here!
		ExitFlag = True
	
	End If ; play again
		
	Return

End Function ; end game

; the rest is the actual game data
.GameData
; ************* DESCRIPTIONS *************
; First lines are first descriptions, then
; second descriptions, then exits. 

	Data "You are on the seabed.  The way west is "
	Data "blocked by a high coral reef."
	Data 0, 0, 2, 0 
	
	Data "You are on the seabed.  To the south a "
	Data "barnacled wall towers above you."
	Data 0, 0, 3, 1 
	
	Data "You are in front of a wooden door.  You "
	Data "can see no handle."
	Data 0, 0, 4, 2 
	
	Data "You are on the seabed.  To the south a "
	Data "barnacled wall towers above you."
	Data 0, 0, 5, 3
	
	Data "You are on the seabed.  To the south a barnacled "
	Data "wall has a square patch of seaweed growing on it."
	Data 0, 0, 6, 4 
	
	Data "You are on the seabed.  To the south is a "
	Data "barnacled wall.  A cliff blocks the way east."
	Data 0, 0, 0, 5 
	
	Data "You are in a long, low cavern.  At the far "
	Data "end a large bonefish is swimming around."
	Data 0, 0, 8, 0 
	
	Data "You are in a brightly lit chamber.  The walls, "
	Data "floor, and room grlow in shimmering light."
	Data 0, 14, 0, 7

	Data "You are in a dimly lit cavern with a huge "
	Data "door at the far end.  You can see no handle."
	Data 0, 15, 0, 0 
	
	Data "You are in a room full of hungry seahorses. "
	Data "They nuzzle your hand in a friendly manner."
	Data 0, 16, 0, 0 
	
	Data "You are in a small room.  The north wall has a small "
	Data "window in it through which you can see the seabed"
	Data 5, 17, 12, 0 
	
	Data "You are in an amazingly square room.  The walls, "
	Data "floor and room are all square as are all the exits. "
	Data 0, 18, 0, 11
	
	Data "You are in a tiny little room that is occupied "
	Data "by a chest inscribed with the initials D.J."
	Data 7, 0, 0, 0 
	
	Data "You are in a cold, murky room.  Grey mud swirls "
	Data "around you and you feel a faint current to the east."
	Data 8, 0, 15, 0 
	
	Data "You are in a gloomy and eerie place.  All around "
	Data "you are the bones of long dead explorers!"
	Data 9, 21, 16, 14 
	
	Data "You are in a square room.   The south exit has the words 'Do not enter!'"
	Data "above it.  The north and west doorways are crawling with sea urchins."
	Data 10, 22, 17, 15
	
	Data "You are in an amazingly square room.  You know there "
	Data "may be other square rooms all as exciting as this one."
	Data 11, 23, 18, 16 
	
	Data "Wow, it's amazingly square i've never seen this "
	Data "before... I bet the exits are squre too... yep."
	Data 12, 24, 0, 17 
	
	Data "You are in a circular room with a very strong current that "
	Data "swirls around the room and down a hole in the floor."
	Data 0, 0, 20, 0 
	
	Data "You are in a corridor with a strong current going west.  Your way "
	Data "is blocked by tthe arms of a large rainbow colored octopus."
	Data 0, 0, 21, 0
	
	Data "You're in a shipwrecked captain's cabin. "
	Data "You feel the flow of water to the west."
	Data 15, 0, 0, 20 
	
	Data "You see a rush of swirling water and "
	Data "face the jaws of a great white shark."
	Data 0, 0, 0, 0 
	
	Data "Square rooms never cease to amaze me.  I could die "
	Data "and go to heaven if I ever see another in my life."
	Data 17, 0, 24, 0 
	
	Data "This square room looks like any other squre room."
	Data "You know, square walls, floor, and exits as well."
	Data 18, 0, 0, 23

; ************* OBJECTS      *************
	Data "KNIFE", 2
	Data "PAIR OF FLIPPERS", 6
	Data "KEY", 0
	Data "CLUMP OF SEAWEED", 0
	Data "ROTTEN OLD BONE", 15
	Data "MAGIC PLUG", 99
	Data "YELLOW PAIR OF HANDCUFFS", 0
	Data "GREEN PAIR OF HANDCUFFS", 9
	Data "RED PAIR OF HANDCUFFS", 17
	Data "BLUE PAIR OF HANDCUFFS", 11

; ** NOUNS ** ;
	Data "NOR"
	Data "SOU"
	Data "EAS"
	Data "WES"
	Data "DOO"
	Data "CHE"
	Data "WIN"
	Data "KNI"
	Data "FLI"
	Data "KEY"
	Data "SEA"
	Data "BON"
	Data "PLU"
	Data "HAN"

; ** VERBS ** ;
	Data "GO "
	Data "GET"
	Data "DROP"
	Data "CUT"
	Data "WEAR"
	Data "GIVE"
	Data "UNLOCK"
	Data "USE"
	Data "INVENTORY"
	Data "HELP"
	Data "LOOK"
	Data "QUIT"
