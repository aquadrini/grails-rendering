/*
 * Copyright 2010 Grails Plugin Collective
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package grails.plugin.rendering.pdf

import grails.plugin.rendering.RenderingService
import org.xhtmlrenderer.pdf.ITextFontResolver
import org.xhtmlrenderer.pdf.ITextRenderer
import org.w3c.dom.Document

import grails.plugin.rendering.datauri.DataUriAwareITextUserAgent

import org.springframework.util.ReflectionUtils
import com.lowagie.text.pdf.BaseFont

class PdfRenderingService extends RenderingService {

	static transactional = false
	
	PdfRenderingService() {
		ReflectionUtils.makeAccessible(ITextRenderer.getDeclaredField("_outputDevice"))
	}
	
	protected doRender(Map args, Document document, OutputStream outputStream) {
		def renderer = new ITextRenderer()
		configureRenderer(renderer)
		renderer.setDocument(document, args.base)
		renderer.layout()
		renderer.createPDF(outputStream)
	}
	
	protected doRender(Map args, Document document, OutputStream outputStream, Map baseFont) {
		def renderer = new ITextRenderer()
		configureRenderer(renderer, baseFont)
		renderer.setDocument(document, args.base)
		renderer.layout()
		renderer.createPDF(outputStream)
	}

	protected getDefaultContentType() {
		"application/pdf"
	}
	
	protected configureRenderer(ITextRenderer renderer) {
		def outputDevice = renderer.@_outputDevice
		def userAgent = new DataUriAwareITextUserAgent(outputDevice)
		def sharedContext = renderer.sharedContext
		
		sharedContext.userAgentCallback = userAgent
		userAgent.sharedContext = sharedContext
	}
	
	protected configureRenderer(ITextRenderer renderer, Map font) {
		configureRenderer(renderer)
		ITextFontResolver fontResolver = renderer.getFontResolver()
		fontResolver.addFont(font.path, font.encoding, font.embedded)
	}
}
