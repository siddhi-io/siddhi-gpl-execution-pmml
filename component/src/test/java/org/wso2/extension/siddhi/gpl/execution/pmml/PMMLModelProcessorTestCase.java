/*
 * Copyright (C) 2017 WSO2 Inc. (http://wso2.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.wso2.extension.siddhi.gpl.execution.pmml;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.wso2.siddhi.core.SiddhiAppRuntime;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.query.output.callback.QueryCallback;
import org.wso2.siddhi.core.stream.input.InputHandler;
import org.wso2.siddhi.core.util.EventPrinter;
import org.wso2.siddhi.core.util.SiddhiTestHelper;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Test class for PmmlModelProcessor.
 */
public class PMMLModelProcessorTestCase {

    private volatile boolean eventArrived;
    private volatile boolean isSuccessfullyExecuted;
    private AtomicInteger eventCount = new AtomicInteger(0);
    private long waitTime = 5000;
    private long timeout = 10000;

    @BeforeTest
    public void init() {
        eventArrived = false;
        isSuccessfullyExecuted = false;
        eventCount.set(0);
    }

    @Test
    public void predictFunctionTest() throws InterruptedException, URISyntaxException {

        URL resource = PMMLModelProcessorTestCase.class.getResource("/decision-tree.pmml");
        String pmmlFile = new File(resource.toURI()).getAbsolutePath();

        SiddhiManager siddhiManager = new SiddhiManager();

        String inputStream = "define stream InputStream " +
                "(root_shell double, su_attempted double, num_root double, num_file_creations double, num_shells " +
                "double, num_access_files double, num_outbound_cmds double, is_host_login double, is_guest_login " +
                "double, count double, srv_count double, serror_rate double, srv_serror_rate double);";

        String query = "@info(name = 'query1') " +
                "from InputStream#pmml:predict('" + pmmlFile + "') " +
                "select * " +
                "insert into outputStream ;";

        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(inputStream + query);

        siddhiAppRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                eventArrived = true;
                if (inEvents != null) {
                    eventCount.getAndIncrement();
                    isSuccessfullyExecuted = inEvents[0].getData(13).equals("1");
                }
            }

        });

        InputHandler inputHandler = siddhiAppRuntime.getInputHandler("InputStream");
        siddhiAppRuntime.start();
        inputHandler.send(new Object[]{6, 148, 72, 35, 0, 33.6, 0.627, 50, 1, 2, 3, 4, 5});
        SiddhiTestHelper.waitForEvents(waitTime, 1, eventCount, timeout);
        Assert.assertTrue(isSuccessfullyExecuted);
        Assert.assertTrue(eventArrived);
        siddhiAppRuntime.shutdown();
    }

    @Test
    public void predictFunctionWithSelectedOutputAttributeTest() throws InterruptedException, URISyntaxException {

        URL resource = PMMLModelProcessorTestCase.class.getResource("/decision-tree.pmml");
        String pmmlFile = new File(resource.toURI()).getAbsolutePath();

        SiddhiManager siddhiManager = new SiddhiManager();

        String inputStream = "define stream InputStream " +
                "(root_shell double, su_attempted double, num_root double, num_file_creations double, num_shells " +
                "double, num_access_files double, num_outbound_cmds double, is_host_login double, is_guest_login " +
                "double, count double, srv_count double, serror_rate double, srv_serror_rate double);";

        String query = "@info(name = 'query1') " +
                "from InputStream#pmml:predict('" + pmmlFile + "', root_shell, su_attempted, num_root, " +
                "num_file_creations, num_shells, num_access_files, num_outbound_cmds, is_host_login, is_guest_login, " +
                "count, srv_count, serror_rate, srv_serror_rate) " +
                "select Predicted_response " +
                "insert into outputStream ;";

        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(inputStream + query);

        siddhiAppRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                eventArrived = true;
                if (inEvents != null) {
                    isSuccessfullyExecuted = inEvents[0].getData(0).equals("1");
                }
            }

        });

        InputHandler inputHandler = siddhiAppRuntime.getInputHandler("InputStream");
        siddhiAppRuntime.start();
        inputHandler.send(new Object[]{6, 148, 72, 35, 0, 33.6, 0.627, 50, 1, 2, 3, 4, 5});
        SiddhiTestHelper.waitForEvents(waitTime, 1, eventCount, timeout);
        Assert.assertTrue(isSuccessfullyExecuted);
        Assert.assertTrue(eventArrived);
        siddhiAppRuntime.shutdown();
    }

    @Test
    public void predictFunctionWithSelectedAttributesTest() throws InterruptedException, URISyntaxException {

        URL resource = PMMLModelProcessorTestCase.class.getResource("/decision-tree.pmml");
        String pmmlFile = new File(resource.toURI()).getAbsolutePath();

        SiddhiManager siddhiManager = new SiddhiManager();

        String inputStream = "define stream InputStream " +
                "(root_shell double, su_attempted double, num_root double, num_file_creations double, num_shells " +
                "double, num_access_files double, num_outbound_cmds double, is_host_login double, is_guest_login " +
                "double, count double, srv_count double, serror_rate double, srv_serror_rate double);";

        String query = "@info(name = 'query1') " +
                "from InputStream#pmml:predict('" + pmmlFile + "', root_shell, su_attempted, num_root, " +
                "num_file_creations, num_shells, num_access_files, num_outbound_cmds, is_host_login, " +
                "is_guest_login, count, srv_count, serror_rate, srv_serror_rate) " +
                "select * " +
                "insert into outputStream ;";

        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(inputStream + query);

        siddhiAppRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                eventArrived = true;
                if (inEvents != null) {
                    isSuccessfullyExecuted = inEvents[0].getData(13).equals("1");
                }
            }

        });

        InputHandler inputHandler = siddhiAppRuntime.getInputHandler("InputStream");
        siddhiAppRuntime.start();
        inputHandler.send(new Object[]{6, 148, 72, 35, 0, 33.6, 0.627, 50, 1, 2, 3, 4, 5});
        SiddhiTestHelper.waitForEvents(waitTime, 1, eventCount, timeout);
        Assert.assertTrue(isSuccessfullyExecuted);
        Assert.assertTrue(eventArrived);
        siddhiAppRuntime.shutdown();
    }

    @Test
    public void predictFunctionWithMissingDataTypeAttributeTest() throws URISyntaxException, InterruptedException {
        URL resource = PMMLModelProcessorTestCase.class.getResource("/decision-tree-modified.pmml");
        String pmmlFile = new File(resource.toURI()).getAbsolutePath();

        SiddhiManager siddhiManager = new SiddhiManager();

        String inputStream = "define stream InputStream "
                + "(root_shell double, su_attempted double, num_root double, num_file_creations double, " +
                "num_shells double, num_access_files double, num_outbound_cmds double, is_host_login double, " +
                "is_guest_login double, count double, srv_count double, serror_rate double, srv_serror_rate double);";

        String query = "@info(name = 'query1') " +
                "from InputStream#pmml:predict('" + pmmlFile
                + "', root_shell, su_attempted, num_root, num_file_creations, num_shells, num_access_files, " +
                "num_outbound_cmds, is_host_login, is_guest_login, count, srv_count, serror_rate, srv_serror_rate) "
                +
                "select * " +
                "insert into outputStream ;";

        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(inputStream + query);

        siddhiAppRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                eventArrived = true;
                if (inEvents != null) {
                    isSuccessfullyExecuted = inEvents[0].getData(13).equals("1");
                }
            }

        });

        InputHandler inputHandler = siddhiAppRuntime.getInputHandler("InputStream");
        siddhiAppRuntime.start();
        inputHandler.send(new Object[]{6, 148, 72, 35, 0, 33.6, 0.627, 50, 1, 2, 3, 4, 5});
        SiddhiTestHelper.waitForEvents(waitTime, 1, eventCount, timeout);
        Assert.assertTrue(isSuccessfullyExecuted);
        Assert.assertTrue(eventArrived);
        siddhiAppRuntime.shutdown();
    }

    @Test
    public void predictFunctionWithMissingOutputTagTest() throws URISyntaxException, InterruptedException {
        URL resource = PMMLModelProcessorTestCase.class.getResource("/linear-regression.pmml");
        String pmmlFile = new File(resource.toURI()).getAbsolutePath();

        SiddhiManager siddhiManager = new SiddhiManager();

        String inputStream = "define stream InputStream "
                + "(field_0 double, field_1 double, field_2 double, field_3 double, field_4 double, field_5 double, " +
                "field_6 double, field_7 double);";

        String query = "@info(name = 'query1') " +
                "from InputStream#pmml:predict('" + pmmlFile
                + "',field_0, field_1, field_2, field_3 , field_4 , field_5 , field_6 , field_7) " +
                "select * " +
                "insert into outputStream ;";

        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(inputStream + query);

        siddhiAppRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                eventArrived = true;
                if (inEvents != null) {
                    isSuccessfullyExecuted = inEvents[0].getData(8).equals(5.216788478335122);
                }
            }

        });

        InputHandler inputHandler = siddhiAppRuntime.getInputHandler("InputStream");
        siddhiAppRuntime.start();
        inputHandler.send(new Object[]{1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0});
        SiddhiTestHelper.waitForEvents(waitTime, 1, eventCount, timeout);
        Assert.assertTrue(isSuccessfullyExecuted);
        Assert.assertTrue(eventArrived);
        siddhiAppRuntime.shutdown();
    }
}
