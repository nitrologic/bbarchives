; ID: 2363
; Author: Barton
; Date: 2008-11-25 16:19:51
; Title: Set Task-Application Priority
; Description: Set Task Priority of your Game or Appliaction for running without freezes !

Here my SetPriority userlib for set the Task Priority. It is very good if running many other applications in the background during playing your game. Your Game or Application running then better without freezes. :)

==============
SetPriority.decls
==============

.lib "kernel32.dll"

GetCurrentProcess%()  :"GetCurrentProcess"
GetCurrentThread%()   :"GetCurrentThread"
GetPriorityClass%()      :"GetPriorityClass"
GetThreadPriority%()     :"GetThreadPriority"
SetPriorityClass%(Process%, Value%)   :"SetPriorityClass"
SetThreadPriority%(Process%, Value%):"SetThreadPriority"



=========
example:
=========

;// THREAD
Const THREAD_BASE_PRIORITY_IDLE = -15
Const THREAD_BASE_PRIORITY_LOWRT = 15
Const THREAD_BASE_PRIORITY_MIN = -2
Const THREAD_BASE_PRIORITY_MAX = 2
Const THREAD_PRIORITY_LOWEST = THREAD_BASE_PRIORITY_MIN
Const THREAD_PRIORITY_HIGHEST = THREAD_BASE_PRIORITY_MAX
Const THREAD_PRIORITY_BELOW_NORMAL = (THREAD_PRIORITY_LOWEST + 1)
Const THREAD_PRIORITY_ABOVE_NORMAL = (THREAD_PRIORITY_HIGHEST - 1)
Const THREAD_PRIORITY_IDLE = THREAD_BASE_PRIORITY_IDLE
Const THREAD_PRIORITY_NORMAL = 0
Const THREAD_PRIORITY_TIME_CRITICAL = THREAD_BASE_PRIORITY_LOWRT
;// CLASS
Const REALTIME_PRIORITY_CLASS = 256
Const HIGH_PRIORITY_CLASS = 128
Const IDLE_PRIORITY_CLASS = 64
Const NORMAL_PRIORITY_CLASS = 32

hThread%  = GetCurrentThread()
hProcess% = GetCurrentProcess()
SetThreadPriority hThread%, THREAD_PRIORITY_LOWEST  ;<< SET YOUR THREAD PRIORITY HERE!!
SetPriorityClass  hProcess%, IDLE_PRIORITY_CLASS ;<< SET YOUR PRIORITY CLASS HERE!!



You can check it when you start your task manager and right click the mouse button over your app-name.exe in the list.
