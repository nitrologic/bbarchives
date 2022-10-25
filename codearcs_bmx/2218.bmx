; ID: 2218
; Author: Nilium
; Date: 2008-02-19 02:04:53
; Title: Console
; Description: Code for interacting with a console

SuperStrict

Import Brl.LinkedList
Import Brl.Map

Private

Global ConsoleMap:TMap = New TMap
Global ConsoleLog:TList = New TList

Public
' If a cvar is set, this hook is called
' useful for monitoring in-game/engine changes
Global CVarSetHookID% = AllocHookID( )
' If anything is written to the console log, this hook is called
' useful for sending console messages to the debuglog, for example
Global ConsolePrintHookID% = AllocHookID( )

Private
Type ICVar
    Field _name$        ' name
    Field _value$       ' current value
    Field _readonly%    ' read-only?
    Field _system%      ' system/important?
    Field _script%=2    ' set from the script?  2 = set by the engine, but not system, and can be modified by script, 1 = (now a?) script variable, 0 = not.
End Type

Type ICCmd
    Field _name$ ' Name
    Field _callback:Int( n$, l$ )=Null ' Engine callback pointer
    Field _scallback$="" ' Script callback name
    Field _script%=0 ' Is a script callback?
    Field _system%=0 ' Is a system/important callback?
End Type

' Not exaclty unicode-friendly thanks to Chr
Function Strip( s$ Var, p$, replaceWith$="" )
    Local q$ = s$
    s = ""
    For Local i:Int = 0 To q.Length-1
        Local c$ = Chr(q[i])
        If p.Find(c)>-1 Then c = replaceWith
        s :+ c
    Next
End Function

Public

' Register a console command/callback
' name is the name of the command, obviously
' cb is the function callback, null if script is true
' system specifies whether or not it is a core/system callback (e.g., important to the function of the entire system), true/false
' script specifies whether or not it uses the script callback, true/false
' sb is the script callback, for instances where an interpreter handles the callback
Function RegisterConCommand( name$, cb:Int( n$, cmd$ ), system%=0, script%=0, sb$="" )
    name = name.ToLower( )
    Strip(name,"\/;'~q.,[]():~~`-{}|?<>*&^%$#@!+=~n~r~t ","")
    If name.Length = 0 Then Return

    Local value:Object = ConsoleMap.ValueForKey( name )
    Local c:ICCmd = ICCmd(value)

    If c Then
        If c._system Then
            ConPrint( "Cannot overwrite system console command" )
            Return
        EndIf
        If (cb = Null And script=0) Or (script=1 And sb.Length=0) Then
            ConsoleMap.Remove( name )
            Return
        EndIf
        c._callback = cb
        Return
    ElseIf Not cb Or value Then
        Return
    EndIf

    c = New ICCmd
    c._name = name
    c._callback = cb
    c._script = script
    c._system = system

    ConsoleMap.Insert( name, c )
End Function

' handles a call to the console
' messages that do not begin with / are interpreted as wanting to just write msg to the console log
Function CallConsole( msg$ )
    Strip(msg,"~n~r~t","")
    If msg[0] <> "/"[0] Then
        ConPrint( msg )
        Return
    EndIf
    
    Local sp:Int = msg.Find(" ")
    If sp = -1 Then
        sp = msg.Length
    ElseIf sp = 1 Then
        ConPrint( "Invalid console command" )
        Return
    EndIf

    Local name$ = msg[1..sp].Trim( ).ToLower( )
    Strip(name,"\/;'~q.,[]():~~`-{}|?<>*&^%$#@!+=","")
    If name.Length = 0 Then
        ConPrint( "Invalid console command" )
        Return
    EndIf

    Local value:Object = ConsoleMap.ValueForKey( name )
    Local c:ICCmd = ICCmd(value)

    If c = Null Then
        If sp = msg.Length Then
            'ConPrint( FormatString("$1 = ~q$2~q", [name, GetCVar(name)]) )
            ConPrint( name+" = ~q"+GetCVar(name)+"~q" )
        Else
            Local val$ = msg[sp..].Trim( )
            'ConPrint( FormatString("set $1 ~q$2~q", [name, val]) )
            SetCvar( name, val )
            ConPrint( name+" = ~q"+GetCVar(name)+"~q" )
        EndIf
        Return
    ElseIf c._script = 0 Then
        c._callback( name, msg[sp..].Trim( ) )
    Else
        Rem
        IScript.PushString( name )
        IScript.PushString( msg[sp..].Trim( ) )
        IScript.Call( c._scallback, 2, 0 )
        EndRem
        ' You'll have to insert how you handle script callbacks here.  I'm not responsible for it.
    EndIf
End Function

