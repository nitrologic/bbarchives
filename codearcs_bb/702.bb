; ID: 702
; Author: Red
; Date: 2003-05-21 19:05:53
; Title: SetTextAreaSel
; Description: Change textarea selection

Function SetTextAreaSel(txt,pos,len,units)	
	Local EM_SETSEL=$B1	
		
	If units=1
		[#C0FF00];selection by characters[#00FF00] 
		sendmessage(QueryObject(txt,1), EM_SETSEL,  pos,pos+len)
	Else
		[#C0FF00];selection by lines[#00FF00]	
		Local posligne=TextAreaChar(txt,pos )
		len=TextAreaChar(txt,pos+len)-posligne 		
		sendmessage(QueryObject(txt,1), EM_SETSEL,  posligne,posligne+len)
	EndIf 
End Function
