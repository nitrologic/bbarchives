; ID: 3176
; Author: Pineapple
; Date: 2015-01-07 11:50:31
; Title: String comparison to pattern using wildcards such as ? and *
; Description: Support for both greedy and lazy quantifiers

'   --+-----------------------------------------------------------------------------------------+--
'     |   This code was originally written by Sophie Kirschner (sophiek@pineapplemachine.com)   |  
'     | It is released as public domain. Please don't interpret that as liberty to claim credit |  
'     |   that isn't yours, or to sell this code when it could otherwise be obtained for free   |  
'     |                because that would be a really shitty thing of you to do.                |
'   --+-----------------------------------------------------------------------------------------+--

SuperStrict



' Example code

Rem

Function metatest( pattern:String, str:String, expected:Int, caseSensitive:Int = True )
    Local result:Int = matchWild( pattern, str, caseSensitive )
    Local failtext:String = ""
    If result <> expected failtext = " (FAILED!)"
    Print "pattern: ~q"+pattern+"~q string: ~q"+str+"~q result: "+result+" expected: "+expected+failtext
End Function

Print;Print "Ordinary strings"
metatest( "test", "test", 1 )
metatest( "test", "toast", 0 )

Print;Print "Wildcard ?"
metatest( "t?st", "test", 1 )
metatest( "t?st", "toot", 0 )

Print;Print "Wildcard *"
metatest( "*", "teeeeeeEEEESSSt", 1 )
metatest( "te*st", "test", 1 )
metatest( "t*st", "teeest", 1 )
metatest( "t*st", "test test test", 1 )
metatest( "one*two*three*four", "onebbbdddtwobbthreefour", 1 )
metatest( "*yes", "testyes", 1 )
metatest( "test*", "testyes", 1 )
metatest( "*testyes*", "testyes", 1 )
metatest( "t*st", "testno", 0 )

Print;Print "Wildcard ."
metatest( ".", "teeeeeeEEEESSSt", 1 )
metatest( "te.st", "test", 1 )
metatest( "t.st", "teeest", 1 )
metatest( "t.st", "test test test", 0 )
metatest( "t.st", "testno", 0 )

Print;Print "Wildcard +"
metatest( "+", "teeeeeeEEEESSSt", 1 )
metatest( "te+st", "test", 0 )
metatest( "t+t", "test", 1 )
metatest( "te+st", "test test test", 1 )
metatest( "t+st", "testno", 0 )
metatest( "testno+", "testno", 0 )
metatest( "test+", "testyes", 1 )

Print;Print "Wildcard %"
metatest( "%", "teeeeeeEEEESSSt", 1 )
metatest( "te%st", "test", 0 )
metatest( "t%t", "test", 1 )
metatest( "te%st", "test test test", 0 )
metatest( "testno%", "testno", 0 )
metatest( "test%", "testyes", 1 )

Print;Print "Blank strings"
metatest( "*", "", 1 )
metatest( ".", "", 1 )
metatest( "+", "", 0 )
metatest( "%", "", 0 )

Print;Print "Case sensitivity"
metatest( "test", "TEST", 0 )
metatest( "test", "TEST", 1, False )

Print;Print "Escaped chars"
metatest( "escape\?\?", "escape??", 1 )
metatest( "a*\*", "a*", 1 )
metatest( "esc*\?\?", "escape??", 1 )
metatest( "esc*b\?\?", "escape??", 0 )

EndRem



Const wildcardAscEsc:Int = Asc( "\" )
Const wildcardAsc1:Int = Asc( "?" )
Const wildcardAscPgreedy:Int = Asc( "+" )
Const wildcardAscNgreedy:Int = Asc( "*" )
Const wildcardAscPlazy:Int = Asc( "%" )
Const wildcardAscNlazy:Int = Asc( "." )



Rem

Fairly simple checking whether a string conforms to a pattern.
The following are various wildcards. Anything else in the pattern must be an exact match to the string.
In the pattern, escape a special character by preceding it with a \. Escape a \ by writing \\.

    ?       Match any one character
    +       Greedily match one or more of any character.
    *       Greedily match zero or more of any character.
    %       Lazily match one or more of any character.
    .       Lazily match zero or more of any character.
    
EndRem

Function matchWild:Int( pattern:String, str:String, caseSensitive:Int = True, initx:Int = 0, inity:Int = 0 )

    ' Match a single character
    Function matchChar:Int( pattern:String, y:Int, str:String, x:Int )
        'print "[matching: "+disp(pattern,y) + " , " + disp(str,x)+"]" ' Debug
        If pattern[y] = wildcardAscEsc And y+1 < pattern.length
            If pattern[y+1] = str[x]
                Return 2
            Else
                Return 0
            EndIf
        ElseIf      (pattern[y] = str[x] Or pattern[y] = wildcardAsc1) And ..
                    Not(pattern[y] = wildcardAscPgreedy Or pattern[y] = wildcardAscNgreedy ..
                    Or pattern[y] = wildcardAscPlazy Or pattern[y] = wildcardAscNlazy)
            Return 1
        EndIf
    End Function
    
    ' Good for debugging
    Function disp:String( str:String, p:Int )
        Return str[..p] + "[" + Chr(str[p]) + "]" + str[p+1..] 
    End Function

    ' Essential vars
    Local x:Int = initx, y:Int = inity, ch:Int
    
    'print "[call: "+disp(pattern,y) + " , " + disp(str,x)+"]" ' Debug
    
    ' Handle case insensitivity
    If Not caseSensitive
        str = str.ToLower()
        pattern = pattern.ToLower()
    EndIf
    
    ' Loop
    Repeat
        
        ' Check for termination of string and/or pattern
        If y >= pattern.length
            Return x >= str.length
        ElseIf x >= str.length
            Return y+1 >= pattern.length And (pattern[y] = wildcardAscNlazy Or pattern[y] = wildcardAscNgreedy)
        EndIf
        
        ' Check for exact match for non-special character
        ch = matchChar( pattern, y, str, x )
        If ch
            x :+ 1; y :+ ch
            
        ' Evaluate .
        ElseIf pattern[y] = wildcardAscNlazy
            y :+ 1
            If y >= pattern.length Return True
            While x < str.length And y < pattern.length
                ch = matchChar( pattern, y, str, x )
                If ch Exit Else x :+ 1
            Wend
            
        ' Evaluate %
        ElseIf pattern[y] = wildcardAscPlazy
            y :+ 1; x :+ 1
            If y >= pattern.length Return True
            While x < str.length And y < pattern.length
                ch = matchChar( pattern, y, str, x )
                If ch Exit Else x :+ 1
            Wend
        
        ' Evaluate *
        ElseIf pattern[y] = wildcardAscNgreedy
            y :+ 1
            If y >= pattern.length Return True
            While x < str.length And y < pattern.length
                ch = matchChar( pattern, y, str, x )
                If ch And matchWild( pattern, str, True, x, y ) Return True Else x :+ 1
            Wend
        
        ' Evaluate +
        ElseIf pattern[y] = wildcardAscPgreedy
            y :+ 1; x :+ 1
            If y >= pattern.length Return True
            While x < str.length And y < pattern.length
                ch = matchChar( pattern, y, str, x )
                If ch And matchWild( pattern, str, True, x, y ) Return True Else x :+ 1
            Wend
            
        ' Not a match
        Else
            Return False
            
        EndIf
        
    Forever
End Function
