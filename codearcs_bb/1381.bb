; ID: 1381
; Author: Snarkbait
; Date: 2005-05-22 19:22:21
; Title: cards.dll userlib
; Description: Make playing card games easily with cards.dll

;decls
; make sure 'user32.decls' has the following line:
; user32_GetDC%(hwnd%):"GetDC"

; "cards.decls"
;.lib "cards.dll"
;
;cdtInit%( pdxCard*, pdyCard* ):"cdtInit"
;cdtTerm%( ):"cdtTerm"
;cdtDraw%( hdc%, cardx%, cardy%, cdraw%, modedraw%, rgbBgrnd% ):"cdtDraw"

;"blitzcards.decls"
;.lib " "
;
;Init_CardGame%( )
;End_CardGame%( )
;MakeDeck%( )
;LoadNames%( )
;GetCardValue%( FaceCard%, Suit% )
;GetCardFace%( Value% )
;GetSuit%( Value% )
;GetCardString$( Value% )
;DrawCard% ( hdc% , cardx% , cardy% , crddraw% , modedraw% , rgbBgrnd% )
;ShuffleCards%( max_rand% )
;QuickSort%( L%, R% )
;GetHDC%( GUIobjHandle% )
;; for b3d use GetHDC%( )

; include file "cards.bb"
;==================================Comments========================================================
; include file for card.dll
; by Snarkbait snarkbait66@gmail.com
; cards.decls AND blitzcards.decls must be in your userlib folder
; cards.dll should be in your system folder, in some OS versions it might be called cards32.dll just change the name in the userlib decls



;==================================Consts==========================================================

; for 'modedraw' parameter in DrawCard()
Const FACE_UP = 0 ; draws the card specified in crddraw
Const FACE_DOWN = 1 ; draws the card back specified in crddraw
Const HI_LITE = 2 ; draws a specified card using inverted colors
Const ACE_PILE = 3 ;  draws a dotted pile indicator in bgColor
Const REMOVE = 4 ;  draws an "empty" card in bgColor - use to remove card from screen
Const DOTTED_LAYER = 5 ; draws a transparent dotted layer on the card
Const RED_X = 6 ; draws the green card with the red X
Const CIRCLE = 7  ; draws the green card with the circle

;====================

Const Clubs = 0
Const Diamonds = 1
Const Hearts = 2
Const Spades = 3

Const Ace = 1 
Const Jack = 11
Const Queen = 12
Const King = 13

; use these constants with 'GetCardValue' like:
; DrawCard( hdc, cardx, cardy, GetCardValue( Ace, Spades), FACE_UP)



;==================================Arrays==========================================================

Dim CardDeck(51) ; use this as deck in your program
Dim ShuffleStack(51) ; used only for shuffling
Dim FaceName$(12)
Dim SuitName$(3)


;==================================Globals==========================================================

Global cardYsize ; will be 96
Global cardXSize ; will be 71




;==================================Functions========================================================

Function Init_CardGame()
	bankx = CreateBank(4)
	banky = CreateBank(4)
	PokeInt bankx,0,1
	PokeInt banky,0,1	
	success = cdtInit(bankx,banky)		
	If success
		cardXsize = PeekInt(bankx,0)
		cardYsize = PeekInt(banky,0)
	Else
		RuntimeError "Cards32.dll Error"
	EndIf
	FreeBank bankx
	FreeBank banky
	MakeDeck()
	LoadNames()
End Function

Function End_CardGame()
	success = cdtTerm()
	Return success
End Function 

Function MakeDeck()
	For a = 0 To 51
		CardDeck(a) = a
	Next
End Function 

Function GetCardValue( FaceCard, Suit)
	Return ((FaceCard - 1) Shl 2) + Suit
End Function 

Function GetCardFace( value )
	Return (value Shr 2) + 1
End Function

Function GetSuit( value )
	Return value And 3
End Function

Function GetCardString$( value )
	face = GetCardFace( value )
	suit = GetSuit( value)
	Return FaceName$(face - 1) + " of " + SuitName$(Suit)
End Function 

Function LoadNames()
	Restore Facenames
	For a = 0 To 12
		Read FaceName$(a)
	Next
	Restore Suitnames
	For a = 0 To 3
		Read SuitName$(a)
	Next
End Function 	

Function DrawCard( hdc, cardx = 0, cardy = 0, crddraw = 0, modedraw = 0, rgbBgrnd = 0)
	If hdc
		success = cdtDraw( hdc, cardx, cardy, crddraw, modedraw, rgbBgrnd)
		Return success
	Else
		Return False
	EndIf
End Function 

Function ShuffleCards(max_rand = $FFFFFFFF)
	For a = 0 To 51
		ShuffleStack(a) = Rand(max_rand)
	Next
	a = QuickSort()
End Function 

Function QuickSort( low = 0,high = 51) ; by TFT modified by me
  Local partition,q,h
  partition=low
  q=high
  x= ShuffleStack((low +high)/2)
  Repeat
    While ShuffleStack(partition) < x
      partition=partition+1
    Wend
    While x < ShuffleStack(q)
      q=q-1
    Wend
    If partition > q Then Exit
	;SWAP------------------
	h=ShuffleStack(q)
	h2 = CardDeck(q)
	ShuffleStack(q)=ShuffleStack(partition)
	CardDeck(q) = CardDeck(partition)
	ShuffleStack(partition)=h
	cardDeck(partition) = h2
	;----------------------
    partition=partition + 1
    q=q-1
    If q<0 Then Exit
  Forever 
  If low<q Then a=QuickSort(low,q)
  If partition < high Then a=QuickSort(partition,high)
  Return True
End Function

; for Blitz plus only, comment out for b3d
Function GetHDC%( GUIobjHandle)
	If GUIobjHandle
		rethdc = user32_GetDC(QueryObject(GUIobjHandle,1))
			If rethdc 
				Return rethdc
			Else
				Return False
			EndIf
	Else
		Return False
	EndIf
End Function 

; for Blitz3d, comment out for blitz plus - wonky, needs work.
;Function GetHDC%()
;	rethdc = user32_GetDC(systemproperty$("AppHWND"))
;	If rethdc 
;		Return rethdc
;	Else
;		Return False
;	EndIf
;End Function 
			
;==================================Data Statements/Labels=============================================

.Facenames
Data "Ace","Two","Three","Four","Five","Six","Seven","Eight","Nine","Ten","Jack","Queen","King"

.Suitnames
Data "Clubs","Diamonds","Hearts","Spades"
