; ID: 2836
; Author: Mainsworthy
; Date: 2011-04-03 14:09:54
; Title: encrytion text readerwriter
; Description: encrypt and decrypt text files

'reader Manual code below Writer code at the end of reader code

Graphics 1024,768,32,60

Global store[4081*20]
Global page = 0
Global psy = 0
Global gfx = 20
Global ok = 1
Global sline = 0

Global ssave = LoadImage(".\grafix\back.PNG",flags=ALPHABITS  )
Global lload = LoadImage(".\grafix\forward.PNG",flags=ALPHABITS  )
Global pics = LoadAnimImage(".\userstuff\pics.PNG",350,695,0,gfx,flags=ALPHABITS  )
savedit = ReadStream( ".\userstuff\encryptedtext.txt" )


While Not KeyHit(KEY_ESCAPE)
Cls
DrawText "Use UP & DOWN Cursor keys or -", 670,715
DrawText "Forward & Back for fast scroll", 670,729
DrawText "Programed By Mark Ainsworth 210665", 670,745

If KeyHit(KEY_UP) Then sline = sline - 66
If KeyHit(KEY_DOWN) Then sline = sline + 66
If sline > (4080*20)-67 Then sline = 4079*20-66


DrawImage( ssave ,909+48,705,frame=1 )
DrawImage( lload ,909+48,736,frame=1 )

If MouseDown(1) And MouseX() > 957 And MouseX() < 957+45 And MouseY() > 705 And MouseY() < 735
#qwbnms
If MouseDown(1) Then Goto qwbnms
page = page - 1
If page < 0 Then page = 0
ok = 1
sline = 0
EndIf

If MouseDown(1) And MouseX() > 957 And MouseX() < 957+45 And MouseY() > 736 And MouseY() < 765
#dqwbnms
If MouseDown(1) Then Goto dqwbnms
page = page + 1
If page > 19 Then page = 19
ok = 1
sline = 0
EndIf

If ok = 1
If Not savedit 
Goto nofile
EndIf

bt = 0
crypta = 0
crypta2 = 3
For j = 0 To (19*4080) + 4079
If Not Eof(savedit) 
bt = ReadInt (savedit)
crypta = crypta + 1
If crypta = 16 Then crypta = 0
store[j] = bt - crypta - crypta2
EndIf

Next
#nofile 
ok = 0
EndIf


disppage()
DrawImage( pics,670 ,0,psy )





Flip
Wend
CloseStream savedit

End

Function disppage()
psy = 0
p = page * 4080 
p = p + sline

For v = 0 To 68
For cnt = 0 To 66
If cnt > 56 And store[p] = 84 Then store[p] = 100

If store[p] = 100 v = v + 1
If store[p] = 100 cnt = 0

If store[p] = 79 Then psy = psy + 1
'set psy maximum png files 0=1 1=2 2=3 so on
If psy > 19 Then psy = 19

If store[p] = 1  Then DrawText "a", cnt*10,v*10
If store[p] = 2  Then DrawText "b", cnt*10,v*10
If store[p] = 3  Then DrawText "c", cnt*10,v*10
If store[p] = 4  Then DrawText "d", cnt*10,v*10
If store[p] = 5  Then DrawText "e", cnt*10,v*10
If store[p] = 6  Then DrawText "f", cnt*10,v*10
If store[p] = 7  Then DrawText "g", cnt*10,v*10
If store[p] = 8  Then DrawText "h", cnt*10,v*10
If store[p] = 9  Then DrawText "i", cnt*10,v*10
If store[p] = 10  Then DrawText "j", cnt*10,v*10
If store[p] = 11  Then DrawText "k", cnt*10,v*10
If store[p] = 12  Then DrawText "l", cnt*10,v*10
If store[p] = 13  Then DrawText "m", cnt*10,v*10
If store[p] = 14  Then DrawText "n", cnt*10,v*10
If store[p] = 15  Then DrawText "o", cnt*10,v*10
If store[p] = 16  Then DrawText "p", cnt*10,v*10
If store[p] = 17  Then DrawText "q", cnt*10,v*10
If store[p] = 18  Then DrawText "r", cnt*10,v*10
If store[p] = 19  Then DrawText "s", cnt*10,v*10
If store[p] = 20  Then DrawText "t", cnt*10,v*10
If store[p] = 21  Then DrawText "u", cnt*10,v*10
If store[p] = 22  Then DrawText "v", cnt*10,v*10
If store[p] = 23  Then DrawText "w", cnt*10,v*10
If store[p] = 24  Then DrawText "x", cnt*10,v*10
If store[p] = 25  Then DrawText "y", cnt*10,v*10
If store[p] = 26  Then DrawText "z", cnt*10,v*10

