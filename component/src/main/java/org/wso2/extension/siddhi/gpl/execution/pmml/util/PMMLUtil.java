/*
 * Copyright (C) 2017 WSO2 Inc. (http://wso2.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.wso2.extension.siddhi.gpl.execution.pmml.util;

import org.apache.log4j.Logger;
import org.dmg.pmml.PMML;
import org.jpmml.model.ImportFilter;
import org.jpmml.model.JAXBUtil;
import org.wso2.siddhi.core.exception.SiddhiAppCreationException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringReader;
import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;

/**
 * Class implementing Pmml Model Processor.
 */
public class PMMLUtil {
    private static final Logger logger = Logger.getLogger(PMMLUtil.class);

    /**
     * Unmarshal the definition and get an executable pmml model.
     *
     * @return pmml model
     */
    public static PMML unmarshal(String pmmlDefinition) {

        try {
            File pmmlFile = new File(pmmlDefinition);
            InputSource pmmlSource;
            Source source;
            // if the given is a file path, read the pmml definition from the file
            if (pmmlFile.isFile() && pmmlFile.canRead()) {
                pmmlSource = new InputSource(new FileInputStream(pmmlFile));
            } else {
                // else, read from the given definition
                pmmlSource = new InputSource(new StringReader(pmmlDefinition));
            }
            source = ImportFilter.apply(pmmlSource);
            return JAXBUtil.unmarshalPMML(source);
        } catch (SAXException | JAXBException | FileNotFoundException e) {
            logger.error("Failed to unmarshal the pmml definition: " + e.getMessage());
            throw new SiddhiAppCreationException("Failed to unmarshal the pmml definition: "
                    + pmmlDefinition + ". " + e.getMessage(), e);
        }
    }
}
