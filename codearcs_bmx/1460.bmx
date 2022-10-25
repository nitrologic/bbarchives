; ID: 1460
; Author: RepeatUntil
; Date: 2005-09-11 01:01:37
; Title: Tokenize a string
; Description: Split a string in several tokens (substring) using a delimiter

'----------------------------------------------------------------------
'------------------------- T O K E N I Z E R --------------------------
'- This object is used to divide a string in several tokens           -
'- See examples and comments                                          -
'- Written by RepeatUntil, free to use                                - 
'----------------------------------------------------------------------
Type Tokenizer

  Field theString:String = ""
  Field tokenList:TList = New TList
  Field tokenLink:TLink = New TLink 
 

  Function Create:Tokenizer(theString:String)
    Local newTokenizer:Tokenizer = New Tokenizer 
    newTokenizer.theString = theString
    Return newTokenizer
  End Function


  Method Set(theString:String)
    self.theString = theString
  End Method


  Method Get:String()
    Return ToString()
  End Method


  Method ToString:String()
		Return theString
	End Method

	
	Method Tokenize(delim:String = " ")
	  tokenList.Clear()
	  lastI = -1
	  token:String = ""
	  For i = 0 To theString.length - 1
	    If theString[i..(i+delim.length)] = delim Then
	      token = theString[(lastI+1)..i]
        tokenList.AddLast(token)
        i = i + delim.length - 1 ' Needed for the support of a delimiter with more than 1 char
	  		lastI = i 
	    EndIf
	  Next
	  token = theString[(lastI+1)..]
    tokenList.AddLast(token) 
	
    self.GotoFirstToken()
	End Method


	Method CountTokens()
	  Return tokenList.Count()
	End Method
	
	
	Method NextToken:String()
    If tokenLink = Null Then Return ""
	  token:String = tokenLink.Value().ToString()
  	tokenLink = tokenLink.NextLink()
	  Return token
	End Method


  Method GotoFirstToken()
    tokenLink = tokenList.FirstLink()
  End Method


  Method HasMoreTokens()
    If tokenLink = Null Then Return False
    Return True
  End Method


  Method TokensToArray:String[]()
    Local array:Object[] 
    array = tokenList.ToArray()
    Local array2:String[array.length]
    For i = 0 To array.length - 1
      array2[i] = array[i].ToString()
    Next
    Return array2
  End Method


  Method Split:String[](delim:String = " ")
    Tokenize(delim)
    Return TokensToArray()
  End Method
End Type



Print
Print "--------------- EXAMPLE OF USE OF THE TOKENIZER OBJECT -------------------"
Print 

' 2 ways to create the object Tokenizer:
Local myString:Tokenizer = New Tokenizer
myString.Set("Hello/Blitz/Max")
' Or:
Local myString2:Tokenizer = Tokenizer.Create("How are you Blitz people?")

' To print it:
Print "myString = " + myString.ToString()
' Or:
Print "myString2 = " + myString2.Get()

' Let's see the first method to tokenize: using lists
' (note than you can use a delimiter of any size)
myString.Tokenize("/")
Print "There are " + myString.CountTokens() + " tokens in myString"
Print 


' Several way to go through the tokens:
While (myString.HasMoreTokens()) 
  Print "Using NextToken() method: " + myString.NextToken()
Wend
Print
' We can also go back to the first token
myString.GotoFirstToken()

' Another way to go through the tokens: using the method TokensToArray()
Local tokenArray:String[] = myString.TokensToArray()
For token$ = EachIn tokenArray
  Print "Using the TokensToArray() method: " + token
Next

' Still another way to go through the token: using the TList tokenList
For token$ = EachIn myString.tokenList
  Print "Using the TList tokenList: " + token
Next 
Print


' Finally another way to tokenize a string: retrieve directly a string array
Local splitArray:String[] = myString2.Split(" ")
Print "There are " + myString2.CountTokens() + " tokens in myString2"

For token$ = EachIn splitArray
  Print "Using Split() on myString2: " + token
Next
