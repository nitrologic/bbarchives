; ID: 19
; Author: Rob Hutchinson
; Date: 2001-08-18 01:17:02
; Title: Keycode scanning...
; Description: Keycode scanning... (Advanced)

Graphics 640,480,16
SetBuffer BackBuffer()

;Include "ELAKeyCodeSource.bb"   ; Dont forget to include :)


; ////- START INCLUDE -////
;------------------------------------

;
; Elaee Graphical User Interface KeyCode Module: Written By Robert Hutchinson 2000-2001.
; Copyright Robert Hutchinson 2000-2001.
;
; REQUIRES BLITZ BASIC VERSION 1.44 OR HIGHER!
; ELAGUI VERSION: 0.71
;
; Ah, full version due for release later!..
;

; +---------------------------------------------------------------------------------------------+
; ¦ SCANCODE CORE - Codes for keypresses.                                                       ¦
; +---------------------------------------------------------------------------------------------+

Const ELA_SCANCODE_TYPEID_ESCAPE       = 1
Const ELA_SCANCODE_TYPEID_BACKSPACE    = 2
Const ELA_SCANCODE_TYPEID_TAB          = 3
Const ELA_SCANCODE_TYPEID_RETURN       = 4
Const ELA_SCANCODE_TYPEID_LCTRL        = 5
Const ELA_SCANCODE_TYPEID_RCTRL        = 6
Const ELA_SCANCODE_TYPEID_LSHIFT       = 7
Const ELA_SCANCODE_TYPEID_RSHIFT       = 8
Const ELA_SCANCODE_TYPEID_LALT         = 9
Const ELA_SCANCODE_TYPEID_LWINKEY      = 10
Const ELA_SCANCODE_TYPEID_NUMLOCK      = 11
Const ELA_SCANCODE_TYPEID_CAPSLOCK     = 12
Const ELA_SCANCODE_TYPEID_SCROLLLOCK   = 13
Const ELA_SCANCODE_TYPEID_LMENU        = 15
Const ELA_SCANCODE_TYPEID_F1           = 16
Const ELA_SCANCODE_TYPEID_F2           = 17
Const ELA_SCANCODE_TYPEID_F3           = 18
Const ELA_SCANCODE_TYPEID_F4           = 19
Const ELA_SCANCODE_TYPEID_F5           = 20
Const ELA_SCANCODE_TYPEID_F6           = 21
Const ELA_SCANCODE_TYPEID_F7           = 22
Const ELA_SCANCODE_TYPEID_F8           = 23
Const ELA_SCANCODE_TYPEID_F9           = 24
Const ELA_SCANCODE_TYPEID_F10          = 25
Const ELA_SCANCODE_TYPEID_F11          = 26
Const ELA_SCANCODE_TYPEID_F12          = 27
Const ELA_SCANCODE_TYPEID_F13          = 28
Const ELA_SCANCODE_TYPEID_F14          = 29
Const ELA_SCANCODE_TYPEID_F15          = 30
Const ELA_SCANCODE_TYPEID_KANA         = 40
Const ELA_SCANCODE_TYPEID_ABNT_C1      = 41
Const ELA_SCANCODE_TYPEID_CONVERT      = 42
Const ELA_SCANCODE_TYPEID_NOCONVERT    = 43
Const ELA_SCANCODE_TYPEID_YEN          = 44
Const ELA_SCANCODE_TYPEID_ABNT_C2      = 45
Const ELA_SCANCODE_TYPEID_NUMPADEQUALS = 46
Const ELA_SCANCODE_TYPEID_PREVTRACK    = 47
Const ELA_SCANCODE_TYPEID_AT           = 48
Const ELA_SCANCODE_TYPEID_COLON        = 49
Const ELA_SCANCODE_TYPEID_UNDERLINE    = 50
Const ELA_SCANCODE_TYPEID_KANJI        = 51
Const ELA_SCANCODE_TYPEID_STOP         = 52
Const ELA_SCANCODE_TYPEID_AX           = 53
Const ELA_SCANCODE_TYPEID_UNLABELED    = 54
Const ELA_SCANCODE_TYPEID_NEXTTRACK    = 55
Const ELA_SCANCODE_TYPEID_NUMPADENTER  = 56
Const ELA_SCANCODE_TYPEID_MUTE         = 58
Const ELA_SCANCODE_TYPEID_CALCULATOR   = 59
Const ELA_SCANCODE_TYPEID_PLAYPAUSE    = 60
Const ELA_SCANCODE_TYPEID_MEDIASTOP    = 61
Const ELA_SCANCODE_TYPEID_VOLUMEDOWN   = 62
Const ELA_SCANCODE_TYPEID_VOLUMEUP     = 63
Const ELA_SCANCODE_TYPEID_WEBHOME      = 64
Const ELA_SCANCODE_TYPEID_NUMPADCOMMA  = 65
Const ELA_SCANCODE_TYPEID_DIVIDE       = 66
Const ELA_SCANCODE_TYPEID_SYSRQ        = 67
Const ELA_SCANCODE_TYPEID_RMENU        = 68
Const ELA_SCANCODE_TYPEID_PAUSE        = 69
Const ELA_SCANCODE_TYPEID_HOME         = 70
Const ELA_SCANCODE_TYPEID_UP           = 71
Const ELA_SCANCODE_TYPEID_PRIOR        = 72
Const ELA_SCANCODE_TYPEID_LEFT         = 73
Const ELA_SCANCODE_TYPEID_RIGHT        = 74
Const ELA_SCANCODE_TYPEID_END          = 75
Const ELA_SCANCODE_TYPEID_DOWN         = 76
Const ELA_SCANCODE_TYPEID_NEXT         = 77
Const ELA_SCANCODE_TYPEID_INSERT       = 78
Const ELA_SCANCODE_TYPEID_DELETE       = 79
Const ELA_SCANCODE_TYPEID_LWIN         = 80
Const ELA_SCANCODE_TYPEID_RWIN         = 81
Const ELA_SCANCODE_TYPEID_APPS         = 82
Const ELA_SCANCODE_TYPEID_POWER        = 83
Const ELA_SCANCODE_TYPEID_SLEEP        = 84
Const ELA_SCANCODE_TYPEID_WAKE         = 85
Const ELA_SCANCODE_TYPEID_WEBSEARCH    = 86
Const ELA_SCANCODE_TYPEID_WEBFAVORITES = 87
Const ELA_SCANCODE_TYPEID_WEBREFRESH   = 88
Const ELA_SCANCODE_TYPEID_WEBSTOP      = 89
Const ELA_SCANCODE_TYPEID_WEBFORWARD   = 90
Const ELA_SCANCODE_TYPEID_WEBBACK      = 91
Const ELA_SCANCODE_TYPEID_MYCOMPUTER   = 92
Const ELA_SCANCODE_TYPEID_MAIL         = 93
Const ELA_SCANCODE_TYPEID_MEDIASELECT  = 94
Const ELA_SCANCODE_TYPEID_NUMPAD7      = 95
Const ELA_SCANCODE_TYPEID_NUMPAD8      = 96
Const ELA_SCANCODE_TYPEID_NUMPAD9      = 97
Const ELA_SCANCODE_TYPEID_NUMPAD4      = 98
Const ELA_SCANCODE_TYPEID_NUMPAD5      = 99
Const ELA_SCANCODE_TYPEID_NUMPAD6      = 100
Const ELA_SCANCODE_TYPEID_NUMPAD1      = 101
Const ELA_SCANCODE_TYPEID_NUMPAD2      = 102
Const ELA_SCANCODE_TYPEID_NUMPAD3      = 103
Const ELA_SCANCODE_TYPEID_NUMPAD0      = 104
Const ELA_SCANCODE_TYPEID_OEM102       = 105

