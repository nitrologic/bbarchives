; ID: 602
; Author: Daz
; Date: 2003-02-25 07:22:42
; Title: Command line switches
; Description: Shows to write a custom command line switch routine

;Function which examines the command line allowing 
;the use of switches to pass parameters into the 
;program in a similar way DOS programs
;use parameters

;To test this program
;Select the Program -> Program Command Line menu option
;Type something into the text box such as
;-f filename.txt /u username -p password -d -z


Dim p$(256)			;Global variable used to hold all the parameters passed in from the command line

;Define a list of all the valid command line switches
Global valid_options$ = "abcdefghijklmnopqrstuvwxyABCDEFGHIJKLMNOPQRSTUVWXYZ" 

;Define the type structure used to store the processed switches and parameters
Type clopt
	Field switch$
	Field valid_char%
	Field param$
End Type

;Generic Parameter handler function
Function HandleParameters%()	
	;Get the command line
	cl$ = CommandLine()
	If Len(Trim(cl$)) = 0 Then
		Return -1
	End If
	
	;Get all the parameters into an array for easier processing
	pn = 1
	For counter = 1 To Len(cl$)
		c$ = Mid$(cl$, counter, 1)
		If c$ = " " Then
			pn = pn + 1
		Else
			p$(pn) = p$(pn) + c$
		End If
	Next
	
	;Work out the parameters
	For counter = 1 To pn
		;If the switch starts with a - or a /
		param$ = ""
		option$ = ""
		valid = 0
		If Left(p$(counter), 1) = "-" Or Left(p$(counter), 1) = "/" Then
			;Make sure the swtich character is in the list of valid switches
			option$ = Mid(p$(counter), 2, 1)
			If Instr(valid_options$, option$, 1) <> 0 Then								
				;Check to see whether or not there is a parameter for the selected switch
				If Left(p$(counter+1), 1) <> "-" And Left(p$(counter+1), 1) <> "/" Then
					param$ = p$(counter+1)
					counter = counter + 1
				Else
					param$ = ""
				End If
				valid = 1
			Else
				
				valid = 0
			End If

			;Add the switch and parameter data to a new entry in the options structure
			o.clopt = New clopt
			o\switch$ = option$
			o\valid_char% = valid
			o\param$ = param$
		End If
	Next
	
	Return 0
End Function


;---------------------------
; Example of how to use the function

;Call the function
If HandleParameters() = -1 Then
	Print "No parameters found on the command line"
	Print ""
Else
	Print ""
	q_found = 0

	;Loop through each switch found on the command line
	For o.clopt = Each clopt
		If o\valid_char% = 0 Then
			Print o\switch$ + " is an invalid switch"
		Else
			Select o\switch$		
				Case "f"
					Print "f switch found with a value of " + o\param$
				Case "u"
					Print "u switch found with a value of " + o\param$
				Case "p"
					Print "p swtich found with a value of " + o\param$
					password$ = o\param$
				Case "P"
					Print "P swtich found with a value of " + o\param$
				Case "q"
					q_found = 1
				Case "d"
					Print "d swtich found with a value of " + o\param$
			End Select
		End If
	Next

	Print ""
	Print ""

	If q_found  = 0 Then
		Print "The q switch is required"
	End If
End If