If store[p] = 27  Then DrawText "A", cnt*10,v*10
If store[p] = 28  Then DrawText "B", cnt*10,v*10
If store[p] = 29  Then DrawText "C", cnt*10,v*10
If store[p] = 30  Then DrawText "D", cnt*10,v*10
If store[p] = 31  Then DrawText "E", cnt*10,v*10
If store[p] = 32  Then DrawText "F", cnt*10,v*10
If store[p] = 33  Then DrawText "G", cnt*10,v*10
If store[p] = 34  Then DrawText "H", cnt*10,v*10
If store[p] = 35  Then DrawText "I", cnt*10,v*10
If store[p] = 36  Then DrawText "J", cnt*10,v*10
If store[p] = 37  Then DrawText "K", cnt*10,v*10
If store[p] = 38  Then DrawText "L", cnt*10,v*10
If store[p] = 39  Then DrawText "M", cnt*10,v*10
If store[p] = 40  Then DrawText "N", cnt*10,v*10
If store[p] = 41  Then DrawText "O", cnt*10,v*10
If store[p] = 42  Then DrawText "P", cnt*10,v*10
If store[p] = 43  Then DrawText "Q", cnt*10,v*10
If store[p] = 44  Then DrawText "R", cnt*10,v*10
If store[p] = 45  Then DrawText "S", cnt*10,v*10
If store[p] = 46  Then DrawText "T", cnt*10,v*10
If store[p] = 47  Then DrawText "U", cnt*10,v*10
If store[p] = 48  Then DrawText "V", cnt*10,v*10
If store[p] = 49  Then DrawText "W", cnt*10,v*10
If store[p] = 50  Then DrawText "X", cnt*10,v*10
If store[p] = 51  Then DrawText "Y", cnt*10,v*10
If store[p] = 52  Then DrawText "Z", cnt*10,v*10

If store[p] = 53  Then DrawText "0", cnt*10,v*10
If store[p] = 54  Then DrawText "1", cnt*10,v*10
If store[p] = 55  Then DrawText "2", cnt*10,v*10
If store[p] = 56  Then DrawText "3", cnt*10,v*10
If store[p] = 57  Then DrawText "4", cnt*10,v*10
If store[p] = 58  Then DrawText "5", cnt*10,v*10
If store[p] = 59  Then DrawText "6", cnt*10,v*10
If store[p] = 60  Then DrawText "7", cnt*10,v*10
If store[p] = 61  Then DrawText "8", cnt*10,v*10
If store[p] = 62  Then DrawText "9", cnt*10,v*10
If store[p] = 63  Then DrawText "10", cnt*10,v*10

