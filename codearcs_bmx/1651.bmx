; ID: 1651
; Author: Nilium
; Date: 2006-03-28 23:06:21
; Title: Search String Array
; Description: Allows you to search an array of strings for a string matching a pattern

Function SearchStrings$[]( search$, arr$[] )
    Local clean$ = ""
    Local l$ = ""
    For Local ex% = 0 To search.Length-1
        Local p$ = Chr(search[ex])
        If p = "*" And l = "*" Then Continue
        If p = "*" Then p = "|*|"
        clean :+ p
    Next
    Local s$[] = SplitString( clean, "|" )
    
    Local resn%=arr.Length
    Local bad%[arr.Length]
    memset_(bad,0,arr.Length*4)
    
    For Local ex% = 0 To arr.Length-1
        Local e$ = arr[ex]
        If e = "" Or e = Null Then
            bad[ex] = 1
            resn :- 1
            Continue
        EndIf
        Local from% = 0, find% = 0
        
        For Local i% = 0 To s.Length-1
            Local p$ = s[i]
            
            If p = "*" And i = s.Length-1 Then
                Continue
            ElseIf p = "*" Then
                p = s[i+1]
            EndIf
            
            find = e.Find(p,from)
            If find = -1 Or (find > 0 And i = 0 And s[i] <> "*") Then
                bad[ex] = 1
                resn :- 1
                Exit
            EndIf
            from = find
        Next
    Next
    
    Local ret$[resn]
    Local n% = 0
    For Local ex% = 0 To arr.Length-1
        If bad[ex] Then Continue
        Local e$ = arr[ex]
        ret[n] = e
        n :+ 1
    Next
    
    Return ret
End Function
