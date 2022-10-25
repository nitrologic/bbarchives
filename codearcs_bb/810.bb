; ID: 810
; Author: Uhfgood
; Date: 2003-10-17 04:25:59
; Title: Neptune's Caverns - Original
; Description: A small text game port, from a kids book from c64 basic, to blitz.  Straight Port.

; Neptune's Cavern's -- A text adventure listed in
; the book "Write your own program : A database adventure game"
; by Steve Rodgers and Marcus Milton orginally published
; in 1985.  Ported to BlitzBasic in 2003 by Keith Weatherby II

Global NN = 14 ; number of nouns
Global VV = 12 ; number of verbs
Global CP = 3  ; character position (room)
Global IN = 1  ; number of inventory items
Global HC = 0  ; number of handcuffs
Global WF = 0  ; Win flag
Global LF = 0  ; Lost Flag
Global UF = 0  ; Unlock Flag
Global FF = 0  ; Flipper Flag
Global CF = 0  ; Cut Flag
Global VBI = 0 ; Verb index
Global NOI = 0 ; Noun Index

Dim T$( 24 )    ; Room description part 1
Dim D$( 24 )    ; room description part 2
Dim EX( 24, 4 ) ; Exits
Dim NO$( NN )   ; Noun descriptions
Dim VB$( VV )   ; Verb Descriptions
Dim OB$( 10 )   ; Object Descriptions
Dim OBI( 10 )   ; Object index

Graphics 640, 480, 16, 2

.l10 ;Neptune's Cavern
.l20 Gosub l5000 ;Initialize
.l30 Gosub l1000 ;Input
.l40 Gosub l2000 ;Sort
.l50 If ( WF <> 1 ) And ( LF <> 1 ) Then Goto l30
.l60 Goto l5500 ;End
.l70 End

.l1000 ; ************* INPUT      *************
.l1010 VB$( 0 ) = "" : NO$( 0 ) = "" : R$ = ""
.l1020 R$ = Input( "What do you do next? " ) : R$ = Upper$( R$ ) : Cls : Locate( 0, 0 )
.l1030 For I = 1 To Len( R$ )
.l1040 If Mid$( R$, I, 1 ) = " " Then VB$( 0 ) = Left$( R$, 3 ) : NO$( 0 ) = Mid$( R$, I + 1, 3 ) : I = Len( R$ )
.l1050 Next
.l1060 If NO$( 0 ) <> "" Then Return
.l1070 R$ = Left$( R$, 3 ) : If R$ = "QUI" Then End
.l1080 If R$ = "NOR" Or  R$ = "SOU" Or R$ = "EAS" Or R$ = "WES" Then VB$( 0 ) = "GO " : NO$( 0 ) = R$ : Return
.l1090 If R$ = "HEL" Or R$ = "INV" Or R$ = "LOO" Or R$ = "QUI" Then NO$( 0 ) = "DOO" : VB$( 0 ) = R$ : Return
.l1100 Print "I Don't understand that. "
.l1110 Goto l1010

.l2000 ; ************* SORT         *************
.l2010 VBI = 0 : NOI = 0
.l2020 For I = 1 To VV
.l2030 If VB$( 0 ) = Left$( VB$( I ), 3 ) Then VBI = I : I = VV
.l2040 Next
.l2050 For I = 1 To NN
.l2060 If NO$( 0 ) = NO$( I ) Then NOI = I : I = NN
.l2070 Next
.l2080 If NOI = 0 Or VBI = 0 Then Print "I don't understand that." : Return
.l2090 Select VBI 
Case 1 Goto l3000 
Case 2 Goto l3100 
Case 3 Goto l3200 
Case 4 Goto l3300 
Case 5 Goto l3400 
Case 6 Goto l3500 
Case 7 Goto l3620 
Case 8 Goto l3700
Case 9 Goto l3800
Case 10 Goto l3900 
Case 11 Goto l4000 
End Select

.l3000 ; ************* GO           *************
.l3010 If NOI > 4 Then Print "Go where?" : Return
.l3020 If EX( CP, NOI ) = 0 Then Print "No Exit That Way" : Return
.l3030 If CP = 16 And ( NOI = 1 Or NOI = 4 ) And FF = 0 Then LF = 1 : Return
.l3040 CP = EX( CP, NOI ) : Gosub l4000
.l3050 Return

