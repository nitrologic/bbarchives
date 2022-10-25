; ID: 498
; Author: Techlord
; Date: 2002-11-20 11:19:43
; Title: Simple Transition Algorithm
; Description: Nifty algo that can be used to increment gradually from one value to another.

;transition algo 
from_n=from_n+Sgn(to_n-from_n);version 1
from_n#=from_n#+(Sgn(to_n#-from_n#)*inc_value#);verson 2

;version 1 demo
from_n=1000
to_n=100
Repeat
	from_n=from_n+Sgn(to_n-from_n)
	Print from_n
Until from_n=to_n
WaitKey
End

;version 2 demo
from_n#=1000
to_n#=100
inc_value#=5.

Repeat
	from_n#=from_n#+(Sgn(to_n#-from_n#)*inc_value#)
	Print from_n#
Until from_n#=to_n#

WaitKey
End
