; ID: 3071
; Author: Pineapple
; Date: 2013-08-27 17:56:15
; Title: Deck of playing cards
; Description: Deck and card classes

' 	--+-----------------------------------------------------------------------------------------+--
'	  |   This code was originally written by Sophie Kirschner (sophiek@pineapplemachine.com)   |  
' 	  | It is released as public domain. Please don't interpret that as liberty to claim credit |  
' 	  |   that isn't yours, or to sell this code when it could otherwise be obtained for free   |  
'	  |                because that would be a really shitty thing of you to do.                |
' 	--+-----------------------------------------------------------------------------------------+--


SuperStrict

Import brl.random



' Example code

Rem

SeedRnd MilliSecs()

Local d:deck=deck.Create(1)

Print "The first 5 cards on the deck are:"

For Local i%=1 To 5
	Print d.pop().tostring()
Next

EndRem



Const SUIT_SPADES%=0
Const SUIT_HEARTS%=1
Const SUIT_CLUBS%=2
Const SUIT_DIAMONDS%=3
Const NUM_ACE%=1
Const NUM_JACK%=11
Const NUM_QUEEN%=12
Const NUM_KING%=13
Const NUM_JOKER%=14

Type deck
	Field cards:TList=CreateList()
	Field count%=0
	Function Create:deck(shuffle%=False)
		Local n:deck=New deck
		n.init
		If shuffle n.shuffle()
		Return n
	End Function
	Method init()
		For Local suit%=0 Until 4
			For Local num%=NUM_ACE To NUM_KING
				Local c:card=New card
				c.suit=suit
				c.num=num
				cards.addlast c
			Next
		Next
		count=52
	End Method
	Method shuffle() ' fewer operations, results in a pretty well-randomized deck.
		Local nlist:TList=CreateList()
		For Local i%=0 Until count
			Local j%=Rand(0,count-i-1)
			Local link:TLink
			If Rand(0,1)
				link=cards._head._succ
				While j
					link=link._succ
					j:-1
				Wend
			Else
				link=cards._head._pred
				While j
					link=link._pred
					j:-1
				Wend
			EndIf
			nlist.addlast link._value
			link.remove
		Next
		cards=nlist
	End Method
	Method shuffle_2(iterations%=60) ' more operations, but shuffles like a human might.
		?debug
		Local icount%=count
		?
		For Local i%=0 Until iterations
			Local link:TLink
			Local slink:TLink,elink:TLink
			Local index%=Rand(1,count-1)
			Local move%=Rand(0,count-index-1)
			Local dir%=Rand(0,1)
			If dir
				link=cards._head._succ
				Local j%=0
				While j<index
					link=link._succ
					j:+1
				Wend
				slink=link
				j=0
				While j<move
					link=link._succ
					j:+1
				Wend
				elink=link
				slink._pred._succ=elink._succ
				elink._succ._pred=slink._pred
				elink._succ=cards._head
				slink._pred=cards._head._pred
				cards._head._pred._succ=slink
				cards._head._pred=elink
			Else
				link=cards._head._pred
				Local j%=0
				While j<index
					link=link._pred
					j:+1
				Wend
				elink=link
				j=0
				While j<move
					link=link._pred
					j:+1
				Wend
				slink=link
				slink._pred._succ=elink._succ
				elink._succ._pred=slink._pred
				slink._pred=cards._head
				elink._succ=cards._head._succ
				cards._head._succ._pred=elink
				cards._head._succ=slink
			EndIf
			?debug
			Assert cards.count()=icount,"Shuffling the deck fucked up a list link somewhere."
			?
		Next
	End Method
	Method tostring$()
		Local str$=""
		For Local c:card=EachIn cards
			str:+c.tostring()+" "
		Next
		Return str
	End Method
	Method pop:card() ' draw a card from the top
		If count=0 Return Null
		count:-1
		Return card(cards.removefirst())
	End Method
	Method popbottom:card() ' draw a card from the bottom
		If count=0 Return Null
		count:-1
		Return card(cards.removelast())
	End Method
	Method push:TLink(c:card) ' put the card on top
		count:+1
		Return cards.addfirst(c)
	End Method
	Method pushbottom:TLink(c:card) ' put the card on bottom
		count:+1
		Return cards.addlast(c)
	End Method
End Type

Type card
	Field suit%
	Field num%
	Global suitname$[]=["spades","hearts","clubs","diamonds"]
	Global suitchar$[]=["S","H","C","D"]
	Global numname$[]=["error","ace","two","three","four","five","six","seven","eight","nine","ten","jack","queen","king","joker"]
	Global numplural$[]=["errors","aces","twos","threes","fours","fives","sixes","sevens","eights","nines","tens","jacks","queens","kings","jokers"]
	Global numchar$[]=["?","A","2","3","4","5","6","7","8","9","10","J","Q","K","@"]
	Method tostring$()
		Return numchar[num]+suitchar[suit]
	End Method
End Type