If store[p] = 64  Then DrawText "-", cnt*10,v*10
If store[p] = 65  Then DrawText "=", cnt*10,v*10
If store[p] = 66  Then DrawText "<", cnt*10,v*10
If store[p] = 67  Then DrawText ">", cnt*10,v*10
If store[p] = 68  Then DrawText ".", cnt*10,v*10
If store[p] = 69  Then DrawText ",", cnt*10,v*10
If store[p] = 70  Then DrawText "(", cnt*10,v*10
If store[p] = 71  Then DrawText ")", cnt*10,v*10
If store[p] = 72  Then DrawText "[", cnt*10,v*10
If store[p] = 73  Then DrawText "]", cnt*10,v*10
If store[p] = 74  Then DrawText "+", cnt*10,v*10
If store[p] = 75  Then DrawText "&", cnt*10,v*10
If store[p] = 76  Then DrawText "#", cnt*10,v*10
If store[p] = 77  Then DrawText ";", cnt*10,v*10
If store[p] = 78  Then DrawText ":", cnt*10,v*10
If store[p] = 79  Then DrawText "@", cnt*10,v*10
If store[p] = 80  Then DrawText "?", cnt*10,v*10
If store[p] = 81  Then DrawText "_", cnt*10,v*10
If store[p] = 82  Then DrawText "%", cnt*10,v*10
If store[p] = 83  Then DrawText "!", cnt*10,v*10
If store[p] = 84  Then DrawText " ", cnt*10,v*10
'If store[p] = 200 Then DrawText "\n", cnt*10,v*10
If store[p] = 0 Then DrawText " ", cnt*10,v*10

If v > 68
cnt = 66
v = 68
EndIf

p = p + 1
If p > (4080*20)-1 Then p = 4079*20
'If p > 4079 Then p = p - 1



Next
Next

End Function


'writer Manual
Graphics 1024,768,32,60

Global store[4081*20]
Global btg = 0
Global page = 0
Global p = 0
Global psy = 0
Global gfx = 20

While Not KeyHit(KEY_ESCAPE)
Cls
DrawText "Programed By Mark Ainsworth 210665", 670,745

#dqwbnms
savedit = ReadStream( ".\userstuff\unencryptedtext.txt" )

If Not savedit 
Goto nofile
EndIf

bt = 0
bt2 = 0
crypta = 0
crypta2 = 3
For jh = 0 To 19
For j = 0 To 4079

If Not Eof(savedit) Then bt2 = ReadByte (savedit)
bt = bt2
btg = 0

If bt = 97 Then btg = 1
If bt = 98 Then btg = 2
If bt = 99 Then btg = 3
If bt = 100 Then btg = 4
If bt = 101 Then btg = 5
If bt = 102 Then btg = 6
If bt = 103 Then btg = 7
If bt = 104 Then btg = 8
If bt = 105 Then btg = 9
If bt = 106 Then btg = 10
If bt = 107 Then btg = 11
If bt = 108 Then btg = 12
If bt = 109 Then btg = 13
If bt = 110 Then btg = 14
If bt = 111 Then btg = 15
If bt = 112 Then btg = 16
If bt = 113 Then btg = 17
If bt = 114 Then btg = 18
If bt = 115 Then btg = 19
If bt = 116 Then btg = 20
If bt = 117 Then btg = 21
If bt = 118 Then btg = 22
If bt = 119 Then btg = 23
If bt = 120 Then btg = 24
If bt = 121 Then btg = 25
If bt = 122 Then btg = 26

If bt = 65 Then btg = 27
If bt = 66 Then btg = 28
If bt = 67 Then btg = 29
If bt = 68 Then btg = 30
If bt = 69 Then btg = 31
If bt = 70 Then btg = 32
If bt = 71 Then btg = 33
If bt = 72 Then btg = 34
If bt = 73 Then btg = 35
If bt = 74 Then btg = 36
If bt = 75 Then btg = 37
If bt = 76 Then btg = 38
If bt = 77 Then btg = 39
If bt = 78 Then btg = 40
If bt = 79 Then btg = 41
If bt = 80 Then btg = 42
If bt = 81 Then btg = 43
If bt = 82 Then btg = 44
If bt = 83 Then btg = 45
If bt = 84 Then btg = 46
If bt = 85 Then btg = 47
If bt = 86 Then btg = 48
If bt = 87 Then btg = 49
If bt = 88 Then btg = 50
If bt = 89 Then btg = 51
If bt = 90 Then btg = 52

If bt = 48 Then btg = 53
If bt = 49  Then btg = 54
If bt = 50  Then btg = 55
If bt = 51  Then btg = 56
If bt = 52  Then btg = 57
If bt = 53  Then btg = 58
If bt = 54  Then btg = 59
If bt = 55  Then btg = 60
If bt = 56  Then btg = 61
If bt = 57  Then btg = 62
If bt = 58  Then btg = 63

