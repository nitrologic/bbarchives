; ID: 1942
; Author: skn3[ac]
; Date: 2007-03-09 19:07:30
; Title: Tokenize, GetToken, Explode, Implode
; Description: Some usefull token related functions for blitzmax

'cache results
Global _lasttokenstring:String
Global _lasttokenarray:String[]

Function _TokenizeNoReturn:Int(nstring:String Var,nsplit:Int=32)
	'this function will take a string and return a token object contianing the tokens
	'check to see if cached token data is still valid
	If _lasttokenstring <> nstring
		'create new
		_lasttokenstring = nstring
		_lasttokenarray = New String[0]
		
		Local temp_i:Int = 0
		Local temp_start:Int = 0
		Local temp_end:Int = 0
		
		For temp_i = 0 Until nstring.length
			If nstring[temp_i] = nsplit Or temp_i = nstring.length - 1
				'set end position
				If temp_i = nstring.length - 1
					temp_end = nstring.length
				Else
					temp_end = temp_i
				End If
				
				'resize the token array
				_lasttokenarray = _lasttokenarray[.._lasttokenarray.length+1]
				
				'fill token data
				_lasttokenarray[_lasttokenarray.length-1] = nstring[temp_start..temp_end]
				
				'update start position
				temp_start = temp_i + 1
			End If
		Next
	End If
End Function

'php style tokenizing functions
Function Implode:String(ntokens:String[],nsplit:Int=32)
	Local temp_token:String
	Local temp_string:String
	
	For temp_token = EachIn ntokens
		If temp_string.length > 0 temp_string :+ Chr(nsplit)
		temp_string :+ temp_token
	Next
	
	Return temp_string
End Function

Function Explode:String[](nstring:String,nsplit:Int=32)
	_TokenizeNoReturn(nstring,nsplit)
	Return _lasttokenarray
End Function

'basic style tokenzing functions
Function Tokenize:String[](nstring:String,nsplit:Int=32)
	_TokenizeNoReturn(nstring,nsplit)
	Return _lasttokenarray
End Function

'universal style tokenzing functions
Function GetToken:String(nstring:String,ntoken:Int,nsplit:Int=32)
	_TokenizeNoReturn(nstring,nsplit)
	If ntoken > _lasttokenarray.length - 1
		Return ""
	Else
		Return _lasttokenarray[ntoken]
	End If
End Function

Function Combine:String(ntokens:String[],nstart:Int,nend:Int=-1,nsplit:Int=32)
	Local temp_start:Int = nstart
	Local temp_end:Int = nend
	Local temp_i:Int
	Local temp_build:String
	
	'fix end for do all
	If nend = -1 nend = ntokens.length-1
	
	'reverse start / end if needed
	If temp_end < temp_start
		temp_end = nstart
		temp_start = nend
	End If
	
	'make sure start is viable
	If temp_start > ntokens.length - 1
		Return ""
	Else
		'make sure end does not go past bounds
		If temp_end > ntokens.length-1 temp_end = ntokens.length - 1
		
		'combine the specified tokens
		For temp_i = temp_start To temp_end
			If temp_build.length >0 temp_build :+ Chr(nsplit)
			temp_build :+ ntokens[temp_i]
		Next
		
		'return
		Return temp_build
	End If
End Function
