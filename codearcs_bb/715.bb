; ID: 715
; Author: Malice
; Date: 2003-06-10 19:05:52
; Title: Function Speed
; Description: Will return the time taken to perfom functions

;Malice's Function Timer
;Please note Although this includes drawing speed to frontbuffer, the main idea is to test the speed
;of mathematical And logical processes.








;Use Input or substitute loop=xxxxx The greater the value, the more accurate result although longer
;Functions may be better tested with a smaller loop.


loop#=Input()
;Sets basic time for for/next and setting timer

control_timer#=MilliSecs()
For f=1 To loop#
Next
control_timer#=(MilliSecs()-control_timer#)

;now to find time of function

function_timer#=MilliSecs()

For g=1 To loop#

;Either use Inclde (as this example) or copy/paste your function HERE
Include "Function.bb"

Next

function_timer#=MilliSecs()-function_timer#

time#=((function_timer#-control_timer#)/loop#)

Cls
t$=" Milliseconds"
If time#>60000 Then t$="Minutes" time#=time#/60000
If time#>1000 Then t$="Seconds" time#=time#/1000
If time<1 Then t$=" (Negligible: less than 1 millisecond)"

Text 10,100,time#+" "+t$


WaitKey

End
