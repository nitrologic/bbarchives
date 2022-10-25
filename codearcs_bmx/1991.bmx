; ID: 1991
; Author: skn3[ac]
; Date: 2007-04-17 06:39:24
; Title: Registry Module
; Description: Read / Write to the windows registry easily

Strict

Import "-ladvapi32"

Extern "win32"
	Function _RegCreateKeyEx:Int(key:Int,subkey$z,reserved:Int,class$z,options:Int,desired:Int,securityattributes:Byte Ptr,result:Byte Ptr,disposition:Byte Ptr)="RegCreateKeyExA@36"
	Function _RegOpenKeyEx:Int(key:Int,subkey$z,options:Int,desired:Int,result:Byte Ptr) = "RegOpenKeyExA@20"
	Function _RegCloseKey:Int(key:Int) = "RegCloseKey@4"
	Function _RegSetValueEx:Int(key:Int,valuename$z,reserved:Int,dwtype:Int,data:Byte Ptr,datasize:Int) = "RegSetValueExA@24"
	Function _RegQueryValueEx:Int(key:Int,valuename$z,reserved:Int,lptype:Byte Ptr,data:Byte Ptr,datasize:Byte Ptr) = "RegQueryValueExA@24"
	Function _RegDeleteValue:Int(key:Int,valuename$z) = "RegDeleteValueA@8"
	Function _RegDeleteKey:Int(key:Int,subkey$z) = "RegDeleteKeyA@8"
	Function _RegEnumKeyEx:Int(key:Int,index:Int,name:Byte Ptr,namesize:Byte Ptr,reserved:Int,class$z,classsize:Byte Ptr,lastwritetime:Byte Ptr) = "RegEnumKeyExA@32"
EndExtern

'win32 constants
Const HKEY_CLASSES_ROOT:Int = 2147483648
Const HKEY_CURRENT_USER:Int = 2147483649
Const HKEY_LOCAL_MACHINE:Int = 2147483650
Const HKEY_USERS:Int = 2147483651

Const REG_OPTION_NON_VOLATILE:Int = 0

Const ERROR_NO_MORE_ITEMS:Int = 259

Const KEY_QUERY_VALUE:Int = 1
Const KEY_SET_VALUE:Int = 2
Const KEY_CREATE_SUB_KEY:Int = 4
Const KEY_ENUMERATE_SUB_KEYS:Int = 8

Const REG_CREATED_NEW_KEY:Int = 1
Const REG_OPENED_EXISTING_KEY:Int = 2

Const REG_SZ:Int = 1
Const REG_DWORD:Int = 4
Const REG_QWORD:Int = 11

