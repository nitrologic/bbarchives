; ID: 2035
; Author: Filax
; Date: 2007-06-11 16:57:00
; Title: Cheetah database for blitzmax
; Description: Cheetah database for blitzmax

' ------------------------------
' Original wrapper code by Knotz
' Blitzmax conversion by Filax
' ------------------------------
Strict
Import Pub.Win32

Const DllName:String = "Cheetah2.dll"
Local Lib = LoadLibraryA(DllName)

If Lib = 0 Then
	Notify "Unable to initialize Cheetah2.dll"
	End
EndIf


Global xdbCreate(dbFile$z, AllFields$z) "win32" = GetProcAddress(lib, "XDBCREATE_Z")
Global xdbCreateExtended (tFileName$z, AllFields$z, MemoBlockSize%, Algorithm%, EncryptionKey$z) "win32" = GetProcAddress(lib, "XDBCREATEEXTENDED_Z")
Global xdbOpen% (dbFile$z, EncryptionKey$z) "win32" = GetProcAddress(lib, "XDBOPEN_Z")
Global xdbOpenEX%(dbFile$z, AccessMode%, ShareMode%, EncryptionKey$z) "win32" = GetProcAddress(lib, "XDBOPENEX_Z")
Global xdbAlias%(dbHandle%) "win32" = GetProcAddress(lib, "XDBALIAS_Z")
Global xdbSetAlias(dbHandle%, AliasName$z) "win32" = GetProcAddress(lib, "XDBSETALIAS_Z")
Global xdbCreateFields(mFileName$z) "win32" = GetProcAddress(lib, "XDBCREATEFIELDS_Z")
Global xdbCreateFieldsExtended(mFileName$z, MemoBlockSize%, Algorithm%, EncryptionKey$z) "win32" = GetProcAddress(lib, "XDBCREATEFIELDSEXTENDED_Z")
Global xdbAddField(FieldInfoString$z) "win32" = GetProcAddress(lib, "XDBADDFIELD_Z")
Global xdbClose(dbHandle%) "win32" = GetProcAddress(lib, "XDBCLOSE_Z")
Global xdbCloseAllIndexes(dbHandle%) "win32" = GetProcAddress(lib, "XDBCLOSEALLINDEXES_Z")
Global xdbMultiUser(TrueFalse%, NumRetries%, WaitTime%) "win32" = GetProcAddress(lib, "XDBMULTIUSER_Z")
Global xdbFailedLockInfo(DBFhandle%, zReason%, zUsername%, zWorkstation%, zLockDate%, zLockTime%) "win32" = GetProcAddress(lib, "XDBFAILEDLOCKINFO_Z")
Global xdbSetExclusiveLock%(DBFhandle%) "win32" = GetProcAddress(lib, "XDBSETEXCLUSIVELOCK_Z")
Global xdbRemoveExclusiveLock(DBFhandle%, LOCK_NUM%) "win32" = GetProcAddress(lib, "XDBREMOVEEXCLUSIVELOCK_Z")
Global xdbSetEditLock%(DBFhandle%, RecordNumber%) "win32" = GetProcAddress(lib, "XDBSETEDITLOCK_Z")
Global xdbRemoveEditLock(DBFhandle%, LOCK_NUM%) "win32" = GetProcAddress(lib, "XDBREMOVEEDITLOCK_Z")
Global xdbIsEditLock%(DBFhandle%, RecordNumber%) "win32" = GetProcAddress(lib, "XDBISEDITLOCK_Z")
Global xdbAddRecord(dbHandle%) "win32" = GetProcAddress(lib, "XDBADDRECORD_Z")
Global xdbAppendRecord(DBFhandle%) "win32" = GetProcAddress(lib, "XDBAPPENDRECORD_Z")
Global xdbClearBuffer(dbHandle%) "win32" = GetProcAddress(lib, "XDBCLEARBUFFER_Z")
Global xdbPutRecord(dbHandle%, RN%) "win32" = GetProcAddress(lib, "XDBPUTRECORD_Z")
Global xdbGetRecord(dbHandle%, RN%) "win32" = GetProcAddress(lib, "XDBGETRECORD_Z")
Global xdbDeleteRecord(DBFhandle%, RecordNumber%) "win32" = GetProcAddress(lib, "XDBDELETERECORD_Z")
Global xdbRecallRecord(DBFhandle%, RecordNumber%) "win32" = GetProcAddress(lib, "XDBRECALLRECORD_Z")
Global xdbDeleted%(DBFhandle%, RecordNumber%) "win32" = GetProcAddress(lib, "XDBDELETED_Z")
Global xdbFlushDatabase(dbHandle%) "win32" = GetProcAddress(lib, "XDBFLUSHDATABASE_Z")
Global xdbPack( DBFhandle%) "win32" = GetProcAddress(lib, "XDBPACK_Z")
Global xdbZap( DBFhandle%) "win32" = GetProcAddress(lib, "XDBZAP_Z")
Global xdbRecordBuffer%(DBFhandle%) "win32" = GetProcAddress(lib, "XDBRECORDBUFFER_Z")
Global xdbSpeedAppend(DBFhandle%, TrueFalse%) "win32" = GetProcAddress(lib, "XDBSPEEDAPPEND_Z")
Global xdbRecordCount%(dbHandle%) "win32" = GetProcAddress(lib, "XDBRECORDCOUNT_Z")
Global xdbFieldCount%(dbHandle%) "win32" = GetProcAddress(lib, "XDBFIELDCOUNT_Z")
Global xdbFieldNumber%(dbHandle%, FieldName$z) "win32" = GetProcAddress(lib, "XDBFIELDNUMBER_Z")
Global xdbRecordNumber%(DBFhandle%) "win32" = GetProcAddress(lib, "XDBRECORDNUMBER_Z")
Global xdbLastUpdated(dbHandle%, YearNr%, MonthNr%, DayNr%) "win32" = GetProcAddress(lib, "XDBLASTUPDATED_Z")
Global xdbFieldInfo(DBFhandle%, FieldNumber%, zFieldName%, zFieldType%, FieldLength%, FieldDecimals%) "win32" = GetProcAddress(lib, "XDBFIELDINFO_Z")
Global xdbFieldName%(DBFhandle%, FieldNumber%) "win32" = GetProcAddress(lib, "XDBFIELDNAME_Z")
Global xdbFieldType%(DBFhandle%, FieldNumber%) "win32" = GetProcAddress(lib, "XDBFIELDTYPE_Z")
Global xdbFieldLength%(DBFhandle%, FieldNumber%) "win32" = GetProcAddress(lib, "XDBFIELDLENGTH_Z")
Global xdbFieldDecimals%(DBFhandle%, FieldNumber%) "win32" = GetProcAddress(lib, "XDBFIELDDECIMALS_Z")
Global xdbDatabaseHandles(DBFfilename$z, CheetahHandle%, WindowsHandle%) "win32" = GetProcAddress(lib, "XDBDATABASEHANDLES_Z")
Global xdbIndexHandles(IDXfilename$z, CheetahHandle%, WindowsHandle%) "win32" = GetProcAddress(lib, "XDBINDEXHANDLES_Z")
Global xdbEOF%(DBFhandle%) "win32" = GetProcAddress(lib, "XDBEOF_Z")
Global xdbBOF%(DBFhandle%) "win32" = GetProcAddress(lib, "XDBBOF_Z")
Global xdbMoveFirst(DBFhandle%, idxHandle%) "win32" = GetProcAddress(lib, "XDBMOVEFIRST_Z")
Global xdbMoveNext(DBFhandle%, idxHandle%) "win32" = GetProcAddress(lib, "XDBMOVENEXT_Z")
Global xdbMoveLast(DBFhandle%, idxHandle%) "win32" = GetProcAddress(lib, "XDBMOVELAST_Z")
Global xdbMovePrev(DBFhandle%, idxHandle%) "win32" = GetProcAddress(lib, "XDBMOVEPREV_Z")
Global xdbSkipDeleted(DBFhandle%, TrueFalse%) "win32" = GetProcAddress(lib, "XDBSKIPDELETED_Z")
Global xdbKeyPosition%(IDXhandle%, KeyPosition%) "win32" = GetProcAddress(lib, "XDBKEYPOSITION_Z")
Global xdbAssignField(dbHandle%, FieldName$z, FieldNumber%, FieldString$z) "win32" = GetProcAddress(lib, "XDBASSIGNFIELD_Z")
Global xdbAssignFieldINT(dbHandle%, FieldName$z, FieldNumber%, FieldInteger%) "win32" = GetProcAddress(lib, "XDBASSIGNFIELDINT_Z")
Global xdbAssignFieldLNG(dbHandle%, FieldName$z, FieldNumber%, FieldLong%) "win32" = GetProcAddress(lib, "XDBASSIGNFIELDLNG_Z")
Global xdbAssignFieldSNG(dbHandle%, FieldName$z, FieldNumber%, FieldSingle#) "win32" = GetProcAddress(lib, "XDBASSIGNFIELDSNG_Z")
Global xdbFieldValue$z(DBFhandle%, FieldName$z, FieldCode%) "win32" = GetProcAddress(lib, "XDBFIELDVALUE_Z")
Global xdbFieldValueINT%(DBFhandle%, FieldName$z, FieldCode%) "win32" = GetProcAddress(lib, "XDBFIELDVALUEINT_Z")
Global xdbFieldValueLNG%(DBFhandle%, FieldName$z, FieldCode%) "win32" = GetProcAddress(lib, "XDBFIELDVALUELNG_Z")
Global xdbFieldValueSNG#(DBFhandle%, FieldName$z, FieldCode%) "win32" = GetProcAddress(lib, "XDBFIELDVALUESNG_Z")
Global xdbFieldPadding(DBFhandle%, TrueFalse%) "win32" = GetProcAddress(lib, "XDBFIELDPADDING_Z")
Global xdbCreateIndex(iFilename$z, dbHandle%, IndexExpression$z, Duplicates%) "win32" = GetProcAddress(lib, "XDBCREATEINDEX_Z")
Global xdbOpenIndex%(iFilename$z, dbHandle%) "win32" = GetProcAddress(lib, "XDBOPENINDEX_Z")
Global xdbCloseIndex(DBFhandle%, idxHandle%) "win32" = GetProcAddress(lib, "XDBCLOSEINDEX_Z")
Global xdbReindex(DBFhandle%, idxHandle%, ContinueOrError%) "win32" = GetProcAddress(lib, "XDBREINDEX_Z")
Global xdbReindexAll(DBFhandle%, ContinueOrError%) "win32" = GetProcAddress(lib, "XDBREINDEXALL_Z")
Global xdbSeek%(DBFhandle%, idxHandle%, LookForKey$z) "win32" = GetProcAddress(lib, "XDBSEEK_Z")
Global xdbSeekNext%(dbHandle%, idxHandle%, LookForKey$z) "win32" = GetProcAddress(lib, "XDBSEEKNEXT_Z")
Global xdbSeekPartial%(dbHandle%, idxHandle%, LookForKey$z) "win32" = GetProcAddress(lib, "XDBSEEKPARTIAL_Z")
Global xdbSeekPartialNext%(dbHandle%, idxHandle%, LookForKey$z) "win32" = GetProcAddress(lib, "XDBSEEKPARTIALNEXT_Z")
Global xdbSetCallback(hWnd%) "win32" = GetProcAddress(lib, "XDBSETCALLBACK_Z")
Global xdbKeyLength%(DBFhandle%, idxHandle%) "win32" = GetProcAddress(lib, "XDBKEYLENGTH_Z")
Global xdbKeyCount%(DBFhandle%, idxHandle%) "win32" = GetProcAddress(lib, "XDBKEYCOUNT_Z")
Global xdbKeyUnique%(DBFhandle%, idxHandle%) "win32" = GetProcAddress(lib, "XDBKEYUNIQUE_Z")
Global xdbKeyExpression%(DBFhandle%, idxHandle%) "win32" = GetProcAddress(lib, "XDBKEYEXPRESSION_Z")
Global xdbMKL(LongValue%) "win32" = GetProcAddress(lib, "XDBMKL_Z")
Global xdbMKI(IntegerValue%) "win32" = GetProcAddress(lib, "XDBMKI_Z")
Global xdbCreateQuery%(DBFhandle%) "win32" = GetProcAddress(lib, "XDBCREATEQUERY_Z")
Global xdbQueryCondition(QueryHandle%, JoinPhrase%, FieldName$z, Equality%, vParameter1$z, vParameter2$z) "win32" = GetProcAddress(lib, "XDBQUERYCONDITION_Z")
Global xdbQuerySort(QueryHandle%, FieldName$z, SortDirection%) "win32" = GetProcAddress(lib, "XDBQUERYSORT_Z")
Global xdbQueryExecute(QueryHandle%) "win32" = GetProcAddress(lib, "XDBQUERYEXECUTE_Z")
Global xdbQuerySUM_Z#(QueryHandle%, FieldName$z) "win32" = GetProcAddress(lib, "XDBQUERYSUM_Z")
Global xdbQueryMIN_Z#(QueryHandle%, FieldName$z) "win32" = GetProcAddress(lib, "XDBQUERYMIN_Z")
Global xdbQueryMAX_Z#(QueryHandle%, FieldName$z) "win32" = GetProcAddress(lib, "XDBQUERYMAX_Z")
Global xdbQueryAVG_Z#(QueryHandle%, FieldName$z) "win32" = GetProcAddress(lib, "XDBQUERYAVG_Z")
Global xdbQueryDistinct(QueryHandle%, DistinctFieldName$z) "win32" = GetProcAddress(lib, "XDBQUERYDISTINCT_Z")
Global xdbHtmlStripTag(QueryHandle%, TrueFalse%) "win32" = GetProcAddress(lib, "XDBHTMLSTRIPTAG_Z")
Global xdbDestroyQuery(QueryHandle%) "win32" = GetProcAddress(lib, "XDBDESTROYQUERY_Z")
Global xdbQueryIndex%(QueryHandle%) "win32" = GetProcAddress(lib, "XDBQUERYINDEX_Z")
Global xdbDaysApart%(DateFrom$z, DateTo$z) "win32" = GetProcAddress(lib, "XDBDAYSAPART_Z")
Global xdbDaysInMonth%(Year%, Month%) "win32" = GetProcAddress(lib, "XDBDAYSINMONTH_Z")
Global xdbAddDate(StartDate$z, Days%) "win32" = GetProcAddress(lib, "XDBADDDATE_Z")
Global xdbNameOfDay%(DateCheck$z) "win32" = GetProcAddress(lib, "XDBNAMEOFDAY_Z")
Global xdbTodaysDate%() "win32" = GetProcAddress(lib, "XDBTODAYSDATE_Z")
Global xdbValidDate%(DateCheck$z) "win32" = GetProcAddress(lib, "XDBVALIDDATE_Z")
Global xdbDateToJulian%(DateString$z) "win32" = GetProcAddress(lib, "XDBDATETOJULIAN_Z")
Global xdbJulianToDate%(JulianNumber%) "win32" = GetProcAddress(lib, "XDBJULIANTODATE_Z")
Global CTOD%(PBDate$z) "win32" = GetProcAddress(lib, "CTOD_Z")
Global DTOS%(xDate$z) "win32" = GetProcAddress(lib, "DTOS_Z")
Global xdbResetError() "win32" = GetProcAddress(lib, "XDBRESETERROR_Z")
Global xdbError%() "win32" = GetProcAddress(lib, "XDBERROR_Z")
Global xdbVersion%() "win32" = GetProcAddress(lib, "XDBVERSION_Z")
Global xdbRegisteredTo%() "win32" = GetProcAddress(lib, "XDBREGISTEREDTO_Z")
Global xdbDebugMode(UserMode%) "win32" = GetProcAddress(lib, "XDBDEBUGMODE_Z")
Global xdbAppPath%() "win32" = GetProcAddress(lib, "XDBAPPPATH_Z")
Global xdbActivate(ActivateNumber%) "win32" = GetProcAddress(lib, "XDBACTIVATE_Z")
Global xdbFreeDLL() "win32" = GetProcAddress(lib, "XDBFREEDLL_Z")
Global xdbIsEncrypted%(DBFhandle%) "win32" = GetProcAddress(lib, "XDBISENCRYPTED_Z")
Global xdbEncryptionMethod%(DBFhandle%) "win32" = GetProcAddress(lib, "XDBENCRYPTIONMETHOD_Z")
Global xdbTempFileName%() "win32" = GetProcAddress(lib, "XDBTEMPFILENAME_Z")

lib = 0

Local DBFname:String
Local AllFields:String
Local db:Int
Local sObjID:String
Local sData:String
Local sTunes:String
Local i:Int
Local LastRecord:Int
Local OBJID:Int
Local DATA:Int,TUNES:Int

DBFname = "test.dbf"
AllFields = "OBJID,C,10,0;DATA,M,0,0"

Print "Delete db"
DeleteFile(DBFname)

Print "Create db"
xdbCreate(DBFname, AllFields)

Print "Open db for write " + DBFname
db = xdbOpenEx(DBFname, 2, 4, "")

OBJID = xdbFieldNumber(db, "OBJID")
DATA = xdbFieldNumber(db, "DATA")

xdbClearBuffer(db)
xdbAssignField(db, "", OBJID, "1")
xdbAssignField(db, "", DATA, "Test")

xdbAddRecord(db)

Print "Close"
xdbClose(db)

Print "Open DB for read " + DBFname
db = xdbOpenEx(DBFname, 2, 4, "")

LastRecord = xdbRecordCount(db)
Print String.FromInt(LastRecord) + " records"
For i = 1 To LastRecord
	xdbGetRecord(db, i)
	sObjID = xdbFieldValue(db, "", OBJID )
	sData = xdbFieldValue(db, "", DATA)

	Print "ID:" + sObjID + " Data:" + sData 
Next

xdbClose(db)
