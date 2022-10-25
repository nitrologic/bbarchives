; ID: 2504
; Author: Nilium
; Date: 2009-06-12 19:45:01
; Title: Character Set
; Description: Classes to aid in searching strings for specific ranges of characters

SuperStrict

'buildopt:threads
Rem
Module Cower.CharSet

ModuleInfo "Name: Character Set Operations"
ModuleInfo "Author: Noel Cower"
ModuleInfo "License: Public Domain" ' was MIT, but the code is too simple to care
EndRem

Import brl.LinkedList

?Threaded
Import brl.Threads
?

' Ranges must be formatted as such: "a-z", single characters are counted as ranges with
' zero length (they only match the beginning character)
' You can escape characters with \s, but this will not convert characters like \n to a newline,
' since BMax already has ~n, ~t, ~q, etc.
' if you want to use - as a single character, you have to escape it.  any other use of it in a range
' is understood to be a character, unless it's a middle character

Public

Type TCharacterSet
	Field _ranges:TList
	
	'#region Convenience functions
	
	Function ForWhitespace:TCharacterSet()
		Return New TCharacterSet.InitWithWhitespace()
	End Function
	
	Function ForAlphanumeric:TCharacterSet()
		Return New TCharacterSet.InitWithAlphanumeric()
	End Function
	
	Function ForNewline:TCharacterSet()
		Return New TCharacterSet.InitWithNewline()
	End Function
	
	Function ForLetters:TCharacterSet()
		Return New TCharacterSet.InitWithLetters()
	End Function
	
	Function ForUppercaseLetters:TCharacterSet()
		Return New TCharacterSet.InitWithUppercaseLetters()
	End Function
	
	Function ForLowercaseLetters:TCharacterSet()
		Return New TCharacterSet.InitWithLowercaseLetters()
	End Function
	
	Function ForNumbers:TCharacterSet()
		Return New TCharacterSet.InitWithNumbers()
	End Function
	
	'#endregion
	
	'#region Initializers
	
	' Initializes an empty charset
	Method Init:TCharacterSet()
		_ranges = New TList
		Return Self
	End Method
	
	Method InitWithWhitespace:TCharacterSet()
		Init
		__addRange(9,1)
		__addRange(13,0)
		__addRange(32,0)
		Return Self
	End Method
	
	Method InitWithAlphanumeric:TCharacterSet()
		Init
		__addRange(65,26)
		__addRange(97,26)
		__addRange(48,10)
		Return Self
	End Method
	
	Method InitWithLetters:TCharacterSet()
		Init
		__addRange(65,26)
		__addRange(97,26)
		Return Self
	End Method
	
	Method InitWithUppercaseLetters:TCharacterSet()
		Init
		__addRange(65,26)
		Return Self
	End Method
	
	Method InitWithLowercaseLetters:TCharacterSet()
		Init
		__addRange(97,26)
		Return Self
	End Method
	
	Method InitWithNumbers:TCharacterSet()
		Init
		__addRange(48,10)
		Return Self
	End Method
	
	Method InitWithNewline:TCharacterSet()
		Init
		__addRange(10,0)
		Return Self
	End Method
	
	' Initializes a character set with a range string
	Method InitWithString:TCharacterSet(s$)
		Init
		__addRangesWithString(s)
		Return Self
	End Method
	
	' Initializes a character set with a range
	Method InitWithRange:TCharacterSet(begin:Short, length:Short)
		Init
		__addRange(begin,length)
		Return Self
	End Method
	
	'#endregion
	
	'#region Charset operations
	
	Method FindInString:Int(s$, from%=0)
		Local result:Int = -1, found:Int
		For Local cr:TCharacterRange = EachIn _ranges
			found = cr.FindInString(s, from)
			If found <> -1 And (found < result Or result = -1) Then
				result = found
			EndIf
		Next
		Return result
	End Method
	
	Method FindLastInString:Int(s$, from%=-1)
		Local result:Int = -1, found:Int
		For Local cr:TCharacterRange = EachIn _ranges
			found = cr.FindLastInString(s, from)
			If found <> -1 And (found < result Or result = -1) Then
				result = found
			EndIf
		Next
		Return result
	End Method
	
	Method Contains:Int(char:Int)
		For Local cr:TCharacterRange = EachIn _ranges
			If cr.Contains(char) Then
				Return True
			EndIf
		Next
		Return False
	End Method
	
	'#endregion
	
	'#region Copying
	
	Method Copy:TCharacterSet()
		Local cp:TCharacterSet = New TCharacterSet
		cp.Init
		__copyInto(cp)
		Return cp
	End Method
	
	Method MutableCopy:TMutableCharacterSet()
		Local cp:TMutableCharacterSet = New TMutableCharacterSet
		cp.Init
		__copyInto(cp)
		Return cp
	End Method
	
	'#endregion
	
	'#region Misc
	
	' Returns a list with all the characters for the set
	Method Characters:TList()
		Local strList:TList = New TList
		Local range:TCharacterRange
		Local char:Int
		Local charString$
		For range = EachIn _ranges
			For char = range._begin To range._end
				charString = bbStringFromChar(char)
				strList.AddLast(charString)
			Next
		Next
		Return strList
	End Method
	
	Method ToString$()
		Local size:Int = 0
		For Local range:TCharacterRange = EachIn _ranges
			size :+ (range._end)-(range._begin)+1
		Next
		Local arr:Short[size]
		Local idx:Int = 0
		For Local range:TCharacterRange = EachIn _ranges
			For Local i:Int = range._begin To range._end
				arr[idx] = i
				idx :+ 1
			Next
		Next
		Return String.FromShorts(arr,size)
	End Method
	
	'#endregion
	
	'#region PROTECTED
	' if you want to modify the type, use the mutable charset
	
	' This should only be called from the Copy/MutableCopy methods
	Method __copyInto(other:TCharacterSet)
		For Local i:Object = EachIn _ranges
			other._ranges.AddLast(i)
		Next
	End Method
	
	Method __debugString:String()
		Local outs$
		If _ranges.IsEmpty() Then
			Return "Empty"
		EndIf
		
		outs :+ "Ranges:"
		For Local i:TCharacterRange = EachIn _ranges
			outs :+ " "+i.__debugString()
		Next
		Return outs
	End Method
	
	Method ObjectEnumerator:TListEnum()
		Return Characters().ObjectEnumerator()
	End Method
	
	Method __addRangesWithString(str$)
		Const CHAR_DASH% = $2D
		Const CHAR_ESCAPE% = $5C
		
		Local lastChar:Int = -1
		Local char:Int
		Local isRange:Int = False
		Local escape:Int = False
		
		For Local i:Int = 0 Until str.Length
			char = str[i]
			If escape Then
				escape = False
			ElseIf char = CHAR_ESCAPE Then
				escape = True
				Continue
			ElseIf lastChar <> -1 And char = CHAR_DASH And Not isRange Then
				isRange = True
				Continue
			EndIf
			
			If lastChar <> -1 Then
				If isRange Then
					If char = lastChar Then
						__addRange(char, 0)
					ElseIf char < lastChar Then
						__addRange(char, lastChar-char)
					Else
						__addRange(lastChar, char-lastChar)
					EndIf
					lastChar = -1
					isRange = False
				Else
					__addRange(lastChar, 0)
					lastChar = char
				EndIf
			Else
				lastChar = char
			EndIf
		Next
		If lastChar <> -1 Then
			If isRange Then
				Throw "Invalid range in string '"+str+"'"
			EndIf
			__addRange(lastChar, 0)
		EndIf
	End Method
	
	Method __addRange(begin:Int, length:Int)
		If begin < 0 Then
			Throw "AddRange: Invalid beginning "+begin
		ElseIf length < 0 Then
			Throw "AddRange: Invalid length "+length
		EndIf
		
		Local empty% = _ranges.IsEmpty()
		Local range:TCharacterRange = New TCharacterRange.InitWithRange(begin, length)
		If range And (empty Or (Not _ranges.Contains(range))) Then ' try to avoid adding the same range twice
			If Not empty Then
				For Local i:TCharacterRange = EachIn _ranges
					If range.Intersects(i) Then
						If range._begin < i._begin Then
							i._begin = range._begin
						EndIf
					
						If i._end < range._end Then
							i._end = range._end
						EndIf
						Return
					EndIf
				Next
			EndIf
			_ranges.AddLast(range)
		EndIf
	End Method
	
	'#endregion
	
