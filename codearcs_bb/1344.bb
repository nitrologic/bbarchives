; ID: 1344
; Author: Knotz
; Date: 2005-04-05 12:18:11
; Title: Cheetah DB
; Description: Cheetah DB decls

.lib "Cheetah2.dll"

; ----------------------------------------------------------------------------------------------------
; DATABASE ROUTINES - Creation/Opening/Closing 
; ----------------------------------------------------------------------------------------------------
xdbCreate(dbFile$, AllFields$):"XDBCREATE_Z"
xdbCreateExtended (tFileName$, AllFields$, MemoBlockSize%, Algorithm%, EncryptionKey$):"XDBCREATEEXTENDED_Z"
xdbOpen% (dbFile$, EncryptionKey$):"XDBOPEN_Z"
xdbOpenEX%(dbFile$, AccessMode%, ShareMode%, EncryptionKey$):"XDBOPENEX_Z"
xdbAlias%(dbHandle%):"XDBALIAS_Z"
xdbSetAlias(dbHandle%, AliasName$):"XDBSETALIAS_Z" 
xdbCreateFields(mFileName$):"XDBCREATEFIELDS_Z" 
xdbCreateFieldsExtended(mFileName$, MemoBlockSize%, Algorithm%, EncryptionKey$):"XDBCREATEFIELDSEXTENDED_Z" 
xdbAddField(FieldInfoString$):"XDBADDFIELD_Z" 
xdbClose(dbHandle%):"XDBCLOSE_Z" 
xdbCloseAllIndexes(dbHandle%):"XDBCLOSEALLINDEXES_Z" 

; ----------------------------------------------------------------------------------------------------
; DATABASE ROUTINES - MultiUser % Locking  
; ----------------------------------------------------------------------------------------------------
xdbMultiUser(TrueFalse%, NumRetries%, WaitTime%):"XDBMULTIUSER_Z"  
xdbFailedLockInfo(DBFhandle%, zReason%, zUsername%, zWorkstation%, zLockDate%, zLockTime%):"XDBFAILEDLOCKINFO_Z"
xdbSetExclusiveLock%(DBFhandle%):"XDBSETEXCLUSIVELOCK_Z"  
xdbRemoveExclusiveLock(DBFhandle%, LOCK_NUM%):"XDBREMOVEEXCLUSIVELOCK_Z"  
xdbSetEditLock%(DBFhandle%, RecordNumber%) :"XDBSETEDITLOCK_Z" 
xdbRemoveEditLock(DBFhandle%, LOCK_NUM%):"XDBREMOVEEDITLOCK_Z"  
xdbIsEditLock%(DBFhandle%, RecordNumber%):"XDBISEDITLOCK_Z"   

; ----------------------------------------------------------------------------------------------------
; DATABASE ROUTINES - Adding/Deleting Records 
; ----------------------------------------------------------------------------------------------------
xdbAddRecord(dbHandle%):"XDBADDRECORD_Z" 
xdbAppendRecord(DBFhandle%):"XDBAPPENDRECORD_Z" 
xdbClearBuffer(dbHandle%):"XDBCLEARBUFFER_Z" 
xdbPutRecord(dbHandle%, RN%):"XDBPUTRECORD_Z" 
xdbGetRecord(dbHandle%, RN%):"XDBGETRECORD_Z" 
xdbDeleteRecord(DBFhandle%, RecordNumber%):"XDBDELETERECORD_Z" 
xdbRecallRecord(DBFhandle%, RecordNumber%):"XDBRECALLRECORD_Z" 
xdbDeleted%(DBFhandle%, RecordNumber%):"XDBDELETED_Z" 
xdbFlushDatabase(dbHandle%):"XDBFLUSHDATABASE_Z" 
xdbPack( DBFhandle%):"XDBPACK_Z" 
xdbZap( DBFhandle%):"XDBZAP_Z" 
xdbRecordBuffer%(DBFhandle%):"XDBRECORDBUFFER_Z"
xdbSpeedAppend(DBFhandle%, TrueFalse%):"XDBSPEEDAPPEND_Z" 

