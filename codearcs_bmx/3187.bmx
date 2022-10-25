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
    filelog.info("girl I want")
    filelog.warning("to be with you")
    filelog.critical("all of the time")
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
stdiolog.debug("well")
stdiolog.info("that's just, like")
stdiolog.warning("your opinion, man")

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



' these functions tell loggers how to output to target types, you can also implement your own as needed
private
function StreamLogger(target:object,message:LoggerMessage)
    tstream(target).writeline(message.toString())
end function
function ListLogger(target:object,message:LoggerMessage)
    tlist(target).addlast(message)
end function
public

' logger object, this thing does the actual logging
type Logger

    ' helpful constants
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
    
    ' list of logged messages
    field messages:tlist = new tlist
    
    ' minimum priority to output a message to the target
    field minpriority% = 0
    
    ' the target object for output
    field target:object
    
    ' the function to use for writing logged messages to the target
    field outputfunc(target:object,message:LoggerMessage)
    
    ' create a new logger
    function create:Logger(target:object,outputfunc(t:object,m:LoggerMessage),minpriority%)
        local n:Logger = new Logger
        n.settarget(target,outputfunc)
        n.minpriority = minpriority
        return n
    end function
    function streamtarget:Logger(target:tstream,minpriority%=0)
        return create(target,StreamLogger,minpriority)
    end function
    function consoletarget:Logger(minpriority%=0)
        return create(standardiostream,StreamLogger,minpriority)
    end function
    function listtarget:Logger(target:tlist,minpriority%=0)
        return create(target,ListLogger,minpriority)
    end function
    
    ' set logger output target
    method settarget:Logger(targ:object,func(t:object,m:LoggerMessage))
        target = targ
        outputfunc = func
    end method
    method setstreamtarget:Logger(targ:tstream)
        settarget(targ,StreamLogger)
        return self
    end method
    method setconsoletarget:Logger()
        settarget(standardiostream,StreamLogger)
    end method
    method setlisttarget:Logger(targ:tlist)
        settarget(targ,ListLogger)
    end method
    
    ' get/set min message priority
    method getpriority%()
        return minpriority
    end method
    method setpriority:Logger(priority%)
        minpriority = priority
        return self
    end method
    
    ' logging methods
    method debug:LoggerMessage(text$)
        local message:LoggerMessage = LoggerMessage.debug(text)
        output(message,target,outputfunc)
        return message
    end method
    method info:LoggerMessage(text$)
        local message:LoggerMessage = LoggerMessage.info(text)
        output(message,target,outputfunc)
        return message
    end method
    method warning:LoggerMessage(text$)
        local message:LoggerMessage = LoggerMessage.warning(text)
        output(message,target,outputfunc)
        return message
    end method
    method error:LoggerMessage(text$)
        local message:LoggerMessage = LoggerMessage.error(text)
        output(message,target,outputfunc)
        return message
    end method
    method critical:LoggerMessage(text$)
        local message:LoggerMessage = LoggerMessage.critical(text)
        output(message,target,outputfunc)
        return message
    end method
    method output%(message:LoggerMessage,target:object,func(t:object,m:LoggerMessage))
        messages.addlast(message)
        if target and outputfunc and message.priority >= minpriority
            outputfunc(target,message)
            return true
        else
            return false
        endif
    end method
    
    ' flush the logger's messages buffer
    method flush()
        messages.clear()
    end method
    
    ' retroactively write all buffered messages to the output target (or to the logger's current target if none is specified)
    method writeall:Logger(targ:object=null,func(t:object,m:LoggerMessage)=null)
        if not targ targ = target
        if not func func = outputfunc
        for local message:LoggerMessage = eachin messages
            output(message,targ,func)
        next
        return self
    end method
    
end type

' logger message object, loggers make these
type LoggerMessage

    ' the text content of the message
    field text$
    
    ' a prefix for the message, such as "INFO: " or "ERROR: "
    field prefix$
    
    ' the message's priority; the logger's minimum priority must be at least this high in order for it to output to target
    field priority%
    
    ' the time when the message was created and its string representation
    field time:timestamp
    field timestr$
    
    ' message creation methods
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
    
    ' turn it into a string
    method tostring$()
        return timestr + " " + prefix + text
    end method
    
end type
