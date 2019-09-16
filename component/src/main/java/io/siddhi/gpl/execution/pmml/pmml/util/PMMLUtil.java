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

package io.siddhi.gpl.execution.pmml.pmml.util;

import io.siddhi.core.exception.SiddhiAppCreationException;
import org.dmg.pmml.PMML;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import javax.xml.bind.JAXBException;

/**
 * Class implementing Pmml Model Processor.
 */
public class PMMLUtil {

    /**
     * Unmarshal the definition and get an executable pmml model.
     *
     * @return pmml model
     */
    public static PMML unmarshal(String pmmlDefinition) {

        try {
            File pmmlFile = new File(pmmlDefinition);
            InputStream pmmlSource;
            // if the given is a file path, read the pmml definition from the file
            if (pmmlFile.isFile() && pmmlFile.canRead()) {
                pmmlSource = new FileInputStream(pmmlFile);
            } else {
                // else, read from the given definition
                pmmlSource = new ByteArrayInputStream(pmmlDefinition.getBytes("UTF8"));
            }
            return org.jpmml.model.PMMLUtil.unmarshal(pmmlSource);
        } catch (SAXException | JAXBException | FileNotFoundException | UnsupportedEncodingException e) {
            throw new SiddhiAppCreationException("Failed to unmarshal the pmml definition: "
                    + pmmlDefinition + ". " + e.getMessage(), e);
        }
    }
}