Type elaKeyCode
	Field typeid              ; This is the ID of this keycode, IDs are as above.
	Field alpha$              ; This is the character string of the key.
	Field ascii               ; This is the ASCII representation of the key.
	Field seckey.elaKeyCode   ; This is the secondary key (upper level key).
	Field terkey.elaKeyCode   ; This is the tertiary key.
End Type

Function ELACreateKeyCode.elaKeyCode(typeid,alpha$,ascii,seckey.elaKeyCode,terkey.elaKeyCode)
	tempKeyCode.elaKeyCode = New elaKeyCode
	tempKeyCode\typeid     = typeid
	tempKeyCode\alpha$     = alpha$
	tempKeyCode\ascii      = ascii
	tempKeyCode\seckey     = seckey
	tempKeyCode\terkey     = terkey
	Return tempKeyCode
End Function

Global keycodeDelay        = 200
Global keycodeNextKeyDelay = 10
Global keycodeTiming       = 0
Global keycodeTimingNext   = 0
Dim    keycodeBase.elaKeyCode(305)
Dim    keycodeValid(255)
Global keycodeLastKeyDown  = 0
Global keycodeLastKeyHit   = 0
Global keycodeValidTo      = 0
Global keycodeCapsLockOn   = False

Function ELASetKeyDelay(millidelay)
	keycodeDelay = millidelay
