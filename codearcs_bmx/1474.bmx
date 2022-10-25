; ID: 1474
; Author: Yan
; Date: 2005-10-04 09:58:50
; Title: HTTP FileSize() and more
; Description: Get the size of a file via HTTP without having to download it.

Print HTTPFileSize("http://www.google.co.uk/intl/en_uk/images/logo.gif") + " Bytes"

End


Function HTTPFileSize(url$)
	url$ = url$.Replace("http://", "")
	Local slashPos = url$.Find("/"), host$, file$
	
	If slashPos <> -1
		host$ = url$[..slashPos]
		file$ = url$[slashPos..]
	Else
		Return -1
	EndIf
	
	Local stream:TStream = OpenStream("tcp::" + host$)
	If Not stream Then Return -1
	
	stream.WriteLine "HEAD " + file$ + " HTTP/1.0"
	stream.WriteLine "Host: " + host$
	stream.WriteLine ""
	
	While Not Eof(stream)
		Local in$ = stream.ReadLine()

		If in$.Find("Content-Length:") <> -1
			stream.Close()
			Return Int(in$[in$.Find(":") + 1..].Trim())
		EndIf	
	Wend
	
	stream.Close()
	Return -1		
End Function
