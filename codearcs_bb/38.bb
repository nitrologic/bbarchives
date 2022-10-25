; ID: 38
; Author: Russell
; Date: 2001-09-02 20:52:21
; Title: Incr()/Decr() with safety check
; Description: Increment or decrement by a value with bounds check

Function Incr(iValue,iIncAmount,iMax)
   If iValue + iIncAmount > iMax Then Return iMax
   Return iValue + iIncAmount
End Function

Function Decr(iValue,iDecAmount,iMin)
   If iValue - iDecAmount < iMin Then Return iMin
   Return iValue - iDecAmount
End Function

x = 1
For z = 1 to 20
Print Incr(x,1,10) ; Will not print higher than 10
Next

x = 10
For p = 20 to 1
Print Decr(x,3,0) ; Will not print lower than 0
Next

;Excellent for nice and neat bounds checking! 
