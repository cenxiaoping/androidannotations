/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2019 the AndroidAnnotations project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.androidannotations.internal.generation;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import javax.annotation.processing.Filer;

import org.androidannotations.Option;
import org.androidannotations.internal.process.ModelProcessor;
import org.androidannotations.logger.Logger;
import org.androidannotations.logger.LoggerFactory;

import com.helger.jcodemodel.writer.JCMWriter;
import com.helger.jcodemodel.writer.PrologCodeWriter;

public class CodeModelGenerator {

	public static final Option OPTION_ENCODING = new Option("encoding", "UTF-8");

	private static final Logger LOGGER = LoggerFactory.getLogger(CodeModelGenerator.class);

	private final Filer filer;
	private final String header;
	private final String encoding;

	public CodeModelGenerator(Filer filer, String aaVersion, String encoding) {
		this.filer = filer;
		this.header = "DO NOT EDIT THIS FILE.\n" + "Generated using AndroidAnnotations " + aaVersion + ".\n\n"
				+ "You can create a larger work that contains this file and distribute that work under terms of your choice.\n";
		this.encoding = encoding;
	}

	public void generate(ModelProcessor.ProcessResult processResult) throws IOException {
		Charset charset = getCharset();

		SourceCodeWriter sourceCodeWriter = new SourceCodeWriter(filer, processResult.originatingElements, charset);

		PrologCodeWriter prologCodeWriter = new PrologCodeWriter(sourceCodeWriter, header);

		JCMWriter jcmWriter = new JCMWriter(processResult.codeModel);

		jcmWriter.build(prologCodeWriter, new ResourceCodeWriter(filer, charset));
	}

	private Charset getCharset() {
		try {
			return Charset.forName(encoding);
		} catch (UnsupportedCharsetException exception) {
			Charset defaultCharset = Charset.defaultCharset();

			LOGGER.warn("The requested charset ({}) is not available, falling back to platform default ({}).", encoding, defaultCharset);

			return defaultCharset;
		}
	}
}
