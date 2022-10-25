; ID: 2552
; Author: Jim Teeuwen
; Date: 2009-08-01 08:23:43
; Title: GetOpts
; Description: A small GetOpts implementation for parsing arbitrary commandline options.

Function GetOpts:Int(tokens:String, opt:String Var, optarg:String Var)
	Global av:String[] = Null;
	global isparsed:int = false;
	If(not isparsed) Then
		av = New String[0];
		For Local n:Int = 1 Until AppArgs.Length
			If(AppArgs[n][0..1] = "-") Then
				For Local i:Int = 1 Until AppArgs[n].Length
					av = av[..av.Length + 1];
					av[av.length - 1] = "-" + AppArgs[n][i..i+1];
				Next
			Else
				av = av[..av.Length + 1];
				av[av.length - 1] = AppArgs[n];
			End If
		Next
		isparsed = true;
	End If

	opt = ""; optarg = "";
	If(av.Length = 0) Then Return False;
	opt = av[0]; av = av[1..];

	if(opt[0..1] <> "-") then return true;

	Local idx:Int = tokens.Find(opt[1..]);
	If(idx = -1) Then
		WriteStdErr("Unrecognized option '" + opt + "' specified.~n");
		end;
	End If

	If(idx < tokens.length - 1) Then
		If(tokens[idx+1..idx+2] = ":") Then
			If(av.length = 0 or av[0][0..1] = "-") Then
				WriteStdErr("Missing value for option '" + opt + "'~n");
				end;
			End If
			optarg = av[0]; av = av[1..];
		End If
	End If

	opt = opt[1..];
	Return True;
End Function
