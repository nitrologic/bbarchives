; ID: 1803
; Author: Ryudin
; Date: 2006-08-31 20:27:10
; Title: Car Salesman
; Description: A very easy game to modify, or fun to play.

AppTitle "Car Salesman","Quit?"
Graphics 1024,768

Global f = LoadFont("Times",16)
SetFont(f)

If FileType ("Data") <> -1 CreateDir("Data")

Const scarp = 19000
Const vanp = 9000
Const svanp = 11000
Const trckp = 10000
Const mvanp = 10000
Const rcarp = 39500
Const gcarp = 7500
Const offp = 125
Const tvp = 1000
Const adlvp = 200
Const cushp = 105
Const ocp = 100
Const spdp = 500

Global money = 10000
Global coxp = 0
Global car = 1
Global cd = 0
Global tape = 0
Global radio = 0
Global cushionlv = 1
Global steerlv = 1
Global speedlv = 1
Global aerolv = 1
Global mileagelv = 1
Global tv = 0
Global ssound = 0
Global officelv = 1
Global bc = 0
Global adlv = 0
Global colv = 1 ;just for future reference - company level
SeedRnd MilliSecs() ;this seems like a good place to put it 
Global Name$ = Input$("What is your name? ")

If FileType("Data\" + Name$ + ".car") <> 1 Then 
	w = WriteFile("Data\" + Name$ + ".car")
		WriteFloat(w,money)
		WriteByte(w,colv)
		WriteFloat(w,coxp)
		WriteByte(w,car)
		WriteByte(w,cd)
		WriteByte(w,tape)
		WriteByte(w,radio)
		WriteByte(w,cushionlv)
		WriteByte(w,steerlv)
		WriteByte(w,speedlv)
		WriteByte(w,aerolv)
		WriteByte(w,mileagelv)
		WriteByte(w,tv)
		WriteByte(w,ssound)
		WriteByte(w,officelv)
		WriteByte(w,bc)
		WriteByte(w,adlv)
	CloseFile(w)
	Goto ctyp
EndIf

r = ReadFile("Data\" + Name$ + ".car")
	money = ReadFloat(r)
	colv = ReadByte(r)
	coxp = ReadFloat(r)
	car = ReadByte(r)
	cd = ReadByte(r)
	tape = ReadByte(r)
	radio = ReadByte(r)
	cushionlv = ReadByte(r)
	steerlv = ReadByte(r)
	speedlv = ReadByte(r)
	aerolv = ReadByte(r)
	mileagelv = ReadByte(r)
	tv = ReadByte(r)
	ssound = ReadByte(r)
	officelv = ReadByte(r)
	bc = ReadByte(r)
	adlv = ReadByte(r)
CloseFile(r)

;locations (loc)
;1 = main
;2 = car type select
;3 = levels up
;4 = car features
;5 = customer convince
;7 = advertising select

.startingpoint

.main
Flip
Cls
Locate 0,0
Print "Welcome to the main screen of Car Salesman, "+Name$+"."
Print "Press '1' to make your car and office better by level. Press '2' to access car features."
Print "Press '3' to see if there is a customer waiting, or '4' to make your advertising better."
Print "Press '5' to stop selling and start a new car, destroying the old brand for a newer one."
Print "Your money - $" + money
w = WriteFile("Data\" + Name$ + ".car")
	WriteFloat(w,money)
	WriteByte(w,colv)
	WriteFloat(w,coxp)
	WriteByte(w,car)
	WriteByte(w,cd)
	WriteByte(w,tape)
	WriteByte(w,radio)
	WriteByte(w,cushionlv)
	WriteByte(w,steerlv)
	WriteByte(w,speedlv)
	WriteByte(w,aerolv)
	WriteByte(w,mileagelv)
	WriteByte(w,tv)
	WriteByte(w,ssound)
	WriteByte(w,officelv)
	WriteByte(w,bc)
	WriteByte(w,adlv)
CloseFile(w)
colv = (coxp / 1000) + 1
Repeat
	If KeyHit(2) Goto lvup
	If KeyHit(3) Goto cfea
	If KeyHit(4) Goto cuco
	If KeyHit(5) Goto advr
	If KeyHit(6) Goto boom
Forever
End
.ctyp
Print "Press the number corresponding to the car type you want (price is listed)."
If colv > 0 Print "1 - Granny Car - $9,500"
If colv > 2 Print "2 - Van - $11,000"
If colv > 3 Print "3 - Truck - $13,000"
If colv > 10 Print "4 - Minivan - $12,000"
If colv > 20 Print "5 - Sports Van - $12,500"
If colv > 40 Print "6 - Sports Car - $21,000"
If colv > 60 Print "7 - Racecar - $41,000"
If colv > 100 Print "8 - Airplane - $100,000"
If money < 8500 RuntimeError "You lose, dude!"
Repeat
	If colv > 0 Then
		If KeyHit(2) Then If money >= 8500 money = money - 9500 car = 1 cars = cars + 1 Goto main
	EndIf
	If colv > 2 Then
		If KeyHit(3) Then If money >= 11000 money = money - 11000 car = 2 cars = cars + 1 Goto main
	EndIf
	If colv > 3 Then
		If KeyHit(4) Then If money >= 13000 money = money - 13000 car = 3 cars = cars + 1 Goto main
	EndIf
	If colv > 10 Then
		If KeyHit(5) Then If money >= 12000 money = money - 12000 car = 4 cars = cars + 1 Goto main
	EndIf
	If colv > 20
		If KeyHit(6) Then If money >= 12500 money = money - 12500 car = 5 cars = cars + 1 Goto main
	EndIf
	If colv > 40 Then
		If KeyHit(7) Then If money >= 21000 money = money - 21000 car = 6 cars = cars + 1 Goto main
	EndIf
	If colv > 60 Then
		If KeyHit(8) Then If money >= 41000 money = money - 41000 car = 7 cars = cars + 1 Goto main
	EndIf
	If colv > 100 Then
		If KeyHit(8) Then If money >= 100000 money = money - 100000 car = 8 cars = cars + 1 Goto main
	EndIf	
Forever

End
.lvup
Print "Here is where you can make your car or office go up levels to help its value. Press"
Print "the corresponding key to raise the level if you have enough money. All prices are"
Print "multiplied by the current level."
Print "1 - Speed - $250 - Current level: " + speedlv
Print "2 - Aerodynamics - $75 - Current level: " + aerolv
Print "3 - Cushions - $100 - Current level: " + cushionlv
Print "4 - Handling - $75 - Current level: " + steerlv
Print "5 - Mileage - $80 - Current level: " + mileagelv
Print "6 - Office - $100 - Current level: " + offlv
Print "7 - Go back to main page"

Repeat	
	If KeyHit(2) And money >= 250*speedlv money=money-250*speedlv speedlv = speedlv + 1 Goto main
	If KeyHit(3) And money >= 75*aerolv money=money-75*aerolv aerolv = aerolv + 1 Goto main
	If KeyHit(4) And money >= 100*cushionlv money=money-100*cushionlv cushionlv = cushionlv+1 Goto main
	If KeyHit(5) And money >= 75*steerlv money=money-75*steerlv steerlv = steerlv + 1 Goto main
	If KeyHit(6) And money >= 80*mileagelv money=money-80*mileagelv mileagelv = mileagelv+1 Goto main
	If KeyHit(7) And money >= 100*offlv money=money-100*offlv offlv = offlv + 1 Goto main

	If KeyHit(8) Goto main
Forever
End
.cfea
Print "You can give your car new features here, such as a TV or a CD Player. Press the key:"
Print "1 - TV - $500"
Print "2 - CD player - $50"
Print "3 - Tape deck - $25"
Print "4 - Radio - $50"
Print "5 - Surround Sound - $125"
Print "6 - Go back to the main page"
Repeat
	If KeyHit(2) And money >= 500 money = money - 500 tv = 1 Goto main
	If KeyHit(3) And money >= 50 money = money - 50 cd = 1 Goto main
	If KeyHit(4) And money >= 25 money = money - 25 tape = 1 Goto main
	If KeyHit(5) And money >= 50 money = money - 50 radio = 1 Goto main
	If KeyHit(6) And money >= 125 money = money - 125 ssound = 1 Goto main
	If KeyHit(7) Goto main
Forever
End
.cuco
Print "This customer wants to know all about the car you are selling. You must tell"
Print "them everything. As you do, they stare in awe. You can bet the price will be"
Select car
	Case 1 valu = gcarp
	Case 2 valu = vanp
	Case 3 valu = trckp
	Case 4 valu = mvanp
	Case 5 valu = svanp
	Case 6 valu = scarp
	Case 7 valu = rcarp
	Case 8 valu = 80000
End Select
If tv = 1 valu = valu + tvp
If cd = 1 valu = valu + ocp
If radio = 1 valu = valu + ocp
If tape = 1 valu = valu + ocp
valu = valu + offp * offlv
valu = valu + spdp * speedlv
valu = valu + cushp * cushionlv
valu = valu + ocp * aerolv 
valu = valu + mileagelv * ocp
valu = valu + adlvp * adlv
valu = valu + ocp * steerlv
If ssound = 1 valu = valu + ocp 
valu = valu + Rand(-1000,1200)
Print "good. Their offer is $"+valu+"." 
Print"Haggle up with '1' or take the offer with '2'. Dump the customer with '3'"
Repeat
	If KeyHit(2) Goto haggle
	If KeyHit(3) Goto take
	If KeyHit(4) Goto main
Forever
.haggle
rn = Rand(1,2)
If rn=1 valu=valu+Rand(100,5000) Print"You were successful in your haggle. The price is now $"+valu
If rn>1 valu=valu-Rand(0,1000) Print"They don't like haggling. The price is now $"+valu
Goto q1
.q1
Print"Take with '1' or dump with '2'."
Repeat
	If KeyHit(2) Goto take
	If KeyHit(3) Goto main
Forever
.take
money = money + valu
loc = 1
Print "You decide to take the offer. Your money is now $" + money + "."
coxp = coxp + valu / 11
Goto boom
End
.advr
Print "Your advertising priority level is " + adlv + ". Press '1' to raise it a level for $125 times"
Print "your current level. Press '2' to go to the main page."
Repeat
	If KeyHit(2) And money >= adlv * 125 money = money - adlv * 125 adlv = adlv + 1 Goto main
	If KeyHit(3) Goto main
Forever 
End
.boom
tv = 0
tape = 0
radio = 0
cd = 0
steerlv = 1
cushionlv = 1
speedlv = 1
aerolv = 1
mileagelv = 1
ssound = 0
WaitKey
Goto ctyp
End