.l3100 ; ************* GET          *************
.l3110 If NOI = 6 And CP = 13 Then Print "It's too heavy!" : Return
.l3120 If NOI < 8 Then Print "Don't be Silly" : Return
.l3130 If OBI( NOI - 7 ) = 99 And NO$( 0 ) <> "HAN" Then Print "You've already got it!" : Return
.l3140 For I = 7 To 10
.l3150 If( OBI( I ) = CP And NOI = 14 ) Then NOI = I + 7 : HC = HC + 1 : I = 10
.l3160 Next
.l3170 If OBI( NOI - 7 ) = CP Then Print "O.K." : OBI( NOI - 7 ) = 99 : IN = IN + 1 : Return
.l3180 Print "It isn't here!" : Return

.l3200 ; ************* DROP         *************
.l3210 If NOI < 8 Then Print "Don't be silly" : Return
.l3220 For I = 7 To 10
.l3230 If( OBI( I ) = 99 And  NOI = 14 ) Then NOI = I + 7 : HC = HC - 1 : I = 10
.l3240 Next
.l3250 If OBI( NOI - 7 ) <> 99 Then Print "You haven't got it!" : Return
.l3260 Print "O.K." : OBI( NOI - 7 ) = CP : IN = IN - 1
.l3270 If CP = 19 And NOI = 13 Then WF = 1
.l3280 Return

.l3300 ; ************* CUT          *************
.l3310 If OBI( 1 ) <> 99 Then Print "You've nothing sharp enough!" : Return
.l3320 If NOI <> 11 Then Print "You can't cut that!" : Return
.l3330 If CP <> 5 Then Print "You can't do that" : Return
.l3340 If CF = 1 Then Print "You've already done that!" : Return
.l3350 Print "The seaweed falls away to reveal an open window."
.l3360 OBI( 4 ) = 5 : EX( 5, 2 ) = 11 : CF = 1
.l3370 D$( 5 ) = Left$( D$( 5 ), 19 ) + "An open window in it" : Return 

.l3400 ; ************* WEAR         *************
.l3410 If FF = 1 And NO$( 0 ) = "FLI" Then Print "You've already got them on!" : Return
.l3420 If OBI( 2 ) = 99 And NO$( 0 ) = "FLI" Then FF = 1 : OB$( 2 ) = 0 : Print "They fit nicely!" : Return
.l3430 If NOI = 14 And HC > 0 Then Print "That's really silly!" : Return
.l3440 Print "You can't wear that!" : Return

.l3500 ; ************* GIVE         *************
.l3510 If CP = 10 And NOI = 11 Then Goto l3550
.l3520 If CP = 7 And NOI = 12 Then Goto l3580
.l3530 If CP = 20 Then Print "That won't do any good!" : Return
.l3540 Print "Nothing here want's it" : Return
.l3550 If OBI( 4 ) <> 99 Then Print "You haven't Got it" : Return
.l3560 OBI( 4 ) = 0 : OBI( 3 ) = 10
.l3570 Print "Something glints in the corner" : Return
.l3580 If OBI( 5 ) <> 99 Then Print "You haven't got it" : Return
.l3590 OBI( 5 ) = 0 : EX( 7, 2 ) = 13
.l3600 Print "The Fish snatches the bone and retires to a corner"
.l3610 T$( 7 ) = "You are in a cavern. In the Corner A"
.l3615 D$( 7 ) = "Bonefish is chewing a thigh-bone" : Return

.l3620 ; ************* UNLOCK       *************
.l3630 If OBI( 3 ) <> 99 Then Print "You haven't even got a key!" : Return
.l3640 If NOI = 5 And ( CP = 3 Or CP = 9 ) Then Print "There isn't even a keyhole!" : Return
.l3650 If CP <> 13 Or NOI <> 6 Then Print "You can't do that" : Return
.l3660 If UF = 1 Then Print "It's already unlocked!" : Return
.l3670 UF = 1 : OBI( 7 ) = 13 : Print "The key turns easily"
.l3680 T$( 13 ) = "You are in a room with an open chest in the middle."
.l3690 D$( 13 ) = "" : Return

.l3700 ; ************* USE          *************
.l3710 If CP <> 20 Or NOI <> 14 Then Print "You can't do that here" : Return
.l3720 If HC < 4 Then Print "You haven't go enough pairs!" : Return
.l3730 Print "The octopus can't movie.  He isn't amused!" 
.l3740 For I =  7 To 10 : OBI( I ) = 0 : Next
.l3750 HC = 0 : EX( 20, 4 ) = 19
.l3760 T$( 20 ) = "You are in a corridor with a current going west"
.l3770 D$( 20 ) = "A colorful, manacled octopus sits sulking. " : Return

