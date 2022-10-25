; ID: 2931
; Author: warwulf
; Date: 2012-03-12 07:47:59
; Title: HTML WEB READER BMX
; Description: this reads html code from a website

HTTPReader("www.blitzbasic.com")
Function HTTPReader$(url$)
	Local stream:TStream = OpenStream("http::" + url$,80)
	If Not stream Then Return -1
	stream.WriteLine "HEAD " + file$ + " HTTP/1.1"
	stream.WriteLine "Host: " + host$
	While Not Eof(stream) ; Print stream.ReadLine() ; Wend
	stream.Close()
	Return header$
End Function
