; ID: 3285
; Author: cps
; Date: 2016-08-26 18:26:56
; Title: Shuffle
; Description: A simple card shuffler

'An easy to follow card shuffler ?  Have fun Cps.
'for 10 cards indexed 0-9
'Takes index 0 and swaps contents with a randomly choosen index number between 1-9
'Takes index 1 and swaps contents with a randomly choosen index number between 2-9
'Takes index 2 and swaps contents with a randomly choosen index number between 3-9 etc
'Stops after index 7 contents swaped with a randomly choosen index number between 8-9.

SuperStrict
Import MaxGui.Drivers


Local Deck:AllCards= New AllCards' generates the cards and methods

Deck.Initilise()' create deck
Deck.Show()' show ordered deck
deck.Shuffle()' shuffle deck
Deck.Show()' show shuffled deck
Print "---------------  And Again  -------------------"
Deck.SetValues()'reset deck
Deck.Show()' show ordered deck
Deck.Shuffle()' shuffle deck
Deck.Show()' show shuffled deck

End


Type AllCards Extends ACard' the full deck
	Field Card:ACard[10]'10 cards in pack
 
	Method Initilise()
		Local Tb1:Byte
		For Tb1=0 To 9 Card[Tb1]=New Acard Next' create the cards (index 1 les than total number of cards)
		SeedRnd MilliSecs()	'seed the random number generator		
		SetValues()
	End Method

	Method SetValues()' card(0 to 4) = black cards values 1 to 5,  card(5 to 9) = red cards values 1 to 5
		Local Tb1:Byte' sets up the ordered deck
		For Tb1=0 To 4
			SetVal(Tb1,Tb1+1); SetVal(Tb1+5,Tb1+1)
			SetCol(Tb1,0); SetCol(Tb1+5,1)
		Next
	End Method
	
	Method Shuffle()'shuffles the deck
		Local Tb1:Byte; Local Nt1:Byte; Local Nt2:Byte
		Local VSwap:Byte' temp stroe for value swap
	 	Local CSwap:Byte'temp store for colur swap
		
		For Tb1=0 To 7' 2 less than max card index
			Nt1=Tb1' card index numbers from top to bottom
			Nt2=RandomNum((Tb1+1),9)' a random card index number from the remaining cards
			VSwap=GetVal(Nt1); CSwap=GetCol(Nt1) 
			SetVal(Nt1,GetVal(Nt2)); SetCol(Nt1,GetCol(Nt2))
			SetVal(Nt2,VSwap); SetCol(Nt2,CSwap)
		Next
	End Method
	
	Method RandomNum:Byte(T1:Byte,T2:Byte)'returns a random number between T1 and T2
		Local Tb1:Byte
		Tb1=Rand(T1,T2)
		Return Tb1
	End Method

	Method Show()'prints cards by card number
		Local Tb1:Byte; Local Ts1$
		For Tb1=0 To 9
			Ts1="Card Number "+String(Tb1)+" : Value = "+String(GetVal(Tb1))+" : Colour = "
			If GetCol(Tb1)=0 Then Ts1=Ts1+"Black" Else Ts1=Ts1+"Red"
			Print Ts1
		Next
		Print "-----------------------------------------"
	End Method
	
	Method SetVal(T1:Byte,T2:Byte)' T1=card number,T2=value
		Card[T1].Val=T2	
	End Method

	Method GetVal:Byte(T1:Byte)' T1=card number, returns card value
		Local Tb1:Byte=Card[T1].Val; Return Tb1	
	End Method
	
	Method SetCol(T1:Byte,T2:Byte)' T1=card number,T2=Colour 0=black, 1=red
		Card[T1].Col=T2	
	End Method
	
	Method GetCol:Byte(T1:Byte)' T1=card number, returns card colour 0=black, 1=red
		Local Tb1:Byte=Card[T1].Col; Return Tb1	
	End Method
End Type


Type ACard'a card
	Field Val:Byte'1 to 5 (the cards value)
	Field Col:Byte'0=black, 1=red (the cards colour)
End Type
