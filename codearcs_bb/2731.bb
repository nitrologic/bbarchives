; ID: 2731
; Author: Malice
; Date: 2010-06-17 05:50:22
; Title: UserAssist registry cypher
; Description: A means to decrypt UserAssisy registry values

;"HKEY_CURRENT_USER\Software\Microsoft\Windows\CurrentVersion\Explorer\UserAssist\"

a$="";Type Content Here!
For f=1 To Len(a$)
	xval=Asc(Mid(Upper(a$),f,1))
	If (xval>31) Then sReturn$=sReturn$+(Chr$(xval+(((xval>64) * (xval)<78)*13) - (((xval>77) * (xval<91))*13)))
Next
Print sReturn$

WaitKey()
End
