; ID: 2678
; Author: Streaksy
; Date: 2010-03-25 10:05:11
; Title: Plants VS Zombies Zen Garden Editor
; Description: Hack your savegame and edit what plants you have

Graphics 410,468,32,2
AppTitle "Zen Garden Editor"
Global Typ=1
Global Garden=0
Global Column=0
Global Row=0 ;3
Global Direction=0
Global F=0
Global G=0 ;time stuff?
Global H=0 ;time stuff
Global Colour=0 ;2
Global Growth=0
Global Watered=0
Global WateredTarget=4
Global Require=0
Global N=0
Global O=0 ;time stuff?
Global P=0 ;time stuff?
Global Q=0
Global R=0
Global S=0
Global T=0
Global U=0
Global V=0

Dim TypName$(255)
TypName$(0)="Peashooter"
TypName$(1)="Sunflower"
TypName$(2)="Cherry Bomb"
TypName$(3)="Wall-Nut"
TypName$(4)="Potato Mine"
TypName$(5)="Show Pea"
TypName$(6)="Chomper"
TypName$(7)="Repeater"
TypName$(8)="Puff-Shroom"
TypName$(9)="Sun-Shroom"
TypName$(10)="Fume-Shroom"
TypName$(11)="Grave Buster"
TypName$(12)="Hypno-Shroom"
TypName$(13)="Scaredy-Shroom"
TypName$(14)="Ice-Shroom"
TypName$(15)="Doom-Shroom"
TypName$(16)="Lily Pad"
TypName$(17)="Squash"
TypName$(18)="Threepeater"
TypName$(19)="Tangle Kelp"
TypName$(20)="Jalapeno"
TypName$(21)="Spikeweed"
TypName$(22)="Torchwood"
TypName$(23)="Tall-Nut"
TypName$(24)="Sea-Shroom"
TypName$(25)="Plantern"
TypName$(26)="Cactus"
TypName$(27)="Blover"
TypName$(28)="Split Pea"
TypName$(29)="Starfruit"
TypName$(30)="Pumpkin"
TypName$(31)="Magnet-Shroom"
TypName$(32)="Cabbage-Pult"
TypName$(33)="Flower Pot"
TypName$(34)="Kernel-Pult"
TypName$(35)="Coffee Bean"
TypName$(36)="Garlic"
TypName$(37)="Umbrella Leaf"
TypName$(38)="Marigold"
TypName$(39)="Melon-Pult"

TypName$(40)="Gatling Pea"
TypName$(41)="Twin Sunflower"
TypName$(42)="Gloom-Shroom"
TypName$(43)="Cattail"
TypName$(44)="Winter Melon"
TypName$(45)="Gold Magnet"
TypName$(46)="Spikerock"
TypName$(47)="Cob Cannon"
TypName$(48)="Imitater"

TypName$(49)="Explode-o-Nut"
TypName$(50)="Giant Wall-Nut"
TypName$(51)="Sprout"
TypName$(52)="Reverse Repeater"

Dim GardenName$(4)
GardenName$(0)="Daytime Garden"
GardenName$(1)="Mushroom Garden";"Nighttime Garden"
GardenName$(2)="Wheelbarrow"
GardenName$(3)="Water Garden"

Global stages=4
Dim stagename$(stages)
StageName(1)="Sprout"
StageName(2)="Small"
StageName(3)="Medium"
StageName(4)="Mature"

Global Cols=13
Dim ColName$(255)
colname(1)="White"
colname(2)="Imitated (Low Saturation)"
colname(3)="White"
colname(4)="Magenta"
colname(5)="Orange"
colname(6)="Pink"
colname(7)="Cyan" ;sky blue?
colname(8)="Red"
colname(9)="Blue"
colname(10)="Purple"
colname(11)="Pale Pink"
colname(12)="Yellow"
colname(13)="Pale Green"
colname(14)="White"
colname(15)="White"
colname(16)="White"
colname(17)=""
colname(18)=""
colname(19)=""
colname(20)=""
colname(21)=""
colname(22)=""
colname(23)=""
colname(24)=""
colname(25)=""
colname(26)=""
colname(27)=""
colname(28)=""
colname(29)=""
colname(30)=""
colname(31)=""
colname(32)=""
colname(33)=""
colname(34)=""
colname(35)=""
colname(36)=""



