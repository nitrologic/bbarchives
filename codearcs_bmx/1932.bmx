; ID: 1932
; Author: skn3[ac]
; Date: 2007-02-22 21:06:36
; Title: HTTP POST multipart/form-data send POST / UPLOAD to web address
; Description: Lets you POST values and uploads to a given web address

Strict

Framework BRL.LinkedList
Import BRL.Socket
Import BRL.FileSystem
Import BRL.SocketStream
Import BRL.StandardIO

Rem
The Blitz Functions
EndRem

Const inputmode_input:Int = 0
Const inputmode_file:Int = 1

Type tsendhttp
	Field inputs:TList = CreateList()

	
	Function ressolvename:Int(naddress:String)
		'this function will take the given name and find the ip, returning an integer
		Local temp_dotted:Int = True
		Local temp_i:Int
		Local temp_asc:Int
		Local temp_countdots:Int = 0
		
		'check if this domain contains non numeric characters
		For temp_i = 0 Until naddress.length
			temp_asc = naddress[temp_i]
			
			If temp_asc = 46
				temp_countdots :+ 1
			Else
				If temp_asc < 47 Or temp_asc > 57
					temp_dotted = False
					Exit
				End If
			End If
		Next
		
		'check which type of address it is
		If temp_dotted
			'this is an number based (ip)
			'check that ip address has 3 dots in
			If temp_countdots < 3 Return 0
			'convert ip into int ip
			Local temp_oct1:Int
			Local temp_oct2:Int
			Local temp_oct3:Int
			Local temp_oct4:Int
			Local temp_offset1:Int
			Local temp_offset2:Int
			Local temp_offset3:Int
		
			temp_offset1 = naddress.find(".")
			temp_oct1 = Int(naddress[..temp_offset1+1])
			temp_offset2 = naddress.find(".",temp_offset1+1)
			temp_oct2 = Int(naddress[temp_offset1+1..temp_offset2-1])
			temp_offset3 = naddress.find(".",temp_offset2+1)
			temp_oct3 = Int(naddress[temp_offset2+1..temp_offset3-1])
			temp_oct4 = Int(naddress[temp_offset3+1..])
			
			'perform the 4 byte to int conversion
			Return (temp_oct1 Shl 24) + (temp_oct2 Shl 16) + (temp_oct3 Shl 8) + temp_oct4
		Else
			'need to resolve this name
			Local temp_ips:Int[] = HostIps(naddress)
			
			'check for no ips returned
			If temp_ips = Null Return 0
			Return temp_ips[0]
		End If
	End Function
	
	Method send:String[](nhost:String,nport:Int,npath:String)
		'this function will upload a file to a web address via a post form
		Local temp_string:String = ""
		Local temp_boundry:String = "-----------------------------23281168279961"
		Local temp_input:thttpinput
		Local temp_size:Int
		Local temp_socket:tsocket
		Local temp_stream:TStream
		Local temp_file:TStream
		Local temp_ip:Int
		
		'calculate the size of the content
		For temp_input = EachIn inputs
			'test which mode this input is in
			Select temp_input.mode
				Case inputmode_input
					'the input is a value
					temp_size :+ temp_boundry.length + "~r~n".length
					temp_size :+ "Content-Disposition: form-data; name=~q".length + temp_input.name.length + "~q".length + "~r~n".length
					temp_size :+ "~r~n".length
					temp_size :+ temp_input.value.length + "~r~n".length
				Case inputmode_file
					'the input is a file
					temp_size :+ temp_boundry.length + "~r~n".length
					temp_size :+ "Content-Disposition: form-data; name=~q".length + temp_input.name.length + "~q; filename=~q".length + temp_input.filename.length + "~q".length + "~r~n".length
					temp_size :+ "Content-Type: ".length + temp_input.filecontenttype.length + "~r~n".length
					temp_size :+ "~r~n".length
					temp_size :+ FileSize(temp_input.filepath) + "~r~n".length
			End Select
		Next
		temp_size :+ temp_boundry.length + "--".length + "~r~n".length
		temp_size :+ "~r~n".length

		'open connection to server
		temp_socket = CreateTCPSocket()
		If temp_socket = Null
			Return Null
		Else
			'attempt to bind teh socket
			If temp_socket.bind(0) = False
				'couldnt bind the socket
				temp_socket.close()
				Return Null
			Else
				'attempt to connect to remote server
				temp_ip = tsendhttp.ressolvename(nhost)
				
				If temp_socket.connect(temp_ip,nport) = False
					'couldnt connect
					temp_socket.close()
					Return Null
				Else
					'create a stream and send the data
					temp_stream = CreateSocketStream(temp_socket,True)
					
					'write the headers		
					temp_stream.writeline("POST http://" + nhost + npath + " HTTP/1.0")
					temp_stream.writeline("Host: " + nhost)
					temp_stream.writeline("User-Agent: CapUploader")
					temp_stream.writeline("Content-Type: multipart/form-data; boundary=---------------------------23281168279961")
					temp_stream.writeline("Content-Length: " + temp_size)
					temp_stream.writeline("")
					
					'write the inputs
					For temp_input = EachIn inputs
						'test which mode this input is in
						Select temp_input.mode
							Case inputmode_input
								'the input is a value
								'boundry + header + value
								temp_stream.writeline(temp_boundry)
								temp_stream.writeline("Content-Disposition: form-data; name=~q"+temp_input.name+"~q")
								temp_stream.writeline("")
								temp_stream.writeline(temp_input.value)
							Case inputmode_file
								'the input is a file
								temp_stream.writeline(temp_boundry)
								temp_stream.writeline("Content-Disposition: form-data; name=~q"+temp_input.name+"~q; filename=~q"+temp_input.filename+"~q")
								temp_stream.writeline("Content-Type: "+temp_input.filecontenttype)
								temp_stream.writeline("")
								temp_file = ReadFile(temp_input.filepath)
								CopyStream(temp_file,temp_stream)
								temp_file.close()
								temp_stream.writeline("")
						End Select
					Next
					
					'write last boundry
					temp_stream.writeline(temp_boundry + "--")
					temp_stream.writeline("")
					
					'recieve response
					Local temp_return:String[0]
					Local temp_buffer:String
					Local temp_lf:Int = -1
					Local temp_crlf:Int = -1
					Local temp_bytes:Int = 0
					Local temp_lines:Int = 0
					
					'continue scanning for data until timeout or connection closed
					While temp_socket.connected()
						'check to see if bytes are available
						temp_bytes = SocketReadAvail(temp_socket)
						If temp_bytes > 0
							'read in bytes from socket until end of line is reached
							While temp_bytes > 0
								temp_buffer :+ Chr(temp_stream.ReadByte())
								temp_bytes :- 1
							Wend
							
							'see if there is an end of line in the buffer
							temp_crlf = temp_buffer.find("~r~n")
							temp_lf = temp_buffer.find("~n")
							While temp_crlf > -1 Or temp_lf > -1
								'add the line to the return array
								'check to see if the return array needs resizing
								If temp_return.length Mod 100 = 0 temp_return = temp_return[..temp_return.length+100]
								
								'check which end of line to look for
								If temp_crlf > -1 And temp_crlf < temp_lf
									'"~r~n"
									temp_return[temp_lines] = temp_buffer[..temp_crlf]
									temp_buffer = temp_buffer[temp_crlf+2..]
								Else
									'lf
									temp_return[temp_lines] = temp_buffer[..temp_lf]
									temp_buffer = temp_buffer[temp_lf+1..]
								End If
								
								'increase line count
								temp_lines :+ 1
								
								'look for new end of line
								temp_crlf = temp_buffer.find("~r~n")
								temp_lf = temp_buffer.find("~n")
							Wend
						End If
					Wend
					
					'connection was closed or ended
					'add last data in buffer as line
					If temp_return.length Mod 100 = 0 temp_return = temp_return[..temp_return.length+100]
					temp_return[temp_lines] = temp_buffer
					temp_lines :+ 1
					
					'fix size of return array
					temp_return = temp_return[..temp_lines]
					temp_socket.close()
					
					'return the array of data
					Return temp_return
				End If
			End If
 		End If
	End Method
	
	Method addinput:thttpinput(nname:String,nvalue:String)
		'create new input
		Local temp_input:thttpinput = New thttpinput
		
		'setup the input field
		temp_input.mode = inputmode_input
		temp_input.name = nname
		temp_input.value = nvalue
		
		'add input to inputs list
		inputs.addlast(temp_input)
		
		'return the newly created input
		Return temp_input
	End Method
	
	Method addfile:thttpinput(nname:String,npath:String)
		If FileType(npath) <> 1 Return Null
		
		'create new input
		Local temp_input:thttpinput = New thttpinput
		
		'open the file for reading
		Local temp_stream:TStream = ReadFile(npath)
		
		'close stream
		temp_stream.close()
		
		'setup te input
		temp_input.mode = inputmode_file
		temp_input.name = nname
		temp_input.filepath = npath
		temp_input.filename = StripDir(npath)
		
		'set content type
		Select ExtractExt(npath).tolower()
			Case "gif"
				temp_input.filecontenttype = "image/gif"
			Case "png"
				temp_input.filecontenttype = "image/png"
			Case "jpg"
				temp_input.filecontenttype = "image/jpg"
			Case "jpeg"
				temp_input.filecontenttype = "image/jpg"
			Case "bmp"
				temp_input.filecontenttype = "image/bmp"
			Case "txt"
				temp_input.filecontenttype = "text/txt"
			Case "htm"
				temp_input.filecontenttype = "text/htm"
			Case "html"
				temp_input.filecontenttype = "text/html"
			Case "doc"
				temp_input.filecontenttype = "text/doc"
			Case "css"
				temp_input.filecontenttype = "text/css"
			Case "exe"
				temp_input.filecontenttype = "application/exe"
			Case "zip"
				temp_input.filecontenttype = "application/zip"
			Case "rar"
				temp_input.filecontenttype = "application/rar"
			Default
				temp_input.filecontenttype = "text/plain"
		End Select
		
		'add input to inputs list
		inputs.addlast(temp_input)
		
		'return the newly created input
		Return temp_input
	End Method
End Type

Type thttpinput
	Field mode:Int
	Field name:String
	Field filepath:String
	Field filename:String
	Field filecontenttype:String
	Field value:String
End Type
