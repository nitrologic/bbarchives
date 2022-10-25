; ID: 1295
; Author: aab
; Date: 2005-02-18 15:47:25
; Title: Confine Cursor
; Description: crappy but functional

Function ConfineCursor(x%,y%,w%,h%)
	Local r%=(CreateBank(16))
		PokeInt(r,0,x%):PokeInt(r,4,y%):PokeInt(r,8,x%+w%):PokeInt(r,12,y%+h%)	
		api_ClipCursor%(r)
		FreeBank(r)
	Return(True)
End Function
;this assumes that a long is the size of an int. i cant take responsibility for that.


;the bank is acting as a LPRECT/RECT*. ie Long Left\Top\Right\Bottom. 
;	PokeInt(r,0,x%):PokeInt(r,4,y%):PokeInt(r,8,x%+w%):PokeInt(r,12,y%+h%)

;knowing this commands such as api_IntersectRect and other Rectanle commands can be addressed this way
;or through a type eg [ Type Rect_ Field L%,T%,R%,B% End Type ].
