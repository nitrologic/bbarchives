; ID: 1706
; Author: Nilium
; Date: 2006-05-10 19:55:05
; Title: .decls Converter
; Description: Convert .decls files to BlitzMax sourcecode

SuperStrict

Framework Brl.Blitz
Import Brl.Filesystem
Import Brl.Stream
Import Brl.LinkedList

Global libs:TList = New TList
Global mode$="A"

For Local i:Int = 1 To AppArgs.Length-1
    If AppArgs[i].ToLower( ) = "/u" Then
        AppArgs[i] = ""
        mode = "W"
    EndIf
Next

For Local i:Int = 1 To AppArgs.Length-1
    If AppArgs[i].Length = 0 Then Continue
    ProcessDecl( AppArgs[i] )
Next

End

Type TFunc
    Field sym$
    Field decl$
    
    Method Format$( s$ )
        Return s.Replace( "$(SYM)", sym )..
                .Replace( "$(DECL)", decl )
    End Method
    
    Function Create:TFunc( decl$, symbol$ )
        Local f:TFunc = New TFunc
        f.sym = symbol
        f.decl = decl
        Return f
    End Function
End Type

Type TLib
    Method New( )
        link = libs.AddLast( Self )
        funcs = New TList
    End Method
    
    Method Dispose( )
        funcs.Clear( )
        funcs = Null
        link.Remove( )
        link = Null
        lib = Null
    End Method
    
    Field lib$
    Field funcs:TList
    Field link:TLink
End Type

Function ProcessDecl( declPath$ )
    Local s:TStream = ReadStream( declPath )
    Local lib:TLib
    While Not s.Eof( )
        Local l$ = s.ReadLine( ).Trim( )
        
        Local cf% = l.Find( ";" )
        If cf <> -1 Then l = l[..cf].Trim( )
        
        If l.Length = 0 Then Continue
        
        If l[0] = 46 Then ' Set lib
            If lib Then
                If lib.funcs.Count( ) = 0 Or lib.lib = "." Then
                    lib.Dispose( )
                    lib = Null
                EndIf
            EndIf
            
            Local qb% = l.Find( "~q" )+1 ' Quote begin
            If qb = 0 then Continue
            Local qe% = l.Find( "~q", qb ) ' Quote end
            If qe = -1 Then Continue
            
            lib = New TLib
            lib.lib = l[qb..qe]
        ElseIf lib Then ' Parse function
            Local s$[] = SplitString( l, ":~q" )
            lib.funcs.AddLast( TFunc.Create( s[0].Replace("*","@ Ptr").Replace("$","$z"), s[1] ) )
        EndIf
    Wend
    s.Close( )
    lib = Null
    
    s = WriteStream( StripExt(declPath)+".bmx" )
    
    s.WriteLine( "Strict" )
    s.WriteLine( "Import Pub.Win32" )
    s.WriteLine( "" )
    s.WriteLine( "Private" )
    s.WriteLine( "Local lib%" )
    s.WriteLine( "" )
    s.WriteLine( "Public" )
    
    For lib = EachIn libs
        s.WriteLine( "" )
        s.WriteLine( "lib = LoadLibrary"+mode+"(~q"+lib.lib+"~q)" )
        s.WriteLine( "" )
        For Local f:TFunc = EachIn lib.funcs
            s.WriteLine( f.Format( "Global $(DECL) = GetProcAddress(lib, ~q$(SYM)~q)" ) )
        Next
        s.WriteLine( "" )
        s.WriteLine( "lib = 0" )
    Next
    
    s.Close( )
    s = Null
    
    ClearLibs( )
End Function

Function ClearLibs( )
    For Local i:TLib = EachIn libs
        i.Dispose( )
    Next
    libs.Clear( )
End Function

Function SplitString$[]( s$, sp$ )
    Local p:Int, l:Int, o$[32], x%
    Local n:Int
    For n = 0 To s.Length - 1
        Local lx% = x
        For p = 0 To sp.Length - 1
            If s[n] = sp[p] Then
                If x = o.Length Then o = o[..o.Length*2]
                o[x] = s[l..n+(n = s.Length-1 And (Not (s[n]=sp[p])))].Trim( )
                l = n+1
                If o[x].Length=0 Then Exit
                x :+ 1
                Exit
            EndIf
        Next
        If x <> lx Then Continue
        If n = s.Length-1 Then
            If x = o.Length Then o = o[..o.Length*2]
            o[x] = s[l..n+(n = s.Length-1 And (Not (s[n]=sp[p])))].Trim( )
            If o[x].Length = 0 Then Exit
            x :+ 1
        EndIf
    Next
    If x = 0 Then Return Null
    Return o[0..x]
End Function