If bt = 45  Then btg = 64
If bt = 61  Then btg = 65
If bt = 60  Then btg = 66
If bt = 62  Then btg = 67
If bt = 46  Then btg = 68
If bt = 44  Then btg = 69
If bt = 40  Then btg = 70
If bt = 41  Then btg = 71
If bt = 91  Then btg = 72
If bt = 93  Then btg = 73
If bt = 43  Then btg = 74
If bt = 38  Then btg = 75
If bt = 35  Then btg = 76
If bt = 59  Then btg = 77
If bt = 58  Then btg = 78
If bt = 64  Then btg = 79
If bt = 63  Then btg = 80
If bt = 95  Then btg = 81
If bt = 37  Then btg = 82
If bt = 33 Then btg = 83
If bt = 32 Then btg = 84
If bt = 13 Then btg = 100

bt = btg

If bt > 0 And bt < 85 store[j+(jh*4080)] = bt
If bt = 100 store[j+(jh*4080)] = bt
Next
Next
CloseStream savedit
#nofile 
#qwbnms
savedit = WriteStream( ".\userstuff\encryptedtext.txt" )

If Not savedit 
Goto nofile2
EndIf

bt = 0
crypta = 0
crypta2 = 3
For kl = 0 To 19
For j = 0 To 4079
bt = store[j+(kl*4080)]
crypta = crypta + 1
If crypta = 16 Then crypta = 0
bt = bt + crypta + crypta2

WriteInt (savedit, bt) 
Next
Next
FlushStream(savedit)
 CloseStream savedit 
#nofile2




DrawText "Press Escape Manual has been created", 10,10



Flip
Wend

Function disppage()
p = page * 4080

For v = 0 To 68
For cnt = 0 To 60
If store[p] = 90 v = v + 1
If store[p] = 90 cnt = 0



If store[p] = 1  Then DrawText "a", cnt*10,v*10
If store[p] = 2  Then DrawText "b", cnt*10,v*10
If store[p] = 3  Then DrawText "c", cnt*10,v*10
If store[p] = 4  Then DrawText "d", cnt*10,v*10
If store[p] = 5  Then DrawText "e", cnt*10,v*10
If store[p] = 6  Then DrawText "f", cnt*10,v*10
If store[p] = 7  Then DrawText "g", cnt*10,v*10
If store[p] = 8  Then DrawText "h", cnt*10,v*10
If store[p] = 9  Then DrawText "i", cnt*10,v*10
If store[p] = 10  Then DrawText "j", cnt*10,v*10
If store[p] = 11  Then DrawText "k", cnt*10,v*10
If store[p] = 12  Then DrawText "l", cnt*10,v*10
If store[p] = 13  Then DrawText "m", cnt*10,v*10
If store[p] = 14  Then DrawText "n", cnt*10,v*10
If store[p] = 15  Then DrawText "o", cnt*10,v*10
If store[p] = 16  Then DrawText "p", cnt*10,v*10
If store[p] = 17  Then DrawText "q", cnt*10,v*10
If store[p] = 18  Then DrawText "r", cnt*10,v*10
If store[p] = 19  Then DrawText "s", cnt*10,v*10
If store[p] = 20  Then DrawText "t", cnt*10,v*10
If store[p] = 21  Then DrawText "u", cnt*10,v*10
If store[p] = 22  Then DrawText "v", cnt*10,v*10
If store[p] = 23  Then DrawText "w", cnt*10,v*10
If store[p] = 24  Then DrawText "x", cnt*10,v*10
If store[p] = 25  Then DrawText "y", cnt*10,v*10
If store[p] = 26  Then DrawText "z", cnt*10,v*10

