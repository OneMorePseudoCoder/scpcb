Type ActiveMods
    Field Path$
End Type

Const MOD_STATUS_LOCAL = 0
Const MOD_STATUS_REMOTE = 1
Const MOD_STATUS_REMOTE_OWNED = 2

Type Mods
    Field Id$
    Field Path$
    Field Name$
    Field Description$
    Field IsActive%
    Field Status%
End Type

Global ModCount%

Function ReloadMods()
    Delete Each Mods
    ModCount = 0
    d% = ReadDir("Mods")
    Repeat
        f$=NextFile(d)
        If f$="" Then Exit
        If f$<>"." And f$<>".." And FileType("Mods\"+f) = 2 Then
            m.Mods = new Mods
            m\Path = "Mods\"+f+"\"
            Local modIni$ = m\Path + "info.ini"
            If FileType(modIni) <> 1 Then RuntimeError("Mod at " + Chr(34) + m\Path + Chr(34) + " is missing an info.ini file.")
            Local ini% = OpenFile(modIni)
            While Not Eof(ini)
                Local l$ = Trim(ReadLine(ini))
                If l <> "" And Instr(l, "#") <> 1 Then
                    Local splitterPos = Instr(l, "=")
                    Local key$ = Trim(Left(l, splitterPos - 1))
                    Local value$ = Trim(Right(l, Len(l) - splitterPos))
                    Select Key
                        Case "id"
                            m\Id = value
                        Case "name"
                            m\Name = value
                        Case "desc"
                            m\Description = value
                    End Select
                EndIf
            Wend

            If m\Id = "" Then RuntimeError("Mod at " + Chr(34) + m\Path + Chr(34) + " is missing an ID in its info.ini file.")
            If m\Name = "" Then RuntimeError("Mod at " + Chr(34) + m\Path + Chr(34) + " is missing a name in its info.ini file.")
            For m2.Mods = Each Mods
                If m2 <> m And m2\Id = m\Id Then RuntimeError("Mod at " + Chr(34) + m\Path + Chr(34) + " and mod at " + Chr(34) + m\Path + Chr(34) + " share a mod ID.")
            Next

            ModCount = ModCount + 1
        EndIf
    Forever
    CloseDir(d)

    If FileType("mods.ini") = 1 Then
        Local mods% = OpenFile("mods.ini")
        Local firstSorted.Mods = First Mods
        While Not Eof(mods)
            l$ = Trim(ReadLine(mods))
            If l <> "" And Instr(l, "#") <> 1 Then
                splitterPos = Instr(l, "=")
                key$ = Trim(Left(l, splitterPos - 1))
                value$ = Trim(Right(l, Len(l) - splitterPos))
                For m.Mods = Each Mods
                    If key = m\Id Then
                        Insert m After firstSorted
                        firstSorted = m
                        m\IsActive = ParseIniInt(value)
                        Exit
                    EndIf
                Next
            EndIf
        Wend
    EndIf
    
    UpdateActiveMods()
End Function

Function SerializeMods()
    Local f% = WriteFile("mods.ini")
    For m.Mods = Each Mods
        WriteLine(f, m\Id + "=" + Str(m\IsActive))
    Next
    CloseFile(f)
End Function

Function UpdateActiveMods()
    Delete Each ActiveMods
    For m.Mods = Each Mods
        If m\IsActive Then
            mm.ActiveMods = New ActiveMods
            mm\Path = m\Path
        EndIf
    Next
End Function

Function LoadModdedTextureNonStrict%(file$, flags%)
    Local ext$ = File_GetExtension(File)
	Local fileNoExt$ = Left(File, Len(File) - Len(ext))
	Local tmp%

	For m.ActiveMods = Each ActiveMods
		For i = 0 To ImageExtensionCount
			Local usedExtension$
			If i = ImageExtensionCount Then
				usedExtension = ext
			Else
				usedExtension = ImageExtensions[i]
			EndIf
			Local modPath$ = m\Path + fileNoExt + usedExtension
			If FileType(modPath) = 1 Then
				tmp = LoadTexture(modPath, flags)
				If tmp <> 0 Then
					Return tmp
				Else If DebugResourcePacks Then
					RuntimeError("Failed to load texture " + Chr(34) + modPath + Chr(34) + ".")
				EndIf
			EndIf
		Next
	Next

    Return LoadTexture(file, flags)
End Function

Function DetermineModdedPath$(f$)
    For m.ActiveMods = Each ActiveMods
        modPath$ = m\Path + f
        If FileType(modPath) = 1 Then Return modPath
    Next
    Return f
End Function
