/*******************************************************************************
 * Copyright 2011 Google Inc. All Rights Reserved.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.sealinc.accurator.server.service;

import org.sealinc.accurator.client.service.AdminComponentService;
import org.sealinc.accurator.server.Utility;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class AdminComponentServiceImpl extends RemoteServiceServlet implements AdminComponentService {

	@Override
	public void testUploadData() {
		Model m = ModelFactory.createDefaultModel();
		Resource r = m.createResource("http://jasperexample.org/resource/1");
		Property p = m.createProperty("http://jasperexample.org/","prop");
		m.add(r,p,"test");
		Utility.uploadData(m);
		
	}
}
