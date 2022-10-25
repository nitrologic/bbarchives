; ID: 8
; Author: BlitzSupport
; Date: 2001-08-29 18:04:23
; Title: Using types-within-types
; Description: An example of using a custom type within another custom type

Type player
	Field mov.animation
	Field die.animation
End Type

Type animation
	Field angle
	Field img
End Type

Dim something.player (24)			; Array of type .player

something.player (1) = New player	; New player object in array
something (1)\mov = New animation	; New animation object within player object!
something (1)\die = New animation	; And again...

something (1)\mov\angle = 45		 ; Referencing objects within objects
something (1)\die\img = 3		    ; And again...

Print something (1)\mov\angle		; Result
Print something (1)\die\img		  ; Ditto here :)
