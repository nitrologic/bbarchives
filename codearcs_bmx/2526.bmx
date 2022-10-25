; ID: 2526
; Author: Brazilian Joe
; Date: 2009-07-11 12:06:16
; Title: Cross-platform CpuCount()
; Description: Adding the function CpuCount which returns the, er, number of cpus on the system. Useful for multi-threading.

----------DONT SAVE THIS LINE. save the contents as cpucount.macos.c----------
#include <stdio.h>
#include <sys/param.h>
#include <sys/sysctl.h>

int CpuCount () {
	int mib[2];
	size_t len;
	int cpun = 1;
	mib[0] = CTL_HW;
	mib[1] = HW_NCPU;
	len = sizeof(cpun);
	if (sysctl(mib, 2, &cpun, &len, NULL, NULL == -1)) {
		cpun=1;
	}
    return cpun;
}
----------EOF cpucount.macos.c----------
----------DONT SAVE THIS LINE. save the contents as cpucount.bmx------------
?macos
Import "cpucount.macos.c"
?
?win32
Type SYSTEM_INFO
	Field wProcessorArchitecture:Short
	Field wReserved:Short
	Field dwPageSize:Int
	Field lpMinimumApplicationAddress:Byte Ptr
	Field lpMaximumApplicationAddress:Byte Ptr
	Field dwActiveProcessorMask:Int
	Field dwNumberOfProcessors:Int
	Field dwProcessorType:Int
	Field dwAllocationGranularity:Int
	Field wProcessorLevel:Short
	Field wProcessorRevision:Short
End Type

Extern "win32"
	Function GetSystemInfo (si:Byte Ptr)
End Extern

Function CpuCount:Int()
	Local info:SYSTEM_INFO=New SYSTEM_INFO
	GetSystemInfo(info)
	Return info.dwNumberOfProcessors
End Function
?linux
Function CpuCount:Int()
	Local file:TStream=ReadFile("/sys/devices/system/cpu/present")
	If Not file RuntimeError "could not open file openfile.bmx"
	Local cpus_str$ =  ReadLine(file)
	Local cpus_a$[] = cpus_str.Split("-")
	If cpus_a.length = 1 Then
		cpus_n = 1
	Else
		Local cpus_n:Int = Int(cpus_a[1])+1
	End If
	Return cpus_n
End Function
?macos
Extern
	Function CpuCount()
End Extern
?
'uncomment this one to test functionality standalone
'Print CpuCount()
------------EOF cpucount.bmx----------
