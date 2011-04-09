/*
 * Copyright 2011 jccastrejon
 *  
 * This file is part of InstanceModelBus.
 *
 * InstanceModelBus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * InstanceModelBus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with InstanceModelBus.  If not, see <http://www.gnu.org/licenses/>.
*/
package mx.itesm.mdd;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

/**
 * Converts the data types generated during a Class2Ecore transformation, to
 * valid Ecore types
 * 
 * @author jccastrejon
 * 
 */
public class EcoreDataTypeConverter {

	/**
	 * JDOM builder to parse Ecore documents.
	 */
	private static SAXBuilder saxBuilder = new SAXBuilder();

	/**
	 * JDOM writer for Ecore documents.
	 */
	private static XMLOutputter out = new XMLOutputter();

	/**
	 * Entry point. A parameter is required with the full path to the Ecore file
	 * to be converted.
	 * 
	 * @param args
	 * @throws JDOMException
	 * @throws IOException
	 */
	public static void main(String[] args) throws JDOMException, IOException {
		EcoreDataTypeConverter.convertTypes(new File(args[0]));
	}

	/**
	 * Performs the actual data type conversion.
	 * 
	 * @param ecoreFile
	 * @throws JDOMException
	 * @throws IOException
	 */
	public static void convertTypes(final File ecoreFile) throws JDOMException,
			IOException {
		Document document;
		Element dataType;
		String dataTypeName;
		String referenceName;
		String ecoreTypeName;
		List<Element> dataTypes;

		document = EcoreDataTypeConverter.saxBuilder.build(ecoreFile);
		dataTypes = EcoreDataTypeConverter.getDataTypes(document);

		if ((dataTypes != null) && (!dataTypes.isEmpty())) {
			for (int i = 1; i <= dataTypes.size(); i++) {
				// Update data type
				dataType = (Element) dataTypes.get(i - 1).detach();
				dataTypeName = dataType.getAttributeValue("name");
				referenceName = "#//" + dataTypeName;

				ecoreTypeName = referenceName;
				if (EcoreDataTypeConverter.isPrimitiveType(dataTypeName)) {
					ecoreTypeName = "ecore:EDataType http://www.eclipse.org/emf/2002/Ecore"
							+ ecoreTypeName;
				}

				EcoreDataTypeConverter.updateStructuralFeatureAttribute(
						document, "eType", i, ecoreTypeName);

				// Update Hierarchy
				EcoreDataTypeConverter.updateStructuralFeatureAttribute(
						document, "eSuperTypes", i, referenceName);
			}
		}

		EcoreDataTypeConverter.writeEcore(ecoreFile, document);
	}

	/**
	 * Replace the original Ecore file with the converted one.
	 * 
	 * @param ecoreFile
	 * @param document
	 * @throws IOException
	 */
	public static void writeEcore(final File ecoreFile, final Document document)
			throws IOException {
		out.output(document, new FileWriter(ecoreFile));
	}

	/**
	 * Update the references to the data types.
	 * 
	 * @param document
	 * @param attribute
	 * @param dataTypeIndex
	 * @param value
	 * @throws JDOMException
	 */
	@SuppressWarnings("unchecked")
	public static void updateStructuralFeatureAttribute(
			final Document document, final String attribute,
			final int dataTypeIndex, final String value) throws JDOMException {
		XPath typePath;
		List<Element> structuralFeatures;

		typePath = XPath.newInstance("//eStructuralFeatures[@" + attribute
				+ "='/" + dataTypeIndex + "']");
		structuralFeatures = (List<Element>) typePath.selectNodes(document);
		for (Element structuralFeature : structuralFeatures) {
			structuralFeature.setAttribute("eType", value);
		}
	}

	/**
	 * Check if a given type should be considered as part of the core EMF types.
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isPrimitiveType(final String type) {
		return type.startsWith("E");
	}

	/**
	 * Get the data types generated during the Class2Ecore transformation
	 * 
	 * @param document
	 * @return
	 * @throws JDOMException
	 */
	@SuppressWarnings("unchecked")
	public static List<Element> getDataTypes(final Document document)
			throws JDOMException {
		XPath dataTypePath;

		dataTypePath = XPath.newInstance("//ecore:EDataType");
		return dataTypePath.selectNodes(document);
	}
}