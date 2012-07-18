<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title><g:message code="localization.create" default="Create Localization" /></title>
        <g:localizationHelpBalloons />
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/', absolute: true)}"><g:message code="home" default="Home" /></a></span>
            <g:localizationMenuButton/>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="localization.list" default="Localization List" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="localization.create" default="Create Localization" /></h1>
            <g:if test="${flash.message}">
            <div class="message"><g:message code="${flash.message}" args="${flash.args}" default="${flash.defaultMessage}" /></div>
            </g:if>
            <g:hasErrors bean="${localization}">
            <div class="errors">
                <g:renderErrors bean="${localization}" as="list" />
            </div>
            </g:hasErrors>
            <g:form action="save" method="post" >
                <g:render plugin="localizations" template="dialog" model="[localization: localization]" />
                <div class="buttons">
                    <span class="button"><input class="save" type="submit" value="${message(code:'create', 'default':'Create')}" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
