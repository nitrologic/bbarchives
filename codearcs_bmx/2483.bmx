; ID: 2483
; Author: jankupila
; Date: 2009-05-21 02:23:06
; Title: Snow
; Description: Snow

Graphics 800,600

Type THiutale
Field x:Int
Field y:Int
End Type

Local hiutale_lukumaara:Int=12

Global lumilist:TList=CreateList()

While Not KeyDown(key_escape)
For Local m=1 To hiutale_lukumaara
Local Newhiutale:Thiutale
Newhiutale=New THiutale

newhiutale.x=Rand(10,790)
newhiutale.y=1
ListAddLast(lumilist,newhiutale)
Next

For Local hiutale:thiutale=EachIn lumilist
hiutale.x:+Rand(-1,1)
hiutale.y:+1
Plot (hiutale.x,hiutale.y)
If hiutale.y=500 Then lumilist.remove hiutale
DrawLine 1,500,800,500

Next
DrawText "Particles : "+lumilist.count(),20,20

Flip
Cls
Wend