'types
Type tregkey
	'fields
	Field root:Int
	Field handle:Int = 0
	Field path:String

	'methods
	Method Delete:Int()
		'close the reg key
		close()
	End Method
		
	Method removevalue:Int(nname:String)
		'delete a value regardless of type
		If handle And _RegDeleteValue(handle,nname) = 0
			'return value deleted
			Return True
		End If
		
		'return value not deleted
		Return False
	End Method
	
	Method remove:Int(nrecurse:Int = False)
		'removes itself from the registry
		If nrecurse
			'recurse key and delete all sub keys and values
			If handle And tregkey.Remove_Recurse(root,path)
				close()
				'return key deleted
				Return True
			End If
		Else
			'delete key
			If handle And _RegDeleteKey(handle,path) = 0
				close()
				'return key deleted
				Return True
			End If
		End If
		
		'return key not deleted
		Return False
	End Method
	
	Method getint:Int(nname:String)
		'get an int
		Local temp_type:Int
		Local temp_size:Int = 4
		Local temp_value:Int
		
		If handle And _RegQueryValueEx(handle,nname,0,Varptr(temp_type),Null,Null) = 0 And temp_type = REG_DWORD And _RegQueryValueEx(handle,nname,0,Null,Varptr(temp_value),Varptr(temp_size)) = 0
			'return value
			Return temp_value
		End If
		
		'return false if no value (can be decieving though as false is also 0)
		Return False
	End Method
	
	Method getlong:Int(nname:String)
		'get an int
		Local temp_type:Int
		Local temp_size:Int = 8
		Local temp_value:Long
		
		If handle And _RegQueryValueEx(handle,nname,0,Varptr(temp_type),Null,Null) = 0 And temp_type = REG_QWORD And _RegQueryValueEx(handle,nname,0,Null,Varptr(temp_value),Varptr(temp_size)) = 0
			'return value
			Return temp_value
		End If
		
		'return false if no value (can be decieving though as false is also 0)
		Return False
	End Method
	
	Method getstring:String(nname:String)
		'get an int
		Local temp_type:Int
		Local temp_size:Int
		Local temp_value:String
		Local temp_cstring:Byte Ptr
		
		If handle And _RegQueryValueEx(handle,nname,0,Varptr(temp_type),Null,Varptr(temp_size)) = 0 And temp_type = REG_SZ
			'create a buffer to recieve the string
			temp_cstring = MemAlloc(temp_size)
			'retrieve the string
			If _RegQueryValueEx(handle,nname,0,Null,temp_cstring,Varptr(temp_size))
				'free created c string
				MemFree(temp_cstring)
				Return ""
			End If
			
			'convert c string to string
			temp_value = String.FromCString(temp_cstring)
			
			'free created c string
			MemFree(temp_cstring)
			
			'return value
			Return temp_value
		End If
		
		'return false if no value (can be decieving though as false is also "")
		Return ""
	End Method

	Method setint:Int(nname:String,nvalue:Int)
		'set an int
		If handle And _RegSetValueEx(handle,nname,0,REG_DWORD,Varptr(nvalue),4) = 0
			'return int set
			Return True
		End If
		
		'return int not set
		Return False
	End Method
	
	Method setlong:Int(nname:String,nvalue:Long)
		'set a long
		If handle And _RegSetValueEx(handle,nname,0,REG_QWORD,Varptr(nvalue),8) = 0
			'return long set
			Return True
		End If
		
		'return long not set
		Return False
	End Method
	
	Method setstring:Int(nname:String,nvalue:String)
		'set a string
		Local temp_cstring:Byte Ptr = nvalue.tocstring()
		If handle And _RegSetValueEx(handle,nname,0,REG_SZ,temp_cstring,nvalue.length) = 0
			'free teh created c string
			MemFree(temp_cstring)
			'return string set
			Return True
		End If
		
		'return string not set
		'free the created c string
		MemFree(temp_cstring)
		Return False
	End Method
	
	Method close:Int()
		'close the reg key
		If handle And handle <> HKEY_CLASSES_ROOT And handle <> HKEY_CURRENT_USER And handle <> HKEY_LOCAL_MACHINE And handle <> HKEY_USERS
			_RegCloseKey(handle)
		End If
		handle = 0
	End Method
	
	
	'functions
	Function Remove_Recurse:Int(nroot:Int,npath:String)
		Local temp_result:Int
		Local temp_index:Int = 0
		Local temp_cstring:Byte Ptr
		Local temp_cstringsize:Int = 255
		Local temp_path:String
	
		'try to delete the key without recursing
		If _RegDeleteKey(nroot,npath) = 0
			Return True
		End If
		
		'open the key for recursing
		If _RegOpenKeyEx(nroot,npath,0,KEY_QUERY_VALUE | KEY_CREATE_SUB_KEY | KEY_SET_VALUE | KEY_ENUMERATE_SUB_KEYS,Varptr(temp_result)) = 0
			'create c string
			temp_cstring = MemAlloc(temp_cstringsize)
			'recurse the keys keys
			While _RegEnumKeyEx(temp_result,temp_index,temp_cstring,Varptr(temp_cstringsize),Null,Null,Null,Null) = 0
				temp_path = npath
				If temp_path.length temp_path :+ "\"
				temp_path :+ String.FromCString(temp_cstring)
				tregkey.Remove_Recurse(nroot,temp_path)
				temp_index :+ 1
				temp_cstringsize = 255
			Wend
			'close the key
			_RegCloseKey(temp_result)
			'free the cstring
			MemFree(temp_cstring)
		End If
		
		'try to delete key again
		If _RegDeleteKey(nroot,npath) = 0
			Return True
		End If
		
		Return False
	End Function
	
	Function PredefinedKey:Int(nroot:String)
		Select nroot
			Case "HKEY_CLASSES_ROOT"
				Return HKEY_CLASSES_ROOT
			Case "HKEY_CURRENT_USER"
				Return HKEY_CURRENT_USER
			Case "HKEY_LOCAL_MACHINE"
				Return HKEY_LOCAL_MACHINE
			Case "HKEY_USERS"
				Return HKEY_USERS
		End Select
		Return 0
	End Function
	
	Function ExtractSegments:String[](npath:String)
		Local temp_segments:String[]
		Local temp_count:Int
		Local temp_pos:Int
		Local temp_segment:String
		Local temp_subpos:Int
		Local temp_subsegment1:String
		Local temp_subsegment2:String
		
		'fix forward slashes
		npath = npath.replace("/","\")
		
		temp_pos = npath.find("\")
		While temp_pos <> -1
			'extract the segment
			temp_segment = npath[..temp_pos]
			If temp_segment.length
				'resize segment array
				temp_segments = temp_segments[..temp_count+1]
				
				'set segment
				temp_segments[temp_count] = temp_segment
				
				'increase segment count
				temp_count :+ 1
			End If
			
			npath = npath[temp_pos+1..]
			temp_pos = npath.find("\")
		Wend
		'add last
		temp_segment = npath
		If temp_segment.length
			'resize segment array
			temp_segments = temp_segments[..temp_count+1]
			
			'set segment
			temp_segments[temp_count] = temp_segment
			
			'increase segment count
			temp_count :+ 1
		End If
		
		Return temp_segments
	End Function
End Type

Function CreateRegKey:tregkey(npath:String)
	Local temp_regkey:tregkey
	Local temp_segments:String[] = tregkey.ExtractSegments(npath)
	Local temp_root:Int
	Local temp_path:String
	Local temp_key:String
	Local temp_index:Int
	Local temp_result:Int
	Local temp_disposition:Int
	
	If temp_segments.length > 1
		'get root
		temp_root = tregkey.PredefinedKey(temp_segments[0])
		
		'get path
		If temp_segments.length > 2
			For temp_index = 1 Until temp_segments.length-1
				If temp_path.length temp_path :+ "\"
				temp_path :+ temp_segments[temp_index]
			Next
		End If
		
		'get key
		temp_key = temp_segments[temp_segments.length-1]
		
		'create the key
		If _RegCreateKeyEx(temp_root,temp_path+"\"+temp_key,0,"",REG_OPTION_NON_VOLATILE,KEY_QUERY_VALUE | KEY_CREATE_SUB_KEY | KEY_SET_VALUE | KEY_ENUMERATE_SUB_KEYS,Null,Varptr(temp_result),Varptr(temp_disposition)) = 0
			'create the reg key
			temp_regkey = New tregkey
			temp_regkey.root = temp_root
			temp_regkey.handle = temp_result
			temp_regkey.path = temp_path+"\"+temp_key
		End If
		
		'return the reg key
		Return temp_regkey
	End If
	
	Return Null
End Function

Function OpenRegKey:tregkey(npath:String)
	Local temp_regkey:tregkey
	Local temp_segments:String[] = tregkey.ExtractSegments(npath)
	Local temp_root:Int
	Local temp_path:String
	Local temp_key:String
	Local temp_index:Int
	Local temp_result:Int
	
	If temp_segments.length > 1
		'get root
		temp_root = tregkey.PredefinedKey(temp_segments[0])
		
		'get path
		If temp_segments.length > 2
			For temp_index = 1 Until temp_segments.length-1
				If temp_path.length temp_path :+ "\"
				temp_path :+ temp_segments[temp_index]
			Next
		End If
		
		'get key
		temp_key = temp_segments[temp_segments.length-1]
		
		If _RegOpenKeyEx(temp_root,temp_path+"\"+temp_key,0,KEY_QUERY_VALUE | KEY_CREATE_SUB_KEY | KEY_SET_VALUE | KEY_ENUMERATE_SUB_KEYS,Varptr(temp_result)) = 0
			'create regkey instance
			temp_regkey = New tregkey
			temp_regkey.root = temp_root
			temp_regkey.handle = temp_result
			temp_regkey.path = temp_path+"\"+temp_key
			
			Return temp_regkey
		End If
	End If
End Function