Dim PColName$(255)
pcolname(1)="Normal"
pcolname(2)="Imitated (Low Saturation)"
pcolname(3)="Normal"
pcolname(4)="Red with magenta eyes"
pcolname(5)="Orange"
pcolname(6)="Orange with pink eyes"
pcolname(7)="Green with teal eyes"
pcolname(8)="Red"
pcolname(9)="Dark green with blue eyes"
pcolname(10)="Red with purple eyes"
pcolname(11)="Normal with pale pink eyes"
pcolname(12)="Normal with yellow eyes"
pcolname(13)="Mossy green"



fn$="C:\ProgramData\PopCap Games\PlantsVsZombies\userdata\user3.dat"


cm$=CommandLine$()
If cm="" Then cm$=Chr(34)+"C:\ProgramData\PopCap Games\PlantsVsZombies\userdata\user1.dat"+Chr(34)

If Len(cm)<2 Then RuntimeError "Open with a userfile!"
cm=Left(cm,Len(cm)-1)
cm=Right(cm,Len(cm)-1)
fn=cm

If Lower(Right(fn,4))<>".dat" Then RuntimeError "Wrong file!  Open with a userdata\user#.dat file!"

For t=Len(fn) To 1 Step -1
If Mid(fn,t,1)="\" Then stat=t+1:Exit
Next
If stat=0 Then RuntimeError "Wierd filename..."

If Lower(Mid(fn,stat,4))<>"user" Then RuntimeError "Wrong file!  Open with a userdata\user#.dat file!"


siz=FileSize(fn)
fil=OpenFile(fn)
If fil=0 Then RuntimeError "Couldn't open "+fn
If siz=0 Then RuntimeError "Size error"



SeekFile fil,(siz-88)+(4*11):wateredtarget=ReadByte(fil):SeekFile fil,FilePos(fil)-1
If wateredtarget=0 Or wateredtarget>8 Then RuntimeError "Add a new plant to the Zen Garden and try again!  Make sure you do nothing else after aquiring a new plant!"


API_ShowWindow(SystemProperty("AppHWND"),5)


.restart
SeekFile fil,siz-88
Cls:Locate 0,0
Print "MOST RECENTLY AQUIRED PLANT:"
Print ""

Typ=ReadByte(fil):SeekFile fil,FilePos(fil)-1
Print "(P) Plant: "+typname(typ)
;Nxt=0:WriteByte fil,nxt:Print "Now it is "+nxt+" ("+typname(nxt)+")"

SeekFile fil,(siz-88)+(4*1):Garden=ReadByte(fil):SeekFile fil,FilePos(fil)-1
Print "(L) Location: "+gardenname(garden)

SeekFile fil,(siz-88)+(4*2):Column=ReadByte(fil):SeekFile fil,FilePos(fil)-1
SeekFile fil,(siz-88)+(4*3):Row=ReadByte(fil):SeekFile fil,FilePos(fil)-1
If Garden=0 Then Print "(S) Slot: x"+(column+1)+" y"+(row+1)+""
If Garden>0 And garden<>2 Then Print "(S) Slot: "+(column+1)

SeekFile fil,(siz-88)+(4*4):Direction=ReadByte(fil):SeekFile fil,FilePos(fil)-1
If Direction=0 Then Print "(F) Facing: Right (Normal)" Else Print "(F) Facing: Left (Flipped)"
SeekFile fil,(siz-88)+(4*8):colour=ReadByte(fil):SeekFile fil,FilePos(fil)-1
If typ<>23 Then Print "(C) Colour: "+colname(colour+1)
If typ=23 Then Print "(C) Colour: "+pcolname(colour+1)
SeekFile fil,(siz-88)+(4*9):growth=ReadByte(fil):SeekFile fil,FilePos(fil)-1
Print "(G) Growth Stage: "+stagename(growth+1)
SeekFile fil,(siz-88)+(4*10):watered=ReadByte(fil):SeekFile fil,FilePos(fil)-1
SeekFile fil,(siz-88)+(4*11):wateredtarget=ReadByte(fil):SeekFile fil,FilePos(fil)-1
Print "(W) Times Watered: "+watered+"/"+(wateredtarget+1)
;SeekFile fil,(siz-88)+(4*12):require=ReadByte(fil):SeekFile fil,FilePos(fil)-1
;If require=0 Then Print "(R) Next Growth Requirement: Fertiliser"
;If require=3 Then Print "(R) Next Growth Requirement: Bug Spray"
;If require=4 Then Print "(R) Next Growth Requirement: Gramophone"

