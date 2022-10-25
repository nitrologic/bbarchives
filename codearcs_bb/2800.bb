; ID: 2800
; Author: ThePict
; Date: 2010-12-21 07:05:44
; Title: BlitzUnixTime
; Description: Functions

Graphics 800,600,16,2
Print "UnixTime = "+UnixTime()
time$=UTDateandTime$()
Print "The correct time (GMT) is "+time$
Print
Print "Your computer thinks it is "+CurrentDate$()+" - "+CurrentTime$()
FlushKeys
WaitKey()
End

;I needed a firm time reference for my web game, so (with lots of help) I put this together.
;It seems to be very accurate. I compared to BT's 123 service. 
;I know that a slightly different php code could have done what the long-winded function does for me,
;but my game only needed an integer to calc from, so had to assemble readable date and time myself.

;Now all I have to do is finish my WhereInTheWorldAreYou() function - anyone already done one?

Function UTDateandTime$()
ut=UnixTime()
days=1+(ut/24/60/60); don't know why but it was always a day slow
For year=1970 To 2039;should exit before we get to 2039
If year Mod 4=0 Then daysinyear=366 Else daysinyear=365
If days<=daysinyear Then Exit
days=days-daysinyear
Next
If daysinyear=366 And days>0 Then mon$="Jan" 
If daysinyear=366 And days>31 Then mon$="Feb"
If daysinyear=366 And days>60 Then mon$="Mar"
If daysinyear=366 And days>91 Then mon$="Apr"
If daysinyear=366 And days>121 Then mon$="May" 
If daysinyear=366 And days>152 Then mon$="Jun" 
If daysinyear=366 And days>182 Then mon$="Jul" 
If daysinyear=366 And days>213 Then mon$="Aug" 
If daysinyear=366 And days>244 Then mon$="Sep" 
If daysinyear=366 And days>274 Then mon$="Oct" 
If daysinyear=366 And days>305 Then mon$="Nov" 
If daysinyear=366 And days>335 Then mon$="Dec"
If daysinyear=365 And days>0 Then mon$="Jan" 
If daysinyear=365 And days>31 Then mon$="Feb" 
If daysinyear=365 And days>59 Then mon$="Mar" 
If daysinyear=365 And days>90 Then mon$="Apr" 
If daysinyear=365 And days>120 Then mon$="May" 
If daysinyear=365 And days>151 Then mon$="Jun" 
If daysinyear=365 And days>181 Then mon$="Jul" 
If daysinyear=365 And days>212 Then mon$="Aug" 
If daysinyear=365 And days>243 Then mon$="Sep" 
If daysinyear=365 And days>273 Then mon$="Oct" 
If daysinyear=365 And days>304 Then mon$="Nov" 
If daysinyear=365 And days>334 Then mon$="Dec"	
If daysinyear=366 Then Goto leap
If mon$="Dec" Then day=days-334
If mon$="Nov" Then day=days-304
If mon$="Oct" Then day=days-273
If mon$="Sep" Then day=days-243
If mon$="Aug" Then day=days-212
If mon$="Jul" Then day=days-181
If mon$="Jun" Then day=days-151
If mon$="May" Then day=days-120
If mon$="Apr" Then day=days-90
If mon$="Mar" Then day=days-59
If mon$="Feb" Then day=days-31
If mon$="Jan" Then day=days
Goto maketime
.leap
If mon$="Dec" Then day=days-335
If mon$="Nov" Then day=days-305
If mon$="Oct" Then day=days-274
If mon$="Sep" Then day=days-244
If mon$="Aug" Then day=days-213
If mon$="Jul" Then day=days-182
If mon$="Jun" Then day=days-152
If mon$="May" Then day=days-121
If mon$="Apr" Then day=days-91
If mon$="Mar" Then day=days-60
If mon$="Feb" Then day=days-31
If mon$="Jan" Then day=days
.maketime
d$=Right$("0"+Str$(day),2)
y$=Str$(year)
date$=d$+" "+mon$+" "+y$
secs=ut Mod 86400
hour$=Right$("0"+Str$(secs/3600),2)
hours=secs/3600
min$=Right$("0"+Str$((secs/60)-(hours*60)),2)
second$=Right$("0"+Str$(secs Mod 60),2)
t$=hour$+":"+min$+":"+second$
Return date$+" - "+t$
End Function
 

;Had many helping hands getting this to work - and I still don't know how php does what it does
;I know it's a bit clunky, but at least it's mostly commented for the noobs (like me)

Function UnixTime$(); WORKS!!! Feel free to use this Function.
;As long as I own the domain the php file should work.
DownloadTime("http://www.lexicod.co.uk/unixtime.php", "", "CurrentUnixTime.txt")
Delay 5
mf=ReadFile("CurrentUnixTime.txt")
t$=ReadLine(mf)
x=Left(t$,10)
CloseFile(mf)
Return x
End Function

;* Using a slightly modified version of Devils Child's Download File Function. *
;Many thanks to DC for this.
;=============================================================================================

; ID: 2279
; Author: Devils Child
; Date: 2008-06-26 12:46:33
; Title: Download file
; Description: Supports downloading files, php sites and redirected sites. (Fast)

;=============================================================================================


;link$      - The link. You may enter the link just like you enter it
;             in your browser. Very tolerant. No http:// required.
;savepath$  - The path where the file should be saved
;savefile$  - The filename of the saved file. When given "", it will
;             be named like the file in the link$.

Function DownloadTime(link$, savepath$ = "", savefile$ = "")
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
WriteLine tcp, "GET " + link_path$ + link_file$ + " HTTP/1.1" + Chr(13) + Chr(10) + "Host: " + host$ + Chr(13) + Chr(10) + "User-Agent: Download_Function_By_DevilsChild" + Chr(13) + Chr(10)

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
					Return DownloadTime(l2$, savepath$, savefile$)
			End Select
		Forever
	Default
		CloseTCPStream tcp
		Return False
End Select
End Function
