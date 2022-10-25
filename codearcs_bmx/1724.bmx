; ID: 1724
; Author: Nilium
; Date: 2006-05-27 02:44:55
; Title: Base64 Encoding
; Description: Encode data using Base64

SuperStrict

Private

Const etable$ = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
Global dtable@[256]
For Local __di:Int = 0 To etable.Length-1
    dtable[etable[__di]] = __di
Next

Public

Function EncodeBase64$( in@ Ptr, sizein:Int )
    If sizein <= 0 Then Return Null
    Local out:Byte[64]
    Local s$
    
    Local p@ Ptr = out
    Local cc% = 0
    
    For Local i% = 0 To sizein-1 Step 3
        Local c1@, c2@
        c1 = in[i]&$FF
        c2 = in[i+1]&$FF
        
        p[0] = etable[in[0] Shr 2]
        If i+1 < sizein Then
            p[1] = etable[((in[0] & $3) Shl 4) | ((in[1] & $F0) Shr 4)]
            If i+2 < sizein Then
                p[2] = etable[((in[1] & $F) Shl 2) | ((in[2] & $C0) Shr 6)]
                p[3] = etable[in[2] & $3F]
            ElseIf i+1 < sizein Then
                p[2] = etable[((in[1] & $F) Shl 2)]
            EndIf
        Else
            p[1] = etable[((in[0] & $3) Shl 4)]
        EndIf
        
        in :+ 3
        p :+ 4
        cc :+ 4
        If cc = 64 Then
            s :+ String.FromBytes(out,cc)
            p = out
            cc = 0
        EndIf
    Next
    Select sizein Mod 3
        Case 0
            s :+ String.FromBytes(out, cc)
        Case 1
            s :+ String.FromBytes(out, cc-2)+"=="
        Case 2
            s :+ String.FromBytes(out, cc-1)+"="
    End Select
    Return s
End Function

' Assumes you're passing a valid string to it
Function DecodeBase64:Byte[]( in$ )
    Local f% = in.Find("=")
    in = in.Replace("=","~0")
    Local rm% = 0
    If f = -1 Then
        rm = 0
        f = in.Length-1
    ElseIf f = in.Length-1 Then
        rm = 1
    ElseIf f = in.Length-2 Then
        rm = 2
    EndIf
    Local out@[((in.Length/4)*3)-rm]
    Local p@ Ptr = out
    Local bc% = 0
    For Local cc:Int = 0 To f Step 4
        p[0] = (dtable[in[cc]] Shl 2) | (dtable[in[cc+1]] Shr 4)
        If out.Length > (bc+1) Then p[1] = ((dtable[in[cc+1]] & $F) Shl 4) | ((dtable[in[cc+2]]&$3C) Shr 2)
        If out.Length > (bc+2) Then p[2] = ((dtable[in[cc+2]] & $3) Shl 6) | dtable[in[cc+3]]
        bc :+ 3
        p :+ 3
    Next
    Return out
End Function
