<%--

    Licensed to Apereo under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Apereo licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License.  You may obtain a
    copy of the License at the following location:

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.

--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

      </div> <!-- END #content -->
      
      <footer id="footer">
        <a id="logo" href="http://www.apereo.org" title="<spring:message code="logo.title"/>"></a>
        <div id="copyright">
          <p><spring:message code="copyright" /></p>
          <p>Powered by <a href="http://www.apereo.org/cas">Apereo Central Authentication Service <%=org.jasig.cas.CasVersion.getVersion()%></a></p>
        </div>
      </footer>

    </div> <!-- END #container -->
    
    <script type="text/javascript" src="<c:url value="/js/jquery.min.js" />"></script>
    <script type="text/javascript" src="<c:url value="/js/jquery-ui.min.js" />"></script>
    
    <%-- 
        JavaScript Debug: A simple wrapper for console.log 
        See this link for more info: http://benalman.com/projects/javascript-debug-console-log/
    --%>
    <script type="text/javascript" src="<c:url value="/js/ba-debug.min.js" />"></script>
    <script type="text/javascript" src="<c:url value="/js/esup-otp-api.js" />"></script>    

    <script type="text/javascript" src="<c:url value="/js/core-min.js" />"></script>
    <script type="text/javascript" src="<c:url value="/js/sha256-min.js" />"></script>
    <script type="text/javascript" src="<c:url value="/js/md5-min.js" />"></script>
    
    <spring:theme code="cas.javascript.file" var="casJavascriptFile" text="" />
    <script type="text/javascript" src="<c:url value="${casJavascriptFile}" />"></script>

    <jsp:useBean id="esupOtpApiAuthenticationHandlerBottom" class="org.esupportail.cas.authentication.EsupOtpApiAuthenticationHandler"/>
    <script type="text/javascript">
      var url_esup_otp = "<jsp:getProperty name='esupOtpApiAuthenticationHandlerBottom' property='urlApi' />";
      var strings = {};
      strings.success = {};
      strings.error = {};
      strings.button = {};
      strings.button.send = {};
      strings.button.code = {};
      strings.label = {};
      strings.method = {};
      strings.success.transport = "<spring:message code='success.transport' javaScriptEscape='true' />";
      strings.error.transport_wait = "<spring:message code='error.transport_wait' javaScriptEscape='true' />";
      strings.error.login_needed = "<spring:message code='error.login_needed' javaScriptEscape='true' />";
      strings.error.message = "<spring:message code='error.message' javaScriptEscape='true' />";
      strings.button.validate = "<spring:message code='button.validate' javaScriptEscape='true' />";
      strings.button.change = "<spring:message code='button.change' javaScriptEscape='true' />";
      strings.button.send.sms = "<spring:message code='button.send.sms' javaScriptEscape='true' />";
      strings.button.send.mail = "<spring:message code='button.send.mail' javaScriptEscape='true' />";
      strings.button.code.owned = "<spring:message code='button.code.owned' javaScriptEscape='true' />";
      strings.button.code.lost = "<spring:message code='button.code.lost' javaScriptEscape='true' />";
      strings.label.sms = "<spring:message code='label.sms' javaScriptEscape='true' />";
      strings.label.mail = "<spring:message code='label.mail' javaScriptEscape='true' />";
      strings.method.totp = "<spring:message code='method.totp' javaScriptEscape='true' />";
      strings.method.random_code = "<spring:message code='method.random_code' javaScriptEscape='true' />";
      strings.method.bypass = "<spring:message code='method.bypass' javaScriptEscape='true' />";
      window.onload = init;
    </script>

  </body>
</html>

