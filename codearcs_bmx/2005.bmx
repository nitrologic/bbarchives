; ID: 2005
; Author: Raph
; Date: 2007-04-30 20:51:31
; Title: URLEncode
; Description: Encodes strings for use in URLs

Function URLEncode:String(t:String)
	Local newString:String
	For i = 0 To Len(t)
		c:String = t[i..i+1]
		asciival = Asc(c)
		If asciival > 32 And asciival < 123
			' handle replacing the special set of chars
			
			c = Replace(c,"%","%25")
			c = Replace(c,"<","%3C")
			c = Replace(c,">","%3E")
			c = Replace(c,"\","%5C")
			c = Replace(c,"^","%5E")
			c = Replace(c,"[","%5B")
			c = Replace(c,"]","%5D")
			c = Replace(c,"+","%2B")
			c = Replace(c,"$","%24")
			c = Replace(c,",","%2C")
			c = Replace(c,"@","%40")
			c = Replace(c,":","%3A")
			c = Replace(c,";","%3B")
			c = Replace(c,"/","%2F")
			c = Replace(c,"!","%21")
			c = Replace(c,"#","%23")
			c = Replace(c,"?","%3F")
			c = Replace(c,"=","%3D")
			c = Replace(c,"&","%26")
			newString:+c
		Else
			hexstr$ = Hex(asciival)
			newhexstr$ = "%" + hexstr[Len(hexstr)-2..Len(hexstr)]
			newstring:+newhexstr
		EndIf
	Next

	newstring = newstring[..Len(newstring)-3]
	Return newstring
	
End Function
