; ID: 2344
; Author: schilcote
; Date: 2008-10-24 14:17:00
; Title: InputTCP$
; Description: Input for TCP streams!

Function InputTCP$(input_stream)
Repeat
;no operation because we're just waiting for something to come up on the TCP stream
Until(ReadAvail(input_stream)) 
ret_val$=ReadLine(input_stream)
Return (ret_val$)
End Function