FlushKeys
Print ""
i$=Upper(Input("Enter a letter or leave black to exit: "))
If i="" Then End




If i="P" Then
Cls:Locate 0,0
Print "SELECT A PLANT:":Print""
For t=1 To 52
If t=>26 Then Locate 200,28+((t-26)*12)
Print t+": "+typname(t)
Next
Print ""
i$=Input("Enter a number or leave black to cancel: ")
If i<>"" Then
ii=Int(i)
If ii<0 Or ii>52 Then Print "Invalid number!  Press a key to continue.":WaitKey
typ=ii
SeekFile fil,siz-88
WriteByte fil,typ
EndIf
EndIf


If i="L" Then
Cls:Locate 0,0
Print "SELECT A LOCATION:":Print""
Print ""
Print "(D) Daytime Garden"
Print "(M) Mushroom Garden"
Print "(W) Water Garden"
Print "(B) Wheelbarrow"
Print ""
i$=Upper(Input("Enter a letter or leave black to cancel: "))
If i<>"" Then
If i="D" Then garden=0
If i="M" Then garden=1
If i="W" Then garden=3
If i="B" Then garden=2
SeekFile fil,(siz-88)+(4*1)
WriteByte fil,garden
EndIf
EndIf


If i="S" Then
Cls:Locate 0,0
Print "SELECT A SLOT:":Print""
Print ""
iii=Int(Input("Enter a column (1-8): "))-1
If iii<0 Or iii>7 Then Print "Out of range!  Press a key to continue.":WaitKey:Goto restart Else column=iii
	If garden=0 Then
	iii=Int(Input("Enter a row (1-4): "))-1
	If iii<0 Or iii>3 Then Print "Out of range!  Press a key to continue.":WaitKey:Goto restart Else row=iii
	EndIf
SeekFile fil,(siz-88)+(4*2)
WriteByte fil,column
SeekFile fil,(siz-88)+(4*3)
WriteByte fil,row
EndIf

If i="F" Then
Direction=Direction+1:If Direction>1 Then Direction=0
SeekFile fil,(siz-88)+(4*4)
WriteByte fil,direction
EndIf


If i="C" Then
Cls:Locate 0,0
Print "SELECT A COLOUR:":Print""
Print ""
Print " NOTE: Colour only seems to affect Marigolds and"
Print " Tall-Nuts, except for the `Imitated' colour which"
Print " seems to work for everything."
Print ""
For t=1 To cols
If typ<>23 Then Print t+": "+colname(t)
If typ=23 Then Print t+": "+pcolname(t)
Next
Print ""
iii=Int(Input("Enter a colour code (1-?): "))-1
If iii<0 Or iii>255 Then Print "Out of range!  Press a key to continue.":WaitKey:Goto restart Else colour=iii
SeekFile fil,(siz-88)+(4*8)
WriteByte fil,colour
EndIf



If i="G" Then
Cls:Locate 0,0
Print "SELECT A GROWTH STAGE:":Print""
Print ""
For t=1 To stages
Print t+": "+stagename(t)
Next
Print ""
iii=Int(Input("Enter a number: "))-1
If iii<0 Or iii>3 Then Print "Out of range!  Press a key to continue.":WaitKey:Goto restart Else growth=iii
SeekFile fil,(siz-88)+(4*9)
WriteByte fil,growth
EndIf


If i="W" Then
Cls:Locate 0,0
Print "SELECT WATERING SETTINGS:":Print""
Print ""
iii=Int(Input("How many drinks does the plant require? (1-8): "))-1
If iii<0 Or iii>7 Then Print "Out of range!  Press a key to continue.":WaitKey:Goto restart Else wateredtarget=iii
iii=Int(Input("How many drinks has it had so far? (0-"+(wateredtarget+1)+"): "))
If iii<0 Or iii>(wateredtarget+1) Then Print "Out of range!  Press a key to continue.":WaitKey:Goto restart Else watered=iii
SeekFile fil,(siz-88)+(4*10)
WriteByte fil,watered
SeekFile fil,(siz-88)+(4*11)
WriteByte fil,wateredtarget
EndIf



Goto restart
CloseFile fil
Print ""
Print "Press a key to exit."
WaitKey
End
