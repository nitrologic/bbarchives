; ID: 547
; Author: Mr Snidesmin
; Date: 2003-01-12 17:08:26
; Title: Split Function
; Description: Separates a string into parts using a specified delimiter

Type StringArray
    Field Value$
    Field FirstElement.StringArray
    Field NextElement.StringArray
    Field PrevElement.StringArray
    Field Count%
End Type
Function Split.StringArray(sVal$, sSep$)
    s.StringArray = New StringArray
    If sVal = "" Then
        Return s
    End If
    While Len(sVal) > 0
        i% = Instr(sVal, sSep)
        If i = 0 Then 
            sa_Append s, sVal
            sVal = ""
        Else
            sa_Append s, Left(sVal,i-1)
            sVal = Mid(sVal, i+Len(sSep))
        End If
    Wend
    Return s
End Function
Function sa_Append(s.StringArray, sVal$)
    If s=Null Then Return
    If s\FirstElement = Null Then
        s\Count = 1
        s\FirstElement = s
        s\Value = sVal
        ;DebugLog "sa_Append, Appended:" + sVal
        Return
    End If
    If s\NextElement = Null
        s\count = s\count + 1
        s\NextElement = New StringArray
        s\NextElement\count = 1
        s\NextElement\PrevElement = s
        s\NextElement\FirstElement= s\FirstElement
        s\NextElement\Value = sVal
        ;DebugLog "sa_Append, Appended:" + sVal
    Else
        s\count = s\count + 1
        sa_Append(s\NextElement , sVal)
    End If
End Function
Function sa_Count%(s.StringArray)
    If s = Null Then Return 0
    Return s\FirstElement\Count%
End Function
Function sa_Find.StringArray(s.StringArray, index%)
    If s = Null Then Return Null
    If index < 1 Then Return Null
    If index = 1 Then Return s
    Return sa_Find(s\NextElement, index-1)
End Function
Function sa_Get$(s.StringArray, index%)
    If s = Null Then Return ""
    s = s\FirstElement
    s = sa_Find(s, index)
    If s<>Null Then    Return s\Value
End Function
Function sa_Destroy(s.StringArray)
    If s = Null Then Return
    If s\FirstElement = Null Then
        ;DebugLog "sa_Destroy, Deleted: " + s\Value
        Delete s
        Return
    End If
    If s\NextElement = Null Then
        s = s\PrevElement        
        ;DebugLog "sa_Destroy, Deleted: " + s\NextElement\Value
        Delete s\NextElement
        sa_Destroy s
    Else
        s = s\FirstElement
        s\FirstElement = Null
        sa_Destroy sa_Find(s, s\Count)
    End If
End Function
