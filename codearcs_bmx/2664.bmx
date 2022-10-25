; ID: 2664
; Author: Nilium
; Date: 2010-03-13 18:58:14
; Title: BlitzMax Lexer Module
; Description: Module for tokenizing BlitzMax source code

SuperStrict

Module Cower.BMXLexer
ModuleInfo "Name: BlitzMax Lexer"
ModuleInfo "Description: Wrapped lexer for BlitzMax source code"
ModuleInfo "Author: Noel Cower"
ModuleInfo "License: Public Domain"

Import "lexer.c"

Private

Extern "C"
	Function lexer_new@Ptr(source_begin@Ptr, source_end@Ptr)
	Function lexer_destroy(lexer@Ptr)
	Function lexer_run:Int(lexer@Ptr)
	Function lexer_get_error$z(lexer@Ptr)
	Function lexer_get_num_tokens:Int(lexer@Ptr)
	Function lexer_get_token:Int(lexer@Ptr, index%, token@Ptr)
'	 Function lexer_copy_tokens@Ptr(lexer@Ptr, num_tokens%Ptr)'unused
	Function token_to_string@Ptr(tok@Ptr)
	Function free(b@Ptr)
End Extern

Public

Type TToken
	Field kind%				' token_kind_t
	Field _from:Byte Ptr	 ' const char *
	Field _to_:Byte Ptr		  ' const char *
	Field line%				' int
	Field column%			' int
	
	Field _cachedStr$=Null
	
	Method ToString$()
		If _cachedStr = Null Then
			Local cstr@Ptr = token_to_string(Self)
			_cachedStr = String.FromCString(cstr)
			free(cstr)
		EndIf
		Return _cachedStr
	End Method
	
	'#region token_kind_t
	Const TOK_INVALID% = 0

	Const TOK_ID% = 1

	Const TOK_END_KW% = 2

	Const TOK_FUNCTION_KW% = 3
	Const TOK_ENDFUNCTION_KW% = 4

	Const TOK_METHOD_KW% = 5
	Const TOK_ENDMETHOD_KW% = 6

	Const TOK_TYPE_KW% = 7
	Const TOK_EXTENDS_KW% = 8
	Const TOK_ABSTRACT_KW% = 9
	Const TOK_FINAL_KW% = 10
	Const TOK_NODEBUG_KW% = 11
	Const TOK_ENDTYPE_KW% = 12

	Const TOK_EXTERN_KW% = 13
	Const TOK_ENDEXTERN_KW% = 14

	Const TOK_REM_KW% = 15
	Const TOK_ENDREM_KW% = 16

	Const TOK_FLOAT_KW% = 17
	Const TOK_DOUBLE_KW% = 18
	Const TOK_BYTE_KW% = 19
	Const TOK_SHORT_KW% = 20
	Const TOK_INT_KW% = 21
	Const TOK_STRING_KW% = 22
	Const TOK_OBJECT_KW% = 23

	Const TOK_LOCAL_KW% = 24
	Const TOK_GLOBAL_KW% = 25
	Const TOK_CONST_KW% = 26

	Const TOK_VARPTR_KW% = 27
	Const TOK_PTR_KW% = 28
	Const TOK_VAR_KW% = 29

	Const TOK_NULL_KW% = 30

	Const TOK_STRICT_KW% = 31
	Const TOK_SUPERSTRICT_KW% = 32

	Const TOK_FRAMEWORK_KW% = 33

	Const TOK_MODULE_KW% = 34
	Const TOK_MODULEINFO_KW% = 35

	Const TOK_IMPORT_KW% = 36
	Const TOK_INCLUDE_KW% = 37

	Const TOK_PRIVATE_KW% = 38
	Const TOK_PUBLIC_KW% = 39

	Const TOK_OR_KW% = 40
	Const TOK_AND_KW% = 41
	Const TOK_SHR_KW% = 42
	Const TOK_SHL_KW% = 43
	Const TOK_SAR_KW% = 44
	Const TOK_MOD_KW% = 45
	Const TOK_NOT_KW% = 46

	Const TOK_WHILE_KW% = 47
	Const TOK_WEND_KW% = 48
	Const TOK_ENDWHILE_KW% = 49

	Const TOK_FOR_KW% = 50
	Const TOK_NEXT_KW% = 51
	Const TOK_UNTIL_KW% = 52
	Const TOK_TO_KW% = 53
	Const TOK_EACHIN_KW% = 54

	Const TOK_REPEAT_KW% = 55
	Const TOK_FOREVER_KW% = 56

	Const TOK_IF_KW% = 57
	Const TOK_ENDIF_KW% = 58
	Const TOK_ELSE_KW% = 59
	Const TOK_ELSEIF_KW% = 60
	Const TOK_THEN_KW% = 61

	Const TOK_SELECT_KW% = 62
	Const TOK_CASE_KW% = 63
	Const TOK_DEFAULT_KW% = 64
	Const TOK_ENDSELECT_KW% = 65

	Const TOK_SELF_KW% = 66
	Const TOK_SUPER_KW% = 67
	Const TOK_PI_KW% = 68
	Const TOK_NEW_KW% = 69

	Const TOK_PROTOCOL_KW% = 70
	Const TOK_ENDPROTOCOL_KW% = 71
	Const TOK_AUTO_KW% = 72
	Const TOK_IMPLEMENTS_KW% = 73

	Const TOK_COLON% = 74
	Const TOK_QUESTION% = 75
	Const TOK_BANG% = 76
	Const TOK_HASH% = 77
	Const TOK_DOT% = 78
	Const TOK_DOUBLEDOT% = 79
	Const TOK_TRIPLEDOT% = 80
	Const TOK_AT% = 81
	Const TOK_DOUBLEAT% = 82
	Const TOK_DOLLAR% = 83
	Const TOK_PERCENT% = 84
	Const TOK_SINGLEQUOTE% = 85
	Const TOK_OPENPAREN% = 86
	Const TOK_CLOSEPAREN% = 87
	Const TOK_OPENBRACKET% = 88
	Const TOK_CLOSEBRACKET% = 89
	Const TOK_OPENCURL% = 90
	Const TOK_CLOSECURL% = 91
	Const TOK_GREATERTHAN% = 92
	Const TOK_LESSTHAN% = 93
	Const TOK_EQUALS% = 94
	Const TOK_MINUS% = 95
	Const TOK_PLUS% = 96
	Const TOK_ASTERISK% = 97
	Const TOK_CARET% = 98
	Const TOK_TILDE% = 99
	Const TOK_GRAVE% = 100
	Const TOK_BACKSLASH% = 101
	Const TOK_SLASH% = 102
	Const TOK_COMMA% = 103
	Const TOK_SEMICOLON% = 104
	Const TOK_PIPE% = 105
	Const TOK_AMPERSAND% = 106
	Const TOK_NEWLINE% = 107

	Const TOK_ASSIGN_ADD% = 108
	Const TOK_ASSIGN_SUBTRACT% = 109
	Const TOK_ASSIGN_DIVIDE% = 110
	Const TOK_ASSIGN_MULTIPLY% = 111
	Const TOK_ASSIGN_POWER% = 112

	Const TOK_ASSIGN_SHL% = 113
	Const TOK_ASSIGN_SHR% = 114
	Const TOK_ASSIGN_SAR% = 115
	Const TOK_ASSIGN_MOD% = 116

	Const TOK_ASSIGN_XOR% = 117
	Const TOK_ASSIGN_AND% = 118
	Const TOK_ASSIGN_OR% = 119

	Const TOK_ASSIGN_AUTO% = 120
	Const TOK_DOUBLEMINUS% = 121
	Const TOK_DOUBLEPLUS% = 122

	Const TOK_NUMBER_LIT% = 123
	Const TOK_HEX_LIT% = 124
	Const TOK_BIN_LIT% = 125
	Const TOK_STRING_LIT% = 126

	Const TOK_LINE_COMMENT% = 127
	Const TOK_BLOCK_COMMENT% = 128

	Const TOK_EOF% = 129
	
	Const TOK_LAST%=TOK_EOF
	Const TOK_COUNT%=TOK_LAST+1
	'#endregion
