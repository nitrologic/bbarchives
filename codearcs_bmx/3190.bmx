; ID: 3190
; Author: Pineapple
; Date: 2015-02-18 08:24:56
; Title: Escape unsafe characters in strings
; Description: Turns things like " into \" or ~q and back again

'   --+-----------------------------------------------------------------------------------------+--
'     |   This code was originally written by Sophie Kirschner (sophiek@pineapplemachine.com)   |  
'     | It is released as public domain. Please don't interpret that as liberty to claim credit |  
'     |   that isn't yours, or to sell this code when it could otherwise be obtained for free   |  
'     |                because that would be a really shitty thing of you to do.                |
'   --+-----------------------------------------------------------------------------------------+--

superstrict



' Example code

rem

framework brl.standardio

local str$ = "tab: ~t~n double quote: ~q~n single quote: '~n tilde: ~~~n backslash: \"

print "test string looks like this:"
print str

print "c escapes:"
local cescape:stringescape = new stringescape.c()
local cresult$ = cescape.escape( str )
print cresult
assert cescape.unescape( cresult ) = str

print "json escapes:"
local jsonescape:stringescape = new stringescape.json()
local jsonresult$ = jsonescape.escape( str )
print jsonresult
assert jsonescape.unescape( jsonresult ) = str

print "blitzmax escapes:"
local bmaxescape:stringescape = new stringescape.bmax()
local bmaxresult$ = bmaxescape.escape( str )
print bmaxresult
assert bmaxescape.unescape( bmaxresult ) = str

endrem



type stringescape

    field escapechar$
    field escapes$[][]
    
    ' intialize with C escapes
    method c:stringescape()
        escapechar = "\"
        escapes = [ ..
            [ "~q", "~q" ], [ "/", "/" ], ..
            [ "'", "'" ], [ "?", "?" ], ..
            [ chr($08), "b" ], .. ' backspace
            [ chr($0C), "f" ], .. ' form feed
            [ chr($0A), "n" ], .. ' newline
            [ chr($0D), "r" ], .. ' carriage return
            [ chr($09), "t" ], .. ' tab
            [ chr($0B), "v" ], .. ' vertical tab
            [ chr($07), "a" ]  .. ' alarm
        ]
        return self
    end method
    
    ' initialize with javascript escapes
    method js:stringescape()
        escapechar = "\"
        escapes = [ ..
            [ "~q", "~q" ], [ "'", "'" ], ..
            [ chr($08), "b" ], .. ' backspace
            [ chr($0C), "f" ], .. ' form feed
            [ chr($0A), "n" ], .. ' newline
            [ chr($0D), "r" ], .. ' carriage return
            [ chr($09), "t" ]  .. ' tab
        ]
        return self
    end method
    
    ' initialize with json escapes
    method json:stringescape()
        escapechar = "\"
        escapes = [ ..
            [ "~q", "~q" ], ..
            [ chr($08), "b" ], .. ' backspace
            [ chr($0C), "f" ], .. ' form feed
            [ chr($0A), "n" ], .. ' newline
            [ chr($0D), "r" ], .. ' carriage return
            [ chr($09), "t" ], .. ' tab
            [ chr($0B), "v" ]  .. ' vertical tab
        ]
        return self
    end method
    
    ' initialize with blitzmax escapes
    method bmax:stringescape()
        escapechar = "~~"
        escapes = [ ..
            [ chr($00), "0" ], .. ' null
            [ chr($09), "t" ], .. ' tab
            [ chr($0D), "r" ], .. ' return
            [ chr($0A), "n" ], .. ' newline
            [ chr($22), "q" ]  .. ' quote
        ]
        return self
    end method
    
    ' initialize with regex escapes
    method regex:stringescape()
        escapechar = "\"
        escapes = [ ..
            [ "/", "/" ], [ "^", "^" ], ..
            [ "$", "$" ], [ ".", "." ], ..
            [ "|", "|" ], [ "?", "?" ], ..
            [ "*", "*" ], [ "+", "+" ], ..
            [ "(", "(" ], [ ")", ")" ], ..
            [ "[", "[" ], [ "]", "]" ], ..
            [ "{", "{" ], [ "}", "}" ]  ..
        ]
        return self
    end method
    
    ' escape unsafe characters in input string
    method escape$( str$ )
        local out$ = ""
        for local i% = 0 until str.length
            local char$ = chr( str[i] )
            local needesc% = (char = escapechar)
            local escchar$
            if not needesc
                for local j% = 0 until escapes.length
                    if char = escapes[j][0]
                        needesc = true
                        escchar = escapes[j][1]
                        exit
                    endif
                next
            endif
            if needesc
                out :+ escapechar
                if escchar
                    out :+ escchar
                else
                    out :+ char
                endif
            else
                out :+ char
            endif
        next
        return out
    end method
    ' get escaped string with unsafe chars not escaped
    method unescape$( str$ )
        local out$ = ""
        for local i% = 0 until str.length
            local char$ = chr( str[i] )
            if char = escapechar and i-1 < str.length
                local escchar$ = str[i+1]
                local subchar$ = escchar
                for local j% = 0 until escapes.length
                    if char = escapes[j][1]
                        subchar = escapes[j][0]
                        exit
                    endif
                next
                out :+ subchar
                i :+ 1
            else
                out :+ char
            endif
        next
        return out
    end method
    
end type