; ----------------------------------------------------------------------------------------------------
; DATABASE ROUTINES - Database Information
; ----------------------------------------------------------------------------------------------------
xdbRecordCount%(dbHandle%):"XDBRECORDCOUNT_Z" 
xdbFieldCount%(dbHandle%):"XDBFIELDCOUNT_Z" 
xdbFieldNumber%(dbHandle%, FieldName$):"XDBFIELDNUMBER_Z" 
xdbRecordNumber%(DBFhandle%):"XDBRECORDNUMBER_Z" 
xdbLastUpdated(dbHandle%, YearNr%, MonthNr%, DayNr%):"XDBLASTUPDATED_Z" 
xdbFieldInfo(DBFhandle%, FieldNumber%, zFieldName%, zFieldType%, FieldLength%, FieldDecimals%):"XDBFIELDINFO_Z"
xdbFieldName%(DBFhandle%, FieldNumber%):"XDBFIELDNAME_Z" 
xdbFieldType%(DBFhandle%, FieldNumber%):"XDBFIELDTYPE_Z" 
xdbFieldLength%(DBFhandle%, FieldNumber%):"XDBFIELDLENGTH_Z" 
xdbFieldDecimals%(DBFhandle%, FieldNumber%):"XDBFIELDDECIMALS_Z" 
xdbDatabaseHandles(DBFfilename$, CheetahHandle%, WindowsHandle%):"XDBDATABASEHANDLES_Z" 
xdbIndexHandles(IDXfilename$, CheetahHandle%, WindowsHandle%):"XDBINDEXHANDLES_Z" 

; ----------------------------------------------------------------------------------------------------
; DATABASE ROUTINES - Navigating the Database 
; ----------------------------------------------------------------------------------------------------
xdbEOF%(DBFhandle%):"XDBEOF_Z" 
xdbBOF%(DBFhandle%):"XDBBOF_Z" 
xdbMoveFirst(DBFhandle%, idxHandle%):"XDBMOVEFIRST_Z" 
xdbMoveNext(DBFhandle%, idxHandle%):"XDBMOVENEXT_Z" 
xdbMoveLast(DBFhandle%, idxHandle%):"XDBMOVELAST_Z" 
xdbMovePrev(DBFhandle%, idxHandle%):"XDBMOVEPREV_Z" 
xdbSkipDeleted(DBFhandle%, TrueFalse%):"XDBSKIPDELETED_Z" 
xdbKeyPosition%(IDXhandle%, KeyPosition%):"XDBKEYPOSITION_Z"  

