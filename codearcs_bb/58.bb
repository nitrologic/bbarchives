; ID: 58
; Author: skidracer
; Date: 2001-09-26 15:54:05
; Title: BubbleSort
; Description: sorts blitz object collections

; sort.bb

Type bob
    Field    z
End Type

Function SortBobs()
    b.bob=First bob
    flag=True
    While flag
        flag=False
        bb.bob=Last bob
        While bb<>b
            bbb.bob=Before bb
            If bbb=Null Exit
            If bb\z<bbb\z 
                Insert bbb After bb
                flag=True
            Else
                bb=bbb
            EndIf
        Wend
        b=After bb
    Wend
End Function

For i=1 To 100
    b.bob=New bob
    b\z=Rnd(1000)
Next

SortBobs

For b.bob=Each bob
    DebugLog b\z
Next

End
