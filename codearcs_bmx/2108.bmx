; ID: 2108
; Author: andre72
; Date: 2007-09-19 03:42:25
; Title: Start/Stop and Control a Windows NT Service
; Description: To start or stop an Windows NT Service and get the state of

'API Constants
Const SERVICES_ACTIVE_DATABASE:String = "ServicesActive"
' Service Control
Const SERVICE_CONTROL_STOP:Int = $1
Const SERVICE_CONTROL_PAUSE:Int = $2
' Service State - for CurrentState
Const SERVICE_STOPPED:Int = $1
Const SERVICE_START_PENDING:Int = $2
Const SERVICE_STOP_PENDING:Int = $3
Const SERVICE_RUNNING:Int = $4
Const SERVICE_CONTINUE_PENDING:Int = $5
Const SERVICE_PAUSE_PENDING:Int = $6
Const SERVICE_PAUSED:Int = $7
'Service Control Manager object specific access types
Const STANDARD_RIGHTS_REQUIRED:Int = $F0000
Const SC_MANAGER_CONNECT:Int = $1
Const SC_MANAGER_CREATE_SERVICE:Int = $2
Const SC_MANAGER_ENUMERATE_SERVICE:Int = $4
Const SC_MANAGER_LOCK:Int = $8
Const SC_MANAGER_QUERY_LOCK_STATUS:Int = $10
Const SC_MANAGER_MODIFY_BOOT_CONFIG:Int = $20
Const SC_MANAGER_ALL_ACCESS:Int = $F003F 'STANDARD_RIGHTS_REQUIRED | SC_MANAGER_CONNECT | SC_MANAGER_CREATE_SERVICE | SC_MANAGER_ENUMERATE_SERVICE | SC_MANAGER_LOCK or SC_MANAGER_QUERY_LOCK_STATUS | SC_MANAGER_MODIFY_BOOT_CONFIG
'Service object specific access types
Const SERVICE_QUERY_CONFIG:Int = $1
Const SERVICE_CHANGE_CONFIG:Int = $2
Const SERVICE_QUERY_STATUS:Int = $4
Const SERVICE_ENUMERATE_DEPENDENTS:Int = $8
Const SERVICE_START:Int = $10
Const SERVICE_STOP:Int = $20
Const SERVICE_PAUSE_CONTINUE:Int = $40
Const SERVICE_INTERROGATE:Int = $80
Const SERVICE_USER_DEFINED_CONTROL:Int = $100
Const SERVICE_ALL_ACCESS:Int = $1FF 'STANDARD_RIGHTS_REQUIRED | SERVICE_QUERY_CONFIG | SERVICE_CHANGE_CONFIG | SERVICE_QUERY_STATUS | SERVICE_ENUMERATE_DEPENDENTS | SERVICE_START | SERVICE_STOP | SERVICE_PAUSE_CONTINUE | SERVICE_INTERROGATE | SERVICE_USER_DEFINED_CONTROL

Type SERVICE_STATUS
    Field dwServiceType:Int
    Field dwCurrentState:Int
    Field dwControlsAccepted:Int
    Field dwWin32ExitCode:Int
    Field dwServiceSpecificExitCode:Int
    Field dwCheckPoint:Int
    Field dwWaitHint:Int
End Type

Extern "Win32"
	Function CloseServiceHandle:Int (hSCObject:Int) = "CloseServiceHandle@4"
	Function ControlService:Int (hService:Int, dwControl:Int, lpServiceStatus:Byte ptr) = "ControlService@12"
	Function OpenSCManager:Int (lpMachineName:Byte Ptr, lpDatabaseName:Byte Ptr, dwDesiredAccess:Int) = "OpenSCManagerA@12"
	Function OpenService:Int (hSCManager:Int, lpServiceName:Byte Ptr, dwDesiredAccess:Int) = "OpenServiceA@12"
	Function QueryServiceStatus (hService:Int, lpServiceStatus:Byte ptr) = "QueryServiceStatus@8"
	Function StartService:Int (hService:Int, dwNumServiceArgs:Int, lpServiceArgVectors:Int) = "StartServiceA@12"
End Extern

Function ServiceStatus:Int(ComputerName:String, ServiceName:String)
	Local ServiceState:SERVICE_STATUS = New SERVICE_STATUS
	Local hSManager:Int = OpenSCManager(ComputerName, SERVICES_ACTIVE_DATABASE, SC_MANAGER_ALL_ACCESS) 
	If hSManager <> 0
		Local hService:Int = OpenService(hSManager, ServiceName, SERVICE_ALL_ACCESS) 
		If hService<>0
			Local hServiceStatus:Int = QueryServiceStatus(hService, ServiceState) 
			CloseServiceHandle hService
		Endif
		CloseServiceHandle hSManager
	Endif
	Return ServiceState.dwCurrentState
End Function

function ServiceStart:Int(ComputerName:String, ServiceName:String)
	Local hSManager:Int = OpenSCManager(ComputerName, SERVICES_ACTIVE_DATABASE, SC_MANAGER_ALL_ACCESS) 
	Local res:Int
	If hSManager <> 0
		Local hService:Int = OpenService(hSManager, ServiceName, SERVICE_ALL_ACCESS) 
		If hService<>0
			res = StartService(hService, 0, 0) 
			CloseServiceHandle hService
		Endif
		CloseServiceHandle hSManager
	Endif
	Return res
End Function

function ServiceStop:Int(ComputerName:String, ServiceName:String)
	Local ServiceState:SERVICE_STATUS = New SERVICE_STATUS
	Local hSManager:Int = OpenSCManager(ComputerName, SERVICES_ACTIVE_DATABASE, SC_MANAGER_ALL_ACCESS) 
	Local res:Int
	If hSManager <> 0
		Local hService:Int = OpenService(hSManager, ServiceName, SERVICE_ALL_ACCESS) 
		If hService<>0
			res = ControlService(hService, SERVICE_CONTROL_STOP, ServiceState) 
			CloseServiceHandle hService
		Endif
		CloseServiceHandle hSManager
	EndIf
	Return res
End Function

function ServicePause:Int(ComputerName:String, ServiceName:String)
	Local ServiceState:SERVICE_STATUS = New SERVICE_STATUS
	Local hSManager:Int = OpenSCManager(ComputerName, SERVICES_ACTIVE_DATABASE, SC_MANAGER_ALL_ACCESS) 
	Local res:Int
	If hSManager <> 0
		Local hService:Int = OpenService(hSManager, ServiceName, SERVICE_ALL_ACCESS) 
		If hService<>0
			res = ControlService(hService, SERVICE_CONTROL_PAUSE, ServiceState) 
			CloseServiceHandle hService
		Endif
		CloseServiceHandle hSManager
	EndIf
	Return res
End Function
