; ID: 6
; Author: BlitzSupport
; Date: 2001-09-13 20:58:57
; Title: Integer swapping
; Description: Swapping two integer variables without using a third

a = 1
b = 2

Print a
Print b

Print

a=a Xor b
b=a Xor b
a=a Xor b

Print a
Print b

; And another version...

Print

a = 1
b = 2

Print a
Print b
Print

a = a + b
b = a - b
a = a - b

Print a
Print b

