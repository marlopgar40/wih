/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package us.dit.muit.ass.wih;

import org.drools.core.process.instance.impl.WorkItemImpl;
import org.hl7.fhir.r5.model.Appointment;
import org.jbpm.process.workitem.core.TestWorkItemManager;
import org.jbpm.test.AbstractBaseTest;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

public class AppointmentDAOWorkItemHandlerTest extends AbstractBaseTest {

    @Test
    public void testHandler() throws Exception {
        WorkItemImpl workItem = new WorkItemImpl();
        /**
         * Cita disponible en hapifhir online, creada a partir del json incluido appointment.json
         */
        workItem.setParameter("URL", "https://hapi.fhir.org/baseR5/Appointment/773551");
        List<String> attributes=new ArrayList<String>();
        attributes.add("Practitioner");
        attributes.add("Patient");
        workItem.setParameter("Attributes",attributes);

        TestWorkItemManager manager = new TestWorkItemManager();

        AppointmentDAOWorkItemHandler handler = new AppointmentDAOWorkItemHandler("https://hapi.fhir.org/baseR5/Appointment/773551",attributes);
        handler.setLogThrownException(true);
        handler.executeWorkItem(workItem,
                                manager);

        assertNotNull(manager.getResults());
        assertEquals(1,
                     manager.getResults().size());
        assertTrue(manager.getResults().containsKey(workItem.getId()));
       
    }
}
