; ID: 2036
; Author: XtremeCoder
; Date: 2007-06-11 17:03:50
; Title: Code2HTML
; Description: Turns code into an HTML file, good for open source distributions

Function Code2HTML(file$,author$)
codefile=OpenFile(file$)
htmlfile=WriteFile(file$+".html")
	WriteLine(htmlfile, "<html>")
	WriteLine(htmlfile, "<head><title>"+ file$ + "</title></head>")
	WriteLine(htmlfile, "<body bgcolor=000000 Text=66CCFF >")
	WriteLine(htmlfile, "<center>")
	WriteLine(htmlfile, "<h1>"+ file$ + "</h1>")
	WriteLine(htmlfile, "<p><h3>Date: "+ CurrentDate() + "</h3>")
	WriteLine(htmlfile, "<h3>Author: "+ author$ +"</h3>")
	WriteLine(htmlfile, "</center>")
	WriteLine(htmlfile, "<hr>")
	WriteLine(htmlfile, "<center>")
	WriteLine(htmlfile, "<h2>Code:</h2>")
	WriteLine(htmlfile, "</center>")
		While Not Eof(codefile)
			ReadCode$ = ReadLine( codefile )
			WriteLine(htmlfile, "<p>"+ReadCode$)
		Wend
	WriteLine(htmlfile, "</body>")
	WriteLine(htmlfile, "</html>")
CloseFile(htmlfile)
CloseFile(codefile)
End Function
