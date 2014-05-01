
package com.mulesoft.jaxrs.raml.annotation.model.apt;

import java.io.File;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.xml.namespace.QName;

import org.dynvocation.lib.xsd4j.XSDUtil;
import org.w3c.dom.Document;

import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;
import com.mulesoft.jaxrs.raml.annotation.model.ResourceVisitor;

public class APTResourceVisitor extends ResourceVisitor {

	private final ProcessingEnvironment processingEnv;
	public APTResourceVisitor(File outputFile, ProcessingEnvironment processingEnv, ClassLoader classLoader) {
		super(outputFile, classLoader);
		this.processingEnv = processingEnv;
	}
	
	
	protected void generateXMLSchema(ITypeModel t) {
		APTType type = (APTType) t;
		TypeElement element = (TypeElement) type.element();
		//try just loading this class
		Class<?> clazz;
		try {
			clazz = Class.forName(processingEnv.getElementUtils().getBinaryName(element).toString());
			generateXSDForClass(clazz);
			return;
		} catch (ClassNotFoundException e1) {
			// Ignore; try some of further approaches
		}
		if (classLoader != null) {
			try {
				clazz = classLoader.loadClass(processingEnv.getElementUtils().getBinaryName(element).toString());
				generateXSDForClass(clazz);
			} catch (ClassNotFoundException e) {
				//TODO log it
			}
		} 
	}

	@Override
	protected ResourceVisitor createResourceVisitor() {
		return new APTResourceVisitor(outputFile, processingEnv, classLoader);
	}
	
	protected void generateExamle(File file, String content) {
		String name = file.getName();
		int idx = name.lastIndexOf('.');
		if (idx > 0) {
			name = name.substring(0, idx) + XML_FILE_EXT;
		} else if (!name.endsWith(XML_FILE_EXT)) {
			name = name + XML_FILE_EXT;
		}

		File parent = file.getParentFile();
		if (parent.getName().endsWith("schemas")) { //$NON-NLS-1$
			parent = new File(parent.getParent(),"examples"); //$NON-NLS-1$
			parent.mkdirs();
		}
		new XSDUtil().instantiate(file.getAbsolutePath(),null,new File(parent,name).getAbsolutePath());
	}
	
}
