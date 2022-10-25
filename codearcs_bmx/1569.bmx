; ID: 1569
; Author: Perturbatio
; Date: 2005-12-21 12:45:01
; Title: SoundEx function
; Description: A simple soundEx function for BMax

Function SoundEx:String(val:String, resolution:Int=3)
	Local result:String
	'a bit overboard but what the heck
	Local punctuation:String[] = [" ", ".", ",", "<", ">", ";", ":","~q","'","@","~~"."#"."(",")","{","}","[","]","!","£","$","%","^","&","*"]
	Local FirstReplace:String[] = ["A", "E", "I", "O", "U", "H", "W", "Y"]
	Local ReplaceWithOne:String[] = ["B", "F", "P", "V"]
	Local ReplaceWithTwo:String[] = ["C", "G", "J", "K", "Q", "S", "X", "Z"]
	Local s:String
	Local padding:Int
	
	val = Trim(val.ToUpper())
	
	'custom part for english language
	val = val.replace("CHR", "KR") 'CH followed by R in english is always a hard K sound
	val = val.replace("TCH", "CH")
	val = val.replace("CK" , "K")
	val = val.replace("SS", "S")
	val = val.replace("CA", "KA")
	val = val.replace("GH", "H") 'with GH, the G is silent
	'an exception is ghost, could do a check to see if a vowel follows
	val = val.replace("PH" , "F")
	'an exception is in haphazard (only one I can think of)
	
	For s = EachIn punctuation
		val = val.replace(s, "")
	Next
	result = val[0..1]

	For s = EachIn FirstReplace
		val = val.replace(s, "0")
	Next

	For s = EachIn ReplaceWithOne
		val = val.replace(s, "1")
	Next

	For s = EachIn ReplaceWithTwo
		val = val.replace(s, "2")
	Next

	val = val.replace("D", 3)
	val = val.replace("T", 3)
	val = val.replace("L", 4)
	val = val.replace("M", 5)
	val = val.replace("N", 5)
	val = val.replace("R", 6)
	val = val.replace("00", "")
	val = val.replace("11", "")
	val = val.replace("22", "")
	val = val.replace("33", "")
	val = val.replace("44", "")
	val = val.replace("55", "")
	val = val.replace("66", "")
	val = val.replace("0", "")
	
	padding = resolution - val.length
	If padding > 0 Then 
		For Local i:Int = 0 To padding -1
			val:+"0"
		Next
	EndIf
	
	Return result+val[..resolution]
End Function

'test
Print SoundEx("Smith")
Print SoundEx("Smyth")
Print SoundEx("Smithe")
Print SoundEx("Smiff")
Print SoundEx("Robertson")
Print SoundEx("Robinson")
Print SoundEx("Davidson")
Print SoundEx("Davison")
Print SoundEx("White")
Print SoundEx("Whyte")
Print SoundEx("Kris Kelly")
Print SoundEx("Chris Kelly")
Print SoundEx("Kriss Kelly")

Print SoundEx("Kelly Robertson")
Print SoundEx("Kayleigh Robinson")

Print SoundEx("Edinburgh")
Print SoundEx("Edinborough") 'bleah!