End Type

Type TLexer
	Field _lexer@Ptr	' lexer_t
	Field _run:Int = False
	Field _cstr_source@Ptr
	Field _length%
	Field _tokens:TToken[]
	Field _error:String = Null
	
	Method InitWithSource:TLexer(source$)
		Assert _cstr_source=Null Else "Lexer already initialized"
		
		_cstr_source = source.ToCString()
		_length = source.Length
		_lexer = lexer_new(_cstr_source, _cstr_source+_length)
		
		Return Self
	End Method
	
	Method Delete()
		If _cstr_source Then
			MemFree(_cstr_source)
		EndIf
		If _lexer Then
			lexer_destroy(_lexer)
		EndIf
	End Method
	
	Method Run:Int()
		Assert _run = False Else "Lexer has already run"
		_run = True
		Local r% = lexer_run(_lexer)
		If r <> 0 Then
			_error = lexer_get_error(_lexer)
		EndIf
		Return (r=0)
	End Method
	
	Method _cacheTokens()
		If _tokens = Null Then
			_tokens = New TToken[lexer_get_num_tokens(_lexer)]
			For Local init_idx:Int = 0 Until _tokens.Length
				_tokens[init_idx] = New TToken
				lexer_get_token(_lexer, init_idx, _tokens[init_idx])
			Next
		EndIf
	End Method
	
	Method GetToken:TToken(index%)
		_cacheTokens()
		Return _tokens[index]
	End Method
	
	Method GetTokens:TToken[]()
		_cacheTokens()
		Return _tokens
	End Method
	
	Method NumTokens:Int()
		If _tokens Then
			Return _tokens.Length
		EndIf
		Return lexer_get_num_tokens(_lexer)
	End Method
	
	Method GetError$()
		Return _error
	End Method
End Type
