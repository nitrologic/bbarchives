; ID: 1382
; Author: Zach3D
; Date: 2005-05-23 15:19:33
; Title: Substring$(string1$,x,y)
; Description: Substring function

;Substring Function(gets the string located at A,B of string)
Function Substring$(s1$,a,b)
length = b - a
J$ = Mid$(s1$,a,length + 1)
Return J$
End Function

trigstring$ = "Hello my name is Zach"
Print SubString$(trigstring$,3,8)
WaitKey()

;This prints "llo my"
