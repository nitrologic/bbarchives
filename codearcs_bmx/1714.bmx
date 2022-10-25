; ID: 1714
; Author: Nilium
; Date: 2006-05-14 13:53:40
; Title: Reverse bytes and bits
; Description: Functions to reverse the order of bytes and bits

Function ReverseBytes( p@ Ptr, sz%, rbits%=0 )
    Local szh% = Int(Floor(sz*.5))
    sz :- 1
    If rbits>0 Then
        For Local i:Int = 0 To szh-1
            szh = p[i] ' Because szh is only used once, we can reuse it here
            p[i] = ReverseBits(p[sz-i])
            p[sz-i] = ReverseBits(szh)
        Next
        sz :+ 1
        If Int(sz*.5) <> Ceil((sz*.5)-.1) Then
            sz = Int(Ceil(sz*.5))
            p[sz] = ReverseBits(p[sz])
        EndIf
    Else
        For Local i:Int = 0 To szh-1
            szh = p[i]
            p[i] = p[sz-i]
            p[sz-i] = szh
        Next
    EndIf
End Function

Function ReverseBitsA@( p@ )
    ?Debug
    Return (p & %1) Shl 7..
    | ((p&%10000000) Shr 7)..
    | (p & %10) Shl 5..
    | ((p&%1000000) Shr 5)..
    | (p & %100) Shl 3..
    | ((p&%100000) Shr 3)..
    | (p & %1000) Shl 1..
    | ((p&%10000) Shr 1)
    ?
    Local p2:Int = ((p & %11110000) Shr 4) | ((p & %1111) Shl 4)
    p =((p & %11001100) Shr 2) | ((p & %110011) Shl 2)
    Return ((p & %10101010) Shr 1) | ((p & %1010101) Shl 1)
End Function
