; ID: 2454
; Author: Loktar
; Date: 2009-04-07 21:35:31
; Title: Access Twitter Search API
; Description: Get data from the Twitter Search API

; This gets the latest 15 tweets based on the keyword. You can also pass rpp= to get 
; up to 100 results, and * to get pages, example "GET /search.atom?q=" + keyword$ + "&rpp=100*5" 

Graphics3D 640,480, 0 ,2
Dim Tweets$(100)

getTweets("blitz")

WaitKey

Function getTweets(keyword$)
	tcp = OpenTCPStream("search.twitter.com", 80)

	If Not tcp Print "Failed.":WaitKey:End


	WriteLine tcp, "GET /search.atom?q=" + keyword$
	WriteLine tcp, "HOST: search.twitter.com"

	If Eof(tcp) Print "Failed.":WaitKey:End

	While Not Eof(tcp)
		currentTweet$ = Trim(ReadLine$(tcp))	
		If Left(currentTweet$, 7) = "<title>" Then
			Tweets$(i) = Mid(currentTweet$, 8, Len(currentTweet$) -15)
			Print Tweets$(i)
		End If 
	Wend

	If Eof(tcp)<>1 Then Print "Error!"

	CloseTCPStream tcp
End Function