' gets a cvar
' n is the name of the cvar, d is a default value to give the cvar if it does not exist
' passing null to d will not create a cvar in the event that n doesn't exist
Function GetCVar$( n$, d$=Null )
    Strip(n, "\/;'~q.,[]():~~`-{}|?<>*&^%$#@!+=~n~r~t ", "")
    n = n.ToLower( ).Trim( )

    Local value:Object = ConsoleMap.ValueForKey( n )
    Local c:ICvar = ICVar(value)

    If Not c And d.Length>0 And Not value And d Then
        SetCVar( n, d )
        Return GetCVar(n)
    ElseIf Not c
        Return ""
    Else
        Return c._value
    EndIf
End Function

' n is the name of the cvar
' v is the value- if the cvar already exists and v is null, the cvar is deleted
' readonly specifies whether or not calls to the console can modify the cvar (via CallConsole)
' fromScript specifies whether or not SetCVar is being called from a script function
' + in the event that the cvar does not already exist and fromScript is true, the cvar is created as a script cvar
' + (this is relatively meaningless unless you implement a way to save cvars)
' system specifies whether or not the cvar is a system/core/important cvar, roughly the same as specifying read-only
' + except that read-only cvars would not be saved in something like autoexec.cfg
Function SetCVar( n$, v$, readonly%=-1, fromScript%=-1, system%=-1 )
    n = n.ToLower( ).Replace("\","").Trim( )
    Strip(n, "\/;'~q.,[]():~~`-{}|?<>*&^%$#@!+=~n~r~t ", "")

    Local value:Object = ConsoleMap.ValueForKey( n )
    Local c:ICvar = ICVar(value)

    If Not c And Not value And v Then
        c = New ICVar
        c._name = n
        ConsoleMap.Insert( n, c )
    ElseIf Not c Then
        Return
    ElseIf Not v And c Then
        ConsoleMap.Remove( n )
        Return
    EndIf

    If c._readonly And fromScript Then
        ConPrint( n+" is a read-only cvar and cannot be overwritten" )
        Return
    EndIf
    
    Strip(v, "~t~n~r~0", "")
    c._value = v
    If readonly<>-1 Then c._readonly = readonly
    If system<>-1 Then c._system = system
    If c._script = 2 And fromScript <> -1 Then c._script = fromScript

    RunHooks( CVarSetHookID, [c._name,c._value] )
End Function

' deletes non-system cvars if force is zero, deletes all cvars if force is true
Function FlushCVars( force%=0 )
    For Local i:ICvar = EachIn ConsoleMap.Values()
        If Not i._system Or force Then ConsoleMap.Remove( i._name )
    Next
End Function

' deletes non-system commands if force is zero, deletes all commands if force is true
Function FlushConCommands( force%=0 )
    For Local i:ICvar = EachIn ConsoleMap.Values()
        If Not i._system Or force Then ConsoleMap.Remove( i._name )
    Next
End Function

' clears the console log (messages logged by ConPrint
Function ClearConsole( )
    ConsoleLog.Clear( )
End Function

' retrieves the console log as a string (useful for writing the log to a file)
Function GetConsoleLog$( )
    Local s$ = ""
    For Local i$ = EachIn ConsoleLog
        s :+ i + "~n"
    Next
    Return s.Trim()
End Function

' retrieves the console log in reverse as an array of strings (useful for displaying the console)
Function ReverseConsoleArray$[]( )
    Local obj:Object[] = (ConsoleLog.Reversed( ).ToArray( ))
    Local arr:String[obj.Length]
    For Local i:Int = 0 To arr.Length-1
        arr[i] = obj[i].ToString()
    Next
    Return arr
End Function

' writes a message to the console log
Function ConPrint( s$ )
    Strip( s, "~r~t", "")
    Local sp$[] = s.Split("~n")
    Local i%
    For i = sp.Length-1 To 0 Step -1
        ConsoleLog.AddLast( sp[i] )
    Next
    RunHooks( ConsolePrintHookID, s )
End Function

' displays all cvars and their values in the console
Function ListCVars( )
    For Local i:ICVar = EachIn ConsoleMap.Values( )
        ConPrint( "~q"+i._name+"~q = ~q"+i._value+"~q" )
    Next
End Function

' lists all console commands in the console
Function ListCommands( )
    For Local i:ICCmd = EachIn ConsoleMap.Values( )
        ConPrint( "~q"+i._name+"~q" )
    Next
End Function

' resets the console to an initial state
Function ResetConsole( )
    FlushConCommands( True )
    FlushCVars( True )
    RegisterConCommand( "listcommands", clistCommands, 1 )
    RegisterConCommand( "listcvars", clistCvars, 1 )
    RegisterConCommand( "clear", cclearConsole, 1 )
    ClearConsole( )
End Function

Private

Function clistCommands%( n$, s$ )
    ListCommands( )
End Function

Function clistCvars%( n$, s$ )
    ListCvars( )
End Function

Function cclearConsole%( n$, s$ )
    ClearConsole( )
End Function

ResetConsole()