If store[p] = 27  Then DrawText "A", cnt*10,v*10
If store[p] = 28  Then DrawText "B", cnt*10,v*10
If store[p] = 29  Then DrawText "C", cnt*10,v*10
If store[p] = 30  Then DrawText "D", cnt*10,v*10
If store[p] = 31  Then DrawText "E", cnt*10,v*10
If store[p] = 32  Then DrawText "F", cnt*10,v*10
If store[p] = 33  Then DrawText "G", cnt*10,v*10
If store[p] = 34  Then DrawText "H", cnt*10,v*10
If store[p] = 35  Then DrawText "I", cnt*10,v*10
If store[p] = 36  Then DrawText "J", cnt*10,v*10
If store[p] = 37  Then DrawText "K", cnt*10,v*10
If store[p] = 38  Then DrawText "L", cnt*10,v*10
If store[p] = 39  Then DrawText "M", cnt*10,v*10
If store[p] = 40  Then DrawText "N", cnt*10,v*10
If store[p] = 41  Then DrawText "O", cnt*10,v*10
If store[p] = 42  Then DrawText "P", cnt*10,v*10
If store[p] = 43  Then DrawText "Q", cnt*10,v*10
If store[p] = 44  Then DrawText "R", cnt*10,v*10
If store[p] = 45  Then DrawText "S", cnt*10,v*10
If store[p] = 46  Then DrawText "T", cnt*10,v*10
If store[p] = 47  Then DrawText "U", cnt*10,v*10
If store[p] = 48  Then DrawText "V", cnt*10,v*10
If store[p] = 49  Then DrawText "W", cnt*10,v*10
If store[p] = 50  Then DrawText "X", cnt*10,v*10
If store[p] = 51  Then DrawText "Y", cnt*10,v*10
If store[p] = 52  Then DrawText "Z", cnt*10,v*10

If store[p] = 53  Then DrawText "0", cnt*10,v*10
If store[p] = 54  Then DrawText "1", cnt*10,v*10
If store[p] = 55  Then DrawText "2", cnt*10,v*10
If store[p] = 56  Then DrawText "3", cnt*10,v*10
If store[p] = 57  Then DrawText "4", cnt*10,v*10
If store[p] = 58  Then DrawText "5", cnt*10,v*10
If store[p] = 59  Then DrawText "6", cnt*10,v*10
If store[p] = 60  Then DrawText "7", cnt*10,v*10
If store[p] = 61  Then DrawText "8", cnt*10,v*10
If store[p] = 62  Then DrawText "9", cnt*10,v*10
If store[p] = 63  Then DrawText "10", cnt*10,v*10

If store[p] = 64  Then DrawText "-", cnt*10,v*10
If store[p] = 65  Then DrawText "=", cnt*10,v*10
If store[p] = 66  Then DrawText "<", cnt*10,v*10
If store[p] = 67  Then DrawText ">", cnt*10,v*10
If store[p] = 68  Then DrawText ".", cnt*10,v*10
If store[p] = 69  Then DrawText ",", cnt*10,v*10
If store[p] = 70  Then DrawText "(", cnt*10,v*10
If store[p] = 71  Then DrawText ")", cnt*10,v*10
If store[p] = 72  Then DrawText "[", cnt*10,v*10
If store[p] = 73  Then DrawText "]", cnt*10,v*10
If store[p] = 74  Then DrawText "+", cnt*10,v*10
If store[p] = 75  Then DrawText "&", cnt*10,v*10
If store[p] = 76  Then DrawText "#", cnt*10,v*10
If store[p] = 77  Then DrawText ";", cnt*10,v*10
If store[p] = 78  Then DrawText ":", cnt*10,v*10
If store[p] = 79  Then DrawText "@", cnt*10,v*10
If store[p] = 80  Then DrawText "?", cnt*10,v*10
If store[p] = 81  Then DrawText "_", cnt*10,v*10
If store[p] = 82  Then DrawText "%", cnt*10,v*10
If store[p] = 83  Then DrawText "!", cnt*10,v*10
If store[p] = 84  Then DrawText " ", cnt*10,v*10
If store[p] = 0  Then DrawText " ", cnt*10,v*10

p = p + 1
If p > 4079 Then p = p - 1





Next
Next

End Function

End