End Type

Type TMutableCharacterSet Extends TCharacterSet
	?Threaded
	Field _lock:TMutex
	?
	
	'#region Convenience functions
	
	Function ForWhitespace:TMutableCharacterSet()
		Return New TMutableCharacterSet.InitWithWhitespace()
	End Function
	
	Function ForAlphanumeric:TMutableCharacterSet()
		Return New TMutableCharacterSet.InitWithAlphanumeric()
	End Function
	
	Function ForNewline:TMutableCharacterSet()
		Return New TMutableCharacterSet.InitWithNewline()
	End Function
	
	Function ForLetters:TMutableCharacterSet()
		Return New TMutableCharacterSet.InitWithLetters()
	End Function
	
	Function ForUppercaseLetters:TMutableCharacterSet()
		Return New TMutableCharacterSet.InitWithUppercaseLetters()
	End Function
	
	Function ForLowercaseLetters:TMutableCharacterSet()
		Return New TMutableCharacterSet.InitWithLowercaseLetters()
	End Function
	
	Function ForNumbers:TMutableCharacterSet()
		Return New TMutableCharacterSet.InitWithNumbers()
	End Function
	
	'#endregion
	
	'#region Initializers
	
	Method Init:TMutableCharacterSet()
		?Threaded
		Local res:TMutableCharacterSet = TMutableCharacterSet(Super.Init())
		If res Then
			_lock = TMutex.Create()
			If Not _lock Then
				RuntimeError("TMutableCharacterSet#Init: Couldn't allocate mutex for character set")
			EndIf
		EndIf
		Return res
		?Not Threads
		Return TMutableCharacterSet(Super.Init())
		?
	End Method
	
	Method InitWithWhitespace:TMutableCharacterSet()
		Return TMutableCharacterSet(Super.InitWithWhitespace())
	End Method
	
	Method InitWithAlphanumeric:TMutableCharacterSet()
		Return TMutableCharacterSet(Super.InitWithAlphanumeric())
	End Method
	
	Method InitWithNewline:TMutableCharacterSet()
		Return TMutableCharacterSet(Super.InitWithNewline())
	End Method
	
	Method InitWithLetters:TMutableCharacterSet()
		Return TMutableCharacterSet(Super.InitWithLetters())
	End Method
	
	Method InitWithUppercaseLetters:TMutableCharacterSet()
		Return TMutableCharacterSet(Super.InitWithUppercaseLetters())
	End Method
	
	Method InitWithLowercaseLetters:TMutableCharacterSet()
		Return TMutableCharacterSet(Super.InitWithLowercaseLetters())
	End Method
	
	Method InitWithNumbers:TMutableCharacterSet()
		Return TMutableCharacterSet(Super.InitWithNumbers())
	End Method
	
	Method InitWithString:TMutableCharacterSet(s$)
		Return TMutableCharacterSet(Super.InitWithString(s))
	End Method
	
	Method InitWithRange:TMutableCharacterSet(begin:Short, length:Short)
		Return TMutableCharacterSet(Super.InitWithRange(begin, length))
	End Method
	
	'#endregion
	
	'#region Charset operations
	
	Method AddRangesWithString(str$)
		__addRangesWithString(str)
	End Method
	
	Method AddRange(begin:Int, length:Int)
		__addRange(begin, length)
	End Method
	
	Method AddCharacter(char:Int)
		__addRange(char, 0)
	End Method
	
	'#endregion
	
	'#region Threaded methods
	
	?Threaded
	Method FindInString:Int(s$, from%=0)
		_lock.Lock
		Local res:Int = Super.FindInString(s,from)
		_lock.Unlock
		Return res
	End Method
	
	Method FindLastInString:Int(s$, from%=-1)
		_lock.Lock
		Local res:Int = Super.FindLastInString(s,from)
		_lock.Unlock
		Return res
	End Method
	
	Method Contains:Int(char:Int)
		_lock.Lock
		Local res:Int = Super.Contains(char)
		_lock.Unlock
		Return res
	End Method
	
	Method Characters:TList()
		_lock.Lock
		Local res:TList = Super.Characters()
		_lock.Unlock
		Return res
	End Method
	
	Method ToString$()
		_lock.Lock
		Local res$ = Super.ToString()
		_lock.Unlock
		Return res
	End Method
	?
	
	'#endregion
	
	' PROTECTED
	
	'#region Threaded methods
	
	?Threaded
	Method __addRange(begin:Int, length:Int)
		_lock.Lock
		Try
			__addRange(begin, length) ' if I had finally blocks, this wouldn't be so horrifyingly ugly
		Catch o:Object
			_lock.Unlock
			Throw o
		End Try
		_lock.Unlock
	End Method
	
	Method __addRangesWithString(str$)
		_lock.Lock
		Try
			__addRangesWithString(str)
		Catch o:Object
			_lock.Unlock
			Throw o
		End Try
		_lock.Unlock
	End Method
	
	Method __debugString:String()
		_lock.Lock
		Local res:String = Super.__debugString()
		_lock.Unlock
		Return res
	End Method
	
	Method __copyInto(other:TCharacterSet)
		_lock.Lock
		Self.__copyInto(other)
		_lock.Unlock
	End Method
	?
	
	'#endregion
	
