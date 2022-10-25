; ID: 1781
; Author: Fabian.
; Date: 2006-08-07 16:08:49
; Title: fmc.SystemTray
; Description: A module to add, modify or remove icons in the system icon tray

Strict
Module fmc.SystemTray
?Win32

ModuleInfo "Version: 0.01"
ModuleInfo "Modserver: Fabian"

Import brl.win32maxgui
Import fmc.ObjectTool

Type TTrayIcon
  Field Link:TLink
  Field NID:Object
  Field SystemIcon:TSystemIcon
  Field Menu:TGadget
  Field ToolTip$

  Function Create:TTrayIcon ( SystemIcon:TSystemIcon = Null , Menu:TGadget = Null , ToolTip$ = "" )
    Local TrayIcon:TTrayIcon = New TTrayIcon
    Local NID:TNotifyIconData = New TNotifyIconData
    TrayIcon.Link = List.AddLast ( TrayIcon )
    TrayIcon.NID = NID
    TrayIcon.Menu = Menu
    TrayIcon.ToolTip = ToolTip
    NID.Size = SizeOf NID
    NID.Win = Window
    NID.ID = Int Byte Ptr TrayIcon
    NID.Flags = 7
    NID.CallbackMessage = $200
    If SystemIcon
      TrayIcon.SystemIcon = SystemIcon
      NID.Icon = SystemIcon.Icon
    EndIf
    MemCopy Varptr NID.Tip00 , StringPtr ( ToolTip ) , 2 * Min ( Len ToolTip , 63 )
    Shell_NotifyIconW 0 , NID
    Return TrayIcon
  EndFunction

  Method SetSystemIcon ( NewSystemIcon:TSystemIcon )
    If Link
      SystemIcon = NewSystemIcon
      If SystemIcon
        TNotifyIconData ( NID ).Icon = SystemIcon.Icon
      Else
        TNotifyIconData ( NID ).Icon = 0
      EndIf
      Shell_NotifyIconW 1 , NID
    EndIf
  EndMethod

  Method SetMenu ( NewMenu:TGadget )
    If Link
      Menu = NewMenu
    EndIf
  EndMethod

  Method SetToolTip ( NewToolTip$ )
    If Link
      ToolTip = NewToolTip
      MemClear Varptr TNotifyIconData ( NID ).Tip00 , 126
      MemCopy Varptr TNotifyIconData ( NID ).Tip00 , StringPtr ( ToolTip ) , 2 * Min ( Len ToolTip , 63 )
      Shell_NotifyIconW 1 , NID
    EndIf
  EndMethod

  Method GetSystemIcon:TSystemIcon ( )
    If Link
      Return SystemIcon
    EndIf
  EndMethod

  Method GetMenu:TGadget ( )
    If Link
      Return Menu
    EndIf
  EndMethod

  Method GetToolTip$ ( )
    If Link
      Return ToolTip
    EndIf
  EndMethod

  Method Destroy ( )
    If Link
      Shell_NotifyIconW 2 , NID
      Link.Remove
      Link = Null
    EndIf
  EndMethod
EndType

Type TSystemIcon
  Field Icon

  Function FromExeRes:TSystemIcon ( ResName$ = "101" , TryInt = True )
    Local NamePtr:Short Ptr
    Local NameInt = Int ResName
    Local W:Short Ptr
    If TryInt And NameInt
      NamePtr = Short Ptr NameInt
    Else
      W = ResName.ToWString ( )
      NamePtr = W
    EndIf
    Local Icon = LoadImageW ( GetModuleHandleW ( Null ) , NamePtr , 1 , 0 , 0 , 0 )
    If W
      MemFree W
    EndIf
    If Icon
      Local SystemIcon:TSystemIcon = New TSystemIcon
      SystemIcon.Icon = Icon
      Return SystemIcon
    EndIf
  EndFunction

  Function FromFile:TSystemIcon ( FileName$ )
    Local W:Short Ptr = FileName.ToWString ( )
    Local Icon = LoadImageW ( 0 , W , 1 , 0 , 0 , 16 )
    MemFree W
    If Icon
      Local SystemIcon:TSystemIcon = New TSystemIcon
      SystemIcon.Icon = Icon
      Return SystemIcon
    EndIf
  EndFunction

  Method Delete ( )
    DestroyIcon Icon
  EndMethod
EndType

Private

Global WinGad:TGadget = CreateWindow ( "" , 0 , 0 , 0 , 0 , Desktop ( ) , WINDOW_HIDDEN )
Global Window = QueryGadget ( WinGad , QUERY_HWND )
Global WinProc = SetWindowLongW ( Window , -4 , Int Byte Ptr Proc )
Global List:TList = CreateList ( )

Type TNotifyIconData
  Field Size
  Field Win
  Field ID
  Field Flags
  Field CallbackMessage
  Field Icon
  Field Tip00:Long
  Field Tip01:Long
  Field Tip02:Long
  Field Tip03:Long
  Field Tip04:Long
  Field Tip05:Long
  Field Tip06:Long
  Field Tip07:Long
  Field Tip08:Long
  Field Tip09:Long
  Field Tip10:Long
  Field Tip11:Long
  Field Tip12:Long
  Field Tip13:Long
  Field Tip14:Long
  Field Tip15:Long
EndType

Function Proc ( Win , Msg , WP , LP )
  If Win = Window And Msg = $200
    For Local TrayIcon:TTrayIcon = EachIn List
      If WP = Int Byte Ptr TrayIcon
        If LP = 514
          TEvent.Create ( EVENT_GADGETACTION , TrayIcon ).Emit
        EndIf
        If LP = 517
          If TrayIcon.Menu
            PopupWindowMenu WinGad , TrayIcon.Menu
          EndIf
        EndIf
        Return
      EndIf
    Next
    Return
  EndIf
  Return CallWindowProcW ( WinProc , Win , Msg , WP , LP )
EndFunction

Extern "Win32"
  Function Shell_NotifyIconW ( Msg , Data:Byte Ptr )
  Function GetModuleHandleW ( Name:Short Ptr )
  Function LoadImageW ( Inst , Name:Short Ptr , T , W , H , L )
  Function DestroyIcon ( Icon )
  Function SetWindowLongW ( Win , Pos , Val )
  Function CallWindowProcW ( Proc , Win , Msg , WP , LP )
EndExtern
?