; ----------------------------------------------------------------------------------------------------
; DATABASE ROUTINES - Assigning Field Values 
; ----------------------------------------------------------------------------------------------------
xdbAssignField(dbHandle%, FieldName$, FieldNumber%, FieldString$):"XDBASSIGNFIELD_Z" 
xdbAssignFieldINT(dbHandle%, FieldName$, FieldNumber%, FieldInteger%):"XDBASSIGNFIELDINT_Z" 
xdbAssignFieldLNG(dbHandle%, FieldName$, FieldNumber%, FieldLong%):"XDBASSIGNFIELDLNG_Z" 
xdbAssignFieldSNG(dbHandle%, FieldName$, FieldNumber%, FieldSingle#):"XDBASSIGNFIELDSNG_Z" 
;xdbAssignFieldDBL(dbHandle%, FieldName$, FieldNumber%, FieldDouble#):"XDBASSIGNFIELDDBL_Z" 

; ----------------------------------------------------------------------------------------------------
; DATABASE ROUTINES - Retrieving Field Values 
; ----------------------------------------------------------------------------------------------------
xdbFieldValue$(DBFhandle%, FieldName$, FieldCode%):"XDBFIELDVALUE_Z" 
xdbFieldValueINT%(DBFhandle%, FieldName$, FieldCode%):"XDBFIELDVALUEINT_Z" 
xdbFieldValueLNG%(DBFhandle%, FieldName$, FieldCode%):"XDBFIELDVALUELNG_Z" 
xdbFieldValueSNG#(DBFhandle%, FieldName$, FieldCode%):"XDBFIELDVALUESNG_Z" 
;xdbFieldValueDBL_Z#(DBFhandle%, FieldName$, FieldCode%):"XDBFIELDVALUEDBL_Z" )
xdbFieldPadding(DBFhandle%, TrueFalse%):"XDBFIELDPADDING_Z"

; ----------------------------------------------------------------------------------------------------
; INDEX ROUTINES - Creation/Opening/Closing 
; ----------------------------------------------------------------------------------------------------
xdbCreateIndex(iFilename$, dbHandle%, IndexExpression$, Duplicates%):"XDBCREATEINDEX_Z" 
xdbOpenIndex%(iFilename$, dbHandle%):"XDBOPENINDEX_Z" 
xdbCloseIndex(DBFhandle%, idxHandle%):"XDBCLOSEINDEX_Z" 
xdbReindex(DBFhandle%, idxHandle%, ContinueOrError%):"XDBREINDEX_Z" 
xdbReindexAll(DBFhandle%, ContinueOrError%):"XDBREINDEXALL_Z"  
xdbSeek%(DBFhandle%, idxHandle%, LookForKey$):"XDBSEEK_Z" 
xdbSeekNext%(dbHandle%, idxHandle%, LookForKey$):"XDBSEEKNEXT_Z" 
xdbSeekPartial%(dbHandle%, idxHandle%, LookForKey$):"XDBSEEKPARTIAL_Z" 
xdbSeekPartialNext%(dbHandle%, idxHandle%, LookForKey$):"XDBSEEKPARTIALNEXT_Z" 
xdbSetCallback(hWnd%):"XDBSETCALLBACK_Z" 

; ----------------------------------------------------------------------------------------------------
; INDEX ROUTINES - Index information 
; ----------------------------------------------------------------------------------------------------
xdbKeyLength%(DBFhandle%, idxHandle%):"XDBKEYLENGTH_Z" 
xdbKeyCount%(DBFhandle%, idxHandle%):"XDBKEYCOUNT_Z" 
xdbKeyUnique%(DBFhandle%, idxHandle%):"XDBKEYUNIQUE_Z" 
xdbKeyExpression%(DBFhandle%, idxHandle%):"XDBKEYEXPRESSION_Z" 
xdbMKL(LongValue%):"XDBMKL_Z"
xdbMKI(IntegerValue%):"XDBMKI_Z" 

; ----------------------------------------------------------------------------------------------------
; QUERY ROUTINES 
; ----------------------------------------------------------------------------------------------------
xdbCreateQuery%(DBFhandle%):"XDBCREATEQUERY_Z" 
xdbQueryCondition(QueryHandle%, JoinPhrase%, FieldName$, Equality%, vParameter1$, vParameter2$):"XDBQUERYCONDITION_Z" 
xdbQuerySort(QueryHandle%, FieldName$, SortDirection%):"XDBQUERYSORT_Z"  
xdbQueryExecute(QueryHandle%):"XDBQUERYEXECUTE_Z" 
xdbQuerySUM_Z#(QueryHandle%, FieldName$):"XDBQUERYSUM_Z" 
xdbQueryMIN_Z#(QueryHandle%, FieldName$):"XDBQUERYMIN_Z" 
xdbQueryMAX_Z#(QueryHandle%, FieldName$):"XDBQUERYMAX_Z" 
xdbQueryAVG_Z#(QueryHandle%, FieldName$):"XDBQUERYAVG_Z" 
xdbQueryDistinct(QueryHandle%, DistinctFieldName$):"XDBQUERYDISTINCT_Z" 
xdbHtmlStripTag(QueryHandle%, TrueFalse%):"XDBHTMLSTRIPTAG_Z"  
xdbDestroyQuery(QueryHandle%):"XDBDESTROYQUERY_Z" 
xdbQueryIndex%(QueryHandle%):"XDBQUERYINDEX_Z" 

; ----------------------------------------------------------------------------------------------------
; DATE ROUTINES  
; ----------------------------------------------------------------------------------------------------
xdbDaysApart%(DateFrom$, DateTo$):"XDBDAYSAPART_Z"  
xdbDaysInMonth%(Year%, Month%):"XDBDAYSINMONTH_Z" 
xdbAddDate(StartDate$, Days%):"XDBADDDATE_Z"  
xdbNameOfDay%(DateCheck$):"XDBNAMEOFDAY_Z"  
xdbTodaysDate%():"XDBTODAYSDATE_Z"  
xdbValidDate%(DateCheck$):"XDBVALIDDATE_Z"  
xdbDateToJulian%(DateString$):"XDBDATETOJULIAN_Z"  
xdbJulianToDate%(JulianNumber%):"XDBJULIANTODATE_Z" 
CTOD%(PBDate$):"CTOD_Z"  
DTOS%(xDate$):"DTOS_Z" 

; ----------------------------------------------------------------------------------------------------
; MISCELLANEOUS ROUTINES 
; ----------------------------------------------------------------------------------------------------
xdbResetError():"XDBRESETERROR_Z" 
xdbError%():"XDBERROR_Z" 
xdbVersion%():"XDBVERSION_Z"  
xdbRegisteredTo%():"XDBREGISTEREDTO_Z"  
xdbDebugMode(UserMode%):"XDBDEBUGMODE_Z" 
xdbAppPath%():"XDBAPPPATH_Z" 
xdbActivate(ActivateNumber%):"XDBACTIVATE_Z" 
xdbFreeDLL():"XDBFREEDLL_Z"  
xdbIsEncrypted%(DBFhandle%):"XDBISENCRYPTED_Z" 
xdbEncryptionMethod%(DBFhandle%):"XDBENCRYPTIONMETHOD_Z"  
xdbTempFileName%():"XDBTEMPFILENAME_Z"
