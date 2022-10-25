; ID: 2013
; Author: skn3[ac]
; Date: 2007-05-15 05:48:29
; Title: win32: start,stop,open,install,uninstall windows services!!!
; Description: Simplified control over windows services

Strict

'--- windows api ---
Import "-ladvapi32"

Private
	Extern "win32"
		Function _OpenSCManager:Int(nmachinename$z,ndatabasename$z,ndesiredaccess:Int) = "OpenSCManagerA@12"
		Function _CloseServiceHandle:Int(nhandle:Int) = "CloseServiceHandle@4"
		Function _OpenService:Int(nmananger:Int,nservicename$z,ndesiredaccess:Int) = "OpenServiceA@12"
		Function _StartService:Int(nservice:Int,nserviceargs:Int,nserviceargvectors$z) = "StartServiceA@12"
		Function _ControlService:Int(nservice:Int,ncontrol:Int,nservicestatus:Byte Ptr) = "ControlService@12"
		Function _CreateService:Int(nscmanager:Int,nservicename$z,nndisplayname$z,ndesiredaccess:Int,nservicetype:Int,nstarttype:Int,nerrorcontrol:Int,nbinarypathname$z,nloadordergroup$z,ntagid:Int,ndependancies$z,nservicestartname$z,npassword$z) = "CreateServiceA@52"
		Function _QueryServiceStatusEx:Int(nservice:Int,ninfolevel:Int,nbuffer:Byte Ptr,bufsize:Int,nbytesneeded:Byte Ptr) = "QueryServiceStatusEx@20"
		Function _DeleteService:Int(nservice:Int) = "DeleteService@4"
		Function _GetLastError:Int() = "GetLastError@0"
		Function _GetTickCount:Int() = "GetTickCount@0"
		Function _Sleep:Int(nmilliseconds:Int) = "Sleep@4"
	End Extern
	
	Const SC_STATUS_PROCESS_INFO:Int = 0
	Const SC_MANAGER_ENUMERATE_SERVICE:Int = 4
	Const SC_MANAGER_CREATE_SERVICE:Int = 2
	
	Const SERVICE_START:Int = 16
	Const SERVICE_STOP:Int = 32
	Const SERVICE_QUERY_STATUS:Int  = 4
	Const SERVICE_DELETE:Int = 65536
	
	Const SERVICE_ALL_ACCESS:Int = 983551
	
	Const SERVICE_KERNEL_DRIVER:Int = 1
	Const SERVICE_FILE_SYSTEM_DRIVER:Int = 2
	Const SERVICE_WIN32_OWN_PROCESS:Int = 16
	Const SERVICE_WIN32_SHARE_PROCESS:Int = 32
	
	Const SERVICE_CONTROL_STOP:Int = 1
	Const SERVICE_CONTROL_PAUSE:Int = 2
	Const SERVICE_CONTROL_CONTINUE:Int = 3
	
	Const SERVICE_BOOT_START:Int = 0
	Const SERVICE_SYSTEM_START:Int = 1
	Const SERVICE_AUTO_START:Int = 2
	Const SERVICE_DEMAND_START:Int = 3
	Const SERVICE_DISABLED:Int = 4
	
	Const SERVICE_ERROR_IGNORE:Int = 0
	Const SERVICE_ERROR_NORMAL:Int = 1
	Const SERVICE_ERROR_SEVERE:Int = 2
	Const SERVICE_ERROR_CRITICAL:Int = 3
	
	Const ERROR_ACCESS_DENIED:Int = 5
	Const ERROR_INVALID_HANDLE:Int = 6
	Const ERROR_PATH_NOT_FOUND:Int = 3
	Const ERROR_SERVICE_ALREADY_RUNNING:Int = 1056
	Const ERROR_SERVICE_DATABASE_LOCKED:Int = 1055
	Const ERROR_SERVICE_DEPENDENCY_DELETED:Int = 1075
	Const ERROR_SERVICE_DEPENDENCY_FAIL:Int = 1068
	Const ERROR_SERVICE_DISABLED:Int = 1058
	Const ERROR_SERVICE_LOGON_FAILED:Int = 1069
	Const ERROR_SERVICE_MARKED_FOR_DELETE:Int = 1072
	Const ERROR_SERVICE_NO_THREAD:Int = 1054
	Const ERROR_SERVICE_REQUEST_TIMEOUT:Int = 1053
	
	Const SERVICE_STOPPED:Int = 1
	Const SERVICE_START_PENDING:Int = 2
	Const SERVICE_STOP_PENDING:Int = 3
	Const SERVICE_RUNNING:Int = 4
	Const SERVICE_CONTINUE_PENDING:Int = 5
	Const SERVICE_PAUSE_PENDING:Int = 6
	Const SERVICE_PAUSED:Int = 7
	
	Type SERVICE_STATUS
		Global instance:SERVICE_STATUS = New SERVICE_STATUS
		Field dwServiceType:Int
		Field dwCurrentState:Int
		Field dwControlsAccepted:Int
		Field dwWin32ExitCode:Int
		Field dwServiceSpecificExitCode:Int
		Field dwCheckPoint:Int
		Field dwWaitHint:Int
		Field dwProcessId:Int
		Field dwServiceFlags:Int
	End Type
