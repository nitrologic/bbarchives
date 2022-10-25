; ID: 2535
; Author: Nilium
; Date: 2009-07-16 17:59:15
; Title: Tag Generator
; Description: Class to generate unique strings/tags from a set of characters.

SuperStrict

Type TTagGenerator

	?Threaded
	Field _tags_mutex:TMutex
	?
	Field _tagmap:TMap
	Field _characters:String = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
	Field _timeout:Int = 2147483647
	Field _length:Int = 8
	Field _tagbuff:Short Ptr

	Method New()
		?Threaded
		_tags_mutex = TMutex.Create()
		?
		_tagmap = New TMap
		SetTagLength(_length)
	End Method
	
	Method Delete()
		?Threaded
		_tags_mutex.Close()
		?
		If _tagbuff Then
			MemFree(_tagbuff)
		EndIf
	End Method
	
	Method SetTagLength(l:Int)
		?Threaded
		_tags_mutex.Lock
		?
		Assert 0 < l Else "Length less than 1"
		_length = l
		If _tagbuff Then
			MemFree(_tagbuff)
		EndIf
		_tagbuff = Short Ptr(MemAlloc(_length*2))
		?Threaded
		_tags_mutex.Unlock
		?
	End Method
	
	Method SetCharacterString(chars$)
		?Threaded
		_tags_mutex.Lock
		?
		Assert chars Else "Empty character string"
		_characters = chars
		?Threaded
		_tags_mutex.Unlock
		?
	End Method
	
	Method SetTimeout(timeout:Int)
		?Threaded
		_tags_mutex.Lock
		?
		Assert timeout < 0 Else "Timeout less than 0"
		_timeout = timeout
		?Threaded
		_tags_mutex.Unlock
		?
	End Method

	Method NextTag$() NoDebug
		
		?Threaded
		_tags_mutex.Lock
		?
		
		' generate a short tag to follow the method name, just to reduce the chance of conflicts
		Local tag:String
		
		Local ticks:Int = 0
		Local tagexists:Int = False
		Repeat
			For Local i:Int = 0 Until _length
				_tagbuff[i] = _characters[Rand(0, _characters.Length-1)]
			Next
			tag = String.FromShorts(_tagbuff, _length)
			tagExists = _tagmap.Contains(tag)
			ticks :+ 1
		Until Not tagexists Or (0 <= _timeout And _timeout <= ticks)
		
		Local timedOut:Int = (tagExists And _timeout <= ticks)
		If Not timedOut Then
			_tagmap.Insert(tag, tag)
		EndIf
		
		?Threaded
		_tags_mutex.Unlock
		?
		
		Assert Not timedOut Else "Unable to create unique tag"
		
		Return tag
	End Method
	
	Method RemoveTag(tag$)
		?Threaded
		_tags_mutex.Lock
		?
		_tagmap.Remove(tag)
		?Threaded
		_tags_mutex.Unlock
		?
	End Method
	
	
	Method ClearTags()
		?Threaded
		_tags_mutex.Lock
		?
		_tagmap.Clear
		?Threaded
		_tags_mutex.Unlock
		?
	End Method
	
End Type
