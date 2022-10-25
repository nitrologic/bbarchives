; ID: 3187
; Author: Pineapple
; Date: 2015-02-12 14:35:30
; Title: Logger
; Description: Flexible logger type makes logging to various targets convenient

'   --+-----------------------------------------------------------------------------------------+--
'     |   This code was originally written by Sophie Kirschner (sophiek@pineapplemachine.com)   |  
'     | It is released as public domain. Please don't interpret that as liberty to claim credit |  
'     |   that isn't yours, or to sell this code when it could otherwise be obtained for free   |  
'     |                because that would be a really shitty thing of you to do.                |
'   --+-----------------------------------------------------------------------------------------+--

superstrict

import brl.stream
import brl.linkedlist
import "timestamp.bmx" ' available here: http://www.blitzmax.com/codearcs/codearcs.php?code=3044



rem

' Example code

print "~nlogger targeting file stream:"
local path$ = "loggertestfile.txt"
if filetype(path) = 0
    local stream:tstream = writefile(path)
    local filelog:Logger = Logger.streamtarget(stream)
    filelog.info("hi!")
    filelog.warning("uh oh")
    filelog.critical("ABANDON SHIP")
    closefile stream
    local file:tstream = readfile("loggertestfile.txt")
    while not eof(file)
        print file.readline()
    wend
    closefile file
    deletefile path
else
    print "oops, example file already exists. not overwriting it just to be safe."
endif

print "~nlogger targeting standard io:"
local stdiolog:Logger = Logger.consoletarget()
stdiolog.debug("welcome")
stdiolog.info("you are entering into")
stdiolog.error("THE HOUSE OF HORRORS")

print "~nlogger targeting list:"
local list:tlist = new tlist
local listlog:Logger = Logger.listtarget(list)
listlog.warning("happy feet")
listlog.error("WOMBO COMBO")
listlog.critical("THAT AIN'T FALCO")
listlog.info("we're all a little bit hard")
for local message:LoggerMessage = eachin list
    print message.toString()
next

endrem



private

function StreamLogger(target:object var,message:LoggerMessage)
    tstream(target).writeline(message.toString())
end function
function ListLogger(target:object var,message:LoggerMessage)
    tlist(target).addlast(message)
end function

public

type Logger
    const PREFIX_DEBUG$ = "DEBUG: "
    const PREFIX_INFO$ = "INFO: "
    const PREFIX_WARNING$ = "WARNING: "
    const PREFIX_ERROR$ = "ERROR: "
    const PREFIX_CRITICAL$ = "CRITICAL: "
    const PRIORITY_DEBUG% = 10
    const PRIORITY_INFO% = 20
    const PRIORITY_WARNING% = 30
    const PRIORITY_ERROR% = 40
    const PRIORITY_CRITICAL% = 50
    field messages:tlist = new tlist
    field minpriority% = 0
    field target:object
    field outputfunc(target:object var,message:LoggerMessage)
    function create:Logger(target:object,outputfunc(t:object var,m:LoggerMessage),minpriority%)
        local n:Logger = new Logger
        n.target = target
        n.outputfunc = outputfunc
        n.minpriority = minpriority
        return n
    end function
    function streamtarget:Logger(target:tstream var,minpriority%=0)
        return create(target,StreamLogger,minpriority)
    end function
    function consoletarget:Logger(minpriority%=0)
        return create(standardiostream,StreamLogger,minpriority)
    end function
    function listtarget:Logger(target:tlist var,minpriority%=0)
        return create(target,ListLogger,minpriority)
    end function
    method debug:LoggerMessage(text$)
        local message:LoggerMessage = LoggerMessage.debug(text)
        output(message)
        return message
    end method
    method info:LoggerMessage(text$)
        local message:LoggerMessage = LoggerMessage.info(text)
        output(message)
        return message
    end method
    method warning:LoggerMessage(text$)
        local message:LoggerMessage = LoggerMessage.warning(text)
        output(message)
        return message
    end method
    method error:LoggerMessage(text$)
        local message:LoggerMessage = LoggerMessage.error(text)
        output(message)
        return message
    end method
    method critical:LoggerMessage(text$)
        local message:LoggerMessage = LoggerMessage.critical(text)
        output(message)
        return message
    end method
    method output%(message:LoggerMessage)
        messages.addlast(message)
        if target and outputfunc and message.priority >= minpriority
            outputfunc(target,message)
            return true
        else
            return false
        endif
    end method
    method flush()
        messages.clear()
    end method
end type

type LoggerMessage
    field text$
    field prefix$
    field priority%
    field time:timestamp
    field timestr$
    function create:LoggerMessage(text$,prefix$,priority%)
        local n:LoggerMessage = new LoggerMessage
        n.text = text
        n.prefix = prefix
        n.priority = priority
        n.time = timestamp.now()
        n.timestr = n.time.todottedstring()
        return n
    end function
    function debug:LoggerMessage(text$)
        return create(text,Logger.PREFIX_DEBUG,Logger.PRIORITY_DEBUG)
    end function
    function info:LoggerMessage(text$)
        return create(text,Logger.PREFIX_INFO,Logger.PRIORITY_INFO)
    end function
    function warning:LoggerMessage(text$)
        return create(text,Logger.PREFIX_WARNING,Logger.PRIORITY_WARNING)
    end function
    function error:LoggerMessage(text$)
        return create(text,Logger.PREFIX_ERROR,Logger.PRIORITY_ERROR)
    end function
    function critical:LoggerMessage(text$)
        return create(text,Logger.PREFIX_CRITICAL,Logger.PRIORITY_CRITICAL)
    end function
    method tostring$()
        return timestr + " " + prefix + text
    end method
end type
