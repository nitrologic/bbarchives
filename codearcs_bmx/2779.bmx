; ID: 2779
; Author: jankupila
; Date: 2010-10-15 06:26:56
; Title: Caesar's crypting -decrypting algorithm
; Description: Caesar's crypting -decrypting algorithm

Strict

Global message:String="Blitzmax programming is fun... Blitzmax programming is fun..."

Local length:Int = message.length


Local k:Int
Local key:Int=6
Local crypted:String=""
Local solved:String=""

' crypting
For k=1 To length  
	Local character_num=Asc(Mid(message,k,1))+key
	crypted=crypted+Chr(character_num)
Next

'decrypting
For k=1 To length
	Local character_num=Asc(Mid(crypted,k,1))-key
	solved=solved+Chr(character_num)
Next

Print message
Print crypted
Print solved