Public

'--- user parts ---
'these values correspond to the win api values
Const SERVICESTATUS_STOPPED:Int = 1
Const SERVICESTATUS_STARTING:Int = 2
Const SERVICESTATUS_STOPPING:Int = 3
Const SERVICESTATUS_RUNNING:Int = 4
Const SERVICESTATUS_RESUMING:Int = 5
Const SERVICESTATUS_PAUSING:Int = 6
Const SERVICESTATUS_PAUSED:Int = 7

Type tservice
	Field handle:Int
	Field _status:Int
	Field _checkpoint:Int
	Field _waithint:Int
	Field _waittime:Int
	
	Method Delete:Int()
		'put cleanup code in here
		close()
	End Method
	
	Method started:Int()
		updateservicestatus()
		Return _status = SERVICE_RUNNING
	End Method
	
	Method stopped:Int()
		updateservicestatus()
		Return _status = SERVICE_STOPPED
	End Method
	
	Method paused:Int()
		updateservicestatus()
		Return _status = SERVICE_PAUSED
	End Method
	
	Method status:Int()
		updateservicestatus()
		Return _status
	End Method
	
	Method updateservicestatus:Int()
		'this function will use teh global instance of teh SERVICE query class to fill
		'the status values into the service. only select values are required!
		
		Local temp_bytesneeded:Int
		Local temp_result:Int
		
		If handle
			temp_result = _QueryServiceStatusEx(handle,SC_STATUS_PROCESS_INFO,SERVICE_STATUS.instance,SizeOf(SERVICE_STATUS.instance),Varptr(temp_bytesneeded))
			
			If temp_result
				'get status of the service
				_status = SERVICE_STATUS.instance.dwCurrentState
				_checkpoint = SERVICE_STATUS.instance.dwCheckPoint
				_waithint = SERVICE_STATUS.instance.dwWaitHint
				_waittime = _waithint / 10
				
				'fix wait time
				If _waittime < 1000
					_waittime = 1000
				ElseIf _waittime > 10000
					_waittime = 10000
				End If
				
				Return True
			End If
		End If
		
		Return False
	End Method
	
	Method close:Int()
		If handle
			_CloseServiceHandle(handle)
			handle = 0
		End If
	End Method
	
	Method stop:Int()
		'this function will attempt to stop a service
		'check to see the current status of the service
		
		Local temp_ticks:Int
		Local temp_checkpoint:Int
		
		If updateservicestatus() = 0 Return False
		
		Select _status
			Case SERVICESTATUS_STARTING,SERVICESTATUS_STOPPING,SERVICESTATUS_RESUMING,SERVICESTATUS_PAUSING
				'service is already doing something
				Return False
				
			'Case SERVICESTATUS_PAUSED
				'resume the service
				
			Case SERVICESTATUS_RUNNING,SERVICESTATUS_PAUSED
				'control the service
				If _ControlService(handle,SERVICE_CONTROL_STOP,SERVICE_STATUS.instance) = 0 Return False
				
				'update status
				updateservicestatus()
				
				'record start point
				temp_ticks = _GetTickCount()
				temp_checkpoint = _checkpoint
				
				While _status = SERVICESTATUS_STOPPING
					_Sleep(_waittime)
					
					'update the service status and check the status could be checked
					If updateservicestatus() = 0 Exit
					
					'check to see if the service is making progress
					If _checkpoint > temp_checkpoint
						'update tick and checkpoint values
						temp_checkpoint = _checkpoint
						temp_ticks = _GetTickCount()
					Else
						'no progress made check for exit
						If _GetTickCount() - temp_ticks > _waithint Exit
					End If
				Wend
				
				'check to see if the service started
				If _status = SERVICESTATUS_STOPPED Return True
				
			Case SERVICESTATUS_STOPPED
				'service already running
				Return True
		End Select
	End Method
	
	Method start:Int()
		'this function will attempt to call the service api to start
		
		Local temp_ticks:Int
		Local temp_checkpoint:Int
		
		'starts the service
		If handle
			'check to see the current status of the service
			If updateservicestatus() = 0 Return False
			
			Select _status
				Case SERVICESTATUS_STOPPING,SERVICESTATUS_RESUMING,SERVICESTATUS_PAUSING,SERVICESTATUS_PAUSED
					'service is already doing something
					Return False
				
				Case SERVICESTATUS_STARTING
					'the service is already starting so wait until it has started
					
					'update status
					updateservicestatus()
					
					'record start point
					temp_ticks = _GetTickCount()
					temp_checkpoint = _checkpoint
					
					While _status = SERVICESTATUS_STARTING
						_Sleep(_waittime)
						
						'update the service status and check the status could be checked
						If updateservicestatus() = 0 Exit
						
						'check to see if the service is making progress
						If _checkpoint > temp_checkpoint
							'update tick and checkpoint values
							temp_checkpoint = _checkpoint
							temp_ticks = _GetTickCount()
						Else
							'no progress made check for exit
							If _GetTickCount() - temp_ticks > _waithint Exit
						End If
					Wend
					
					'check to see if the service started
					If _status = SERVICESTATUS_RUNNING Return True
				
				Case SERVICESTATUS_STOPPED
					'start the service
					If _StartService(handle,0,Null) = 0 Return False
					
					'update status
					updateservicestatus()
					
					'record start point
					temp_ticks = _GetTickCount()
					temp_checkpoint = _checkpoint
					
					While _status = SERVICESTATUS_STARTING
						_Sleep(_waittime)
						
						'update the service status and check the status could be checked
						If updateservicestatus() = 0 Exit
						
						'check to see if the service is making progress
						If _checkpoint > temp_checkpoint
							'update tick and checkpoint values
							temp_checkpoint = _checkpoint
							temp_ticks = _GetTickCount()
						Else
							'no progress made check for exit
							If _GetTickCount() - temp_ticks > _waithint Exit
						End If
					Wend
					
					'check to see if the service started
					If _status = SERVICESTATUS_RUNNING Return True
					
				Case SERVICESTATUS_RUNNING
					'service already running
					Return True
			End Select
		End If
		
		Return False
	End Method
	
	Method uninstall:Int()
		'this function will attempt to uninstall a service
		If handle
			'attempt to stop the service before deleting
			If stopped() = False stop()
			'delete the service
			If _DeleteService(handle)
				close()
				Return True
			End If
		End If
	End Method
