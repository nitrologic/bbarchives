; ID: 2279
; Author: bytecode77
; Date: 2008-06-26 12:46:33
; Title: Download file
; Description: Supports downloading files, php sites and redirected sites. (Fast)

Graphics 640, 480, 32, 2
SetBuffer BackBuffer()

;Download a normal file (easy)
Print Download("http://www.google.com/intl/en_us/images/logo.gif")

;Download a php file, which is chunked!
Print Download("http://www.blitzbasic.com/index.php", "", "blitzbasic.com.index.html")

;A download which will be redirected, which is also chunked!
Print Download("http://example.com/index.php", "", "index.html")

Print ""
Print "Done."
Print "What the download function returned is printed on the screen."
Print "0 = download failed"
Print "1 = download successful"
WaitKey()
End

;=============================================================================================


;link$      - The link. You may enter the link just like you enter it
;             in your browser. Very tolerant. No http:// required.
;savepath$  - The path where the file should be saved
;savefile$  - The filename of the saved file. When given "", it will
;             be named like the file in the link$.

Function Download(link$, savepath$ = "", savefile$ = "")
;Strip protocol and return false if not "http"
inst = Instr(link$, "://")
If inst Then
	If Lower(Trim(Left(link$, inst - 1))) <> "http" Then Return False
	link$ = Right(link$, Len(link$) - inst - 2)
EndIf

;Seperate host from link
inst = Instr(link$, "/")
If inst = 0 Then Return False
host$ = Trim(Left(link$, inst - 1))
link$ = Trim(Right(link$, Len(link$) - inst + 1))

;Seperate path and file from the link
For i = Len(link$) To 1 Step -1
	If Mid(link$, i, 1) = "/" Then
		link_path$ = Trim(Left(link$, i))
		link_file$ = Right(link$, Len(link$) - i)
		Exit
	EndIf
Next
If link_file$ = "" Then Return False
If savefile$ = "" Then savefile$ = link_file$

;Open TCP stream
tcp = OpenTCPStream(host$, 80)
If tcp = 0 Then Return False
WriteLine tcp, "GET " + link_path$ + link_file$ + " HTTP/1.1" + Chr(13) + Chr(10) + "Host: " + host$ + Chr(13) + Chr(10) + "User-Agent: Download_Function_By_bytecode77" + Chr(13) + Chr(10)

;Download file
l$ = ReadLine(tcp)
inst1 = Instr(l$, " ")
inst2 = Instr(l$, " ", inst1 + 1)
num = Mid(l$, inst1, inst2 - inst1)
Select num
	Case 200
		conlen = -1
		chunk = False
		
		Repeat
			l$ = Trim(ReadLine(tcp))
			If l$ = "" Then Exit
			
			inst = Instr(l$, ":")
			l1$ = Trim(Left(l$, inst - 1))
			l2$ = Trim(Right(l$, Len(l$) - inst))
			Select Lower(l1$)
				Case "content-length"
					conlen = l2$
				Case "transfer-encoding"
					If Lower(l2$) = "chunked" Then chunk = True
			End Select
		Forever
		
		If conlen = 0 Then
			file = WriteFile(savepath$ + savefile$)
			CloseFile file
			CloseTCPStream tcp
			Return True
		ElseIf conlen > 0 Then
			file = WriteFile(savepath$ + savefile$)
			bnk = CreateBank(4096)
			pos = 0
			Repeat
				avail = conlen - pos
				If avail > 4096 Then
					ReadBytes bnk, tcp, 0, 4096
					WriteBytes bnk, file, 0, 4096
					pos = pos + 4096
				Else
					ReadBytes bnk, tcp, 0, avail
					WriteBytes bnk, file, 0, avail
					Exit
				EndIf
			Forever
			FreeBank bnk
			CloseFile file
			CloseTCPStream tcp
			Return True
		ElseIf chunk Then
			file = WriteFile(savepath$ + savefile$)
			bnk = CreateBank(4096)
			
			Repeat
				l$ = Trim(Upper(ReadLine(tcp)))
				ln = 0
				For i = 1 To Len(l$)
					ln = 16 * ln + Instr("123456789ABCDEF", Mid$(l$, i, 1))
				Next
				If ln = 0 Then Exit
				
				If BankSize(bnk) < ln Then ResizeBank bnk, ln
				ReadBytes bnk, tcp, 0, ln
				WriteBytes bnk, file, 0, ln
				ReadShort(tcp)
			Forever
			
			FreeBank bnk
			CloseFile file
			CloseTCPStream tcp
			Return True
		Else
			CloseTCPStream tcp
			Return False
		EndIf
	Case 301, 302
		Repeat
			l$ = Trim(ReadLine(tcp))
			If l$ = "" Then Exit
			
			inst = Instr(l$, ":")
			l1$ = Trim(Left(l$, inst - 1))
			l2$ = Trim(Right(l$, Len(l$) - inst))
			Select Lower(l1$)
				Case "location"
					CloseTCPStream tcp
					Return Download(l2$, savepath$, savefile$)
			End Select
		Forever
	Default
		CloseTCPStream tcp
		Return False
End Select
End Function
