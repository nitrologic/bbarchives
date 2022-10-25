; ID: 2098
; Author: Yo! Wazzup?
; Date: 2007-08-31 23:34:44
; Title: CSS link color gen
; Description: Define colors for every link on your page with CSS

Graphics 800,600,0,2 ;Set the graphics

Global quote$=Chr(34) ;Make a "quote" variable so I can add quotes to the Print function.

Print "Please put all colors in hexadecimal format and add a # before every Hex number." ;Tell the user that you need to use a Hex color with a # before it for this to work
color_of_link$=Input("Color of every link on page? ") ;Ask what color every link should be
vlink_of_link$=Input("Color of every visited link on page? ") ;Ask what color every visited link should be
alink_of_link$=Input("Color of every active link on page? ") ;Ask what color every Active link on the page should be
hover_color_of_link$=Input("Color of every link being hovered over with mouse? ") ;Ask what color every link being hovered over by the mouse should be.
css_file_name$=Input("Name of .css file? ") ;And finally, ask what the name of the css file should be.

Cls ;Clear the screen

Locate 0,0 ;Set the Print and Input location to 0,0


Print "Writing to text file. Please wait." ;Tell the user that the text file is being written to. Normally the user can not see this message because the file writing process is so fast.

If Right(css_file_name, 4) = ".css" Then ;Check to see if the user entered the .css extention already
	css_file=WriteFile(css_file_name) ;If the user did, write the file.
Else ;If the user didn't, write the file PLUS add the extension of .css
	css_file_name = css_file_name + ".css"
	css_file=WriteFile(css_file_name)
EndIf

;Write the CSS code to the file
WriteLine(css_file, "a:link {color: " + color_of_link + " }")
WriteLine(css_file, "a:visited {color: " + vlink_of_link + " }")
WriteLine(css_file, "a:hover {color: " + hover_color_of_link + " }")
WriteLine(css_file, "a:active {color: " + alink_of_link + " }")

CloseFile(css_file) ;Close the file

Cls ;Clear the screen

Locate 0,0 ;Set the Print and Input location to 0,0

Print "File writing complete!" ;Tell the user that the "writing to the file" process is complete

;Give the user instructions on how to use the .css file
Print "First, make sure " + css_file_name + " is in the same directory as your html file."
Print "Second, type the following into the head section of the html file."
Print
Print "<link rel=" + quote + "stylesheet" + quote + " type=" + quote + "text/css" + quote + " href=" + quote + css_file_name + quote + ">"
Print
Print "Please press esc to quit."

;Wait for the ESC key to be hit before ending the program
While Not KeyDown(1)
Wend
End
