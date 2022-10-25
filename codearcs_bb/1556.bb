; ID: 1556
; Author: VP
; Date: 2005-12-10 10:21:09
; Title: Bitmap font routine.
; Description: Small and modular bitmap font routine.

; Bitmap font routines.
;
; Version 0.21 FREELY DISTRIBUTABLE
; Conception 10th December 2005 14:20 GMT.

; Copyright © 2005 Stefan Holmes.
; Whilst I retain copyright, I give permission for the free distribution and use of this sourcecode.

; HISTORY
;
; Version 0.01, 10th December 2005.
; Non-working alpha.
; --
; Version 0.10, 10th December 2005.
; Working beta.
; Text justification not implemented.
; --
; Version 0.20, 10th December 2005.
; Working release.
; Multi-line output is missing.
; --
; Version 0.21, 10th December 2005.
; Working release.
; Multi-line output is missing.
; Implemented colour. Requires change to how the font image is grabbed.

Const BF_JUSTIFY_LEFT                   = %0000000000000001 ; $0001
Const BF_JUSTIFY_RIGHT                  = %0000000000000010 ; $0002
Const BF_JUSTIFY_CENTRE                 = %0000000000000011 ; $0003
Const BF_JUSTIFY_TOP                    = %0000000000000100 ; $0004
Const BF_JUSTIFY_BOTTOM                 = %0000000000001000 ; $0008
Const BF_JUSTIFY_MIDDLE                 = %0000000000001100 ; $000C

Const BF_CHARSET_RESTRICTED_UPPERCASE   = %0000000000010000 ; $0010
Const BF_CHARSET_RESTRICTED_BOTHCASES   = %0000000000100000 ; $0020
Const BF_CHARSET_FULL_UPPERCASE         = %0000000000110000 ; $0030
Const BF_CHARSET_FULL_BOTHCASES         = %0000000001000000 ; $0040
Const BF_CHARSET_FULL_INTERNATIONAL     = %0000000001010000 ; $0050
Const BF_CHARSET_RESERVED1              = %0000000001100000 ; $0060
Const BF_CHARSET_RESERVED2              = %0000000001110000 ; $0070


Function bfText(font,word$,bfColor%=$FFFFFF,flags%=0,x%=0,y%=0,w%=0,h%=0)
; ARGUMENTS:
;
; font  = Valid Blitz image with anim frames containing characters. Sequence
;         depends on the BF_CHARSET_... bitmask.
;
;       BF_CHARSET_RESTRICTED_UPPERCASE =
;       ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.,!?@#-©®
;       45 characters.
;
;       BF_CHARSET_RESTRICTED_BOTHCASES =
;       ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789.,!?@#-©®
;       71 characters.
;
;       BF_CHARSET_FULL_UPPERCASE =
;       ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.!?@#-'"£$%^&*()=+_:;<>/\[]{}~©®
;       69 characters.
;
;       BF_CHARSET_FULL_BOTHCASES =
;       ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789,.!?@#-'"£$%^&*()=+_:;<>/\[]{}~©®
;       95 characters.
;
;       BF_CHARSET_FULL_INTERNATIONAL is not defined in this version.
;
;       BF_CHARSET_RESERVED1 and BF_CHARSET_RESERVED2 are undefined in this version.
;
; word$ = Text to be displayed.
;
; bfColor%  = $RRGGBB hex value.
;           N.B. There is a gotcha here. Because of the way the images are masked, pure black text
;           is not possible. Pure black is converted to darkest blue $000001.
;
; flags%    = Bit flags denoting how the text is rendered.
;
;           Bits 1-2 control horizontal justification.
;           Bits 3-4 control vertical justification.
;           Bits 5-7 denote which charset to use (CAUTION: incorrect setting can cause program to exit).
;           All other bits are undefined in this version.
;
; x% and y% = Nominal pixel coordinates where text is to be displayed.
;
; w% and h% = If non-zero denotes a virtual display width and height.
;             If zero, current screen width and height are used.


If font = 0
    DebugLog "bfText: font image not available" : End
EndIf

If bfColor%=0 Then bfColor%=$000001
b% = bfColor% And %00000000000000000000000011111111
g% = (bfColor% And %00000000000000001111111100000000) Shr 8
r% = (bfColor% And %00000000111111110000000000000000) Shr 16

charwidth%  = ImageWidth(font)
charheight% = ImageHeight(font)

If w% = 0 Then w% = GraphicsWidth()
If h% = 0 Then h% = GraphicsHeight()

horizjustify%   = (flags% And %0000000000000011)
vertjustify%    = (flags% And %0000000000001100) Shr 2
charset%        = (flags% And %0000000001110000) Shr 4

Select horizjustify%
    Case 1
        ; BF_JUSTIFY_LEFT
        offsetx% = x%
    Case 2
        ; BF_JUSTIFY_RIGHT
        offsetx% = w% - (charwidth% * Len(word$)) + x%
    Case 3
        ; BF_JUSTIFY_CENTRE
        offsetx% = (w% / 2) - ((charwidth% * Len(word$)) / 2) + x%
End Select

Select vertjustify%
    Case 1
        offsety% = y%
    Case 2
        offsety% = h% - charheight% + y%
    Case 3
        offsety% = (h% / 2) - (charheight% / 2) + y%
End Select 

Select charset%
    Case 0
        DebugLog "bfText: No charset defined" : End
    Case 1
        ; BF_CHARSET_RESTRICTED_UPPERCASE
        source$="ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.,!?@#-©®"
        word$ = Upper(word$)
    Case 2
        ; BF_CHARSET_RESTRICTED_BOTHCASES
        source$="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789.,!?@#-©®"
    Case 3
        ; BF_CHARSET_FULL_UPPERCASE
        source$="ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.!?@#-'"+Chr$(34)+"£$%^&*()=+_:;<>/\[]{}~©®"
        word$ = Upper(word$)
    Case 4
        ; BF_CHARSET_FULL_BOTHCASES
        source$="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789,.!?@#-'"+Chr$(34)+"£$%^&*()=+_:;<>/\[]{}~©®"
    Case 5
        ; BF_CHARSET_FULL_INTERNATIONAL
        DebugLog "bfText: BF_CHARSET_FULL_INTERNATIONAL not implemented" : End
    Case 6
        ; BF_CHARSET_RESERVED1
        DebugLog "bfText: Illegal flag BF_CHARSET_RESERVED1" : End
    Case 7
        ; BF_CHARSET_RESERVED2
        DebugLog "bfText: Illegal flag BF_CHARSET_RESERVED2" : End
End Select

originalbuffer = GraphicsBuffer()
imgTemp = CreateImage(Len(word$)*charwidth,charheight%)

SetBuffer ImageBuffer(imgTemp)
ClsColor r%,g%,b% : Color 0,0,0 : Cls ; This sets the colour of the text.

For i% = 1 To Len(word$)
    char% = Instr(source$,Mid$(word$,i%,1))
    If char%>0
        DrawImage font,(i%-1)*charwidth%,0,char%-1
    Else
        Rect (i%-1)*charwidth%,0,charwidth%,charheight%,True ; Draw a negative space!
    EndIf 
Next 

SetBuffer originalbuffer
DrawImage imgTemp,offsetx%,offsety%

End Function

; EOF
