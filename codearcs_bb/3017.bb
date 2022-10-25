; ID: 3017
; Author: Yasha
; Date: 2013-01-25 20:25:43
; Title: String banks/data buffers
; Description: Very fast access to string data; modify strings in-place!

; Fast string data access (Blitz3D/+)
;=====================================

; Requires FastPointer DLL (free): http://www.fastlibs.com/index.php


; Offsets of char data, data length, and buffer size within a Blitz string
Const FSTRING_CHAR_OFFSET = 4, FSTRING_LEN_OFFSET = 8, FSTRING_MAX_OFFSET = 12


Type FSTRING_Wrapper_	;Singleton wrapper type, used to get pointer only
	Field s$
End Type

Global FSTRING_private_WSlot_.FSTRING_Wrapper_, FSTRING_private_SPtr_	;Singleton instance


; Retrieve the byte at the given offset from a string buffer
Function StrPeekByte(s$, offset)
	Return Asc(Mid(s, offset + 1, 1))	;Trivial, but here for completeness
End Function

; Retrieve the short int at the given offset from a string buffer
Function StrPeekShort(s$, offset)
	If FSTRING_private_WSlot_ = Null
		FSTRING_private_WSlot_ = New FSTRING_Wrapper_
		FSTRING_private_SPtr_ = TypePointer(FSTRING_private_WSlot_)
	EndIf
	FSTRING_private_WSlot_\s = s
	Local strPtr = MemoryFastPeekInt(FSTRING_private_SPtr_)
	If offset < 0 Or offset > (MemoryFastPeekInt(strPtr + FSTRING_LEN_OFFSET) - 2)
		RuntimeError "StrPeekShort: offset out of range"
	EndIf
	Return MemoryFastPeekShort(MemoryFastPeekInt(strPtr + FSTRING_CHAR_OFFSET) + offset)
End Function

; Retrieve the integer at the given offset from a string buffer
Function StrPeekInt(s$, offset)
	If FSTRING_private_WSlot_ = Null
		FSTRING_private_WSlot_ = New FSTRING_Wrapper_
		FSTRING_private_SPtr_ = TypePointer(FSTRING_private_WSlot_)
	EndIf
	FSTRING_private_WSlot_\s = s
	Local strPtr = MemoryFastPeekInt(FSTRING_private_SPtr_)
	If offset < 0 Or offset > (MemoryFastPeekInt(strPtr + FSTRING_LEN_OFFSET) - 4)
		RuntimeError "StrPeekInt: offset out of range"
	EndIf
	Return MemoryFastPeekInt(MemoryFastPeekInt(strPtr + FSTRING_CHAR_OFFSET) + offset)
End Function

; Retrieve the float at the given offset from a string buffer
Function StrPeekFloat#(s$, offset)
	If FSTRING_private_WSlot_ = Null
		FSTRING_private_WSlot_ = New FSTRING_Wrapper_
		FSTRING_private_SPtr_ = TypePointer(FSTRING_private_WSlot_)
	EndIf
	FSTRING_private_WSlot_\s = s
	Local strPtr = MemoryFastPeekInt(FSTRING_private_SPtr_)
	If offset < 0 Or offset > (MemoryFastPeekInt(strPtr + FSTRING_LEN_OFFSET) - 4)
		RuntimeError "StrPeekFloat: offset out of range"
	EndIf
	Return MemoryFastPeekFloat(MemoryFastPeekInt(strPtr + FSTRING_CHAR_OFFSET) + offset)
End Function

; Destructively set the byte at the given offset within a string buffer
Function StrPokeByte(s$, offset, val)
	If FSTRING_private_WSlot_ = Null
		FSTRING_private_WSlot_ = New FSTRING_Wrapper_
		FSTRING_private_SPtr_ = TypePointer(FSTRING_private_WSlot_)
	EndIf
	FSTRING_private_WSlot_\s = s
	Local strPtr = MemoryFastPeekInt(FSTRING_private_SPtr_)
	If offset < 0 Or offset > (MemoryFastPeekInt(strPtr + FSTRING_LEN_OFFSET) - 1)
		RuntimeError "StrPokeByte: offset out of range"
	EndIf
	Return MemoryFastPokeByte(MemoryFastPeekInt(strPtr + FSTRING_CHAR_OFFSET) + offset, val)
End Function

; Destructively set the short int at the given offset within a string buffer
Function StrPokeShort(s$, offset, val)
	If FSTRING_private_WSlot_ = Null
		FSTRING_private_WSlot_ = New FSTRING_Wrapper_
		FSTRING_private_SPtr_ = TypePointer(FSTRING_private_WSlot_)
	EndIf
	FSTRING_private_WSlot_\s = s
	Local strPtr = MemoryFastPeekInt(FSTRING_private_SPtr_)
	If offset < 0 Or offset > (MemoryFastPeekInt(strPtr + FSTRING_LEN_OFFSET) - 2)
		RuntimeError "StrPokeShort: offset out of range"
	EndIf
	Return MemoryFastPokeShort(MemoryFastPeekInt(strPtr + FSTRING_CHAR_OFFSET) + offset, val)
End Function

; Destructively set the integer at the given offset within a string buffer
Function StrPokeInt(s$, offset, val)
	If FSTRING_private_WSlot_ = Null
		FSTRING_private_WSlot_ = New FSTRING_Wrapper_
		FSTRING_private_SPtr_ = TypePointer(FSTRING_private_WSlot_)
	EndIf
	FSTRING_private_WSlot_\s = s
	Local strPtr = MemoryFastPeekInt(FSTRING_private_SPtr_)
	If offset < 0 Or offset > (MemoryFastPeekInt(strPtr + FSTRING_LEN_OFFSET) - 4)
		RuntimeError "StrPokeInt: offset out of range"
	EndIf
	Return MemoryFastPokeInt(MemoryFastPeekInt(strPtr + FSTRING_CHAR_OFFSET) + offset, val)
End Function

; Destructively set the float at the given offset within a string buffer
Function StrPokeFloat(s$, offset, val#)
	If FSTRING_private_WSlot_ = Null
		FSTRING_private_WSlot_ = New FSTRING_Wrapper_
		FSTRING_private_SPtr_ = TypePointer(FSTRING_private_WSlot_)
	EndIf
	FSTRING_private_WSlot_\s = s
	Local strPtr = MemoryFastPeekInt(FSTRING_private_SPtr_)
	If offset < 0 Or offset > (MemoryFastPeekInt(strPtr + FSTRING_LEN_OFFSET) - 4)
		RuntimeError "StrPokeFloat: offset out of range"
	EndIf
	Return MemoryFastPokeFloat(MemoryFastPeekInt(strPtr + FSTRING_CHAR_OFFSET) + offset, val)
End Function


;~IDEal Editor Parameters:
;~F#17#1F#24#32#40#4E#5C#6A#78
;~C#Blitz3D
