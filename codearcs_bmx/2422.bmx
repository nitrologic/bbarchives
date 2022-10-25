; ID: 2422
; Author: xlsior
; Date: 2009-02-23 06:04:21
; Title: Roman Numerals
; Description: Validate and convert between Roman Numerals and Integers

' ROMAN NUMERAL FUNCTIONS
' 
' By Marc van den Dikkenberg / www.xlsior.org
' Created in February 2009
'
'   This code will allow you to:
' - Validate a Roman Numeral (Check a string and state whether or not it contains a valid number)
' - Convert from 'normal' Arabic numbers into Roman
' - Convert from Roman into 'normal' Arabic numbers
'
' Note: Bah.Regex is needed to validate the validity of a Roman Numeral.
' If you do not have the bah.regex module installed, you can remark out the 'ValidateRomanNumeral' call -- the others functions will still work, but won't be able to detect invalid input.
'

Import BaH.RegEx
SuperStrict

Local Test:String="MCMXCIX"  ' MCMXCIX = 1999

If ValidateRomanNumeral(Test)=True Then
	Print test+" is "+RomanToInt(Test)
Else
	Print test+" is not a valid number"
End If

Print "Roman Numeral for 1984 is: "+intToRoman(1984)

Function RomanToInt:Int(Roman:String)
	Local tempvar1:Int=0
	Local Workvar1:Int=0
	roman=Trim(roman.toUpper())
	While Len(roman)>0
		If Left(roman,2)="CM" Then
			workvar1=workvar1+900
			roman=Mid(roman,3)
		ElseIf Left(roman,1)="M" Then
			workvar1=workvar1+1000
			roman=Mid(roman,2)
		ElseIf Left(roman,2)="CD" Then
			workvar1=workvar1+400
			roman=Mid(roman,3)
		ElseIf Left(roman,1)="D" Then
			workvar1=workvar1+500
			roman=Mid(roman,2)
		ElseIf Left(roman,1)="D" Then
			workvar1=workvar1+500
			roman=Mid(roman,2)
		ElseIf Left(roman,1)="C" Then
			workvar1=workvar1+100
			roman=Mid(roman,2)
		ElseIf Left(roman,2)="XC" Then
			workvar1=workvar1+90
			roman=Mid(roman,3)
		ElseIf Left(roman,2)="XL" Then
			workvar1=workvar1+40
			roman=Mid(roman,3)
		ElseIf Left(roman,1)="L" Then
			workvar1=workvar1+50
			roman=Mid(roman,2)
		ElseIf Left(roman,2)="IX" Then
			workvar1=workvar1+9
			roman=Mid(roman,3)
		ElseIf Left(roman,1)="X" Then
			workvar1=workvar1+10
			roman=Mid(roman,2)
		ElseIf Left(roman,2)="IV" Then
			workvar1=workvar1+4
			roman=Mid(roman,3)
		ElseIf Left(roman,1)="V" Then
			workvar1=workvar1+5
			roman=Mid(roman,2)
		ElseIf Left(roman,1)="I" Then
			workvar1=workvar1+1
			roman=Mid(roman,2)
		End If	
	Wend
	Return workvar1		
End Function

Function IntToRoman:String(Number:Int)
	Local tempint1:Int=0
	Local tempstring:String=""
	While number>=1000 
		tempstring=tempstring+"M"
		number=number-1000
	Wend
	If number>=900 Then
		tempstring=tempstring+"CM"
		number=number-900
	End If
	If number>=500 Then
		tempstring=tempstring+"D"
		number=number-500
	End If
	If number>=400 Then
		tempstring=tempstring+"CD"
		number=number-400
	End If
	If number>=300 Then
		tempstring=tempstring+"CCC"
		number=number-300
	End If
	If number>=200 Then
		tempstring=tempstring+"CC"
		number=number-200
	End If	
	If number>=100 Then
		tempstring=tempstring+"C"
		number=number-100
	End If		
	If number>=90 Then
		tempstring=tempstring+"XC"
		number=number-90
	End If		
	If number>=80 Then
		tempstring=tempstring+"LXXX"
		number=number-80
	End If		
	If number>=70 Then
		tempstring=tempstring+"LXX"
		number=number-70
	End If			
	If number>=60 Then
		tempstring=tempstring+"LX"
		number=number-60
	End If			
	If number>=50 Then
		tempstring=tempstring+"L"
		number=number-50
	End If			
	If number>=40 Then
		tempstring=tempstring+"XL"
		number=number-40
	End If			
	If number>=30 Then
		tempstring=tempstring+"XXX"
		number=number-30
	End If			
	If number>=20 Then
		tempstring=tempstring+"XX"
		number=number-20
	End If			
	If number>=15 Then
		tempstring=tempstring+"XV"
		number=number-15
	End If			
	If number>=10 Then
		tempstring=tempstring+"X"
		number=number-10
	End If
	If number>=9 Then
		tempstring=tempstring+"IX"
		number=number-9
	End If	
	If number>=8 Then
		tempstring=tempstring+"VIII"
		number=number-8
	End If	
	If number>=7 Then
		tempstring=tempstring+"VII"
		number=number-7
	End If	
	If number>=6 Then
		tempstring=tempstring+"VI"
		number=number-6
	End If	
	If number>=5 Then
		tempstring=tempstring+"V"
		number=number-5
	End If	
	If number>=4 Then
		tempstring=tempstring+"IV"
		number=number-4
	End If	
	If number>=3 Then
		tempstring=tempstring+"III"
		number=number-3
	End If	
	If number>=2 Then
		tempstring=tempstring+"II"
		number=number-2
	End If	
	If number>=1 Then
		tempstring=tempstring+"I"
		number=number-1
	End If	
	Return tempstring
End Function


Function ValidateRomanNumeral:Int(Roman:String)
	Local RegEx:TRegEx = TRegEx.Create("(([IXCM])\2{10,})|[^IVXLCDM]|([IL][LCDM])|([XD][DM])|(V[VXLCDM])|(IX[VXLC])|(VI[VX])|(XC[LCDM])|(LX[LC])|((CM|DC)[DM])|(I[VX]I)|(X[CL]X)|(C[DM]C)|(I{2,}[VX])|(X{2,}[CL])|(C{2,}[DM])")
	Local match:TRegExMatch = regex.Find(Roman)
	If match Or Trim(Roman)=""
		' Not a valid Roman Numeral
		Return False
	Else
		' Valid Roman Numeral
		Return True
	End If
End Function
