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

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import org.jbpm.process.workitem.core.AbstractLogOrThrowWorkItemHandler;
import org.jbpm.process.workitem.core.util.RequiredParameterValidator;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.jbpm.process.workitem.core.util.Wid;
import org.jbpm.process.workitem.core.util.WidParameter;
import org.jbpm.process.workitem.core.util.WidResult;
import org.jbpm.process.workitem.core.util.service.WidAction;
import org.jbpm.process.workitem.core.util.service.WidAuth;
import org.jbpm.process.workitem.core.util.service.WidService;
import org.jbpm.process.workitem.core.util.WidMavenDepends;
import org.hl7.fhir.r5.model.Appointment;
import ca.uhn.fhir.util.UrlUtil;
import java.net.URI;

import java.util.logging.Logger;


@Wid(widfile="AppointmentDAODefinitions.wid", name="AppointmentDAODefinitions",
        displayName="AppointmentDAODefinitions",
        defaultHandler="mvel: new us.dit.muit.ass.wih.AppointmentDAOWorkItemHandler()",
        documentation = "wih/index.html",
        category = "wih",
        icon = "AppointmentDAODefinitions.png",
        parameters={
        		      @WidParameter(name="URL", required = true),
        		      @WidParameter(name="Attributes", required = true)
        		   },
        results={
        		      @WidResult(name="Practitioner"),
        		      @WidResult(name="Subject")
        		 },
        mavenDepends={
            @WidMavenDepends(group="us.dit.muit.ass", artifact="wih", version="0.0.1-SNAPSHOT")
        },
        serviceInfo = @WidService(category = "wih", description = "${description}",
                         keywords = "",
                		 action = @WidAction(title = "Devuelve los atributos solicitados del Appointment especificado"),
                         authinfo = @WidAuth(required = true, params = {"URL", "Attributes"},
                                 paramsdescription = {"URL del Appoinment de interés", "Listado de los atributos solicitados"},
                                 referencesite = "referenceSiteURL")
        )
)

public class AppointmentDAOWorkItemHandler extends AbstractLogOrThrowWorkItemHandler {
	    private static final Logger logger = Logger.getLogger(AppointmentDAOWorkItemHandler.class.getName());
        private String appointmentURL;
        private List<String> reqAttributes;
        private Appointment appointement;

    public AppointmentDAOWorkItemHandler(String appointmentURL, List<String> reqAttributes){
            this.appointmentURL = appointmentURL;
            this.reqAttributes = reqAttributes;
        }

    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        try {
            RequiredParameterValidator.validate(this.getClass(), workItem);

            // sample parameters
            appointmentURL = (String) workItem.getParameter("URL");
            reqAttributes = (List<String>) workItem.getParameter("Attributes");

            // obtener el recurso Appointment
            // para cada uno de los atributos pedidos y 
            //consultar los y meterlos en el mapa de valores devueltos
            //Usar el nombre del atributo como clave en el mapa
            
            this.appointement=getAppointment(appointmentURL);
            // return results
            Map<String, Object> results = new HashMap<String, Object>();
            //Bucle añadiendo cada uno de los atributos solicitados al mapa de salida
            for(String attribute:reqAttributes) {
            	results.put(attribute,getAttribute(attribute));
            }
                
            manager.completeWorkItem(workItem.getId(), results);
        } catch(Throwable cause) {
            handleException(cause);
            //puede no estar disponible el servidor, puede pasarse un atributo equivocado
            //puede no estar el atributo solicitado
            
        }
    }
    private String getAttribute(String attribute) {
    	String attributeValue="valor del atributo";
    	return attributeValue;
    }
    
    private Appointment getAppointment(String url) {
		// We're connecting to a DSTU1 compliant server in this example
		FhirContext ctx = FhirContext.forR5();
		//Necesito sacar del url por un lado el servidor y por otro el id
		String serverBase= new URI(uri).getHost();
		String appointmentId=UrlUtil.parseUrl(url).getResourceId();
		IGenericClient client = ctx.newRestfulGenericClient(serverBase);
		logger.info("Busco appointment " + url);
		Appointment appointment =
		      client.read().resource(Appointment.class).withId(appointmentId).execute();
		logger.info("Localizado appointment " + appointment.getId());
		return Appointment;

	}

    @Override
    public void abortWorkItem(WorkItem workItem,
                              WorkItemManager manager) {
        // stub
    }
}