.l3800 ; ************* INVENTORY    *************
.l3810 Print : Print "You are carrying : " 
.l3820 If IN = 0 Then Print "Nothing!" : Return
.l3830 For I = 1 To 10
.l3840 If OBI( I ) = 99 Then Print "A " + OB$( I )
.l3850 Next : Return

.l3900 ; ************* HELP         *************
.l3910 Print : Print "These are the verbs you may use : " 
.l3920 For I = 1 To VV : Print VB$( I ) : Next
.l3930 Print : Print "(You need only type the first three letters)" : Return


.l4000 ; ************* LOOK         *************
.l4010 Print : Print T$( CP ) : Print D$( CP ) : If CP = 22 Then LF = 1 : Return
.l4020 For I = 1 To 10
.l4030 If I = 5 And CP = 15 Then Goto l4050
.l4040 If OBI( I ) = CP Then Print "A " + OB$( I ) + " is here"
.l4050 Next
.l4060 Print : Print "Exits : "
.l4070 If EX( CP, 1 ) > 0 Then Print "North  " 
.l4080 If EX( CP, 2 ) > 0 Then Print "South  "
.l4090 If EX( CP, 3 ) > 0 Then Print "East   "
.l4100 If EX( CP, 4 ) > 0 Then Print "West   "
.l4110 Print : Print : Return

.l5000 ; ************* INITIALIZE   *************
.l5010 Cls : Locate( 0, 0 ) : Restore l6000
.l5020 Print "Neptune's Caverns"
.l5030 Print "You have found the magic plug that"
.l5040 Print "belongs at the bottom of the sea and"
.l5050 Print "decide to replace it before the water"
.l5060 Print "drains away.  With your scuba gear you"
.l5070 Print "dive into the ocean and begin your "
.l5080 Print "adventure..."
.l5090 Print "(If you need assistance type HELP)"
.l5100 ; init variables - nn, vv, cp, in, hc, wf, lf, uf, ff, cf
	NN = 14 ; number of nouns
	VV = 12 ; number of verbs
	CP = 3  ; character position (room)
	IN = 1  ; number of inventory items
	HC = 0  ; number of handcuffs
	WF = 0  ; Win flag
	LF = 0  ; Lost Flag
	UF = 0  ; Unlock Flag
	FF = 0  ; Flipper Flag
	CF = 0  ; Cut Flag
	VBI = 0 ; Verb index
	NOI = 0 ; Noun Index

.l5110 ; create arrays - T$, D$, EX, NO$, VB$ 
.l5120 ; create the rest of the arrays - OB$, OBI
.l5130 For I = 1 To 24
.l5140 If I = 17 Or I = 18 Or I = 23 Or I = 24 Then T$( I ) = T$( 12 ) : D$( I ) = D$( 12 ) : Goto l5160
.l5150 Read T$( 0 ), D$( 0 ) : T$( I ) = T$( 0 ) : D$( I ) = D$( 0 )
.l5160 Next
.l5170 For I = 1 To 24 : For J = 1 To 4 : Read EX( I, J ) : Next : Next
.l5180 For I = 1 To 10 : Read OB$( I ), OBI( I ) : Next
.l5190 For I = 1 To NN : Read NO$( I ) : Next
.l5200 For I = 1 To VV : Read VB$( I ) : Next
.l5210 Gosub l4000 : Return : ; look

.l5500 ; ************* END GAME     *************
.l5510 If CP = 22 Then Print "with a snap, the shark bites off your head " : Goto l5570
.l5520 If CP = 16 Then Print "You have stepped on a poisonous sea-urchin"
.l5530 If CP = 16 Then Print "You die a horrible death." : Goto l5570
.l5540 Print "With a 'THUNK' the plug drops into the "
.l5550 Print "hole and the swirling waters grow still"
.l5560 Print "Congratulations! You saved the seas! " 
.l5570 R$ = Upper( Input$( "Do you wish to play again? " ) )
.l5580 If Left$( R$, 1 ) = "Y" Then Goto l10
.l5590 Return