End Type

Private

Type TCharacterRange
	Field _begin:Int
	Field _end:Int
	
	Method InitWithRange:TCharacterRange(begin:Int, length:Int)
		_begin = begin
		_end = begin+length
		
		Return Self
	End Method
	
	Method Contains:Int( char:Int )
		Return (char = _begin Or (char >= _begin And char <= _end))
	End Method
	
	Method FindInString:Int( str$, from%=0 )
		For Local idx:Int = from Until str.Length
			If Contains(str[idx]) Then
				Return idx
			EndIf
		Next
		Return -1
	End Method
	
	Method FindLastInString:Int( str$, from%=0 )
		For Local idx:Int = from To 0 Step -1
			If Contains(str[idx]) Then
				Return idx
			EndIf
		Next
		Return -1
	End Method
	
	Method Intersects:Int(other:TCharacterRange)
		If Compare(other) = 0 Then
			Return True
		EndIf
		
		If _end = other._begin-1 Then
			Return True
		ElseIf _begin-1 = other._end Then
			Return True
		EndIf
		
		Return Not(_begin > other._end Or _end < other._begin)
	End Method
	
	Method Compare:Int(other:Object)
		Local o:TCharacterRange = TCharacterRange(other)
		If o = Null Then
			Return Super.Compare(other)
		EndIf
		
		Local sd% = _end-_begin
		Local od% = o._end-o._begin
		If sd < od Then
			Return -1
		ElseIf sd > od Then
			Return 1
		EndIf
		
		If _begin < o._begin Then
			Return -1
		ElseIf _begin > o._begin Then
			Return 1
		EndIf
		Return 0
	End Method
	
	Method Copy:TCharacterRange()
		Local cp:TCharacterRange = New TCharacterRange
		cp._begin = _begin
		cp._end = _end
		Return cp
	End Method
	
	' PROTECTED
	
	Method __debugString:String()
		Return "("+_begin+", "+_end+")"
	End Method
End Type

Private

Extern "C"
	Function bbStringFromChar:String(char%)
EndExtern
