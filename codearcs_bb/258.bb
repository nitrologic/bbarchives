; ID: 258
; Author: Giano
; Date: 2002-03-03 14:54:35
; Title: Pecentage values
; Description: Statistics functions and controlled randoms

;******************************************************
;*** This function returns a random number
;*** with percentage parameter
;*** percRnd returns a float value
;*** percRand returns an integer value
;***
;*** Example.1 : percRnd(25) 
;***	 Return 1 with 25% of probability
;***     Return 0 with 75% (100-25) of probability
;***
;*** Example.2 : percRnd(25,1000) 
;***	 Return 1 with 2.5% of probability
;***     Return 0 with 97.5% (1000-25) of probability
;******************************************************
Function percRnd#(random#, max#=100)
	Return random=>Rnd(max)
End Function

Function percRand(random, max=100)
	Return random=>Rand(max)
End Function
;******************************************************
;*** Return the percentual of value to max
;*** Example.1: percent(25,100) = 25.000
;*** Example.2: percent(25,1000) = 2.500
;******************************************************
Function percent#(value#,max#)
	Return value* 100.0 / max#
End Function

;******************************************************
;*** This function returns a random number
;*** of a percentage parameter from 0 to 1
;***
;*** Example.1 : percFuzzy(25) 
;***	 0 > Return >=1 with 25% of probability
;***     Return 0 with 75% (100-25) of probability
;******************************************************
Function fuzzyRnd#(random#, max#=100)
	r# = Rnd(max)
	If random=>r
		Return Float(r/random)
	End If
	Return 0
End Function
;******************************************************

Print "tests (eg.1000, bigger = better precision)"
test# = Input(">")
Print "percent (eg.10, max = 100)"
perc# = Input(">")

Print 
SeedRnd MilliSecs() 
tot = 0
fuz = 0
;*** perfoms a lot of tests
For t=1 To test
	If percRnd(perc) tot=tot+1
	If fuzzyRnd(perc)<>0 fuz=fuz+1
Next
;*** report statistics
Print
Print "Test     : " 
Print tot  + "/" + test + "=" + Float(test/tot) +" "+ percent(tot,test) + "%"
Print "Attended : " 
Print percent(perc,100) + "%"
Print "Error: " 
Print Abs(percent(tot,test) - perc)
Print
Print "Fuzzies  : " 
Print fuz  + "/" + test + "=" + Float(test/fuz) +" "+ percent(fuz,test) + "%"
Print "Attended : " 
Print percent(perc,100) + "%"
Print "Error: " 
Print Abs(percent(fuz,test) - perc)
Print
Print "percRnd(25) = " + percRnd(25)
Print "percRnd(25,1000) = " + percRnd(25,1000)
Print "percent(25,100) = " +percent(25,100)
Print "percent(25,1000) = " + percent(25,1000)
Print
