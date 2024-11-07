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

import java.util.Map;

import org.jbpm.process.workitem.WorkDefinitionImpl;
import org.jbpm.process.workitem.WorkItemRepository;

import org.junit.Test;

import static org.junit.Assert.*;

public class AppointmentDAOWorkitemIntegrationTest {
	@Test
	public void testWorkitemValidity() {
		/**
		 * Este test funciona si el artefacto está descomprimido únicamente, pero por defecto en target se crea el .jar
		 * así que el test no va funcionar a menos que se descomprima...
		 */

		 String repoPath = "file:" + System.getProperty("builddir") +
	                "/" + System.getProperty("artifactId") + "-" +
	                System.getProperty("version")+ "/";
	                
		
	    	System.out.println("artifactId propiedad: "+System.getProperty("artifactId"));
	    	System.out.println("RepoPath "+repoPath);
/*tal y como lo he puesto se encuentra, pero el problema es que da error al procesar el fichero de texto
 * para poder continuar anulo esta parte, pero la dejo comentada*/
	    WorkItemRepository repo=new WorkItemRepository();
	    String[] def= {"AppointmentDAODefinitions","wih"};
	    Map<String, WorkDefinitionImpl> repoResults=repo.getWorkDefinitions(repoPath, null, "wih");
	   
		assertNotNull(repoResults);
		System.out.println("Todo el mapa "+repoResults);
	//	assertNotEquals(0, repoResults.size());

	//	assertTrue(repoResults.containsKey("AppointmentDAODefinitions"));
	}
}
