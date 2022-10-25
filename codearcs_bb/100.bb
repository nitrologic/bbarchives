; ID: 100
; Author: Cpt. Sovok
; Date: 2001-12-07 14:20:48
; Title: FTP in a nutshell
; Description: A minimalistic FTP Proggy in 8 lines.

ftp$ = Input("Adress>")
com = OpenTCPStream(ftp$,21)
Repeat
 l$ = ReadLine$(com)
 Print l$
 i$ = Input(">")
 WriteLine com, i$
Until i$ = "quit"
