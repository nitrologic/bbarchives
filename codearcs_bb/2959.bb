; ID: 2959
; Author: mark1110
; Date: 2012-07-09 23:46:15
; Title: mindreader
; Description: guesses a number

Graphics 600,400,16,2
.st0
cd1=0
cd2=0
cd4=0
cd8=0
cd16=0
cd32=0
Print "Think of a number between 1 and 60 - press any key to continue"
WaitKey ()
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
.st1
Cls
Locate 0,0
Print " 1 11 21 31 41 51"
Print " 3 13 23 33 43 53"
Print " 5 15 25 35 45 55"
Print " 7 17 27 37 47 57"
Print " 9 19 29 39 49 59"
one$ = Input$ ("do you see your number here? y/n : ")
If Not one$ = "y" Or one$ = "n" Then Goto st1
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
.st2
Cls
Locate 0,0
Print " 2 11 22 31 42 51"
Print " 3 14 23 34 43 54"
Print " 6 15 26 35 46 55"
Print " 7 18 27 38 47 58"
Print "10 19 30 39 50 59"
two$ = Input$ ("do you see your number here? y/n : ")
If Not two$ = "y" Or two$ = "n" Then Goto st2
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
.st4
Cls 
Locate 0,0
Print " 4 13 22 31 44 53"
Print " 5 14 23 36 45 54"
Print " 6 15 28 37 46 55"
Print " 7 20 29 38 47 60"
Print "12 21 30 39 52 **"
four$ = Input$ ("do you see your number here? y/n : ")
If Not four$ = "y" Or four$ = "n" Then Goto st4
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
.st8
Cls
Locate 0,0
Print " 8 13 26 31 44 57"
Print " 9 14 27 40 45 58"
Print "10 15 28 41 46 59"
Print "11 24 29 42 47 60"
Print "12 25 30 43 56 **"
ate$ = Input$ ("do you see your number here? y/n : ")
If Not ate$ = "y" Or ate$ = "n" Then Goto st8
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
.st16
Cls
Locate 0,0
Print "16 21 26 31 52 57"
Print "17 22 27 48 53 58"
Print "18 23 28 49 54 59"
Print "19 24 29 50 55 60"
Print "20 25 30 51 56 **"
teen$ = Input$ ("do you see your number here? y/n : ")
If Not teen$ = "y" Or teen$ = "n" Then Goto st16
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
.st32
Cls
Locate 0,0
Print "32 37 42 47 52 57"
Print "33 38 43 48 53 58"
Print "34 39 44 49 54 59"
Print "35 40 45 50 55 60"
Print "36 41 46 51 56 **"
thr$ = Input$ ("do you see your number here? y/n : ")
If Not thr$ = "y" Or thr$ = "n" Then Goto st32
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
If one$ = "y" Then cd1 = 1
If two$ = "y" Then cd2 = 2
If four$ = "y" Then cd4 = 4
If ate$ = "y" Then cd8 = 8
If teen$ = "y" Then cd16 = 16
If thr$ = "y" Then cd32 = 32
total = cd1 + cd2 + cd4 + cd8 + cd16 + cd32
Cls
Locate 0,0
Print "Your number is " + total
WaitKey ()
Goto st0
End
