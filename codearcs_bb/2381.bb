; ID: 2381
; Author: Mahan
; Date: 2008-12-23 13:37:13
; Title: StringList
; Description: Multipurpose StringList functions for b3d

;-------------------------------------------------
; strlist.bb - stringlist functions
;
; purpose: 
;	A very basic StringList.
;
;   Beware. Performance will degrade if used with
;   large amounts of Data. 
;
;-------------------------------------------------

Global sfHdlCounter_=-1

;INTERNAL: This is a contaner type for all strings in all stringlists.
Type TStrings
	Field hdl; this object belongs to this list-handle
	Field string$
End Type

;-------------------------------------------------
; Creates a new stringlist.
;
; Returns handle to new stringlist.
;
Function CreateStrings()
	sfHdlCounter_=sfHdlCounter_+1
	Return sfHdlCounter_
End Function

;-------------------------------------------------
; Frees (releases) a stringlist by handle.
;
; hdl - handle to previously created stringlist.
Function FreeStrings(hdl)
	For s.TStrings = Each TStrings
		If s\hdl=hdl Then Delete s
	Next
End Function

;-------------------------------------------------
; Adds a new string to the stringlist.
;
; hdl - stringlist handle
; s$ - the string to add
Function AddString(hdl, s$)
	n.TStrings = New TStrings
	n\hdl=hdl
	n\string=s
End Function

;-------------------------------------------------
; Returns the current number of strings in the
; stringlist.
;
; hdl - stringlist handle
Function CountStrings(hdl)
	cnt=0
	For s.TStrings = Each TStrings
		If s\hdl = hdl Then cnt=cnt+1
	Next
	Return cnt
End Function

;-------------------------------------------------
; Returns the string at a current index in the
; stringlist.
; 
; hdl - stringlist handle
; idx - index of a string between 0 and
;       CountStrings(hdl)-1.
;
; NOTE: Passing an invalid index will return ""
Function GetString$(hdl, idx)
	cnt=0
	For s.TStrings = Each TStrings
		If (s\hdl = hdl) And (cnt=idx) Then Return s.TStrings\String
		If s\hdl = hdl Then cnt=cnt+1
	Next
	Return ""
End Function


;-------------------------------------------------
; Splits a string based on a separator into a
; stringlist.
;
; hdl - stringlist handle
; s$ - the string to split
; sep$ - the separator
Function AddSplitString(hdl, s$, sep$)
	ls=Len(sep)
	p=Instr(s, sep)
	While p>0
		AddString(hdl, Left$(s, p-1))
		;s=Right$(s, p+ls+1)
		s$=Mid$(s$, p+ls, 2000000000) ; just copy the rest of the string
		p=Instr(s, sep)
	Wend
	If Len(s) > 0 Then AddString(hdl, s)
End Function

;-------------------------------------------------
; Deletes a string on a certain index in the
; stringlist.
;
; hdl - stringlist handle
; idx - index of a string between 0 and
;       CountStrings(hdl)-1.
;
; NOTE: Passing an invalid index will just waste
; cpu-cycles and do nothing.
Function DeleteStringIdx(hdl, idx)
	cnt=0
	For s.TStrings = Each TStrings
		If (s\hdl = hdl) And (cnt=idx) Then
			Delete s
			Return
		EndIf
		If s\hdl = hdl Then cnt=cnt+1
	Next
End Function


;-------------------------------------------------
; Deletes all strings equal to s$ in a stringlist.
;
; hdl - stringlist handle
; s$ - string to delete
Function DeleteString(hdl, s$)
	For st.TStrings = Each TStrings
		If (st\hdl = hdl) And (st\String=s) Then Delete st
	Next
End Function

;-------------------------------------------------
; Build a new string from all strings in the
; stringlist. A separator may be used between each
; element of the stringlist
;
; hdl - stringlist handle
; sep$ - optional separator
Function StringFromStrings$(hdl, sep$="")
	res$=""
	For s.TStrings = Each TStrings
		If (s\hdl = hdl) Then
			res=res+s\String+sep
		EndIf
	Next
	l = Len(res)
	ls = Len(sep)
	If (ls>0) And (l>0) Then res = Left$(res, l-Len(sep)) ;remove last separator
	Return res
End Function


;-------------------------------------------------
; Read in a whole ascii-stream into a stringlist
;
; hdl - stringlist handle
; stream - stream/file handle.
Function ReadStrings(hdl, stream)
	While Not Eof(stream)
		AddString(hdl, ReadLine(stream))
	Wend
End Function
