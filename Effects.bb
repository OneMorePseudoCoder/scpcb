Global GammaEffect%

Global PostEffectQuad%, QuadCamera%, PostEffect%

Global ScreenTexture%
Global TempTexture%

Function InitPostProcess()
	ScreenTexture = CreateTexture(GraphicsWidth(), GraphicsHeight(), 1 + 1024)
	TempTexture = CreateTexture(GraphicsWidth(), GraphicsHeight(), 1 + 1024)
	
	GammaEffect = LoadEffect("GFX\shaders\Gamma.fx")
	DebugLog GetEffectError()
	
	PostEffectQuad = CreateFullscreenQuad()
	EntityTexture(PostEffectQuad, ScreenTexture, 0, 0)
	EntityOrder(PostEffectQuad, 10000000)
	EntityFX(PostEffectQuad, 8)
	HideEntity(PostEffectQuad)
	
	QuadCamera = CreateCamera()
	CameraClsMode(QuadCamera, 0, 0)
	HideEntity(QuadCamera)
	
	PostEffect = 0
End Function

Function UpdatePostProcess()
	CopyRect(0, 0, GraphicsWidth(), GraphicsHeight(), 0, 0, BackBuffer(), TextureBuffer(ScreenTexture))
	ProcessGammaEffect(ScreenGamma)
End Function

Function ProcessGammaEffect(gamma#)
	EffectFloat(GammaEffect, "Gamma", lerp(gamma, 1.0, 0.3)) ; Limit gamma
	RenderEffectQuad(GammaEffect, BackBuffer(), "Main")
End Function

Function RenderEffectQuad(effect%, buffer%, technique$, blend% = 0)
	SetQuadEffect(effect)
	ShowEntity(PostEffectQuad)
	EntityBlend(PostEffectQuad, blend)
	SetBuffer(buffer)
	EffectTechnique(effect, technique)
	CameraViewport(QuadCamera, 0, 0, BufferWidth(buffer), BufferHeight(buffer))
	RenderEntity(QuadCamera, PostEffectQuad)
	HideEntity(PostEffectQuad)
End Function

Function CreateFullscreenQuad%(Parent% = 0)
	Quad = CreateSprite(Parent)
	ScaleSprite(Quad, 1.0, (Float(GraphicsHeight()) / Float(GraphicsWidth())))

	Local pixelWidth# = 0.0
	Local pixelHeight# = 0.0
	
	If GetGraphicsLevel() < 100
		pixelWidth# = 0.5 / GraphicsWidth()
		pixelHeight# = 0.5 / GraphicsHeight()
	EndIf
	
	MoveEntity(Quad, -pixelWidth, pixelHeight, 1.0001)
	Return(Quad)
End Function

Function SetQuadEffect(effect%)
	if PostEffect = effect Then Return
	EntityEffect PostEffectQuad, effect
	PostEffect = effect
End Function