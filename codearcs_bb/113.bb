; ID: 113
; Author: Unknown
; Date: 2001-10-27 00:24:32
; Title: Custom Input Function (no bugs yet!)
; Description: Custom Input Function

; Input
Dim tdel(255)
Function inp$(strng$,max)
   If Len(strng$)<max
      If KeyDown(42)=0 And KeyDown(54)=0
         For t=2 To 10
            If KeyDown(t)=1 And tdel(t)=0 Then strng$=strng$+Str(t-1) : tdel(t)=15
            If KeyDown(t)=1 And tdel(t)=1 Then strng$=strng$+Str(t-1) : tdel(t)=3
         Next
         If KeyDown(11)=1 And tdel(11)=0 Then strng$=strng$+"0" : tdel(11)=15
         If KeyDown(11)=1 And tdel(11)=1 Then strng$=strng$+"0" : tdel(11)=3
      EndIf
      If KeyDown(42)=1 Or KeyDown(54)=1
      If KeyDown(2)=1 And tdel(2)=0 Then strng$=strng$+"!" : tdel(2)=15
      If KeyDown(3)=1 And tdel(3)=0 Then strng$=strng$+"@" : tdel(3)=15
      If KeyDown(4)=1 And tdel(4)=0 Then strng$=strng$+"#" : tdel(4)=15
      If KeyDown(5)=1 And tdel(5)=0 Then strng$=strng$+"$" : tdel(5)=15
      If KeyDown(6)=1 And tdel(6)=0 Then strng$=strng$+"%" : tdel(6)=15
      If KeyDown(7)=1 And tdel(7)=0 Then strng$=strng$+"^" : tdel(7)=15
      If KeyDown(8)=1 And tdel(8)=0 Then strng$=strng$+"&" : tdel(8)=15
      If KeyDown(9)=1 And tdel(9)=0 Then strng$=strng$+"*" : tdel(9)=15
      If KeyDown(10)=1 And tdel(10)=0 Then strng$=strng$+"(" : tdel(10)=15
      If KeyDown(11)=1 And tdel(11)=0 Then strng$=strng$+")" : tdel(11)=15
      If KeyDown(2)=1 And tdel(2)=1 Then strng$=strng$+"!" : tdel(2)=3
      If KeyDown(3)=1 And tdel(3)=1 Then strng$=strng$+"@" : tdel(3)=3
      If KeyDown(4)=1 And tdel(4)=1 Then strng$=strng$+"#" : tdel(4)=3
      If KeyDown(5)=1 And tdel(5)=1 Then strng$=strng$+"$" : tdel(5)=3
      If KeyDown(6)=1 And tdel(6)=1 Then strng$=strng$+"%" : tdel(6)=3
      If KeyDown(7)=1 And tdel(7)=1 Then strng$=strng$+"^" : tdel(7)=3
      If KeyDown(8)=1 And tdel(8)=1 Then strng$=strng$+"&" : tdel(8)=3
      If KeyDown(9)=1 And tdel(9)=1 Then strng$=strng$+"*" : tdel(9)=3
      If KeyDown(10)=1 And tdel(10)=1 Then strng$=strng$+"(" : tdel(10)=3
      If KeyDown(11)=1 And tdel(11)=1 Then strng$=strng$+")" : tdel(11)=3
      EndIf
      For t=2 To 11
         If KeyDown(t)=0 Then tdel(t)=0
      Next
      If KeyDown(82)=1 And tdel(82)=0 Then strng$=strng$+"0" : tdel(82)=15
      If KeyDown(79)=1 And tdel(79)=0 Then strng$=strng$+"1" : tdel(79)=15
      If KeyDown(80)=1 And tdel(80)=0 Then strng$=strng$+"2" : tdel(80)=15
      If KeyDown(81)=1 And tdel(81)=0 Then strng$=strng$+"3" : tdel(81)=15
      If KeyDown(75)=1 And tdel(75)=0 Then strng$=strng$+"4" : tdel(75)=15
      If KeyDown(76)=1 And tdel(76)=0 Then strng$=strng$+"5" : tdel(76)=15
      If KeyDown(77)=1 And tdel(77)=0 Then strng$=strng$+"6" : tdel(77)=15
      If KeyDown(71)=1 And tdel(71)=0 Then strng$=strng$+"7" : tdel(71)=15
      If KeyDown(72)=1 And tdel(72)=0 Then strng$=strng$+"8" : tdel(72)=15
      If KeyDown(73)=1 And tdel(73)=0 Then strng$=strng$+"9" : tdel(73)=15
      If KeyDown(181)=1 And tdel(181)=0 Then strng$=strng$+"/" : tdel(181)=15
      If KeyDown(55)=1 And tdel(55)=0 Then strng$=strng$+"*" : tdel(55)=15
      If KeyDown(74)=1 And tdel(74)=0 Then strng$=strng$+"-" : tdel(74)=15
      If KeyDown(78)=1 And tdel(78)=0 Then strng$=strng$+"+" : tdel(78)=15
      If KeyDown(83)=1 And tdel(83)=0 Then strng$=strng$+"+" : tdel(83)=15
      If KeyDown(82)=1 And tdel(82)=1 Then strng$=strng$+"0" : tdel(82)=3
      If KeyDown(79)=1 And tdel(79)=1 Then strng$=strng$+"1" : tdel(79)=3
      If KeyDown(80)=1 And tdel(80)=1 Then strng$=strng$+"2" : tdel(80)=3
      If KeyDown(81)=1 And tdel(81)=1 Then strng$=strng$+"3" : tdel(81)=3
      If KeyDown(75)=1 And tdel(75)=1 Then strng$=strng$+"4" : tdel(75)=3
      If KeyDown(76)=1 And tdel(76)=1 Then strng$=strng$+"5" : tdel(76)=3
      If KeyDown(77)=1 And tdel(77)=1 Then strng$=strng$+"6" : tdel(77)=3
      If KeyDown(71)=1 And tdel(71)=1 Then strng$=strng$+"7" : tdel(71)=3
      If KeyDown(72)=1 And tdel(72)=1 Then strng$=strng$+"8" : tdel(72)=3
      If KeyDown(73)=1 And tdel(73)=1 Then strng$=strng$+"9" : tdel(73)=3
      If KeyDown(181)=1 And tdel(181)=1 Then strng$=strng$+"/" : tdel(181)=3
      If KeyDown(55)=1 And tdel(55)=1 Then strng$=strng$+"*" : tdel(55)=3
      If KeyDown(74)=1 And tdel(74)=1 Then strng$=strng$+"-" : tdel(74)=3
      If KeyDown(78)=1 And tdel(78)=1 Then strng$=strng$+"+" : tdel(78)=3
      If KeyDown(83)=1 And tdel(83)=1 Then strng$=strng$+"." : tdel(83)=3
      If KeyDown(11)=0 Then tdel(11)=0
      If KeyDown(82)=0 Then tdel(82)=0
      If KeyDown(79)=0 Then tdel(79)=0
      If KeyDown(80)=0 Then tdel(80)=0
      If KeyDown(81)=0 Then tdel(81)=0
      If KeyDown(75)=0 Then tdel(75)=0
      If KeyDown(76)=0 Then tdel(76)=0
      If KeyDown(77)=0 Then tdel(77)=0
      If KeyDown(71)=0 Then tdel(71)=0
      If KeyDown(72)=0 Then tdel(72)=0
      If KeyDown(73)=0 Then tdel(73)=0
      If KeyDown(181)=0 Then tdel(181)=0
      If KeyDown(55)=0 Then tdel(55)=0
      If KeyDown(74)=0 Then tdel(74)=0
      If KeyDown(78)=0 Then tdel(78)=0
      If KeyDown(83)=0 Then tdel(83)=0
      If KeyDown(42)=0 And KeyDown(54)=0
         If KeyDown(16)=1 And tdel(16)=0 Then strng$=strng$+"q" : tdel(16)=15
         If KeyDown(17)=1 And tdel(17)=0 Then strng$=strng$+"w" : tdel(17)=15
         If KeyDown(18)=1 And tdel(18)=0 Then strng$=strng$+"e" : tdel(18)=15
         If KeyDown(19)=1 And tdel(19)=0 Then strng$=strng$+"r" : tdel(19)=15
         If KeyDown(20)=1 And tdel(20)=0 Then strng$=strng$+"t" : tdel(20)=15
         If KeyDown(21)=1 And tdel(21)=0 Then strng$=strng$+"y" : tdel(21)=15
         If KeyDown(22)=1 And tdel(22)=0 Then strng$=strng$+"u" : tdel(22)=15
         If KeyDown(23)=1 And tdel(23)=0 Then strng$=strng$+"i" : tdel(23)=15
         If KeyDown(24)=1 And tdel(24)=0 Then strng$=strng$+"o" : tdel(24)=15
         If KeyDown(25)=1 And tdel(25)=0 Then strng$=strng$+"p" : tdel(25)=15
         If KeyDown(30)=1 And tdel(30)=0 Then strng$=strng$+"a" : tdel(30)=15
         If KeyDown(31)=1 And tdel(31)=0 Then strng$=strng$+"s" : tdel(31)=15
         If KeyDown(32)=1 And tdel(32)=0 Then strng$=strng$+"d" : tdel(32)=15
         If KeyDown(33)=1 And tdel(33)=0 Then strng$=strng$+"f" : tdel(33)=15
         If KeyDown(34)=1 And tdel(34)=0 Then strng$=strng$+"g" : tdel(34)=15
         If KeyDown(35)=1 And tdel(35)=0 Then strng$=strng$+"h" : tdel(35)=15
         If KeyDown(36)=1 And tdel(36)=0 Then strng$=strng$+"j" : tdel(36)=15
         If KeyDown(37)=1 And tdel(37)=0 Then strng$=strng$+"k" : tdel(37)=15
         If KeyDown(38)=1 And tdel(38)=0 Then strng$=strng$+"l" : tdel(38)=15
         If KeyDown(44)=1 And tdel(44)=0 Then strng$=strng$+"z" : tdel(44)=15
         If KeyDown(45)=1 And tdel(45)=0 Then strng$=strng$+"x" : tdel(45)=15
         If KeyDown(46)=1 And tdel(46)=0 Then strng$=strng$+"c" : tdel(46)=15
         If KeyDown(47)=1 And tdel(47)=0 Then strng$=strng$+"v" : tdel(47)=15
         If KeyDown(48)=1 And tdel(48)=0 Then strng$=strng$+"b" : tdel(48)=15
         If KeyDown(49)=1 And tdel(49)=0 Then strng$=strng$+"n" : tdel(49)=15
         If KeyDown(50)=1 And tdel(50)=0 Then strng$=strng$+"m" : tdel(50)=15
         If KeyDown(16)=1 And tdel(16)=1 Then strng$=strng$+"q" : tdel(16)=3
         If KeyDown(17)=1 And tdel(17)=1 Then strng$=strng$+"w" : tdel(17)=3
         If KeyDown(18)=1 And tdel(18)=1 Then strng$=strng$+"e" : tdel(18)=3
         If KeyDown(19)=1 And tdel(19)=1 Then strng$=strng$+"r" : tdel(19)=3
         If KeyDown(20)=1 And tdel(20)=1 Then strng$=strng$+"t" : tdel(20)=3
         If KeyDown(21)=1 And tdel(21)=1 Then strng$=strng$+"y" : tdel(21)=3
         If KeyDown(22)=1 And tdel(22)=1 Then strng$=strng$+"u" : tdel(22)=3
         If KeyDown(23)=1 And tdel(23)=1 Then strng$=strng$+"i" : tdel(23)=3
         If KeyDown(24)=1 And tdel(24)=1 Then strng$=strng$+"o" : tdel(24)=3
         If KeyDown(25)=1 And tdel(25)=1 Then strng$=strng$+"p" : tdel(25)=3
         If KeyDown(30)=1 And tdel(30)=1 Then strng$=strng$+"a" : tdel(30)=3
         If KeyDown(31)=1 And tdel(31)=1 Then strng$=strng$+"s" : tdel(31)=3
         If KeyDown(32)=1 And tdel(32)=1 Then strng$=strng$+"d" : tdel(32)=3
         If KeyDown(33)=1 And tdel(33)=1 Then strng$=strng$+"f" : tdel(33)=3
         If KeyDown(34)=1 And tdel(34)=1 Then strng$=strng$+"g" : tdel(34)=3
         If KeyDown(35)=1 And tdel(35)=1 Then strng$=strng$+"h" : tdel(35)=3
         If KeyDown(36)=1 And tdel(36)=1 Then strng$=strng$+"j" : tdel(36)=3
         If KeyDown(37)=1 And tdel(37)=1 Then strng$=strng$+"k" : tdel(37)=3
         If KeyDown(38)=1 And tdel(38)=1 Then strng$=strng$+"l" : tdel(38)=3
         If KeyDown(44)=1 And tdel(44)=1 Then strng$=strng$+"z" : tdel(44)=3
         If KeyDown(45)=1 And tdel(45)=1 Then strng$=strng$+"x" : tdel(45)=3
         If KeyDown(46)=1 And tdel(46)=1 Then strng$=strng$+"c" : tdel(46)=3
         If KeyDown(47)=1 And tdel(47)=1 Then strng$=strng$+"v" : tdel(47)=3
         If KeyDown(48)=1 And tdel(48)=1 Then strng$=strng$+"b" : tdel(48)=3
         If KeyDown(49)=1 And tdel(49)=1 Then strng$=strng$+"n" : tdel(49)=3
         If KeyDown(50)=1 And tdel(50)=1 Then strng$=strng$+"m" : tdel(50)=3
      EndIf
      If KeyDown(42)=1 Or KeyDown(54)=1
         If KeyDown(16)=1 And tdel(16)=0 Then strng$=strng$+"Q" : tdel(16)=15
         If KeyDown(17)=1 And tdel(17)=0 Then strng$=strng$+"W" : tdel(17)=15
         If KeyDown(18)=1 And tdel(18)=0 Then strng$=strng$+"E" : tdel(18)=15
         If KeyDown(19)=1 And tdel(19)=0 Then strng$=strng$+"R" : tdel(19)=15
         If KeyDown(20)=1 And tdel(20)=0 Then strng$=strng$+"T" : tdel(20)=15
         If KeyDown(21)=1 And tdel(21)=0 Then strng$=strng$+"Y" : tdel(21)=15
         If KeyDown(22)=1 And tdel(22)=0 Then strng$=strng$+"U" : tdel(22)=15
         If KeyDown(23)=1 And tdel(23)=0 Then strng$=strng$+"I" : tdel(23)=15
         If KeyDown(24)=1 And tdel(24)=0 Then strng$=strng$+"O" : tdel(24)=15
         If KeyDown(25)=1 And tdel(25)=0 Then strng$=strng$+"P" : tdel(25)=15
         If KeyDown(30)=1 And tdel(30)=0 Then strng$=strng$+"A" : tdel(30)=15
         If KeyDown(31)=1 And tdel(31)=0 Then strng$=strng$+"S" : tdel(31)=15
         If KeyDown(32)=1 And tdel(32)=0 Then strng$=strng$+"D" : tdel(32)=15
         If KeyDown(33)=1 And tdel(33)=0 Then strng$=strng$+"F" : tdel(33)=15
         If KeyDown(34)=1 And tdel(34)=0 Then strng$=strng$+"G" : tdel(34)=15
         If KeyDown(35)=1 And tdel(35)=0 Then strng$=strng$+"H" : tdel(35)=15
         If KeyDown(36)=1 And tdel(36)=0 Then strng$=strng$+"J" : tdel(36)=15
         If KeyDown(37)=1 And tdel(37)=0 Then strng$=strng$+"K" : tdel(37)=15
         If KeyDown(38)=1 And tdel(38)=0 Then strng$=strng$+"L" : tdel(38)=15
         If KeyDown(44)=1 And tdel(44)=0 Then strng$=strng$+"Z" : tdel(44)=15
         If KeyDown(45)=1 And tdel(45)=0 Then strng$=strng$+"X" : tdel(45)=15
         If KeyDown(46)=1 And tdel(46)=0 Then strng$=strng$+"C" : tdel(46)=15
         If KeyDown(47)=1 And tdel(47)=0 Then strng$=strng$+"V" : tdel(47)=15
         If KeyDown(48)=1 And tdel(48)=0 Then strng$=strng$+"B" : tdel(48)=15
         If KeyDown(49)=1 And tdel(49)=0 Then strng$=strng$+"N" : tdel(49)=15
         If KeyDown(50)=1 And tdel(50)=0 Then strng$=strng$+"M" : tdel(50)=15
         If KeyDown(16)=1 And tdel(16)=1 Then strng$=strng$+"Q" : tdel(16)=3
         If KeyDown(17)=1 And tdel(17)=1 Then strng$=strng$+"W" : tdel(17)=3
         If KeyDown(18)=1 And tdel(18)=1 Then strng$=strng$+"E" : tdel(18)=3
         If KeyDown(19)=1 And tdel(19)=1 Then strng$=strng$+"R" : tdel(19)=3
         If KeyDown(20)=1 And tdel(20)=1 Then strng$=strng$+"T" : tdel(20)=3
         If KeyDown(21)=1 And tdel(21)=1 Then strng$=strng$+"Y" : tdel(21)=3
         If KeyDown(22)=1 And tdel(22)=1 Then strng$=strng$+"U" : tdel(22)=3
         If KeyDown(23)=1 And tdel(23)=1 Then strng$=strng$+"I" : tdel(23)=3
         If KeyDown(24)=1 And tdel(24)=1 Then strng$=strng$+"O" : tdel(24)=3
         If KeyDown(25)=1 And tdel(25)=1 Then strng$=strng$+"P" : tdel(25)=3
         If KeyDown(30)=1 And tdel(30)=1 Then strng$=strng$+"A" : tdel(30)=3
         If KeyDown(31)=1 And tdel(31)=1 Then strng$=strng$+"S" : tdel(31)=3
         If KeyDown(32)=1 And tdel(32)=1 Then strng$=strng$+"D" : tdel(32)=3
         If KeyDown(33)=1 And tdel(33)=1 Then strng$=strng$+"F" : tdel(33)=3
         If KeyDown(34)=1 And tdel(34)=1 Then strng$=strng$+"G" : tdel(34)=3
         If KeyDown(35)=1 And tdel(35)=1 Then strng$=strng$+"H" : tdel(35)=3
         If KeyDown(36)=1 And tdel(36)=1 Then strng$=strng$+"J" : tdel(36)=3
         If KeyDown(37)=1 And tdel(37)=1 Then strng$=strng$+"K" : tdel(37)=3
         If KeyDown(38)=1 And tdel(38)=1 Then strng$=strng$+"L" : tdel(38)=3
         If KeyDown(44)=1 And tdel(44)=1 Then strng$=strng$+"Z" : tdel(44)=3
         If KeyDown(45)=1 And tdel(45)=1 Then strng$=strng$+"X" : tdel(45)=3
         If KeyDown(46)=1 And tdel(46)=1 Then strng$=strng$+"C" : tdel(46)=3
         If KeyDown(47)=1 And tdel(47)=1 Then strng$=strng$+"V" : tdel(47)=3
         If KeyDown(48)=1 And tdel(48)=1 Then strng$=strng$+"B" : tdel(48)=3
         If KeyDown(49)=1 And tdel(49)=1 Then strng$=strng$+"N" : tdel(49)=3
         If KeyDown(50)=1 And tdel(50)=1 Then strng$=strng$+"M" : tdel(50)=3
      EndIf
      For t=16 To 25
         If KeyDown(t)=0 Then tdel(t)=0
      Next
      For t=30 To 38
         If KeyDown(t)=0 Then tdel(t)=0
      Next
      For t=44 To 50
         If KeyDown(t)=0 Then tdel(t)=0
      Next
      If KeyDown(42)=0 And KeyDown(54)=0
         If KeyDown(12)=1 And tdel(12)=0 Then strng$=strng$+"-" : tdel(12)=15
         If KeyDown(13)=1 And tdel(13)=0 Then strng$=strng$+"=" : tdel(13)=15
         If KeyDown(26)=1 And tdel(26)=0 Then strng$=strng$+"[" : tdel(26)=15
         If KeyDown(27)=1 And tdel(27)=0 Then strng$=strng$+"]" : tdel(27)=15
         If KeyDown(39)=1 And tdel(39)=0 Then strng$=strng$+";" : tdel(39)=15
         If KeyDown(40)=1 And tdel(40)=0 Then strng$=strng$+"'" : tdel(40)=15
         If KeyDown(51)=1 And tdel(51)=0 Then strng$=strng$+"," : tdel(51)=15
         If KeyDown(52)=1 And tdel(52)=0 Then strng$=strng$+"." : tdel(52)=15
         If KeyDown(43)=1 And tdel(43)=0 Then strng$=strng$+"\" : tdel(43)=15
         If KeyDown(53)=1 And tdel(53)=0 Then strng$=strng$+"/" : tdel(53)=15
         If KeyDown(41)=1 And tdel(41)=0 Then strng$=strng$+"`" : tdel(41)=15
         If KeyDown(12)=1 And tdel(12)=1 Then strng$=strng$+"-" : tdel(12)=3
         If KeyDown(13)=1 And tdel(13)=1 Then strng$=strng$+"=" : tdel(13)=3
         If KeyDown(26)=1 And tdel(26)=1 Then strng$=strng$+"[" : tdel(26)=3
         If KeyDown(27)=1 And tdel(27)=1 Then strng$=strng$+"]" : tdel(27)=3
         If KeyDown(39)=1 And tdel(39)=1 Then strng$=strng$+";" : tdel(39)=3
         If KeyDown(40)=1 And tdel(40)=1 Then strng$=strng$+"'" : tdel(40)=3
         If KeyDown(51)=1 And tdel(51)=1 Then strng$=strng$+"," : tdel(51)=3
         If KeyDown(52)=1 And tdel(52)=1 Then strng$=strng$+"." : tdel(52)=3
         If KeyDown(43)=1 And tdel(43)=1 Then strng$=strng$+"\" : tdel(43)=3
         If KeyDown(53)=1 And tdel(53)=1 Then strng$=strng$+"/" : tdel(53)=3
         If KeyDown(41)=1 And tdel(41)=1 Then strng$=strng$+"`" : tdel(41)=3
      EndIf
      If KeyDown(42)=1 Or KeyDown(54)=1
         If KeyDown(12)=1 And tdel(12)=0 Then strng$=strng$+"_" : tdel(12)=15
         If KeyDown(13)=1 And tdel(13)=0 Then strng$=strng$+"+" : tdel(13)=15
         If KeyDown(26)=1 And tdel(26)=0 Then strng$=strng$+"{" : tdel(26)=15
         If KeyDown(27)=1 And tdel(27)=0 Then strng$=strng$+"}" : tdel(27)=15
         If KeyDown(39)=1 And tdel(39)=0 Then strng$=strng$+":" : tdel(39)=15
         If KeyDown(51)=1 And tdel(51)=0 Then strng$=strng$+"<" : tdel(51)=15
         If KeyDown(52)=1 And tdel(52)=0 Then strng$=strng$+">" : tdel(52)=15
         If KeyDown(43)=1 And tdel(43)=0 Then strng$=strng$+"|" : tdel(43)=15
         If KeyDown(53)=1 And tdel(53)=0 Then strng$=strng$+"?" : tdel(53)=15
         If KeyDown(41)=1 And tdel(41)=0 Then strng$=strng$+"~" : tdel(41)=15
         If KeyDown(12)=1 And tdel(12)=1 Then strng$=strng$+"_" : tdel(12)=3
         If KeyDown(13)=1 And tdel(13)=1 Then strng$=strng$+"+" : tdel(13)=3
         If KeyDown(26)=1 And tdel(26)=1 Then strng$=strng$+"{" : tdel(26)=3
         If KeyDown(27)=1 And tdel(27)=1 Then strng$=strng$+"}" : tdel(27)=3
         If KeyDown(39)=1 And tdel(39)=1 Then strng$=strng$+":" : tdel(39)=3
         If KeyDown(51)=1 And tdel(51)=1 Then strng$=strng$+"<" : tdel(51)=3
         If KeyDown(52)=1 And tdel(52)=1 Then strng$=strng$+">" : tdel(52)=3
         If KeyDown(43)=1 And tdel(43)=1 Then strng$=strng$+"|" : tdel(43)=3
         If KeyDown(53)=1 And tdel(53)=1 Then strng$=strng$+"?" : tdel(53)=3
         If KeyDown(41)=1 And tdel(41)=1 Then strng$=strng$+"~" : tdel(41)=3
      EndIf
      If KeyDown(12)=0 Then tdel(12)=0
      If KeyDown(13)=0 Then tdel(13)=0
      If KeyDown(26)=0 Then tdel(26)=0
      If KeyDown(27)=0 Then tdel(27)=0
      If KeyDown(39)=0 Then tdel(39)=0
      If KeyDown(40)=0 Then tdel(40)=0
      If KeyDown(51)=0 Then tdel(51)=0
      If KeyDown(52)=0 Then tdel(52)=0
      If KeyDown(43)=0 Then tdel(43)=0
      If KeyDown(53)=0 Then tdel(53)=0
      If KeyDown(41)=0 Then tdel(41)=0
      If KeyDown(57)=1 And tdel(57)=0 Then strng$=strng$+" " : tdel(57)=15
      If KeyDown(57)=1 And tdel(57)=1 Then strng$=strng$+" " : tdel(57)=3
      If KeyDown(57)=0 Then tdel(57)=0
   EndIf
   If KeyDown(14)=1 And tdel(14)=0 Then strng$=Left(strng$,Len(strng$)-1) : tdel(14)=15
   If KeyDown(14)=1 And tdel(14)=1 Then strng$=Left(strng$,Len(strng$)-1) : tdel(14)=3
   If KeyDown(14)=0 Then tdel(14)=0
   For t=0 To 255
      If tdel(t)>1 Then tdel(t)=tdel(t)-1
   Next
   Return strng$
End Function
