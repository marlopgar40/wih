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
import org.hl7.fhir.r5.model.Appointment.AppointmentParticipantComponent;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.util.UrlUtil;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;


@Wid(widfile="AppointmentDAODefinitions.wid", name="AppointmentDAODefinitions",
        displayName="AppointmentDAODefinitions",
        defaultHandler="mvel: new us.dit.muit.ass.wih.AppointmentDAOWorkItemHandler()",
        documentation = "${artifactId}/index.html",
        category = "${artifactId}",
        description = "${description}",
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
        		  @WidMavenDepends(group = "${groupId}", artifact = "${artifactId}", version = "${version}")
        },
        serviceInfo = @WidService(category = "${name}", description ="${description}",
                         keywords = "appointment,fhir,attributes",
                		 action = @WidAction(title = "Devuelve los atributos solicitados del Appointment especificado"),
                         authinfo = @WidAuth(required = true, params = {"URL", "Attributes"},
                                 paramsdescription = {"URL del Appoinment", "Listado de los atributos solicitados"},
                                 referencesite = "referenceSiteURL")
        )
)

public class AppointmentDAOWorkItemHandler extends AbstractLogOrThrowWorkItemHandler {
	    private static final Logger logger = Logger.getLogger(AppointmentDAOWorkItemHandler.class.getName());
        private String appointmentURL;
        private List<String> reqAttributes;
        private Appointment appointment;

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
            
            this.appointment=getAppointment(appointmentURL);
            // return results
            logger.info("creamos el mapa de resultados");
            Map<String, Object> results = new HashMap<String, Object>();
            //Bucle añadiendo cada uno de los atributos solicitados al mapa de salida
            for(String attribute:reqAttributes) {
            	
            	results.put(attribute,getAttribute(attribute));
            	logger.info("incluyendo en el mapa de salida attributo "+attribute+" con valor "+getAttribute(attribute));
            }
                
            manager.completeWorkItem(workItem.getId(), results);
        } catch(Throwable cause) {
            handleException(cause);
            //puede no estar disponible el servidor, puede pasarse un atributo equivocado
            //puede no estar el atributo solicitado
            
        }
    }
    private String getAttribute(String attribute) {
    	String attributeValue=null;
    	logger.info("Buscamos el atributo "+attribute);
    	switch(attribute) {
    	case "Practitioner":
    			attributeValue=getPractitioner();
    			break;
    	case "Subject":
    			attributeValue=getSubject();
    			break;
    	default:
    			attributeValue="no implementado";
    	}

    	return attributeValue;
    }
    private String getSubject() {
    	String attributeValue=null;
    	logger.info("Buscamos subject");
    	if(appointment.hasSubject()) {    		
    		attributeValue=appointment.getSubject().getReference();
    		logger.info("appointement tiene subject y es "+attributeValue);
    	}
    	return attributeValue;
    }   
    private String getPractitioner() {
    	String attributeValue=null;
    	logger.info("Buscando practitioner");
    	List<AppointmentParticipantComponent> participants=appointment.getParticipant();
    	for(AppointmentParticipantComponent participant:participants) {
    		
    		if(participant.hasActor() && participant.hasType()) {
    			logger.info("localizado participante con actor y tipo "+participant.getActor().getDisplay());
    			String code=participant.getTypeFirstRep().getCode("http://terminology.hl7.org/CodeSystem/v3-ParticipationType");
    			logger.info("codigo "+code);
    			if(code.equals("ATND")) {
    					
    				attributeValue=participant.getActor().getReference();
    				logger.info("appointement practitioner y es "+attributeValue);
    			}   			
    		}
    	}

    	return attributeValue;
    }
    private Appointment getAppointment(String url) {
		// We're connecting to a DSTU1 compliant server in this example
		FhirContext ctx = FhirContext.forR5();
		//Necesito sacar del url por un lado el servidor y por otro el id
		String serverBase;
		Appointment appointment=null;
		try {
			logger.fine("Busco appointment " + url);
			URI uri = new URI(url);
			int pos=url.indexOf("Appointment");
			serverBase=url.substring(0,pos);
			logger.fine("indice "+pos);			
			String appointmentId=UrlUtil.parseUrl(url).getResourceId();	
			logger.fine("serverBase: "+serverBase);
			logger.fine("appointmet id "+appointmentId);
			IGenericClient client = ctx.newRestfulGenericClient(serverBase);			
			appointment =
			      client.read().resource(Appointment.class).withId(appointmentId).execute();
			logger.info("Localizado appointment " + appointment.getId());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return appointment;

	}

    @Override
    public void abortWorkItem(WorkItem workItem,
                              WorkItemManager manager) {
        // stub
    }
}


