; ID: 2313
; Author: tonyg
; Date: 2008-09-12 12:57:08
; Title: Active window handle
; Description: Get the active window handle

Graphics 800,600
Local my_driver:TD3D7Graphicsdriver=D3D7GraphicsDriver()
Local my_graphics:TD3D7Graphics=my_driver.Graphics()
Local my_hWnd:Int = my_graphics._hwnd
Print my_hwnd