End Function

Function ELASetNextKeyDelay(millidelay)
	keycodeNextKeyDelay = millidelay
End Function

Function ELASetSpacesPerTab(noofspaces)
	keycodeBase(15)\alpha = String(" ",noofspaces)
End Function

Function ELACapsLockStatus(truefalse)
	keycodeCapsLockOn   = truefalse
End Function

Function ELALastKeyCodeDown.elaKeyCode()
	Return keycodeBase(keycodeLastKeyDown)
End Function

Function ELALastScanCodeDown()
	Return keycodeLastKeyDown
End Function

Function ELALastKeyCodeHit.elaKeyCode()
	Return keycodeBase(keycodeLastKeyHit)
End Function

Function ELALastScanCodeHit()
	Return keycodeLastKeyHit
End Function

keycodeBase(256) = ELACreateKeyCode(0,"!",Asc("!"),Null,Null)
keycodeBase(257) = ELACreateKeyCode(0,Chr(34),34  ,Null,Null)    ; "
keycodeBase(258) = ELACreateKeyCode(0,"£",Asc("£"),Null,Null)
keycodeBase(259) = ELACreateKeyCode(0,"$",Asc("$"),Null,Null)
keycodeBase(260) = ELACreateKeyCode(0,"%",Asc("%"),Null,Null)
keycodeBase(261) = ELACreateKeyCode(0,"^",Asc("^"),Null,Null)
keycodeBase(262) = ELACreateKeyCode(0,"&",Asc("&"),Null,Null)
keycodeBase(263) = ELACreateKeyCode(0,"*",Asc("*"),Null,Null)
keycodeBase(264) = ELACreateKeyCode(0,"(",Asc("("),Null,Null)
keycodeBase(265) = ELACreateKeyCode(0,")",Asc(")"),Null,Null)
keycodeBase(266) = ELACreateKeyCode(0,"_",Asc("_"),Null,Null)
keycodeBase(267) = ELACreateKeyCode(0,"+",Asc("+"),Null,Null)
keycodeBase(268) = ELACreateKeyCode(0,"Q",Asc("Q"),Null,Null)
keycodeBase(269) = ELACreateKeyCode(0,"W",Asc("W"),Null,Null)
keycodeBase(270) = ELACreateKeyCode(0,"E",Asc("E"),Null,Null)
keycodeBase(271) = ELACreateKeyCode(0,"R",Asc("R"),Null,Null)
keycodeBase(272) = ELACreateKeyCode(0,"T",Asc("T"),Null,Null)
keycodeBase(273) = ELACreateKeyCode(0,"Y",Asc("Y"),Null,Null)
keycodeBase(274) = ELACreateKeyCode(0,"U",Asc("U"),Null,Null)
keycodeBase(275) = ELACreateKeyCode(0,"I",Asc("I"),Null,Null)
keycodeBase(276) = ELACreateKeyCode(0,"O",Asc("O"),Null,Null)
keycodeBase(277) = ELACreateKeyCode(0,"P",Asc("P"),Null,Null)
keycodeBase(278) = ELACreateKeyCode(0,"{",Asc("{"),Null,Null)
keycodeBase(279) = ELACreateKeyCode(0,"}",Asc("}"),Null,Null)
keycodeBase(280) = ELACreateKeyCode(0,"A",Asc("A"),Null,Null)
keycodeBase(281) = ELACreateKeyCode(0,"S",Asc("S"),Null,Null)
keycodeBase(282) = ELACreateKeyCode(0,"D",Asc("D"),Null,Null)
keycodeBase(283) = ELACreateKeyCode(0,"F",Asc("F"),Null,Null)
keycodeBase(284) = ELACreateKeyCode(0,"G",Asc("G"),Null,Null)
keycodeBase(285) = ELACreateKeyCode(0,"H",Asc("H"),Null,Null)
keycodeBase(286) = ELACreateKeyCode(0,"J",Asc("J"),Null,Null)
keycodeBase(287) = ELACreateKeyCode(0,"K",Asc("K"),Null,Null)
keycodeBase(288) = ELACreateKeyCode(0,"L",Asc("L"),Null,Null)
keycodeBase(289) = ELACreateKeyCode(0,":",Asc(":"),Null,Null)
keycodeBase(290) = ELACreateKeyCode(0,"@",Asc("@"),Null,Null)
keycodeBase(291) = ELACreateKeyCode(0,"~",Asc("~"),Null,Null)
keycodeBase(292) = ELACreateKeyCode(0,"|",Asc("|"),Null,Null)
keycodeBase(293) = ELACreateKeyCode(0,"Z",Asc("Z"),Null,Null)
keycodeBase(294) = ELACreateKeyCode(0,"X",Asc("X"),Null,Null)
keycodeBase(295) = ELACreateKeyCode(0,"C",Asc("C"),Null,Null)
keycodeBase(296) = ELACreateKeyCode(0,"V",Asc("V"),Null,Null)
keycodeBase(297) = ELACreateKeyCode(0,"B",Asc("B"),Null,Null)
keycodeBase(298) = ELACreateKeyCode(0,"N",Asc("N"),Null,Null)
keycodeBase(299) = ELACreateKeyCode(0,"M",Asc("M"),Null,Null)
keycodeBase(300) = ELACreateKeyCode(0,"<",Asc("<"),Null,Null)
keycodeBase(301) = ELACreateKeyCode(0,">",Asc(">"),Null,Null)
keycodeBase(302) = ELACreateKeyCode(0,"?",Asc("?"),Null,Null)
keycodeBase(303) = ELACreateKeyCode(0,"¬",Asc("¬"),Null,Null)