.l6000 ; ************* DESCRIPTIONS *************
.l6010 Data "You are on the seabed.  The way west is "
.l6011 Data "blocked by a high coral reef."
.l6020 Data "You are on the seabed.  To the south a "
.l6021 Data "barnacled wall towers above you."
.l6030 Data "You are in front of a wooden door.  You "
.l6031 Data "can see no handle."
.l6040 Data "You are on the seabed.  To the south a "
.l6041 Data "barnacled wall towers above you."
.l6050 Data "You are on the seabed.  To the south a barnacled "
.l6051 Data "wall has a square patch of seaweed growing on it."
.l6060 Data "You are on the seabed.  To the south is a "
.l6061 Data "barnacled wall.  A cliff blocks the way east."
.l6070 Data "You are in a long, low cavern.  At the far "
.l6071 Data "end a large bonefish is swimming around."
.l6080 Data "You are in a brightly lit chamber.  The walls, "
.l6081 Data "floor, and room grlow in shimmering light."
.l6090 Data "You are in a dimly lit cavern with a huge "
.l6091 Data "door at the far end.  You can see no handle."
.l6100 Data "You are in a room full of hungry seahorses. "
.l6101 Data "They nuzzle your hand in a friendly manner."
.l6110 Data "You are in a small room.  The north wall has a small "
.l6111 Data "window in it through which you can see the seabed"
.l6120 Data "You are in an amazingly square room.  The walls, "
.l6121 Data "floor and room are all square as are all the exits. "
.l6130 Data "You are in a tiny little room that is occupied "
.l6131 Data "by a chest inscribed with the initials D.J."
.l6140 Data "You are in a cold, murky room.  Grey mud swirls "
.l6141 Data "around you and you feel a faint current to the east."
.l6150 Data "You are in a gloomy and eerie place.  All around "
.l6151 Data "you are the bones of long dead explorers!"
.l6160 Data "You are in a square room.   The south exit has the words 'Do not enter!'"
.l6161 Data "above it.  The north and west doorways are crawling with sea urchins."
.l6170 Data "You are in a circular room with a very strong current that "
.l6171 Data "swirls around the room and down a hole in the floor."
.l6180 Data "You are in a corridor with a stron gcurrent going west.  Your way "
.l6181 Data "is blocked by tthe arms of a large rainbow colored octopus."
.l6190 Data "You're in a shipwrecked captain's cabin. "
.l6191 Data "You feel the flow of water to the west."
.l6200 Data "You see a rush of swirling water and "
.l6201 Data "face the jaws of a great white shark."

.l6210 ; ************* EXITS        *************
.l6220 Data  0, 0, 2, 0, 0, 0, 3, 1, 0, 0, 4, 2, 0, 0, 5, 3
.l6230 Data  0, 0, 6, 4, 0, 0, 0, 5, 0, 0, 8, 0, 0, 14, 0, 7
.l6240 Data  0, 15, 0, 0, 0, 16, 0, 0, 5, 17, 12, 0, 0, 18, 0, 11 
.l6250 Data  7, 0, 0, 0, 8, 0, 15, 0, 9, 21, 16, 14, 10, 22, 17, 15
.l6260 Data 11, 23, 18, 16, 12, 24, 0, 17, 0, 0, 20, 0, 0, 0, 21, 0
.l6270 Data 15, 0, 0, 20, 0, 0, 0, 0, 17, 0, 24, 0, 18, 0, 0, 23

.l6300 ; ************* OBJECTS      *************
.l6310 Data "KNIFE", 2
.l6320 Data "PAIR OF FLIPPERS", 6
.l6330 Data "KEY", 0
.l6340 Data "CLUMP OF SEAWEED", 0
.l6350 Data "ROTTEN OLD BONE", 15
.l6360 Data "MAGIC PLUG", 99
.l6370 Data "YELLOW PAIR OF HANDCUFFS", 0
.l6380 Data "GREEN PAIR OF HANDCUFFS", 9
.l6390 Data "RED PAIR OF HANDCUFFS", 17
.l6400 Data "BLUE PAIR OF HANDCUFFS", 11

.l6500 ; ************* NOUNS        *************
.l6510 Data "NOR", "SOU", "EAS", "WES", "DOO", "CHE", "WIN", "KNI", "FLI", "KEY", "SEA", "BON", "PLU", "HAN"
.l6520 ; ************* VERBS        *************
.l6530 Data "GO ", "GET", "DROP", "CUT", "WEAR", "GIVE", "UNLOCK", "USE", "INVENTORY", "HELP", "LOOK", "QUIT"
