; ID: 3044
; Author: Pineapple
; Date: 2013-03-22 10:41:17
; Title: Timestamp type
; Description: Quite useful for handling dates and times.

'   --+-----------------------------------------------------------------------------------------+--
'     |   This code was originally written by Sophie Kirschner (sophiek@pineapplemachine.com)   |  
'     | It is released as public domain. Please don't interpret that as liberty to claim credit |  
'     |   that isn't yours, or to sell this code when it could otherwise be obtained for free   |  
'     |                because that would be a really shitty thing of you to do.                |
'   --+-----------------------------------------------------------------------------------------+--

SuperStrict

Import brl.stream
Import brl.retro

Type timestamp
    Field hour%,minute%,second%
    Field day%,month%,year%
    Function Create:timestamp(year%=2000,month%=1,day%=1,hour%=1,minute%=1,second%=1)
        Local n:timestamp=New timestamp
        n.year=year;n.month=month;n.day=day;n.hour=hour;n.minute=minute;n.second=second
        Return n
    End Function
    Method tostring$()
        Return getdate()+" - "+gettime()
    End Method
    Method todottedstring$()
        Return year+"."+padbefore(month,"0",2)+"."+padbefore(day,"0",2)+"."+padbefore(hour,"0",2)+"."+padbefore(minute,"0",2)+"."+padbefore(second,"0",2)
    End Method
    Method getdate$()
        Return padbefore(day,"0",2)+"/"+padbefore(month,"0",2)+"/"+Right(year,2)
    End Method
    Method gettime$()
        Local suff$=" AM"
        If hour>11 Then suff=" PM"
        Return padbefore((hour Mod 12),"0",2)+":"+padbefore(minute,"0",2)+":"+padbefore(second,"0",2)+suff
    End Method
    Method write(f:TStream)
        WriteInt f,hour
        WriteInt f,minute
        WriteInt f,second
        WriteInt f,day
        WriteInt f,month
        WriteInt f,year
    End Method
    Function read:timestamp(f:TStream)
        Local n:timestamp=New timestamp
        n.hour=ReadInt(f)
        n.minute=ReadInt(f)
        n.second=ReadInt(f)
        n.day=ReadInt(f)
        n.month=ReadInt(f)
        n.year=ReadInt(f)
        Return n
    End Function
    Function now:timestamp()
        Local n:timestamp=New timestamp
        Local time@[256],buff:Byte[256]
        time_(time)
        Local localtime:Byte Ptr=localtime_( time )
        strftime_(buff,256,"%d",localtime)
        n.day=Int(String.FromCString(buff))
        strftime_(buff,256,"%m",localtime)
        n.month=Int(String.FromCString(buff))
        strftime_(buff,256,"%Y",localtime)
        n.year=Int(String.FromCString(buff))
        strftime_(buff,256,"%H",localtime)
        n.hour=Int(String.FromCString(buff))
        strftime_(buff,256,"%M",localtime)
        n.minute=Int(String.FromCString(buff))
        strftime_(buff,256,"%S",localtime)
        n.second=Int(String.FromCString(buff))
        Return n
    End Function
    Method compare%(o1:Object)
        Local o:timestamp=timestamp(o1)
        If year >o.year Return 1 ElseIf year <o.year Return -1
        If month >o.month Return 1 ElseIf month <o.month Return -1
        If day >o.day Return 1 ElseIf day <o.day Return -1
        If hour >o.hour Return 1 ElseIf hour <o.hour Return -1
        If minute >o.minute Return 1 ElseIf minute <o.minute Return -1
        If second >o.second Return 1 ElseIf second <o.second Return -1
        Return 0
    End Method
    Function padbefore$(s$,char$,length%)
        While Len(s)<length
            s=char+s
        Wend
        Return s
    End Function 
End Type
