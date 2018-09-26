/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.all41.sap.cloud.web;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;
// import org.apache.olingo.server.api.edmx.EdmxReference;  //valid for v4.0.0
import org.apache.olingo.commons.api.edmx.EdmxReference; // valid for v4.5.0
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.all41.sap.cloud.data.Storage;
import com.all41.sap.cloud.service.odata.EvatrEdmProvider;
import com.all41.sap.cloud.service.odata.EvatrEntityCollectionProcessor;
import com.all41.sap.cloud.service.odata.EvatrEntityProcessor;

/**
 * This class represents a standard HttpServlet implementation.
 * It is used as main entry point for the web application that carries the OData service.
 * The implementation of this HttpServlet simply delegates the user requests to the ODataHttpHandler
 */
public class EvatrServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;
  private static final Logger LOG = LoggerFactory.getLogger(EvatrServlet.class);

  @Override
  protected void service(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {

      HttpSession session = req.getSession(true);
      Storage storage = (Storage) session.getAttribute(Storage.class.getName());
      if (storage == null) {
         storage = new Storage();
         session.setAttribute(Storage.class.getName(), storage);
      }	  
	  
    try {
      // create odata handler and configure it with EdmProvider and Processor
      OData odata = OData.newInstance();
      ServiceMetadata edm = odata.createServiceMetadata(new EvatrEdmProvider(), new ArrayList<EdmxReference>());
      ODataHttpHandler handler = odata.createHandler(edm);
      handler.register(new EvatrEntityCollectionProcessor( storage));
      handler.register(new EvatrEntityProcessor(storage));

      // let the handler do the work
      handler.process(req, resp);

    } catch (RuntimeException e) {
      LOG.error("Server Error occurred in ExampleServlet", e);
      throw new ServletException(e);
    }
  }
}