End Type

Function InstallService:tservice(ndisplayname:String,nname:String,npath:String,nstartup:Int=True)
	Local temp_servicemanagerhandle:Int
	Local temp_servicehandle:Int
	Local temp_starttype:Int
	Local temp_service:tservice

	'attempt tp connect to the service mananger
	temp_servicemanagerhandle = _OpenSCManager(Null,Null,SC_MANAGER_CREATE_SERVICE | SC_MANAGER_ENUMERATE_SERVICE)
	
	'define start type
	If nstartup
		temp_starttype = SERVICE_AUTO_START
	Else
		temp_starttype = SERVICE_DEMAND_START
	End If
	
	'check to see if could connect
	If temp_servicemanagerhandle = 0
		'couldnt connect to the service mananger
		Return Null
	Else
		'see if service is already installed
		temp_servicehandle = _OpenService(temp_servicemanagerhandle,nname,SERVICE_QUERY_STATUS)
		If temp_servicehandle
			'service already exists
			_CloseServiceHandle(temp_servicehandle)
			_CloseServiceHandle(temp_servicemanagerhandle)
			Return Null
		End If
		
		'attempt to create the service
		temp_servicehandle = _CreateService(temp_servicemanagerhandle,nname,ndisplayname,SERVICE_ALL_ACCESS,SERVICE_WIN32_OWN_PROCESS,temp_starttype,SERVICE_ERROR_NORMAL,"~q"+npath+"~q",Null,Null,Null,Null,"")
		
		'check to see the service could be created
		If temp_servicehandle
			'close the service manager
			_CloseServiceHandle(temp_servicemanagerhandle)
			
			'create a new service object
			temp_service = New tservice
			temp_service.handle = temp_servicehandle
			
			Return temp_service
		End If
	End If
End Function

Function OpenService:tservice(nname:String)
	'open the service manager
	Local temp_servicemanagerhandle:Int
	Local temp_servicehandle:Int
	Local temp_service:tservice
	
	'attempt tp connect to the service mananger
	temp_servicemanagerhandle = _OpenSCManager(Null,Null,SC_MANAGER_ENUMERATE_SERVICE)
	
	'check to see if could connect
	If temp_servicemanagerhandle = 0
		'couldnt connect to the service mananger
	Else
		'attempt to open the specified service from teh service mananger
		temp_servicehandle = _OpenService(temp_servicemanagerhandle,nname,SERVICE_ALL_ACCESS)'SERVICE_QUERY_STATUS | SERVICE_START | SERVICE_STOP | SERVICE_DELETE)
		
		'check to see if the service could be opened
		If temp_servicehandle = 0
			'unable to open the service
			'close the service manager handle
			_CloseServiceHandle(temp_servicemanagerhandle)
		Else
			'able to open the service
			'close the service manager handle
			_CloseServiceHandle(temp_servicemanagerhandle)
			
			'create a new service object
			temp_service = New tservice
			temp_service.handle = temp_servicehandle
			
			'return the service object
			Return temp_service
		End If
	End If
End Function
