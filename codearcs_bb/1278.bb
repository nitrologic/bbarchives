; ID: 1278
; Author: jfk EO-11110
; Date: 2005-02-04 20:38:00
; Title: HTTP Post
; Description: How to Post Data to a Webserver

; The following Code can be used to send Strings to a Webserver using the POST Method, 
; eg. to a PHP Script that will save then in a MySQL Database, Hiscore list or whatever.
; This example works with the HTML Form element "input", that is a single line
; text input field. All other form elements are possible too, also Upload of files, tho
; the request may look slightly diffrent.

; To find out how exactly your Request has to be structured it may be the best idea to
; write a HTML Form that does the same thing you want to code in Blitz, then run a little Blitz
; TCP Server that will dump any Request to a textfile. Now you can run the Webpage Form in
; a browser, and Blitz will "monitor" what it sends to the server. You need to use "127.0.0.1"
; as the host here. Now you can investigate the dumped Request Structure easlily.

; There is one thing that took me some time until I have tracked it down: After the last 
; boundary line you need to add "--".

; Of course, there must be a Script on the Webserver that will process your Data. It may be
; a good idea to make the script work for browser access until you start trying to access it 
; with Blitz. (BTW. I recommend to use EasyPHP as a locally installed Webserver to develop
; PHP Scripts. In this case you can also unplug the Internet).


Graphics 1000,600,32,2 ; (to make sure we can read the servers answer, in case of an error msg)


Dim post$(100)

; reentrant part:

; assembling client packet:
target_host$="127.0.0.1"
target_port=80
target_script$="/hiscores/check_hiscore.php"


; creating a random boundary string
bound_legal$="yxcvbnmasdfghjklqwertzuiopYXCVBNMASDFGHJKLQWERTZUIOP0123456789"
boundary$="------------"
SeedRnd(MilliSecs())
For i=0 To 21
 boundary$=boundary$+Mid$(bound_legal$,Rand(1,Len(bound_legal$)),1)
Next


post$(0)="POST "+target_script$+" HTTP/1.1"
post$(1)="User-Agent: BlitzBasic Client V0.1"
post$(2)="Host: "+target_host$
post$(3)="Accept: text/html, image/png, image/jpeg, image/gif, image/x-xbitmap, */*"
post$(4)="Accept-Language: de, en"
post$(5)="Accept-Charset: windows-1252;q=1.0, utf-8;q=1.0, utf-16;q=1.0, iso-8859-1;q=0.6, *;q=0.1"
post$(6)="Accept-Encoding: deflate, gzip, x-gzip, identity, *;q=0"
post$(7)="Referer: http://www.disneyland.com"
post$(8)="Connection: Keep-Alive, TE"
post$(9)="TE: deflate, gzip, chunked, identity, trailers"
post$(10)="Content-length: ?" ; this will be set at the end of the content definition
post$(11)="Content-Type: multipart/form-data; boundary="+Right$(boundary$,Len(boundary$)-2)
post$(12)=""
post$(13)=boundary$
post$(14)="Content-Disposition: form-data; name="+Chr$(34)+"M_Nick"+Chr$(34)
post$(15)=""
post$(16)="jfk"
post$(17)=boundary$
post$(18)="Content-Disposition: form-data; name="+Chr$(34)+"M_Score"+Chr$(34)
post$(19)=""
post$(20)="77770"
post$(21)=boundary$
post$(22)="Content-Disposition: form-data; name="+Chr$(34)+"M_Comment"+Chr$(34)
post$(23)=""
post$(24)="i am the king! well - maybe..."
post$(25)=boundary$
post$(26)="Content-Disposition: form-data; name="+Chr$(34)+"M_Name"+Chr$(34)
post$(27)=""
post$(28)="john kennedy"
post$(29)=boundary$
post$(30)="Content-Disposition: form-data; name="+Chr$(34)+"M_Street"+Chr$(34)
post$(31)=""
post$(32)="wallstreet 123"
post$(33)=boundary$
post$(34)="Content-Disposition: form-data; name="+Chr$(34)+"M_Town"+Chr$(34)
post$(35)=""
post$(36)="1234567 Nirvana USA"
post$(37)=boundary$
post$(38)="Content-Disposition: form-data; name="+Chr$(34)+"M_Email"+Chr$(34)
post$(39)=""
post$(40)="jonny@whitehouse.gov"
post$(41)=boundary$

lastline=41

post$(lastline+1)=""
post$(lastline+2)=""

; calulate total content lenght:
clen=0
For i=12 To lastline
 clen=clen+Len(post$(i))+2
Next

post$(10)="Content-length: "+clen



; ----------send it ---------------

strmGame=OpenTCPStream(target_host$,target_port)

If strmGame<>0 Then 
 ; send request
 For i=0 To lastline-1
  WriteLine strmGame,post$(i)
  Delay 1
 Next
 WriteLine strmGame,post$(lastline)+"--"
 WriteLine strmGame,post$(lastline+1)
 WriteLine strmGame,post$(lastline+2)

 ; read the servers answer...
 While Eof(strmGame)=0
  rln$=ReadLine(strmGame)
  Delay 1
  Print rln$
 Wend
 Print "successfully received data from sever"
 WaitKey()
Else
 Print "Server failed to connect."
 WaitKey 
End If

End
