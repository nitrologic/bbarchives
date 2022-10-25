; ID: 2566
; Author: BlitzSupport
; Date: 2009-08-23 16:44:16
; Title: BlitzGet MaxDeluxe
; Description: Update to BlitzGet Deluxe for BlitzMax

' SuperStrict

Function BlitzGet:Int (url:String, savepath:String, savefile:String)

	If Not url Then Return False
	
	Local success:Int = False	' File downloaded OK...
	Local done:Int = False		' Exit download loop (for retries, etc)...
	
	Local host:String
	Local file:String

	Local bytestoread:Int
	Local date:String
	Local server:String
	Local contenttype:String
	Local location:String
	Local pos:Int

	Print ""
	Print "Downloading..."
	Print ""
	
	Repeat
	
		If Left (url, 7) = "http://"
			url= Right (url, Len (url) - 7)
		EndIf
		
		Local slash:Int = Instr (url, "/")
	
		If slash
			host = Left (url, slash - 1)
			file = Right (url, Len (url) - slash + 1)
		Else
			host = url
			file = "/"
		EndIf

		If Right (savepath, 1) <> "\" And Right (savepath, 1) <> "/" Then savepath = savepath + "\"
	
		If savefile = ""
		
			If file = "/"
				savefile = "Unknown file.txt"
			Else
			
				Local findslash:Int
				Local testforslash:String
				
				For findslash = Len (file) To 1 Step - 1
					testforslash = Mid (file, findslash, 1)
					If testforslash = "/"
						savefile = Right (file, Len (file) - findslash)
						Exit
					EndIf
				Next
				
				If savefile = "" Then savefile = "Unknown file.txt"
				
			EndIf
			
		EndIf

		Local http:TSocket = CreateTCPSocket ()
		
		If http
		
			If ConnectSocket (http, HostIp (host), 80)
				
				Local www:TStream = CreateSocketStream (http)
				
				WriteLine www, "GET " + file + " HTTP/1.1" ' "GET /" gets default page...
				WriteLine www, "Host: " + host
				WriteLine www, "User-Agent: BlitzGet Deluxe"
				WriteLine www, "Accept: */*"
				WriteLine www, ""
	
				Local response:String = ReadLine (www)
		
				Print "Server response: " + response
				
				Local replycode:String
				
				If Left (response, 5) = "HTTP/"
					pos = Instr (response, " ")
					replycode = Mid (response, pos + 1, 3)
				EndIf
	
				Local header:String
	
				Repeat
			
					header = ReadLine (www)
			
					Local reply:String = ""
	
					pos = Instr (header, ": ")
	
					If pos
						reply = Left (header, pos + 1)
					EndIf
			
					Select Lower (reply)
						Case "content-length: "
							bytestoread = Int (Right (header, Len (header) - Len (reply)))
						Case "location: "
							location = Right (header, Len (header) - Len (reply))
					End Select
	
					If header Then Print header ' Skip blank line (if header = "" then nothing is printed)...
									
				Until header$ = "" Or (Eof (www))
	
				Select replycode$
		
					Case "200" ' File found...
		
						Print "Downloading file..."
						
						Local save:TStream = WriteFile (savepath + savefile)
	
						If save
					
							Local readwebfile:Int

							' Crude download routine!
							
							CopyBytes www, save, bytestoread
					
							CloseFile save
							
							' Fully downloaded, ie. same size?
							
							If FileSize (savepath + savefile) = bytestoread
								success = True
							EndIf
							
							done = True
						
						Else
							Print "Failed to create local file!"
						EndIf
	
					Case "404" ' File Not found...
					
						Print "File not found"
						done = True
					
					Case "301" ' File permanently moved...
					
						url = location

					Case "302" ' File temporarily moved...
			
						url = location
		
					Case "303" ' File moved...
		
						url = location
		
					Case "307" ' Naughty...
		
						url = location
						
				End Select
	
			EndIf
			
			CloseSocket http
			
		EndIf

		' If 'done' is still false, go back to the start with new URL (from '30*' responses)...
		
	Until done
		
	Return success
	
End Function

' This URL has worked for years...

Local download:String = "http://www.google.com/images/title_homepage4.gif"			' Google homepage logo

' Test error 404, file not found...
' download:String	 = "http://www.hi-toro.com/mp3/diffusion.mp3"					' Old music (now gone)...

' Test error 303, file moved example (works as of 28 Aug 2009)...
' download:String = "http://www.rentnet.com/" ' Redirects to http://www.move.com/apartments/main.aspx

Local download_path:String = CurrentDir ()

If BlitzGet (download, download_path, "")
	Print ""
	Print "File downloaded successfully! Check " + download_path
Else
	Print ""
	Print "Download failed!"
EndIf
