; ID: 2673
; Author: stanrol
; Date: 2010-03-22 20:33:30
; Title: 3D Adventure
; Description: ahahahahah

;program super V
;:***
;An adventure game of mega proportions
;designed autumn '10
;made in Melbourne
;press Save and save it all * here
Function recta(x1,y1,x2,y2,x3,y3,x4,y4)
Line x1,y1,x2,y2
Line x2,y2,x3,y3
Line x3,y3,x4,y4
Line x4,y4,x1,y1
;Return Abs(x2-x1*
End Function
Function rec(x,y,w,h)
Line x,y,x+w,y
Line x+w,y,x+w,y+h
Line x+w,y+h,x,y+h
Line x,y+h,x,y
Return Abs(w*h)
End Function
Function p(s$)
Print s$
End Function
Function PrintScore(n)
Print "Score is "+n
End Function
Function prompt(s$)
Print s$
Print "Press any key to continue"
WaitKey
End Function
Function pro(s$)
Prompt(s$)
End Function
Type pt
Field x,y,z
End Type
Type pt2D
Field x,y
End Type
Function wk()
WaitKey
End Function
Function pr(s$)
Print s$
WaitKey
End Function
AppTitle "Super adventure shareware by Roland- share it Welcome to the world of the plastic beach"
Graphics 600,444,0,2:Global name$="no one":Global money=255:Global mana=200:Global HP=1000:Global goblins=5
Global morale=29:Global fsword=False:Global fExit=False:Global famulet=False
Type robot
Field loc.pt
Field name$
End Type
SeedRnd MilliSecs():HidePointer
Print "To buy visit http://matrix.happyhost.org or livingcafe.com soon":Print "Price point $49.90"
Print "dont press a key yet.":Delay 2354
Print "Welcome eager traveller to this epic adventure.":name$=Input$("What's your name?")
While fExit=False
If HP>0 Then
Print money+ " rubles in your bank account"
Else
Print "You are dead"
End If
menu
Wend
p "Game ended":p "...and Gorrilasz hitler hit u":p "Made by a guy who likes Tuna":p "don't press a key...":Delay 2888
ShowPointer
Function menu()	
Print "1 - buy item":Print "2 - enter shop":Print "3 - go to forrest":Print "4 - joust"
Print "5 - buy amulet":Print "6 - Set Sail":Print "8 - eat fresh":Print "7 - Quit game"
ch=Int(Input$("Enter your choice:"))
Select ch
Case 1
fsword=True:cost=Rand(51,220)
money=money-cost
prompt "You buy a longsword for "+cost+" rubles"
Case 2
PlaySound "H:\download\media\sound\spookysoundfx_-_psionic\creak04.wav"
If money>=5 Then
money=money-5
prompt "You enter tavern and buy a beer for 5 rub$"
Else
prompt "got no money"
End If	
Case 8
If money>=11 Then
money=money-11
Prompt "you eat at Subway."
Else
Print "Got no money."
End If
Case 3
prompt "Gone to park with trees. Got lost."
Case 6
Prompt "You go to Europe, Hungary - the butt B-3 of many jokes in English to do with food and salivation"
Case 7
fExit=True
Case 5
a=Rand(42,79)
If money>=a Then
P "You buy a gold studded amulet of protection for "+a+" rubles"
money=money-a
Else
Print "you don't have enough money."
End If
Case 4
enemyhp=50
While enemyhp>=1 And HP>=1
enemyhit=Rand(0,500):HP=HP-enemyhit:Color 255,1,1:Prompt "You were hit for "+enemyhit+". You have "+HP+"/1000 HP remaining."
If HP<=0 Then
Exit
End If
hit=Rand(0,25)
If fsword Then
hit=hit+Rand(0,4)
End If
enemyhp=enemyhp-hit:Color 100,100,255:Prompt "You hit monster for "+hit+" "+enemyhp+"/50 hp"
Wend
If enemyhp>0 Then
Color 255,255,0:prompt "You lost loser."
Else
Color 0,255,10
prompt "You won'"
End If
Default
prompt "Wrong choice; please try again":menu
End Select
End Function
