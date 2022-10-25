; ID: 2875
; Author: sting
; Date: 2011-07-25 04:24:36
; Title: Get random wiki article title.
; Description: Random wiki title

Graphics 500,180,24,2

                                        ; / Globals
Global NumTopics = 9,NumFlags = 3       ;| Flags: 1 = Disambiguation, 2 = Length Over 20, 3 = Total Length      Read as Int(Topic$(i,?))
Dim Topic$(NumTopics,NumFlags)          ;|
For i = 0 To NumTopics                  ;| Initialize array. 
	Topic$(i,0) = ""                ;|
	Topic$(i,1) = 0                 ;|
	Topic$(i,2) = 0                 ;|
	Topic$(i,3) = 0                 ;|
Next                                    ;|



;######################### EXAMPLE #########################
.start
Cls : Locate 0,5
Color 105,105,105 : Print " Acquiring 3 Random Redirects..." : Color 155,15,15
RND_Topic(0) : Print " 1" : Locate 20,18
RND_Topic(1) : Print "2"  : Locate 32,18
RND_Topic(2) : Print "3"  
Color 255,255,255 : Print " -------------------------------"
For i = 0 To 2
	Color 92,187,233
	Print " "+Topic$(i,0)
	Color 105,105,105
	Print " Flags (( Dis="+Int(Topic$(i,1))+"  20+="+Int(Topic$(i,2))+"  Length="+Int(Topic$(i,3))+" ))"
Next
Color 255,255,255 : Print " -------------------------------"
Color 155,155,155 : Print " Space = new   Esc = Exit" ; Or any other...
FlushKeys 
WaitKey
For i = 0 To NumTopics               
	Topic$(i,0) = ""                    
	Topic$(i,1) = 0                     
	Topic$(i,2) = 0                     
	Topic$(i,3) = 0                     
Next
If KeyHit(57) Then Goto start
End
;###########################################################




Function RND_Topic(L_var=0)
tcp=OpenTCPStream( "www.wikipedia.org",80 )
	If Not tcp Then Topic$(L_var,0) = "Null" : Return 
	WriteLine tcp,"GET /wiki/Special"+Chr$(58)+"Random HTTP/1.1"
	WriteLine tcp,"Host: en.wikipedia.org"
	WriteLine tcp,"User-Agent: Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.2) Gecko/20100115 Firefox/3.6 (.NET CLR 3.5.30729)"
	WriteLine tcp,Chr$(10)
	If Eof(tcp) Then Topic$(L_var,0) = "Null" : Return 
	While Not Eof(tcp)
	L_man$ = ReadLine$(tcp) : L_length = Len(L_man$)                                             ; / Header processing
	If Left$(L_man$,5) = "Locat" Then                                                            ;| Detect the line with the info we want.
		L_man$ = Mid$(L_man$,40,L_length)                                                    ;| Trim URL amd clean it up.
		For i = 1 To Len(L_man$)                                                             ;|
			If Mid$(L_man$,i,1) = "%" Then                                               ;| Detect and convert URL Escape Codes to normal ASCII.
				L_rpvr$ = Mid$(L_man$,i,3) : L_rpv$ = Chr$(h2d(Mid$(L_man$,i+1,2)))  ;|
				L_man$ = Replace$(L_man$,L_rpvr$,L_rpv$)                             ;|
			EndIf                                                                        ;|
		Next                                                                                 ;|
		L_man$ = Replace$(L_man$,"_"," ")                                                    ;| replace underscores.

		                                                              ; / Flag processing
									      ;|------------------
		If Right$(L_man$,16) = "(disambiguation)" Then                ;| Detects if "(disambiguation)" is in the title.
			Topic$(L_var,1) = 1                                   ;| Sets the flag that it was there and removes string part.
			L_man$ = Replace$(L_man$,"(disambiguation)","")       ;|
		EndIf                                                         ;|------------------
		If Len(L_man$) > 20 Then                                      ;| Detects if length is over 20 chars.
			Topic$(L_var,2) = 1                                   ;|
		EndIf                                                         ;|------------------
		Topic$(L_var,3) = Len(L_man$)                                 ;| Record length
		
		L_Topic$ = L_man$                                             ;| Make sure it wont be overwritten.
	EndIf 
	Wend
	
	If Eof(tcp)=1 Then Topic$(L_var,0) = L_Topic$                         ;| Change "Topic$(L_var,0)" to some global if you just want the string.
CloseTCPStream tcp
FlushKeys : Return
End Function 

Function h2d(L_in$)
	Local L_c, L_dec, L_val$ = "0123456789ABCDEF"                              ; / Credit Yan
	For L_c=1 To Len(L_in$)                                                    ;|
		L_dec = (L_dec Shl 4)Or(Instr(L_val$,Upper$(Mid$(L_in$,L_c,1)))-1) ;|
	Next                                                                       ;|
	Return L_dec                                                               ;|
End Function
