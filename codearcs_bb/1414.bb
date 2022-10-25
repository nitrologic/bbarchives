; ID: 1414
; Author: HappyCat
; Date: 2005-06-30 01:52:49
; Title: SQLite 3 decls
; Description: Decls for SQLite 3 lightweight database engine

; Tested with SQLite version 3.2.2

; SQLite3 available from:    http://www.sqlite.org
; SQLite3 API documentation: http://www.sqlite.org/capi3ref.html

.lib "sqlite3.dll"


; SQLLite3 Functions Included --------------------------------------------------

; sqlite3_bind_blob
; sqlite3_bind_double
; sqlite3_bind_int
; sqlite3_bind_null
; sqlite3_bind_parameter_count
; sqlite3_bind_parameter_index
; sqlite3_bind_parameter_name
; sqlite3_bind_text
; sqlite3_busy_timeout
; sqlite3_changes
; sqlite3_close
; sqlite3_column_blob
; sqlite3_column_bytes
; sqlite3_column_count
; sqlite3_column_decltype
; sqlite3_column_double
; sqlite3_column_int
; sqlite3_column_name
; sqlite3_column_text
; sqlite3_column_type
; sqlite3_complete
; sqlite3_data_count
; sqlite3_db_handle
; sqlite3_errcode
; sqlite3_errmsg
; sqlite3_exec
; sqlite3_expired
; sqlite3_finalize
; sqlite3_get_autocommit
; sqlite3_interrupt
; sqlite3_last_insert_rowid
; sqlite3_libversion
; sqlite3_open
; sqlite3_prepare
; sqlite3_reset
; sqlite3_step
; sqlite3_total_changes
; sqlite3_transfer_bindings


; Notes --------------------------------------------------------------

; For functions that return a handle as an out parameter (ie. the 
; Handle* parameters of SQLite3_Open and SQLite3_Prepare)
; declare a Type with a single integer field, for example:

;	Type HandleContainer
;		Field TheHandle
;	End Type

; then pass an instance of the Type in as the required parameter.
; When it's done the integer field of the instance will contain the
; handle that you have to pass into the other functions.


; Opening and Closing ------------------------------------------------

SQLite3_Open%(Filename$, DatabaseHandle*) : "sqlite3_open"
SQLite3_Close%(DatabaseHandle) : "sqlite3_close"


; Misc ---------------------------------------------------------------

SQLite3_LibVersion$() : "sqlite3_libversion"
SQLite3_Busy_TimeOut%(DatabaseHandle, TimeOut): "sqlite3_busy_timeout"
SQLite3_Get_AutoCommit%(DatabaseHandle) : "sqlite3_get_autocommit"
SQLite3_Interrupt(DatabaseHandle) : "sqlite3_interrupt"


; Errors -------------------------------------------------------------

SQLite3_ErrCode%(DatabaseHandle) : "sqlite3_errcode"
SQLite3_ErrMsg$(DatabaseHandle) : "sqlite3_errmsg"

; Always seems to return 0.
SQLite3_Complete%(SQL$) : "sqlite3_complete"


; Executing SQL without results --------------------------------------

; As Blitz3D doesn't have function pointers the CallBack is pointless
; and this can only really be used for result-less SQL statements.
; Also, I've never got the Error return to work. So just pass in
; zeros for the last three parameters and use SQLite3_ErrMsg if you
; need to get the error message.
SQLite3_Exec%(DatabaseHandle, SQL$, CallBack, FirstParam, Error) : "sqlite3_exec"

SQLite3_Changes%(DatabaseHandle): "sqlite3_changes"
SQLite3_Total_Changes%(DatabaseHandle) : "sqlite3_total_changes"
SQLite3_Last_Insert_RowID%(DatabaseHandle): "sqlite3_last_insert_rowid"


; Executing SQL with results -----------------------------------------

; Never got the SQLTail to work so just pass in a zero.
SQLite3_Prepare%(DatabaseHandle, SQL$, LengthOfSQL, StatementHandle*, SQLTail) : "sqlite3_prepare"

SQLite3_Step%(StatementHandle) : "sqlite3_step"
SQLite3_Reset%(StatementHandle) : "sqlite3_reset"
SQLite3_Finalize%(StatementHandle) : "sqlite3_finalize"
SQLite3_Data_Count%(StatementHandle) : "sqlite3_data_count"
SQLite3_DB_Handle%(StatementHandle) : "sqlite3_db_handle"

; Probably not required as none of the functions that can cause a
; statement to expire are included here.
SQLite3_Expired%(StatementHandle): "sqlite3_expired"


; SQL Parameter Binding ----------------------------------------------

SQLite3_Bind_Parameter_Count%(StatementHandle): "sqlite3_bind_parameter_count"
SQLite3_Bind_Parameter_Index%(StatementHandle, ParameterName$): "sqlite3_bind_parameter_index"
SQLite3_Bind_Parameter_Name$(StatementHandle, ParameterIndex): "sqlite3_bind_parameter_name"

; Never tested this for real, but it should work.
SQLite3_Transfer_Bindings%(StatementHandle1, StatementHandle2): "sqlite3_transfer_bindings"

SQLite3_Bind_Null%(StatementHandle, Index) : "sqlite3_bind_null"
SQLite3_Bind_Int%(StatementHandle, Index, Value) : "sqlite3_bind_int"

; If you pass -1 for LengthOfText it will work it out for itself.
; Pass a zero in for Destructor.
SQLite3_Bind_Text%(StatementHandle, Index, Value$, LengthOfText, Destructor) : "sqlite3_bind_text"

