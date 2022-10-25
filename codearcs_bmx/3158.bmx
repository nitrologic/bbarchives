; ID: 3158
; Author: BlitzSupport
; Date: 2014-11-09 13:52:24
; Title: Simple CPU monitor (Windows)
; Description: Simple CPU monitor to give overall percentage, as per Task Manager

SuperStrict

Extern "win32"
	Function GetSystemTimes (lpIdleTime:Byte Ptr, lpKernelTime:Byte Ptr, lpUserTime:Byte Ptr)
End Extern

Type Win32FileTime
	Field lo:Int
	Field hi:Int
End Type

Type CPUMonitor

	' References...
	
	' http://www.codeproject.com/Articles/9113/Get-CPU-Usage-with-GetSystemTimes
	' http://www.purebasic.fr/english/viewtopic.php?t=26200&start=3
	' http://msdn.microsoft.com/en-us/library/windows/desktop/ms724400%28v=vs.85%29.aspx

	Const SAMPLES:Int = 3

	Field cpu_sample:Int [SAMPLES]
	
	Field idle:Win32FileTime = New Win32FileTime
	Field kern:Win32FileTime = New Win32FileTime
	Field user:Win32FileTime = New Win32FileTime
	
	Field last_idle:Win32FileTime = New Win32FileTime
	Field last_kern:Win32FileTime = New Win32FileTime
	Field last_user:Win32FileTime = New Win32FileTime

	' Pass False for realtime value, though result will fluctuate a lot more...

	Method Usage:Int (average:Int = True)
	
		GetSystemTimes idle, kern, user
		
		' Apparently only need the 'low' part of the FILETIME structure...
		
		Local idl:Int = idle.lo - last_idle.lo
		Local krn:Int = kern.lo - last_kern.lo
		Local usr:Int = user.lo - last_user.lo
	
		last_idle.lo = idle.lo
		last_kern.lo = kern.lo
		last_user.lo = user.lo
	
		Local sys:Int = krn + usr
		
		Local cpu:Int
		
		If sys
			cpu = Int ((sys - idl) * 100 / sys)
		Else
			cpu = 0
		EndIf
	
		If average
		
			Local avg_cpu:Int = 0
			
			For Local loop:Int = 0 Until SAMPLES - 1
				cpu_sample [loop] = cpu_sample [loop + 1]
				'Print "cpu_sample [" + loop + "] = " + cpu_sample [loop]
				avg_cpu = avg_cpu + cpu_sample [loop]
			Next
			
			cpu_sample [SAMPLES - 1] = cpu
		
			avg_cpu = avg_cpu + cpu_sample [SAMPLES - 1]
			
			avg_cpu = avg_cpu / SAMPLES
			
			' Print "Current sample: " + cpu + " (Average of last three samples: " + avg_cpu + ")"
		
			If avg_cpu < 0 Then avg_cpu = 0
			
			Return avg_cpu
			
		Else
		
			If cpu < 0 Then cpu = 0
			
			Return cpu
			
		EndIf
		
	End Method
	
End Type

' D E M O . . .

Local cpu:CPUMonitor = New CPUMonitor

Repeat
	
	Print "Average CPU usage: " + cpu.Usage () + "%"
'	Print "Current CPU usage: " + cpu.Usage (False) + "%"
	
	Delay 100 ' Sampling every 1000 ms or more will give errors! Don't know why...
	
Until KeyHit (KEY_ESCAPE)

End