keycodeBase(1)   = ELACreateKeyCode(ELA_SCANCODE_TYPEID_ESCAPE,"",0,Null,Null)
keycodeBase(2)   = ELACreateKeyCode(0,"1",Asc("1"),Null,keycodeBase(256))
keycodeBase(3)   = ELACreateKeyCode(0,"2",Asc("2"),Null,keycodeBase(257))
keycodeBase(4)   = ELACreateKeyCode(0,"3",Asc("3"),Null,keycodeBase(258))
keycodeBase(5)   = ELACreateKeyCode(0,"4",Asc("4"),Null,keycodeBase(259))
keycodeBase(6)   = ELACreateKeyCode(0,"5",Asc("5"),Null,keycodeBase(260))
keycodeBase(7)   = ELACreateKeyCode(0,"6",Asc("6"),Null,keycodeBase(261))
keycodeBase(8)   = ELACreateKeyCode(0,"7",Asc("7"),Null,keycodeBase(262))
keycodeBase(9)   = ELACreateKeyCode(0,"8",Asc("8"),Null,keycodeBase(263))
keycodeBase(10)  = ELACreateKeyCode(0,"9",Asc("9"),Null,keycodeBase(264))
keycodeBase(11)  = ELACreateKeyCode(0,"0",Asc("0"),Null,keycodeBase(265))
keycodeBase(12)  = ELACreateKeyCode(0,"-",Asc("-"),Null,keycodeBase(266))
keycodeBase(13)  = ELACreateKeyCode(0,"=",Asc("="),Null,keycodeBase(267))
keycodeBase(14)  = ELACreateKeyCode(ELA_SCANCODE_TYPEID_BACKSPACE,"",0,Null,Null)
keycodeBase(15)  = ELACreateKeyCode(ELA_SCANCODE_TYPEID_TAB      ,"    ",Asc("	"),Null,Null)
keycodeBase(16)  = ELACreateKeyCode(0,"q",Asc("q"),keycodeBase(268),keycodeBase(268))
keycodeBase(17)  = ELACreateKeyCode(0,"w",Asc("w"),keycodeBase(269),keycodeBase(269))
keycodeBase(18)  = ELACreateKeyCode(0,"e",Asc("e"),keycodeBase(270),keycodeBase(270))
keycodeBase(19)  = ELACreateKeyCode(0,"r",Asc("r"),keycodeBase(271),keycodeBase(271))
keycodeBase(20)  = ELACreateKeyCode(0,"t",Asc("t"),keycodeBase(272),keycodeBase(272))
keycodeBase(21)  = ELACreateKeyCode(0,"y",Asc("y"),keycodeBase(273),keycodeBase(273))
keycodeBase(22)  = ELACreateKeyCode(0,"u",Asc("u"),keycodeBase(274),keycodeBase(274))
keycodeBase(23)  = ELACreateKeyCode(0,"i",Asc("i"),keycodeBase(275),keycodeBase(275))
keycodeBase(24)  = ELACreateKeyCode(0,"o",Asc("o"),keycodeBase(276),keycodeBase(276))
keycodeBase(25)  = ELACreateKeyCode(0,"p",Asc("p"),keycodeBase(277),keycodeBase(277))
keycodeBase(26)  = ELACreateKeyCode(0,"[",Asc("["),Null,keycodeBase(278))
keycodeBase(27)  = ELACreateKeyCode(0,"]",Asc("]"),Null,keycodeBase(279))
keycodeBase(28)  = ELACreateKeyCode(ELA_SCANCODE_TYPEID_RETURN,"",0,Null,Null)
keycodeBase(29)  = ELACreateKeyCode(ELA_SCANCODE_TYPEID_LCTRL ,"",0,Null,Null)
keycodeBase(30)  = ELACreateKeyCode(0,"a",Asc("a"),keycodeBase(280),keycodeBase(280))
keycodeBase(31)  = ELACreateKeyCode(0,"s",Asc("s"),keycodeBase(281),keycodeBase(281))
keycodeBase(32)  = ELACreateKeyCode(0,"d",Asc("d"),keycodeBase(282),keycodeBase(282))
keycodeBase(33)  = ELACreateKeyCode(0,"f",Asc("f"),keycodeBase(283),keycodeBase(283))
keycodeBase(34)  = ELACreateKeyCode(0,"g",Asc("g"),keycodeBase(284),keycodeBase(284))
keycodeBase(35)  = ELACreateKeyCode(0,"h",Asc("h"),keycodeBase(285),keycodeBase(285))
keycodeBase(36)  = ELACreateKeyCode(0,"j",Asc("j"),keycodeBase(286),keycodeBase(286))
keycodeBase(37)  = ELACreateKeyCode(0,"k",Asc("k"),keycodeBase(287),keycodeBase(287))
keycodeBase(38)  = ELACreateKeyCode(0,"l",Asc("l"),keycodeBase(288),keycodeBase(288))
keycodeBase(39)  = ELACreateKeyCode(0,";",Asc(";"),Null,keycodeBase(289))
keycodeBase(40)  = ELACreateKeyCode(0,"'",Asc("'"),Null,keycodeBase(290))
keycodeBase(41)  = ELACreateKeyCode(0,"`",Asc("`"),Null,keycodeBase(303))  ; `
keycodeBase(42)  = ELACreateKeyCode(ELA_SCANCODE_TYPEID_LSHIFT,"",0,Null,Null)
keycodeBase(43)  = ELACreateKeyCode(0,"#",Asc("#"),Null,keycodeBase(291))
keycodeBase(44)  = ELACreateKeyCode(0,"z",Asc("z"),keycodeBase(293),keycodeBase(293))
keycodeBase(45)  = ELACreateKeyCode(0,"x",Asc("x"),keycodeBase(294),keycodeBase(294))
keycodeBase(46)  = ELACreateKeyCode(0,"c",Asc("c"),keycodeBase(295),keycodeBase(295))
keycodeBase(47)  = ELACreateKeyCode(0,"v",Asc("v"),keycodeBase(296),keycodeBase(296))
keycodeBase(48)  = ELACreateKeyCode(0,"b",Asc("b"),keycodeBase(297),keycodeBase(297))
keycodeBase(49)  = ELACreateKeyCode(0,"n",Asc("n"),keycodeBase(298),keycodeBase(298))
keycodeBase(50)  = ELACreateKeyCode(0,"m",Asc("m"),keycodeBase(299),keycodeBase(299))
keycodeBase(51)  = ELACreateKeyCode(0,",",Asc(","),Null,keycodeBase(300))
keycodeBase(52)  = ELACreateKeyCode(0,".",Asc("."),Null,keycodeBase(301))
keycodeBase(53)  = ELACreateKeyCode(0,"/",Asc("/"),Null,keycodeBase(302))
keycodeBase(54)  = ELACreateKeyCode(ELA_SCANCODE_TYPEID_RSHIFT,"",0,Null,Null)
keycodeBase(55)  = ELACreateKeyCode(0,"*",Asc("*"),Null,Null)
keycodeBase(56)  = ELACreateKeyCode(ELA_SCANCODE_TYPEID_LMENU,"",0,Null,Null)
keycodeBase(57)  = ELACreateKeyCode(0," ",Asc(" "),Null,Null)
keycodeBase(58)  = ELACreateKeyCode(ELA_SCANCODE_TYPEID_CAPSLOCK,"" ,Asc("") ,Null,Null)
keycodeBase(59)  = ELACreateKeyCode(ELA_SCANCODE_TYPEID_F1 ,"F1",0,Null,Null)
keycodeBase(60)  = ELACreateKeyCode(ELA_SCANCODE_TYPEID_F2 ,"F2",0,Null,Null)
keycodeBase(61)  = ELACreateKeyCode(ELA_SCANCODE_TYPEID_F3 ,"F3",0,Null,Null)
keycodeBase(62)  = ELACreateKeyCode(ELA_SCANCODE_TYPEID_F4 ,"F4",0,Null,Null)
keycodeBase(63)  = ELACreateKeyCode(ELA_SCANCODE_TYPEID_F5 ,"F5",0,Null,Null)
keycodeBase(64)  = ELACreateKeyCode(ELA_SCANCODE_TYPEID_F6 ,"F6",0,Null,Null)
keycodeBase(65)  = ELACreateKeyCode(ELA_SCANCODE_TYPEID_F7 ,"F7",0,Null,Null)
keycodeBase(66)  = ELACreateKeyCode(ELA_SCANCODE_TYPEID_F8 ,"F8",0,Null,Null)
keycodeBase(67)  = ELACreateKeyCode(ELA_SCANCODE_TYPEID_F9 ,"F9",0,Null,Null)
keycodeBase(68)  = ELACreateKeyCode(ELA_SCANCODE_TYPEID_F10,"F10",0,Null,Null)
keycodeBase(69)  = ELACreateKeyCode(ELA_SCANCODE_TYPEID_NUMLOCK   ,"",0,Null,Null)
keycodeBase(70)  = ELACreateKeyCode(ELA_SCANCODE_TYPEID_SCROLLLOCK,"",0,Null,Null)
keycodeBase(71)  = ELACreateKeyCode(ELA_SCANCODE_TYPEID_NUMPAD7   ,"7",Asc("7"),Null,Null)
keycodeBase(72)  = ELACreateKeyCode(ELA_SCANCODE_TYPEID_NUMPAD8   ,"8",Asc("8"),Null,Null)
keycodeBase(73)  = ELACreateKeyCode(ELA_SCANCODE_TYPEID_NUMPAD9   ,"9",Asc("9"),Null,Null)
keycodeBase(74)  = ELACreateKeyCode(0,"-",Asc("-"),Null,Null)
keycodeBase(75)  = ELACreateKeyCode(ELA_SCANCODE_TYPEID_NUMPAD4   ,"4",Asc("4"),Null,Null)
keycodeBase(76)  = ELACreateKeyCode(ELA_SCANCODE_TYPEID_NUMPAD5   ,"5",Asc("5"),Null,Null)
keycodeBase(77)  = ELACreateKeyCode(ELA_SCANCODE_TYPEID_NUMPAD6   ,"6",Asc("6"),Null,Null)
keycodeBase(78)  = ELACreateKeyCode(0,"+",Asc("+"),Null,Null)
keycodeBase(79)  = ELACreateKeyCode(ELA_SCANCODE_TYPEID_NUMPAD1   ,"1",Asc("1"),Null,Null)
keycodeBase(80)  = ELACreateKeyCode(ELA_SCANCODE_TYPEID_NUMPAD2   ,"2",Asc("2"),Null,Null)
keycodeBase(81)  = ELACreateKeyCode(ELA_SCANCODE_TYPEID_NUMPAD3   ,"3",Asc("3"),Null,Null)
keycodeBase(82)  = ELACreateKeyCode(ELA_SCANCODE_TYPEID_NUMPAD0   ,"0",Asc("0"),Null,Null)
keycodeBase(83)  = ELACreateKeyCode(0,".",Asc("."),Null,Null)
keycodeBase(86)  = ELACreateKeyCode(ELA_SCANCODE_TYPEID_OEM102    ,"\",Asc("\"),Null,keycodeBase(292))
keycodeBase(87)  = ELACreateKeyCode(ELA_SCANCODE_TYPEID_F11,"F11",0,Null,Null)
keycodeBase(88)  = ELACreateKeyCode(ELA_SCANCODE_TYPEID_F12,"F12",0,Null,Null)
keycodeBase(100) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_F13,"F13",0,Null,Null)
keycodeBase(101) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_F14,"F14",0,Null,Null)
keycodeBase(102) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_F15,"F15",0,Null,Null)

keycodeBase(112) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_KANA         ,"",0,Null,Null)
keycodeBase(115) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_ABNT_C1      ,"",0,Null,Null)
keycodeBase(121) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_CONVERT      ,"",0,Null,Null)
keycodeBase(123) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_NOCONVERT    ,"",0,Null,Null)
keycodeBase(125) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_YEN          ,"",0,Null,Null)
keycodeBase(126) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_ABNT_C2      ,"",0,Null,Null)
keycodeBase(141) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_NUMPADEQUALS ,"=",Asc("="),Null,Null)
keycodeBase(144) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_PREVTRACK    ,"",0,Null,Null)
keycodeBase(145) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_AT           ,"",0,Null,Null)
keycodeBase(146) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_COLON        ,":",Asc(":"),Null,Null)
keycodeBase(147) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_UNDERLINE    ,"",0,Null,Null)
keycodeBase(148) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_KANJI        ,"",0,Null,Null)
keycodeBase(149) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_STOP         ,"",0,Null,Null)
keycodeBase(150) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_AX           ,"",0,Null,Null)
keycodeBase(151) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_UNLABELED    ,"",0,Null,Null)
keycodeBase(153) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_NEXTTRACK    ,"",0,Null,Null)
keycodeBase(156) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_NUMPADENTER  ,"",0,Null,Null)
keycodeBase(157) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_RCTRL        ,"",0,Null,Null)
keycodeBase(160) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_MUTE         ,"",0,Null,Null)
keycodeBase(161) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_CALCULATOR   ,"",0,Null,Null)
keycodeBase(162) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_PLAYPAUSE    ,"",0,Null,Null)
keycodeBase(164) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_MEDIASTOP    ,"",0,Null,Null)
keycodeBase(174) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_VOLUMEDOWN   ,"",0,Null,Null)
keycodeBase(176) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_VOLUMEUP     ,"",0,Null,Null)
keycodeBase(178) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_WEBHOME      ,"",0,Null,Null)
keycodeBase(179) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_NUMPADCOMMA  ,",",Asc(","),Null,Null)
keycodeBase(181) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_DIVIDE       ,"/",Asc("/"),Null,Null)
keycodeBase(183) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_SYSRQ        ,"",0,Null,Null)
keycodeBase(184) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_RMENU        ,"",0,Null,Null)
keycodeBase(197) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_PAUSE        ,"",0,Null,Null)
keycodeBase(199) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_HOME         ,"",0,Null,Null)
keycodeBase(200) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_UP           ,"",0,Null,Null)
keycodeBase(201) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_PRIOR        ,"",0,Null,Null)
keycodeBase(203) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_LEFT         ,"",0,Null,Null)
keycodeBase(205) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_RIGHT        ,"",0,Null,Null)
keycodeBase(207) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_END          ,"",0,Null,Null)
keycodeBase(208) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_DOWN         ,"",0,Null,Null)
keycodeBase(209) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_NEXT         ,"",0,Null,Null)
keycodeBase(210) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_INSERT       ,"",0,Null,Null)
keycodeBase(211) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_DELETE       ,"",0,Null,Null)
keycodeBase(219) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_LWIN         ,"",0,Null,Null)
keycodeBase(220) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_RWIN         ,"",0,Null,Null)
keycodeBase(221) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_APPS         ,"",0,Null,Null)
keycodeBase(222) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_POWER        ,"",0,Null,Null)
keycodeBase(223) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_SLEEP        ,"",0,Null,Null)
keycodeBase(227) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_WAKE         ,"",0,Null,Null)
keycodeBase(229) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_WEBSEARCH    ,"",0,Null,Null)
keycodeBase(230) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_WEBFAVORITES ,"",0,Null,Null)
keycodeBase(231) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_WEBREFRESH   ,"",0,Null,Null)
keycodeBase(232) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_WEBSTOP      ,"",0,Null,Null)
keycodeBase(233) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_WEBFORWARD   ,"",0,Null,Null)
keycodeBase(234) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_WEBBACK      ,"",0,Null,Null)
keycodeBase(235) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_MYCOMPUTER   ,"",0,Null,Null)
keycodeBase(236) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_MAIL         ,"",0,Null,Null)
keycodeBase(237) = ELACreateKeyCode(ELA_SCANCODE_TYPEID_MEDIASELECT  ,"",0,Null,Null)


; Yes, there IS a reason for this LUT within a LUT! :) 6 milliseconds is the reason! :)
; A small optimisation I know, but an optimisation non the less!
Restore keycodesUseTheseKeyCodes
Read keyScan
While keyScan <> 0
	keycodeValidTo = keycodeValidTo + 1
	keycodeValid(keycodeValidTo) = keyScan
	Read keyScan
Wend

Function ELAScanCode.elaKeyCode()
	If KeyHit(58) Then keycodeCapsLockOn = 1 - keycodeCapsLockOn
	
	For keycodeScannedCode = 1 To keycodeValidTo 
		keyScanTF = KeyDown(keycodeValid(keycodeScannedCode))
		If keyScanTF
			tempScanCode = keycodeValid(keycodeScannedCode)
			If keycodeLastKeyDown <> tempScanCode
				keycodeTiming = 0
			EndIf
			keycodeLastKeyDown = tempScanCode
			If keycodeTiming = 0
				keycodeTiming = MilliSecs()
				keycodeLastKeyHit = tempScanCode
				Return ELAKeyCodeReturnKey(tempScanCode)
			Else
				If MilliSecs() - keycodeTiming > keycodeDelay
					If keycodeTimingNext = 0
						keycodeTimingNext = MilliSecs()
						keycodeLastKeyHit = 0
						Return Null
					Else
						If MilliSecs() - keycodeTimingNext > keycodeNextKeyDelay
							keycodeTimingNext = 0
							keycodeLastKeyHit = tempScanCode
							Return ELAKeyCodeReturnKey(tempScanCode)
						Else
							keycodeLastKeyHit = 0
							Return Null
						EndIf
					EndIf
				Else
					Return Null
				EndIf
			EndIf	
		EndIf
	Next
	
	keycodeTimingNext  = 0
	keycodeLastKeyDown = 0
	keycodeTiming      = 0
	Return Null
End Function

Function ELAKeyCodeReturnKey.elaKeyCode(scancodeid)
	tempShiftDown = KeyDown(42) Or KeyDown(54)
	If tempShiftDown <> 0
		If keycodeBase(scancodeid)\terkey <> Null
;			If keycodeBase(scancodeid)\ascii <> 0
				Return keycodeBase(scancodeid)\terkey
;			EndIf
		Else
;			If keycodeBase(scancodeid)\ascii <> 0
				Return keycodeBase(scancodeid)
;			EndIf
		EndIf
	Else
		If keycodeCapsLockOn <> 0
			If keycodeBase(scancodeid)\seckey <> Null
;				If keycodeBase(scancodeid)\ascii <> 0
					Return keycodeBase(scancodeid)\seckey
;				EndIf
			Else
;				If keycodeBase(scancodeid)\ascii <> 0
					Return keycodeBase(scancodeid)
;				EndIf
			EndIf
		Else
;			If keycodeBase(scancodeid)\ascii <> 0
				Return keycodeBase(scancodeid)
;			EndIf
		EndIf
	EndIf
	Return Null
End Function

.keycodesUseTheseKeyCodes:
Data 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30
Data 31,32,33,34,35,36,37,38,39,40,41,43,44,45,46,47,48,49,50,51,52,53,55,56,57,59,60
Data 61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,86,87,88,100
Data 101,102,112,115,121,123,125,126,141,144,145,146,147,148,149,150,151,153,156,157,160
Data 161,162,164,174,176,178,179,181,183,184,197,199,200,201,203,205,207,208,209,210,211
Data 219,220,221,222,223,227,229,230,231,232,233,234,235,236,237,0     ; null terminated

;-------------------------------------------
;////- END INCLUDE -////

; Example:

printtext$ = ""
ELASetKeyDelay(500)      ; Delay between next key return in millisecs.
ELASetNextKeyDelay(20)   ; Delay between next key return when holding the key, in millisecs.


Repeat
	Cls
	
	tempkeycode.elaKeyCode = ELAScanCode()
	
	If tempkeycode <> Null
		printtext$ = printtext$ + tempkeycode\alpha
		If StringWidth(printtext$)>GraphicsWidth()
			printtext$ = ""
		EndIf
	EndIf
	
	Text 0,0,"Type something...."
	Text 0,15,printtext$
	
	Flip
Until KeyDown(1)
End