; Never tried this so it probably won't work.
; Pass a zero in for Destructor.
SQLite3_Bind_Blob%(StatementHandle, Index, Value, LengthOfBlob, Destructor) : "sqlite3_bind_blob"

; Doesn't seem to work unfortunately.
SQLite3_Bind_Double%(StatementHandle, Index, Value#) : "sqlite3_bind_double"


; Getting values from executed SQL  ----------------------------------

SQLite3_Column_Count%(StatementHandle): "sqlite3_column_count"
SQLite3_Column_Name$(StatementHandle, ColumnIndex) : "sqlite3_column_name"
SQLite3_Column_Type%(StatementHandle, ColumnIndex) : "sqlite3_column_type"
SQLite3_Column_DeclType$(StatementHandle, ColumnIndex) : "sqlite3_column_decltype"
SQLite3_Column_Int%(StatementHandle, ColumnIndex) : "sqlite3_column_int"
SQLite3_Column_Double#(StatementHandle, ColumnIndex) : "sqlite3_column_double"
SQLite3_Column_Text$(StatementHandle, ColumnIndex) : "sqlite3_column_text"
SQLite3_Column_Bytes%(StatementHandle, ColumnIndex) : "sqlite3_column_bytes"

; Never tried this so it probably won't work.
SQLite3_Column_Blob%(StatementHandle, ColumnIndex) : "sqlite3_column_blob"


; SQLite3 Functions Not Included --------------------------------------

; The following SQLite3 functions have not been included here:

; sqlite3_aggregate_context	- used by user defined functions, which aren't included here
; sqlite3_aggregate_count	- used by user defined functions, which aren't included here
; sqlite3_bind_int64		- Int64 means nothing to Blitz, use Int version instead
; sqlite3_bind_text16		- UTF16 means nothing to Blitz, use normal version instead
; sqlite3_busy_handler		- requires function pointers, which Blitz doesn't have
; sqlite3_clear_bindings	- just didn't work, claimed that function didn't exist in the .dll
; sqlite3_collation_needed	- requires function pointers, which Blitz doesn't have
; sqlite3_collation_needed16	- requires function pointers, which Blitz doesn't have
; sqlite3_column_bytes16	- UTF16 means nothing to Blitz, use normal version instead
; sqlite3_column_decltype16	- UTF16 means nothing to Blitz, use normal version instead
; sqlite3_column_int64		- Int64 means nothing to Blitz, use Int version instead
; sqlite3_column_name16		- UTF16 means nothing to Blitz, use normal version instead
; sqlite3_column_text16		- UTF16 means nothing to Blitz, use normal version instead
; sqlite3_commit_hook		- requires function pointers, which Blitz doesn't have
; sqlite3_complete16		- UTF16 means nothing to Blitz, use normal version instead
; sqlite3_create_collation	- requires function pointers, which Blitz doesn't have
; sqlite3_create_collation16	- requires function pointers, which Blitz doesn't have
; sqlite3_create_function	- requires function pointers, which Blitz doesn't have
; sqlite3_create_function16	- requires function pointers, which Blitz doesn't have
; sqlite3_errmsg16		- UTF16 means nothing to Blitz, use normal version instead
; sqlite3_free			- only really required by C
; sqlite3_get_table		- couldn't work out how to get the data out
; sqlite3_free_table		- not required as I couldn't get sqlite3_get_table to work
; sqlite3_global_recover	- only really required by C
; sqlite3_mprintf		- only really required by C
; sqlite3_open16		- UTF16 means nothing to Blitz, use normal version instead
; sqlite3_prepare16		- UTF16 means nothing to Blitz, use normal version instead
; sqlite3_progress_handler	- requires function pointers, which Blitz doesn't have
; sqlite3_result_blob		- used by user defined functions, which aren't included here
; sqlite3_result_double		- used by user defined functions, which aren't included here
; sqlite3_result_error		- used by user defined functions, which aren't included here
; sqlite3_result_error16	- used by user defined functions, which aren't included here
; sqlite3_result_int		- used by user defined functions, which aren't included here
; sqlite3_result_int64		- used by user defined functions, which aren't included here
; sqlite3_result_null		- used by user defined functions, which aren't included here
; sqlite3_result_text		- used by user defined functions, which aren't included here
; sqlite3_result_text16		- used by user defined functions, which aren't included here
; sqlite3_result_text16be	- used by user defined functions, which aren't included here
; sqlite3_result_text16le	- used by user defined functions, which aren't included here
; sqlite3_result_value		- used by user defined functions, which aren't included here
; sqlite3_set_authorizer	- requires function pointers, which Blitz doesn't have
; sqlite3_sleep			- just didn't work, claimed that function didn't exist in the .dll
; sqlite3_trace			- requires function pointers, which Blitz doesn't have
; sqlite3_user_data		- used by user defined functions, which aren't included here
; sqlite3_value_blob		- used by user defined functions, which aren't included here
; sqlite3_value_bytes		- used by user defined functions, which aren't included here
; sqlite3_value_bytes16		- used by user defined functions, which aren't included here
; sqlite3_value_double		- used by user defined functions, which aren't included here
; sqlite3_value_int		- used by user defined functions, which aren't included here
; sqlite3_value_int64		- used by user defined functions, which aren't included here
; sqlite3_value_text		- used by user defined functions, which aren't included here
; sqlite3_value_text16		- used by user defined functions, which aren't included here
; sqlite3_value_text16be	- used by user defined functions, which aren't included here
; sqlite3_value_text16le	- used by user defined functions, which aren't included here
; sqlite3_value_type		- used by user defined functions, which aren't included here
; sqlite3_vmprintf		- only really required by C
