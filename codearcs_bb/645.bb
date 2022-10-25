; ID: 645
; Author: Prof
; Date: 2003-04-08 10:47:16
; Title: Web Page Exist?
; Description: Function to see if a web page exists

; Web page/file exists code v2 by The Prof for Blitzers everywhere!
;
; This code will check to see if a web page exists. This is handy
; if you want to create your own error 404 pages dynamically in Blitz.
; Tested with 5 ISP web servers - seems OK - if problems let me know.
;
Graphics 640,200,32,2

WebPage$="http://www.blitzbasic.com/index.html" ;case sensitive!

If WebFileExists(WebPage$)=True
   Text 10,10,WebPage$+" exists!"
Else
   Text 10,10,WebPage$+" Does not appear to exist :-("
EndIf
WaitKey():End

; *****************************************************

Function WebFileExists(webFile$) 
 ; Checks to see if the Web File (webfile$) exists on the net
 ; Modified from Blitz Get Deluxe - Thanks!
 ; Returns TRUE if the file exists, otherwise FALSE
 
 If Left (webFile$, 7) = "http://" Then webFile$ = Right (webFile$, Len (webFile$) - 7) 
 slash = Instr (webFile$, "/") 
 If slash 
    webHost$ = Left (webFile$, slash - 1) 
    webFile$ = Right (webFile$, Len (webFile$) - slash + 1) 
 Else 
    webHost$ = webFile$:webFile$ = "/" 
 EndIf 
 www = OpenTCPStream (webHost$, 80) 
 If www 
    WriteLine www, "GET " + webFile$ + " HTTP/1.1" 
    WriteLine www, "Host: " + webHost$ 
    WriteLine www, "User-Agent: BlitzWebFileExists" 
    WriteLine www, "Accept: */*" 
    WriteLine www, "" 
    header$ = ReadLine (www)
    If TextInString("404",Header$)=False
       Return True
    EndIf
    CloseTCPStream www
 Else
   Return False
 EndIf
End Function

; *************************************************

Function TextInString(t1$,t2$)
  ;Returns True IF t1$ is within the string t2$
  ;i.e. I=IsTextInString("he","hello") will return TRUE
  ; because 'he' is within 'hello'
  L1=Len(t1$):L2=Len(t2$)
  For p=1 To (L2-L1+1)
      s$=Mid$(t2$,p,L1)
      If s$=t1$
         Return True
      EndIf
  Next
End Function
